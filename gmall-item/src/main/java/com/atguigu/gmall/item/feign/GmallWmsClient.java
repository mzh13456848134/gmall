package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.wms.entity.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("wms-server")
public interface GmallWmsClient extends GmallWmsApi {
}
