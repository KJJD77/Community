package com.kjjd.community.community;

import com.kjjd.community.community.dao.CommentMapper;
import com.kjjd.community.community.entity.Comment;
import org.apache.ibatis.annotations.Select;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommentTests {
    @Autowired
    CommentMapper commentMapper;
    @Test
    public void selectTests(){
        List<Comment> comments = commentMapper.selectCommentByEntity(228, 1, 0, 5);
        System.out.println(comments);
    }

}
