package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.CategorySonVo;
import com.atguigu.gmall.pms.vo.GroupVo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface GmallPmsApi {

    @GetMapping("pms/attrgroup/item/group/{cid}/{spuId}")
    public Resp<List<GroupVo>> queryGroupVOByCid(@PathVariable("cid")Long cid, @PathVariable("spuId") Long spuId);

    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> querySpuDescById(@PathVariable("spuId") Long spuId);
    /**
     * 根据spuId查询sku销售属性的信息
     * @param spuId
     * @return
     */
    @GetMapping("pms/skusaleattrvalue/{spuId}")
    public Resp<List<SkuSaleAttrValueEntity>> querySaleAttrValues(@PathVariable("spuId") Long spuId);
    /**
     * 根据skuid查询图片信息
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuinfo/{skuId}")
    public Resp<List<String>> queryPicsBySkuid(@PathVariable("skuId") Long skuId);
    /**
     * 根据spuId查询Spu信息
     * @param id
     * @return
     */
    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuById(@PathVariable("id") Long id);

    /**
     * 根据skuId查询sku信息
     * @param skuId
     * @return
     */
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuById(@PathVariable("skuId") Long skuId);

    /**
     * 查询spu分页信息
     * @param queryCondition
     * @return
     */
    @PostMapping("pms/spuinfo/save/list")
    public Resp<PageVo> querylist(@RequestBody QueryCondition queryCondition) ;

    /**
     *  根据spuId查询sku信息
     * @param spuId
     * @return
     */
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuInfoBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据brandId查询品牌信息
     * @param brandId
     * @return
     */
    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandById(@PathVariable("brandId") Long brandId);

    /**
     * 根据catelogId查询分类信息
     * @param catId
     * @return
     */
    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCategroyById(@PathVariable("catId") Long catId);


    /**
     * 查询一级菜单
     * @param level
     * @param parentCid
     * @return
     */

    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategory(@RequestParam(value="level", defaultValue = "0")Integer level
            , @RequestParam(value="parentCid", required = false)Long parentCid);

    /**
     * 查询二级菜单及其子菜单
     * @param pid
     * @return
     */
    @GetMapping("pms/category/cates")
    public Resp<List<CategorySonVo>> queryCategoryByPid(@RequestParam("pid")Integer pid);

    /**
     * 根据spuId查询索引信息
     * @param spuId
     * @return
     */
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> querySearchAttrValue(@PathVariable("spuId") Long spuId);

}
