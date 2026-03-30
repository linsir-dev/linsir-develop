package com.linsir.abc.mysql.chapter01.architecture.engine;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 存储引擎接口
 * 模拟MySQL存储引擎层的统一接口
 *
 * 职责：
 * 1. 定义数据存取标准接口
 * 2. 支持不同存储引擎实现
 *
 * 设计模式：策略模式
 * 不同存储引擎实现此接口，提供不同的存储特性
 *
 * @author linsir
 * @since 1.0.0
 */
public interface StorageEngine {

    /**
     * 获取引擎名称
     *
     * @return 引擎名称
     */
    String getEngineName();

    /**
     * 是否支持事务
     *
     * @return true-支持
     */
    boolean supportsTransactions();

    /**
     * 是否支持行级锁
     *
     * @return true-支持
     */
    boolean supportsRowLocking();

    /**
     * 是否支持外键
     *
     * @return true-支持
     */
    boolean supportsForeignKeys();

    /**
     * 开始事务
     *
     * @param connection 数据库连接
     * @throws SQLException SQL异常
     */
    void beginTransaction(Connection connection) throws SQLException;

    /**
     * 提交事务
     *
     * @param connection 数据库连接
     * @throws SQLException SQL异常
     */
    void commit(Connection connection) throws SQLException;

    /**
     * 回滚事务
     *
     * @param connection 数据库连接
     * @throws SQLException SQL异常
     */
    void rollback(Connection connection) throws SQLException;

    /**
     * 执行查询
     *
     * @param connection 数据库连接
     * @param sql        SQL语句
     * @param params     参数
     * @return 查询结果
     * @throws SQLException SQL异常
     */
    List<Map<String, Object>> executeQuery(Connection connection, String sql,
                                           Object... params) throws SQLException;

    /**
     * 执行查询（无参数）
     *
     * @param connection 数据库连接
     * @param sql        SQL语句
     * @return 查询结果
     * @throws SQLException SQL异常
     */
    default List<Map<String, Object>> executeQuery(Connection connection, String sql) throws SQLException {
        return executeQuery(connection, sql, new Object[0]);
    }

    /**
     * 执行更新
     *
     * @param connection 数据库连接
     * @param sql        SQL语句
     * @param params     参数
     * @return 影响行数
     * @throws SQLException SQL异常
     */
    int executeUpdate(Connection connection, String sql,
                      Object... params) throws SQLException;

    /**
     * 执行更新（无参数）
     *
     * @param connection 数据库连接
     * @param sql        SQL语句
     * @return 影响行数
     * @throws SQLException SQL异常
     */
    default int executeUpdate(Connection connection, String sql) throws SQLException {
        return executeUpdate(connection, sql, new Object[0]);
    }
}
