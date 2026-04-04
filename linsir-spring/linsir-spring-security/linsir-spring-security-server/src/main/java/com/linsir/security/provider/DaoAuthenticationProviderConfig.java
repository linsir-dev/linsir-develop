package com.linsir.security.provider;

import com.linsir.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 基于用户名密码的认证 Provider
 * 使用 DaoAuthenticationProvider 进行数据库用户认证
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class DaoAuthenticationProviderConfig extends DaoAuthenticationProvider {

    /**
     * 构造函数，注入 UserDetailsService
     * 
     * Spring Security 7.0+ 变化：
     * - DaoAuthenticationProvider 的构造函数必须接受 UserDetailsService
     */
    @Autowired
    public DaoAuthenticationProviderConfig(CustomUserDetailsService userDetailsService) {
        super(userDetailsService);
    }

    /**
     * 注入 PasswordEncoder
     */
    @Autowired
    @Override
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        super.setPasswordEncoder(passwordEncoder);
    }
}
