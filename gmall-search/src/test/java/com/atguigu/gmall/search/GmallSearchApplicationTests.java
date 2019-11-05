package com.atguigu.gmall.search;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.SpuAttributeValueVO;
import com.atguigu.gmall.search.feign.GmallPmsFegin;
import com.atguigu.gmall.search.feign.GmallWmsFegin;
import com.atguigu.gmall.search.vo.GoodsVo;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired 
    JestClient jestClient;
    
    @Autowired
    private GmallPmsFegin gmallPmsFegin;
    
    @Autowired
    private GmallWmsFegin gmallWmsFegin;
    @Test
    public void importData(){
        Long pageNum = 1L;
        Long pageSize = 100L;
        do{
            //分页查询已上架商品，即spu中publish_status = 1的商品
            QueryCondition queryCondition = new QueryCondition();
            queryCondition.setPage(pageNum);
            queryCondition.setLimit(pageSize);
            Resp<PageVo> pageVoResp = this.gmallPmsFegin.querylist(queryCondition);
            PageVo pageVo = pageVoResp.getData();
            //判断pageVo是否为null
            if(pageVo.getList() == null){
                pageSize = 0L;
                continue;
            }

            //获取当前页的spuInfo数据
            //List<SpuInfoEntity> spuInfoEntities = (List<SpuInfoEntity>) pageVo.getList();
            //解决pageVo没有空构造的原因，到时类型转换异常，直接将集合转换成json,再将json转换成对象
            String s = JSON.toJSONString(pageVo.getList());
            List<SpuInfoEntity> spuInfoEntities = JSON.parseArray(s, SpuInfoEntity.class);

            //遍历spu获取spu下的所有sku导入的所有库中
            for (SpuInfoEntity spuInfoEntity : spuInfoEntities) {
                Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsFegin.querySkuInfoBySpuId(spuInfoEntity.getId());
                List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
                if(CollectionUtils.isEmpty(skuInfoEntities)){
                    continue;
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
                    Resp<List<ProductAttrValueEntity>> searchAttrValueResp = this.gmallPmsFegin.querySearchAttrValue(spuInfoEntity.getId());
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
            pageNum++;
        }while (pageNum == 100);
    }



    @Test
    void println(){
        //Resp<BrandEntity> brandEntityResp = this.gmallPmsFegin.queryBrandById(null);

    }












   /* @Autowired
    JestClient jestClient;

    *//**
     * 有该记录就更新，没有则新增(以id为判断标准)
     * 会把没有设置值的字段更新为null
     * @throws IOException
     *//*
    @Test
    void contextLoads() throws IOException {

        Index index = new Index.Builder(new User("zhang3","123456",18)).index("user").type("index").id("1").build();
        jestClient.execute(index);
    }

    *//**
     * 更新(仅仅更新不为null的字段)
     *//*
    @Test
    void update() throws IOException {
        User user = new User("mzh", null, null);
        Map<String,Object> map =  new HashMap<>();
        map.put("doc",user);
        Update update = new Update.Builder(map).index("user").type("index").id("1").build();

        DocumentResult result = jestClient.execute(update);
        System.out.println(result);
    }

    @Test
    void select() throws IOException {
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"match_all\": {}\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(query).addIndex("user").addType("index").build();
        SearchResult execute = jestClient.execute(search);
        System.out.println(execute);

        List<User> sourceAsObjectList = execute.getSourceAsObjectList(User.class, false);
        System.out.println(sourceAsObjectList);

        execute.getHits(User.class).forEach(hit -> {
            System.out.println(hit.source);
        });
    }

    @Test
    void delete() throws IOException {
        Delete delete = new Delete.Builder("1").index("user").type("index").build();
        DocumentResult execute = jestClient.execute(delete);
        System.out.println(execute.toString());
    }*/

}
/*@Data
@AllArgsConstructor
@NoArgsConstructor
class User{
    private String name;
    private String password;
    private Integer age;
}*/
