package com.atguigu.gmallcart.service;

import com.atguigu.gmall.cart.vo.CartItemVo;
import com.atguigu.gmallcart.vo.Cart;

import java.util.List;

public interface CartService {
    void addCart(Cart cart);

    List<Cart> queryCarts();

    void updateCart(Cart cart);

    void deleteCart(Long skuId);

    void checkCart(List<Cart> cart);

    List<CartItemVo> queryCheckSkuInfo(Long userId);
}
