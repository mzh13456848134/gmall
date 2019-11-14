package com.atguigu.gmallcart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("pms-server")
public interface GmallPmsClient extends GmallPmsApi {
}
