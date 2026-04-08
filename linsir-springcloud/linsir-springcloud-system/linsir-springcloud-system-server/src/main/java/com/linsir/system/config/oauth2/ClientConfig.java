package com.linsir.system.config.oauth2;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.time.Duration;
import java.util.UUID;

/**
 * OAuth2 客户端配置类
 * Spring Security 7.0.4 版本
 * 配置客户端存储和初始化客户端数据
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class ClientConfig {

    /**
     * 客户端 Repository
     * 使用 JDBC 存储客户端信息
     */
    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcRegisteredClientRepository(jdbcTemplate);
    }

    /**
     * Token 设置
     */
    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                // Access Token: 30分钟
                .accessTokenTimeToLive(Duration.ofMinutes(30))
                // Refresh Token: 7天
                .refreshTokenTimeToLive(Duration.ofDays(7))
                // 授权码: 5分钟
                .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                // 刷新Token轮换: 每次刷新都生成新的
                .reuseRefreshTokens(false)
                // Access Token格式: JWT
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                .build();
    }

    /**
     * 初始化客户端数据
     * 应用启动时检查并创建默认客户端
     */
    @Bean
    public ApplicationRunner clientInitializer(RegisteredClientRepository clientRepository) {
        return args -> {
            // 1. system-server-web（认证中心管理端）
            if (clientRepository.findByClientId("system-web") == null) {
                createSystemWebClient(clientRepository);
            }

            // 2. 应用Web服务A
            if (clientRepository.findByClientId("app-a-web") == null) {
                createAppAWebClient(clientRepository);
            }

            // 3. 应用Web服务B
            if (clientRepository.findByClientId("app-b-web") == null) {
                createAppBWebClient(clientRepository);
            }

            // 4. App服务A（移动端，需要PKCE）
            if (clientRepository.findByClientId("app-a-mobile") == null) {
                createAppAMobileClient(clientRepository);
            }

            // 5. App服务B（移动端，需要PKCE）
            if (clientRepository.findByClientId("app-b-mobile") == null) {
                createAppBMobileClient(clientRepository);
            }
        };
    }

    /**
     * 创建 system-server-web 客户端
     * 认证中心管理端，不需要授权确认
     */
    private void createSystemWebClient(RegisteredClientRepository repository) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("system-web")
                .clientSecret("{bcrypt}" + new BCryptPasswordEncoder().encode("system-web-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://system.linsir.com/callback")
                .redirectUri("http://localhost:5173/callback")
                .postLogoutRedirectUri("http://system.linsir.com")
                .postLogoutRedirectUri("http://localhost:5173")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("admin")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .requireProofKey(false)
                        .build())
                .tokenSettings(tokenSettings())
                .build();

        repository.save(client);
    }

    /**
     * 创建应用A Web客户端
     */
    private void createAppAWebClient(RegisteredClientRepository repository) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("app-a-web")
                .clientSecret("{bcrypt}" + new BCryptPasswordEncoder().encode("app-a-web-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://app-a.linsir.com/callback")
                .redirectUri("http://localhost:5174/callback")
                .postLogoutRedirectUri("http://app-a.linsir.com")
                .postLogoutRedirectUri("http://localhost:5174")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("api")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .build())
                .tokenSettings(tokenSettings())
                .build();

        repository.save(client);
    }

    /**
     * 创建应用B Web客户端
     */
    private void createAppBWebClient(RegisteredClientRepository repository) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("app-b-web")
                .clientSecret("{bcrypt}" + new BCryptPasswordEncoder().encode("app-b-web-secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://app-b.linsir.com/callback")
                .redirectUri("http://localhost:5175/callback")
                .postLogoutRedirectUri("http://app-b.linsir.com")
                .postLogoutRedirectUri("http://localhost:5175")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("api")
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(false)
                        .build())
                .tokenSettings(tokenSettings())
                .build();

        repository.save(client);
    }

    /**
     * 创建应用A移动端客户端（需要PKCE）
     */
    private void createAppAMobileClient(RegisteredClientRepository repository) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("app-a-mobile")
                // 移动端不使用 client_secret，使用 PKCE
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                // App Scheme 回调地址
                .redirectUri("com.linsir.app.a://callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(true)  // 必须使用 PKCE
                        .build())
                .tokenSettings(tokenSettings())
                .build();

        repository.save(client);
    }

    /**
     * 创建应用B移动端客户端（需要PKCE）
     */
    private void createAppBMobileClient(RegisteredClientRepository repository) {
        RegisteredClient client = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("app-b-mobile")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("com.linsir.app.b://callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .requireProofKey(true)
                        .build())
                .tokenSettings(tokenSettings())
                .build();

        repository.save(client);
    }
}
