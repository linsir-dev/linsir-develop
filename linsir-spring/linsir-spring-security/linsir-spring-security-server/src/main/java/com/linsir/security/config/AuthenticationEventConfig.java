package com.linsir.security.config;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;

import java.util.Collections;
import java.util.Map;

/**
 * 认证事件发布器配置
 * 配置认证事件的发布逻辑和异常映射
 * 
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class AuthenticationEventConfig {

    /**
     * 配置认证事件发布器
     * 
     * @param applicationEventPublisher Spring 应用事件发布器
     * @return 认证事件发布器
     */
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        
        DefaultAuthenticationEventPublisher publisher = 
            new DefaultAuthenticationEventPublisher(applicationEventPublisher);
        
        // 配置额外的异常映射（如果需要）
        // 默认映射已包含：
        // - BadCredentialsException -> AuthenticationFailureBadCredentialsEvent
        // - UsernameNotFoundException -> AuthenticationFailureBadCredentialsEvent
        // - AccountExpiredException -> AuthenticationFailureExpiredEvent
        // - DisabledException -> AuthenticationFailureDisabledEvent
        // - LockedException -> AuthenticationFailureLockedEvent
        // - CredentialsExpiredException -> AuthenticationFailureCredentialsExpiredEvent
        // - AuthenticationServiceException -> AuthenticationFailureServiceExceptionEvent
        
        // 如果有自定义异常，可以通过以下方式添加映射：
        // 注意：Map 不能为空，如果不需要额外映射，不要调用 setAdditionalExceptionMappings
        // Map<Class<? extends AuthenticationException, 
        //     Class<? extends AbstractAuthenticationFailureEvent>> additionalMappings = 
        //         new HashMap<>();
        // additionalMappings.put(CustomException.class, CustomFailureEvent.class);
        // publisher.setAdditionalExceptionMappings(additionalMappings);
        
        return publisher;
    }
}
