package com.huhaoyu.thu.controller;

import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.entity.WechatToken;
import com.huhaoyu.thu.service.AuthorizationService;
import com.huhaoyu.thu.service.WechatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:42.
 */

@RestController
public class AuthorizationController {

    @Autowired
    private WechatService wechatService;
    @Autowired
    private AuthorizationService authService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity login(@RequestParam(value = "code") String code,
                                @RequestParam(value = "nickname") String nickname,
                                @RequestParam(value = "gender") Integer gender,
                                @RequestParam(value = "language", required = false) String language,
                                @RequestParam(value = "city", required = false) String city,
                                @RequestParam(value = "province", required = false) String province,
                                @RequestParam(value = "country", required = false) String country,
                                @RequestParam(value = "avatar_url") String avatarUrl,
                                @RequestParam(value = "description") String description) {
        WechatToken token = wechatService.getWechatTokenByCode(code);
        if (token == null) {
            return ResponseEntity.ok(Response.IncorrectAuthCode.createResponseMap());
        }
        String sessionId = authService.createSessionId();
        if (wechatService.hasWechatUserAccordingToOpenId(token.getOpenId())) {
            wechatService.updateWechatUserByOpenId(token.getOpenId(), nickname, gender, language, city, province,
                    country, avatarUrl, description);
        } else {
            wechatService.createWechatUser(token.getOpenId(), nickname, gender, language, city, province, country,
                    avatarUrl, description);
        }
        authService.saveOpenIdBySessionId(sessionId, token.getOpenId());
        authService.saveSessionKeyBySessionId(sessionId, token.getSessionKey());
        Map<String, String> data = new HashMap<>();
        data.put(AuthorizationService.SESSION_ID, sessionId);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    public ResponseEntity logout(@RequestParam(value = "session_id") String sessionId) {
        authService.deleteAllCacheBySessionId(sessionId);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

}
