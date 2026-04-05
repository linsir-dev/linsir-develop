package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * PermitAll AuthorizationManager 配置类
 * 
 * 允许所有请求访问，不需要任何认证或授权
 * 适用于公开接口、公共资源等
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class PermitAllAuthorizationManagerConfig {

    /**
     * 配置 permitAll 的 AuthorizationManager
     * 
     * 使用工厂创建
     * 
     * @param factory 注入的工厂 Bean
     * @return permitAll AuthorizationManager 实例
     */
    @Bean
    public AuthorizationManager<Object> permitAllAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.permitAll();
    }
}
