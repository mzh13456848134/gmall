package com.atguigu.gmallcart.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.cart.vo.CartItemVo;
import com.atguigu.gmallcart.interceptor.LoginInterceptor;
import com.atguigu.gmallcart.service.CartService;
import com.atguigu.gmallcart.vo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("cart")
public class CartController {


    @Autowired
    private CartService cartService;


    @GetMapping("test")
    public Resp<Object> test(){
        return Resp.ok(LoginInterceptor.get());
    }

    @PostMapping
    public Resp<Object> addCart(@RequestBody Cart cart){
        this.cartService.addCart(cart);
        return Resp.ok(null);
    }

    @GetMapping
    public Resp<List<Cart>> queryCarts(){
        List<Cart> carts = this.cartService.queryCarts();
        return Resp.ok(carts);
    }

    @PostMapping("update")
    public Resp<Object> updateCart(@RequestBody Cart cart){
        this.cartService.updateCart(cart);
        return Resp.ok(null);
    }

    @PostMapping("{skuId}")
    public Resp<Object> deleteCart(@PathVariable("skuId")Long skuId){
        this.cartService.deleteCart(skuId);
        return Resp.ok(null);
    }

    @PostMapping("check")
    public Resp<Object> checkCart(@RequestBody List<Cart> carts){
        this.cartService.checkCart(carts);
        return Resp.ok(null);
    }

    @GetMapping("order/{userId}")
    public Resp<List<CartItemVo>> queryCheckSkuInfo(@PathVariable("userId")Long userId){
        List<CartItemVo> cartItemVos = this.cartService.queryCheckSkuInfo(userId);
        return Resp.ok(cartItemVos);

    }



}
