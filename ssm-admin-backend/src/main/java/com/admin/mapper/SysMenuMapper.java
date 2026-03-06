package com.admin.mapper;

import com.admin.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysMenuMapper {
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}