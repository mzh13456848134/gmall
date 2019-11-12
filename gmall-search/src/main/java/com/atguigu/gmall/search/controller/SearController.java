package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.service.SearchService;
import com.atguigu.gmall.search.vo.SearchParamVO;
import com.atguigu.gmall.search.vo.SearchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearController {

    @Autowired
    private SearchService searchService;
    @GetMapping
    public Resp<SearchResponse> search(SearchParamVO searchParamVO){
        SearchResponse search = this.searchService.search(searchParamVO);
        return Resp.ok(search);
    }
}
