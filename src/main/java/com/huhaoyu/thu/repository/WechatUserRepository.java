package com.huhaoyu.thu.repository;

import com.huhaoyu.thu.entity.WechatUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:47.
 */

public interface WechatUserRepository extends JpaRepository<WechatUser, Long> {

    WechatUser findByOpenId(String openId);

}
