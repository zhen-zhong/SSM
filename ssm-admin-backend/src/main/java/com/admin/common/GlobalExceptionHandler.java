package com.admin.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 作用：拦截 Controller 层抛出的所有异常，并严格按照前端 Soybean Admin 的契约返回 JSON
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🌟 1. 核心新增：精准处理我们手动抛出的 RuntimeException (业务异常)
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        System.err.println("拦截到业务异常！请求路径: " + request.getRequestURI() + "，原因: " + e.getMessage());
        String errorMsg = e.getMessage();
        
        // 契约对齐：判断是否为 Token 失效、未登录相关的异常，如果是，返回 401
        if (errorMsg != null && (errorMsg.contains("Token") || errorMsg.contains("登录"))) {
            return Result.error(401, errorMsg);
        }
        
        // 其他常规业务报错（如“用户名或密码错误”），返回 500，并剥离掉原先冗长的前缀
        return Result.error(500, errorMsg);
    }

    // 2. 兜底处理：抓取真正意料之外的系统异常 Exception
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        // 在后台打印出真实的堆栈信息，方便我们调试排错
        System.err.println("发生系统异常！请求路径: " + request.getRequestURI());
        e.printStackTrace();
        
        // 给前端一个友好的兜底提示
        return Result.error(500, "服务器内部发生未知错误，请联系管理员。");
    }

    // 3. 处理特定代码级异常（比如：空指针异常）
    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        System.err.println("发生空指针异常！请求路径: " + request.getRequestURI());
        e.printStackTrace();
        return Result.error(500, "程序运行出现空引用，请联系后端开发人员排查！");
    }
}