package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security 认证配置类
 * 专门负责认证相关的配置，包括 AuthenticationManager 和 PasswordEncoder
 * 
 * 注意：
 * 1. 具体的 AuthenticationProvider 实现已移至 provider 包中作为独立组件
 * 2. 使用 DelegatingPasswordEncoder 支持多种密码编码格式
 *    - {noop}: 明文密码
 *    - {bcrypt}: BCrypt 加密密码
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class AuthenticationConfig {

    /**
     * 配置 AuthenticationManager
     * 
     * Spring Security 7.0 变化：
     * - 通过 AuthenticationConfiguration 获取 AuthenticationManager
     * - 会自动使用已配置的 AuthenticationProvider
     * 
     * @param config Spring Security 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 配置异常
     */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * 配置密码编码器
     * 使用 DelegatingPasswordEncoder 支持多种编码格式
     * 
     * 默认使用 bcrypt 编码，同时支持 {noop} 明文密码
     * 
     * @return PasswordEncoder 实例
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("noop", org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance());
        
        DelegatingPasswordEncoder delegatingPasswordEncoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return delegatingPasswordEncoder;
    }
}
