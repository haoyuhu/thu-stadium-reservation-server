package com.huhaoyu.thu.service;

import com.huhaoyu.thu.entity.WechatToken;
import com.huhaoyu.thu.entity.WechatUser;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/31 下午9:55.
 */

public interface WechatService {

    WechatToken getWechatTokenByCode(String code);

    boolean hasWechatUserAccordingToOpenId(String openId);

    WechatUser findWechatUserByOpenId(String openId);

    WechatUser createWechatUser(String openId, String nickname, Integer gender, String language, String city,
                                String province, String country, String avatarUrl, String description);

    WechatUser updateWechatUserByOpenId(String openId, String nickname, Integer gender, String language, String city,
                                        String province, String country, String avatarUrl, String description);

    WechatUser insertOrUpdateWechatUser(WechatUser user);

    List<WechatUser> findAllWechatUser();

}
