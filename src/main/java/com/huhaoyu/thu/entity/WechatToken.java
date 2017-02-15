package com.huhaoyu.thu.entity;

import lombok.Getter;

/**
 * Created by huhaoyu
 * Created On 2017/2/7 下午4:43.
 */

@Getter
public class WechatToken {

    private String sessionKey;

    private String openId;

    public WechatToken(String sessionKey, String openId) {
        this.sessionKey = sessionKey;
        this.openId = openId;
    }

}
