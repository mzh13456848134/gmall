package com.atguigu.gmallorder.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmallorder.service.OrderService;
import com.atguigu.gmallorder.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("confirm/{userId}")
    public Resp<OrderConfirmVo> confirm(@PathVariable("userId")Long userId){
        OrderConfirmVo orderConfirmVo = this.orderService.confirm(userId);
        return Resp.ok(orderConfirmVo);
    }
}
