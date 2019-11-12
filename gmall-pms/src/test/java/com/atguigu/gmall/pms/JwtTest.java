package com.atguigu.gmall.pms;


import com.atguigu.core.utils.JwtUtils;
import com.atguigu.core.utils.RsaUtils;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class JwtTest {
   /* private static final String pubKeyPath = "E:\\temp\\rsa\\rsa.pub";

    private static final String priKeyPath = "E:\\temp\\rsa\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    *//**
     * 使用Rsa工具类，产生公钥私钥
     * @throws Exception
     *//*

    @Test
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    *//**
     * 根据公钥私钥Path路径来拿到公钥私钥对象
     * @throws Exception
     *//*
    @Before
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    *//**
     *根据私钥来产出token
     * @throws Exception
     *//*
    @Test
    public void testGenerateToken() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("id", "11");
        map.put("username", "liuyan");
        // 生成token
        String token = JwtUtils.generateToken(map, privateKey, 5);
        System.out.println("token = " + token);
    }

    *//**
     * 拿到token来通过公钥验签，解析，获取到token中的信息
     * @throws Exception
     *//*

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjExIiwidXNlcm5hbWUiOiJsaXV5YW4iLCJleHAiOjE1NzM1NDc2MTB9.JU4wjRFVRJ_BMNv7vxAO2np1LiTdxNTyh5IQORsaw5tjtJZl12dKJl6YX2DIXMOkRtRoztYkjQoAtomQeTsu8rcJq0P9PamxT5Kf-ry2mxvA0e9XFdbH7QLkX6odBPO2GswgbLblwY8VGtLi6m71oOjRHnSEsyHnNug-jfAN_H-a_Rb_Ass3ukCICHBvtgSbE8FLBBie9G-DaS4b1Y2KXv1SBKJr9IE6LeTbQ3Ywsw8umqk8-NrGKASLiJJKv9xytjAYqfjGctaPlEMeSeKis_yBHnj4FFIVxBg5FF5Pl8ViLsSa4KsYuEPfc6eYbDCNxGw972Xj2P73v6ClcLIWXw";        // 解析token
        Map<String, Object> map = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + map.get("id"));
        System.out.println("userName: " + map.get("username"));
    }*/
}