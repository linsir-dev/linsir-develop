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
