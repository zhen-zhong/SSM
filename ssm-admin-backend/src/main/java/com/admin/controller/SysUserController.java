package com.admin.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.admin.common.Result;
import com.admin.common.annotation.Log;
import com.admin.entity.SysUser;
import com.admin.service.SysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "用户管理模块")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @ApiOperation("用户登录")
    @Log(module = "认证模块", action = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody SysUser loginUser) {
        
        String token = userService.login(loginUser.getUsername(), loginUser.getPassword());
        Map<String, String> data = new java.util.HashMap<>();
        data.put("token", token);
        data.put("refreshToken", token); 

        return Result.success(data);
    }

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/info")
    public Result<SysUser> getInfo(@ApiParam("用户名") @RequestParam String username) {
        SysUser user = userService.getByUsername(username);
        return Result.success(user);
    }

    @ApiOperation("注册新用户")
    @Log(module = "用户管理", action = "注册账号")
    @PostMapping("/register")
    public Result register(@RequestBody SysUser user) {
        userService.register(user);
        return Result.success("注册成功");
    }

    @ApiOperation("修改账号状态")
    @Log(module = "用户管理", action = "修改状态")
    @PutMapping("/status")
    public Result updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    @ApiOperation("逻辑删除用户")
    @Log(module = "用户管理", action = "删除用户")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    @ApiOperation("获取所有用户列表")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.success(userService.listAll());
    }

    @ApiOperation("找回密码（身份校验方式）")
    @Log(module = "用户管理", action = "找回密码")
    @PostMapping("/find-password")
    public Result findPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String realName = params.get("realName");
        String phone = params.get("phone");
        String newPassword = params.get("newPassword");
        
        userService.findPassword(username, realName, phone, newPassword);
        return Result.success("密码重置成功，请重新登录");
    }

    @ApiOperation("修改密码（登录后操作）")
    @Log(module = "用户管理", action = "修改密码")
    @PutMapping("/reset-password")
    public Result resetPassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request) {
        // 从 Request 属性中获取拦截器解析出的当前用户 ID (假设你在拦截器里存了 userId)
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        
        userService.resetPassword(currentUserId, oldPassword, newPassword);
        return Result.success("修改成功");
    }

    @ApiOperation("修改个人信息")
    @Log(module = "用户管理", action = "修改个人信息")
    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody SysUser user, HttpServletRequest request) {
        // 安全逻辑：从请求域中获取拦截器解析出的当前登录用户 ID
        // 这样即使前端传了别的 ID，后端也会以当前登录人为准，防止“越权修改”
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        user.setId(currentUserId); 
        
        userService.updateUserInfo(user);
        return Result.success("信息修改成功");
    }

    @ApiOperation("分配用户角色")
    @Log(module = "用户管理", action = "分配角色")
    @PostMapping("/role")
    public Result assignRole(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        // 这里的 roleIds 是 List<Long>，现在能正确对齐接口了
        userService.updateRole(userId, roleIds);
        return Result.success("角色分配成功");
    }

}