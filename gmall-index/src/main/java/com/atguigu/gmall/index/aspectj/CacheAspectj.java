package com.atguigu.gmall.index.aspectj;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.annotation.GmallCache;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import springfox.documentation.spring.web.json.Json;
import sun.reflect.generics.tree.ReturnType;

import java.lang.annotation.Annotation;
import java.security.Key;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class CacheAspectj {


    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 环绕通知满足的条件
     * 方法的返回值是Object
     * 方法的参数ProceedingJoinPoint
     * 方法丙烯抛出Throwable异常
     * 通知joinPoint.procedd（args）执行原始方法
     *
     *
     */

    @Around("@annotation(com.atguigu.gmall.index.annotation.GmallCache )")
    public Object cacheAroundAdvice(ProceedingJoinPoint joinPoint)throws  Throwable{
        //获取注解
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();//获取方法签名
        GmallCache annotation = signature.getMethod().getAnnotation(GmallCache.class);//获取方法注解对象
        Class returnType = signature.getReturnType();//获取方法的返回值
        String prefix = annotation.prefix();//获取注解属性值
        String args = Arrays.asList(joinPoint.getArgs()).toString();
        String key = prefix + args;
        //查询缓存
        Object result = this.cacheHit(key, returnType);
        if(result != null){
            return result;
        }

        //分布式锁

        RLock lock = this.redissonClient.getLock("lock" + args);
        lock.lock();


        //查询缓存
        result = this.cacheHit(key, returnType);
        //如果缓存中有，直接返回，并释放锁
        if(result != null){
            lock.unlock();
            return result;
        }


       result = joinPoint.proceed(joinPoint.getArgs());

        //放入缓存，并释放分布式锁
        long timeout = annotation.timeout();//过期时间
        long random = annotation.random();
        timeout = timeout + (long)(Math.random() *  random);
        this.redisTemplate.opsForValue().set(key,JSON.toJSONString(result),timeout, TimeUnit.SECONDS);
        lock.unlock();
        return result;
    }

    public Object cacheHit(String key,Class returnType){
        String joinString = this.redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(joinString)){
            return JSON.parseObject(joinString, returnType);
        }
        return  null;
    }

}
