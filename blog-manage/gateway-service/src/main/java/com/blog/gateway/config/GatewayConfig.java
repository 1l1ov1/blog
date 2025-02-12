package com.blog.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
                .route("user-service", r -> r.path("/users/**")
                        .uri("lb://user-service"))
                // 配置文章服务的路由
                .route("article-service", r -> r.path("/articles/**")
                        .uri("lb://article-service"))
                // 配置认证服务的路由
                .route("auth-service", r -> r.path("/auth/**")
                        .uri("lb://auth-service"))
                // 结束路由规则配置并构建
                .build();
    }
}
