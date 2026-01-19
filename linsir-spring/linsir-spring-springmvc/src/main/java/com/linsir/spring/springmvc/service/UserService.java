package com.linsir.spring.springmvc.service;

import com.linsir.spring.springmvc.entiy.User;

/**
 * @ClassName : UserService
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-09 13:28
 */

public interface UserService {
    User getById(Integer id);
}
