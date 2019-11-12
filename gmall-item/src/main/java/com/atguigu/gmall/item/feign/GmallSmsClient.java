package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.wms.entity.api.GmallWmsApi;
import feign.SmsFegin;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-server")
public interface GmallSmsClient extends SmsFegin {
}
