package com.admin.vo;

import lombok.Data;
import java.util.List;

@Data
public class RouteMetaVO {
    private String title;
    private String i18nKey;
    private String icon;
    private Integer order;
    private Boolean hideInMenu;
    private Boolean keepAlive;
    private List<String> roles; 
}