package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVo;
import lombok.Data;
import vo.ItemSaleVo;

import java.util.List;

@Data
public class ItemVo extends SkuInfoEntity {

    private BrandEntity brand; //品牌信息

    private CategoryEntity category; //分类信息

    private List<String> Pics; //图片信息

    private  List<ItemSaleVo> Sales; // 销售属性

    private  Boolean store; //是否库存

    private  List<SkuSaleAttrValueEntity>  skuSale;

    private SpuInfoDescEntity desc;

    private  List<GroupVo> groups;

    private SpuInfoEntity spuInfo; //spu信息
}
