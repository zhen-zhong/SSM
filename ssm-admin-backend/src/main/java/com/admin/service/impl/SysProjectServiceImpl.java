package com.admin.service.impl;

import com.admin.entity.SysProject;
import com.admin.mapper.SysProjectMapper;
import com.admin.service.SysProjectService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SysProjectServiceImpl implements SysProjectService {

    @Autowired
    private SysProjectMapper projectMapper;

    @Override
    public PageInfo<SysProject> getProjectPage(int pageNum, int pageSize, String projectName) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysProject> list = projectMapper.selectProjectList(projectName);
        
        // 处理 managerIdsStr 转为 List<Long>，方便前端回显编辑数据
        for (SysProject p : list) {
            if (p.getManagerIdsStr() != null && !p.getManagerIdsStr().isEmpty()) {
                List<Long> ids = new ArrayList<>();
                for (String idStr : p.getManagerIdsStr().split(",")) {
                    ids.add(Long.valueOf(idStr));
                }
                p.setManagerIds(ids);
            }
        }
        return new PageInfo<>(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProject(SysProject project) {
        // 1. 保存主表
        projectMapper.insertProject(project);
        // 2. 保存关联的负责人
        if (project.getManagerIds() != null && !project.getManagerIds().isEmpty()) {
            projectMapper.insertProjectManagers(project.getId(), project.getManagerIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProject(SysProject project) {
        // 1. 更新主表
        projectMapper.updateProject(project);
        // 2. 更新负责人（先删后加模式）
        projectMapper.deleteManagersByProjectId(project.getId());
        if (project.getManagerIds() != null && !project.getManagerIds().isEmpty()) {
            projectMapper.insertProjectManagers(project.getId(), project.getManagerIds());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long id) {
        projectMapper.deleteProjectById(id);
        projectMapper.deleteManagersByProjectId(id);
    }
}