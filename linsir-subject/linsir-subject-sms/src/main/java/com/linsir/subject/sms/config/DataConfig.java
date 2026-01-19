package com.linsir.subject.sms.config;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @ClassName : DataConfig
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-11 17:40
 */

@Configuration
@MapperScan("com.linsir.subject.sms.mapper")
public class DataConfig {

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
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDbType(DbType.mysql);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(name);
        dataSource.setPassword(password);
        return dataSource;
    }


    @Bean
    public SqlSessionFactoryBean  sqlSessionFactoryBean(DataSource dataSource)
    {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean;
    }


}
