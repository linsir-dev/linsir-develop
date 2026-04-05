package com.linsir.security.listener;

import com.linsir.security.service.AuthenticationAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureCredentialsExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureExpiredEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureProxyUntrustedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * 认证事件监听器
 * 用于监听和记录所有认证相关事件（成功和失败）
 * 
 * @author linsir
 * @version 1.0.0
 */
@Component
public class AuthenticationEventListener {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationEventListener.class);

    private final AuthenticationAuditService auditService;

    public AuthenticationEventListener(AuthenticationAuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * 监听认证成功事件
     */
    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        logger.info("✅ 认证成功 - 用户名：{}", username);
        auditService.logSuccess(event.getAuthentication());
    }

    /**
     * 监听认证失败事件（通用）
     */
    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String exceptionType = event.getException().getClass().getSimpleName();
        logger.warn("❌ 认证失败 - 用户名：{}, 异常类型：{}", username, exceptionType);
        auditService.logFailure(username, exceptionType);
    }

    /**
     * 监听用户名或密码错误事件
     */
    @EventListener
    public void onBadCredentials(AuthenticationFailureBadCredentialsEvent event) {
        String username = event.getAuthentication().getName();
        logger.warn("🔐 用户名或密码错误 - 用户名：{}", username);
        auditService.logFailure(username, "用户名或密码错误");
    }

    /**
     * 监听账户被锁定事件
     */
    @EventListener
    public void onLocked(AuthenticationFailureLockedEvent event) {
        String username = event.getAuthentication().getName();
        logger.warn("🔒 账户被锁定 - 用户名：{}", username);
        auditService.logLocked(username);
    }

    /**
     * 监听账户被禁用事件
     */
    @EventListener
    public void onDisabled(AuthenticationFailureDisabledEvent event) {
        String username = event.getAuthentication().getName();
        logger.warn("🚫 账户被禁用 - 用户名：{}", username);
        auditService.logDisabled(username);
    }

    /**
     * 监听账户已过期事件
     */
    @EventListener
    public void onExpired(AuthenticationFailureExpiredEvent event) {
        String username = event.getAuthentication().getName();
        logger.warn("⏰ 账户已过期 - 用户名：{}", username);
        auditService.logExpired(username);
    }

    /**
     * 监听密码已过期事件
     */
    @EventListener
    public void onCredentialsExpired(AuthenticationFailureCredentialsExpiredEvent event) {
        String username = event.getAuthentication().getName();
        logger.warn("🔑 密码已过期 - 用户名：{}", username);
        auditService.logCredentialsExpired(username);
    }

    /**
     * 监听认证服务异常事件
     */
    @EventListener
    public void onServiceException(AuthenticationFailureServiceExceptionEvent event) {
        String username = event.getAuthentication().getName();
        String message = event.getException().getMessage();
        logger.error("💥 认证服务异常 - 用户名：{}, 异常：{}", username, message);
        auditService.logFailure(username, "服务异常：" + message);
    }

    /**
     * 监听代理不受信任事件（OAuth2 等场景）
     */
    @EventListener
    public void onProxyUntrusted(AuthenticationFailureProxyUntrustedEvent event) {
        String username = event.getAuthentication().getName();
        logger.warn("⚠️  代理不受信任 - 用户名：{}", username);
        auditService.logFailure(username, "代理不受信任");
    }
}
