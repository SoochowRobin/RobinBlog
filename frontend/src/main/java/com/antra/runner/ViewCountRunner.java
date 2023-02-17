package com.antra.runner;

import com.antra.domain.entity.Article;
import com.antra.mapper.ArticleMapper;
import com.antra.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ViewCountRunner implements CommandLineRunner {

    private ArticleMapper articleMapper;

    private RedisCache redisCache;

    @Autowired
    public ViewCountRunner(ArticleMapper articleMapper, RedisCache redisCache) {
        this.articleMapper = articleMapper;
        this.redisCache = redisCache;
    }

    // 实现CommandLinerRunner接口
    @Override
    public void run(String... args) throws Exception {

        // 查询博客信息 id viewCount
        // 不需要任何条件，拿出所有的信息
        List<Article> articleList = articleMapper.selectList(null);
        // 处理数据，变成map结构存入Redis
        Map<String, Integer> viewCountMap = articleList.stream()
                .collect(Collectors.toMap(article -> article.getId().toString(), article -> article.getViewCount().intValue()));

        // 存储到Redis中
        redisCache.setCacheMap("articleViewCount", viewCountMap);

    }
}
