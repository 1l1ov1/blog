package com.blog.auth.config;

import com.blog.common.context.UserContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.reactive.CorsUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;


@Configuration
public class AuthConfig {

//    /**
//     * 配置Security过滤链
//     * <p>
//     * 该方法定义了如何配置WebFlux中的Security过滤链，以保护WebFlux应用免受未经授权的访问
//     * 它特别关注于跨域资源共享（CORS）配置和路径级别的安全授权
//     *
//     * @param http ServerHttpSecurity实例，用于配置安全属性
//     * @return SecurityWebFilterChain实例，代表配置好的安全过滤链
//     */
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                // 禁用跨站请求伪造（CSRF）保护，因为CORS配置已经足够
//                .csrf().disable()
//                // 配置跨域资源共享（CORS），允许从不同的域进行请求
//                .cors().and()
//                // 配置路径级别的安全授权
//                .authorizeExchange()
//                // 允许所有与'/api/auth/**'路径匹配的请求无需认证
//                .pathMatchers("/auth/**").permitAll()
//                // 其他所有交换都需要经过身份验证
//                .anyExchange().authenticated()
//                .and();
//        // 构建并返回配置好的Security过滤链
//        return http.build();
//    }

}
