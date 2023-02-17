package com.antra.controller;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Article;
import com.antra.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    private ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /*@GetMapping
    public List<Article> test(){
        List<Article> list = articleService.list();
        return list;
    }*/

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
        // 查询热门文章，然后封装成 ResponseResult返回
        // service 层封装成 ResponseResult 对象
        return articleService.hotArticleList();
    }

    // 参数类型是query类型  ? & &
    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId){
        return articleService.artileList(pageNum, pageSize, categoryId);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable Long id){
        return articleService.updateViewCount(id);
    }


}
