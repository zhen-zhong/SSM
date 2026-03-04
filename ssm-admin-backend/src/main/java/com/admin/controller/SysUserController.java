package com.admin.controller;

import com.admin.common.Result;
import com.admin.common.annotation.Log;
import com.admin.entity.SysRole;
import com.admin.entity.SysUser;
import com.admin.mapper.SysRoleMapper;
import com.admin.service.SysUserService;
import com.admin.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "用户管理模块")
@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    @Autowired
    private SysRoleMapper roleMapper;

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
        String currentUsername = (String) request.getAttribute("currentUsername");
        if (currentUsername == null) {
            return Result.error(401, "未能获取当前登录状态");
        }
        SysUser user = userService.getByUsername(currentUsername);
        if (user == null) {
            return Result.error(401, "用户不存在");
        }
        return Result.success(userService.getUserInfo(user.getId()));
    }

    @ApiOperation("获取所有用户列表")
    @GetMapping("/list")
    public Result<List<SysUser>> list() {
        return Result.success(userService.listAll());
    }

    @ApiOperation("新增用户")
    @Log(module = "用户管理", action = "新增用户")
    @PostMapping("/register")
    public Result<String> register(@RequestBody SysUser user) {
        userService.register(user);
        return Result.success("用户添加成功");
    }

    @ApiOperation("修改用户基本信息")
    @Log(module = "用户管理", action = "编辑用户")
    @PutMapping("/update")
    public Result<String> updateUserInfo(@RequestBody SysUser user) {
        userService.updateUserInfo(user);
        return Result.success("信息修改成功");
    }

    @ApiOperation("管理员重置用户密码")
    @Log(module = "用户管理", action = "重置密码")
    @PutMapping("/reset-password-admin")
    public Result<String> resetPasswordAdmin(@RequestParam Long userId, @RequestParam String newPassword) {
        userService.updatePassword(userId, newPassword);
        return Result.success("密码重置成功");
    }

    @ApiOperation("分配用户角色权限")
    @Log(module = "用户管理", action = "分配权限")
    @PostMapping("/role")
    public Result<String> assignRole(@RequestParam Long userId, @RequestBody List<Long> roleIds) {
        userService.updateRole(userId, roleIds);
        return Result.success("权限分配成功");
    }

    // 🌟 新增接口：获取所有角色列表供前端下拉框使用
    @ApiOperation("获取所有可用角色列表")
    @GetMapping("/role/list")
    public Result<List<SysRole>> listAllRoles() {
        return Result.success(roleMapper.listAllRoles());
    }

    @ApiOperation("修改账号状态")
    @Log(module = "用户管理", action = "修改状态")
    @PutMapping("/status")
    public Result<String> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }

    @ApiOperation("逻辑删除用户")
    @Log(module = "用户管理", action = "删除用户")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        // 🌟 这里的逻辑在 ServiceImpl 中已经加了禁止删除 admin 的判断
        userService.deleteUser(id);
        return Result.success("删除成功");
    }

    @ApiOperation("找回密码（身份校验方式）")
    @Log(module = "用户管理", action = "找回密码")
    @PostMapping("/find-password")
    public Result<String> findPassword(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String realName = params.get("realName");
        String phone = params.get("phone");
        String newPassword = params.get("newPassword");
        
        userService.findPassword(username, realName, phone, newPassword);
        return Result.success("密码重置成功，请重新登录");
    }

    @ApiOperation("用户自行修改密码")
    @PutMapping("/reset-password")
    public Result<String> resetPassword(@RequestParam String oldPassword, @RequestParam String newPassword, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        userService.resetPassword(currentUserId, oldPassword, newPassword);
        return Result.success("修改成功");
    }
}