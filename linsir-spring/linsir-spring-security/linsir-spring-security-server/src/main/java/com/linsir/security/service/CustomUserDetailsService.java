package com.linsir.security.service;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 自定义 UserDetailsService 实现
 * 用于从数据库或其他数据源加载用户信息
 * 
 * 测试账号说明：
 * - admin: 正常用户，密码 admin123
 * - user: 正常用户，密码 user123
 * - locked: 账户被锁定，密码 locked123
 * - disabled: 账户被禁用，密码 disabled123
 * - expired: 账户已过期，密码 expired123
 * - credentials_expired: 密码已过期，密码 creds123
 *
 * @author linsir
 * @version 1.0.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * 根据用户名加载用户信息
     * 
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 用户不存在时抛出
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 模拟从数据库查询用户
        // 实际应用中应该从数据库查询
        
        // 1. 先判断用户是否存在，定义用户是否存在标志
        boolean userExists = false;
        
        // 2. 检查用户名是否在已知用户列表中
        if ("admin".equals(username) || "user".equals(username) || 
            "locked".equals(username) || "disabled".equals(username) || 
            "expired".equals(username) || "credentials_expired".equals(username)) {
            userExists = true;
        }
        
        // 3. 如果用户不存在，直接抛出异常
        if (!userExists) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        
        // 4. 用户存在，返回对应的用户信息（包括各种状态）
        // 正常用户
        if ("admin".equals(username)) {
            return User.builder()
                    .username("admin")
                    .password("{noop}admin123") // {noop} 表示明文密码，实际生产环境应使用 {bcrypt} 加密
                    .roles("ADMIN", "USER")
                    .build();
        } else if ("user".equals(username)) {
            return User.builder()
                    .username("user")
                    .password("{noop}user123") // {noop} 表示明文密码
                    .roles("USER")
                    .build();
        }
        // 账户被锁定的用户
        else if ("locked".equals(username)) {
            return User.builder()
                    .username("locked")
                    .password("{noop}locked123")
                    .roles("USER")
                    .accountLocked(true) // 账户锁定
                    .build();
        }
        // 账户被禁用的用户
        else if ("disabled".equals(username)) {
            return User.builder()
                    .username("disabled")
                    .password("{noop}disabled123")
                    .roles("USER")
                    .disabled(true) // 账户禁用
                    .build();
        }
        // 账户已过期的用户
        else if ("expired".equals(username)) {
            return User.builder()
                    .username("expired")
                    .password("{noop}expired123")
                    .roles("USER")
                    .accountExpired(true) // 账户过期
                    .build();
        }
        // 密码已过期的用户
        else if ("credentials_expired".equals(username)) {
            return User.builder()
                    .username("credentials_expired")
                    .password("{noop}creds123")
                    .roles("USER")
                    .credentialsExpired(true) // 密码过期
                    .build();
        }
        
        // 理论上不会到这里，因为前面已经判断过用户是否存在
        throw new UsernameNotFoundException("用户不存在：" + username);
    }
}
