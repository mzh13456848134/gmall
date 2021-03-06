package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员
 *
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:49:36
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);

    Boolean checkData(String data, Integer type);

    String register(MemberEntity memberEntity, String code);

    MemberEntity queryUser(String username, String password);

}

