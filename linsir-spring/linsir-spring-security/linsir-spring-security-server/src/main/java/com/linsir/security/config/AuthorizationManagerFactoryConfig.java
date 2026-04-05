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
        
        // 配置角色前缀为 "ROLE_"
        // 当使用 hasRole("ADMIN") 时，实际检查的权限是 "ROLE_ADMIN"
        // 如果设置为空字符串 ""，则 hasRole("ADMIN") 检查的就是 "ADMIN"
        factory.setRolePrefix("ROLE_");
        
        // 可选：配置角色层次结构
        // 作用：定义角色之间的继承关系，简化授权配置
        // 示例：配置 ADMIN 角色自动拥有 USER 角色的权限
        // @Bean
        // public RoleHierarchy roleHierarchy() {
        //     return RoleHierarchyImpl.withDefaultRolePrefix()
        //         .role("ADMIN").implies("USER")      // ADMIN 包含 USER
        //         .role("USER").implies("GUEST")      // USER 包含 GUEST
        //         .build();
        // }
        // 效果：拥有 ROLE_ADMIN 的用户自动拥有 ROLE_USER 和 ROLE_GUEST 权限
        // factory.setRoleHierarchy(roleHierarchy());
        
        // 可选：配置认证信任解析器
        // 作用：自定义认证状态的判断逻辑（匿名、完全认证、remember-me）
        // 默认使用 AuthenticationTrustResolverImpl
        // 自定义示例：
        // @Bean
        // public AuthenticationTrustResolver trustResolver() {
        //     return new AuthenticationTrustResolverImpl(
        //         CustomAnonymousToken.class,    // 自定义匿名 Token
        //         CustomRememberMeToken.class    // 自定义 RememberMe Token
        //     );
        // }
        // 适用场景：需要自定义认证 Token 类型或修改认证状态判断逻辑时使用
        // factory.setTrustResolver(authenticationTrustResolver);
        
        return factory;
    }
}
