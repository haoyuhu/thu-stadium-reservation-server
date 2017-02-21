package com.huhaoyu.thu.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:49.
 */
public interface MailService {

    void sendMail(final MimeMessage message) throws Exception;

    void sendMailByAsynchronousMode(final MimeMessage message) throws Exception;

    void sendMailBySynchronousMode(final MimeMessage message) throws Exception;

    MimeMessage createSimpleTextMailMessage(final String subject, final String content,
                                            final String[] receivers) throws IllegalArgumentException, MessagingException;

    MimeMessage createSimpleTextMailMessage(final String subject, final String content, final String[] receivers,
                                            final String[] ccs) throws IllegalArgumentException, MessagingException;

    String createAddressWithNickname(String nickname, String address);

}
