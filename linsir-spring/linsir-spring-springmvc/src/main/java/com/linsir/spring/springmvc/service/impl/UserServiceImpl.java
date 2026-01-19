package com.linsir.spring.springmvc.service.impl;

import com.linsir.spring.springmvc.dao.UerDao;
import com.linsir.spring.springmvc.entiy.User;
import com.linsir.spring.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName : UserServiceImpl
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-09 13:29
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UerDao uerDao;

    @Override
    public User getById(Integer id) {
        return uerDao.getUserById(id);
    }
}
