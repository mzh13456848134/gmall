package com.atguigu.gmallalisms.service;

public interface AliSmsService {
    String sendSms(String phoneNum);

    String sendRegisterMess(String phoneNum);
}
