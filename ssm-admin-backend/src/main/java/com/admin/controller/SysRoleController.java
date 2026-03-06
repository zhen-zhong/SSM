package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysRole;
import com.admin.service.SysRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "角色管理模块")
@RestController
@RequestMapping("/role")
public class SysRoleController {

    @Autowired
    private SysRoleService roleService;

    @ApiOperation("获取角色列表(分页)")
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "1") int pageNum, 
                                            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(roleService.listPage(pageNum, pageSize));
    }

    @ApiOperation("新增角色")
    @PostMapping("/add")
    public Result<String> add(@RequestBody SysRole role) {
        roleService.addRole(role);
        return Result.success("新增成功");
    }

    @ApiOperation("修改角色")
    @PutMapping("/update")
    public Result<String> update(@RequestBody SysRole role) {
        roleService.updateRole(role);
        return Result.success("修改成功");
    }

    @ApiOperation("删除角色")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success("删除成功");
    }
}