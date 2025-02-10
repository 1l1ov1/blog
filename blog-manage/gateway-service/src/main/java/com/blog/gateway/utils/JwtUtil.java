package com.blog.gateway.utils;


import cn.hutool.jwt.JWTUtil;
import com.blog.common.enums.ErrorCode;
import com.blog.common.exception.TokenException;
import com.blog.common.exception.UserException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = "vei@i#ihfoiq(*&^fhwieo!@)$_+f";
    private static final Long EXPIRATION_TIME = 1000L * 60 * 60 * 3L; // 3 小时
    // 将密钥转换为 Key 对象
    private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    /**
     * 生成 JWT
     *
     * @param userId 用户Id
     * @return 生成的 JWT 字符串
     */
    public static String generateToken(Long userId) {
        if (userId == null || userId <= 0) {
            throw new UserException(ErrorCode.INVALID_USER_ID);
        }
        return Jwts.builder()
                .setSubject(userId + "")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 1 天
                .signWith(key)
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
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenException(ErrorCode.TOKEN_INVALID);
        } catch (MalformedJwtException e) {
            throw new TokenException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    public static Long pareToken(String token) {
        try {
            Claims claims = validateToken(token);
            String userIdStr = claims.getSubject();
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new UserException(ErrorCode.INVALID_USER_ID_FORMAT);
        }
    }
}
