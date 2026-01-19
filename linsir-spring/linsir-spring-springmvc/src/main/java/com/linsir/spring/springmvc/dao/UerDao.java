package com.linsir.spring.springmvc.dao;

import com.linsir.spring.springmvc.entiy.User;

/**
 * @ClassName : UerDao
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-09 13:17
 */

public interface UerDao {
    User getUserById(Integer id);
}
