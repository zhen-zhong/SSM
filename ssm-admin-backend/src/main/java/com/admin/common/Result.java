package com.admin.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;    // 状态码: 200-成功, 500-失败, 401-无权限
    private String message;  // 提示消息
    private T data;          // 数据主体

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("操作成功");
        r.setData(data);
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> r = new Result<>();
        r.setCode(500);
        r.setMessage(msg);
        return r;
    }
}