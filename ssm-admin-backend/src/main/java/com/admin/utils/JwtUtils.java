package com.admin.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 1. 设置 Token 过期时间（24小时）
    private static final long EXPIRE = 1000L * 60 * 60 * 24 * 30;
    private static final String SECRET = "Admin_SSM_React_2026_Secret_Key";

    /**
     * 生成 Token
     * @param userId   用户ID
     * @param username 用户名
     * @return 加密后的字符串
     */
    public static String createToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims)           // 设置自定义 claims
                .setSubject(username)        // 设置标准 claims 里的主题 (Subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE))
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    /**
     * 解析并验证 Token
     * @param token 客户端传来的字符串
     * @return 解析出的 Claims (包含用户名和 userId)
     */
    public static Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 如果解析失败（过期或篡改），抛出异常，会被我们的全局处理器捕获
            throw new RuntimeException("Token 无效或已过期，请重新登录");
        }
    }
}