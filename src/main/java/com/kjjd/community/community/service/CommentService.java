package com.kjjd.community.community.service;

import com.kjjd.community.community.dao.CommentMapper;
import com.kjjd.community.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    @Autowired
    CommentMapper commentMapper;
    public List<Comment> selectCommentByEntity(int entityId,int entityType,int offset,int limit)
    {
        return commentMapper.selectCommentByEntity(entityId,entityType,offset,limit);
    }

    public int selectCountByEntity(int entityType,int entityId)
    {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

}
