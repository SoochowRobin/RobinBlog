package com.antra.job;

import com.antra.domain.entity.Article;
import com.antra.service.ArticleService;
import com.antra.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UpdateViewCountJob {
    /*
    用cron表达式定时任务， 5秒钟将redis里的数据写入到mysql数据库当中
     */
    private RedisCache redisCache;

    private ArticleService articleService;

    @Autowired
    public UpdateViewCountJob(RedisCache redisCache, ArticleService articleService) {
        this.redisCache = redisCache;
        this.articleService = articleService;
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void updateViewCount(){
        // 获取redis中的浏览量
        // TODO: 最好用定义好的常量 articleViewCount
        Map<String, Integer> articleViewCount = redisCache.getCacheMap("articleViewCount");
        // 利用stream 流 拿到map的entryset， 然后进行map中key, value 的修改，然后变成list集合返回
        List<Article> articles = articleViewCount.entrySet().stream()
                .map(entry -> new Article(Long.valueOf(entry.getKey()), Long.valueOf(entry.getValue())))
                .collect(Collectors.toList());

        // 更新到数据库中
        // MP中有对数据进行批量操作的方法
        articleService.updateBatchById(articles);
    }

}
