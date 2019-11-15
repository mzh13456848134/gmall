package com.atguigu.gmall.wms.dao;

import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:52:07
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<WareSkuEntity> queryWareSkuBySkuId(@Param("skuId") Long skuId, @Param("count") Integer count);

    Integer LockStore(@Param("id") Long id,@Param("count") Integer count);

    Integer unlock(@Param("id") Long id,@Param("count") Integer count);
}
