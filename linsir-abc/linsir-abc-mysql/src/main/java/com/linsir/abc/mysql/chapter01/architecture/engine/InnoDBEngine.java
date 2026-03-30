package com.linsir.abc.mysql.chapter01.architecture.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * InnoDB存储引擎实现
 * 模拟MySQL InnoDB存储引擎的核心功能
 *
 * 特性：
 * 1. 支持事务（ACID）
 * 2. 支持行级锁
 * 3. 支持外键
 * 4. 支持崩溃恢复
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component
public class InnoDBEngine implements StorageEngine {

    @Override
    public String getEngineName() {
        return "InnoDB";
    }

    @Override
    public boolean supportsTransactions() {
        return true;
    }

    @Override
    public boolean supportsRowLocking() {
        return true;
    }

    @Override
    public boolean supportsForeignKeys() {
        return true;
    }

    @Override
    public void beginTransaction(Connection connection) throws SQLException {
        connection.setAutoCommit(false);
        log.debug("[InnoDB] 开始事务");
    }

    @Override
    public void commit(Connection connection) throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
        log.debug("[InnoDB] 提交事务");
    }

    @Override
    public void rollback(Connection connection) throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
        log.debug("[InnoDB] 回滚事务");
    }

    @Override
    public List<Map<String, Object>> executeQuery(Connection connection, String sql,
                                                   Object... params) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // 执行查询
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    results.add(row);
                }
            }
        }

        log.debug("[InnoDB] 查询执行完成: 返回{}条记录", results.size());
        return results;
    }

    @Override
    public int executeUpdate(Connection connection, String sql,
                             Object... params) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // 设置参数
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // 执行更新
            int affectedRows = stmt.executeUpdate();
            log.debug("[InnoDB] 更新执行完成: 影响{}行", affectedRows);
            return affectedRows;
        }
    }
}
