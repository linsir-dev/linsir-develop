package com.linsir.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（仅用于测试）
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问测试接口
                .requestMatchers("/api/security-context/**").permitAll()
                // 允许匿名访问 hello 接口
                .requestMatchers("/api/hello").permitAll()
                // 其他请求需要认证
                .anyRequest().authenticated()
            )
            // 启用表单登录
            .formLogin(form -> form.permitAll())
            // 启用 HTTP Basic 认证
            .httpBasic(basic -> {});

        return http.build();
    }
}
