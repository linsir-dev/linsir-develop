package com.linsir.abc.pdai.database.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MySQL分表示例
 * 演示水平分表的实现
 */
public class ShardingTableExample {

    // 数据库连接参数
    private static final String URL = "jdbc:mysql://localhost:3306/linsir-abc-pdai?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // 分表数量
    private static final int TABLE_COUNT = 4;

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
            connection = java.sql.DriverManager.getConnection(URL, USER, PASSWORD);
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
     * 根据用户ID计算分表索引
     * @param userId 用户ID
     * @return 分表索引
     */
    public static int getTableIndex(long userId) {
        return (int) (userId % TABLE_COUNT);
    }

    /**
     * 获取分表名称
     * @param userId 用户ID
     * @return 分表名称
     */
    public static String getTableName(long userId) {
        int index = getTableIndex(userId);
        return "user_info_" + index;
    }

    /**
     * 创建分表
     */
    public static void createShardingTables() {
        Connection connection = null;
        Statement statement = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();

            // 创建多个分表
            for (int i = 0; i < TABLE_COUNT; i++) {
                String tableName = "user_info_" + i;
                String createTableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                        "id BIGINT PRIMARY KEY, " +
                        "username VARCHAR(50) NOT NULL, " +
                        "email VARCHAR(100), " +
                        "phone VARCHAR(20), " +
                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
                statement.executeUpdate(createTableSql);
                System.out.println("创建分表: " + tableName + " 成功");
            }

        } catch (SQLException e) {
            System.err.println("创建分表失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection, statement, null);
        }
    }

    /**
     * 插入用户数据
     * @param userId 用户ID
     * @param username 用户名
     * @param email 邮箱
     * @param phone 电话
     */
    public static void insertUser(long userId, String username, String email, String phone) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String tableName = getTableName(userId);
            String insertSql = "INSERT INTO " + tableName + " (id, username, email, phone) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setLong(1, userId);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, phone);

            int result = preparedStatement.executeUpdate();
            System.out.println("插入用户数据到表 " + tableName + " 成功，影响行数: " + result);

        } catch (SQLException e) {
            System.err.println("插入用户数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection, preparedStatement, null);
        }
    }

    /**
     * 查询用户数据
     * @param userId 用户ID
     */
    public static void queryUser(long userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String tableName = getTableName(userId);
            String querySql = "SELECT * FROM " + tableName + " WHERE id = ?";
            preparedStatement = connection.prepareStatement(querySql);
            preparedStatement.setLong(1, userId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println("查询用户数据成功:");
                System.out.println("用户ID: " + resultSet.getLong("id"));
                System.out.println("用户名: " + resultSet.getString("username"));
                System.out.println("邮箱: " + resultSet.getString("email"));
                System.out.println("电话: " + resultSet.getString("phone"));
                System.out.println("创建时间: " + resultSet.getTimestamp("created_at"));
                System.out.println("更新时间: " + resultSet.getTimestamp("updated_at"));
            } else {
                System.out.println("用户ID " + userId + " 不存在");
            }

        } catch (SQLException e) {
            System.err.println("查询用户数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection, preparedStatement, resultSet);
        }
    }

    /**
     * 更新用户数据
     * @param userId 用户ID
     * @param email 邮箱
     * @param phone 电话
     */
    public static void updateUser(long userId, String email, String phone) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String tableName = getTableName(userId);
            String updateSql = "UPDATE " + tableName + " SET email = ?, phone = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(updateSql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, phone);
            preparedStatement.setLong(3, userId);

            int result = preparedStatement.executeUpdate();
            System.out.println("更新用户数据到表 " + tableName + " 成功，影响行数: " + result);

        } catch (SQLException e) {
            System.err.println("更新用户数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection, preparedStatement, null);
        }
    }

    /**
     * 删除用户数据
     * @param userId 用户ID
     */
    public static void deleteUser(long userId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            String tableName = getTableName(userId);
            String deleteSql = "DELETE FROM " + tableName + " WHERE id = ?";
            preparedStatement = connection.prepareStatement(deleteSql);
            preparedStatement.setLong(1, userId);

            int result = preparedStatement.executeUpdate();
            System.out.println("删除用户数据从表 " + tableName + " 成功，影响行数: " + result);

        } catch (SQLException e) {
            System.err.println("删除用户数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection, preparedStatement, null);
        }
    }

    /**
     * 查询所有分表中的数据
     */
    public static void queryAllUsers() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.createStatement();

            System.out.println("查询所有分表中的用户数据:");
            for (int i = 0; i < TABLE_COUNT; i++) {
                String tableName = "user_info_" + i;
                String querySql = "SELECT * FROM " + tableName;
                resultSet = statement.executeQuery(querySql);

                System.out.println("\n表 " + tableName + " 中的数据:");
                boolean hasData = false;
                while (resultSet.next()) {
                    hasData = true;
                    System.out.println("用户ID: " + resultSet.getLong("id") + ", 用户名: " + resultSet.getString("username") + ", 邮箱: " + resultSet.getString("email"));
                }
                if (!hasData) {
                    System.out.println("该表无数据");
                }
                resultSet.close();
            }

        } catch (SQLException e) {
            System.err.println("查询所有用户数据失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnection(connection, statement, resultSet);
        }
    }

    /**
     * 测试分表功能
     */
    public static void testShardingTable() {
        // 创建分表
        createShardingTables();

        // 插入测试数据
        insertUser(1, "张三", "zhangsan@example.com", "13800138001");
        insertUser(2, "李四", "lisi@example.com", "13800138002");
        insertUser(3, "王五", "wangwu@example.com", "13800138003");
        insertUser(4, "赵六", "zhaoliu@example.com", "13800138004");
        insertUser(5, "钱七", "qianqi@example.com", "13800138005");
        insertUser(6, "孙八", "sunba@example.com", "13800138006");
        insertUser(7, "周九", "zhoujiu@example.com", "13800138007");
        insertUser(8, "吴十", "wushi@example.com", "13800138008");

        // 查询测试数据
        queryUser(1);
        queryUser(5);

        // 更新测试数据
        updateUser(1, "zhangsan_new@example.com", "13900139001");
        queryUser(1);

        // 删除测试数据
        deleteUser(2);
        queryUser(2);

        // 查询所有数据
        queryAllUsers();
    }

    /**
     * 主方法，用于测试
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        testShardingTable();
    }
}
