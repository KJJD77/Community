package com.kjjd.community.community.util;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class MailClient {
    @Autowired
    JavaMailSender mailSender;
    private Logger logger=LoggerFactory.getLogger(MailClient.class);

    @Value("${spring.mail.username}")
    private String from;
    //spring.mail.host=stmp.sina.com
    //spring.mail.port=456
    //spring.mail.username=kjjd77
    //spring.mail.password=Jjd040830
    //spring.mail.protocol=stmps
    //spring.mail.properties.mail.smtp.ssl.enable=true
    public void sendMail(String to,String subject,String content)
    {
        try {
            MimeMessage mimeMailMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMailMessage);
            mimeMessageHelper.setFrom(from);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(content,true);
            mailSender.send(mimeMessageHelper.getMimeMessage());
        }
        catch (Exception e)
        {
            logger.error("发送邮件失败"+e.getMessage());
        }
    }

}
