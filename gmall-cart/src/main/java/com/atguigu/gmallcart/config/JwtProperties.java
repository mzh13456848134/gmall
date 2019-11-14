package com.atguigu.gmallcart.config;

import com.atguigu.core.utils.RsaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;

@Data
@Slf4j
@ConfigurationProperties(prefix = "gmall.jwt")
public class JwtProperties {

    private String pubKeyPath; //公钥路径


    private PublicKey publicKey; //公钥对象

    private String cookieName; //cookie名称

    private String userKey; //零时用户信息


    private Integer expire; //过期时间
    @PostConstruct
    public void init(){
        try{

            publicKey = RsaUtils.getPublicKey(pubKeyPath);
        }catch (Exception e){
            log.error("初始化公钥和私钥失败",e);

        }

    }
}
