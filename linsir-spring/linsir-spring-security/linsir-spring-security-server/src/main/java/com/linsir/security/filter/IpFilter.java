package com.linsir.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * IP 地址过滤器
 *
 * 对所有请求进行 IP 地址过滤
 * 支持白名单和黑名单模式
 *
 * @author linsir
 * @version 1.0.0
 */
@Component
public class IpFilter extends OncePerRequestFilter {

    /**
     * 白名单 IP 列表
     * 如果设置，只允许这些 IP 访问
     */
    private final Set<String> whiteList = new HashSet<>(Arrays.asList(
            // 示例：允许本地访问
            "127.0.0.1",
            "0:0:0:0:0:0:0:1",  // IPv6 localhost
            "localhost"
    ));

    /**
     * 黑名单 IP 列表
     * 如果设置，禁止这些 IP 访问
     */
    private final Set<String> blackList = new HashSet<>(Arrays.asList(
            // 示例：禁止某些 IP
            // "192.168.1.100"
    ));

    /**
     * 是否启用白名单模式
     * true: 只允许白名单中的 IP 访问
     * false: 禁止黑名单中的 IP 访问，其他允许
     */
    private final boolean whiteListMode = false;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 获取客户端真实 IP 地址
        String clientIp = getClientIp(request);

        // IP 过滤检查
        if (!isAllowed(clientIp)) {
            // IP 被禁止访问
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");

            PrintWriter out = response.getWriter();
            out.write("{\"code\":403,\"message\":\"IP地址 " + clientIp + " 被禁止访问\",\"timestamp\":" + System.currentTimeMillis() + "}");
            return;
        }

        // IP 允许访问，继续过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 获取客户端真实 IP 地址
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 如果存在多个 IP，取第一个（真实客户端 IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    /**
     * 检查 IP 是否允许访问
     *
     * @param ip 客户端 IP
     * @return true: 允许访问, false: 禁止访问
     */
    private boolean isAllowed(String ip) {
        if (whiteListMode) {
            // 白名单模式：只允许白名单中的 IP
            return whiteList.contains(ip);
        } else {
            // 黑名单模式：禁止黑名单中的 IP
            return !blackList.contains(ip);
        }
    }

    /**
     * 添加 IP 到白名单
     *
     * @param ip IP 地址
     */
    public void addToWhiteList(String ip) {
        whiteList.add(ip);
    }

    /**
     * 添加 IP 到黑名单
     *
     * @param ip IP 地址
     */
    public void addToBlackList(String ip) {
        blackList.add(ip);
    }

    /**
     * 从白名单移除 IP
     *
     * @param ip IP 地址
     */
    public void removeFromWhiteList(String ip) {
        whiteList.remove(ip);
    }

    /**
     * 从黑名单移除 IP
     *
     * @param ip IP 地址
     */
    public void removeFromBlackList(String ip) {
        blackList.remove(ip);
    }
}
