package com.kjjd.community.community.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class testAuth{
    @RequestMapping("/test-auth")
    @ResponseBody
    public String testAuth() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "未登录";
        }
        return "当前用户权限: " + authentication.getAuthorities();
    }

}

