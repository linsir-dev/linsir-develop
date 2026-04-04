package com.linsir.security.provider;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌认证 Provider
 * 用于基于 JWT Token 的认证
 * 
 * 使用示例：
 * 1. 添加 jjwt 或 nimbus-jose-jwt 依赖
 * 2. 实现 JWT 解析和验证逻辑
 * 3. 在 AuthenticationConfig 中注册此 Provider
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    /**
     * 执行 JWT 认证
     * 
     * @param authentication 包含 JWT token 的认证对象
     * @return 认证成功后的 Authentication 对象
     * @throws AuthenticationException 认证失败时抛出
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 获取 JWT token（通常从 Header 中获取）
        String token = authentication.getCredentials().toString();
        
        // TODO: 实现 JWT 验证逻辑
        // 1. 验证 token 签名
        // 2. 检查 token 是否过期
        // 3. 解析 token 中的用户信息
        
        // 示例：模拟验证成功
        if (token == null || token.isEmpty()) {
            throw new BadCredentialsException("JWT token 不能为空");
        }
        
        // 解析 token 获取用户名（示例）
        String username = "jwtuser";
        
        // 解析 token 获取权限（示例）
        java.util.List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        
        // 创建认证成功的 Authentication 对象
        return new UsernamePasswordAuthenticationToken(
            username,
            token,
            authorities
        );
    }

    /**
     * 指定此 Provider 支持的 Authentication 类型
     * 
     * @param authentication Authentication 类型
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // 支持 UsernamePasswordAuthenticationToken 类型的认证
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
