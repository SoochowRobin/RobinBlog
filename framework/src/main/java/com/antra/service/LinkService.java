package com.antra.service;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2023-02-11 15:27:01
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();
}

