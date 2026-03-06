package com.admin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RouteVO {
    private String name;
    private String path;
    private String component;
    private RouteMetaVO meta;
    private List<RouteVO> children;
}