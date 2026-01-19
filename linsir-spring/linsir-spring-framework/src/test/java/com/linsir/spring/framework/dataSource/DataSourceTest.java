package com.linsir.spring.framework.dataSource;

import com.linsir.spring.framework.config.DataSourceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

/**
 * @ClassName : DataSourceTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-05 12:31
 */

@SpringJUnitConfig(classes = DataSourceTestConfig.class)
@ActiveProfiles("dev")
public class DataSourceTest {

    @Autowired
    DataSource dataSource;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    void dataSourceTest()
    {
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(
                System.out::println
        );

    }

    @Test
    void getConnTest() throws SQLException {
        Connection connection = dataSource.getConnection();
        System.out.println(connection);
    }

    @Test
    void sqlTest() throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        String sql ="select * from users";
        ResultSet resultSet = statement.executeQuery(sql);
    }
}
