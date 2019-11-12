package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;

import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.vo.*;
import feign.SmsFegin;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;
import vo.SkuSaleVo;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private ProductAttrValueDao productAttrValueDao;

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuSaleAttrValueDao skuSaleAttrValueDao;

    @Autowired
    private SmsFegin smsFegin;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySkuInfoByCatId(Long catId, QueryCondition condition) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();

        if(catId != 0){
            wrapper.eq("catalog_id",catId);
        }

        String key = condition.getKey();
        if(!StringUtils.isEmpty(key)){
            wrapper.and(k -> k.eq("id",key).or().like("spu_name",key));
        }

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(condition),
                wrapper
        );

        return new PageVo(page);
    }



    @Override
    @GlobalTransactional
    public void bigSave(SaveInfoVo saveInfoVo) {
        //1.spu相关3张表
        //1.1spuInfo表
        Long spuId = saveSpuInfo(saveInfoVo);
        //1.2spuInfoDesc表

        saveSpuDesc(saveInfoVo, spuId);

        //1.3productAttrValue表
        saveBaseAttr(saveInfoVo, spuId);


        //2.sku相关的3张表
        //2.1skuInfo表
        saveSku(saveInfoVo, spuId);

        sendMsg(spuId,"insert");

    }

    public void sendMsg(Long spuId,String type) {
        Map<String, Object> map = new HashMap<>();
        map.put("id",spuId);
        map.put("type",type);
        this.amqpTemplate.convertAndSend("GMALL-PMS-EXCHANGE","item.insert",map);
    }


    public void saveSku(SaveInfoVo saveInfoVo, Long spuId) {
        saveInfoVo.getSkus().forEach(sku->{
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(sku,skuInfoEntity);
            skuInfoEntity.setSpuId(spuId);
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setCatalogId(saveInfoVo.getCatalogId());
            skuInfoEntity.setBrandId(saveInfoVo.getBrandId());
            List<String> images = sku.getImages();
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(skuInfoEntity.getSkuDefaultImg()==null ? images.get(0) : skuInfoEntity.getSkuDefaultImg());
            }
            this.skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //2.2skuSalsAttrValue
            sku.getSaleAttrs().forEach(saleAttr ->{
                SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                skuSaleAttrValueEntity.setAttrId(saleAttr.getAttrId());
                skuSaleAttrValueEntity.setAttrValue(saleAttr.getAttrValue());
                skuSaleAttrValueEntity.setSkuId(skuId);
                skuSaleAttrValueEntity.setAttrSort(1);
                this.skuSaleAttrValueDao.insert(skuSaleAttrValueEntity);
            });
            //2.3SkuImage
            if(!CollectionUtils.isEmpty(images)){
                List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(images.get(0), image) ? 1 : 0);
                    skuImagesEntity.setImgSort(1);
                    skuImagesEntity.setImgUrl(image);
                    return skuImagesEntity;
                }).collect(Collectors.toList());
                this.skuImagesService.saveBatch(skuImagesEntities);
            }

            //3.营销相关的3张表
            //3.1SkuBounds表
            //3.2SkuFullreduction表
            //3.3SkuLadder表
            SkuSaleVo skuSaleVo = new SkuSaleVo();
            BeanUtils.copyProperties(sku,skuSaleVo);
            skuSaleVo.setSkuId(skuId);

            smsFegin.save(skuSaleVo);

        });
    }

    public void saveBaseAttr(SaveInfoVo saveInfoVo, Long spuId) {
        List<ProductAttrValueVo> baseAttrs = saveInfoVo.getBaseAttrs();
        baseAttrs.forEach( baseAttr-> {
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(baseAttr,productAttrValueEntity);
            productAttrValueEntity.setSpuId(spuId);
            productAttrValueEntity.setAttrSort(1);
            productAttrValueEntity.setQuickShow(1);
            this.productAttrValueDao.insert(productAttrValueEntity);
        });
    }

    public void saveSpuDesc(SaveInfoVo saveInfoVo, Long spuId) {
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuId);
        spuInfoDescEntity.setDecript(StringUtils.join(saveInfoVo.getSpuImages(),","));
        this.spuInfoDescDao.insert(spuInfoDescEntity);
    }

    public Long saveSpuInfo(SaveInfoVo saveInfoVo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(saveInfoVo,spuInfoEntity);
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUodateTime(spuInfoEntity.getCreateTime());
        this.save(spuInfoEntity);

        return spuInfoEntity.getId();
    }


}