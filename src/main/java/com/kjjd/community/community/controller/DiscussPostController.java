package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.DiscussPost;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.DiscussPostService;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path="/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content)
    {
        User user=hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJSONString(403, "你还没有登录哦!");
        }
        DiscussPost discussPost=new DiscussPost();
        discussPost.setContent(content);
        discussPost.setTitle(title);
        discussPost.setCreateTime(new Date());
        discussPost.setUserId(user.getId());
        discussPostService.insertDiscussPost(discussPost);

        return CommunityUtil.getJSONString(0,"成功发布");
    }

}
