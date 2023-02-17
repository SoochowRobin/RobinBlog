package com.antra.controller;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.User;
import com.antra.enums.AppHttpCodeEnum;
import com.antra.exception.SystemException;
import com.antra.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;

@RestController
public class BlogLoginController {

    private BlogLoginService blogLoginService;

    @Autowired
    public BlogLoginController(BlogLoginService blogLoginService) {
        this.blogLoginService = blogLoginService;
    }

    @PostMapping("/login")
    // 先用 User来接收，
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            // 提示 必须要有用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }

        return blogLoginService.login(user);
    }


    @PostMapping("/logout")
    public ResponseResult logout(){
        // 调用对应的方法
        return blogLoginService.logout();
    }
}
