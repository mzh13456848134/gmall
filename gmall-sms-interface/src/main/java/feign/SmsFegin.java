package feign;

import com.atguigu.core.bean.Resp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vo.SkuSaleVo;

@FeignClient("sms-server")
public interface SmsFegin {
    @PostMapping("sms/skubounds/save")
    public Resp<Object> save(@RequestBody SkuSaleVo skuSaleVo);
}
