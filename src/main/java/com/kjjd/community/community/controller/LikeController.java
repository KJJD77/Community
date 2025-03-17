package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.service.LikeService;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path="/like",method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityId,int entityType,int entityUserId)
    {
        User user=hostHolder.getUser();
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        Map<String,Object>map=new HashMap<>();
        Long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        map.put("likeCount",entityLikeCount);
        map.put("likeStatus",entityLikeStatus);

        return CommunityUtil.getJSONString(0,null,map);
    }


}
