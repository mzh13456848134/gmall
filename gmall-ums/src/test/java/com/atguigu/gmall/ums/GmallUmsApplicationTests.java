package com.atguigu.gmall.ums;

import com.atguigu.gmall.ums.feign.GmallAliSmsClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GmallUmsApplicationTests {
    @Autowired
    private GmallAliSmsClient gmallAliSmsClient;
    @Test
    void contextLoads() {
        System.out.println(gmallAliSmsClient);
    }

}
