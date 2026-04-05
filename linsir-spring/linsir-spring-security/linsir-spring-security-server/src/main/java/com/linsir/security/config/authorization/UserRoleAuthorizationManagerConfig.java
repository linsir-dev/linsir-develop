package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * User Role AuthorizationManager 配置类
 * 
 * 要求用户拥有 USER 角色（实际检查 ROLE_USER）
 * 适用于普通用户功能
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class UserRoleAuthorizationManagerConfig {

    /**
     * 配置 hasRole("USER") 的 AuthorizationManager
     * 
     * @param factory 注入的工厂 Bean
     * @return hasRole("USER") AuthorizationManager 实例
     */
    @Bean
    public AuthorizationManager<Object> userRoleAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.hasRole("USER");
    }
}
