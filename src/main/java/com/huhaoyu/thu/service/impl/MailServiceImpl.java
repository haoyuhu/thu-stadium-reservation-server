package com.huhaoyu.thu.service.impl;

import com.huhaoyu.thu.config.MailSenderConfiguration;
import com.huhaoyu.thu.service.MailService;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:48.
 */

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    private static final int DEFAULT_SYNC_SENDING_COUNT_LIMIT = 5;

    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private MailSenderConfiguration config;

    @Override
    public void sendMail(MimeMessage message) throws Exception {
        Assert.notNull(message);
        if (message.getRecipients(Message.RecipientType.TO).length > DEFAULT_SYNC_SENDING_COUNT_LIMIT) {
            sendMailByAsynchronousMode(message);
        } else {
            sendMailBySynchronousMode(message);
        }
    }

    @Override
    public void sendMailByAsynchronousMode(final MimeMessage message) throws Exception {
        taskExecutor.execute(() -> {
            try {
                sendMailBySynchronousMode(message);
            } catch (Exception e) {
                logger.error("cannot send mails by mail service at MailServiceImpl.sendMailByAsynchronousMode()", e.getMessage(), e);
            }
        });
    }

    @Override
    public void sendMailBySynchronousMode(MimeMessage message) throws MailException {
        mailSender.send(message);
    }

    @Override
    public MimeMessage createSimpleTextMailMessage(String subject, String content, String[] receivers)
            throws IllegalArgumentException, MessagingException {
        return createSimpleTextMailMessage(subject, content, receivers, null);
    }

    @Override
    public MimeMessage createSimpleTextMailMessage(String subject, String content, String[] receivers, String[] ccs)
            throws IllegalArgumentException, MessagingException {
        if (StringUtils.isEmpty(subject) || StringUtils.isEmpty(content) || receivers == null || receivers.length == 0) {
            throw new IllegalArgumentException("subject, content should not be null, and receivers should be at least 1 receiver.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, CharEncoding.UTF_8);
        helper.setTo(receivers);
        helper.setFrom(createAddressWithNickname(config.getNickname(), config.getUsername()));
        helper.setSubject(subject);
        helper.setText(content);
        if (ccs != null && ccs.length != 0) {
            helper.setCc(ccs);
        }
        return message;
    }

    @Override
    public String createAddressWithNickname(String nickname, String address) {
        return String.format("%s<%s>", nickname, address);
    }

}
