package com.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysUserRoleMapper {
    /**
     * 删除用户拥有的所有角色关系
     */
    void deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量插入用户角色关系
     */
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    
    int countUsersByRoleId(@Param("roleId") Long roleId);
}