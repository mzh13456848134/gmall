package com.atguigu.gmall.pms.fegin;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.SkuSaleVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("sms-server")
public interface SmsFegin {
    @PostMapping("sms/skubounds/save")
    public Resp<Object> save(@RequestBody SkuSaleVo skuSaleVo);
}
