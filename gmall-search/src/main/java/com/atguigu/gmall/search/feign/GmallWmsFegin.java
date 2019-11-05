package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.wms.entity.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-server")
public interface GmallWmsFegin extends GmallWmsApi {
}
