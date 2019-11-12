package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategorySonVo;

import java.util.List;

public interface IndexService {
    List<CategoryEntity> queryCvategory1();

    List<CategorySonVo> queryCategorySonByPid(Integer pid);

    String test();

    String testRead();

    String testWrite();

    String latch() throws InterruptedException;

    String out();
}
