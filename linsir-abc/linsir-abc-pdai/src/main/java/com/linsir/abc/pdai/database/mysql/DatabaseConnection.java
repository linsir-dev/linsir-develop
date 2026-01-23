package com.linsir.abc.pdai.database.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MySQL数据库连接示例
 */
public class DatabaseConnection {

    // 数据库连接参数
    private static final String URL = "jdbc:mysql://localhost:3306/linsir-abc-pdai?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    static {
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL驱动加载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     * @return 数据库连接对象
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("数据库连接成功");
        } catch (SQLException e) {
            System.err.println("数据库连接失败: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * 关闭数据库连接
     * @param connection 数据库连接对象
     * @param statement 语句对象
     * @param resultSet 结果集对象
     */
    public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
                System.out.println("数据库连接关闭成功");
            }
        } catch (SQLException e) {
            System.err.println("数据库连接关闭失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 测试数据库连接
     */
    public static void testConnection() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            // 获取连接
            connection = getConnection();

            // 创建表
            statement = connection.createStatement();
            String createTableSql = "CREATE TABLE IF NOT EXISTS test_user (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "age INT, " +
                    "email VARCHAR(100)" +
                    ")";
            statement.executeUpdate(createTableSql);
            System.out.println("表创建成功");

            // 插入数据
            String insertSql = "INSERT INTO test_user (name, age, email) VALUES ('张三', 25, 'zhangsan@example.com')";
            int insertResult = statement.executeUpdate(insertSql);
            System.out.println("插入数据成功，影响行数: " + insertResult);

            // 查询数据
            String selectSql = "SELECT * FROM test_user";
            resultSet = statement.executeQuery(selectSql);
            System.out.println("查询结果:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String email = resultSet.getString("email");
                System.out.println("ID: " + id + ", 姓名: " + name + ", 年龄: " + age + ", 邮箱: " + email);
            }

            // 更新数据
            String updateSql = "UPDATE test_user SET age = 26 WHERE name = '张三'";
            int updateResult = statement.executeUpdate(updateSql);
            System.out.println("更新数据成功，影响行数: " + updateResult);

            // 再次查询数据
            resultSet = statement.executeQuery(selectSql);
            System.out.println("更新后的查询结果:");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String email = resultSet.getString("email");
                System.out.println("ID: " + id + ", 姓名: " + name + ", 年龄: " + age + ", 邮箱: " + email);
            }

            // 删除数据
            String deleteSql = "DELETE FROM test_user WHERE name = '张三'";
            int deleteResult = statement.executeUpdate(deleteSql);
            System.out.println("删除数据成功，影响行数: " + deleteResult);

        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭连接
            closeConnection(connection, statement, resultSet);
        }
    }

    /**
     * 主方法，用于测试
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        testConnection();
    }
}
