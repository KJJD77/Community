package com.kjjd.community.community;

import com.kjjd.community.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {
    @Autowired
    MailClient mailClient;
    @Test
    public void testTextMail()
    {
        mailClient.sendMail("1350063100@qq.com","Test","hi");
    }
    @Autowired
    TemplateEngine templateEngine;
    @Test
    public void testHtmlMail()
    {
        Context context=new Context();
        context.setVariable("username","2c1");
        String content=templateEngine.process("/mail/demo",context);
        System.out.println(content);
        mailClient.sendMail("1350063100@qq.com","HTML",content);
    }

}
