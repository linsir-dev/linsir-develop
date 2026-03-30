package com.linsir.abc.mysql.chapter01.architecture.client.auth;

import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.mapper.UserMapper;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 认证器
 * 模拟MySQL客户端层的认证授权功能
 *
 * 职责：
 * 1. 用户身份验证
 * 2. 密码校验
 * 3. 登录状态更新
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class Authenticator {

    /**
     * 用户Mapper
     */
    private final UserMapper userMapper;

    /**
     * 密码编码器
     */
    private final BCryptPasswordEncoder passwordEncoder;

    public Authenticator(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 用户认证
     *
     * @param username 用户名
     * @param password 密码
     * @param clientIp 客户端IP
     * @return 认证结果
     */
    public AuthResult authenticate(String username, String password, String clientIp) {
        // 1. 查询用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            log.warn("认证失败：用户不存在, username={}", username);
            return AuthResult.fail("用户名或密码错误");
        }

        // 2. 检查用户状态
        if (!user.isValid()) {
            log.warn("认证失败：用户已禁用, username={}", username);
            return AuthResult.fail("用户已被禁用");
        }

        // 3. 验证密码
        if (!verifyPassword(password, user.getPassword())) {
            log.warn("认证失败：密码错误, username={}", username);
            return AuthResult.fail("用户名或密码错误");
        }

        // 4. 更新登录信息
        updateLoginInfo(user, clientIp);

        log.info("认证成功：username={}, role={}", username, user.getRole());
        return AuthResult.success(user);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密密码
     * @return true-验证通过
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    /**
     * 检查权限
     *
     * @param user         用户
     * @param requiredRole 需要的角色
     * @return true-有权限
     */
    public boolean checkPermission(User user, String requiredRole) {
        if (user == null || !user.isValid()) {
            return false;
        }
        return user.hasRole(requiredRole);
    }

    /**
     * 更新登录信息
     *
     * @param user     用户
     * @param clientIp 客户端IP
     */
    private void updateLoginInfo(User user, String clientIp) {
        Integer loginCount = user.getLoginCount() == null ? 0 : user.getLoginCount();
        userMapper.updateLoginInfo(
                user.getId(),
                LocalDateTime.now(),
                clientIp,
                loginCount + 1
        );
    }

    /**
     * 认证结果
     */
    @Data
    @Builder
    public static class AuthResult {
        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 用户信息
         */
        private User user;

        /**
         * 错误信息
         */
        private String message;

        /**
         * 创建成功结果
         *
         * @param user 用户
         * @return 认证结果
         */
        public static AuthResult success(User user) {
            return AuthResult.builder()
                    .success(true)
                    .user(user)
                    .build();
        }

        /**
         * 创建失败结果
         *
         * @param message 错误信息
         * @return 认证结果
         */
        public static AuthResult fail(String message) {
            return AuthResult.builder()
                    .success(false)
                    .message(message)
                    .build();
        }
    }
}
