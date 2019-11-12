package feign;

import com.atguigu.core.bean.Resp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import vo.ItemSaleVo;
import vo.SkuSaleVo;

import java.util.List;


public interface SmsFegin {

    @GetMapping("sms/skubounds/item/sales/{skuId}")
    public Resp<List<ItemSaleVo>> queryItemSaleVos(@PathVariable("skuId") Long skuId);

    @PostMapping("sms/skubounds/save")
    public Resp<Object> save(@RequestBody SkuSaleVo skuSaleVo);
}
