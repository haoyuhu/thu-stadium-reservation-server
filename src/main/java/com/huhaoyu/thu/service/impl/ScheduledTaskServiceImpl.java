package com.huhaoyu.thu.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huhaoyu.thu.common.*;
import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.common.Constants.SimpleMailTemplate;
import com.huhaoyu.thu.common.Constants.SportType;
import com.huhaoyu.thu.common.Constants.UserType;
import com.huhaoyu.thu.config.SecurityConfiguration;
import com.huhaoyu.thu.entity.*;
import com.huhaoyu.thu.repository.StadiumRepository;
import com.huhaoyu.thu.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 上午10:58.
 */

@Service
@Transactional
public class ScheduledTaskServiceImpl implements ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskServiceImpl.class);

    @Autowired
    private StadiumRepository stadiumRepository;
    @Autowired
    private RecordService recordService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private WechatService wechatService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private MailService mailService;
    @Autowired
    private SecurityConfiguration config;

    @Override
    public boolean notifyReceiversByMail(String secretId, String openId, Long groupId, String encrypted, String signature) {
        // validate signature
        Map<String, Object> data = new TreeMap<>();
        data.put("secret_id", secretId);
        data.put("open_id", openId);
        data.put("group_id", groupId);
        data.put("encrypted", encrypted);
        String queryStrings = HttpUtil.convertMapToQueryStrings(data);
        if (queryStrings == null) {
            throw new UnacceptableParamException(Response.ServerError);
        }
        if (!DigestUtils.md5DigestAsHex(queryStrings.getBytes()).equals(signature)) {
            throw new UnacceptableParamException("unacceptable signature");
        }
        // decrypt data
        String json = SecurityUtil.decrypt(encrypted, config.getScheduledTaskSecretKey(), config.getSymmetricEncryptionAlgorithm());
        if (json == null) {
            throw new UnacceptableParamException("cannot decrypt encrypted content");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map[] list = mapper.readValue(json, Map[].class);
            String stadium = null;
            List<String> sites = new ArrayList<>();
            String date = null;
            List<String> sections = new ArrayList<>();
            double cost = 0;
            List<String> receivers = new ArrayList<>();
            // add admin receiver
            receivers.add(Constants.GLOBAL_ADMIN_RECEIVER);
            String username = null;

            for (Map item : list) {
                ReservationRecord record = recordService.createRecord(openId, (String) item.get("stadium_name"),
                        (String) item.get("site_name"), (long) item.get("start_time"), (long) item.get("end_time"),
                        (String) item.get("thu_account"), (double) item.get("cost"), (String) item.get("description"));
                if (stadium == null) {
                    stadium = record.getStadiumName();
                }
                sites.add(record.getSiteName());
                Date start = record.getStartTime();
                Date end = record.getEndTime();
                if (date == null) {
                    date = new SimpleDateFormat("yyyy-MM-dd E").format(start);
                }
                String section = new SimpleDateFormat("HH:mm").format(start) + "~" + new SimpleDateFormat("HH:mm").format(end);
                sections.add(section);
                cost += record.getCost();
                if (username == null) {
                    username = record.getAccountUsername();
                }
            }
            // find email for account which book the site(s)
            List<THUAccount> accounts = accountService.findAccountByProperties(openId, null, null, username, null,
                    null, null);
            if (accounts.size() != 1) {
                throw new UnacceptableParamException("wrong open_id or thu_account");
            }
            receivers.add(accounts.get(0).getEmail());
            // find emails for reservation group to notify
            List<ReservationGroup> groups = reservationService.getReservationGroupByProperties(openId, groupId, null, null);
            if (groups.size() != 1) {
                throw new UnacceptableParamException("wrong open_id or group_id");
            }
            receivers.addAll(groups.get(0).getReceiverList());
            // create subject and body content for reservation mail
            SimpleMailTemplate template = SimpleMailTemplate.ReservationSuccess;
            String subject = template.getSubject();
            // stadium&site: %s;section: %s;account: %s;cost: %.2f;current: %s;
            String content = String.format(template.getBody(),
                    stadium + " " + String.join(CommonUtil.COMMA_SEPARATOR, sites),
                    date + " " + String.join(CommonUtil.SPACE_SEPARATOR, sections),
                    username,
                    cost,
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            SimpleMailMessage message = mailService.createSimpleTextMailMessage(subject, content,
                    receivers.stream().toArray(String[]::new));
            mailService.sendMail(message);
            return true;
        } catch (IOException e) {
            logger.error("cannot convert json to map by jackson");
            throw new UnacceptableParamException("unacceptable encrypted content, hint: cannot convert json to map");
        } catch (Exception e) {
            logger.error("cannot send mails", e);
            return false;
        }
    }

    /* Reservation list item pattern
    {
    sig,
    account: {student_id, username, password, phone, user_type},
    groups: [{sub_sig, open_id, group_id, candidates: [{ available, sport_type, week, wish_start, wish_end, length, section_start, section_end, fixed}]}]
    }
    */
    @Override
    public String getEncryptedReservationListString() {
        List<Map> list = new ArrayList<>();
        Map<String, Map> accountMap = new HashMap<>();
        Map<String, List<Map>> groupsMap = new HashMap<>();

        for (WechatUser user : wechatService.findAllWechatUser()) {
            // add account
            THUAccount acc = user.getAccountBeingUsed();
            if (acc == null || accountMap.containsKey(acc.getUsername())) continue;
            Map<String, String> accInfo = createAccountInfo(acc);
            if (accInfo == null) continue;
            accountMap.put(acc.getUsername(), accInfo);
            // add reservation
            List<Map> groups = new ArrayList<>();
            for (ReservationGroup group : user.getAvailableReservationGroup()) {
                Map<String, Object> groupInfo = createGroupInfo(user.getOpenId(), group);
                groups.add(groupInfo);
            }
            if (!groupsMap.containsKey(acc.getUsername())) {
                groupsMap.put(acc.getUsername(), new ArrayList<>());
            }
            List<Map> current = groupsMap.get(acc.getUsername());
            current.addAll(groups);
        }
        // create signature and filter wrong account
        for (String username : accountMap.keySet()) {
            List<String> signatures = new ArrayList<>();
            List<Map> groups = groupsMap.get(username);
            for (Map group : groups) {
                String subSig = (String) group.get("sub_sig");
                signatures.add(subSig);
            }
            Collections.sort(signatures);
            String sig = DigestUtils.md5DigestAsHex(String.join(CommonUtil.COMMA_SEPARATOR, signatures).getBytes());

            Map account = accountMap.get(username);
            String studentId = (String) account.get("student_id");
            String password = (String) account.get("password");
            if (accountService.verifyAccount(studentId, password)) {
                Map<String, Object> item = new HashMap<>();
                item.put("sig", sig);
                item.put("account", account);
                item.put("groups", groups);
                list.add(item);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(list);
            return SecurityUtil.encrypt(json, config.getScheduledTaskSecretKey(), config.getSymmetricEncryptionAlgorithm());
        } catch (JsonProcessingException e) {
            logger.error("cannot convert reservation list to json string", e);
            throw new ServerErrorException("cannot convert reservation list to json string");
        }
    }

    private Map<String, Object> createGroupInfo(String openId, ReservationGroup group) {
        Map<String, Object> item = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        List<Map> list = new ArrayList<>();
        List<ReservationCandidate> candidates = group.getAvailableReservationCandidate();
        for (ReservationCandidate candidate : candidates) {
            Map<String, Object> c = createCandidateInfo(candidate);
            if (c == null) continue;
            list.add(c);
            ids.add(candidate.getId());
        }
        Collections.sort(ids);
        List<String> idStrings = ids.stream().map(String::valueOf).collect(Collectors.toList());
        String raw = String.join(CommonUtil.COMMA_SEPARATOR, idStrings);

        item.put("sub_sig", DigestUtils.md5DigestAsHex(raw.getBytes()));
        item.put("open_id", openId);
        item.put("group_id", group.getId());
        item.put("candidates", list);

        return item;
    }

    private Map<String, Object> createCandidateInfo(ReservationCandidate candidate) {
        SportType type = SportType.from(candidate.getSportType());
        if (type == null) return null;
        Map<String, Object> ret = new HashMap<>();

        ret.put("available", candidate.getAvailable());
        ret.put("sport_type", type.getName());
        ret.put("week", candidate.getWeek());
        ret.put("wish_start", candidate.getWishStartTime());
        ret.put("wish_end", candidate.getWishEndTime());
        int length = CommonUtil.getTimeStringIntervalInSecond(candidate.getWishEndTime(), candidate.getWishStartTime());
        ret.put("length", length / CommonUtil.SECOND_OF_MINUTE);
        ret.put("section_start", candidate.getSectionStartTime());
        ret.put("section_end", candidate.getSectionEndTime());
        ret.put("fixed", candidate.getFixed());

        return ret;
    }

    private Map<String, String> createAccountInfo(THUAccount account) {
        Map<String, String> ret = new HashMap<>();
        UserType userType = UserType.from(account.getUserType());
        if (userType == null) return null;

        ret.put("student_id", account.getStudentId());
        ret.put("username", account.getUsername());
        ret.put("password", account.getPassword());
        ret.put("phone", account.getPhoneNumber());
        ret.put("user_type", userType.getName());

        return ret;
    }

    @Override
    public List<Stadium> getStadiumByProperties(Long stadiumId, String name, String stadiumCode, String siteCode, Integer sportType) {
        if (sportType != null && !SportType.validate(sportType)) {
            throw new UnacceptableParamException("unacceptable sport_type");
        }

        Stadium stadium = new Stadium();
        stadium.setName(name);
        stadium.setStadiumCode(stadiumCode);
        stadium.setSiteCode(siteCode);
        stadium.setSportType(sportType);

        return stadiumRepository.findAll(Example.of(stadium));
    }

    @Override
    public Stadium createStadium(String name, String stadiumCode, String siteCode, Integer sportType, String exceptions,
                                 String description) {
        if (!SportType.validate(sportType) || !validateExceptions(exceptions)) {
            throw new UnacceptableParamException("unacceptable sport_type or exceptions");
        }

        Stadium stadium = new Stadium();
        stadium.setName(name);
        stadium.setStadiumCode(stadiumCode);
        stadium.setSiteCode(siteCode);
        stadium.setSportType(sportType);
        stadium.setExceptions(exceptions);
        stadium.setDescription(description);

        return stadiumRepository.save(stadium);
    }

    @Override
    public Stadium updateStadium(Long stadiumId, String name, String stadiumCode, String siteCode, Integer sportType,
                                 String exceptions, String description) {
        if ((sportType != null && !SportType.validate(sportType)) || !validateExceptions(exceptions)) {
            throw new UnacceptableParamException("unacceptable sport_type or exceptions");
        }

        Stadium target = stadiumRepository.findOne(stadiumId);
        if (target == null) {
            throw new UnacceptableParamException("unacceptable stadium_id");
        }

        if (name != null) {
            target.setName(name);
        }
        if (stadiumCode != null) {
            target.setStadiumCode(stadiumCode);
        }
        if (siteCode != null) {
            target.setSiteCode(siteCode);
        }
        if (sportType != null) {
            target.setSportType(sportType);
        }
        if (exceptions != null) {
            target.setExceptions(exceptions);
        }
        if (description != null) {
            target.setDescription(description);
        }

        return stadiumRepository.save(target);
    }

    @Override
    public boolean deleteStadium(Long stadiumId) {
        Stadium target = stadiumRepository.findOne(stadiumId);
        if (target == null) {
            throw new UnacceptableParamException("unacceptable stadium_id");
        }

        stadiumRepository.delete(target);
        return true;
    }

    private boolean validateExceptions(String exceptions) {
        if (exceptions != null) {
            List<String> list = CommonUtil.splitStringBySeparator(exceptions, CommonUtil.SEMICOLON_SEPARATOR);
            for (String item : list) {
                for (int i = 0; i < item.length(); ++i) {
                    if (!Character.isDigit(item.charAt(i))) return false;
                }
            }
        }
        return true;
    }

}
