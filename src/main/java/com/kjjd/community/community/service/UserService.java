package com.kjjd.community.community.service;

import com.kjjd.community.community.dao.UserMapper;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MailClient mailClient;
    @Autowired
    private TemplateEngine templateEngine;
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;
    public User findUserById(int id)
    {
        return userMapper.selectById(id);
    }
    public Map<String,Object> register(User user)
    {
        Map<String,Object> map=new HashMap<>();
        if(user==null)
        {
            throw new IllegalArgumentException("参数不能空");
        }

        //验证参数
        if(StringUtils.isBlank(user.getUsername()))
        {
            map.put("usernameMsg","姓名不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword()))
        {
            map.put("passwordMsg","密码不能为空!");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail()))
        {
            map.put("emailMsg","邮箱不能为空!");
            return map;
        }

        //验证账号
        if(userMapper.selectByName(user.getUsername())!=null)
        {
            map.put("usernameMsg","该账号已存在!");
            return map;
        }
        if(userMapper.selectByEmail(user.getEmail())!=null)
        {
            map.put("emailMsg","该邮箱已被注册!");
            return map;
        }

        //配置账号
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setCreateTime(new Date());
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        userMapper.insertUser(user);

        //激活邮件
        Context context=new Context();
        context.setVariable("email", user.getEmail());
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);//?
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);
        return map;
    }

}
