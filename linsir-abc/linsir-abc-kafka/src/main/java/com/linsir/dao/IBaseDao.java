package com.linsir.dao;

import java.sql.SQLException;
import java.util.List;

public interface IBaseDao<T> {

    public boolean insert(String sql) throws SQLException, ClassNotFoundException;

    public boolean update(String sql);

    public boolean delete(String sql);

    public T get(String sql);

    public List<T> getAll(String sql) throws SQLException;
}
