package com.admin.service;

import com.admin.entity.SysRole;
import java.util.List;

public interface SysRoleService {
    List<SysRole> listAll();
    void addRole(SysRole role);
    void updateRole(SysRole role);
    void deleteRole(Long id);
}