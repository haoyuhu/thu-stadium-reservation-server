package com.huhaoyu.thu.service.impl;

import com.huhaoyu.thu.common.CommonUtil;
import com.huhaoyu.thu.common.Constants;
import com.huhaoyu.thu.common.UnacceptableParamException;
import com.huhaoyu.thu.common.Validator;
import com.huhaoyu.thu.entity.ReservationCandidate;
import com.huhaoyu.thu.entity.ReservationGroup;
import com.huhaoyu.thu.entity.WechatUser;
import com.huhaoyu.thu.repository.CandidateRepository;
import com.huhaoyu.thu.repository.GroupRepository;
import com.huhaoyu.thu.service.ReservationService;
import com.huhaoyu.thu.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 上午10:57.
 */

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private WechatService wechatService;

    @Override
    public List<ReservationGroup> getReservationGroupByProperties(String openId, Long groupId, Boolean available, String name) {
        WechatUser user = wechatService.findWechatUserByOpenId(openId);
        ReservationGroup group = new ReservationGroup();
        group.setUser(user);
        group.setId(groupId);
        group.setAvailable(available);
        group.setName(name);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<ReservationGroup> example = Example.of(group, matcher);

        return groupRepository.findAll(example);
    }

    @Override
    public ReservationGroup createReservationGroup(String openId, String name, Boolean available, String description,
                                                   String mailReceivers) {
        final String DEFAULT_GROUP_NAME = "标准愿望单";

        WechatUser user = wechatService.findWechatUserByOpenId(openId);
        if (!validateMailReceivers(mailReceivers)) {
            throw new UnacceptableParamException("unacceptable mail_receivers");
        }
        if (available == null) {
            available = true;
        }
        if (StringUtils.isEmpty(name)) {
            name = DEFAULT_GROUP_NAME;
        }
        ReservationGroup group = new ReservationGroup();
        group.setName(name);
        group.setAvailable(available);
        group.setReceivers(mailReceivers);
        group.setUser(user);
        group.setDescription(description);

        return groupRepository.save(group);
    }

    @Override
    public ReservationGroup updateReservationGroup(String openId, Long groupId, String name, Boolean available,
                                                   String description, String mailReceivers) {
        ReservationGroup target = groupRepository.findOne(groupId);
        if (target == null || !target.getUser().getOpenId().equals(openId)) {
            throw new UnacceptableParamException("unacceptable group_id");
        }
        if (!validateMailReceivers(mailReceivers)) {
            throw new UnacceptableParamException("unacceptable mail_receivers");
        }

        if (!StringUtils.isEmpty(name)) {
            target.setName(name);
        }
        if (available != null) {
            target.setAvailable(available);
        }
        if (description != null) {
            target.setDescription(description);
        }
        if (mailReceivers != null) {
            target.setReceivers(mailReceivers);
        }
        return groupRepository.save(target);
    }

    @Override
    public boolean deleteReservationGroup(String openId, Long groupId) {
        ReservationGroup target = groupRepository.findOne(groupId);
        if (target == null || !target.getUser().getOpenId().equals(openId)) {
            throw new UnacceptableParamException("unacceptable group_id");
        }
        groupRepository.delete(target);
        return true;
    }

    @Override
    public List<ReservationCandidate> getReservationCandidateByProperties(String openId, Long groupId, Long candidateId,
                                                                          Boolean available, Integer sportType) {
        if (sportType != null && !Constants.SportType.validate(sportType)) {
            throw new UnacceptableParamException("unacceptable sport_type");
        }

        ReservationGroup group = findGroupById(openId, groupId);
        Set<ReservationCandidate> candidates = group.getCandidates();
        return candidates.stream().filter(c -> (candidateId == null || c.getId().equals(candidateId))
                && (available == null || c.getAvailable().equals(available))
                && (sportType == null || c.getSportType().equals(sportType))).collect(Collectors.toList());
    }

    @Override
    public ReservationCandidate createReservationCandidate(String openId, Long groupId, Boolean available, Integer sportType,
                                                           Integer week, String wishStart, String wishEnd, String sectionStart,
                                                           String sectionEnd, Boolean fixed, String description) {
        ReservationGroup group = findGroupById(openId, groupId);
        Set<ReservationCandidate> candidates = group.getCandidates();
        if (candidates == null) {
            candidates = new HashSet<>();
        }

        if (available == null) available = true;
        ReservationCandidate candidate = new ReservationCandidate();
        candidate.setAvailable(available);
        candidate.setSportType(sportType);
        candidate.setWeek(week);
        candidate.setWishStartTime(wishStart);
        candidate.setWishEndTime(wishEnd);
        candidate.setSectionStartTime(sectionStart);
        candidate.setSectionEndTime(sectionEnd);
        candidate.setFixed(fixed);
        candidate.setDescription(description);
        validateCandidate(candidate);
        candidate = candidateRepository.save(candidate);

        candidates.add(candidate);
        group.setCandidates(candidates);
        groupRepository.save(group);

        return candidate;
    }

    @Override
    public ReservationCandidate updateReservationCandidate(String openId, Long groupId, Long candidateId, Boolean available,
                                                           Integer sportType, Integer week, String wishStart, String wishEnd,
                                                           String sectionStart, String sectionEnd, Boolean fixed,
                                                           String description) {
        ReservationCandidate target = findCandidateByGroupIdAndCandidateId(openId, groupId, candidateId);
        if (target == null) {
            throw new UnacceptableParamException("unacceptable candidate_id");
        }

        if (available != null) {
            target.setAvailable(available);
        }
        if (sportType != null) {
            target.setSportType(sportType);
        }
        if (week != null) {
            target.setWeek(week);
        }
        if (wishStart != null) {
            target.setWishStartTime(wishStart);
        }
        if (wishEnd != null) {
            target.setWishEndTime(wishEnd);
        }
        if (sectionStart != null) {
            target.setSectionStartTime(sectionStart);
        }
        if (sectionEnd != null) {
            target.setSectionEndTime(sectionEnd);
        }
        if (fixed != null) {
            target.setFixed(fixed);
        }
        if (description != null) {
            target.setDescription(description);
        }
        validateCandidate(target);
        return candidateRepository.save(target);
    }

    @Override
    public boolean deleteReservationCandidate(String openId, Long groupId, Long candidateId) {
        ReservationCandidate target = findCandidateByGroupIdAndCandidateId(openId, groupId, candidateId);
        if (target == null) {
            throw new UnacceptableParamException("unacceptable candidate_id");
        }
        ReservationGroup parent = findGroupById(openId, groupId);
        parent.getCandidates().remove(target);
        groupRepository.save(parent);
        candidateRepository.delete(target);
        return true;
    }

    private boolean validateMailReceivers(String mailReceivers) {
        if (!StringUtils.isEmpty(mailReceivers)) {
            List<String> receivers = CommonUtil.splitStringBySeparator(mailReceivers, CommonUtil.SEMICOLON_SEPARATOR);
            for (String receiver : receivers) {
                if (!Validator.validateMail(receiver).isPassed()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateCandidate(ReservationCandidate candidate) {
        Integer sportType = candidate.getSportType();
        Integer week = candidate.getWeek();
        Boolean fixed = candidate.getFixed();
        String sectionStart = candidate.getSectionStartTime();
        String sectionEnd = candidate.getSectionEndTime();
        String wishStart = candidate.getWishStartTime();
        String wishEnd = candidate.getWishEndTime();

        if (!Constants.SportType.validate(sportType)) {
            throw new UnacceptableParamException("unacceptable sport_type");
        }
        if (!CommonUtil.validateWeek(week)) {
            throw new UnacceptableParamException("unacceptable week");
        }
        try {
            if (!fixed && (sectionStart == null || sectionEnd == null || !CommonUtil.compareTimeString(sectionStart, sectionEnd))) {
                throw new UnacceptableParamException("should provide section_start and section_end and section_start < section_end when not fixed");
            }
            if (fixed && (sectionStart == null && sectionEnd != null || sectionStart != null && sectionEnd == null
                    || sectionStart != null && !CommonUtil.compareTimeString(sectionStart, sectionEnd))) {
                throw new UnacceptableParamException("should provide both section_start and section_end and section_start < section_end when fixed, or all be null");
            }
            if (!CommonUtil.compareTimeString(wishStart, wishEnd)
                    || (sectionStart != null && !CommonUtil.validateTimeString(sectionStart))
                    || (sectionEnd != null && !CommonUtil.validateTimeString(sectionEnd))) {
                throw new UnacceptableParamException(("unacceptable wish_start, wish_end, section_start or section_end"));
            }
        } catch (IllegalArgumentException e) {
            throw new UnacceptableParamException(e.getMessage());
        }
        return true;
    }

    private ReservationCandidate findCandidateByGroupIdAndCandidateId(String openId, Long groupId, Long candidateId) {
        ReservationGroup group = findGroupById(openId, groupId);
        ReservationCandidate target = null;
        Set<ReservationCandidate> candidates = group.getCandidates();
        if (candidates == null) {
            throw new UnacceptableParamException("unacceptable candidate_id");
        }
        for (ReservationCandidate c : candidates) {
            if (c.getId().equals(candidateId)) {
                target = c;
                break;
            }
        }
        return target;
    }

    private ReservationGroup findGroupById(String openId, Long groupId) {
        List<ReservationGroup> groups = getReservationGroupByProperties(openId, groupId, null, null);
        if (groups.size() != 1) {
            throw new UnacceptableParamException("unacceptable group_id");
        }
        return groups.get(0);
    }

}
