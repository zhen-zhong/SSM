package com.admin.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * 作用：拦截 Controller 层抛出的所有异常，并返回统一的 JSON 格式
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. 处理系统最基础的异常 Exception
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e, HttpServletRequest request) {
        // 在后台打印出真实的堆栈信息，方便我们调试
        System.err.println("发生系统异常！请求路径: " + request.getRequestURI());
        e.printStackTrace();
        
        // 给前端一个友好的提示，而不是一堆看不懂的代码
        return Result.error("服务器内部错误，请联系管理员。错误信息：" + e.getMessage());
    }

    // 2. 处理特定异常（比如：空指针异常）
    @ExceptionHandler(NullPointerException.class)
    public Result<String> handleNullPointerException(NullPointerException e) {
        return Result.error("程序操作了空对象，请检查输入数据！");
    }

    // 3. 自定义业务异常（后面我们可以建一个 ServiceException）
    // 当账号密码错误时，我们可以手动 throw 一个异常，被这里拦截并返回给前端
}