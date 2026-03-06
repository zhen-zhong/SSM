package com.admin.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysMenu {
    private Long id;
    private Long parentId;
    private String menuName;
    private String routeName;
    private String routePath;
    private String component;
    private Integer menuType;
    private String icon;
    private Integer sortNum;
    private Integer isHide;
    private Integer keepAlive;
    private Date createTime;
    private Date updateTime;
}