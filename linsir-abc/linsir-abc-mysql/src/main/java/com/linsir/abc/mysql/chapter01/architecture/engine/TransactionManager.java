package com.linsir.abc.mysql.chapter01.architecture.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事务管理器
 * 管理数据库事务的生命周期
 *
 * 职责：
 * 1. 事务的开始、提交、回滚
 * 2. 事务隔离级别管理
 * 3. 分布式事务支持（简化版）
 *
 * ACID特性：
 * - Atomicity（原子性）：事务是不可分割的工作单位
 * - Consistency（一致性）：事务执行前后数据保持一致
 * - Isolation（隔离性）：事务之间相互隔离
 * - Durability（持久性）：事务提交后数据永久保存
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Component("myTransactionManager")
public class TransactionManager {

    /**
     * 数据源
     */
    private final DataSource dataSource;

    /**
     * 事务连接持有者
     * key: 线程ID, value: 连接
     */
    private final ConcurrentHashMap<Long, Connection> transactionHolder;

    public TransactionManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.transactionHolder = new ConcurrentHashMap<>();
    }

    /**
     * 开始事务
     *
     * @throws SQLException SQL异常
     */
    public void beginTransaction() throws SQLException {
        long threadId = Thread.currentThread().getId();

        if (transactionHolder.containsKey(threadId)) {
            throw new SQLException("当前线程已有活动事务");
        }

        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        transactionHolder.put(threadId, connection);

        log.debug("[Transaction] 开始事务: threadId={}", threadId);
    }

    /**
     * 提交事务
     *
     * @throws SQLException SQL异常
     */
    public void commit() throws SQLException {
        long threadId = Thread.currentThread().getId();
        Connection connection = transactionHolder.get(threadId);

        if (connection == null) {
            throw new SQLException("当前线程没有活动事务");
        }

        try {
            connection.commit();
            log.debug("[Transaction] 提交事务: threadId={}", threadId);
        } finally {
            closeConnection(connection);
            transactionHolder.remove(threadId);
        }
    }

    /**
     * 回滚事务
     *
     * @throws SQLException SQL异常
     */
    public void rollback() throws SQLException {
        long threadId = Thread.currentThread().getId();
        Connection connection = transactionHolder.get(threadId);

        if (connection == null) {
            throw new SQLException("当前线程没有活动事务");
        }

        try {
            connection.rollback();
            log.debug("[Transaction] 回滚事务: threadId={}", threadId);
        } finally {
            closeConnection(connection);
            transactionHolder.remove(threadId);
        }
    }

    /**
     * 获取当前事务连接
     *
     * @return 连接
     * @throws SQLException SQL异常
     */
    public Connection getCurrentConnection() throws SQLException {
        long threadId = Thread.currentThread().getId();
        Connection connection = transactionHolder.get(threadId);

        if (connection == null) {
            // 没有事务时返回普通连接
            return dataSource.getConnection();
        }

        return connection;
    }

    /**
     * 检查当前线程是否在事务中
     *
     * @return true-在事务中
     */
    public boolean isInTransaction() {
        return transactionHolder.containsKey(Thread.currentThread().getId());
    }

    /**
     * 关闭连接
     *
     * @param connection 连接
     */
    private void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.setAutoCommit(true);
                connection.close();
            }
        } catch (SQLException e) {
            log.error("关闭连接失败: {}", e.getMessage());
        }
    }

    /**
     * 设置事务隔离级别
     *
     * @param level 隔离级别
     * @throws SQLException SQL异常
     */
    public void setIsolationLevel(int level) throws SQLException {
        Connection connection = getCurrentConnection();
        connection.setTransactionIsolation(level);
        log.debug("[Transaction] 设置隔离级别: level={}", level);
    }

    /**
     * 事务隔离级别常量
     */
    public static class IsolationLevel {
        /**
         * 读未提交
         * 可能出现：脏读、不可重复读、幻读
         */
        public static final int READ_UNCOMMITTED = Connection.TRANSACTION_READ_UNCOMMITTED;

        /**
         * 读已提交
         * 可能出现：不可重复读、幻读
         */
        public static final int READ_COMMITTED = Connection.TRANSACTION_READ_COMMITTED;

        /**
         * 可重复读（MySQL默认）
         * 可能出现：幻读
         */
        public static final int REPEATABLE_READ = Connection.TRANSACTION_REPEATABLE_READ;

        /**
         * 串行化
         * 最高隔离级别，完全避免并发问题
         */
        public static final int SERIALIZABLE = Connection.TRANSACTION_SERIALIZABLE;
    }
}
