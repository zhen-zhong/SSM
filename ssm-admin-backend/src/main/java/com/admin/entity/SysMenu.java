package com.admin.entity;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SysMenu {
    private Long id;
    private Long parentId;
    private String menuName;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private String menuType; // M目录 C菜单 F按钮
    private Integer orderNum;
    private Date createTime;

    // 非数据库字段，用于在 Java 中组装树形结构
    private List<SysMenu> children;
}