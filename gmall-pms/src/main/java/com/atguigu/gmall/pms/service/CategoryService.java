package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.CategorySonVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:44:18
 */

public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);

    List<CategoryEntity> queryCategory(Integer level, Long parentCid);

    List<CategorySonVo> queryCategoryByPid(Integer pid);
}

