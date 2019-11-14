package com.atguigu.gmallcart.interceptor;

import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmallcart.config.JwtProperties;
import com.atguigu.gmallcart.vo.UserInfoVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@Component
@EnableConfigurationProperties(value = {JwtProperties.class})
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private static final ThreadLocal<UserInfoVO> THREAD_LOCAL = new ThreadLocal();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //threadlocal中的载荷信息
        UserInfoVO userInfoVO = new UserInfoVO();
        //获取cookie信息(GMALL_TOKEN,userKey)
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        String userkey = CookieUtils.getCookieValue(request, this.jwtProperties.getUserKey());


        //如果没有临时用户信息，直接创建一个新的临时用户信息
        if(StringUtils.isEmpty(userkey)){
            userkey = UUID.randomUUID().toString();
            CookieUtils.setCookie(request,response,this.jwtProperties.getUserKey(),userkey,jwtProperties.getExpire());
        }
        //把临时用户信息设置到对象中
        userInfoVO.setUserKey(userkey);


        //如果没有登录信息
        if(StringUtils.isEmpty(token)){
            THREAD_LOCAL.set(userInfoVO);
            return true;
        }

        try {
            //解析gmall_token
            Map<String, Object> userInfoMap = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());

            userInfoVO.setUserId(Long.valueOf(userInfoMap.get("id").toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        THREAD_LOCAL.set(userInfoVO);

        return true;
    }

    public static UserInfoVO get(){
        return THREAD_LOCAL.get();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        THREAD_LOCAL.remove();
    }
}
