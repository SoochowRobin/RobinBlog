package com.antra.service;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.User;

public interface BlogLoginService {

    ResponseResult login(User user);

    ResponseResult logout();
}
