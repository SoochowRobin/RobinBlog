package com.antra.mapper;

import com.antra.domain.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 分类表(Category)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-10 22:03:30
 */
@Repository
public interface CategoryMapper extends BaseMapper<Category> {

}

