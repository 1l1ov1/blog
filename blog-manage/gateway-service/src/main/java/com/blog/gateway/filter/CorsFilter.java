package com.blog.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class CorsFilter implements GlobalFilter, Ordered {
    /**
     * 自定义过滤器处理跨域请求
     *
     * @param exchange 服务器Web交换对象，包含请求和响应
     * @param chain 过滤器链对象，用于执行下一个过滤器
     * @return 返回一个空的Mono对象，表示过滤器执行完毕
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取当前请求
        ServerHttpRequest request = exchange.getRequest();
        // 检查请求是否为跨域请求
        if (CorsUtils.isCorsRequest(request)) {
            // 获取当前响应
            ServerHttpResponse response = exchange.getResponse();
            // 获取响应头
            HttpHeaders headers = response.getHeaders();
            // 设置允许跨域请求的源为所有域
            headers.add("Access-Control-Allow-Origin", "*");
            // 设置允许的HTTP方法
            headers.add("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, OPTIONS");
            // 设置预取指令的最大时间
            headers.add("Access-Control-Max-Age", "3600");
            // 设置允许的HTTP请求头
            headers.add("Access-Control-Allow-Headers", "Authorization, Content-Type");
            // 如果请求方法为OPTIONS，则直接返回OK状态码，不进行后续处理
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }
        // 继续执行后续的过滤器
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
