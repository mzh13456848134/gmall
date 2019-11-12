package com.atguigu.gmallauth.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    String accredit(String username, String password, HttpServletRequest request, HttpServletResponse response);
}
