package com.atguigu.gmallorder.service;

import com.atguigu.gmallorder.vo.OrderConfirmVo;

public interface OrderService {
    OrderConfirmVo confirm(Long userId);
}
