package com.atguigu.gmall.wms.entity.vo;

import lombok.Data;

@Data
public class SkuLock {
    private Long skuId;
    private Integer count;
    private Boolean lock; //锁定成功true,锁定失败false
    private Long skuWareId;//锁定库存的id
}
