package com.atguigu.gmallalisms.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmallalisms.service.AliSmsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("alisms")
public class AliSmsController {

    @Autowired
    private AliSmsService aliSmsService;
    @GetMapping("send")
    public Resp<Object> sendSms(@RequestParam("phoneNum") String phoneNum){
        if(StringUtils.isEmpty(phoneNum)){
            return Resp.fail("请输入手机号");
        }
        String s = this.aliSmsService.sendSms(phoneNum);
        return Resp.ok(s);
    }
}
