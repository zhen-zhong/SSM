package com.admin.vo;

import java.util.List;

public class UserInfoVO {
    private Long userId;
    private String userName;
    private String realName;
    private List<String> roles;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
}