package com.linsir.security.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 认证审计日志服务
 * 用于记录详细的认证审计信息
 * 
 * @author linsir
 * @version 1.0.0
 */
@Service
public class AuthenticationAuditService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationAuditService.class);

    /**
     * 记录认证成功日志
     * 
     * @param authentication 认证信息
     */
    public void logSuccess(Authentication authentication) {
        String username = authentication.getName();
        String remoteAddress = getRemoteAddress();
        String sessionId = getSessionId();
        
        logger.info("🔐 认证成功审计 - 用户名：{}, IP: {}, SessionId: {}, 权限：{}", 
            username, 
            remoteAddress, 
            sessionId,
            authentication.getAuthorities());
    }

    /**
     * 记录认证失败日志
     * 
     * @param username 用户名
     * @param reason 失败原因
     */
    public void logFailure(String username, String reason) {
        String remoteAddress = getRemoteAddress();
        
        logger.warn("❌ 认证失败审计 - 用户名：{}, IP: {}, 原因：{}", 
            username, 
            remoteAddress, 
            reason);
    }

    /**
     * 记录账户锁定日志
     * 
     * @param username 用户名
     */
    public void logLocked(String username) {
        String remoteAddress = getRemoteAddress();
        
        logger.warn("🔒 账户锁定审计 - 用户名：{}, IP: {}", username, remoteAddress);
    }

    /**
     * 记录账户禁用日志
     * 
     * @param username 用户名
     */
    public void logDisabled(String username) {
        String remoteAddress = getRemoteAddress();
        
        logger.warn("🚫 账户禁用审计 - 用户名：{}, IP: {}", username, remoteAddress);
    }

    /**
     * 记录账户过期日志
     * 
     * @param username 用户名
     */
    public void logExpired(String username) {
        String remoteAddress = getRemoteAddress();
        
        logger.warn("⏰ 账户过期审计 - 用户名：{}, IP: {}", username, remoteAddress);
    }

    /**
     * 记录密码过期日志
     * 
     * @param username 用户名
     */
    public void logCredentialsExpired(String username) {
        String remoteAddress = getRemoteAddress();
        
        logger.warn("🔑 密码过期审计 - 用户名：{}, IP: {}", username, remoteAddress);
    }

    /**
     * 获取客户端 IP 地址
     * 
     * @return IP 地址
     */
    private String getRemoteAddress() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String forwardedFor = request.getHeader("X-Forwarded-For");
                if (forwardedFor != null && !forwardedFor.isEmpty()) {
                    return forwardedFor.split(",")[0].trim();
                }
                return request.getRemoteAddr();
            }
        } catch (Exception e) {
            // 忽略异常，返回 unknown
        }
        return "unknown";
    }

    /**
     * 获取 Session ID
     * 
     * @return Session ID
     */
    private String getSessionId() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return request.getSession(false) != null ? 
                    request.getSession().getId() : "N/A";
            }
        } catch (Exception e) {
            // 忽略异常
        }
        return "N/A";
    }
}
