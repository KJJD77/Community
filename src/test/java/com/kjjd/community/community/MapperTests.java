package com.kjjd.community.community;

import com.kjjd.community.community.dao.DiscussPostMapper;
import com.kjjd.community.community.dao.UserMapper;
import com.kjjd.community.community.entity.DiscussPost;
import com.kjjd.community.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Dictionary;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTests {
    @Autowired
    UserMapper userMapper;

    @Test
    public void testSelectUser() {
        System.out.println(userMapper.selectById(13));

        System.out.println(userMapper.selectByName("aaa"));

        System.out.println(userMapper.selectByEmail("nowcoder121@sina.com"));
    }

    @Test
    public void testUpdateUser()
    {
        userMapper.updateHeader(150,"http://www.nowcoder.com/102.png");

        userMapper.updateStatus(150,1);

        userMapper.updatePassword(150,"654321");
    }

    @Test
    public void testInsertUser()
    {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());
        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());
    }

    @Autowired
    DiscussPostMapper discussPostMapper;
    @Test
    public void testDisscussPost()
    {
        int cnt = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(cnt);
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149,0,10);
        for(DiscussPost dis:discussPosts)
            System.out.println(dis);

    }

}
