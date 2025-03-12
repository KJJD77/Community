package com.kjjd.community.community.controller;

import com.kjjd.community.community.annotation.LoginRequired;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.HostHolder;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.annotation.Retention;

@Controller
@RequestMapping(path ="/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @LoginRequired
    @RequestMapping(path="/setting",method = RequestMethod.GET)
    public String setting()
    {
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path="/upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model)
    {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }
        String originalFilename = headerImage.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if(StringUtils.isBlank(substring))
        {
            model.addAttribute("error", "文件的格式不正确!");
            return "/site/setting";
        }
        String filename= CommunityUtil.generateUUID() + substring;
        File file=new File(uploadPath+"/"+filename);
        try {
            headerImage.transferTo(file);
        }
        catch (Exception e)
        {
            logger.error("上传文件失败: " + e.getMessage());
            throw new RuntimeException("上传文件失败,服务器发生异常!", e);
        }
        // 更新当前用户的头像的路径(web访问路径)
        // http://localhost:8080/community/user/header/xxx.png
        String headerUrl=domain+contextPath+"/user/header/"+filename;
        User user= hostHolder.getUser();
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    @RequestMapping(path ="/header/{fileName}",method =RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response)
    {
        fileName=uploadPath+"/"+fileName;
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        response.setContentType("image/"+suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @LoginRequired
    @RequestMapping(path="/changePassword",method = RequestMethod.POST)
    public String changePassword(String oldPassword ,String newPasswordFirst,String newPasswordSecond, Model model)
    {
        if (oldPassword == null||newPasswordFirst == null||newPasswordSecond == null) {
            model.addAttribute("passwordError", "密码为空!");
            return "/site/setting";
        }
        if (!newPasswordFirst.equals(newPasswordSecond)) {
            model.addAttribute("passwordError", "重复密码不一致!");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        if(!CommunityUtil.md5(oldPassword+user.getSalt()).equals(user.getPassword()))
        {
            model.addAttribute("passwordError", "密码错误!");
            return "/site/setting";
        }
        String password=CommunityUtil.md5(newPasswordFirst+user.getSalt());
        userService.updatePassword(user.getId(),password);
        return "redirect:/index";
    }
}
