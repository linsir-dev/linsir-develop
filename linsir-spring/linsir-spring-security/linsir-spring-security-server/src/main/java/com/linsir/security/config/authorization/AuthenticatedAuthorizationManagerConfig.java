package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * Authenticated AuthorizationManager 配置类
 * 
 * 要求用户已认证（包括 remember-me）
 * 适用于需要登录才能访问的资源
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class AuthenticatedAuthorizationManagerConfig {

    /**
     * 配置 authenticated 的 AuthorizationManager
     * 
     * @param factory 注入的工厂 Bean
     * @return authenticated AuthorizationManager 实例
     */
    @Bean
    public AuthorizationManager<Object> authenticatedAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.authenticated();
    }
}
