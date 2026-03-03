package com.admin.mapper;

import com.admin.entity.SysLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SysLogMapper {
    void insert(SysLog sysLog);
}