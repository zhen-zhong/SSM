package com.admin.service;

import com.admin.entity.SysRole;
import java.util.List;
import java.util.Map;

public interface SysRoleService {
    // 分页查询接口
    Map<String, Object> listPage(int pageNum, int pageSize);
    
    // 查询所有角色（下拉框等可能用到）
    List<SysRole> listAll();
    
    // 新增角色
    void addRole(SysRole role);
    
    // 修改角色
    void updateRole(SysRole role);
    
    // 删除角色
    void deleteRole(Long id);
}