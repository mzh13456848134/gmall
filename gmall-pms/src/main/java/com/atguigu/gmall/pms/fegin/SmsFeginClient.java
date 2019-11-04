package com.atguigu.gmall.pms.fegin;

import feign.SmsFegin;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-server")
public interface SmsFeginClient extends SmsFegin {

}
