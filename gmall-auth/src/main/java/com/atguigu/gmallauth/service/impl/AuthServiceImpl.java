package com.atguigu.gmallauth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.exception.GmallException;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmallauth.config.JwtProperties;
import com.atguigu.gmallauth.feign.GmallUmsClient;
import com.atguigu.gmallauth.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Service
@EnableConfigurationProperties(value = {JwtProperties.class})
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;

    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public String accredit(String username, String password, HttpServletRequest request, HttpServletResponse response) {

        try {
            //1.远程调用用户中心的数据接口，查询用户信息
            Resp<MemberEntity> memberEntityResp = this.gmallUmsClient.queryUser(username, password);
            MemberEntity memberEntity = memberEntityResp.getData();

            //2.判断用户是否存在，不存在直接返回
            if(memberEntity == null){
                return null;
            }

            //3.存在生成jwt
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("id", memberEntity.getId());
            map.put("username", memberEntity.getUsername());
            String token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());


            if(StringUtils.isEmpty(token)){
                return "认证失败";
            }
            //4把生成的jwt放入cookie中
            CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getExpire() * 60);
            return "认证成功";
        } catch (Exception e) {
            e.printStackTrace();
            throw  new GmallException("jwt认证失败失败！！");
        }

    }
}
