package com.atguigu.gmall.ums.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.ums.dao.MemberDao;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.gmall.ums.service.MemberService;

import java.util.Date;
import java.util.UUID;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public Boolean checkData(String data, Integer type) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        switch (type){
            case 1:wrapper.eq("username",data);break;
            case 2:wrapper.eq("mobile",data);break;
            case 3:wrapper.eq("email",data);break;
            default:return false;
        }
        return this.count(wrapper) == 0;
    }

    @Override
    public void register(MemberEntity memberEntity, String code) {

        //验证码校验未完成待续。。。。。
        //生成盐
        String salt = StringUtils.substring(UUID.randomUUID().toString(), 0, 6);
        memberEntity.setSalt(salt);
        //对密码加密
        memberEntity.setPassword(DigestUtils.md5Hex(memberEntity.getPassword()+salt));
        //设置创建时间等信息
        memberEntity.setLevelId(1L);
        memberEntity.setStatus(1);
        memberEntity.setCreateTime(new Date());
        memberEntity.setIntegration(0);
        memberEntity.setGrowth(0);


        //添加到数据库
        this.save(memberEntity);


    }

    @Override
    public MemberEntity queryUser(String username, String password) {
        QueryWrapper<MemberEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        MemberEntity memberEntity = this.getOne(wrapper);
        if(memberEntity == null){
            throw new IllegalArgumentException("账户不合法");
        }

        boolean b = memberEntity.getPassword().equals(DigestUtils.md5Hex(password + memberEntity.getSalt()));

        if(!b){
            throw new IllegalArgumentException("密码不合法");
        }

        return memberEntity;
    }

}