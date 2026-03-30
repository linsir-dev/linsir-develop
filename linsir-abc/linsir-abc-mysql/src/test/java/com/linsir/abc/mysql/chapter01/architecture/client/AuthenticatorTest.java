package com.linsir.abc.mysql.chapter01.architecture.client;

import com.linsir.abc.mysql.chapter01.architecture.client.auth.Authenticator;
import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 认证器单元测试
 *
 * 测试范围：
 * 1. 用户认证
 * 2. 密码验证
 * 3. 权限检查
 *
 * @author linsir
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证器测试")
class AuthenticatorTest {

    @Mock
    private UserMapper userMapper;

    private Authenticator authenticator;

    @BeforeEach
    void setUp() {
        authenticator = new Authenticator(userMapper);
    }

    @Test
    @DisplayName("测试认证成功")
    void testAuthenticate_Success() {
        // Given
        String username = "zhangsan";
        String password = "password123";
        String clientIp = "192.168.1.100";

        User user = User.builder()
                .id(1L)
                .username(username)
                .password(authenticator.encodePassword(password))
                .status(1)
                .role("USER")
                .build();

        when(userMapper.findByUsername(username)).thenReturn(user);

        // When
        Authenticator.AuthResult result = authenticator.authenticate(username, password, clientIp);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getUser());
        assertEquals(username, result.getUser().getUsername());
        verify(userMapper).updateLoginInfo(any(), any(), any(), any());
    }

    @Test
    @DisplayName("测试认证失败 - 用户不存在")
    void testAuthenticate_UserNotFound() {
        // Given
        String username = "nonexistent";
        String password = "password123";
        String clientIp = "192.168.1.100";

        when(userMapper.findByUsername(username)).thenReturn(null);

        // When
        Authenticator.AuthResult result = authenticator.authenticate(username, password, clientIp);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("测试认证失败 - 用户被禁用")
    void testAuthenticate_UserDisabled() {
        // Given
        String username = "disabled";
        String password = "password123";
        String clientIp = "192.168.1.100";

        User user = User.builder()
                .id(1L)
                .username(username)
                .password(authenticator.encodePassword(password))
                .status(0) // 禁用
                .role("USER")
                .build();

        when(userMapper.findByUsername(username)).thenReturn(user);

        // When
        Authenticator.AuthResult result = authenticator.authenticate(username, password, clientIp);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("测试认证失败 - 密码错误")
    void testAuthenticate_WrongPassword() {
        // Given
        String username = "zhangsan";
        String correctPassword = "password123";
        String wrongPassword = "wrongpassword";
        String clientIp = "192.168.1.100";

        User user = User.builder()
                .id(1L)
                .username(username)
                .password(authenticator.encodePassword(correctPassword))
                .status(1)
                .role("USER")
                .build();

        when(userMapper.findByUsername(username)).thenReturn(user);

        // When
        Authenticator.AuthResult result = authenticator.authenticate(username, wrongPassword, clientIp);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getMessage());
    }

    @Test
    @DisplayName("测试密码加密")
    void testEncodePassword() {
        // Given
        String rawPassword = "password123";

        // When
        String encodedPassword = authenticator.encodePassword(rawPassword);

        // Then
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(authenticator.verifyPassword(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("测试密码验证成功")
    void testVerifyPassword_Success() {
        // Given
        String rawPassword = "password123";
        String encodedPassword = authenticator.encodePassword(rawPassword);

        // When
        boolean result = authenticator.verifyPassword(rawPassword, encodedPassword);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试密码验证失败")
    void testVerifyPassword_Fail() {
        // Given
        String rawPassword = "password123";
        String wrongPassword = "wrongpassword";
        String encodedPassword = authenticator.encodePassword(rawPassword);

        // When
        boolean result = authenticator.verifyPassword(wrongPassword, encodedPassword);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试权限检查 - 有权限")
    void testCheckPermission_HasPermission() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("admin")
                .status(1)
                .role("ADMIN")
                .build();

        // When
        boolean result = authenticator.checkPermission(user, "ADMIN");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("测试权限检查 - 无权限")
    void testCheckPermission_NoPermission() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("user")
                .status(1)
                .role("USER")
                .build();

        // When
        boolean result = authenticator.checkPermission(user, "ADMIN");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试权限检查 - 用户为空")
    void testCheckPermission_NullUser() {
        // When
        boolean result = authenticator.checkPermission(null, "ADMIN");

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("测试权限检查 - 用户被禁用")
    void testCheckPermission_DisabledUser() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("disabled")
                .status(0)
                .role("ADMIN")
                .build();

        // When
        boolean result = authenticator.checkPermission(user, "ADMIN");

        // Then
        assertFalse(result);
    }
}
