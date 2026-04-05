package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.authorization.DefaultAuthorizationManagerFactory;

/**
 * AuthorizationManagerFactory 配置类
 * 
 * 用于创建和管理 AuthorizationManager 实例，提供统一的授权管理器创建方式
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class AuthorizationManagerFactoryConfig {

    /**
     * 配置 AuthorizationManagerFactory
     * 
     * @return AuthorizationManagerFactory 实例
     */
    @Bean
    public AuthorizationManagerFactory<Object> authorizationManagerFactory() {
        DefaultAuthorizationManagerFactory<Object> factory = 
            new DefaultAuthorizationManagerFactory<>();
        
        // 可选：自定义角色前缀（默认为 "ROLE_"）
        // factory.setRolePrefix("MY_ROLE_");
        
        // 可选：配置角色层次结构
        // factory.setRoleHierarchy(roleHierarchy());
        
        // 可选：配置认证信任解析器
        // factory.setTrustResolver(authenticationTrustResolver);
        
        return factory;
    }
}
