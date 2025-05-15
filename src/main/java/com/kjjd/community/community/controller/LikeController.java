package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.Event;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.event.EventProducer;
import com.kjjd.community.community.service.LikeService;
import com.kjjd.community.community.util.CommunityConstant;
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
public class LikeController implements CommunityConstant {
    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityId, int entityType, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        Map<String, Object> map = new HashMap<>();
        Long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);
        int entityLikeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        map.put("likeCount", entityLikeCount);
        map.put("likeStatus", entityLikeStatus);

        //触发点赞事件
        if (entityLikeStatus == 1) {
            Event event = new Event()
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setData("postId", postId);
            eventProducer.send(event);
        }

        return CommunityUtil.getJSONString(0, null, map);
    }
}
