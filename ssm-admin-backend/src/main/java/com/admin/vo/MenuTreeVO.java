package com.admin.vo;

import java.util.List;

public class MenuTreeVO {
    private Long id;
    private String label;
    private Long parentId;
    private List<MenuTreeVO> children;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<MenuTreeVO> getChildren() { return children; }
    public void setChildren(List<MenuTreeVO> children) { this.children = children; }
}