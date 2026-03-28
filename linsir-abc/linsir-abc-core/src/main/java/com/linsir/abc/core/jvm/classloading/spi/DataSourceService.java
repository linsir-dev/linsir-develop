package com.linsir.abc.core.jvm.classloading.spi;

/**
 * 数据源服务接口
 * 用于演示SPI机制
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public interface DataSourceService {

    /**
     * 获取数据源名称
     *
     * @return 数据源名称
     */
    String getDataSourceName();

    /**
     * 获取数据源连接
     *
     * @return 连接字符串
     */
    String getConnection();

    /**
     * 测试连接
     *
     * @return true表示连接成功
     */
    boolean testConnection();
}
