package com.linsir.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.transaction.TransactionManager;


import javax.sql.DataSource;
import java.text.SimpleDateFormat;


@Getter
@Configuration
@EnableJdbcRepositories("com.linsir.repositories")
public class ApplicationConfig extends AbstractJdbcConfiguration {


    @Resource
    private DataSource dataSource;


    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(dataSource);
    }


    @Bean
    public NamedParameterJdbcOperations namedParameterJdbcOperations() {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public TransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(){
        MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
        ObjectMapper om=new ObjectMapper();
        om.setDateFormat(new SimpleDateFormat("YYYY/MM/dd"));
        converter.setObjectMapper(om);
        return converter;
    }


}
