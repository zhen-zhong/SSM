package com.admin.service;

import com.admin.vo.RouteVO;
import com.admin.vo.MenuTreeVO;
import java.util.List;

public interface SysMenuService {
    List<RouteVO> getUserRoutes(Long userId);
    List<MenuTreeVO> getAllMenuTree();
}