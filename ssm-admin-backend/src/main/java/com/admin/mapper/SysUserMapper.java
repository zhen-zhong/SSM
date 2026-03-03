package com.admin.mapper;

import com.admin.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户管理持久层接口
 */
@Mapper
public interface SysUserMapper {

    /**
     * 插入新用户
     * @param user 用户实体
     * @return 影响行数
     */
    int insert(SysUser user);

    /**
     * 动态更新用户信息
     * @param user 包含ID的用户实体
     * @return 影响行数
     */
    int update(SysUser user);

    /**
     * 根据主键ID查询用户
     * @param id 用户ID
     * @return 用户实体
     */
    SysUser selectById(@Param("id") Long id);
    
    /**
     * 根据用户名查询用户（用于登录校验、唯一性检查）
     * @param username 用户名
     * @return 用户实体
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 查询所有未删除的用户列表
     * @return 用户列表
     */
    List<SysUser> listAll();
}