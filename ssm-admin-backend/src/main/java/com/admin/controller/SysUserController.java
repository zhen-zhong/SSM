package com.admin.controller;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.admin.common.Result;
import com.admin.common.annotation.Log;
import com.admin.entity.SysUser;
import com.admin.service.SysUserService;
import com.admin.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation("获取当前登录用户信息及角色")
    @GetMapping("/info")
    public Result<UserInfoVO> getInfo(HttpServletRequest request) {
        // 1. 拦截器里必定能解析出 subject (也就是 username)，因为你的 Token 里有 "sub": "admin"
        String currentUsername = (String) request.getAttribute("currentUsername");
        if (currentUsername == null) {
            throw new RuntimeException("未能获取当前登录状态，请重新登录");
        }

        // 2. 拐个弯：用现成的 getByUsername 方法查出该用户的全部信息（拿到 ID）
        SysUser user = userService.getByUsername(currentUsername);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 3. 完美复用我们写好的核心 RBAC 查询逻辑
        UserInfoVO userInfo = userService.getUserInfo(user.getId());
        
        return Result.success(userInfo);
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
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        userService.resetPassword(currentUserId, oldPassword, newPassword);
        return Result.success("修改成功");
    }

    @ApiOperation("修改个人信息")
    @Log(module = "用户管理", action = "修改个人信息")
    @PutMapping("/update")
    public Result updateUserInfo(@RequestBody SysUser user, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        user.setId(currentUserId); 
        userService.updateUserInfo(user);
        return Result.success("信息修改成功");
    }

    @ApiOperation("分配用户角色")
    @Log(module = "用户管理", action = "分配角色")
    @PostMapping("/role")
    public Result assignRole(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        userService.updateRole(userId, roleIds);
        return Result.success("角色分配成功");
    }
}