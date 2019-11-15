package com.atguigu.gmallorder.feign;

import feign.SmsFegin;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-server")
public interface GmallSmsFeign  extends SmsFegin {
}
