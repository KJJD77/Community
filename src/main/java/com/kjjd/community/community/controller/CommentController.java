package com.kjjd.community.community.controller;

import com.kjjd.community.community.entity.*;
import com.kjjd.community.community.event.EventProducer;
import com.kjjd.community.community.service.CommentService;
import com.kjjd.community.community.service.DiscussPostService;
import com.kjjd.community.community.service.MessageService;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired
    HostHolder hostHolder;
    @Autowired
    CommentService commentService;
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    EventProducer eventProducer;
    @RequestMapping(path="/add/{discussPostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId")int discussPostId, Comment comment)
    {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        discussPostService.addComment(comment);

        //触发事件
        Event event = new Event().setEntityId(comment.getEntityId()).setEntityType(comment.getEntityType()).setUserId(hostHolder.getUser().getId()).setTopic(TOPIC_COMMENT).setData("postId", discussPostId);
        if(comment.getEntityType() == ENTITY_TYPE_POST)
        {
            DiscussPost discussPost = discussPostService.selectDiscussPostById(comment.getEntityId());
            event.setEntityUserId(discussPost.getUserId());
        }
        else if(comment.getEntityType() == ENTITY_TYPE_COMMENT)
        {
            Comment target = commentService.selectCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        eventProducer.send(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event = new Event()
                    .setTopic(TOPIC_PUBLISH)
                    .setUserId(comment.getUserId())
                    .setEntityType(ENTITY_TYPE_POST)
                    .setEntityId(discussPostId);
            eventProducer.send(event);
        }


        return "redirect:/discuss/detail/" + discussPostId;
    }

}
