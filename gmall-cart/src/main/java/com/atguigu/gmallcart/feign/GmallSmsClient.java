package com.atguigu.gmallcart.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import feign.SmsFegin;
import org.springframework.cloud.openfeign.FeignClient;


@FeignClient("sms-server")
public interface GmallSmsClient extends SmsFegin {
}
