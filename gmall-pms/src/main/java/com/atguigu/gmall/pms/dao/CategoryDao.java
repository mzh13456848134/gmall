package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategorySonVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:44:18
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {

    List<CategorySonVo> queryCategoryByPid(Integer pid);
}
