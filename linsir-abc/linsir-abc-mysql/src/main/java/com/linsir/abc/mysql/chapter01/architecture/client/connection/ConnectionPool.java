package com.linsir.abc.mysql.chapter01.architecture.client.connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * 连接池配置
 * 配置HikariCP连接池，模拟MySQL连接池管理
 *
 * HikariCP特点：
 * 1. 高性能，低延迟
 * 2. 轻量级，代码精简
 * 3. 可靠性高
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class ConnectionPool {

    /**
     * 数据库URL
     */
    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    /**
     * 数据库用户名
     */
    @Value("${spring.datasource.username}")
    private String username;

    /**
     * 数据库密码
     */
    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 驱动类名
     */
    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    /**
     * 配置数据源
     *
     * @return 数据源
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driverClassName);

        // 连接池配置
        config.setPoolName("MySQL-HikariPool");
        config.setMinimumIdle(5);              // 最小空闲连接数
        config.setMaximumPoolSize(20);         // 最大连接数
        config.setIdleTimeout(300000);         // 空闲连接超时时间(5分钟)
        config.setMaxLifetime(1800000);        // 连接最大生命周期(30分钟)
        config.setConnectionTimeout(30000);    // 连接超时时间(30秒)
        config.setConnectionTestQuery("SELECT 1"); // 连接测试SQL

        // 性能优化配置
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        HikariDataSource dataSource = new HikariDataSource(config);
        log.info("HikariCP连接池初始化完成: url={}", jdbcUrl);

        return dataSource;
    }

    /**
     * 连接池监控信息
     */
    @Data
    public static class PoolMetrics {
        /**
         * 连接池名称
         */
        private String poolName;

        /**
         * 活跃连接数
         */
        private int activeConnections;

        /**
         * 空闲连接数
         */
        private int idleConnections;

        /**
         * 总连接数
         */
        private int totalConnections;

        /**
         * 等待线程数
         */
        private int waitingThreads;

        /**
         * 连接获取平均耗时(ms)
         */
        private long averageConnectionAcquireTime;
    }
}
