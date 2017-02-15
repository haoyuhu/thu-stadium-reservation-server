package com.huhaoyu.thu.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huhaoyu.thu.common.*;
import com.huhaoyu.thu.config.WechatConfiguration;
import com.huhaoyu.thu.entity.WechatToken;
import com.huhaoyu.thu.entity.WechatUser;
import com.huhaoyu.thu.repository.WechatUserRepository;
import com.huhaoyu.thu.service.WechatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 上午10:58.
 */

@Service
@Transactional
public class WechatServiceImpl implements WechatService {

    private static final Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

    @Autowired
    private WechatUserRepository userRepository;
    @Autowired
    private WechatConfiguration config;

    @Override
    public WechatToken getWechatTokenByCode(String code) {
        final String scheme = HttpUtil.Scheme.HTTPS;
        final String host = "api.weixin.qq.com";
        final String[] segments = {"sns", "jscode2session"};
        final Map<String, Object> query = new HashMap<>();
        query.put("appid", config.getAppId());
        query.put("secret", config.getSecret());
        query.put("js_code", code);
        query.put("grant_type", "authorization_code");

        String res = HttpUtil.get(scheme, host, null, segments, query);
        if (res == null) {
            throw new ServerErrorException(Constants.Response.WechatServerError, "network error");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map result = mapper.readValue(res, Map.class);
            if (result.containsKey("openid") && result.containsKey("session_key")) {
                String openId = (String) result.get("openid");
                String sessionKey = (String) result.get("session_key");
                if (!StringUtils.isEmpty(openId) && !StringUtils.isEmpty(sessionKey)) {
                    return new WechatToken(sessionKey, openId);
                }
            }
            logger.error("error response from api.weixin.qq.com when fetch wechat token by code: " + res);
            throw new UnacceptableParamException(Constants.Response.IncorrectAuthCode);
        } catch (IOException e) {
            logger.error("cannot parse response from api.weixin.qq.com when fetch wechat token by code: " + res, e);
            throw new ServerErrorException(Constants.Response.WechatServerError, "parse json response error");
        }
    }

    @Override
    public boolean hasWechatUserAccordingToOpenId(String openId) {
        WechatUser user = new WechatUser();
        user.setOpenId(openId);
        Example<WechatUser> example = Example.of(user);
        return userRepository.exists(example);
    }

    @Override
    public WechatUser findWechatUserByOpenId(String openId) {
        WechatUser user = userRepository.findByOpenId(openId);
        if (user == null) {
            throw new UnacceptableParamException("server error with unacceptable open_id");
        }
        return user;
    }

    @Override
    public WechatUser createWechatUser(String openId, String nickname, Integer gender, String language, String city,
                                       String province, String country, String avatarUrl, String description) {
        if (!Validator.validateUrl(avatarUrl).isPassed() || !Constants.Gender.validate(gender)) {
            throw new UnacceptableParamException("unacceptable avatar_url or gender");
        }

        WechatUser user = new WechatUser();
        user.setOpenId(openId);
        user.setNickName(nickname);
        user.setGender(gender);
        user.setLanguage(language);
        user.setCity(city);
        user.setProvince(province);
        user.setCountry(country);
        user.setAvatarUrl(avatarUrl);
        user.setDescription(description);

        return userRepository.save(user);
    }

    @Override
    public WechatUser updateWechatUserByOpenId(String openId, String nickname, Integer gender, String language,
                                               String city, String province, String country, String avatarUrl, String description) {
        if (avatarUrl != null && !Validator.validateUrl(avatarUrl).isPassed()) {
            throw new UnacceptableParamException("unacceptable avatar_url");
        }
        if (gender != null && !Constants.Gender.validate(gender)) {
            throw new UnacceptableParamException("unacceptable gender");
        }

        WechatUser user = findWechatUserByOpenId(openId);
        if (nickname != null) {
            user.setNickName(nickname);
        }
        if (gender != null) {
            user.setGender(gender);
        }
        if (language != null) {
            user.setLanguage(language);
        }
        if (city != null) {
            user.setCity(city);
        }
        if (province != null) {
            user.setProvince(province);
        }
        if (country != null) {
            user.setCountry(country);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        if (description != null) {
            user.setDescription(description);
        }

        return userRepository.save(user);
    }

    @Override
    public WechatUser insertOrUpdateWechatUser(WechatUser user) {
        return userRepository.save(user);
    }

    @Override
    public List<WechatUser> findAllWechatUser() {
        return userRepository.findAll();
    }

}
