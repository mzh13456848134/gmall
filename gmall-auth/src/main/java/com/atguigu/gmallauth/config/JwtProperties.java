package com.atguigu.gmallauth.config;

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
    private String secret ; //加盐

    private String pubKeyPath; //公钥路径

    private String priKeyPath; //私钥路径

    private Integer expire;//token过期时间

    private PublicKey publicKey; //公钥对象

    private PrivateKey privateKey;//私钥对象

    private String cookieName; //cookie名称

    @PostConstruct
    public void init(){
        try{
            File pubKey = new File(pubKeyPath);
            File priKey = new File(priKeyPath);

            if(!pubKey.exists() || !priKey.exists()){
                RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
            }

            publicKey = RsaUtils.getPublicKey(pubKeyPath);
            privateKey = RsaUtils.getPrivateKey(priKeyPath);
        }catch (Exception e){
            log.error("初始化公钥和私钥失败",e);

        }

    }
}
