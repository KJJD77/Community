package com.kjjd.community.community;

import com.sun.jdi.VoidValue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTests {
    @Autowired
    RedisTemplate redisTemplate;
    @Test
    public void testString()
    {
        redisTemplate.opsForValue().set("test:name","kjjd");
        redisTemplate.opsForValue().set("test:count",2);
        redisTemplate.opsForValue().increment("test:count");
        System.out.println(redisTemplate.opsForValue().get("test:name"));
        System.out.println(redisTemplate.opsForValue().get("test:count"));
    }
    @Test
    public void testHash()
    {
        String key="test:hash";
        redisTemplate.opsForHash().put(key,"id",1);
        redisTemplate.opsForHash().put(key,"name","jjd");

        System.out.println(redisTemplate.opsForHash().get(key,"id"));
        System.out.println(redisTemplate.opsForHash().get(key,"name"));
    }
    @Test
    public void testSet()
    {
        String key="test:set";
        redisTemplate.opsForSet().add(key,"2c1");
        redisTemplate.opsForSet().add(key,"kjjd");
        redisTemplate.opsForSet().add(key,"kcz");
        System.out.println(redisTemplate.opsForSet().size(key));
        System.out.println(redisTemplate.opsForSet().members(key));
        System.out.println(redisTemplate.opsForSet().pop(key));
        System.out.println(redisTemplate.opsForSet().members(key));
    }
    @Test
    public void testList()
    {
        String key="test:list";
        redisTemplate.opsForList().leftPush(key,101);
        redisTemplate.opsForList().leftPush(key,102);
        redisTemplate.opsForList().leftPush(key,103);
        System.out.println(redisTemplate.opsForList().size(key));
        System.out.println(redisTemplate.opsForList().index(key,0));;
        System.out.println(redisTemplate.opsForList().range(key,0,2));

        System.out.println(redisTemplate.opsForList().leftPop(key));
        System.out.println(redisTemplate.opsForList().leftPop(key));
        System.out.println(redisTemplate.opsForList().leftPop(key));
    }

    @Test
    public void testSortedSet()
    {
        String key="test:zset";
        redisTemplate.opsForZSet().add(key,"kjjd",100);
        redisTemplate.opsForZSet().add(key,"2c1",90);
        redisTemplate.opsForZSet().add(key,"kcz",80);
        redisTemplate.opsForZSet().add(key,"kc0",70);
        redisTemplate.opsForZSet().add(key,"zcl",60);

        System.out.println(redisTemplate.opsForZSet().size(key));

        System.out.println(redisTemplate.opsForZSet().zCard(key));
        System.out.println( redisTemplate.opsForZSet().rank(key,"kjjd"));
        System.out.println(redisTemplate.opsForZSet().reverseRange(key,0,2));
    }
    @Test
    public void testKeys() {
        redisTemplate.delete("test:hash");
        System.out.println(redisTemplate.hasKey("test:hash"));
        redisTemplate.expire("test:students", 10, TimeUnit.SECONDS);
    }
    @Test
    public void testBoundOperations(){
        BoundValueOperations operations = redisTemplate.boundValueOps("test:count");
        operations.set(0);
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    @Test
    public void testTransaction() {
        Object result = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String redisKey = "test:tx";

                operations.multi();
                operations.opsForSet().add(redisKey, "zhangsan");
                operations.opsForSet().add(redisKey, "lisi");
                operations.opsForSet().add(redisKey, "wangwu");

                System.out.println(operations.opsForSet().members(redisKey));
                return operations.exec();
            }
        });
        System.out.println(result);
    }
}
