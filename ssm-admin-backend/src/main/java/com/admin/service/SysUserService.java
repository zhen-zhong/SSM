package com.admin.service;

import com.admin.entity.SysUser;
import java.util.List;

public interface SysUserService {
    // 基础功能
    SysUser getByUsername(String username);
    String login(String username, String password);
    void register(SysUser user);
    void findPassword(String username, String realName, String phone, String newPassword);
    void resetPassword(Long userId, String oldPassword, String newPassword);
    // 用户管理功能
    void updateUserInfo(SysUser user);
    void updatePassword(Long id, String newPwd);
    void updateStatus(Long id, Integer status);
    void updateRole(Long userId, List<Long> roleIds);
    void deleteUser(Long id);
    List<SysUser> listAll();
}