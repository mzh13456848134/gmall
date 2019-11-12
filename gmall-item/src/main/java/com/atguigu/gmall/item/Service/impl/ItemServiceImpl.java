package com.atguigu.gmall.item.Service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.GroupVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import com.atguigu.gmall.wms.entity.api.GmallWmsApi;
import com.atguigu.gmall.item.Service.ItemService;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.vo.ItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vo.ItemSaleVo;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GmallWmsApi gmallWmsApi;

    @Autowired
    private GmallSmsClient gmallSmsClient;
    
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public ItemVo item(Long skuId) {
        ItemVo itemVo = new ItemVo();

        CompletableFuture<SkuInfoEntity> skuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            //1，查询sku信息
            Resp<SkuInfoEntity> skuInfoEntityResp = this.gmallPmsClient.querySkuById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoEntityResp.getData();
            BeanUtils.copyProperties(skuInfoEntity, itemVo);
            return skuInfoEntity;
        },threadPoolExecutor);


        CompletableFuture<Void> brandCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //2.品牌
            Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandById(skuInfoEntity.getBrandId());
            itemVo.setBrand(brandEntityResp.getData());

        }, threadPoolExecutor);


        CompletableFuture<Void> categoryCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //3.分类
            Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCategroyById(skuInfoEntity.getCatalogId());
            itemVo.setCategory(categoryEntityResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> spuInfoCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //4.spu信息
            Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsClient.querySpuById(skuInfoEntity.getSpuId());
            itemVo.setSpuInfo(spuInfoEntityResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> picsCompletableFuture = CompletableFuture.runAsync(() -> {
            //5设置图片信息
            Resp<List<String>> pics = this.gmallPmsClient.queryPicsBySkuid(skuId);
            itemVo.setPics(pics.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> SaleCompletableFuture = CompletableFuture.runAsync(() -> {
            //6营销信息
            Resp<List<ItemSaleVo>> saleResp = this.gmallSmsClient.queryItemSaleVos(skuId);
            itemVo.setSales(saleResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> storCompletableFuture = CompletableFuture.runAsync(() -> {
            //7是否有货
            Resp<List<WareSkuEntity>> wareResp = this.gmallWmsApi.queryWareSkuBySkuId(skuId);
            boolean b = wareResp.getData().stream().anyMatch(ware -> ware.getStock() > 0);
            itemVo.setStore(b);
        }, threadPoolExecutor);


        CompletableFuture<Void> SaleInfoCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //8.所有销售属性
            Resp<List<SkuSaleAttrValueEntity>> skuSaleResp = this.gmallPmsClient.querySaleAttrValues(skuInfoEntity.getSpuId());
            itemVo.setSkuSale(skuSaleResp.getData());
        }, threadPoolExecutor);

        CompletableFuture<Void> spuDescCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //9spu的描述信息
            Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsClient.querySpuDescById(skuInfoEntity.getSpuId());
            itemVo.setDesc(spuInfoDescEntityResp.getData());
        }, threadPoolExecutor);


        CompletableFuture<Void> groupAttrCompletableFuture = skuInfoCompletableFuture.thenAcceptAsync(skuInfoEntity -> {
            //10规格属性及分组和规格属性的和值
            Resp<List<GroupVo>> listResp = this.gmallPmsClient.queryGroupVOByCid(skuInfoEntity.getCatalogId(), skuInfoEntity.getSpuId());
            itemVo.setGroups(listResp.getData());
        }, threadPoolExecutor);


        try {
            CompletableFuture.allOf(skuInfoCompletableFuture,brandCompletableFuture,categoryCompletableFuture,spuInfoCompletableFuture
                                    ,picsCompletableFuture,SaleCompletableFuture,storCompletableFuture,SaleInfoCompletableFuture
                                    ,spuDescCompletableFuture,groupAttrCompletableFuture).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemVo;
    }
}
