package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.Event;
import com.kjjd.community.community.entity.Page;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.event.EventProducer;
import com.kjjd.community.community.service.FollowService;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.HostHolder;
import com.kjjd.community.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    FollowService followService;
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId)
    {
        followService.follow(hostHolder.getUser().getId(),entityType,entityId);
        //触发关注事件
        Event event=new Event().setTopic(TOPIC_FOLLOW).setEntityId(entityId).setEntityType(entityType).setEntityUserId(entityId).setUserId(hostHolder.getUser().getId());
        eventProducer.send(event);
        return CommunityUtil.getJSONString(0,"已关注!");

    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0, "已取消关注!");
    }
    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if(user==null)
        {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user",user);
        page.setPath("/followees/"+userId);
        page.setRows((int)followService.findFolloweeCount(userId,ENTITY_TYPE_USER));
        page.setLimit(5);

        List<Map<String, Object>> followees = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        for(Map<String, Object> map:followees)
        {
            User tagetUser =(User)map.get("user");
            boolean hasFollowed = hasFollowed(tagetUser.getId());
            map.put("hasFollowed", hasFollowed);
        }
        model.addAttribute("users",followees);
        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if(user==null)
        {
            throw new RuntimeException("该用户不存在!");
        }
        model.addAttribute("user",user);

        page.setPath("/followers/"+userId);
        page.setRows((int)followService.findFollowerCount(userId,ENTITY_TYPE_USER));
        page.setLimit(5);

        List<Map<String, Object>> followers = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        for(Map<String, Object> map:followers)
        {
            User tagetUser =(User)map.get("user");
            boolean hasFollowed = hasFollowed(tagetUser.getId());
            map.put("hasFollowed", hasFollowed);
        }
        model.addAttribute("users",followers);
        return "/site/follower";
    }

    private boolean hasFollowed(int userId)
    {
        if (hostHolder.getUser() == null) {
            return false;
        }
       return followService.hasFollowed(hostHolder.getUser().getId(),userId,ENTITY_TYPE_USER);
    }

}
