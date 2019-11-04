package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SaveInfoVo;
import com.atguigu.gmall.pms.vo.SaveVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * spu信息
 *
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:44:18
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);


    PageVo querySkuInfoByCatId(Long catId, QueryCondition condition);

    void bigSave(SaveInfoVo saveInfoVo);
}

