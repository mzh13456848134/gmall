package com.atguigu.gmallorder.vo;

import com.atguigu.gmall.ums.entity.MemberReceiveAddressEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderConfirmVo {
    /**
     * 收货地址，user_member_receiver_address表
     */
    private List<MemberReceiveAddressEntity> addresses;

    /**
     * 购物清单，根据购物车页面传递过来的skuIds查询
     */
    private List<OrderItemVo> orderItems;

    /**
     * 可用积分，ums_memeber表中的integration字段
     */
    private Integer bounds;

    /**
     * 订单令牌，防止重复提交
     */
    private String orderToken;

}
