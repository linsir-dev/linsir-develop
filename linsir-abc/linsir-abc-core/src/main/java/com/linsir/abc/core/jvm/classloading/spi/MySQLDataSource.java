package com.linsir.abc.core.jvm.classloading.spi;

/**
 * MySQL数据源实现
 * SPI服务实现类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class MySQLDataSource implements DataSourceService {

    @Override
    public String getDataSourceName() {
        return "MySQL";
    }

    @Override
    public String getConnection() {
        return "jdbc:mysql://localhost:3306/test";
    }

    @Override
    public boolean testConnection() {
        System.out.println("Testing MySQL connection...");
        return true;
    }
}
