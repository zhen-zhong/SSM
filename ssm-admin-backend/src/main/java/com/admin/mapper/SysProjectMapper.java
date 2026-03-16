package com.admin.mapper;

import com.admin.entity.SysProject;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface SysProjectMapper {
    // 查询列表
    List<SysProject> selectProjectList(@Param("projectName") String projectName);
    // 新增项目
    int insertProject(SysProject project);
    // 更新项目
    int updateProject(SysProject project);
    // 逻辑删除
    int deleteProjectById(Long id);
    
    // --- 关联表操作 ---
    void insertProjectManagers(@Param("projectId") Long projectId, @Param("userIds") List<Long> userIds);
    void deleteManagersByProjectId(Long projectId);
}