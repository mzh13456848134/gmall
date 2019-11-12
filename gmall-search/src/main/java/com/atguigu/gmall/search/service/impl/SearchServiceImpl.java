package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.GoodsVo;
import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;
import com.atguigu.gmall.search.vo.SearchResponseAttrVO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.CardinalityAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private JestClient jestClient;

    @Override
    public SearchResponse search(SearchParamVO searchParamVO){

        try {
            //构建查询条件
            String query =  builderDslQuery(searchParamVO);

            Search search = new Search.Builder(query).addIndex("goods").addType("info").build();
            //执行搜索，获取搜索结果集
            SearchResult searchResult = jestClient.execute(search);

            //解析dsl查询结果
            SearchResponse response = parseResult(searchResult);

            response.setPageSize(searchParamVO.getPageSize());
            response.setPageNum(searchParamVO.getPageNum());
            response.setTotal(searchResult.getTotal());

            return response;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    private SearchResponse parseResult(SearchResult result) {

        //获取所有聚合
        SearchResponse response = new SearchResponse();
        MetricAggregation aggregations = result.getAggregations();
        //解析品牌的聚合结果集
        //获取品牌的聚合
        TermsAggregation brandAgg = aggregations.getTermsAggregation("brandAgg");
        //获取品牌聚合中的所有桶
        List<TermsAggregation.Entry> buckets = brandAgg.getBuckets();
        //判断品牌聚合是否为空
        if(!CollectionUtils.isEmpty(buckets)){
            //初始化品牌vo对象
            SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
            attrVO.setName("品牌");
            List<String> brandValues = buckets.stream().map(bucket ->{
                HashMap<String, Object> map = new HashMap<>();
                map.put("id",bucket.getKeyAsString());
                map.put("name",bucket.getTermsAggregation("brandNameAgg").getBuckets().get(0).getKeyAsString());
                return JSON.toJSONString(map);
            }).collect(Collectors.toList());
            attrVO.setValue(brandValues);
            response.setBrand(attrVO);
        }


        //解析分类的集合结果集
        TermsAggregation categoryAgg = aggregations.getTermsAggregation("categoryAgg");

        List<TermsAggregation.Entry> categroyBuckets = categoryAgg.getBuckets();
        if(!CollectionUtils.isEmpty(categroyBuckets)){
            SearchResponseAttrVO cateVo = new SearchResponseAttrVO();
            List<String> cateValue = categroyBuckets.stream().map(bucket ->{
                HashMap<String, Object> map = new HashMap<>();
                map.put("id",bucket.getKeyAsString());
                map.put("name",bucket.getTermsAggregation("categroyNameAgg").getBuckets().get(0).getKeyAsString());
                return JSON.toJSONString(map);
            }).collect(Collectors.toList());

            cateVo.setName("分类");
            cateVo.setValue(cateValue);
            response.setCatelog(cateVo);
        }



        //解析搜索属性的聚合结果集
        CardinalityAggregation attrAgg = aggregations.getCardinalityAggregation("attrAgg");
        TermsAggregation attrIdAgg = attrAgg.getTermsAggregation("attrIdAgg");
        List<TermsAggregation.Entry> attrBuckets = attrIdAgg.getBuckets();
        if(!CollectionUtils.isEmpty(attrBuckets)) {
            List<SearchResponseAttrVO> attrVos = attrBuckets.stream().map(bucket -> {
                SearchResponseAttrVO attrVO = new SearchResponseAttrVO();
                attrVO.setProductAttributeId(Long.valueOf(bucket.getKeyAsString()));
                //获取搜索属性的子聚合(搜索属性名)
                TermsAggregation attrNameAgg = bucket.getTermsAggregation("attrNameAgg");
                attrVO.setName(attrNameAgg.getBuckets().get(0).getKeyAsString());
                //获取搜索属性的子聚合(搜索属性值)

                TermsAggregation attrValueAgg = bucket.getTermsAggregation("attrValueAgg");
                List<String> values = attrValueAgg.getBuckets().stream().map(bucket1 -> bucket1.getKeyAsString()).collect(Collectors.toList());
                attrVO.setValue(values);
                return attrVO;
            }).collect(Collectors.toList());
            response.setAttrs(attrVos);
        }


        //解析商品列表的结果集

        List<GoodsVo> goodsVos = result.getSourceAsObjectList(GoodsVo.class, false);
        response.setProducts(goodsVos);

        return response;
    }


    private String builderDslQuery(SearchParamVO searchParamVO) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();


        //1.构建查询和过滤条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //构建查询条件
        if(StringUtils.isNotBlank(searchParamVO.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("name",searchParamVO.getKeyword()).operator(Operator.AND));
            searchSourceBuilder.query(boolQueryBuilder);
        }


        //构建过滤条件
        //品牌
        String[] brands = searchParamVO.getBrand();
        if(ArrayUtils.isNotEmpty(brands)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParamVO.getBrand()));
        }
        //分类
        String[] catelog3 = searchParamVO.getCatelog3();
        if(ArrayUtils.isNotEmpty(catelog3)){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("productCategoryId",catelog3));
        }

        //搜索的规格属性过滤
        String[] props = searchParamVO.getProps();
        if(ArrayUtils.isNotEmpty(props)){
            for (String prop : props) {
                String[] attr = StringUtils.split(prop, ":");
                if(attr != null && attr.length == 2){
                    BoolQueryBuilder propBoolBuilder = QueryBuilders.boolQuery();
                    propBoolBuilder.must(QueryBuilders.termQuery("attrValueList.productAttributeId",attr[0]));
                    String[] attrValues = StringUtils.split(attr[1], "-");
                    propBoolBuilder.must(QueryBuilders.termsQuery("attrValueList.value",attrValues));
                    boolQueryBuilder.filter(QueryBuilders.nestedQuery("attrValueList",propBoolBuilder, ScoreMode.None));
                }
            }
        }

        searchSourceBuilder.query(boolQueryBuilder);

        //2.完成分页的构建
        Integer pageNum = searchParamVO.getPageNum();
        Integer pageSize = searchParamVO.getPageSize();
        searchSourceBuilder.from((pageNum - 1) * pageSize);
        searchSourceBuilder.size(pageSize);


        //3.排序
        String order = searchParamVO.getOrder();
        if(StringUtils.isNotBlank(order)){
            String[] split = StringUtils.split(order, ":");

            if(split != null & split.length == 2){
                SortOrder sortOrder = StringUtils.equals("asc", split[1]) ? SortOrder.ASC : SortOrder.DESC;
                switch (split[0]){
                    case "0": searchSourceBuilder.sort("_score",sortOrder);break;
                    case "1":searchSourceBuilder.sort("sale",sortOrder); break;
                    case "2":searchSourceBuilder.sort("price",sortOrder); break;
                    default:break;
                }
            }

        }

        //4.高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        searchSourceBuilder.highlighter(highlightBuilder);


        //5.完成聚合条件的构建
        //品牌
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("brandAgg").field("brandId")
                        .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));
        //分类
        searchSourceBuilder.aggregation(
                AggregationBuilders.terms("categoryAgg").field("productCategoryId")
                    .subAggregation(AggregationBuilders.terms("categroyNameAgg").field("productCategoryName")));
        //搜索属性
        searchSourceBuilder.aggregation(
                AggregationBuilders.nested("attrAgg","attrValueList")
                    .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrValueList.productAttributeId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrValueList.name"))
                        .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrValueList.value"))
                    )
        );


        return searchSourceBuilder.toString();
    }
}
