package com.admin.service;

import com.admin.entity.SysUser;
import com.admin.vo.UserInfoVO;
import java.util.List;
import java.util.Map;

public interface SysUserService {
    // 根据用户名查询
    SysUser getByUsername(String username);
    
    // 分页查询：返回包含 list, total, pageNum, pageSize 的 Map
    Map<String, Object> listPage(int pageNum, int pageSize);
    
    // 登录验证
    String login(String username, String password);
    
    // 获取用户信息
    UserInfoVO getUserInfo(Long userId);
    
    // 注册/新增用户
    void register(SysUser user);
    
    // 更新用户信息
    void updateUserInfo(SysUser user);
    
    // 修改密码
    void updatePassword(Long id, String newPwd);
    
    // 修改状态
    void updateStatus(Long id, Integer status);
    
    // 分配角色
    void updateRole(Long userId, List<Long> roleIds);
    
    // 删除用户
    void deleteUser(Long id);
    
    // 查询所有
    List<SysUser> listAll();
    
    // 找回密码
    void findPassword(String username, String realName, String phone, String newPassword);
    
    // 重置密码
    void resetPassword(Long userId, String oldPassword, String newPassword);
}