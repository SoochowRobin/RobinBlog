package com.antra.service.impl;

import com.antra.constant.SystemConstants;
import com.antra.domain.entity.Menu;
import com.antra.mapper.MenuMapper;
import com.antra.service.MenuService;
import com.antra.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2023-02-16 13:36:35
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {





    @Override
    public List<String> selectPermsByUserId(Long id) {
        // 根据用户id去查询关键字
        // 如果是管理员返回所有的权限

        if(SecurityUtils.isAdmin()){
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Menu::getMenuType, SystemConstants.MEMU, SystemConstants.BUTTON);
            queryWrapper.eq(Menu::getStatus, SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(queryWrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        // 否则返回其所具有的权限
        return getBaseMapper().selectPermsByUserId(id);
    }


    @Override
    public List<Menu> selectRouterMemuTreeByUserId(Long userId) {
        MenuMapper memuMapper = getBaseMapper();
        List<Menu> menus = null;
        // 判断是否为管理员
        //如果是 返回所有符合要求的Menu
        if(SecurityUtils.isAdmin()){
            menus = memuMapper.selectAllRouterMenu();
        }else{
            //如果不是返回当前用户的Menu
            menus = memuMapper.selectRouterMenuTreeByUserId(userId);
        }
        // 构建memuTree的形式
        List<Menu> menuTree = buildMenuTree(menus, 0L);
        return menus;

    }

    private List<Menu> buildMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menuTree;
    }

    /**
     * 获取存入参数的 子Menu集合
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu, List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m->m.setChildren(getChildren(m,menus)))
                .collect(Collectors.toList());
        return childrenList;
    }
}

