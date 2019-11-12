package com.atguigu.gmallauth.controller;


import com.atguigu.core.bean.Resp;
import com.atguigu.gmallauth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("auth")
public class AuthController {


    @Autowired
    private AuthService authService;
    @PostMapping("accredit")
    public Resp<Object> accredit(@RequestParam("username") String username, @RequestParam("password")String password, HttpServletRequest request, HttpServletResponse response){
        String token = this.authService.accredit(username,password,request,response);

        return Resp.ok(token);
    }

}
