package com.blog.common.config;

import com.blog.common.exception.handle.HandleGlobalException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalExceptionConfig {
    // 由于common-service不是启动类，所以不能扫描到这个全局异常捕获
    // 所以要将其注册到容器中
    @Bean
    public HandleGlobalException globalException() {
        return new HandleGlobalException();
    }
}
