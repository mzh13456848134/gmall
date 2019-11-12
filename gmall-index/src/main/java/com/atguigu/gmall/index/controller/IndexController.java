package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategorySonVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    private IndexService indexService;

    //http://localhost:2000/api/index/cates
    //Request Method: GET
    @ApiOperation("查询一级菜单")
    @GetMapping("cates")
    public Resp<List<CategoryEntity>> queryCategory1(){
        List<CategoryEntity>  categoryEntities =  this.indexService.queryCvategory1();
        return Resp.ok(categoryEntities);
    }

    // http://localhost:2000/api/index/cates/2
    //Request Method: GET
    @ApiOperation("查询一级菜单下的子菜单")
    @GetMapping("cates/{pid}")
    public Resp<List<CategorySonVo>> queryCategorySonByPid(@PathVariable("pid")Integer pid){
        List<CategorySonVo> categorySonVos =  this.indexService.queryCategorySonByPid(pid);
        return Resp.ok(categorySonVos);
    }

    @GetMapping("test")
    public Resp<Object> test(HttpServletRequest httpServletRequest){
        System.out.println(httpServletRequest.getLocalPort());
        String  s = this.indexService.test();
        return Resp.ok(s);
    }


    @GetMapping("testread")
    public Resp<Object> testRead(){
        String msg = this.indexService.testRead();
        return Resp.ok(msg);
    }
    @GetMapping("testwrite")
    public Resp<Object> testWrite(){
        String msg = this.indexService.testWrite();
        return Resp.ok(msg);
    }

    @GetMapping("latch")
    public Resp<Object> latch() throws InterruptedException {
        String msg = this.indexService.latch();
        return Resp.ok(msg);
    }

    @GetMapping("out")
    public Resp<Object> out(){
        String msg = this.indexService.out();
        return Resp.ok(msg);
    }
}
