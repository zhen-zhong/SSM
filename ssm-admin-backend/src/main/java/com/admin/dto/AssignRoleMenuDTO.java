package com.admin.dto;

import java.util.List;

public class AssignRoleMenuDTO {
    private Long roleId;
    private List<Long> menuIds;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public List<Long> getMenuIds() { return menuIds; }
    public void setMenuIds(List<Long> menuIds) { this.menuIds = menuIds; }
}