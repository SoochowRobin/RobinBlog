package com.antra.service.impl;

import com.antra.constant.SystemConstants;
import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Article;
import com.antra.domain.entity.Category;
import com.antra.domain.vo.CategoryVO;
import com.antra.mapper.CategoryMapper;
import com.antra.service.ArticleService;
import com.antra.service.CategoryService;
import com.antra.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2023-02-10 22:01:03
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    // 在这边使用articleservice: 在一个service里面调用其他的service
    private ArticleService articleService;

    @Autowired
    public CategoryServiceImpl(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public ResponseResult getCategoryList() {

        // 分步查询:
        // 1.在文章表里面查询
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL); // 用定义的常量
        List<Article> articleList = articleService.list(articleWrapper);

        // 2.拿到文章后获取分类id,然后去重
        Set<Long> categoryIds = articleList.stream()
                .map(article -> article.getCategoryId())
                .collect(Collectors.toSet());

        // 3. 查询分类表
        // 用category的方法
        List<Category> categories = listByIds(categoryIds);

        categories = categories.stream()
                               .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                               .collect(Collectors.toList());

        // 4. 封装VO
        List<CategoryVO> categoryVOS = BeanCopyUtils.copyBeanList(categories, CategoryVO.class);
        // 5. 返回
        return ResponseResult.okResult(categoryVOS);
    }
}

