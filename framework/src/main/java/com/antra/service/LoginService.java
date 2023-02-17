package com.antra.service;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.User;

public interface LoginService {

    ResponseResult login(User user);

//    ResponseResult logout();
}
