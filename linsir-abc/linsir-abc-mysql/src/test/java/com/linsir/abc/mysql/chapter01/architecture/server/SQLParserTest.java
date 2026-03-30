package com.linsir.abc.mysql.chapter01.architecture.server;

import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import com.linsir.abc.mysql.chapter01.architecture.server.parser.ParseTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQL解析器单元测试
 *
 * 测试范围：
 * 1. SELECT语句解析
 * 2. INSERT语句解析
 * 3. UPDATE语句解析
 * 4. DELETE语句解析
 * 5. 语法错误检测
 *
 * @author linsir
 * @since 1.0.0
 */
@DisplayName("SQL解析器测试")
class SQLParserTest {

    private SQLParser sqlParser;

    @BeforeEach
    void setUp() {
        sqlParser = new SQLParser();
    }

    @Test
    @DisplayName("测试简单SELECT语句解析")
    void testParseSimpleSelect() {
        // Given
        String sql = "SELECT id, username, email FROM users";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertEquals("SELECT", tree.getStatementType());
        assertEquals("users", tree.getTableName());
        assertEquals(3, tree.getColumns().size());
        assertTrue(tree.getColumns().contains("id"));
        assertTrue(tree.getColumns().contains("username"));
        assertTrue(tree.getColumns().contains("email"));
    }

    @Test
    @DisplayName("测试带WHERE条件的SELECT语句")
    void testParseSelectWithWhere() {
        // Given
        String sql = "SELECT * FROM users WHERE status = 1 AND role = 'ADMIN'";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertEquals("SELECT", tree.getStatementType());
        assertNotNull(tree.getWhereClause());
        assertTrue(tree.getWhereClause().contains("status = 1"));
        assertTrue(tree.getWhereClause().contains("role = 'ADMIN'"));
    }

    @Test
    @DisplayName("测试带JOIN的SELECT语句")
    void testParseSelectWithJoin() {
        // Given
        String sql = "SELECT u.id, u.username, o.order_no FROM users u INNER JOIN orders o ON u.id = o.user_id";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertEquals("SELECT", tree.getStatementType());
        assertTrue(tree.isHasJoin());
        assertEquals("INNER JOIN", tree.getJoinType());
        assertEquals("orders", tree.getJoinTable());
    }

    @Test
    @DisplayName("测试带ORDER BY的SELECT语句")
    void testParseSelectWithOrderBy() {
        // Given
        String sql = "SELECT * FROM products ORDER BY price DESC, created_at ASC";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertTrue(tree.isHasOrderBy());
        assertNotNull(tree.getOrderByClause());
    }

    @Test
    @DisplayName("测试带LIMIT的SELECT语句")
    void testParseSelectWithLimit() {
        // Given
        String sql = "SELECT * FROM users LIMIT 10 OFFSET 20";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertTrue(tree.isHasLimit());
        assertEquals(10, tree.getLimit());
        assertEquals(20, tree.getOffset());
    }

    @Test
    @DisplayName("测试INSERT语句解析")
    void testParseInsert() {
        // Given
        String sql = "INSERT INTO users (username, email) VALUES ('zhangsan', 'zhangsan@test.com')";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertEquals("INSERT", tree.getStatementType());
        assertEquals("users", tree.getTableName());
    }

    @Test
    @DisplayName("测试UPDATE语句解析")
    void testParseUpdate() {
        // Given
        String sql = "UPDATE users SET email = 'new@test.com' WHERE id = 1";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertEquals("UPDATE", tree.getStatementType());
        assertEquals("users", tree.getTableName());
        assertNotNull(tree.getWhereClause());
    }

    @Test
    @DisplayName("测试DELETE语句解析")
    void testParseDelete() {
        // Given
        String sql = "DELETE FROM users WHERE id = 1";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertEquals("DELETE", tree.getStatementType());
        assertEquals("users", tree.getTableName());
        assertNotNull(tree.getWhereClause());
    }

    @Test
    @DisplayName("测试聚合函数检测")
    void testDetectAggregateFunction() {
        // Given
        String sql = "SELECT COUNT(*), SUM(amount), AVG(price) FROM orders";

        // When
        ParseTree tree = sqlParser.parseToTree(sql);

        // Then
        assertNotNull(tree);
        assertTrue(tree.isHasAggregateFunction());
        assertNotNull(tree.getAggregateFunctions());
        assertFalse(tree.getAggregateFunctions().isEmpty());
    }

    @Test
    @DisplayName("测试SQL注入检测")
    void testDetectSqlInjection() {
        // Given
        String maliciousSql = "SELECT * FROM users WHERE username = 'admin' OR '1'='1'";

        // When
        boolean isInjection = sqlParser.detectSqlInjection(maliciousSql);

        // Then
        assertTrue(isInjection);
    }

    @Test
    @DisplayName("测试SQL格式化")
    void testFormatSql() {
        // Given
        String messySql = "SELECT   *   FROM   users   WHERE   id   =   1";

        // When
        String formatted = sqlParser.format(messySql);

        // Then
        assertNotNull(formatted);
        assertFalse(formatted.contains("  "));
    }

    @ParameterizedTest
    @CsvSource({
        "SELECT * FROM users, true",
        "INSERT INTO users VALUES (1), true",
        "UPDATE users SET name='test', true",
        "DELETE FROM users, true",
        "SELECT * FROM users WHERE (id = 1, false"
    })
    @DisplayName("测试SQL语法验证")
    void testValidateSyntax(String sql, boolean expected) {
        // When
        boolean isValid = sqlParser.validateSyntax(sql);

        // Then
        assertEquals(expected, isValid);
    }

    @Test
    @DisplayName("测试空SQL处理")
    void testParseEmptySql() {
        // Given
        String emptySql = "";

        // When
        SQLParser.ParseResult result = sqlParser.parse(emptySql);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("测试NULL SQL处理")
    void testParseNullSql() {
        // When
        SQLParser.ParseResult result = sqlParser.parse(null);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "SELECT * FROM users",
        "select * from users",
        "Select * From Users"
    })
    @DisplayName("测试SQL大小写不敏感")
    void testCaseInsensitive(String sql) {
        // When
        SQLParser.ParseResult result = sqlParser.parse(sql);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(SQLParser.SQLType.SELECT, result.getSqlType());
    }
}
