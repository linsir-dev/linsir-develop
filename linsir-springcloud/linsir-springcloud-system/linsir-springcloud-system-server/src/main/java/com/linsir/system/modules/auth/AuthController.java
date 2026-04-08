package com.linsir.system.modules.auth;

import com.linsir.system.core.result.CommonResult;
import com.linsir.system.modules.auth.dto.LoginRequest;
import com.linsir.system.modules.auth.dto.LoginResponse;
import com.linsir.system.modules.rbac.entity.User;
import com.linsir.system.modules.rbac.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 认证控制器
 * 处理登录、刷新Token等认证相关请求
 *
 * @author linsir
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final UserMapper userMapper;

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public CommonResult<LoginResponse> login(@RequestBody LoginRequest request) {
        log.info("用户登录请求: username={}, loginType={}", request.getUsername(), request.getLoginType());

        try {
            // 1. 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 2. 获取用户信息
            User user = getUserByUsername(request.getUsername());
            if (user == null) {
                return CommonResult.error("用户不存在");
            }

            // 3. 生成 Access Token
            String accessToken = generateAccessToken(authentication, user, request.getDeviceId());

            // 4. 生成 Refresh Token
            String refreshToken = generateRefreshToken(authentication, user, request.getDeviceId());

            // 5. 构建响应
            LoginResponse response = LoginResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(1800L) // 30分钟
                    .userInfo(LoginResponse.UserInfo.builder()
                            .userId(user.getId())
                            .username(user.getUsername())
                            .nickname(user.getNickname())
                            .avatar(user.getAvatar())
                            .roles(getRoles(authentication))
                            .build())
                    .build();

            log.info("用户登录成功: username={}", request.getUsername());
            return CommonResult.success(response);

        } catch (BadCredentialsException e) {
            log.warn("登录失败，用户名或密码错误: username={}", request.getUsername());
            return CommonResult.error("用户名或密码错误");
        } catch (Exception e) {
            log.error("登录异常: username={}, error={}", request.getUsername(), e.getMessage(), e);
            return CommonResult.error("登录失败，请稍后重试");
        }
    }

    /**
     * 生成 Access Token
     */
    private String generateAccessToken(Authentication authentication, User user, String deviceId) {
        Instant now = Instant.now();

        // 构建 JWT Claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .issuedAt(now)
                .expiresAt(now.plus(30, ChronoUnit.MINUTES))
                .subject(String.valueOf(user.getId()))
                .id(UUID.randomUUID().toString())
                .claim("username", user.getUsername())
                .claim("nickname", user.getNickname())
                .claim("roles", getRoles(authentication))
                .claim("login_type", "password")
                .claim("device_id", deviceId != null ? deviceId : "unknown")
                .claim("token_type", "access_token")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * 生成 Refresh Token
     */
    private String generateRefreshToken(Authentication authentication, User user, String deviceId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("http://localhost:8080")
                .issuedAt(now)
                .expiresAt(now.plus(7, ChronoUnit.DAYS))
                .subject(String.valueOf(user.getId()))
                .id(UUID.randomUUID().toString())
                .claim("username", user.getUsername())
                .claim("device_id", deviceId != null ? deviceId : "unknown")
                .claim("token_type", "refresh_token")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    /**
     * 获取用户角色列表
     */
    private List<String> getRoles(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return Collections.emptyList();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户名获取用户信息
     */
    private User getUserByUsername(String username) {
        // 使用 MyBatis-Plus 查询用户
        return userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
        );
    }
}
