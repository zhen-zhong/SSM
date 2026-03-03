package com.admin.service;
import com.admin.entity.SysMenu;
import java.util.List;

public interface SysMenuService {
    // 获取当前用户的动态菜单树
    List<SysMenu> getMenuTreeByUserId(Long userId);
}