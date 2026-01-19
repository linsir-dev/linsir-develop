package com.linsir.spring.springmvc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @ClassName : ApplicationConfig
 * @Description : 应用配置
 * @Author : Linsir
 * @Date: 2023-12-09 01:45
 */

@Configuration
@PropertySource({"classpath:application.properties"})
@ComponentScan("com.linsir.spring.springmvc")
public class ApplicationConfig {


    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.name}")
    private String name;

    @Value("${jdbc.password}")
    private String password;

    @Bean
    public DataSource dataSource()
    {
       DriverManagerDataSource driverManagerDataSource = new  DriverManagerDataSource(url,name,password);
       driverManagerDataSource.setDriverClassName(driverClassName);
       return driverManagerDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource)
    {
        return new JdbcTemplate(dataSource);
    }

   /* @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource)
    {
        return new DataSourceTransactionManager(dataSource);
    }*/


}
