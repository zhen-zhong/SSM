package com.admin.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMenuMapper {
    // 清空角色原有的所有菜单权限
    void deleteByRoleId(Long roleId);
    
    // 批量插入新的菜单权限
    void batchInsert(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);
    
    // 查询角色当前拥有的菜单ID列表（用于前端回显）
    List<Long> selectMenuIdsByRoleId(Long roleId);
}