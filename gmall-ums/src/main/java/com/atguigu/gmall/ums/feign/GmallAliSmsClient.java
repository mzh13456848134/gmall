package com.atguigu.gmall.ums.feign;

import com.atguigu.gmall.alisms.api.GmallAlismsAip;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("alisms-server")
public interface GmallAliSmsClient extends GmallAlismsAip {
}
