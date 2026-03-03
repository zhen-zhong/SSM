package com.admin.controller;

import com.admin.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@Api(tags = "系统数据管理（测试数据）")
@RestController
@RequestMapping("/data")
public class SysDataController {

    // 直接注入 Spring 管理的数据源 (Druid)
    @Autowired
    private DataSource dataSource;

    @ApiOperation("一键批量插入测试数据")
    @PostMapping("/init")
    public Result initTestData() {
        // try-with-resources 语法，自动关闭连接，防止连接泄漏
        try (Connection conn = dataSource.getConnection()) {
            // 从 classpath (resources目录) 下读取 SQL 文件
            ClassPathResource resource = new ClassPathResource("sql/insert_test_data.sql");
            
            // 使用 Spring 内置的 ScriptUtils 执行脚本
            ScriptUtils.executeSqlScript(conn, resource);
            
            return Result.success("测试数据批量插入成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("执行脚本失败: " + e.getMessage());
        }
    }

    @ApiOperation("一键清空并重置数据库环境")
    @PostMapping("/reset")
    public Result resetData() {
        try (Connection conn = dataSource.getConnection()) {
            ClassPathResource resource = new ClassPathResource("sql/reset_data.sql");
            ScriptUtils.executeSqlScript(conn, resource);
            return Result.success("数据清空与重置成功，已恢复到纯净状态！");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("执行脚本失败: " + e.getMessage());
        }
    }
}