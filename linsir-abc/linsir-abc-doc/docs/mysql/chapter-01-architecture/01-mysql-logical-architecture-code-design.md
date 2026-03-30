# 1.1 MySQL 逻辑架构 - 示例代码详细设计文档

> 基于MySQL逻辑架构的三层设计，实现一个完整的数据库连接管理与查询处理示例系统

***

## 一、设计概述

### 1.1 设计目标

本示例系统模拟MySQL的逻辑架构三层设计，实现：

- **客户端层**：连接管理、认证授权
- **服务层**：SQL解析、查询优化、执行器
- **存储引擎层**：数据存取（基于实际MySQL数据库）

### 1.2 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        应用层 (Application)                       │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐              │
│  │  连接管理模块  │  │  查询执行模块  │  │  监控管理模块  │              │
│  └─────────────┘  └─────────────┘  └─────────────┘              │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                      客户端层 (Client Layer)                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │  Connection  │  │Authenticator │  │ Session      │           │
│  │  Manager     │  │   Manager    │  │ Manager      │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                      服务层 (Server Layer)                        │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │  Parser  │  │ Optimizer│  │ Executor │  │  Cache   │        │
│  │ (SQL解析) │  │(查询优化) │  │ (执行器)  │  │ (缓存)   │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                    存储引擎层 (Storage Engine)                     │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │              MySQL Database (InnoDB Engine)              │   │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐    │   │
│  │  │  users  │  │ orders  │  │products │  │  logs   │    │   │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘    │   │
│  └─────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### 1.3 技术栈

| 层级    | 技术选型              | 说明     |
| ----- | ----------------- | ------ |
| 编程语言  | Java 17           | 主要开发语言 |
| 数据库   | MySQL 8.0         | 数据存储   |
| 连接池   | HikariCP          | 高性能连接池 |
| ORM框架 | MyBatis           | SQL映射  |
| 测试框架  | JUnit 5 + Mockito | 单元测试   |
| 构建工具  | Maven             | 项目管理   |

***

## 二、Maven依赖配置（使用dependencyManagement）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- 父项目配置 - 使用linsir-abc作为父项目 -->
    <parent>
        <groupId>com.linsir</groupId>
        <artifactId>linsir-abc</artifactId>
        <version>1.0.0</version>
    </parent>

    <!-- 当前模块配置 -->
    <artifactId>linsir-abc-mysql</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>linsir-abc-mysql</name>
    <description>MySQL Architecture Learning Module</description>

    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
        <mybatis-spring-boot.version>3.0.3</mybatis-spring-boot.version>
        <mysql-connector.version>8.2.0</mysql-connector.version>
        <hikaricp.version>5.1.0</hikaricp.version>
        <lombok.version>1.18.30</lombok.version>
    </properties>

    <!-- 依赖管理 - 使用Spring Boot BOM统一管理版本 -->
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <dependencies>
        <!-- Spring Boot Starter Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Starter JDBC -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <!-- MyBatis Spring Boot Starter -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>${mybatis-spring-boot.version}</version>
        </dependency>

        <!-- MySQL Connector -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>${mysql-connector.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- HikariCP -->
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>${hikaricp.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <optional>true</optional>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- H2 Database for Testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

***

## 三、核心代码设计

### 3.1 ParseTree.java - SQL解析树

```java
package com.linsir.abc.mysql.chapter01.architecture.server.parser;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * SQL解析树
 *
 * 用于存储SQL解析后的结构化信息
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParseTree {

    /** SQL语句类型：SELECT/INSERT/UPDATE/DELETE */
    private String statementType;

    /** 表名 */
    private String tableName;

    /** 查询列 */
    private List<String> columns;

    /** 插入/更新值 */
    private List<String> values;

    /** 批量插入值 */
    private List<List<String>> batchValues;

    /** WHERE条件 */
    private String whereClause;

    /** ORDER BY条件 */
    private String orderByClause;

    /** ORDER BY列 */
    private List<String> orderByColumns;

    /** LIMIT数量 */
    private Integer limit;

    /** OFFSET数量 */
    private Integer offset;

    /** SET子句（UPDATE用） */
    private Map<String, String> setClauses;

    /** 是否有JOIN */
    private boolean hasJoin;

    /** JOIN类型 */
    private String joinType;

    /** JOIN表名 */
    private String joinTable;

    /** 是否有聚合函数 */
    private boolean hasAggregateFunction;

    /** 聚合函数列表 */
    private List<String> aggregateFunctions;

    /** 是否有子查询 */
    private boolean hasSubquery;

    /** 子查询列表 */
    private List<ParseTree> subqueries;

    /** 是否有GROUP BY */
    private boolean hasGroupBy;

    /** 是否有HAVING */
    private boolean hasHaving;

    /** 是否有ORDER BY */
    private boolean hasOrderBy;

    /** 是否有LIMIT */
    private boolean hasLimit;

    /** 是否有UNION */
    private boolean hasUnion;

    /** UNION分支数量 */
    private int unionBranches;
}
```

### 3.2 SQLParser.java - SQL解析器

```java
package com.linsir.abc.mysql.chapter01.architecture.server.parser;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL解析器
 * 模拟MySQL服务层的解析器功能
 */
@Slf4j
@Component
public class SQLParser {
    
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern UPDATE_PATTERN = Pattern.compile("^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE);
    private static final Pattern TABLE_PATTERN = Pattern.compile(
        "(?:FROM|INTO|UPDATE|JOIN)\\s+(`?)(\\w+)\\1", 
        Pattern.CASE_INSENSITIVE
    );
    
    public ParseResult parse(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return ParseResult.fail("SQL不能为空");
        }
        
        try {
            List<Token> tokens = tokenize(sql);
            SQLType sqlType = identifySQLType(sql);
            List<String> tables = extractTables(sql);
            
            ParseResult result = new ParseResult();
            result.setSuccess(true);
            result.setSqlType(sqlType);
            result.setTables(tables);
            result.setTokens(tokens);
            result.setOriginalSql(sql);
            
            log.debug("SQL解析成功: type={}, tables={}", sqlType, tables);
            return result;
            
        } catch (Exception e) {
            log.error("SQL解析失败: {}", e.getMessage());
            return ParseResult.fail("SQL解析错误: " + e.getMessage());
        }
    }
    
    public List<Token> tokenize(String sql) {
        List<Token> tokens = new ArrayList<>();
        String[] words = sql.split("\\s+");
        
        int position = 0;
        for (String word : words) {
            if (word.trim().isEmpty()) continue;
            TokenType type = identifyTokenType(word);
            tokens.add(new Token(type, word, position));
            position += word.length() + 1;
        }
        
        return tokens;
    }
    
    private TokenType identifyTokenType(String word) {
        String upper = word.toUpperCase();
        return switch (upper) {
            case "SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", 
                 "AND", "OR", "JOIN", "LEFT", "RIGHT", "INNER", "OUTER",
                 "GROUP", "BY", "ORDER", "HAVING", "LIMIT", "OFFSET" -> TokenType.KEYWORD;
            case "*", "=", "<", ">", "<=", ">=", "<>", "!=", ",", ";", "(", ")" -> TokenType.OPERATOR;
            default -> {
                if (word.matches("^\\d+$")) yield TokenType.NUMBER;
                else if (word.matches("^'.*'$")) yield TokenType.STRING;
                else yield TokenType.IDENTIFIER;
            }
        };
    }
    
    private SQLType identifySQLType(String sql) {
        if (SELECT_PATTERN.matcher(sql).find()) return SQLType.SELECT;
        else if (INSERT_PATTERN.matcher(sql).find()) return SQLType.INSERT;
        else if (UPDATE_PATTERN.matcher(sql).find()) return SQLType.UPDATE;
        else if (DELETE_PATTERN.matcher(sql).find()) return SQLType.DELETE;
        return SQLType.UNKNOWN;
    }
    
    private List<String> extractTables(String sql) {
        List<String> tables = new ArrayList<>();
        Matcher matcher = TABLE_PATTERN.matcher(sql);
        while (matcher.find()) {
            String tableName = matcher.group(2);
            if (!tables.contains(tableName)) tables.add(tableName);
        }
        return tables;
    }
    
    public boolean validateSyntax(String sql) {
        if (sql == null || sql.trim().isEmpty()) return false;
        int bracketCount = 0;
        for (char c : sql.toCharArray()) {
            if (c == '(') bracketCount++;
            if (c == ')') bracketCount--;
            if (bracketCount < 0) return false;
        }
        return bracketCount == 0;
    }
    
    // ===== 新增方法 =====
    
    public ParseTree parseToTree(String sql) {
        ParseResult result = parse(sql);
        if (!result.isSuccess()) {
            throw new RuntimeException("SQL解析失败: " + result.getMessage());
        }

        ParseTree tree = new ParseTree();
        tree.setStatementType(result.getSqlType().name());
        tree.setTableName(result.getTables().isEmpty() ? null : result.getTables().get(0));
        tree.setColumns(extractColumns(sql));
        tree.setWhereClause(extractWhereClause(sql));
        tree.setOrderByClause(extractOrderByClause(sql));
        tree.setHasOrderBy(tree.getOrderByClause() != null);
        extractLimit(sql, tree);
        tree.setHasJoin(sql.toUpperCase().contains("JOIN"));
        if (tree.isHasJoin()) {
            tree.setJoinType(extractJoinType(sql));
            tree.setJoinTable(extractJoinTable(sql));
        }
        tree.setHasAggregateFunction(checkAggregateFunction(sql));
        if (tree.isHasAggregateFunction()) {
            tree.setAggregateFunctions(extractAggregateFunctions(sql));
        }
        tree.setHasSubquery(sql.contains("("));
        tree.setHasGroupBy(sql.toUpperCase().contains("GROUP BY"));

        return tree;
    }

    private List<String> extractColumns(String sql) {
        List<String> columns = new ArrayList<>();
        Matcher matcher = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            String cols = matcher.group(1);
            if ("*".equals(cols.trim())) columns.add("*");
            else {
                for (String col : cols.split(",")) {
                    columns.add(col.trim().replaceAll("\\s+AS\\s+\\w+", "").replaceAll("\\w+\\.", ""));
                }
            }
        }
        return columns;
    }

    private String extractWhereClause(String sql) {
        Matcher matcher = Pattern.compile("WHERE\\s+(.*?)(?:ORDER BY|GROUP BY|LIMIT|$)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) return matcher.group(1).trim();
        return null;
    }

    private String extractOrderByClause(String sql) {
        Matcher matcher = Pattern.compile("ORDER\\s+BY\\s+(.*?)(?:LIMIT|$)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) return matcher.group(1).trim();
        return null;
    }

    private void extractLimit(String sql, ParseTree tree) {
        Matcher matcher = Pattern.compile("LIMIT\\s+(\\d+)(?:\\s+OFFSET\\s+(\\d+))?", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            tree.setLimit(Integer.parseInt(matcher.group(1)));
            tree.setHasLimit(true);
            if (matcher.group(2) != null) tree.setOffset(Integer.parseInt(matcher.group(2)));
        }
    }

    private String extractJoinType(String sql) {
        if (sql.toUpperCase().contains("INNER JOIN")) return "INNER JOIN";
        if (sql.toUpperCase().contains("LEFT JOIN")) return "LEFT JOIN";
        if (sql.toUpperCase().contains("RIGHT JOIN")) return "RIGHT JOIN";
        return "JOIN";
    }

    private String extractJoinTable(String sql) {
        Matcher matcher = Pattern.compile("JOIN\\s+(\\w+)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private boolean checkAggregateFunction(String sql) {
        return Pattern.compile("(COUNT|SUM|AVG|MAX|MIN)\\s*\\(", Pattern.CASE_INSENSITIVE).matcher(sql).find();
    }

    private List<String> extractAggregateFunctions(String sql) {
        List<String> functions = new ArrayList<>();
        Matcher matcher = Pattern.compile("(COUNT|SUM|AVG|MAX|MIN)\\s*\\([^)]+\\)", Pattern.CASE_INSENSITIVE).matcher(sql);
        while (matcher.find()) functions.add(matcher.group());
        return functions;
    }

    public boolean detectSqlInjection(String sql) {
        String[] patterns = {"OR '1'='1", "DROP TABLE", "UNION SELECT", ";--", "/*", "*/"};
        String upperSql = sql.toUpperCase();
        for (String pattern : patterns) {
            if (upperSql.contains(pattern)) return true;
        }
        return false;
    }

    public String format(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }
    
    public enum TokenType { KEYWORD, IDENTIFIER, OPERATOR, NUMBER, STRING, SYMBOL }
    public enum SQLType { SELECT, INSERT, UPDATE, DELETE, UNKNOWN }
    
    @Data @AllArgsConstructor
    public static class Token {
        private TokenType type;
        private String value;
        private int position;
    }
    
    @Data
    public static class ParseResult {
        private boolean success;
        private String message;
        private SQLType sqlType;
        private List<String> tables;
        private List<Token> tokens;
        private String originalSql;
        
        public static ParseResult fail(String message) {
            ParseResult result = new ParseResult();
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
}
```

### 3.3 QueryOptimizer.java - 查询优化器

```java
package com.linsir.abc.mysql.chapter01.architecture.server.optimizer;

import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 查询优化器
 * 模拟MySQL服务层的优化器功能
 */
@Slf4j
@Component
public class QueryOptimizer {
    
    public ExecutionPlan optimize(SQLParser.ParseResult parseResult) {
        if (!parseResult.isSuccess()) {
            return ExecutionPlan.fail("解析失败，无法优化");
        }
        
        ExecutionPlan plan = new ExecutionPlan();
        plan.setOriginalSql(parseResult.getOriginalSql());
        plan.setSqlType(parseResult.getSqlType());
        plan.setTables(parseResult.getTables());
        
        switch (parseResult.getSqlType()) {
            case SELECT -> optimizeSelect(plan, parseResult);
            case INSERT -> optimizeInsert(plan, parseResult);
            case UPDATE -> optimizeUpdate(plan, parseResult);
            case DELETE -> optimizeDelete(plan, parseResult);
            default -> plan.setStrategy(ExecutionStrategy.FULL_SCAN);
        }
        
        estimateCost(plan);
        
        log.debug("查询优化完成: strategy={}, estimatedCost={}", 
                plan.getStrategy(), plan.getEstimatedCost());
        
        return plan;
    }
    
    private void optimizeSelect(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        String sql = parseResult.getOriginalSql().toUpperCase();

        if (sql.contains("WHERE")) {
            List<Condition> conditions = extractConditions(sql);
            plan.setConditions(conditions);
            
            // 检查是否可以使用索引（简化：只要有等号条件就认为可以用索引）
            boolean canUseIndex = sql.contains("=") && !sql.contains("OR");
            
            if (canUseIndex) {
                plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
                plan.setIndexSuggestion("建议在条件字段上创建索引");
            } else {
                plan.setStrategy(ExecutionStrategy.FULL_SCAN);
            }
        } else {
            plan.setStrategy(ExecutionStrategy.FULL_SCAN);
        }

        if (sql.contains("ORDER BY")) {
            plan.setNeedSort(true);
            plan.setSortFields(extractOrderByFields(sql));
        }

        if (sql.contains("GROUP BY")) {
            plan.setNeedGroup(true);
        }

        if (sql.contains("LIMIT")) {
            plan.setLimit(extractLimit(sql));
        }
    }
    
    private void optimizeInsert(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
    }
    
    private void optimizeUpdate(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        String sql = parseResult.getOriginalSql().toUpperCase();
        if (sql.contains("WHERE")) {
            plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
        } else {
            plan.setStrategy(ExecutionStrategy.FULL_SCAN);
            plan.setWarning("UPDATE语句缺少WHERE条件，将更新全表");
        }
    }
    
    private void optimizeDelete(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        String sql = parseResult.getOriginalSql().toUpperCase();
        if (sql.contains("WHERE")) {
            plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
        } else {
            plan.setStrategy(ExecutionStrategy.FULL_SCAN);
            plan.setWarning("DELETE语句缺少WHERE条件，将删除全表数据");
        }
    }
    
    private void estimateCost(ExecutionPlan plan) {
        double cost = 0.0;
        switch (plan.getStrategy()) {
            case FULL_SCAN -> cost = 1000.0;
            case INDEX_SCAN -> cost = 100.0;
            case RANGE_SCAN -> cost = 50.0;
            case POINT_QUERY -> cost = 10.0;
        }
        if (plan.isNeedSort()) cost += 200.0;
        if (plan.isNeedGroup()) cost += 300.0;
        plan.setEstimatedCost(cost);
    }
    
    private List<Condition> extractConditions(String sql) {
        return new ArrayList<>();
    }
    
    private List<String> extractOrderByFields(String sql) {
        return new ArrayList<>();
    }
    
    private Integer extractLimit(String sql) {
        try {
            int index = sql.indexOf("LIMIT");
            if (index > 0) {
                String afterLimit = sql.substring(index + 5).trim();
                String[] parts = afterLimit.split("\\s+");
                if (parts.length > 0) {
                    return Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
                }
            }
        } catch (Exception e) {
            log.warn("提取LIMIT失败: {}", e.getMessage());
        }
        return null;
    }
    
    @Data
    public static class ExecutionPlan {
        private boolean success = true;
        private String errorMessage;
        private String originalSql;
        private SQLParser.SQLType sqlType;
        private List<String> tables;
        private ExecutionStrategy strategy;
        private double estimatedCost;
        private String indexSuggestion;
        private String warning;
        private List<Condition> conditions;
        private boolean needSort;
        private List<String> sortFields;
        private boolean needGroup;
        private Integer limit;
        
        public static ExecutionPlan fail(String message) {
            ExecutionPlan plan = new ExecutionPlan();
            plan.setSuccess(false);
            plan.setErrorMessage(message);
            return plan;
        }
    }
    
    public enum ExecutionStrategy {
        FULL_SCAN, INDEX_SCAN, RANGE_SCAN, POINT_QUERY
    }
    
    @Data @AllArgsConstructor
    public static class Condition {
        private String field;
        private String operator;
        private String value;
    }
}
```

***

## 四、单元测试代码

### 4.0 测试配置说明

本项目使用JUnit 5 + Mockito进行单元测试：

1. **单元测试**：使用Mockito模拟依赖，不依赖Spring容器
2. **测试数据库**：使用H2内存数据库

### 4.1 ConnectionManagerTest.java

```java
package com.linsir.abc.mysql.chapter01.architecture.client;

import com.linsir.abc.mysql.chapter01.architecture.client.connection.ConnectionManager;
import com.linsir.abc.mysql.chapter01.architecture.entity.ConnectionSession;
import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.mapper.ConnectionSessionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionManagerTest {

    @Mock private DataSource dataSource;
    @Mock private ConnectionSessionMapper sessionMapper;
    @Mock private Connection connection;

    private ConnectionManager connectionManager;

    @BeforeEach
    void setUp() {
        connectionManager = new ConnectionManager(dataSource, sessionMapper);
    }

    @Test
    @DisplayName("测试创建会话")
    void testCreateSession() {
        User user = User.builder().id(1L).username("test_user").role("USER").status(1).build();
        ConnectionSession session = connectionManager.createSession(user, "192.168.1.100", 54321);

        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals(user.getId(), session.getUserId());
        verify(sessionMapper).insert(any(ConnectionSession.class));
    }

    @Test
    @DisplayName("测试获取数据库连接")
    void testGetConnection() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isClosed()).thenReturn(false);

        Connection result = connectionManager.getConnection();

        assertNotNull(result);
        assertFalse(result.isClosed());
        verify(dataSource).getConnection();
    }

    @Test
    @DisplayName("测试连接池统计")
    void testGetPoolStats() {
        com.zaxxer.hikari.HikariPoolMXBean poolMXBean = mock(com.zaxxer.hikari.HikariPoolMXBean.class);
        when(dataSource.getHikariPoolMXBean()).thenReturn(poolMXBean);
        when(poolMXBean.getTotalConnections()).thenReturn(10);
        when(poolMXBean.getActiveConnections()).thenReturn(3);
        when(poolMXBean.getIdleConnections()).thenReturn(7);

        ConnectionManager.ConnectionPoolStats stats = connectionManager.getPoolStats();

        assertNotNull(stats);
        assertEquals(10, stats.getTotalConnections());
        assertEquals(3, stats.getActiveConnections());
    }
}
```

### 4.2 AuthenticatorTest.java

```java
package com.linsir.abc.mysql.chapter01.architecture.client;

import com.linsir.abc.mysql.chapter01.architecture.client.auth.Authenticator;
import com.linsir.abc.mysql.chapter01.architecture.entity.User;
import com.linsir.abc.mysql.chapter01.architecture.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatorTest {

    @Mock private UserMapper userMapper;
    private Authenticator authenticator;

    @BeforeEach
    void setUp() {
        authenticator = new Authenticator(userMapper);
    }

    @Test
    @DisplayName("测试认证成功")
    void testAuthenticateSuccess() {
        String username = "zhangsan";
        String password = "password";
        User user = User.builder()
                .id(1L).username(username)
                .password(authenticator.encodePassword(password))
                .role("USER").status(1).build();

        when(userMapper.selectByUsername(username)).thenReturn(user);

        Authenticator.AuthResult result = authenticator.authenticate(username, password, "192.168.1.100");

        assertTrue(result.isSuccess());
        assertNotNull(result.getUser());
    }

    @Test
    @DisplayName("测试认证失败-用户不存在")
    void testAuthenticateFail_UserNotExist() {
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);
        Authenticator.AuthResult result = authenticator.authenticate("nonexistent", "password", "192.168.1.100");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("测试密码加密")
    void testPasswordEncode() {
        String rawPassword = "test_password";
        String encodedPassword = authenticator.encodePassword(rawPassword);
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(authenticator.verifyPassword(rawPassword, encodedPassword));
    }

    @Test
    @DisplayName("测试权限检查")
    void testCheckPermission() {
        User admin = User.builder().role("ADMIN").status(1).build();
        User user = User.builder().role("USER").status(1).build();

        assertTrue(authenticator.checkPermission(admin, "USER"));
        assertTrue(authenticator.checkPermission(user, "USER"));
        assertFalse(authenticator.checkPermission(user, "ADMIN"));
    }
}
```

### 4.3 SQLParserTest.java

```java
package com.linsir.abc.mysql.chapter01.architecture.server;

import com.linsir.abc.mysql.chapter01.architecture.server.parser.ParseTree;
import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SQLParserTest {

    private SQLParser sqlParser;

    @BeforeEach
    void setUp() {
        sqlParser = new SQLParser();
    }

    @Test
    @DisplayName("测试解析SELECT语句")
    void testParseSelect() {
        String sql = "SELECT * FROM users WHERE id = 1";
        SQLParser.ParseResult result = sqlParser.parse(sql);

        assertTrue(result.isSuccess());
        assertEquals(SQLParser.SQLType.SELECT, result.getSqlType());
        assertTrue(result.getTables().contains("users"));
    }

    @Test
    @DisplayName("测试解析为树结构")
    void testParseToTree() {
        String sql = "SELECT id, username, email FROM users WHERE id = 1 ORDER BY id LIMIT 10";
        ParseTree tree = sqlParser.parseToTree(sql);

        assertNotNull(tree);
        assertEquals("SELECT", tree.getStatementType());
        assertEquals("users", tree.getTableName());
        assertEquals(3, tree.getColumns().size());
        assertTrue(tree.isHasLimit());
        assertEquals(10, tree.getLimit());
    }

    @Test
    @DisplayName("测试SQL注入检测")
    void testDetectSqlInjection() {
        String normalSql = "SELECT * FROM users WHERE username = 'admin'";
        String injectionSql = "SELECT * FROM users WHERE username = 'admin' OR '1'='1'";

        assertFalse(sqlParser.detectSqlInjection(normalSql));
        assertTrue(sqlParser.detectSqlInjection(injectionSql));
    }

    @Test
    @DisplayName("测试SQL格式化")
    void testFormat() {
        String messySql = "SELECT   *   FROM   users   WHERE   id   =   1";
        String formatted = sqlParser.format(messySql);
        assertEquals("SELECT * FROM users WHERE id = 1", formatted);
    }
}
```

### 4.4 QueryOptimizerTest.java

```java
package com.linsir.abc.mysql.chapter01.architecture.server;

import com.linsir.abc.mysql.chapter01.architecture.server.optimizer.QueryOptimizer;
import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryOptimizerTest {

    private QueryOptimizer queryOptimizer;
    private SQLParser sqlParser;

    @BeforeEach
    void setUp() {
        queryOptimizer = new QueryOptimizer();
        sqlParser = new SQLParser();
    }

    @Test
    @DisplayName("测试优化带WHERE条件的SELECT")
    void testOptimizeSelectWithWhere() {
        String sql = "SELECT * FROM users WHERE id = 1";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.INDEX_SCAN, plan.getStrategy());
    }

    @Test
    @DisplayName("测试优化不带WHERE条件的SELECT")
    void testOptimizeSelectWithoutWhere() {
        String sql = "SELECT * FROM users";
        SQLParser.ParseResult parseResult = sqlParser.parse(sql);
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(parseResult);

        assertTrue(plan.isSuccess());
        assertEquals(QueryOptimizer.ExecutionStrategy.FULL_SCAN, plan.getStrategy());
    }

    @Test
    @DisplayName("测试优化失败的解析结果")
    void testOptimizeFailedParse() {
        SQLParser.ParseResult failedResult = SQLParser.ParseResult.fail("解析失败");
        QueryOptimizer.ExecutionPlan plan = queryOptimizer.optimize(failedResult);

        assertNotNull(plan);
        assertFalse(plan.isSuccess());
    }
}
```

***

## 五、测试执行

运行所有单元测试：

```bash
mvn test
```

运行特定测试类：

```bash
mvn test -Dtest=SQLParserTest
mvn test -Dtest=QueryOptimizerTest
mvn test -Dtest=ConnectionManagerTest
mvn test -Dtest=AuthenticatorTest
```

测试报告位置：`target/surefire-reports/`
