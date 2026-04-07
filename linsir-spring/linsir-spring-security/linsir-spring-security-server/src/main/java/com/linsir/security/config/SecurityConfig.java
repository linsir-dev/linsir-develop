package com.linsir.security.config;

import com.linsir.security.handler.CustomAccessDeniedHandler;
import com.linsir.security.handler.CustomAuthenticationEntryPoint;
import com.linsir.security.handler.CustomLoginFailureHandler;
import com.linsir.security.handler.CustomLoginSuccessHandler;
import com.linsir.security.handler.CustomLogoutSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

/**
 * Spring Security 安全过滤器链配置类
 * 主要负责 HTTP 安全策略、表单登录、登出等配置
 * 
 * 前后端分离配置：
 * - 登录接口：POST /api/auth/login
 * - 登出接口：POST /api/auth/logout
 * - 未认证返回 401，而不是重定向到登录页
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomLoginSuccessHandler loginSuccessHandler;
    private final CustomLoginFailureHandler loginFailureHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final AuthorizationManager<RequestAuthorizationContext> ipAuthorizationManager;

    public SecurityConfig(CustomLoginSuccessHandler loginSuccessHandler,
                         CustomLoginFailureHandler loginFailureHandler,
                         CustomLogoutSuccessHandler logoutSuccessHandler,
                         CustomAuthenticationEntryPoint authenticationEntryPoint,
                         CustomAccessDeniedHandler accessDeniedHandler,
                         AuthorizationManager<RequestAuthorizationContext> ipAuthorizationManager) {
        this.loginSuccessHandler = loginSuccessHandler;
        this.loginFailureHandler = loginFailureHandler;
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.ipAuthorizationManager = ipAuthorizationManager;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（前后端分离通常使用 JWT 或 Session+Token 方式）
            .csrf(csrf -> csrf.disable())
            // 配置 X-Frame-Options，允许 iframe 加载同源页面
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions
                    .sameOrigin()
                )
            )
            .authorizeHttpRequests(auth -> auth
                // 所有请求都通过 IP 授权管理器
                // 公开路径：IP 授权通过即可
                // 其他路径：IP 授权通过后还需要认证
                .anyRequest().access(ipAuthorizationManager)
            )
            // 异常处理配置
            .exceptionHandling(exception -> exception
                // 未认证时返回 JSON 错误信息
                .authenticationEntryPoint(authenticationEntryPoint)
                // 未授权时返回 JSON 错误信息
                .accessDeniedHandler(accessDeniedHandler)
            )
            // Session 管理配置
            .sessionManagement(session -> session
                // 总是创建新的 Session（用于登录）
                .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.IF_REQUIRED)
            )
            // 启用表单登录（前后端分离模式）
            .formLogin(form -> form
                // 登录请求处理 URL
                .loginProcessingUrl("/api/auth/login")
                // 登录成功处理器
                .successHandler(loginSuccessHandler)
                // 登录失败处理器
                .failureHandler(loginFailureHandler)
                // 不跳转到登录页面
                .permitAll()
            )
            // 启用登出（前后端分离模式）
            .logout(logout -> logout
                // 登出请求 URL
                .logoutUrl("/api/auth/logout")
                // 登出成功处理器
                .logoutSuccessHandler(logoutSuccessHandler)
                // 使 Session 失效
                .invalidateHttpSession(true)
                // 清除 SecurityContext
                .clearAuthentication(true)
                // 删除 JSESSIONID Cookie
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
