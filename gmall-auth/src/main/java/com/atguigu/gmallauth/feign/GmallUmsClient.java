package com.atguigu.gmallauth.feign;

import com.atguigu.gmall.ums.api.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("ums-server")
public interface GmallUmsClient extends GmallUmsApi {
}
