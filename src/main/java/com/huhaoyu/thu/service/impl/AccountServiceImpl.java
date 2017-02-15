package com.huhaoyu.thu.service.impl;

import com.huhaoyu.thu.common.Constants;
import com.huhaoyu.thu.common.Constants.AccountStatus;
import com.huhaoyu.thu.common.HttpUtil;
import com.huhaoyu.thu.common.UnacceptableParamException;
import com.huhaoyu.thu.common.Validator;
import com.huhaoyu.thu.entity.THUAccount;
import com.huhaoyu.thu.entity.WechatUser;
import com.huhaoyu.thu.repository.THUAccountRepository;
import com.huhaoyu.thu.service.AccountService;
import com.huhaoyu.thu.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 上午10:56.
 */

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    @Autowired
    private THUAccountRepository accountRepository;
    @Autowired
    private WechatService wechatService;

    @Override
    public List<THUAccount> findAccountByProperties(String openId, Long accountId, String studentId, String username,
                                                    String alias, Integer status, Integer userType) {
        if (status != null && !AccountStatus.validate(status) || userType != null && !Constants.UserType.validate(userType)) {
            throw new UnacceptableParamException("unacceptable status or user_type");
        }

        WechatUser user = wechatService.findWechatUserByOpenId(openId);
        THUAccount account = new THUAccount();
        account.setId(accountId);
        account.setStudentId(studentId);
        account.setUsername(username);
        account.setAlias(alias);
        account.setStatus(status);
        account.setUserType(userType);
        account.setUser(user);

        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<THUAccount> example = Example.of(account, matcher);

        return accountRepository.findAll(example);
    }

    @Override
    public THUAccount createAccount(String openId, String studentId, String username, String password, String alias,
                                    Integer userType, String phoneNumber, String email, String description) {
        if (!Validator.validateStringNotEmpty(studentId, username, password).isPassed()
                || !Validator.validatePhone(phoneNumber).isPassed()
                || !Validator.validateMail(email).isPassed()
                || !Validator.validateNotNull(userType).isPassed()
                || !Constants.UserType.validate(userType)) {
            throw new UnacceptableParamException("unacceptable student_id, username, password, phone_number, email or user_type");
        }
        WechatUser user = wechatService.findWechatUserByOpenId(openId);
        Set<THUAccount> accounts = user.getAccounts();
        if (accounts == null) {
            accounts = new HashSet<>();
        }
        boolean hasAccountBeingUsed = false;

        for (THUAccount acc : accounts) {
            if (!hasAccountBeingUsed && AccountStatus.Using == AccountStatus.from(acc.getStatus())) {
                hasAccountBeingUsed = true;
            }
            if (acc.getStudentId().equals(studentId) || acc.getUsername().equals(username)) {
                throw new UnacceptableParamException(Constants.Response.EntityExists, "same account exists");
            }
        }

        if (!verifyAccount(studentId, password)) {
            throw new UnacceptableParamException(Constants.Response.IncorrectTHUAccount, "cannot verify tsinghua account, wrong student_id or password");
        }

        THUAccount account = new THUAccount();
        account.setStudentId(studentId);
        account.setUsername(username);
        account.setPassword(password);
        account.setAlias(alias);
        account.setUserType(userType);
        account.setPhoneNumber(phoneNumber);
        account.setEmail(email);
        account.setStatus(!hasAccountBeingUsed ? AccountStatus.Using.getCode() : AccountStatus.Available.getCode());
        account.setDescription(description);
        account.setUser(user);

        return accountRepository.save(account);
    }

    @Override
    public THUAccount updateAccount(String openId, Long accountId, String password, String alias, Integer userType,
                                    String phoneNumber, String email, String description) {
        THUAccount account = getAccountByOpenId(openId, accountId);
        if (phoneNumber != null && !Validator.validatePhone(phoneNumber).isPassed()
                || email != null && !Validator.validateMail(email).isPassed()
                || userType != null && !Constants.UserType.validate(userType)) {
            throw new UnacceptableParamException("invalid phone, email or user_type");
        }
        if (password != null && !verifyAccount(account.getStudentId(), password)) {
            throw new UnacceptableParamException(Constants.Response.IncorrectTHUAccount, "cannot verify tsinghua account, wrong student_id or password");
        }

        if (password != null) {
            account.setPassword(password);
        }
        if (alias != null) {
            account.setAlias(alias);
        }
        if (userType != null) {
            account.setUserType(userType);
        }
        if (phoneNumber != null) {
            account.setPhoneNumber(phoneNumber);
        }
        if (email != null) {
            account.setEmail(email);
        }
        if (description != null) {
            account.setDescription(description);
        }

        return accountRepository.save(account);
    }

    @Override
    public boolean deleteAccount(String openId, Long accountId) {
        WechatUser user = wechatService.findWechatUserByOpenId(openId);
        THUAccount target = getAccountByOpenId(openId, accountId);
        Set<THUAccount> accounts = user.getAccounts();
        accounts.remove(target);
        wechatService.insertOrUpdateWechatUser(user);
        accountRepository.delete(target);
        return true;
    }

    @Override
    public boolean activateAccount(String openId, Long accountId, Integer st) {
        THUAccount account = getAccountByOpenId(openId, accountId);
        AccountStatus target = AccountStatus.from(st);
        AccountStatus current = AccountStatus.from(account.getStatus());
        if (target == null || AccountStatus.Using != target && AccountStatus.Available != target) {
            throw new UnacceptableParamException("unacceptable status");
        }
        if (current == target) {
            throw new UnacceptableParamException("status is already in the desire state");
        }

        // verify current tsinghua account username and password
        if (!verifyAccount(account.getStudentId(), account.getPassword())) {
            account.setStatus(AccountStatus.VerificationFail.getCode());
            accountRepository.save(account);
            throw new UnacceptableParamException(Constants.Response.IncorrectTHUAccount);
        }

        // if account's current status is OTHER STATUS and status should be USING
        if (AccountStatus.Using == target) {
            WechatUser user = wechatService.findWechatUserByOpenId(openId);
            for (THUAccount acc : user.getAccounts()) {
                AccountStatus status = AccountStatus.from(acc.getStatus());
                if (status == AccountStatus.Using) {
                    acc.setStatus(AccountStatus.Available.getCode());
                    accountRepository.save(acc);
                    break;
                }
            }
        }
        account.setStatus(target.getCode());
        accountRepository.save(account);
        return true;
    }

    @Override
    public boolean verifyAccount(String studentId, String password) {
        final String scheme = HttpUtil.Scheme.HTTPS;
        final String host = "sslvpn.tsinghua.edu.cn";
        final String[] segments = {"dana-na", "auth", "url_default", "login.cgi"};
        final Map<String, Object> data = new HashMap<>();
        data.put("username", studentId);
        data.put("password", password);
        data.put("realm", "ldap");

        String res = HttpUtil.post(scheme, host, null, segments, null, data);
        return res != null && !res.contains("用户名或密码无效") && !res.contains("Invalid username or password");
    }

    private THUAccount getAccountByOpenId(String openId, Long accountId) {
        WechatUser user = wechatService.findWechatUserByOpenId(openId);
        THUAccount account = null;
        for (THUAccount acc : user.getAccounts()) {
            if (acc.getId().equals(accountId)) {
                account = acc;
                break;
            }
        }
        if (account == null) {
            throw new UnacceptableParamException("invalid account_id");
        }
        return account;
    }

}
