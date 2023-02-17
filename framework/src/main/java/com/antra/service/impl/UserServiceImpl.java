package com.antra.service.impl;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.User;
import com.antra.domain.vo.UserInfoVO;
import com.antra.enums.AppHttpCodeEnum;
import com.antra.exception.SystemException;
import com.antra.mapper.UserMapper;
import com.antra.service.UserService;
import com.antra.utils.BeanCopyUtils;
import com.antra.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2023-02-14 15:05:27
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseResult userInfo() {
        // 获取用户当前id
        // 利用前面封装的工具类
        Long userId = SecurityUtils.getUserId();
        // 根据用户id 查询用户信息
        User user = getById(userId);
        // 封装成 UserInfoVO
        UserInfoVO vo = BeanCopyUtils.copyBean(user, UserInfoVO.class);

        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        // update
        // 这样update不太规范
        boolean b = updateById(user);
        return ResponseResult.okResult();
    }


    @Override
    public ResponseResult register(User user) {
        // TODO: 看一下 ajax怎么操作的？
        // TODO: 可以用 validation框架来判断
        // 对数据进行非空判断， 前后端都需要判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }

        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }

        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }

        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }

        // 对数据进行是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }

        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }

        // 对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        // 加密完数据之后，要重新setpassword
        user.setPassword(encodePassword);
        // 存入数据库
        save(user);
        return ResponseResult.okResult();

    }

    // 判断 username不能存在
    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName, nickName);
        return count(queryWrapper) > 0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        return count(queryWrapper) > 0;
    }
}

