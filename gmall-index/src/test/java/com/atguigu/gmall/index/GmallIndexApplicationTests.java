package com.atguigu.gmall.index;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class GmallIndexApplicationTests {

    /*@Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {

        redisTemplate.opsForValue().set("pid","111");
        String pid = redisTemplate.opsForValue().get("pid");
        System.out.println(pid);
    }*/

}
