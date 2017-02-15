package com.huhaoyu.thu.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by huhaoyu
 * Created On 2017/1/25 下午4:52.
 */

@Getter
@Configuration
@PropertySource(value = "classpath:wechat.properties")
public class WechatConfiguration {

    @Value(value = "${wechat.appId}")
    private String appId;
    @Value(value = "${wechat.appSecret}")
    private String secret;

}
