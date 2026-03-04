package com.admin.vo;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoVO {
    private Long userId;
    private String userName;
    private String realName;
    
    private List<String> roles; 
}