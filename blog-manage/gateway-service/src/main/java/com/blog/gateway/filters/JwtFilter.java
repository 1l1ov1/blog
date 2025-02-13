package com.blog.gateway.filters;

import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.blog.common.context.UserContext;
import com.blog.gateway.utils.JwtUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
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
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();
    // TODO 后续需要将白名单存入数据库，到时候可以在管理员后台进行动态的设置
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/**"
    );
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /**
     * 自定义网关过滤器，用于处理每个请求
     *
     * @param exchange 服务器Web交换对象，包含请求和响应
     * @param chain 过滤器链对象，用于将控制权转交给下一个过滤器或最终的目标处理程序
     * @return Mono<Void> 表示异步处理完成的信号
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取当前请求
        ServerHttpRequest request = exchange.getRequest();
        // 检查请求路径是否在白名单内，如果在，则直接继续执行请求
        if (isInWhiteList(request.getPath().value())) {
            return chain.filter(exchange);
        }
        // 尝试从请求头中获取授权信息
        String authorizationHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
        String token = null;
        // 检查授权头是否符合预期格式，并提取令牌
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            token = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
        }
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

    /**
     * 检查给定路径是否在白名单中
     * 该方法使用AntPathMatcher来支持模式匹配，以确定路径是否与白名单中的任何模式匹配
     *
     * @param path 待检查的路径
     * @return 如果路径在白名单中，则返回true；否则返回false
     */
    private boolean isInWhiteList(String path) {
        if (StrUtil.isBlank(path)) {
            return false;
        }
        // 遍历白名单中的所有路径模式
        for (String whiteListPath : WHITE_LIST) {
            // 使用AntPathMatcher检查当前路径是否与白名单中的模式匹配
            if (antPathMatcher.match(whiteListPath, path)) {
                // 如果匹配成功，返回true，表示路径在白名单中
                return true;
            }
        }
        // 如果没有找到匹配的模式，返回false，表示路径不在白名单中
        return false;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
