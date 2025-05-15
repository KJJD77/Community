package com.kjjd.community.community.service;

import com.kjjd.community.community.entity.User;
import com.kjjd.community.community.util.CommunityConstant;
import com.kjjd.community.community.util.CommunityUtil;
import com.kjjd.community.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    UserService userService;
    public void follow(int userId,int entityType,int entityId)
    {
           redisTemplate.execute(new SessionCallback() {
               @Override
               public Object execute(RedisOperations operations) throws DataAccessException {

                   String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                   String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
                   operations.multi();

                   redisTemplate.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                   redisTemplate.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                   return operations.exec();
               }
           });
    }

    public void unfollow(int userId,int entityType,int entityId)
    {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType,entityId);
                operations.multi();

                redisTemplate.opsForZSet().remove(followeeKey,entityId);
                redisTemplate.opsForZSet().remove(followerKey,userId);

                return operations.exec();
            }
        });
    }
    // 查询关注的实体的数量
    public long findFolloweeCount(int userId,int entityType)
    {
        String followKey=RedisKeyUtil.getFolloweeKey(userId,entityType);
        return redisTemplate.opsForZSet().zCard(followKey);
    }
    // 查询实体的粉丝的数量
    public long findFollowerCount(int entityId,int entityType)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    // 查询当前用户是否已关注该实体
    public boolean hasFollowed(int userId,int entityId,int entityType)
    {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit)
    {
        List<Map<String,Object>>list=new ArrayList<>();
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if(set==null) {return null;}
        for(Integer id:set)
        {
            Map<String,Object>map=new HashMap<>();
            User user= userService.findUserById(id);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }

        return list;
    }
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit)
    {
        List<Map<String,Object>>list=new ArrayList<>();
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER,userId);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if(set==null) {return null;}
        for(Integer id:set)
        {
            Map<String,Object>map=new HashMap<>();
            User user= userService.findUserById(id);
            map.put("user",user);
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime",new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

}