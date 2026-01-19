package com.linsir.spring.framework.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

import javax.sql.DataSource;

import static org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2;

/**
 * @ClassName : DataSourceTestConfig
 * @Description :
 *
 * EmbeddedDatabaseBuilder 类提供了一个 fluent 的API，用于以编程方式构建一个嵌入式数据库。
 *
 * 当你需要在一个独立的环境中或者在一个独立的集成测试中创建一个嵌入式数据库时，你可以使用它，就像下面的例子：
 *
 *
 * @Author : Linsir
 * @Date: 2023-12-04 23:41
 */

@Configuration
public class DataSourceTestConfig {
    @Bean
    @Profile("dev")
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .generateUniqueName(true)
                .setType(H2)
                .setScriptEncoding("UTF-8")
                .ignoreFailedDrops(true)
                .addScript("/sql/schema.sql")
                /*.addScript("/sql/user_data.sql")*/
                .build();
    }


    @Bean
    @Profile("test")
    public DataSource mySqlDataSource()
    {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://106.55.181.141:3306/linsir-spring-framework?characterEncoding=utf8&useSSL=false");
        ds.setUsername("root");
        ds.setPassword("yu@2023");
        ds.setMaxIdle(5);//连接池最大空闲数
        ds.setMaxIdle(5);//连接池最大空闲数
        ds.setMinIdle(3);//连接池最小空闲数
        ds.setInitialSize(10);//初始化连接池时的连接数
        return ds;
    }

   /* @Bean
    public JdbcTemplate jdbcTemplate()
    {
        return  new JdbcTemplate(dataSource());
    }


    @Bean
    @Profile("test")
    public DataSource testDataSource()
    {
        return null;
    }*/
}
