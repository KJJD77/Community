package com.kjjd.community.community.service;

import com.kjjd.community.community.dao.UserMapper;
import com.kjjd.community.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;
    public User findUserById(int id)
    {
        return userMapper.selectById(id);
    }

}
