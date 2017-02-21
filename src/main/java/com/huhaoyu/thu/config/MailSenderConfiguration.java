package com.huhaoyu.thu.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Properties;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午5:17.
 */

@Configuration
@PropertySource(value = "classpath:mail.properties")
public class MailSenderConfiguration {

    private static final int CORE_POOL_SIZE = 10;
    private static final int MAX_POOL_SIZE = 30;

    @Value(value = "${mail.hostName}")
    private String host;
    @Value(value = "${mail.smtpPort}")
    private int port;
    @Getter
    @Value(value = "${mail.smtpUsername}")
    private String username;
    @Value(value = "${mail.smtpPassword}")
    private String password;
    @Value(value = "${mail.transport.protocol}")
    private String protocol;

    @Value(value = "${mail.smtp.auth}")
    private boolean authentication;
    @Value(value = "${mail.smtp.socketFactory.class}")
    private String factory;
    @Value(value = "${mail.smtp.socketFactory.fallback}")
    private boolean fallback;
    @Value(value = "${mail.smtp.timeout}")
    private int timeout;
    @Value(value = "${mail.debug}")
    private boolean debug;

    @Getter
    @Value(value = "${mail.nickname}")
    private String nickname;

    @Bean
    JavaMailSender mailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setProtocol(protocol);

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", authentication);
        properties.put("mail.smtp.socketFactory.class", factory);
        properties.put("mail.smtp.socketFactory.fallback", fallback);
        properties.put("mail.smtp.timeout", timeout);
        properties.put("mail.debug", debug);
        sender.setJavaMailProperties(properties);

        return sender;
    }

    @Bean
    TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        return executor;
    }

}
