package com.antra.mapper;

import com.antra.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 用户表(User)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-11 21:04:34
 */
@Repository
public interface UserMapper extends BaseMapper<User> {

}

