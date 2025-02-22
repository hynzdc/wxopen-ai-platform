package com.hyn.demos.service.impl;

import com.hyn.demos.config.MyRedissonConfig;
import com.hyn.demos.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * @author hyn
 * @version 1.0.0
 * @description 邮件服务
 * @date 2023/4/6
 */
@Service
public class EmailServiceImpl implements EmailService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String EMAIL_VERIFY_CODE = "VERIFY_CODE_%s:%s";
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Value("${spring.mail.nickname}")
    private String nickName;
    @Resource
    private MyRedissonConfig redissonConfig;
    @Override
    public void sendEmailToSomeone(String to, String context,String subject) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setFrom(nickName+"<"+from+">");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(context,true);
            mailSender.send(mimeMessage);
            logger.info("发送邮件发送成功");
        }catch (Exception e){
            logger.error("错误信息,{}",e.getMessage());
        }
    }

    @Override
    public void sendTemplateMail(List<String> emailAddressesList, String subject, String templateName, String senderName, String username) {
        try {
            //根据templateName获取邮件模版
            Context context = new Context();
            context.setVariable("username", username);  // 用户名
            context.setVariable("wxOpenName", "进化码"); // 公众号名称
            String emailTemplate = templateEngine.process(templateName, context);
            emailAddressesList.forEach(e -> sendHtmlMail(e, subject, emailTemplate, senderName));
        } catch (Exception e) {
            logger.error("用户：{}发送邮件发生异常", username, e);
        }
    }

    @Override
    public void sendRegisterTemplateMail(List<String> emailAddressesList, String subject, String templateName, String senderName, String email, String code) {
        try {
            //根据templateName获取邮件模版
            Context context = new Context();
            context.setVariable("email", email);  // 邮箱
            context.setVariable("code", code); // 验证码
            String emailTemplate = templateEngine.process(templateName, context);
            emailAddressesList.forEach(e -> sendHtmlMail(e, subject, emailTemplate, senderName));
        } catch (Exception e) {
            logger.error("用户：{}发送注册邮件发生异常", email, e);
        }
    }

    public void sendHtmlMail(String to, String subject, String content, String senderName) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            mimeMessageHelper.setFrom(senderName+"<"+from+">");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content,true);
            mailSender.send(mimeMessage);
            logger.info("发送html邮件发送成功");
        } catch (MessagingException e) {
            logger.error("发送html邮件时发生异常！", e);
        }
    }
}
