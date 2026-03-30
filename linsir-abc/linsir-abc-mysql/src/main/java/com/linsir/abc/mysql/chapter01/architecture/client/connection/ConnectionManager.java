package com.linsir.abc.mysql.chapter01.architecture.client.connection;

import com.linsir.abc.mysql.chapter01.architecture.entity.ConnectionSession;
import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.mapper.ConnectionSessionMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接管理器
 * 模拟MySQL客户端层的连接管理功能
 *
 * 职责：
 * 1. 管理数据库连接池
 * 2. 创建和销毁会话
 * 3. 维护连接状态
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class ConnectionManager {

    /**
     * 数据源
     */
    private final DataSource dataSource;

    /**
     * 会话Mapper
     */
    private final ConnectionSessionMapper sessionMapper;

    /**
     * 活跃会话缓存
     * key: sessionId, value: ConnectionSession
     */
    private final ConcurrentHashMap<String, ConnectionSession> activeSessions;

    public ConnectionManager(DataSource dataSource,
                             ConnectionSessionMapper sessionMapper) {
        this.dataSource = dataSource;
        this.sessionMapper = sessionMapper;
        this.activeSessions = new ConcurrentHashMap<>();
    }

    /**
     * 创建新连接会话
     *
     * @param user       认证用户
     * @param clientHost 客户端主机
     * @param clientPort 客户端端口
     * @return 会话信息
     */
    public ConnectionSession createSession(User user, String clientHost, Integer clientPort) {
        // 生成会话ID
        String sessionId = generateSessionId();

        // 创建会话对象
        ConnectionSession session = ConnectionSession.builder()
                .sessionId(sessionId)
                .userId(user.getId())
                .clientHost(clientHost)
                .clientPort(clientPort)
                .serverHost(getServerHost())
                .databaseName("linsir-abc-mysql")
                .connectionTime(LocalDateTime.now())
                .lastActiveTime(LocalDateTime.now())
                .status(1) // 活跃状态
                .commandCount(0)
                .totalExecuteTime(0L)
                .build();

        // 保存到数据库
        sessionMapper.insert(session);

        // 缓存到内存
        activeSessions.put(sessionId, session);

        log.info("创建新会话: sessionId={}, userId={}, clientHost={}",
                sessionId, user.getId(), clientHost);

        return session;
    }

    /**
     * 获取数据库连接
     *
     * @return 数据库连接
     * @throws SQLException 连接异常
     */
    public Connection getConnection() throws SQLException {
        long startTime = System.currentTimeMillis();
        Connection connection = dataSource.getConnection();
        long costTime = System.currentTimeMillis() - startTime;

        log.debug("获取连接耗时: {}ms", costTime);
        return connection;
    }

    /**
     * 关闭会话
     *
     * @param sessionId 会话ID
     */
    public void closeSession(String sessionId) {
        ConnectionSession session = activeSessions.remove(sessionId);
        if (session != null) {
            sessionMapper.updateStatus(sessionId, 0); // 0-断开
            log.info("关闭会话: sessionId={}", sessionId);
        }
    }

    /**
     * 获取活跃会话数
     *
     * @return 活跃会话数
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    /**
     * 获取连接池统计信息
     *
     * @return 连接池统计
     */
    public ConnectionPoolStats getPoolStats() {
        ConnectionPoolStats stats = new ConnectionPoolStats();

        if (dataSource instanceof HikariDataSource hikariDataSource) {
            stats.setTotalConnections(hikariDataSource.getHikariPoolMXBean().getTotalConnections());
            stats.setActiveConnections(hikariDataSource.getHikariPoolMXBean().getActiveConnections());
            stats.setIdleConnections(hikariDataSource.getHikariPoolMXBean().getIdleConnections());
            stats.setWaitingConnections(hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
        }

        return stats;
    }

    /**
     * 生成会话ID
     *
     * @return 会话ID
     */
    private String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取服务器主机名
     *
     * @return 服务器主机名
     */
    private String getServerHost() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "localhost";
        }
    }

    /**
     * 连接池统计信息
     */
    @Data
    public static class ConnectionPoolStats {
        /**
         * 总连接数
         */
        private int totalConnections;

        /**
         * 活跃连接数
         */
        private int activeConnections;

        /**
         * 空闲连接数
         */
        private int idleConnections;

        /**
         * 等待连接数
         */
        private int waitingConnections;
    }
}
