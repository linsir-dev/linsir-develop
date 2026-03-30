package com.linsir.abc.mysql.chapter01.architecture.server.optimizer;

import com.linsir.abc.mysql.chapter01.architecture.server.parser.SQLParser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 查询优化器
 * 模拟MySQL服务层的优化器功能
 *
 * 职责：
 * 1. 分析查询条件
 * 2. 选择最优执行计划
 * 3. 估算查询成本
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class QueryOptimizer {

    /**
     * 优化查询
     *
     * @param parseResult 解析结果
     * @return 执行计划
     */
    public ExecutionPlan optimize(SQLParser.ParseResult parseResult) {
        if (!parseResult.isSuccess()) {
            return ExecutionPlan.fail("解析失败，无法优化");
        }

        ExecutionPlan plan = new ExecutionPlan();
        plan.setSuccess(true);
        plan.setOriginalSql(parseResult.getOriginalSql());
        plan.setSqlType(parseResult.getSqlType());
        plan.setTables(parseResult.getTables());

        // 根据SQL类型选择优化策略
        switch (parseResult.getSqlType()) {
            case SELECT -> optimizeSelect(plan, parseResult);
            case INSERT -> optimizeInsert(plan, parseResult);
            case UPDATE -> optimizeUpdate(plan, parseResult);
            case DELETE -> optimizeDelete(plan, parseResult);
            default -> plan.setStrategy(ExecutionStrategy.FULL_SCAN);
        }

        // 估算成本
        estimateCost(plan);

        log.debug("查询优化完成: strategy={}, estimatedCost={}",
                plan.getStrategy(), plan.getEstimatedCost());

        return plan;
    }

    /**
     * 优化SELECT查询
     */
    private void optimizeSelect(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        String sql = parseResult.getOriginalSql().toUpperCase();

        // 检查是否有WHERE条件
        if (sql.contains("WHERE")) {
            // 分析WHERE条件
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

        // 检查是否需要排序
        if (sql.contains("ORDER BY")) {
            plan.setNeedSort(true);
            plan.setSortFields(extractOrderByFields(sql));
        }

        // 检查是否有限制
        if (sql.contains("LIMIT")) {
            plan.setHasLimit(true);
        }
    }

    /**
     * 优化INSERT
     */
    private void optimizeInsert(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
        plan.setIndexSuggestion("主键索引");
    }

    /**
     * 优化UPDATE
     */
    private void optimizeUpdate(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        String sql = parseResult.getOriginalSql().toUpperCase();

        if (sql.contains("WHERE")) {
            plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
        } else {
            plan.setStrategy(ExecutionStrategy.FULL_SCAN);
            plan.setWarning("UPDATE语句缺少WHERE条件，将更新全表");
        }
    }

    /**
     * 优化DELETE
     */
    private void optimizeDelete(ExecutionPlan plan, SQLParser.ParseResult parseResult) {
        String sql = parseResult.getOriginalSql().toUpperCase();

        if (sql.contains("WHERE")) {
            plan.setStrategy(ExecutionStrategy.INDEX_SCAN);
        } else {
            plan.setStrategy(ExecutionStrategy.FULL_SCAN);
            plan.setWarning("DELETE语句缺少WHERE条件，将删除全表数据");
        }
    }

    /**
     * 提取WHERE条件
     */
    private List<Condition> extractConditions(String sql) {
        List<Condition> conditions = new ArrayList<>();
        // 简化实现，实际应使用更复杂的解析逻辑
        return conditions;
    }

    /**
     * 提取ORDER BY字段
     */
    private List<String> extractOrderByFields(String sql) {
        List<String> fields = new ArrayList<>();
        // 简化实现
        return fields;
    }

    /**
     * 估算执行成本
     */
    private void estimateCost(ExecutionPlan plan) {
        long baseCost = 100;

        switch (plan.getStrategy()) {
            case FULL_SCAN -> baseCost *= 10;
            case INDEX_SCAN -> baseCost *= 2;
            case RANGE_SCAN -> baseCost *= 3;
        }

        if (plan.isNeedSort()) {
            baseCost += 50;
        }

        plan.setEstimatedCost(baseCost);
    }

    /**
     * 执行策略枚举
     */
    public enum ExecutionStrategy {
        FULL_SCAN("全表扫描"),
        INDEX_SCAN("索引扫描"),
        RANGE_SCAN("范围扫描"),
        CONST("常量查询");

        private final String description;

        ExecutionStrategy(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 执行计划
     */
    @Data
    public static class ExecutionPlan {
        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 原始SQL
         */
        private String originalSql;

        /**
         * SQL类型
         */
        private SQLParser.SQLType sqlType;

        /**
         * 涉及的表
         */
        private List<String> tables;

        /**
         * 执行策略
         */
        private ExecutionStrategy strategy;

        /**
         * 索引建议
         */
        private String indexSuggestion;

        /**
         * 查询条件
         */
        private List<Condition> conditions;

        /**
         * 是否需要排序
         */
        private boolean needSort;

        /**
         * 排序字段
         */
        private List<String> sortFields;

        /**
         * 是否有限制
         */
        private boolean hasLimit;

        /**
         * 估算成本
         */
        private long estimatedCost;

        /**
         * 警告信息
         */
        private String warning;

        public static ExecutionPlan fail(String message) {
            ExecutionPlan plan = new ExecutionPlan();
            plan.setSuccess(false);
            return plan;
        }
    }

    /**
     * 查询条件
     */
    @Data
    public static class Condition {
        /**
         * 字段名
         */
        private String field;

        /**
         * 操作符
         */
        private String operator;

        /**
         * 值
         */
        private String value;
    }
}
