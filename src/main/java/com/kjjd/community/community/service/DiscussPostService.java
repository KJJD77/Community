package com.kjjd.community.community.service;

import com.kjjd.community.community.dao.DiscussPostMapper;
import com.kjjd.community.community.dao.UserMapper;
import com.kjjd.community.community.entity.DiscussPost;
import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private UserMapper userMapper;
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit)
    {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId)
    {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int insertDiscussPost(DiscussPost discussPost)
    {
        if (discussPost == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }
    public DiscussPost selectDiscussPostById(int id)
    {
        return discussPostMapper.selectDiscussPostById(id);
    }



}
