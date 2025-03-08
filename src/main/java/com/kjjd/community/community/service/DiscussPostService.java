package com.kjjd.community.community.service;

import com.kjjd.community.community.dao.DiscussPostMapper;
import com.kjjd.community.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    DiscussPostMapper discussPostMapper;
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit)
    {
        return discussPostMapper.selectDiscussPosts(userId,offset,limit);
    }
    public int findDiscussPostRows(int userId)
    {
        return discussPostMapper.selectDiscussPostRows(userId);
    }


}
