package com.linsir.spring.framework.dataSource;

import com.linsir.spring.framework.config.DataSourceTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

/**
 * @ClassName : JDBCTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-07 20:52
 */

@SpringJUnitConfig(classes = DataSourceTestConfig.class)
@ActiveProfiles("test")
public class JDBCTest {

    @Autowired
    public DataSource dataSource;

    public JdbcClient jdbcClient;


    @BeforeEach
    public void setUp()
    {
        jdbcClient = JdbcClient.create(dataSource);
    }
    @Test
    public void jdbcTest(){
      System.out.println(JdbcTestUtils.countRowsInTable(jdbcClient,"user"));
    }
}
