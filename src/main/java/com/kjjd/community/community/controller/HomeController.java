package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.DiscussPost;
import com.kjjd.community.community.entity.Page;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.DiscussPostService;
import com.kjjd.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;

    @RequestMapping(path="/index", method= RequestMethod.GET)
    public String getIndexPage(Model model, Page page)
    {
        System.out.println("Current Page: " + page.getCurrent());
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts=new ArrayList<>();
        for(DiscussPost discussPost:list)
        {
            Map<String,Object> map=new HashMap<>();
            User user= userService.findUserById(discussPost.getUserId());
            map.put("post",discussPost);
            map.put("user",user);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
        return "/index";
    }

}
