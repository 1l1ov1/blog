package com.blog.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    /**
     * 配置自定义的路由定位器
     * 该方法使用RouteLocatorBuilder来构建路由规则，用于在Spring Cloud Gateway中定义请求如何被路由到不同的微服务
     *
     * @param builder RouteLocatorBuilder类型的参数，用于构建路由规则
     * @return 返回一个RouteLocator实例，定义了一组路由规则
     */
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        // 构建路由规则
        return builder.routes()
                // 配置用户服务的路由
                .route("user-service", r -> r.path("/api/users/**")
                        .uri("lb://user-service"))
                // 配置文章服务的路由
                .route("article-service", r -> r.path("/api/articles/**")
                        .uri("lb://article-service"))
                // 配置认证服务的路由
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://auth-service"))
                // 结束路由规则配置并构建
                .build();
    }

    /**
     * 配置Security过滤链
     *
     * 该方法定义了如何配置WebFlux中的Security过滤链，以保护WebFlux应用免受未经授权的访问
     * 它特别关注于跨域资源共享（CORS）配置和路径级别的安全授权
     *
     * @param http ServerHttpSecurity实例，用于配置安全属性
     * @return SecurityWebFilterChain实例，代表配置好的安全过滤链
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                // 禁用跨站请求伪造（CSRF）保护，因为CORS配置已经足够
                .csrf().disable()
                // 配置跨域资源共享（CORS），允许从不同的域进行请求
                .cors().and()
                // 配置路径级别的安全授权
                .authorizeExchange()
                // 允许所有与'/api/auth/**'路径匹配的请求无需认证
                .pathMatchers("/api/auth/**").permitAll()
                // 其他所有交换都需要经过身份验证
                .anyExchange().authenticated()
                // 在特定过滤器之前添加自定义的CORS过滤器
                .and()
                .addFilterBefore(corsFilter(), SecurityWebFiltersOrder.REACTOR_CONTEXT);
        // 构建并返回配置好的Security过滤链
        return http.build();
    }

    /**
     * 创建跨域请求过滤器
     * 该方法用于定义一个WebFilter，主要用于处理跨域请求（CORS）
     * 跨域请求是当客户端从一个域请求资源时，这些资源位于另一个不同的域上
     * 此过滤器的目的是在响应中添加适当的CORS头，以允许来自不同域的请求
     *
     * @return WebFilter 返回一个WebFilter，用于处理跨域请求
     */
    private WebFilter corsFilter() {
        return (ServerWebExchange ctx, WebFilterChain chain) -> {
            // 获取当前请求
            ServerHttpRequest request = ctx.getRequest();
            // 检查请求是否为跨域请求
            if (CorsUtils.isCorsRequest(request)) {
                // 获取当前响应
                ServerHttpResponse response = ctx.getResponse();
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
            return chain.filter(ctx);
        };
    }
}
