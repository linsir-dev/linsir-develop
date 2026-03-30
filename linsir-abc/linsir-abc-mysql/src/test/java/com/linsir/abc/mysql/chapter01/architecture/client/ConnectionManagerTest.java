package com.linsir.abc.mysql.chapter01.architecture.client;

import com.linsir.abc.mysql.chapter01.architecture.client.connection.ConnectionManager;
import com.linsir.abc.mysql.chapter01.architecture.entity.ConnectionSession;
import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.mapper.ConnectionSessionMapper;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 连接管理器单元测试
 *
 * 测试范围：
 * 1. 会话创建
 * 2. 连接获取
 * 3. 会话关闭
 * 4. 连接池统计
 *
 * @author linsir
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("连接管理器测试")
class ConnectionManagerTest {

    @Mock
    private HikariDataSource dataSource;

    @Mock
    private ConnectionSessionMapper sessionMapper;

    @Mock
    private Connection connection;

    private ConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager(dataSource, sessionMapper);
    }

    @Test
    @DisplayName("测试创建会话")
    void testCreateSession() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("zhangsan")
                .role("USER")
                .build();
        String clientHost = "192.168.1.100";
        Integer clientPort = 54321;

        // When
        ConnectionSession session = connectionManager.createSession(user, clientHost, clientPort);

        // Then
        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals(user.getId(), session.getUserId());
        assertEquals(clientHost, session.getClientHost());
        assertEquals(clientPort, session.getClientPort());
        assertEquals(1, session.getStatus());
        verify(sessionMapper).insert(any(ConnectionSession.class));
    }

    @Test
    @DisplayName("测试获取数据库连接")
    void testGetConnection() throws SQLException {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);

        // When
        Connection result = connectionManager.getConnection();

        // Then
        assertNotNull(result);
        assertEquals(connection, result);
        verify(dataSource).getConnection();
    }

    @Test
    @DisplayName("测试关闭会话")
    void testCloseSession() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("zhangsan")
                .build();
        ConnectionSession session = connectionManager.createSession(user, "192.168.1.100", 54321);
        String sessionId = session.getSessionId();

        // When
        connectionManager.closeSession(sessionId);

        // Then
        verify(sessionMapper).updateStatus(sessionId, 0);
        assertEquals(0, connectionManager.getActiveSessionCount());
    }

    @Test
    @DisplayName("测试获取活跃会话数")
    void testGetActiveSessionCount() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("zhangsan")
                .build();

        // When
        connectionManager.createSession(user, "192.168.1.100", 54321);
        connectionManager.createSession(user, "192.168.1.101", 54322);

        // Then
        assertEquals(2, connectionManager.getActiveSessionCount());
    }

    @Test
    @DisplayName("测试连接池统计")
    void testGetPoolStats() {
        // Given - mock HikariPoolMXBean
        com.zaxxer.hikari.HikariPoolMXBean poolMXBean = mock(com.zaxxer.hikari.HikariPoolMXBean.class);
        when(dataSource.getHikariPoolMXBean()).thenReturn(poolMXBean);
        when(poolMXBean.getTotalConnections()).thenReturn(10);
        when(poolMXBean.getActiveConnections()).thenReturn(3);
        when(poolMXBean.getIdleConnections()).thenReturn(7);
        when(poolMXBean.getThreadsAwaitingConnection()).thenReturn(0);

        // When
        ConnectionManager.ConnectionPoolStats stats = connectionManager.getPoolStats();

        // Then
        assertNotNull(stats);
        assertEquals(10, stats.getTotalConnections());
        assertEquals(3, stats.getActiveConnections());
        assertEquals(7, stats.getIdleConnections());
        assertEquals(0, stats.getWaitingConnections());
    }

    @Test
    @DisplayName("测试会话创建后状态为活跃")
    void testSessionStatusAfterCreation() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("zhangsan")
                .build();

        // When
        ConnectionSession session = connectionManager.createSession(user, "192.168.1.100", 54321);

        // Then
        assertEquals(1, session.getStatus()); // 1表示活跃
        assertNotNull(session.getConnectionTime());
        assertNotNull(session.getLastActiveTime());
    }

    @Test
    @DisplayName("测试会话ID唯一性")
    void testSessionIdUniqueness() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("zhangsan")
                .build();

        // When
        ConnectionSession session1 = connectionManager.createSession(user, "192.168.1.100", 54321);
        ConnectionSession session2 = connectionManager.createSession(user, "192.168.1.101", 54322);

        // Then
        assertNotEquals(session1.getSessionId(), session2.getSessionId());
    }
}
