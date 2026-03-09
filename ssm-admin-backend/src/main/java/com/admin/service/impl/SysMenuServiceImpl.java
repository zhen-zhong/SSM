package com.admin.service.impl;

import com.admin.entity.SysMenu;
import com.admin.mapper.SysMenuMapper;
import com.admin.service.SysMenuService;
import com.admin.vo.MenuTreeVO;
import com.admin.vo.RouteMetaVO;
import com.admin.vo.RouteVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<RouteVO> getUserRoutes(Long userId) {
        List<SysMenu> allMenus = menuMapper.selectMenusByUserId(userId);
        return buildRouteTree(allMenus, 0L);
    }

    @Override
    public List<MenuTreeVO> getAllMenuTree() {
        List<SysMenu> allMenus = menuMapper.listAll();
        return buildMenuTreeVO(allMenus, 0L);
    }

    private List<RouteVO> buildRouteTree(List<SysMenu> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    RouteVO routeVO = new RouteVO();
                    routeVO.setName(menu.getRouteName());
                    routeVO.setPath(menu.getRoutePath());
                    routeVO.setComponent(menu.getComponent());

                    RouteMetaVO meta = new RouteMetaVO();
                    meta.setTitle(menu.getMenuName());
                    // meta.setI18nKey("route." + menu.getRouteName());
                    meta.setI18nKey(menu.getMenuName());
                    meta.setI18nKey(null); 
                    meta.setIcon(menu.getIcon());
                    meta.setOrder(menu.getSortNum());
                    meta.setHideInMenu(menu.getIsHide() == 1);
                    meta.setKeepAlive(menu.getKeepAlive() == 1);
                    routeVO.setMeta(meta);

                    List<RouteVO> children = buildRouteTree(allMenus, menu.getId());
                    if (!children.isEmpty()) {
                        routeVO.setChildren(children);
                    }

                    return routeVO;
                })
                .collect(Collectors.toList());
    }

    private List<MenuTreeVO> buildMenuTreeVO(List<SysMenu> allMenus, Long parentId) {
        return allMenus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> {
                    MenuTreeVO vo = new MenuTreeVO();
                    vo.setId(menu.getId());
                    vo.setLabel(menu.getMenuName());
                    vo.setParentId(menu.getParentId());
                    List<MenuTreeVO> children = buildMenuTreeVO(allMenus, menu.getId());
                    if (!children.isEmpty()) {
                        vo.setChildren(children);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
    }
}