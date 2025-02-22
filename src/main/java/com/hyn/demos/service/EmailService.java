package com.hyn.demos.service;

import java.util.List;

/**
 * 邮件服务
 */
public interface EmailService {
    void sendEmailToSomeone(String to,String context,String subject);

    void sendTemplateMail(List<String> emailAddressesList, String subject, String templateName, String senderName, String username);

    void sendRegisterTemplateMail(List<String> emailAddressesList, String subject, String templateName, String senderName, String email,String code);

}
