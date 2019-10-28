package com.atguigu.gmall.oms.dao;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:42:09
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
