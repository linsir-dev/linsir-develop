package com.linsir.spring.springmvc.dao.impl;

import com.linsir.spring.springmvc.dao.UerDao;
import com.linsir.spring.springmvc.entiy.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * @ClassName : UerDaoImpl
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-09 13:19
 */

@Repository
public class UerDaoImpl implements UerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public User getUserById(Integer id) {
       User user = jdbcTemplate.queryForObject(
                "select * from user where id = ?",
          new BeanPropertyRowMapper<>(User.class),id);
        return user;
    }
}
