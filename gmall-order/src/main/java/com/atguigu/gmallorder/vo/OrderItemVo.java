package com.atguigu.gmallorder.vo;

import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import lombok.Data;
import vo.ItemSaleVo;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderItemVo {

    private Long skuId; //skuid

    private String title; //sku标题

    private String defaultImage; //sku默认图片

    private BigDecimal price; //商品价格

    private Integer count; //商品数量

    private Boolean store; //商品库存

    private List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities; //商品的销售属性

    private List<ItemSaleVo> sales; //商品的营销信息

    private BigDecimal weight; //商品重量

}
