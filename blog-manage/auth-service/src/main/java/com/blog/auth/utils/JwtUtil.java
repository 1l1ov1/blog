package com.blog.auth.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // 使用 Keys.secretKeyFor 生成一个足够安全的密钥
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 3L; // 3 小时

    /**
     * 生成 JWT，默认有效期是3 小时
     *
     * @param str 字符串
     * @return 生成的 JWT 字符串
     */
    public static String generateToken(String str) {
        return Jwts.builder()
                .setSubject(str)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成 JWT，自定义有效期
     * @param str 字符串
     * @param expirationTime 过期时间，单位是毫秒
     * @return token令牌
     */
    public static String generateToken(String str, Long expirationTime) {
        return Jwts.builder()
                .setSubject(str)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证 JWT 并返回 Claims
     *
     * @param token JWT 字符串
     * @return 解析后的 Claims 对象
     * @throws RuntimeException 如果 JWT 验证失败
     */
    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static Long pareToken(String token) {
        Claims claims = validateToken(token);
        String userIdStr = claims.getSubject();
        return Long.parseLong(userIdStr);
    }
}
