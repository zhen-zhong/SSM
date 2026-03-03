package com.admin.mapper;

import com.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysMenuMapper {
    // 超级管理员查询所有菜单 (只查目录 M 和菜单 C，不查按钮 F)
    List<SysMenu> selectAllMenus();

    // 普通用户根据用户 ID 关联角色查询菜单
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}