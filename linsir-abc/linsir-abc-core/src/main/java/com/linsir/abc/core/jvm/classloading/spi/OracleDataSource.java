package com.linsir.abc.core.jvm.classloading.spi;

/**
 * Oracle数据源实现
 * SPI服务实现类
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class OracleDataSource implements DataSourceService {

    @Override
    public String getDataSourceName() {
        return "Oracle";
    }

    @Override
    public String getConnection() {
        return "jdbc:oracle:thin:@localhost:1521:ORCL";
    }

    @Override
    public boolean testConnection() {
        System.out.println("Testing Oracle connection...");
        return true;
    }
}
