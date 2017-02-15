package com.huhaoyu.thu.service;

import com.huhaoyu.thu.entity.THUAccount;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:48.
 */

public interface AccountService {

    List<THUAccount> findAccountByProperties(String openId, Long accountId, String studentId, String username,
                                             String alias, Integer status, Integer userType);

    THUAccount createAccount(String openId, String studentId, String username, String password, String alias,
                         Integer userType, String phoneNumber, String email, String description);

    THUAccount updateAccount(String openId, Long accountId, String password, String alias, Integer userType,
                         String phoneNumber, String email, String description);

    boolean deleteAccount(String openId, Long accountId);

    boolean activateAccount(String openId, Long accountId, Integer status);

    boolean verifyAccount(String studentId, String password);

}
