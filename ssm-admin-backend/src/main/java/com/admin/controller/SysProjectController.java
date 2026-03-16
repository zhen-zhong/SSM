package com.admin.controller;

import com.admin.common.Result;
import com.admin.common.annotation.Log;
import com.admin.entity.SysProject;
import com.admin.service.SysProjectService;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "项目管理模块")
@RestController
@RequestMapping("/project")
public class SysProjectController {

    @Autowired
    private SysProjectService projectService;

    @GetMapping("/list")
    @ApiOperation("获取项目分页列表")
    public Result<PageInfo<SysProject>> getList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String projectName) {
        PageInfo<SysProject> pageInfo = projectService.getProjectPage(pageNum, pageSize, projectName);
        return Result.success(pageInfo);
    }

    @PostMapping("/add")
    @ApiOperation("新增项目")
    @Log(module = "项目管理", action = "新增项目")
    public Result<?> add(@RequestBody SysProject project) {
        projectService.addProject(project);
        return Result.success(null);
    }

    @PutMapping("/update")
    @ApiOperation("修改项目")
    @Log(module = "项目管理", action = "修改项目")
    public Result<?> update(@RequestBody SysProject project) {
        projectService.updateProject(project);
        return Result.success(null);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation("删除项目")
    @Log(module = "项目管理", action = "删除项目")
    public Result<?> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success(null);
    }
}