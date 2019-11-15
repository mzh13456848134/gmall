package com.atguigu.gmallorder.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderSubmitVo {
    private MemberReceiveAddressEntity addresses;//收货地址

    private Integer payType; //支付方式

    private String deliveyCompany; //物流公司

    private List<OrderItemVo> orderItemVos; //订单商品详情

    private Integer useIntegration;//下单时使用的积分

    private BigDecimal totalPrice;//总价，用于验证价格

    private String orderToken;//防重，也可以做订单编号
}
