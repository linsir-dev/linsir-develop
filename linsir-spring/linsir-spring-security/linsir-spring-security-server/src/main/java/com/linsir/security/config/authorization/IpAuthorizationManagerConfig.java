package com.linsir.security.config.authorization;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * IP 地址授权管理器配置类
 *
 * 基于客户端 IP 地址进行访问控制
 * 支持单个 IP 和 CIDR 网段配置
 *
 * @author linsir
 * @version 1.0.0
 */
@Configuration
public class IpAuthorizationManagerConfig {

    /**
     * 允许的 IP 列表
     * 支持单个 IP 或 CIDR 网段（如 192.168.1.0/24）
     */
    private final List<String> allowedIps = Arrays.asList(
            "127.0.0.1",          // 本地地址
            "192.168.1.0/24",     // 内网网段示例
            "10.0.0.0/8"          // 私有网段示例
    );

    /**
     * 不需要登录的公开路径
     */
    private final List<String> publicPaths = Arrays.asList(
            "/static/",
            "/api/auth/",
            "/api/hello",
            "/api/index",
            "/api/hello-page",
            "/api/security-context-page",
            "/api/user/update/password/",
            "/",
            "/index",
            "/login",
            "/error",
            "/easyui-demo"
    );

    @Bean
    public AuthorizationManager<RequestAuthorizationContext> ipAuthorizationManager() {
        return new IpAuthorizationManager(allowedIps, publicPaths);
    }

    /**
     * IP 授权管理器内部类
     * 先检查 IP，再检查是否需要认证
     */
    public static class IpAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

        private final List<String> allowedIps;
        private final List<String> publicPaths;

        public IpAuthorizationManager(List<String> allowedIps, List<String> publicPaths) {
            this.allowedIps = allowedIps;
            this.publicPaths = publicPaths;
        }

        @Override
        public AuthorizationDecision authorize(Supplier<? extends Authentication> authentication,
                                               RequestAuthorizationContext context) {
            HttpServletRequest request = context.getRequest();
            String clientIp = getClientIp(request);

            // 第一步：检查 IP 是否允许
            boolean ipAllowed = allowedIps.stream()
                    .anyMatch(allowedIp -> ipMatches(clientIp, allowedIp));

            if (!ipAllowed) {
                return new AuthorizationDecision(false);
            }

            // 第二步：检查是否是公开路径
            String requestUri = request.getRequestURI();
            boolean isPublicPath = publicPaths.stream()
                    .anyMatch(requestUri::startsWith);

            if (isPublicPath) {
                // 公开路径：IP 通过即可
                return new AuthorizationDecision(true);
            }

            // 第三步：非公开路径需要认证
            Authentication auth = authentication.get();
            boolean isAuthenticated = auth != null && auth.isAuthenticated();
            return new AuthorizationDecision(isAuthenticated);
        }

        /**
         * 获取客户端真实 IP 地址
         */
        private String getClientIp(HttpServletRequest request) {
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp.trim();
            }

            return request.getRemoteAddr();
        }

        /**
         * 检查 IP 是否匹配
         */
        private boolean ipMatches(String clientIp, String allowedIp) {
            if (allowedIp.contains("/")) {
                return isIpInRange(clientIp, allowedIp);
            }
            return clientIp.equals(allowedIp);
        }

        /**
         * 检查 IP 是否在 CIDR 网段内
         */
        private boolean isIpInRange(String ip, String cidr) {
            try {
                String[] parts = cidr.split("/");
                String networkAddress = parts[0];
                int prefixLength = Integer.parseInt(parts[1]);

                byte[] ipBytes = ipToBytes(ip);
                byte[] networkBytes = ipToBytes(networkAddress);

                int mask = 0xFFFFFFFF << (32 - prefixLength);
                int ipInt = bytesToInt(ipBytes);
                int networkInt = bytesToInt(networkBytes);

                return (ipInt & mask) == (networkInt & mask);
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * IP 地址转字节数组
         */
        private byte[] ipToBytes(String ip) {
            String[] parts = ip.split("\\.");
            byte[] bytes = new byte[4];
            for (int i = 0; i < 4; i++) {
                bytes[i] = (byte) Integer.parseInt(parts[i]);
            }
            return bytes;
        }

        /**
         * 字节数组转整数
         */
        private int bytesToInt(byte[] bytes) {
            int result = 0;
            for (int i = 0; i < 4; i++) {
                result |= (bytes[i] & 0xFF) << (24 - (i * 8));
            }
            return result;
        }
    }
}
