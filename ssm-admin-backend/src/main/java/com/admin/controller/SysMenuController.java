package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysUser;
import com.admin.service.SysMenuService;
import com.admin.service.SysUserService;
import com.admin.vo.RouteVO;
import com.admin.vo.RouteMetaVO; // 🌟 新增导入：需要用到 RouteMetaVO
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "菜单与路由模块")
@RestController
@RequestMapping("/route")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;

    @Autowired
    private SysUserService userService; 

    @ApiOperation("获取当前登录用户的动态路由树")
    @GetMapping("/getUserRoutes")
    public Result<Map<String, Object>> getUserRoutes(HttpServletRequest request) {
        String currentUsername = (String) request.getAttribute("currentUsername");
        if (currentUsername == null) {
            return Result.error(401, "未能获取当前登录状态");
        }

        SysUser user = userService.getByUsername(currentUsername);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }

        // 1. 获取该用户在数据库中配置的路由
        List<RouteVO> routes = menuService.getUserRoutes(user.getId());

        // 🌟 2. 代码兜底逻辑：确保所有用户必定拥有 home 页面的访问权限
        boolean hasHome = routes.stream().anyMatch(r -> "home".equals(r.getName()));
        if (!hasHome) {
            RouteVO homeRoute = new RouteVO();
            homeRoute.setName("home");
            homeRoute.setPath("/home");
            homeRoute.setComponent("layout.base$view.home"); 

            RouteMetaVO homeMeta = new RouteMetaVO();
            homeMeta.setTitle("首页");
            homeMeta.setI18nKey("route.home");
            homeMeta.setIcon("mdi:monitor-dashboard");
            homeMeta.setOrder(1);
            homeMeta.setHideInMenu(false);
            homeMeta.setKeepAlive(false);
            // 这里也可以加上 roles 权限标识，确保前端校验通过
            homeRoute.setMeta(homeMeta);

            // 将首页强行插入到路由列表的第一个位置
            routes.add(0, homeRoute);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("routes", routes);      
        data.put("home", "home");        

        return Result.success(data);
    }

    @ApiOperation("获取常量路由(用于动态模式兼容)")
    @GetMapping("/getConstantRoutes")
    public Result<List<Object>> getConstantRoutes() {
        return Result.success(new ArrayList<>());
    }

    @ApiOperation("检查路由是否存在")
    @GetMapping("/isRouteExist")
    public Result<Boolean> isRouteExist(@RequestParam("routeName") String routeName) {
        if (routeName == null || routeName.isEmpty()) {
            return Result.success(false);
        }
        return Result.success(true); 
    }
}