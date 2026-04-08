package com.linsir.system.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * OAuth2 授权服务器配置类
 * Spring Security 7.0.4 版本
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {

    /**
     * 授权服务器安全过滤器链
     * 优先级最高
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 配置授权服务器
                .oauth2AuthorizationServer((authorizationServer) -> {
                    // 配置安全匹配器，只匹配授权服务器端点
                    http.securityMatcher(authorizationServer.getEndpointsMatcher());
                    // 启用 OIDC
                    authorizationServer.oidc(Customizer.withDefaults());
                    // 授权端点配置
                    authorizationServer.authorizationEndpoint(authorizationEndpoint ->
                            authorizationEndpoint
                                    // 自定义授权确认页面
                                    .consentPage("http://localhost:5173/oauth2/consent")
                    );
                })
                // 配置请求授权
                .authorizeHttpRequests((authorize) ->
                        authorize.anyRequest().authenticated()
                )
                // 配置异常处理
                .exceptionHandling((exceptions) -> exceptions
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("http://localhost:5173/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                );

        return http.build();
    }

    /**
     * OAuth2 授权服务
     * 使用 JDBC 存储授权信息（Token等）
     */
    @Bean
    public OAuth2AuthorizationService authorizationService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * OAuth2 授权确认服务
     * 使用 JDBC 存储用户的授权同意记录
     */
    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(
            JdbcTemplate jdbcTemplate,
            RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    /**
     * Token 自定义器
     * 在 JWT Token 中添加自定义信息
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            if (context.getTokenType().getValue().equals("access_token")) {
                // 获取当前认证信息
                org.springframework.security.core.Authentication principal = context.getPrincipal();

                // 添加用户ID（sub字段）
                context.getClaims().subject(principal.getName());

                // 添加用户名
                context.getClaims().claim("username", principal.getName());

                // 添加角色信息
                Set<String> roles = principal.getAuthorities().stream()
                        .map(grantedAuthority -> grantedAuthority.getAuthority())
                        .collect(Collectors.toSet());
                context.getClaims().claim("roles", roles);

                // 添加登录方式
                context.getClaims().claim("login_type",
                        context.getAuthorizationGrantType().getValue());
            }
        };
    }

    /**
     * 授权服务器端点配置
     */
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                // 签发者
                .issuer("http://localhost:8080")
                // 授权端点
                .authorizationEndpoint("/oauth2/authorize")
                // Token 端点
                .tokenEndpoint("/oauth2/token")
                // Token 自省端点
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                // Token 撤销端点
                .tokenRevocationEndpoint("/oauth2/revoke")
                // JWK 集合端点（公钥）
                .jwkSetEndpoint("/oauth2/jwks")
                // OIDC 用户信息端点
                .oidcUserInfoEndpoint("/userinfo")
                // OIDC 客户端注册端点
                .oidcClientRegistrationEndpoint("/connect/register")
                .build();
    }
}
