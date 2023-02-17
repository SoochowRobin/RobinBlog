package com.antra.service.impl;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.LoginUser;
import com.antra.domain.entity.User;
import com.antra.domain.vo.BlogUserLoginVO;
import com.antra.domain.vo.UserInfoVO;
import com.antra.service.BlogLoginService;
import com.antra.utils.BeanCopyUtils;
import com.antra.utils.JwtUtil;
import com.antra.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class BlogLoginServiceImpl implements BlogLoginService {

    // 用AuthenticationManager 方法来操作, 在配置类中定义
    private AuthenticationManager authenticationManager;

    private RedisCache redisCache;

    @Autowired
    public BlogLoginServiceImpl(AuthenticationManager authenticationManager, RedisCache redisCache) {
        this.authenticationManager = authenticationManager;
        this.redisCache = redisCache;
    }

    @Override
    public ResponseResult login(User user) {

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        // 获取user id 生成token
        LoginUser loginUser = (LoginUser)authenticate.getPrincipal();
        Long id = loginUser.getUser().getId();
        // create JWT
        String jwt = JwtUtil.createJWT(id.toString());

        // 存入 user id + loginUser object
        redisCache.setCacheObject("bloglogin:"+ id, loginUser);

        // 把用户信息存入redis, 把token 和 userinfo封装 返回
        // 把User 转换成UserInfoVO
        UserInfoVO userInfoVO = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVO.class);
        BlogUserLoginVO vo = new BlogUserLoginVO(jwt, userInfoVO);
        return ResponseResult.okResult(vo);
    }


    // logout就是删除redis当中的用户信息
    // 从securityContext中获取userid,会经常使用，可以做一个封装
    @Override
    public ResponseResult logout() {
        // 获取token,解析获取user id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser)authentication.getPrincipal();
        // 获取userid
        Long id = loginUser.getUser().getId();
        redisCache.deleteObject("bloglogin:"+id);
        return ResponseResult.okResult();
    }
}
