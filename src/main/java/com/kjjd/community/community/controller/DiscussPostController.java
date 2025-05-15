package com.kjjd.community.community.controller;

import ch.qos.logback.core.joran.conditional.ElseAction;
import com.kjjd.community.community.dao.CommentMapper;
import com.kjjd.community.community.entity.*;
import com.kjjd.community.community.event.EventProducer;
import com.kjjd.community.community.service.CommentService;
import com.kjjd.community.community.service.DiscussPostService;
import com.kjjd.community.community.service.LikeService;
import com.kjjd.community.community.service.UserService;
import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;
    @Autowired
    EventProducer    eventProducer;
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
        Event event = new Event().setTopic(TOPIC_PUBLISH).setUserId(user.getId()).setEntityType(ENTITY_TYPE_POST).setEntityId(discussPost.getId());
        eventProducer.send(event);
        return CommunityUtil.getJSONString(0,"成功发布");
    }
    @RequestMapping(path="/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDisPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page)
    {
        DiscussPost discussPost = discussPostService.selectDiscussPostById(discussPostId);
        User user = userService.findUserById(discussPost.getUserId());
        model.addAttribute("post",discussPost);
        model.addAttribute("user",user);
        Long entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, discussPostId);
        int entityLikeStatus;
        if(hostHolder.getUser()!=null)
            entityLikeStatus = likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, discussPostId);
        else
            entityLikeStatus=0;
        model.addAttribute("likeCount", entityLikeCount);
        model.addAttribute("likeStatus", entityLikeStatus);

        //评论分页
        page.setPath("/discuss/detail/"+discussPostId);
        page.setLimit(5);
        page.setRows(discussPost.getCommentCount());

        // 评论: 给帖子的评论
        // 回复: 给评论的评论
        // 评论列表
        List<Comment>commentList=commentService.selectCommentByEntity(discussPost.getId(),
                ENTITY_TYPE_POST,page.getOffset(),page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList!=null)
        for(Comment comment:commentList)
        {
            Map<String,Object> CommentVo =new HashMap<>();
            //评论
            CommentVo.put("comment",comment);
            //作者
            CommentVo.put("user",userService.findUserById(comment.getUserId()));
            entityLikeStatus = hostHolder.getUser()==null? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
            entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, comment.getId());
            CommentVo.put("likeCount",entityLikeCount);
            CommentVo.put("likeStatus", entityLikeStatus);

            //回复列表
            List<Comment> replyList=commentService.selectCommentByEntity
                    (comment.getId(),ENTITY_TYPE_COMMENT,0,Integer.MAX_VALUE);
            List<Map<String, Object>> replyVoList = new ArrayList<>();
            if(replyList!=null)
            {
                for(Comment reply:replyList)
                {
                    Map<String,Object>replyVo =new HashMap<>();
                    replyVo.put("user",userService.findUserById((reply.getUserId())));
                    replyVo.put("reply",reply);

                    entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                    entityLikeStatus = hostHolder.getUser()==null ? 0: likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                    replyVo.put("likeCount", entityLikeCount);
                    replyVo.put("likeStatus", entityLikeStatus);
                    //回复目标
                    User target=userService.findUserById(reply.getTargetId());
                    replyVo.put("target",target);

                    replyVoList.add(replyVo);
                }
            }
            CommentVo.put("replys",replyVoList);

            //回复数量
            int replyCount = commentService.selectCountByEntity(ENTITY_TYPE_COMMENT,comment.getId());
            CommentVo.put("replyCount",replyCount);

            commentVoList.add(CommentVo);
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}
