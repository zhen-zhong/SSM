package com.admin.service.impl;

import com.admin.entity.SysMenu;
import com.admin.mapper.SysMenuMapper;
import com.admin.service.SysMenuService;
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

    /**
     * 递归构建路由树
     */
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
                    meta.setI18nKey("route." + menu.getRouteName());
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
}