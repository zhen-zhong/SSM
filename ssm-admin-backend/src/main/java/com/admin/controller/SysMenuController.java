package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysUser;
import com.admin.service.SysMenuService;
import com.admin.service.SysUserService;
import com.admin.vo.MenuTreeVO;
import com.admin.vo.RouteMetaVO;
import com.admin.vo.RouteVO;
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

        List<RouteVO> routes = menuService.getUserRoutes(user.getId());

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
            homeRoute.setMeta(homeMeta);

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

    @ApiOperation("获取所有菜单树(用于角色分配权限和菜单管理页面)")
    @GetMapping("/getAllMenuTree")
    public Result<List<MenuTreeVO>> getAllMenuTree() {
        return Result.success(menuService.getAllMenuTree());
    }
}