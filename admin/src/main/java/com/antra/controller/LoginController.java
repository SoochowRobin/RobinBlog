package com.antra.controller;

import com.antra.domain.ResponseResult;
import com.antra.domain.entity.LoginUser;
import com.antra.domain.entity.Menu;
import com.antra.domain.entity.User;
import com.antra.domain.vo.AdminUserInfoVO;
import com.antra.domain.vo.RoutersVO;
import com.antra.domain.vo.UserInfoVO;
import com.antra.enums.AppHttpCodeEnum;
import com.antra.exception.SystemException;
import com.antra.service.BlogLoginService;
import com.antra.service.LoginService;
import com.antra.service.MenuService;
import com.antra.service.RoleService;
import com.antra.utils.BeanCopyUtils;
import com.antra.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {

    private LoginService loginService;

    private MenuService menuService;

    private RoleService roleService;

    @Autowired
    public LoginController(LoginService loginService, MenuService menuService, RoleService roleService) {
        this.loginService = loginService;
        this.menuService = menuService;
        this.roleService = roleService;
    }

    @PostMapping("/user/login")
    // 先用 User来接收，
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            // 提示 必须要有用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }

        return loginService.login(user);
    }

    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVO> getInfo(){
        // 获取当前登录的用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        // 根据用户id查询权限信息
        List<String> perms = menuService.selectPermsByUserId(loginUser.getUser().getId());
        // 根据用户id查询角色信息
        List<String> roleKeyList = roleService.selectRoleKeyByUserId(loginUser.getUser().getId());
        // 封装数据返回
        UserInfoVO userInfoVO = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVO.class);
        AdminUserInfoVO adminUserInfoVO = new AdminUserInfoVO(perms, roleKeyList, userInfoVO);
        return ResponseResult.okResult(adminUserInfoVO);
    }

    @GetMapping("/getRouters")
    public ResponseResult<RoutersVO> getRoutersInfo(){
        Long userId = SecurityUtils.getUserId();
        //查询 memu, 结果是tree的形式
        List<Menu> menus = menuService.selectRouterMemuTreeByUserId(userId);

        return ResponseResult.okResult(new RoutersVO(menus));
    }


//    @PostMapping("/logout")
//    public ResponseResult logout(){
//        // 调用对应的方法
//        return blogLoginService.logout();
//    }
}
