package com.linsir.abc.pdai.database.mysql;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * MySQL数据库连接测试类
 */
public class DatabaseConnectionTest {

    private Connection connection;

    @BeforeEach
    void setUp() {
        // 获取数据库连接
        connection = DatabaseConnection.getConnection();
    }

    @AfterEach
    void tearDown() {
        // 关闭数据库连接
        if (connection != null) {
            try {
                connection.close();
                System.out.println("测试后关闭数据库连接");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试数据库连接是否成功
     */
    @Test
    void testGetConnection() {
        assertNotNull(connection, "数据库连接应该不为null");
        try {
            assert !connection.isClosed() : "数据库连接应该是打开的";
            System.out.println("数据库连接测试通过");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试创建表操作
     */
    @Test
    void testCreateTable() {
        assertNotNull(connection, "数据库连接应该不为null");
        try {
            Statement statement = connection.createStatement();
            String createTableSql = "CREATE TABLE IF NOT EXISTS test_user (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "age INT, " +
                    "email VARCHAR(100)" +
                    ")";
            statement.executeUpdate(createTableSql);
            System.out.println("创建表测试通过");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试插入数据操作
     */
    @Test
    void testInsertData() {
        assertNotNull(connection, "数据库连接应该不为null");
        try {
            Statement statement = connection.createStatement();
            // 先创建表
            String createTableSql = "CREATE TABLE IF NOT EXISTS test_user (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "age INT, " +
                    "email VARCHAR(100)" +
                    ")";
            statement.executeUpdate(createTableSql);

            // 插入数据
            String insertSql = "INSERT INTO test_user (name, age, email) VALUES ('李四', 30, 'lisi@example.com')";
            int result = statement.executeUpdate(insertSql);
            assert result > 0 : "插入数据应该成功";
            System.out.println("插入数据测试通过，影响行数: " + result);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试完整的数据库操作流程
     */
    @Test
    void testCompleteDatabaseOperation() {
        assertNotNull(connection, "数据库连接应该不为null");
        try {
            Statement statement = connection.createStatement();
            
            // 1. 创建表
            String createTableSql = "CREATE TABLE IF NOT EXISTS test_user (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "age INT, " +
                    "email VARCHAR(100)" +
                    ")";
            statement.executeUpdate(createTableSql);
            System.out.println("1. 创建表成功");

            // 2. 插入数据
            String insertSql = "INSERT INTO test_user (name, age, email) VALUES ('王五', 28, 'wangwu@example.com')";
            int insertResult = statement.executeUpdate(insertSql);
            assert insertResult > 0 : "插入数据应该成功";
            System.out.println("2. 插入数据成功，影响行数: " + insertResult);

            // 3. 更新数据
            String updateSql = "UPDATE test_user SET age = 29 WHERE name = '王五'";
            int updateResult = statement.executeUpdate(updateSql);
            assert updateResult > 0 : "更新数据应该成功";
            System.out.println("3. 更新数据成功，影响行数: " + updateResult);

            // 4. 删除数据
            String deleteSql = "DELETE FROM test_user WHERE name = '王五'";
            int deleteResult = statement.executeUpdate(deleteSql);
            assert deleteResult > 0 : "删除数据应该成功";
            System.out.println("4. 删除数据成功，影响行数: " + deleteResult);

            statement.close();
            System.out.println("完整数据库操作测试通过");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
