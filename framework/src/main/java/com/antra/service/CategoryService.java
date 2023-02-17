package com.antra.service;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2023-02-10 22:01:03
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();
}

