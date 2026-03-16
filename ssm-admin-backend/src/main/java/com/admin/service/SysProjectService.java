package com.admin.service;

import com.admin.entity.SysProject;
import com.github.pagehelper.PageInfo;

public interface SysProjectService {

    /**
     * 获取项目分页列表
     * @param pageNum 当前页
     * @param pageSize 每页条数
     * @param projectName 项目名称(模糊搜索)
     * @return 分页对象
     */
    PageInfo<SysProject> getProjectPage(int pageNum, int pageSize, String projectName);

    /**
     * 新增项目
     * @param project 项目实体
     */
    void addProject(SysProject project);

    /**
     * 更新项目
     * @param project 项目实体
     */
    void updateProject(SysProject project);

    /**
     * 删除项目
     * @param id 项目ID
     */
    void deleteProject(Long id);
}