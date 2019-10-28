package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:47:37
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
