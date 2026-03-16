package com.admin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
@ApiModel("项目实体类")
public class SysProject {
    
    @ApiModelProperty("项目ID")
    private Long id;

    @ApiModelProperty("项目名称")
    private String projectName;

    @ApiModelProperty("状态(0未开始 1进行中 2已完成 3挂起)")
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("预计结束日期")
    private Date estimatedEndDate;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty("实际结束日期")
    private Date actualEndDate;

    @ApiModelProperty("备注")
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    // ----- 业务扩展字段 (非数据库实际字段) -----
    
    @ApiModelProperty("负责人ID集合(前端多选传参用)")
    private List<Long> managerIds;

    @ApiModelProperty("负责人名称拼接字符串(前端列表展示用)")
    private String managerNames;
    
    @ApiModelProperty("用于接收SQL聚合的ID字符串")
    private String managerIdsStr;
}