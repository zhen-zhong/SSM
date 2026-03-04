package com.admin.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 用户实体类
 */
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    // 🌟 与前端接口对接，保持 userName 大写 N 的兼容性
    @JsonProperty("userName")
    private String username;

    private String password;
    private String realName;
    private String phone;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    private Integer isDeleted;

    // 🌟 1. 用于【展示】：MyBatis 关联查询出的角色对象列表
    private List<SysRole> roles;

    // 🌟 2. 用于【接收】：前端新增/修改时传来的角色 ID 数组（如 [1, 2]）
    private List<Long> roleIds;

    // --- 手动生成的 Getter 和 Setter (确保编译 100% 通过) ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }

    public List<SysRole> getRoles() { return roles; }
    public void setRoles(List<SysRole> roles) { this.roles = roles; }

    public List<Long> getRoleIds() { return roleIds; }
    public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }
}