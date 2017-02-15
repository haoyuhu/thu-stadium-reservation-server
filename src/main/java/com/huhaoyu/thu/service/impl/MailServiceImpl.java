package com.huhaoyu.thu.service.impl;

import com.huhaoyu.thu.config.MailSenderConfiguration;
import com.huhaoyu.thu.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:48.
 */

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final int DEFAULT_SYNC_SENDING_COUNT_LIMIT = 5;

    @Autowired
    private MailSender mailSender;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private MailSenderConfiguration config;

    @Override
    public void sendMail(SimpleMailMessage message) throws Exception {
        Assert.notNull(message);
        if (message.getTo().length > DEFAULT_SYNC_SENDING_COUNT_LIMIT) {
            sendMailByAsynchronousMode(message);
        } else {
            sendMailBySynchronousMode(message);
        }
    }

    @Override
    public void sendMailByAsynchronousMode(final SimpleMailMessage message) throws Exception {
        taskExecutor.execute(() -> {
            try {
                sendMailBySynchronousMode(message);
            } catch (Exception e) {
                logger.error("cannot send mails by mail service at MailServiceImpl.sendMailByAsynchronousMode()", e.getMessage(), e);
            }
        });
    }

    @Override
    public void sendMailBySynchronousMode(SimpleMailMessage message) throws MailException {
        mailSender.send(message);
    }

    @Override
    public SimpleMailMessage createSimpleTextMailMessage(String subject, String content, String[] receivers)
            throws IllegalArgumentException {
        return createSimpleTextMailMessage(subject, content, receivers, null);
    }

    @Override
    public SimpleMailMessage createSimpleTextMailMessage(String subject, String content, String[] receivers, String[] ccs)
            throws IllegalArgumentException {
        if (StringUtils.isEmpty(subject) || StringUtils.isEmpty(content) || receivers == null || receivers.length == 0) {
            throw new IllegalArgumentException("subject, content should not be null, and receivers should be at least 1 receiver.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(createAddressWithNickname(config.getNickname(), config.getUsername()));
        message.setTo(receivers);
        message.setSubject(subject);
        message.setText(content);
        if (ccs != null && ccs.length != 0) {
            message.setCc(ccs);
        }
        return message;
    }

    @Override
    public String createAddressWithNickname(String nickname, String address) {
        return String.format("%s<%s>", nickname, address);
    }

}
