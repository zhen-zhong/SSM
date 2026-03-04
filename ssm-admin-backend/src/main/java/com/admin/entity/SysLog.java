package com.admin.entity;

import java.util.Date;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Long getExecuteTime() { return executeTime; }
    public void setExecuteTime(Long executeTime) { this.executeTime = executeTime; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}