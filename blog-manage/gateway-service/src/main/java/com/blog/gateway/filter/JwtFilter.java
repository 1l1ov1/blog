package com.blog.gateway.filter;

import com.blog.common.context.UserContext;
import com.blog.gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Component
public class JwtFilter implements GlobalFilter, Ordered {
    // 定义Token前缀
    private static final String TOKEN_PREFIX = "Bearer ";
    // 定义Token前缀长度
    private static final int TOKEN_PREFIX_LENGTH = TOKEN_PREFIX.length();
    // 白名单 TODO 后续会改为从数据库中获取
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/**"
    );
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取当前请求
        ServerHttpRequest request = exchange.getRequest();
        if (isWhiteList(request.getPath().value())) {
            return chain.filter(exchange);
        }
        // 从请求中提取JWT令牌
        String token = extractToken(request);
        // 检查令牌是否为空或无效
        if (token == null || JwtUtil.validateToken(token) == null) {
            // 获取当前响应
            ServerHttpResponse response = exchange.getResponse();
            // 设置未授权状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            // 结束响应，返回错误信息
            return response.setComplete();
        }
        // 解析令牌中的用户ID，并设置到用户上下文中
        UserContext.setUserId(JwtUtil.pareToken(token));
        // 继续处理链中的下一个过滤器或处理函数
        return chain.filter(exchange);
    }
    private String extractToken(ServerHttpRequest request) {
        String authorizationHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            return authorizationHeader.substring(TOKEN_PREFIX_LENGTH);
        }
        return null;
    }

    private boolean isWhiteList(String path) {
        for (String whiteListPath : WHITE_LIST) {
            if (path.startsWith(whiteListPath)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int getOrder() {
        return 0;
    }
}
