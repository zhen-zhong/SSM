package com.admin.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysUser {
    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
}