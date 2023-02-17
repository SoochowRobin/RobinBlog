package com.antra.service.impl;

import com.antra.constant.SystemConstants;
import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Article;
import com.antra.domain.entity.Category;
import com.antra.domain.vo.ArticleDetailVO;
import com.antra.domain.vo.ArticleListVO;
import com.antra.domain.vo.HotArticleVO;
import com.antra.domain.vo.PageVO;
import com.antra.mapper.ArticleMapper;
import com.antra.service.ArticleService;
import com.antra.service.CategoryService;
import com.antra.utils.BeanCopyUtils;
import com.antra.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private CategoryService categoryService;

    private final RedisCache redisCache;

    // use lazy injection to avoid a cycle formation
    @Autowired
    @Lazy
    public ArticleServiceImpl(CategoryService categoryService, RedisCache redisCache) {
        this.categoryService = categoryService;
        this.redisCache = redisCache;
    }

    // 查询热门文章： 1. 不能查询出来草稿， 2.降序排列 3. 删除的文章不能查询出来

    @Override
    public ResponseResult hotArticleList() {
        // 查询热门文章，封装成 ResponseResult 返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        // 必须是正式文章
        // 使用系统常量来标注
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        // 按照浏览量进行排序
        // 按照viewCount进行排序,降序
        queryWrapper.orderByDesc(Article::getViewCount);
        Page<Article> page = new Page<>(1, 10);
        page(page, queryWrapper);

        // 这边有个问题就是article字段里面有很多的数据， 有些数据是很敏感的
        // 不应该返回给前端（查询的时候可以用实体类封装）
        List<Article> records = page.getRecords();

        /**
         // 封装成VO
         // bean拷贝
         List<HotArticleVO> articleVOS = new ArrayList<>();
         for(Article article: records){
         HotArticleVO vo = new HotArticleVO();
         // 用 Bean 拷贝
         BeanUtils.copyProperties(article, vo);
         articleVOS.add(vo);
         }


         // 用ResponseResult的静态方法，来返回结果
         return ResponseResult.okResult(records);
         */


        // 改进后
        List<HotArticleVO> articleVOS = BeanCopyUtils.copyBeanList(records, HotArticleVO.class);
        return ResponseResult.okResult(articleVOS);
    }


    @Override
    public ResponseResult artileList(Integer pageNum, Integer pageSize, Long categoryId) {
        //  查询条件：
        LambdaQueryWrapper<Article> lambdaWrapper = new LambdaQueryWrapper<>();
        // 如果有categoryId 就要 查询时要和传入的相同
        /*
        eq() 第一个参数是 boolean类型， 如果true就执行后面的语句，否则就不执行
         */
        lambdaWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        // 状态是正式发布的
        lambdaWrapper.eq(Article::getStatus, SystemConstants.STATUS_NORMAL);
        // 对isTop 进行降序
        lambdaWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, lambdaWrapper);

        // 查询categoryName
        List<Article> articles = page.getRecords();

        /* for 循环的形式来处理
        // 拿articleId 去查询articleName
        for (Article article : articles) {
            Category category = categoryService.getById(article.getCategoryId());
            article.setCategoryName(category.getName());
        }

        *
         */

        /*
            Stream流形式来处理
            如果stream 中只有一个返回值 return 可以省略
         */

        articles.stream()
                // 获取分类id, 查询分类信息，获取分类名称
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());


        // 封装查询结果
        List<ArticleListVO> articleListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVO.class);


        PageVO pageVO = new PageVO(articleListVOS, page.getTotal());
        return ResponseResult.okResult(pageVO);
    }


    /*
    更改从 Redis中获取viewCount
     */

    @Override
    public ResponseResult getArticleDetail(Long id) {
        // 根据id 查询文章
        Article article = getById(id);

        // 从redis当中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue("articleViewCount", id.toString());
        article.setViewCount(Long.valueOf(viewCount));

        // 转换成VO
        ArticleDetailVO articleDetailVO = BeanCopyUtils.copyBean(article, ArticleDetailVO.class);
        //根据分类id 查询分类名
        Long categoryId = articleDetailVO.getCategoryId();
        Category category = categoryService.getById(categoryId);
        // avoid NPE
        if(category!=null){
            articleDetailVO.setCategoryName(category.getName());
        }

        return ResponseResult.okResult(articleDetailVO);
    }



    @Override
    public ResponseResult updateViewCount(Long id) {

        // 更新Redis中文章的浏览量
        // TODO: 这边要定义常量 不用"articleViewCount"
        redisCache.incrementCacheMapValue("articleViewCount", id.toString(), 1);
        return ResponseResult.okResult();
    }
}
