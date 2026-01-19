package com.linsir.dao;

import com.linsir.config.DbConfig;
import org.apache.commons.dbutils.BasicRowProcessor;

import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDao<T> implements IBaseDao<T>{


    private Class<T> clazz;

    private final Statement statement;

    public BaseDao() {
        this.clazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            Connection connection = DbConfig.getConnection();
            this.statement = connection.createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean insert(String sql) throws SQLException {
        boolean result = false;
        try {
            if (statement.executeUpdate(sql) >0) {
                result = true;
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(String sql) {
        boolean result = false;
        try {
            if (statement.executeUpdate(sql)>0)
                result = true;statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public boolean delete(String sql) {
        return false;
    }

    @Override
    public T get(String sql) {
        return null;
    }

    @Override
    public List<T> getAll(String sql) throws SQLException {
        ResultSet resultSet = null;
        List<T> result = new ArrayList<>();
        try {
            resultSet = statement.executeQuery(sql);
        } catch (SQLException e) {
//            throw new RuntimeException(e);
        }
        BasicRowProcessor basicRowProcessor = new BasicRowProcessor();
        result = basicRowProcessor.toBeanList(resultSet,clazz);
        return result;
    }
}
