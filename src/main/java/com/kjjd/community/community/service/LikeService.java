package com.kjjd.community.community.service;

import com.kjjd.community.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.swing.plaf.PanelUI;

@Service
public class LikeService {
    @Autowired
    RedisTemplate redisTemplate;
    public void like(int userId,int entityType,int entityId,int entityUserId)
    {
//                if(redisTemplate.opsForSet().isMember(entityKey,userId))
//        {
//            redisTemplate.opsForSet().remove(entityKey,userId);
//        }
//        else
//        {
//            redisTemplate.opsForSet().add(entityKey,userId);
//        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                boolean isMember=redisTemplate.opsForSet().isMember(entityKey,userId);

                operations.multi();
                if(isMember)
                {
                    operations.opsForSet().remove(entityKey,userId);
                    operations.opsForValue().decrement(userKey);
                }
                else
                {
                    operations.opsForSet().add(entityKey,userId);
                    operations.opsForValue().increment(userKey);
                }
                return operations.exec();
            }
        });

    }
    // 查询某实体点赞的数量
    public Long findEntityLikeCount(int entityType,int entityId)
    {
        String entityKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return  redisTemplate.opsForSet().size(entityKey);
    }
    // 查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId, int entityType, int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(entityLikeKey,userId)?1:0;
    }
    public int findUserLikeCount(int userId)
    {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count=(Integer) redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }

}
