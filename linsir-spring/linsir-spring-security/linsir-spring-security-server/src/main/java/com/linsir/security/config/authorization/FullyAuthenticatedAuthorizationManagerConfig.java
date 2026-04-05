package com.linsir.security.config.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagerFactory;

/**
 * FullyAuthenticated AuthorizationManager 配置类
 * 
 * 要求用户完全认证（不包括 remember-me）
 * 适用于敏感操作，如修改密码、支付等
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class FullyAuthenticatedAuthorizationManagerConfig {

    /**
     * 配置 fullyAuthenticated 的 AuthorizationManager
     * 
     * @param factory 注入的工厂 Bean
     * @return fullyAuthenticated AuthorizationManager 实例
     */
    @Bean
    public AuthorizationManager<Object> fullyAuthenticatedAuthorizationManager(
            AuthorizationManagerFactory<Object> factory) {
        return factory.fullyAuthenticated();
    }
}
