package com.atguigu.gmall.sms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import vo.ItemSaleVo;
import vo.SkuSaleVo;

import java.util.List;


/**
 * 商品sku积分设置
 *
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:47:37
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void bigSave(SkuSaleVo skuSaleVo);

    List<ItemSaleVo> queryItemSaleVos(Long skuId);
}

