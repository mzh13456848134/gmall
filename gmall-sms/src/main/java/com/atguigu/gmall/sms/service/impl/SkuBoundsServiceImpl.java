package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import vo.ItemSaleVo;
import vo.SkuSaleVo;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {
    
    @Autowired
    private SkuFullReductionDao skuFullReductionDao;

    @Autowired
    private SkuLadderDao skuLadderDao;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }

    @Override
    @Transactional
    public void bigSave(SkuSaleVo skuSaleVo) {
        //3.营销相关的3张表
        //3.1SkuBounds表
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setSkuId(skuSaleVo.getSkuId());
        skuBoundsEntity.setBuyBounds(skuSaleVo.getBuyBounds());
        skuBoundsEntity.setGrowBounds(skuSaleVo.getBuyBounds());
        Integer newWork;
        List<Integer> works = skuSaleVo.getWork();
        newWork = works.get(0) * 1 + works.get(1) * 2 + works.get(2) * 4 + works.get(3) * 8;
        skuBoundsEntity.setWork(newWork);
        this.save(skuBoundsEntity);

        //3.2SkuFullreduction表
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        skuFullReductionEntity.setSkuId(skuSaleVo.getSkuId());
        skuFullReductionEntity.setAddOther(skuSaleVo.getFullAddOther());
        skuFullReductionEntity.setFullPrice(skuSaleVo.getFullPrice());
        skuFullReductionEntity.setReducePrice(skuSaleVo.getReducePrice());

        this.skuFullReductionDao.insert(skuFullReductionEntity);

        //3.3SkuLadder表
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuSaleVo.getSkuId());
        skuLadderEntity.setAddOther(skuSaleVo.getLadderAddOther());
        skuLadderEntity.setDiscount(skuSaleVo.getDiscount());
        skuLadderEntity.setFullCount(skuSaleVo.getFullCount());
        skuLadderEntity.setPrice(new BigDecimal(67890));

        this.skuLadderDao.insert(skuLadderEntity);
    }

    @Override
    public List<ItemSaleVo> queryItemSaleVos(Long skuId) {
        List<ItemSaleVo> itemSaleVoList  = new ArrayList<>();
        //查询积分信息
        List<SkuBoundsEntity> skuBoundsEntities = this.list(new QueryWrapper<SkuBoundsEntity>().eq("sku_id", skuId));
        if(!CollectionUtils.isEmpty(skuBoundsEntities))
        {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("积分");
            BigDecimal buyBounds = skuBoundsEntities.get(0).getBuyBounds();
            BigDecimal growBounds = skuBoundsEntities.get(0).getGrowBounds();
            itemSaleVo.setDesc("购物积分赠送" + buyBounds.intValue() + "成长积分赠送" + growBounds.intValue());
            itemSaleVoList.add(itemSaleVo);
        }
        //查询满减信息
        List<SkuFullReductionEntity> skuFullReductionEntities = this.skuFullReductionDao.selectList(new QueryWrapper<SkuFullReductionEntity>().eq("sku_id", skuId));
        if(!CollectionUtils.isEmpty(skuFullReductionEntities))
        {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("满减");
            BigDecimal fullPrice = skuFullReductionEntities.get(0).getFullPrice();
            BigDecimal reducePrice = skuFullReductionEntities.get(0).getReducePrice();
            itemSaleVo.setDesc("满" + fullPrice.intValue() + "减" + reducePrice.intValue());
            itemSaleVoList.add(itemSaleVo);
        }
        //查询打折信息
        List<SkuLadderEntity> skuLadderEntities = this.skuLadderDao.selectList(new QueryWrapper<SkuLadderEntity>().eq("sku_id", skuId));
        if(!CollectionUtils.isEmpty(skuLadderEntities))
        {
            ItemSaleVo itemSaleVo = new ItemSaleVo();
            itemSaleVo.setType("打折");
            Integer fullCount = skuLadderEntities.get(0).getFullCount();
            BigDecimal discount = skuLadderEntities.get(0).getDiscount();
            itemSaleVo.setDesc("满" + fullCount + "件，打" + discount.divide(new BigDecimal(10)).floatValue()+"折");
            itemSaleVoList.add(itemSaleVo);
        }
        return itemSaleVoList;
    }

}