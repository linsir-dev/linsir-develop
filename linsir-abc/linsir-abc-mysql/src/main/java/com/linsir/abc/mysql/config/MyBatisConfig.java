package com.linsir.abc.mysql.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * MyBatis配置类
 *
 * 职责：
 * 1. 配置SqlSessionFactory
 * 2. 配置事务管理器
 *
 * 说明：
 * 本项目使用注解方式定义Mapper（@Mapper + @Select/@Insert等），
 * 无需配置XML文件位置。Mapper接口通过@MapperScan在启动类中扫描。
 *
 * @author linsir
 * @since 1.0.0
 */
@Configuration
@EnableTransactionManagement
public class MyBatisConfig {

    /**
     * 配置SqlSessionFactory
     *
     * @param dataSource 数据源
     * @return SqlSessionFactory
     * @throws Exception 异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 设置实体类别名包
        factoryBean.setTypeAliasesPackage("com.linsir.abc.mysql.chapter01.architecture.entity");

        // 配置MyBatis设置
        org.apache.ibatis.session.Configuration configuration =
                new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        configuration.setLazyLoadingEnabled(false);
        configuration.setMultipleResultSetsEnabled(true);
        configuration.setUseColumnLabel(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setAutoMappingBehavior(
                org.apache.ibatis.session.AutoMappingBehavior.PARTIAL
        );

        factoryBean.setConfiguration(configuration);

        return factoryBean.getObject();
    }

    /**
     * 配置SqlSessionTemplate
     *
     * @param sqlSessionFactory SqlSessionFactory
     * @return SqlSessionTemplate
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    /**
     * 配置事务管理器
     *
     * @param dataSource 数据源
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
