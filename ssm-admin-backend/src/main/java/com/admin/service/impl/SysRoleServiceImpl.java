package com.admin.service.impl;

import com.admin.entity.SysRole;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysUserRoleMapper;
import com.admin.service.SysRoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public Map<String, Object> listPage(int pageNum, int pageSize) {
        Map<String, Object> result = new HashMap<>();

        // 如果传 -1，则跳过 PageHelper，直接全量查询
        if (pageNum == -1) {
            List<SysRole> list = roleMapper.listAllRoles();
            result.put("list", list);
            result.put("total", list.size());
            result.put("pageNum", -1);
            result.put("pageSize", list.size());
            return result;
        }

        // 正常分页逻辑
        PageHelper.startPage(pageNum, pageSize);
        List<SysRole> list = roleMapper.listAllRoles();
        PageInfo<SysRole> pageInfo = new PageInfo<>(list);

        result.put("list", pageInfo.getList());
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());

        return result;
    }

    @Override
    public List<SysRole> listAll() {
        return roleMapper.listAllRoles();
    }

    @Override
    public void addRole(SysRole role) {
        if (role.getRoleCode() == null || role.getRoleCode().trim().isEmpty()) {
            throw new RuntimeException("操作失败：角色编码不能为空！");
        }
        SysRole existRole = roleMapper.selectByRoleCode(role.getRoleCode());
        if (existRole != null) {
            throw new RuntimeException("操作失败：角色编码已存在，请更换！");
        }
        
        roleMapper.insert(role);
    }

    @Override
    public void updateRole(SysRole role) {
        if (role.getRoleCode() != null && !role.getRoleCode().trim().isEmpty()) {
            SysRole existRole = roleMapper.selectByRoleCode(role.getRoleCode());
            if (existRole != null && !existRole.getId().equals(role.getId())) {
                throw new RuntimeException("操作失败：角色编码已存在，请更换！");
            }
        }
        
        roleMapper.update(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        int count = userRoleMapper.countUsersByRoleId(id);
        if (count > 0) {
            throw new RuntimeException("操作失败：该角色下有关联用户（共" + count + "人），请先移除用户角色再删除！");
        }

        SysRole role = roleMapper.selectById(id);
        if (role != null && "admin".equalsIgnoreCase(role.getRoleCode())) {
            throw new RuntimeException("操作失败：系统内置超级管理员角色禁止删除！");
        }

        roleMapper.deleteById(id);
    }
}