package com.antra.service.impl;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.LoginUser;
import com.antra.domain.entity.User;
import com.antra.domain.vo.BlogUserLoginVO;
import com.antra.domain.vo.UserInfoVO;
import com.antra.service.LoginService;
import com.antra.utils.BeanCopyUtils;
import com.antra.utils.JwtUtil;
import com.antra.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class SystemLoginServiceImpl implements LoginService {

    // 用AuthenticationManager 方法来操作, 在配置类中定义
    private AuthenticationManager authenticationManager;

    private RedisCache redisCache;

    @Autowired
    public SystemLoginServiceImpl(AuthenticationManager authenticationManager, RedisCache redisCache) {
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
        // 后台存的是 login:
        redisCache.setCacheObject("login:"+ id, loginUser);


        // 这边不需要返回UserInfo了，只需要返回token 就可以了
        Map<String, String> map = new HashMap<>();
        map.put("token", jwt);
        return ResponseResult.okResult(map);
    }

}
