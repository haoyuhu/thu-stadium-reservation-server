package com.huhaoyu.thu.service;

import org.springframework.mail.SimpleMailMessage;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:49.
 */
public interface MailService {

    void sendMail(final SimpleMailMessage message) throws Exception;

    void sendMailByAsynchronousMode(final SimpleMailMessage message) throws Exception;

    void sendMailBySynchronousMode(final SimpleMailMessage message) throws Exception;

    SimpleMailMessage createSimpleTextMailMessage(final String subject, final String content,
                                                  final String[] receivers) throws IllegalArgumentException;

    SimpleMailMessage createSimpleTextMailMessage(final String subject, final String content, final String[] receivers,
                                                  final String[] ccs) throws IllegalArgumentException;

    String createAddressWithNickname(String nickname, String address);

}
