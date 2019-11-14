package com.atguigu.gmall.alisms;

import com.atguigu.core.bean.Resp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface GmallAlismsAip {
    @GetMapping("alisms/send")
    public Resp<Object> sendSms(@RequestParam("phoneNum") String phoneNum);
}
