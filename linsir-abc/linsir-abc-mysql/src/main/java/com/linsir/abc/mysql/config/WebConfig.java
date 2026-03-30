package com.linsir.abc.mysql.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 *
 * 职责：
 * 1. 配置跨域支持
 * 2. 配置拦截器（可选）
 * 3. 配置视图解析器（可选）
 *
 * @author linsir
 * @since 1.0.0
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 配置跨域访问
     *
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许访问的源
                .allowedOrigins("*")
                // 允许的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的头部
                .allowedHeaders("*")
                // 是否允许携带凭证
                .allowCredentials(false)
                // 预检请求缓存时间
                .maxAge(3600);
    }
}
