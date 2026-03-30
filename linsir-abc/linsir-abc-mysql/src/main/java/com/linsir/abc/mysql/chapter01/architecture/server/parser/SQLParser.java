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
 *
 * 职责：
 * 1. 词法分析：将SQL拆分为Token
 * 2. 语法分析：检查SQL语法
 * 3. 提取SQL信息：表名、字段、条件等
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class SQLParser {

    /**
     * SQL类型正则 - SELECT
     */
    private static final Pattern SELECT_PATTERN = Pattern.compile("^\\s*SELECT\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * SQL类型正则 - INSERT
     */
    private static final Pattern INSERT_PATTERN = Pattern.compile("^\\s*INSERT\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * SQL类型正则 - UPDATE
     */
    private static final Pattern UPDATE_PATTERN = Pattern.compile("^\\s*UPDATE\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * SQL类型正则 - DELETE
     */
    private static final Pattern DELETE_PATTERN = Pattern.compile("^\\s*DELETE\\s+", Pattern.CASE_INSENSITIVE);

    /**
     * 表名提取正则
     */
    private static final Pattern TABLE_PATTERN = Pattern.compile(
            "(?:FROM|INTO|UPDATE|JOIN)\\s+(`?)(\\w+)\\1",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * 解析SQL
     *
     * @param sql SQL语句
     * @return 解析结果
     */
    public ParseResult parse(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return ParseResult.fail("SQL不能为空");
        }

        try {
            // 1. 词法分析
            List<Token> tokens = tokenize(sql);

            // 2. 识别SQL类型
            SQLType sqlType = identifySQLType(sql);

            // 3. 提取表名
            List<String> tables = extractTables(sql);

            // 4. 构建解析结果
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

    /**
     * 词法分析：将SQL拆分为Token
     *
     * @param sql SQL语句
     * @return Token列表
     */
    public List<Token> tokenize(String sql) {
        List<Token> tokens = new ArrayList<>();
        String[] words = sql.split("\\s+");

        int position = 0;
        for (String word : words) {
            if (word.trim().isEmpty()) {
                continue;
            }

            TokenType type = identifyTokenType(word);
            tokens.add(new Token(type, word, position));
            position += word.length() + 1;
        }

        return tokens;
    }

    /**
     * 识别Token类型
     *
     * @param word 单词
     * @return Token类型
     */
    private TokenType identifyTokenType(String word) {
        String upper = word.toUpperCase();

        // 关键字
        Set<String> keywords = Set.of(
                "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE",
                "VALUES", "SET", "AND", "OR", "NOT", "NULL", "IS",
                "ORDER", "BY", "GROUP", "HAVING", "LIMIT", "JOIN",
                "LEFT", "RIGHT", "INNER", "OUTER", "ON", "AS"
        );

        if (keywords.contains(upper)) {
            return TokenType.KEYWORD;
        }

        // 操作符
        if (word.matches("[=<>!]+")) {
            return TokenType.OPERATOR;
        }

        // 数字
        if (word.matches("-?\\d+(\\.\\d+)?")) {
            return TokenType.NUMBER;
        }

        // 字符串
        if (word.startsWith("'") || word.startsWith("\"")) {
            return TokenType.STRING;
        }

        return TokenType.IDENTIFIER;
    }

    /**
     * 识别SQL类型
     *
     * @param sql SQL语句
     * @return SQL类型
     */
    private SQLType identifySQLType(String sql) {
        if (SELECT_PATTERN.matcher(sql).find()) {
            return SQLType.SELECT;
        } else if (INSERT_PATTERN.matcher(sql).find()) {
            return SQLType.INSERT;
        } else if (UPDATE_PATTERN.matcher(sql).find()) {
            return SQLType.UPDATE;
        } else if (DELETE_PATTERN.matcher(sql).find()) {
            return SQLType.DELETE;
        }
        return SQLType.UNKNOWN;
    }

    /**
     * 提取表名
     *
     * @param sql SQL语句
     * @return 表名列表
     */
    private List<String> extractTables(String sql) {
        List<String> tables = new ArrayList<>();
        Matcher matcher = TABLE_PATTERN.matcher(sql);

        while (matcher.find()) {
            tables.add(matcher.group(2));
        }

        return tables;
    }

    /**
     * 解析SQL并返回解析树（用于测试）
     *
     * @param sql SQL语句
     * @return 解析树
     */
    public ParseTree parseToTree(String sql) {
        ParseResult result = parse(sql);
        if (!result.isSuccess()) {
            throw new RuntimeException("SQL解析失败: " + result.getMessage());
        }

        ParseTree tree = new ParseTree();
        tree.setStatementType(result.getSqlType().name());
        tree.setTableName(result.getTables().isEmpty() ? null : result.getTables().get(0));

        // 提取列名
        tree.setColumns(extractColumns(sql));

        // 提取WHERE条件
        tree.setWhereClause(extractWhereClause(sql));

        // 提取ORDER BY
        tree.setOrderByClause(extractOrderByClause(sql));
        tree.setHasOrderBy(tree.getOrderByClause() != null);

        // 提取LIMIT
        extractLimit(sql, tree);

        // 检查JOIN
        tree.setHasJoin(sql.toUpperCase().contains("JOIN"));
        if (tree.isHasJoin()) {
            tree.setJoinType(extractJoinType(sql));
            tree.setJoinTable(extractJoinTable(sql));
        }

        // 检查聚合函数
        tree.setHasAggregateFunction(checkAggregateFunction(sql));
        if (tree.isHasAggregateFunction()) {
            tree.setAggregateFunctions(extractAggregateFunctions(sql));
        }

        // 检查子查询
        tree.setHasSubquery(sql.contains("("));

        // 检查GROUP BY
        tree.setHasGroupBy(sql.toUpperCase().contains("GROUP BY"));

        return tree;
    }

    /**
     * 提取列名
     */
    private List<String> extractColumns(String sql) {
        List<String> columns = new ArrayList<>();
        Matcher matcher = Pattern.compile("SELECT\\s+(.*?)\\s+FROM", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            String cols = matcher.group(1);
            if ("*".equals(cols.trim())) {
                columns.add("*");
            } else {
                for (String col : cols.split(",")) {
                    columns.add(col.trim().replaceAll("\\s+AS\\s+\\w+", "").replaceAll("\\w+\\.", ""));
                }
            }
        }
        return columns;
    }

    /**
     * 提取WHERE条件
     */
    private String extractWhereClause(String sql) {
        Matcher matcher = Pattern.compile("WHERE\\s+(.*?)(?:ORDER BY|GROUP BY|LIMIT|$)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * 提取ORDER BY条件
     */
    private String extractOrderByClause(String sql) {
        Matcher matcher = Pattern.compile("ORDER\\s+BY\\s+(.*?)(?:LIMIT|$)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * 提取LIMIT和OFFSET
     */
    private void extractLimit(String sql, ParseTree tree) {
        Matcher matcher = Pattern.compile("LIMIT\\s+(\\d+)(?:\\s+OFFSET\\s+(\\d+))?", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            tree.setLimit(Integer.parseInt(matcher.group(1)));
            tree.setHasLimit(true);
            if (matcher.group(2) != null) {
                tree.setOffset(Integer.parseInt(matcher.group(2)));
            }
        }
    }

    /**
     * 提取JOIN类型
     */
    private String extractJoinType(String sql) {
        if (sql.toUpperCase().contains("INNER JOIN")) return "INNER JOIN";
        if (sql.toUpperCase().contains("LEFT JOIN")) return "LEFT JOIN";
        if (sql.toUpperCase().contains("RIGHT JOIN")) return "RIGHT JOIN";
        return "JOIN";
    }

    /**
     * 提取JOIN表名
     */
    private String extractJoinTable(String sql) {
        Matcher matcher = Pattern.compile("JOIN\\s+(\\w+)", Pattern.CASE_INSENSITIVE).matcher(sql);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 检查是否有聚合函数
     */
    private boolean checkAggregateFunction(String sql) {
        return Pattern.compile("(COUNT|SUM|AVG|MAX|MIN)\\s*\\(", Pattern.CASE_INSENSITIVE).matcher(sql).find();
    }

    /**
     * 提取聚合函数
     */
    private List<String> extractAggregateFunctions(String sql) {
        List<String> functions = new ArrayList<>();
        Matcher matcher = Pattern.compile("(COUNT|SUM|AVG|MAX|MIN)\\s*\\([^)]+\\)", Pattern.CASE_INSENSITIVE).matcher(sql);
        while (matcher.find()) {
            functions.add(matcher.group());
        }
        return functions;
    }

    /**
     * 检测SQL注入
     */
    public boolean detectSqlInjection(String sql) {
        String[] patterns = {"OR '1'='1", "DROP TABLE", "UNION SELECT", ";--", "/*", "*/"};
        String upperSql = sql.toUpperCase();
        for (String pattern : patterns) {
            if (upperSql.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 格式化SQL
     */
    public String format(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }

    /**
     * 验证SQL语法
     *
     * @param sql SQL语句
     * @return true-语法正确
     */
    public boolean validateSyntax(String sql) {
        // 简单验证：括号匹配
        int bracketCount = 0;
        for (char c : sql.toCharArray()) {
            if (c == '(') bracketCount++;
            if (c == ')') bracketCount--;
            if (bracketCount < 0) return false;
        }
        return bracketCount == 0;
    }

    /**
     * SQL类型枚举
     */
    public enum SQLType {
        SELECT, INSERT, UPDATE, DELETE, UNKNOWN
    }

    /**
     * Token类型枚举
     */
    public enum TokenType {
        KEYWORD, IDENTIFIER, OPERATOR, NUMBER, STRING, SYMBOL
    }

    /**
     * Token类
     */
    @Data
    public static class Token {
        /**
         * Token类型
         */
        private TokenType type;

        /**
         * Token值
         */
        private String value;

        /**
         * 位置
         */
        private int position;

        public Token(TokenType type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }
    }

    /**
     * 解析结果
     */
    @Data
    public static class ParseResult {
        /**
         * 是否成功
         */
        private boolean success;

        /**
         * SQL类型
         */
        private SQLType sqlType;

        /**
         * 涉及的表
         */
        private List<String> tables;

        /**
         * Token列表
         */
        private List<Token> tokens;

        /**
         * 原始SQL
         */
        private String originalSql;

        /**
         * 错误信息
         */
        private String message;

        public static ParseResult fail(String message) {
            ParseResult result = new ParseResult();
            result.setSuccess(false);
            result.setMessage(message);
            return result;
        }
    }
}
