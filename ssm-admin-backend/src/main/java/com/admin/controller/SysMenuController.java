package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysMenu;
import com.admin.entity.SysUser;
import com.admin.service.SysMenuService;
import com.admin.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Api(tags = "菜单权限模块")
@RestController
@RequestMapping("/menu")
public class SysMenuController {

    @Autowired
    private SysMenuService menuService;
    
    @Autowired
    private SysUserService userService; // 注入 UserService

    @ApiOperation("获取当前登录用户的动态菜单树")
    @GetMapping("/tree")
    public Result<List<SysMenu>> getMenuTree(HttpServletRequest request) {
        // 1. 从 request 获取用户名 (拦截器里存的)
        String username = (String) request.getAttribute("currentUsername");
        if (username == null) {
            // 修改这里：只传字符串
            return Result.error("未能获取登录状态，请重新登录"); 
        }

        // 2. 根据用户名查询当前用户真实 ID
        SysUser currentUser = userService.getByUsername(username);
        if (currentUser == null) {
            // 修改这里：只传字符串
            return Result.error("用户不存在"); 
        }

        // 3. 传入真实的 userId 获取菜单树
        List<SysMenu> tree = menuService.getMenuTreeByUserId(currentUser.getId());
        return Result.success(tree);
    }
}