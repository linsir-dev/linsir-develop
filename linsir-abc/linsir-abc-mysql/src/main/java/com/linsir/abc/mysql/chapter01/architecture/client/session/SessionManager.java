package com.linsir.abc.mysql.chapter01.architecture.client.session;

import com.linsir.abc.mysql.chapter01.architecture.entity.ConnectionSession;
import com.linsir.abc.mysql.chapter01.architecture.mapper.ConnectionSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理器
 * 管理数据库连接会话的生命周期
 *
 * 职责：
 * 1. 会话创建和销毁
 * 2. 会话状态监控
 * 3. 空闲会话清理
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class SessionManager {

    /**
     * 会话超时时间(分钟)
     */
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    /**
     * 活跃会话缓存
     */
    private final Map<String, ConnectionSession> sessionCache;

    /**
     * 会话Mapper
     */
    private final ConnectionSessionMapper sessionMapper;

    public SessionManager(ConnectionSessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
        this.sessionCache = new ConcurrentHashMap<>();
    }

    /**
     * 注册会话
     *
     * @param session 会话
     */
    public void registerSession(ConnectionSession session) {
        sessionCache.put(session.getSessionId(), session);
        log.debug("注册会话: sessionId={}", session.getSessionId());
    }

    /**
     * 获取会话
     *
     * @param sessionId 会话ID
     * @return 会话
     */
    public ConnectionSession getSession(String sessionId) {
        ConnectionSession session = sessionCache.get(sessionId);
        if (session != null) {
            // 更新最后活跃时间
            session.updateLastActiveTime();
            sessionMapper.updateActiveTime(sessionId, session.getLastActiveTime());
        }
        return session;
    }

    /**
     * 移除会话
     *
     * @param sessionId 会话ID
     */
    public void removeSession(String sessionId) {
        sessionCache.remove(sessionId);
        sessionMapper.updateStatus(sessionId, 0);
        log.debug("移除会话: sessionId={}", sessionId);
    }

    /**
     * 获取活跃会话数
     *
     * @return 活跃会话数
     */
    public int getActiveSessionCount() {
        return sessionCache.size();
    }

    /**
     * 获取会话统计信息
     *
     * @return 会话统计
     */
    public SessionStats getSessionStats() {
        SessionStats stats = new SessionStats();
        stats.setTotalSessions(sessionCache.size());
        stats.setActiveSessions((int) sessionCache.values().stream()
                .filter(ConnectionSession::isActive).count());
        return stats;
    }

    /**
     * 定时清理空闲会话
     * 每5分钟执行一次
     */
    @Scheduled(fixedRate = 300000)
    public void cleanupIdleSessions() {
        log.debug("开始清理空闲会话...");
        LocalDateTime now = LocalDateTime.now();

        sessionCache.entrySet().removeIf(entry -> {
            ConnectionSession session = entry.getValue();
            long idleMinutes = ChronoUnit.MINUTES.between(
                    session.getLastActiveTime(), now);

            if (idleMinutes > SESSION_TIMEOUT_MINUTES) {
                sessionMapper.updateStatus(session.getSessionId(), 2); // 2-空闲/超时
                log.info("清理空闲会话: sessionId={}, idleMinutes={}",
                        session.getSessionId(), idleMinutes);
                return true;
            }
            return false;
        });
    }

    /**
     * 会话统计信息
     */
    public static class SessionStats {
        /**
         * 总会话数
         */
        private int totalSessions;

        /**
         * 活跃会话数
         */
        private int activeSessions;

        public int getTotalSessions() {
            return totalSessions;
        }

        public void setTotalSessions(int totalSessions) {
            this.totalSessions = totalSessions;
        }

        public int getActiveSessions() {
            return activeSessions;
        }

        public void setActiveSessions(int activeSessions) {
            this.activeSessions = activeSessions;
        }
    }
}
