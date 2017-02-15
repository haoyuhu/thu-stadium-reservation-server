package com.huhaoyu.thu.config;

import com.huhaoyu.thu.realm.WechatUserRealm;
import lombok.Getter;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.eis.JavaUuidSessionIdGenerator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午5:17.
 */

@Getter
@Configuration
@PropertySource(value = "classpath:security.properties")
public class SecurityConfiguration {

    @Value(value = "${encryption.algorithm.md5}")
    private String signatureAlgorithm;
    @Value(value = "${encryption.algorithm.aes}")
    private String symmetricEncryptionAlgorithm;
    @Value(value = "${encryption.iteration}")
    private int iterations;
    @Value(value = "${session.normal.timeout}")
    private long sessionTimeout;
    @Value(value = "${session.wechat.timeout}")
    private long wechatSessionTimeout;
    @Value(value = "${scheduledTask.secretId}")
    private String scheduledTaskSecretId;
    @Value(value = "${scheduledTask.secretKey}")
    private String scheduledTaskSecretKey;

    @Bean
    public SecurityManager securityManager() {
        WechatUserRealm realm = new WechatUserRealm();
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        return manager;
    }

    @Autowired
    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        bean.setSecurityManager(securityManager);
        return bean;
    }

    @Bean
    public JavaUuidSessionIdGenerator sessionIdGenerator() {
        return new JavaUuidSessionIdGenerator();
    }

    @Bean
    public SimpleCookie sessionIdCookie() {
        SimpleCookie cookie = new SimpleCookie();
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        return cookie;
    }

    @Autowired
    @Bean
    public SessionManager sessionManager(Cookie cookie) {
        DefaultWebSessionManager manager = new DefaultWebSessionManager();
        manager.setSessionIdCookie(cookie);
        manager.setSessionIdCookieEnabled(true);
        manager.setGlobalSessionTimeout(sessionTimeout);
        return manager;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    @Autowired
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager manager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(manager);
        return advisor;
    }

}

