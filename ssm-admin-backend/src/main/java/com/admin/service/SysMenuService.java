package com.admin.service;

import com.admin.vo.RouteVO;
import java.util.List;

public interface SysMenuService {
    List<RouteVO> getUserRoutes(Long userId);
}