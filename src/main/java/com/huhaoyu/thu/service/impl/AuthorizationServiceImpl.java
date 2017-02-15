package com.huhaoyu.thu.service.impl;

import com.huhaoyu.thu.common.RedisUtil;
import com.huhaoyu.thu.config.SecurityConfiguration;
import com.huhaoyu.thu.service.AuthorizationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 上午10:56.
 */

@Service
@Transactional
public class AuthorizationServiceImpl implements AuthorizationService {

    private static final int SESSION_ID_LENGTH = 32;
    @Autowired
    private SecurityConfiguration config;
    @Autowired
    private RedisUtil redis;

    @Override
    public boolean isAuthenticatedUser(String sessionId) {
        return redis.exists(sessionId);
    }

    @Override
    public boolean isVerifiedTaskClient(String secretId) {
        return config.getScheduledTaskSecretId().equals(secretId);
    }

    @Override
    public String createSessionId() {
        return RandomStringUtils.random(SESSION_ID_LENGTH, true, true);
    }

    @Override
    public void refreshAllCacheBySessionId(String sessionId) {
        redis.expire(sessionId, config.getWechatSessionTimeout());
    }

    @Override
    public void deleteAllCacheBySessionId(String sessionId) {
        if (redis.exists(sessionId)) {
            redis.remove(sessionId);
        }
    }

    @Override
    public boolean saveSessionKeyBySessionId(String sessionId, String sessionKey) {
        if (redis.setHash(sessionId, AuthorizationService.SESSION_KEY, sessionKey)) {
            redis.expire(sessionId, config.getWechatSessionTimeout());
            return true;
        }
        return false;
    }

    @Override
    public boolean saveOpenIdBySessionId(String sessionId, String openId) {
        if (redis.setHash(sessionId, AuthorizationService.OPEN_ID, openId)) {
            redis.expire(sessionId, config.getWechatSessionTimeout());
            return true;
        }
        return false;
    }

    @Override
    public String getSessionKeyBySessionId(String sessionId) {
        if (redis.exists(sessionId)) {
            return (String) redis.getHash(sessionId, AuthorizationService.SESSION_KEY);
        }
        return null;
    }

    @Override
    public String getOpenIdBySessionId(String sessionId) {
        if (redis.exists(sessionId)) {
            return (String) redis.getHash(sessionId, AuthorizationService.OPEN_ID);
        }
        return null;
    }

}
