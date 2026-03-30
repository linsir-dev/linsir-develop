package com.linsir.abc.mysql.chapter01.architecture.server;

import com.linsir.abc.mysql.chapter01.architecture.server.optimizer.QueryOptimizer;
import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 查询优化器单元测试
 *
 * 测试范围：
 * 1. 索引选择优化
 * 2. JOIN顺序优化
 * 3. 执行计划生成
 * 4. 成本估算
 *
 * @author linsir
 * @since 1.0.0
 */
@DisplayName("查询优化器测试")
class QueryOptimizerTest {

    private QueryOptimizer queryOptimizer;
    private SQLParser sqlParser;

    @BeforeEach
    void setUp() {
        sqlParser = new SQLParser();
        queryOptimizer = new QueryOptimizer();
    }

    @Test
    @DisplayName("测试简单查询优化 - 全表扫描")
    void testOptimizeSimpleQuery_FullScan() {
        // Given
        String sql = "SELECT * FROM users";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.FULL_SCAN, plan.getStrategy());
        assertTrue(plan.getTables().contains("users"));
    }

    @Test
    @DisplayName("测试索引选择优化 - 带WHERE条件的查询")
    void testOptimizeIndexQuery() {
        // Given
        String sql = "SELECT * FROM users WHERE username = 'zhangsan'";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.INDEX_SCAN, plan.getStrategy());
        assertNotNull(plan.getIndexSuggestion());
    }

    @Test
    @DisplayName("测试INSERT优化")
    void testOptimizeInsert() {
        // Given
        String sql = "INSERT INTO users (username, email) VALUES ('test', 'test@test.com')";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.INDEX_SCAN, plan.getStrategy());
    }

    @Test
    @DisplayName("测试UPDATE优化 - 带WHERE条件")
    void testOptimizeUpdateWithWhere() {
        // Given
        String sql = "UPDATE users SET email = 'new@test.com' WHERE id = 1";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.INDEX_SCAN, plan.getStrategy());
    }

    @Test
    @DisplayName("测试UPDATE优化 - 无WHERE条件（警告）")
    void testOptimizeUpdateWithoutWhere() {
        // Given
        String sql = "UPDATE users SET status = 0";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.FULL_SCAN, plan.getStrategy());
        assertNotNull(plan.getWarning());
        assertTrue(plan.getWarning().contains("WHERE"));
    }

    @Test
    @DisplayName("测试DELETE优化 - 带WHERE条件")
    void testOptimizeDeleteWithWhere() {
        // Given
        String sql = "DELETE FROM users WHERE id = 1";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.INDEX_SCAN, plan.getStrategy());
    }

    @Test
    @DisplayName("测试DELETE优化 - 无WHERE条件（警告）")
    void testOptimizeDeleteWithoutWhere() {
        // Given
        String sql = "DELETE FROM users";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.FULL_SCAN, plan.getStrategy());
        assertNotNull(plan.getWarning());
        assertTrue(plan.getWarning().contains("WHERE"));
    }

    @Test
    @DisplayName("测试成本估算")
    void testCostEstimation() {
        // Given
        String sql = "SELECT * FROM users";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertTrue(plan.getEstimatedCost() > 0);
    }

    @Test
    @DisplayName("测试ORDER BY优化")
    void testOptimizeOrderBy() {
        // Given
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertTrue(plan.isNeedSort());
    }

    @Test
    @DisplayName("测试LIMIT优化")
    void testOptimizeLimit() {
        // Given
        String sql = "SELECT * FROM users LIMIT 10";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        // Then
        assertNotNull(plan);
        assertTrue(plan.isSuccess());
        assertTrue(plan.isHasLimit());
    }

    @Test
    @DisplayName("测试解析失败时的处理")
    void testOptimizeFailedParse() {
        // Given
        SQLParser.ParseResult failedResult = SQLParser.ParseResult.fail("解析失败");

        // When
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(failedResult);

        // Then
        assertNotNull(plan);
        assertFalse(plan.isSuccess());
    }
}
