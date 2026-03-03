package com.admin.entity;

import lombok.Data;
import java.util.Date;

@Data
public class SysLog {
    private Long id;
    private String username;
    private String module;
    private String action;
    private String method;
    private String params;
    private String ip;
    private Long executeTime;
    private Date createTime;
}