package com.antra.mapper;

import com.antra.domain.entity.Link;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 友链(Link)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-11 15:26:59
 */
@Repository
public interface LinkMapper extends BaseMapper<Link> {

}

