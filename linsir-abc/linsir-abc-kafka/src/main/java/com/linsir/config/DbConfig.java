package com.linsir.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConfig {

    private static final String url = "jdbc:mysql://106.55.181.141:3306/linsir-abc-kafka?characterEncoding=utf8";

    private static final String user = "root";

    private static final String password ="yu@2023";

    private static final String driver = "com.mysql.cj.jdbc.Driver";

    private static Connection connection;


    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null)
        {
            Class.forName(driver);
            connection = DriverManager.getConnection(url,user,password);
        }
        return connection;
    }
}
