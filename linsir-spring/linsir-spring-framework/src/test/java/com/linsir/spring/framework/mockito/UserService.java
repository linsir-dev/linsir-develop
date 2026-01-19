package com.linsir.spring.framework.mockito;

import com.linsir.spring.framework.mock.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName : UserService
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-06 15:03
 */

@Component
public class UserService {

    @Autowired
    private UserDao userDao;

    public User getUserById(Integer id){

        return userDao.getUserById(id);

    }
}
