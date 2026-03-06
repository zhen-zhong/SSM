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
        // 1. 放行 OPTIONS 预检请求（跨域必备）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        // 2. 校验格式：必须包含 Authorization 且以 Bearer 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return returnUnauthorized(response, "未检测到登录状态，请先登录");
        }

        // 截取真实的 Token 字符串
        String token = authHeader.substring(7);

        try {
            // 3. 解析 Token
            Claims claims = JwtUtils.parseToken(token);
            
            // 🌟 核心修正 1：获取用户名并存入 Request
            String username = claims.getSubject();
            if (username == null || username.isEmpty()) {
                throw new RuntimeException("Token中未包含有效用户名");
            }
            request.setAttribute("currentUsername", username);
            
            // 🌟 核心修正 2：必须与 JwtUtils.createToken 里的 key 保持一致，使用 "userId"
            Object userIdObj = claims.get("userId"); 
            if (userIdObj != null) {
                // 转为 Long 存入，供业务层使用
                request.setAttribute("currentUserId", Long.valueOf(userIdObj.toString()));
            }

            // 控制台打印调试信息（上线可删除）
            System.out.println("DEBUG: 用户 [" + username + "] 访问 " + request.getRequestURI());

            return true;
        } catch (Exception e) {
            // 捕获所有解析异常（过期、伪造、空值等）
            System.err.println("DEBUG: Token验证失败 - " + e.getMessage());
            return returnUnauthorized(response, "登录已过期，请重新登录");
        }
    }

    /**
     * 向前端输出 401 错误 JSON
     */
    private boolean returnUnauthorized(HttpServletResponse response, String message) throws Exception {
        // 必须在获取 writer 之前设置编码
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        // 组装符合 Result<T> 规范的 JSON
        String json = String.format("{\"code\":401,\"message\":\"%s\",\"data\":null}", message);
        
        try (PrintWriter out = response.getWriter()) {
            out.write(json);
            out.flush();
        }
        return false; // 拦截请求，不再下发到 Controller
    }
}