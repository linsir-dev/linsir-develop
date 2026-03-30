package com.linsir.abc.mysql.chapter01.architecture.server.executor;

import com.linsir.abc.mysql.chapter01.architecture.engine.StorageEngine;
import com.linsir.abc.mysql.chapter01.architecture.server.optimizer.QueryOptimizer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 查询执行器
 * 模拟MySQL服务层的执行器功能
 *
 * 职责：
 * 1. 执行查询计划
 * 2. 调用存储引擎
 * 3. 返回结果集
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class QueryExecutor {

    /**
     * 存储引擎
     */
    private final StorageEngine storageEngine;

    public QueryExecutor(StorageEngine storageEngine) {
        this.storageEngine = storageEngine;
    }

    /**
     * 执行查询
     *
     * @param connection 数据库连接
     * @param plan       执行计划
     * @return 执行结果
     */
    public ExecutionResult execute(Connection connection, QueryOptimizer.ExecutionPlan plan) {
        long startTime = System.currentTimeMillis();

        try {
            log.debug("开始执行查询: strategy={}", plan.getStrategy());

            // 根据策略执行
            List<Map<String, Object>> results = switch (plan.getSqlType()) {
                case SELECT -> executeSelect(connection, plan);
                case INSERT -> executeInsert(connection, plan);
                case UPDATE -> executeUpdate(connection, plan);
                case DELETE -> executeDelete(connection, plan);
                default -> throw new UnsupportedOperationException("不支持的SQL类型");
            };

            long costTime = System.currentTimeMillis() - startTime;

            ExecutionResult result = new ExecutionResult();
            result.setSuccess(true);
            result.setData(results);
            result.setCostTime(costTime);
            result.setRowCount(results.size());

            log.debug("查询执行完成: costTime={}ms, rowCount={}", costTime, results.size());
            return result;

        } catch (Exception e) {
            log.error("查询执行失败: {}", e.getMessage());
            return ExecutionResult.fail(e.getMessage());
        }
    }

    /**
     * 执行SELECT
     */
    private List<Map<String, Object>> executeSelect(Connection connection,
                                                     QueryOptimizer.ExecutionPlan plan) throws SQLException {
        return storageEngine.executeQuery(connection, plan.getOriginalSql());
    }

    /**
     * 执行INSERT
     */
    private List<Map<String, Object>> executeInsert(Connection connection,
                                                     QueryOptimizer.ExecutionPlan plan) throws SQLException {
        int affectedRows = storageEngine.executeUpdate(connection, plan.getOriginalSql());
        return List.of(Map.of("affectedRows", affectedRows));
    }

    /**
     * 执行UPDATE
     */
    private List<Map<String, Object>> executeUpdate(Connection connection,
                                                     QueryOptimizer.ExecutionPlan plan) throws SQLException {
        int affectedRows = storageEngine.executeUpdate(connection, plan.getOriginalSql());
        return List.of(Map.of("affectedRows", affectedRows));
    }

    /**
     * 执行DELETE
     */
    private List<Map<String, Object>> executeDelete(Connection connection,
                                                     QueryOptimizer.ExecutionPlan plan) throws SQLException {
        int affectedRows = storageEngine.executeUpdate(connection, plan.getOriginalSql());
        return List.of(Map.of("affectedRows", affectedRows));
    }

    /**
     * 执行结果
     */
    @Data
    public static class ExecutionResult {
        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 结果数据
         */
        private List<Map<String, Object>> data;

        /**
         * 影响行数
         */
        private int rowCount;

        /**
         * 执行耗时(ms)
         */
        private long costTime;

        /**
         * 错误信息
         */
        private String errorMessage;

        public static ExecutionResult fail(String message) {
            ExecutionResult result = new ExecutionResult();
            result.setSuccess(false);
            result.setErrorMessage(message);
            return result;
        }
    }
}
