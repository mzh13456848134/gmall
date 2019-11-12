package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.AttrAttrgroupRelationDao;
import com.atguigu.gmall.pms.dao.AttrDao;
import com.atguigu.gmall.pms.dao.ProductAttrValueDao;
import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.vo.AttrVo;
import com.atguigu.gmall.pms.vo.GroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.dao.AttrGroupDao;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;
import org.springframework.util.CollectionUtils;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Autowired
    private  AttrDao attrDao;

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo queryAttrGroupBycatId(QueryCondition condition, Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();

        if(catId != null){
            wrapper.eq("catelog_id",catId);
        }

        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }

    @Override
    public AttrVo queryAttrAndAttrgroupByGid(Long gid) {
        AttrVo attrVo = new AttrVo();
        //查询分组表
        AttrGroupEntity attrGroupEntity = this.getById(gid);
        BeanUtils.copyProperties(attrGroupEntity,attrVo);

        //查询分组关联表

        QueryWrapper<AttrAttrgroupRelationEntity> wrapper = new QueryWrapper<>();


        wrapper.eq("attr_group_id",gid);
        List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntities = this.attrAttrgroupRelationDao.selectList(wrapper);
        attrVo.setRelations(attrAttrgroupRelationEntities);
        List<Long> attrIds = attrAttrgroupRelationEntities.stream().map(relation -> relation.getAttrId()).collect(Collectors.toList());


        if(CollectionUtils.isEmpty(attrIds)){
            return attrVo;
        }

        //查询分组的属性表
        QueryWrapper<AttrEntity> attWrapper = new QueryWrapper<>();
        attWrapper.in("attr_id",attrIds);
        List<AttrEntity> attrEntities = this.attrDao.selectList(attWrapper);
        attrVo.setAttrEntities(attrEntities);

        return attrVo;
    }

    @Override
    public List<AttrVo> queryByCatId(Long catId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("catelog_id",catId);
        List<AttrGroupEntity> attrGroupEntities = this.list(wrapper);

        if(!CollectionUtils.isEmpty(attrGroupEntities)){
            List<AttrVo> attrVos = attrGroupEntities.stream().map(attrGroupEntity -> {
                return this.queryAttrAndAttrgroupByGid(attrGroupEntity.getAttrGroupId());
            }).collect(Collectors.toList());
            return attrVos;
        }
        return null;
    }

    @Override
    public List<GroupVo> queryGroupVOByCid(Long cid, Long spuId) {
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", cid));
        if(CollectionUtils.isEmpty(attrGroupEntities)){
            return null;
        }
        List<GroupVo> groupVos = attrGroupEntities.stream().map(attrGroupEntity -> {
            GroupVo groupVo = new GroupVo();
            groupVo.setGrupName(attrGroupEntity.getAttrGroupName());
            List<ProductAttrValueEntity> productAttrValueEntities = this.productAttrValueDao.queryByGidAndSpuId(spuId, cid);
            groupVo.setBaseAttr(productAttrValueEntities);
            return groupVo;
        }).collect(Collectors.toList());
        return groupVos;
    }

}