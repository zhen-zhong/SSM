package com.admin.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysRoleMapper {
    // 🌟 根据用户 ID 查询他所有的角色编码
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}