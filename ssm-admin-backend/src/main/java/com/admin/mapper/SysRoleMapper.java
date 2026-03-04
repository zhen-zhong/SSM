package com.admin.mapper;

import com.admin.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysRoleMapper {

    /** 查询角色编码列表 */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
    
    /** 查询所有角色 */
    List<SysRole> listAllRoles();

    /** 根据用户ID查询其角色列表 */
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /** 根据ID查询单个角色 */
    SysRole selectById(@Param("id") Long id);

    /** 新增角色 */
    int insert(SysRole role);

    /** 修改角色 */
    int update(SysRole role);

    /** 删除角色 */
    int deleteById(@Param("id") Long id);
}