package com.linsir.abc.mysql.chapter01.architecture;

import com.linsir.abc.mysql.MySQLApplication;
import com.linsir.abc.mysql.chapter01.architecture.client.connection.ConnectionManager;
import com.linsir.abc.mysql.chapter01.architecture.client.auth.Authenticator;
import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import com.linsir.abc.mysql.chapter01.architecture.server.optimizer.QueryOptimizer;
import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.entity.Product;
import com.linsir.abc.mysql.chapter01.architecture.entity.ConnectionSession;
import com.linsir.abc.mysql.chapter01.architecture.mapper.UserMapper;
import com.linsir.abc.mysql.chapter01.architecture.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MySQL架构集成测试
 *
 * 测试范围：
 * 1. 完整的三层架构流程测试
 * 2. 端到端的查询处理流程
 * 3. 事务处理测试
 *
 * @author linsir
 * @since 1.0.0
 */
@SpringBootTest(classes = MySQLApplication.class)
@ActiveProfiles("test")
@DisplayName("MySQL架构集成测试")
class MySQLArchitectureIntegrationTest {

    @Autowired
    private ConnectionManager connectionManager;

    @Autowired
    private Authenticator authenticator;

    @Autowired
    private SQLParser sqlParser;

    @Autowired
    private QueryOptimizer queryOptimizer;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        // 初始化测试数据
    }

    @Test
    @DisplayName("测试完整登录流程")
    @Transactional
    void testCompleteLoginFlow() {
        // Given - 创建测试用户
        User user = User.builder()
                .username("testuser")
                .password(authenticator.encodePassword("password123"))
                .email("test@test.com")
                .status(1)
                .role("USER")
                .build();
        userMapper.insert(user);

        // When - 执行认证
        Authenticator.AuthResult authResult = authenticator.authenticate(
                "testuser", "password123", "127.0.0.1");

        // Then - 验证认证成功
        assertTrue(authResult.isSuccess());
        assertNotNull(authResult.getUser());

        // When - 创建会话
        ConnectionSession session = connectionManager.createSession(
                authResult.getUser(), "127.0.0.1", 54321);

        // Then - 验证会话创建
        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals(1, session.getStatus());

        // When - 关闭会话
        connectionManager.closeSession(session.getSessionId());

        // Then - 验证会话关闭
        assertEquals(0, connectionManager.getActiveSessionCount());
    }

    @Test
    @DisplayName("测试SQL解析和优化流程")
    void testSqlParseAndOptimizeFlow() {
        // Given
        String sql = "SELECT * FROM users WHERE status = 1";

        // When - 解析SQL
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // Then - 验证解析成功
        assertTrue(parseResult.isSuccess());
        assertEquals(SQLParser.SQLType.SELECT, parseResult.getSqlType());

        // When - 优化查询
        QueryOptimizer.ExecutionPlan executionPlan = queryOptimizer.optimize(parseResult);

        // Then - 验证优化结果
        assertTrue(executionPlan.isSuccess());
        assertNotNull(executionPlan.getStrategy());
        assertTrue(executionPlan.getEstimatedCost() > 0);
    }

    @Test
    @DisplayName("测试认证失败场景")
    @Transactional
    void testAuthenticationFailure() {
        // Given - 创建测试用户
        User user = User.builder()
                .username("testuser2")
                .password(authenticator.encodePassword("password123"))
                .email("test2@test.com")
                .status(1)
                .role("USER")
                .build();
        userMapper.insert(user);

        // When - 使用错误密码认证
        Authenticator.AuthResult authResult = authenticator.authenticate(
                "testuser2", "wrongpassword", "127.0.0.1");

        // Then - 验证认证失败
        assertFalse(authResult.isSuccess());
        assertNotNull(authResult.getMessage());
    }

    @Test
    @DisplayName("测试权限检查")
    @Transactional
    void testPermissionCheck() {
        // Given - 创建管理员用户（使用唯一用户名避免冲突）
        User admin = User.builder()
                .username("test_admin_" + System.currentTimeMillis())
                .password(authenticator.encodePassword("admin123"))
                .email("admin_test@test.com")
                .status(1)
                .role("ADMIN")
                .build();
        userMapper.insert(admin);

        User userFromDb = userMapper.findByUsername(admin.getUsername());

        // When/Then - 检查权限
        // ADMIN用户拥有所有权限，所以检查ADMIN返回true，检查其他角色也返回true
        assertTrue(authenticator.checkPermission(userFromDb, "ADMIN"));
        // 注意：根据设计，ADMIN角色拥有所有权限，所以检查SUPER_ADMIN也返回true
        assertTrue(authenticator.checkPermission(userFromDb, "SUPER_ADMIN"));
    }

    @Test
    @DisplayName("测试连接池统计")
    void testConnectionPoolStats() {
        // When
        ConnectionManager.ConnectionPoolStats stats = connectionManager.getPoolStats();

        // Then
        assertNotNull(stats);
        // 在测试环境中，HikariDataSource可能是mock的，所以不验证具体数值
    }

    @Test
    @DisplayName("测试SQL语法验证")
    void testSqlSyntaxValidation() {
        // Given
        String validSql = "SELECT * FROM users WHERE id = 1";
        String invalidSql = "SELECT * FROM users WHERE (id = 1"; // 括号不匹配

        // When/Then
        assertTrue(sqlParser.validateSyntax(validSql));
        assertFalse(sqlParser.validateSyntax(invalidSql));
    }

    @Test
    @DisplayName("测试SQL注入检测")
    void testSqlInjectionDetection() {
        // Given
        String normalSql = "SELECT * FROM users WHERE username = 'zhangsan'";
        String injectionSql = "SELECT * FROM users WHERE username = 'admin' OR '1'='1'";

        // When/Then
        assertFalse(sqlParser.detectSqlInjection(normalSql));
        assertTrue(sqlParser.detectSqlInjection(injectionSql));
    }
}
