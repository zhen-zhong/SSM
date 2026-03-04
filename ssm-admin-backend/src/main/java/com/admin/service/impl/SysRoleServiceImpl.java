package com.admin.service.impl;

import com.admin.entity.SysRole;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysUserRoleMapper;
import com.admin.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public List<SysRole> listAll() {
        return roleMapper.listAllRoles();
    }

    @Override
    public void addRole(SysRole role) {
        roleMapper.insert(role);
    }

    @Override
    public void updateRole(SysRole role) {
        roleMapper.update(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        // 🌟 核心需求：检查是否有用户正在使用该角色
        int count = userRoleMapper.countUsersByRoleId(id);
        if (count > 0) {
            throw new RuntimeException("操作失败：该角色下有关联用户（共" + count + "人），请先移除用户角色再删除！");
        }

        // 🌟 安全锁：禁止删除 admin 编码的角色
        SysRole role = roleMapper.selectById(id);
        if (role != null && "admin".equalsIgnoreCase(role.getRoleCode())) {
            throw new RuntimeException("操作失败：系统内置超级管理员角色禁止删除！");
        }

        roleMapper.deleteById(id);
    }
}