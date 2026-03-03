package com.admin.config;

import com.admin.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 预检请求 (解决跨域时的拦截问题)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从 HTTP 请求头中获取 Token
        String token = request.getHeader("Authorization");

        // 3. 如果没有 Token，直接抛出异常
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("未检测到登录状态，请先登录");
        }

        // 4. 调用你写的 JwtUtils 解析 Token
        // 你的 parseToken 方法内部如果解析失败会自动抛出异常，所以这里直接拿结果就行
        Claims claims = JwtUtils.parseToken(token);

        // 5. 解析成功，将核心用户信息存入 request 作用域
        String username = claims.getSubject();
        request.setAttribute("currentUsername", username);

        // 6. 放行请求，继续执行后续的 Controller
        return true;
    }
}