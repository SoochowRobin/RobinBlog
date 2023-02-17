package com.antra.filter;

import com.alibaba.fastjson.JSON;
import com.antra.domain.ResponseResult;
import com.antra.domain.entity.LoginUser;
import com.antra.enums.AppHttpCodeEnum;
import com.antra.utils.JwtUtil;
import com.antra.utils.RedisCache;
import com.antra.utils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private RedisCache redisCache;

    @Autowired
    public JwtAuthenticationTokenFilter(RedisCache redisCache) {
        this.redisCache = redisCache;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 获取请求头中的token
        String token = httpServletRequest.getHeader("token");

        if(!StringUtils.hasText(token)){
            // 没有token直接放行
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        Claims claims = null;
        try {
            // 对Jwt进行解密
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            // token超时， token非法
            e.printStackTrace();
            // 响应告诉前端重新登录
            ResponseResult responseResult = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(responseResult));
            return;
        }

        // 解析获取userid
        String userId = claims.getSubject();
        // 从redis中获取用户信息(如果我们登录过了， redis里面是有信息的，如果没有，证明登录失效或者是没有登录)
        LoginUser loginUser = redisCache.getCacheObject("bloglogin:" + userId);

        if(Objects.isNull(loginUser)){
            ResponseResult responseResult = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(httpServletResponse, JSON.toJSONString(responseResult));
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, null);
        // 如果有就存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }
}
