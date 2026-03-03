package com.admin.service.impl;

import com.admin.entity.SysMenu;
import com.admin.mapper.SysMenuMapper;
import com.admin.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper menuMapper;

    @Override
    public List<SysMenu> getMenuTreeByUserId(Long userId) {
        List<SysMenu> menus;
        // 1. 获取平铺的菜单数据：假设 ID 为 1 的是超级管理员 admin
        if (userId == 1L) {
            menus = menuMapper.selectAllMenus();
        } else {
            menus = menuMapper.selectMenusByUserId(userId);
        }

        // 2. 将平铺的数据组装成树形结构 (根节点的 parentId 通常为 0)
        return buildTree(menus, 0L);
    }

    /**
     * 核心算法：递归组装菜单树 (论文亮点)
     * @param flatList 从数据库查出的所有菜单平铺集合
     * @param parentId 当前需要查找的父级 ID
     */
    private List<SysMenu> buildTree(List<SysMenu> flatList, Long parentId) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : flatList) {
            if (parentId.equals(menu.getParentId())) {
                menu.setChildren(buildTree(flatList, menu.getId()));
                tree.add(menu);
            }
        }
        return tree;
    }
}