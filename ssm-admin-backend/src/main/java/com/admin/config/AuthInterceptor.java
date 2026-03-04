package com.admin.config;

import com.admin.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        // 2. 校验格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return returnUnauthorized(response, "未检测到登录状态，请先登录");
        }

        String token = authHeader.substring(7);

        try {
            // 3. 解析 Token
            Claims claims = JwtUtils.parseToken(token);
            request.setAttribute("currentUsername", claims.getSubject());
            
            Object userId = claims.get("id"); // 或者 "userId"，看你存的是什么
            if (userId != null) {
                request.setAttribute("currentUserId", Long.valueOf(userId.toString()));
            }

            return true;
        } catch (Exception e) {
            // 🌟 核心修复：捕获解析异常，返回 401 而不是抛出 500 异常
            return returnUnauthorized(response, "登录已过期，请重新登录");
        }
    }

    /**
     * 辅助方法：直接向前端输出 401 状态的 JSON
     * 这样 SoybeanAdmin 就能识别并自动清除本地 Token
     */
    private boolean returnUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        // 这里的 401 必须和前端 .env 里的 VITE_SERVICE_LOGOUT_CODES=401 对应
        String json = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", message);
        out.write(json);
        out.flush();
        out.close();
        return false; // 拦截请求
    }
}