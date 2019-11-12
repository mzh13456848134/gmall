package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.ProductAttrValueEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.atguigu.gmall.search.feign.GmallPmsFegin;
import com.atguigu.gmall.search.feign.GmallWmsFegin;
import com.atguigu.gmall.search.vo.GoodsVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemListener {
    @Autowired
    JestClient jestClient;

    @Autowired
    private GmallPmsFegin gmallPmsFegin;

    @Autowired
    private GmallWmsFegin gmallWmsFegin;

    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(value = "GMALL-SERCCH_QUEUE",durable = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            key = {"item.*"}

    ))
    public void listener(Map<String,Object> map){

        if(CollectionUtils.isEmpty(map)){
            return;
        }
        Long spuId = (Long) map.get("id");
        String type = map.get("type").toString();

        if(StringUtils.equals("insert",type) || StringUtils.equals("update",type)){

            insertOrUpdate(spuId);

        }else if(StringUtils.equals("delete",type)){

            Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsFegin.querySkuInfoBySpuId(spuId);
            List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
            if(CollectionUtils.isEmpty(skuInfoEntities)){
                return;
            }

            skuInfoEntities.forEach(skuInfoEntity -> {

                Delete delete = new Delete.Builder(skuInfoEntity.getSkuId().toString()).index("goods").type("info").build();
                try {
                    this.jestClient.execute(delete);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });


        }
    }

    public void insertOrUpdate(Long spuId) {
        Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsFegin.querySkuInfoBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
        if(CollectionUtils.isEmpty(skuInfoEntities)){
            return;
        }

        skuInfoEntities.forEach(skuInfoEntity -> {
            GoodsVo goodsVo = new GoodsVo();
            //设置sku相关的数据
            goodsVo.setName(skuInfoEntity.getSkuTitle());
            goodsVo.setId(skuInfoEntity.getSkuId());
            goodsVo.setPic(skuInfoEntity.getSkuDefaultImg());
            goodsVo.setPrice(skuInfoEntity.getPrice());
            goodsVo.setSale(100);
            goodsVo.setSort(0);

            //设置品牌相关的
            Resp<BrandEntity> brandEntityResp = this.gmallPmsFegin.queryBrandById(skuInfoEntity.getBrandId());
            BrandEntity brandEntity = brandEntityResp.getData();
            if(brandEntity != null){
                goodsVo.setBrandId(skuInfoEntity.getBrandId());
                goodsVo.setBrandName(brandEntity.getName());
            }


            //设置分类相关的
            Resp<CategoryEntity> categoryEntityResp = this.gmallPmsFegin.queryCategroyById(skuInfoEntity.getCatalogId());
            CategoryEntity categoryEntity = categoryEntityResp.getData();
            if(categoryEntity != null){
                goodsVo.setProductCategoryId(skuInfoEntity.getCatalogId());
                goodsVo.setProductCategoryName(categoryEntity.getName());
            }


            //设置搜索属性的
            Resp<List<ProductAttrValueEntity>> searchAttrValueResp = this.gmallPmsFegin.querySearchAttrValue(spuId);
            List<ProductAttrValueEntity> productAttrValueEntities = searchAttrValueResp.getData();
            if(!CollectionUtils.isEmpty(productAttrValueEntities)){
                List<SpuAttributeValueVO> spuAttributeValueVOS = productAttrValueEntities.stream().map(productAttrValueEntity -> {
                    SpuAttributeValueVO spuAttributeValueVO = new SpuAttributeValueVO();
                    spuAttributeValueVO.setName(productAttrValueEntity.getAttrName());
                    spuAttributeValueVO.setValue(productAttrValueEntity.getAttrValue());
                    spuAttributeValueVO.setProductAttributeId(productAttrValueEntity.getAttrId());
                    return spuAttributeValueVO;

                }).collect(Collectors.toList());
                goodsVo.setAttrValueList(spuAttributeValueVOS);
            }

            //设置库存
            Resp<List<WareSkuEntity>> wareResp = this.gmallWmsFegin.queryWareSkuBySkuId(skuInfoEntity.getSkuId());
            List<WareSkuEntity> wareSkuEntities = wareResp.getData();
            if(!CollectionUtils.isEmpty(wareSkuEntities)){
                long sum = wareSkuEntities.stream().mapToLong(WareSkuEntity::getSkuId).sum();
                goodsVo.setStock(sum);
            }


            Index index = new Index.Builder(goodsVo).index("goods").type("info").id(skuInfoEntity.getSkuId().toString()).build();
            try {
                jestClient.execute(index);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
