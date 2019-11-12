package com.atguigu.gmall.pms.controller;

import java.util.Arrays;
import java.util.List;


import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.vo.AttrVo;
import com.atguigu.gmall.pms.vo.GroupVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.gmall.pms.service.AttrGroupService;




/**
 * 属性分组
 *
 * @author miaozhonghui
 * @email 1104452564@qq.com
 * @date 2019-10-28 20:44:18
 */
@Api(tags = "属性分组 管理")
@RestController
@RequestMapping("pms/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;


    @GetMapping("item/group/{cid}/{spuId}")
    public Resp<List<GroupVo>> queryGroupVOByCid(@PathVariable("cid")Long cid,@PathVariable("spuId") Long spuId){
        List<GroupVo> groupVos =  this.attrGroupService.queryGroupVOByCid(cid,spuId);
        return Resp.ok(groupVos);
    }

    //http://127.0.0.1:8888/pms/attrgroup/withattrs/cat/225
    @ApiOperation("查询分类下的组及规格参数")
    @GetMapping("withattrs/cat/{catId}")
    public Resp<List<AttrVo>> queryByCatId(@PathVariable("catId") Long catId){
        List<AttrVo> groupCatVos =  this.attrGroupService.queryByCatId(catId);
        return Resp.ok(groupCatVos);
    }



    //http://127.0.0.1:8888/pms/attrgroup/withattr/1

    @GetMapping("withattr/{gid}")
    public Resp<AttrVo> queryAttrAndAttrgroupByGid(@PathVariable("gid")Long gid){
        AttrVo attrVo  =  this.attrGroupService.queryAttrAndAttrgroupByGid(gid);
        return Resp.ok(attrVo);
    }



    //http://127.0.0.1:8888/pms/attrgroup/225?t=1572581765477&limit=10&page=1
    @ApiOperation("查询三级分类的分组")
    @GetMapping("{catId}")
    public Resp<PageVo> queryAttrGroupBycatId(QueryCondition condition,@PathVariable(value = "catId",required = false) Long catId){
        PageVo pageVo = this.attrGroupService.queryAttrGroupBycatId(condition,catId);
        return Resp.ok(pageVo);
    }
    /**
     * 列表
     */
    @ApiOperation("分页查询(排序)")
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('pms:attrgroup:list')")
    public Resp<PageVo> list(QueryCondition queryCondition) {
        PageVo page = attrGroupService.queryPage(queryCondition);

        return Resp.ok(page);
    }


    /**
     * 信息
     */
    @ApiOperation("详情查询")
    @GetMapping("/info/{attrGroupId}")
    @PreAuthorize("hasAuthority('pms:attrgroup:info')")
    public Resp<AttrGroupEntity> info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return Resp.ok(attrGroup);
    }

    /**
     * 保存
     */
    @ApiOperation("保存")
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('pms:attrgroup:save')")
    public Resp<Object> save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 修改
     */
    @ApiOperation("修改")
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('pms:attrgroup:update')")
    public Resp<Object> update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return Resp.ok(null);
    }

    /**
     * 删除
     */
    @ApiOperation("删除")
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('pms:attrgroup:delete')")
    public Resp<Object> delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return Resp.ok(null);
    }

}
