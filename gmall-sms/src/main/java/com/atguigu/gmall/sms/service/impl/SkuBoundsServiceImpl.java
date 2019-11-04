package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

}