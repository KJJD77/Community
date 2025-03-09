package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.CommunityUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    UserService userService;
    @RequestMapping(path="/register",method = RequestMethod.GET)
    public String getRegisterPage()
    {
        return "/site/register";
    }
    @RequestMapping(path="/login",method = RequestMethod.GET)
    public String getLoginPage() {return "/site/login";}
    @RequestMapping(path="/register",method = RequestMethod.POST)
    public String register(Model model, User user)
    {
        Map<String,Object>map= userService.register(user);
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功,我们已经向您的邮箱发送了一封激活邮件,请尽快激活!");
            model.addAttribute("target", "/index");
            return "/site/operate-result";
        } else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }
    // http://localhost:8080/community/activation/101/code
    @RequestMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code")String code)
    {
        int result =userService.activation(userId,code);
        if(result==ACTIVATION_SUCCESS)
        {
            model.addAttribute("msg", "激活成功,您的账号已经可以正常使用了!");
            model.addAttribute("target", "/login");
        }
        else if(result==ACTIVATION_REPEAT)
        {
            model.addAttribute("msg", "无效操作,该账号已经激活过了!");
            model.addAttribute("target", "/index");
        }
        else
        {
            model.addAttribute("msg", "激活失败,您提供的激活码不正确!");
            model.addAttribute("target", "/index");
        }
        return "/site/operate-result";
    }



}
