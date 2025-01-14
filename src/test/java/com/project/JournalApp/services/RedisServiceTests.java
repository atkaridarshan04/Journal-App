package com.project.JournalApp.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
public class RedisServiceTests {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisConnection(){
        redisTemplate.opsForValue().set("test", "test@yahoo.com");
        Object test = redisTemplate.opsForValue().get("test");
        System.out.println(test);
    }
}
