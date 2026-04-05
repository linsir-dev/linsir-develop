package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * Admin Role AuthorizationManager 配置类
 * 
 * 要求用户拥有 ADMIN 角色（实际检查 ROLE_ADMIN）
 * 适用于管理员专属功能
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class AdminRoleAuthorizationManagerConfig {

    /**
     * 配置 hasRole("ADMIN") 的 AuthorizationManager
     * 
     * @param factory 注入的工厂 Bean
     * @return hasRole("ADMIN") AuthorizationManager 实例
     */
    @Bean
    public AuthorizationManager<Object> adminRoleAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.hasRole("ADMIN");
    }
}
