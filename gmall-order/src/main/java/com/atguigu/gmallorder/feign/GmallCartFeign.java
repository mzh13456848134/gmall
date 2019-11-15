package com.atguigu.gmallorder.feign;

import com.atguigu.gmall.cart.api.GmallCartApi;
import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cart-server")
public interface GmallCartFeign extends GmallCartApi {
}
