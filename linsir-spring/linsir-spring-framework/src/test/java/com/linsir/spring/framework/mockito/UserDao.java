package com.linsir.spring.framework.mockito;

import com.linsir.spring.framework.mock.User;

/**
 * @ClassName : UserDao
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-06 15:03
 */


public interface UserDao {
    User getUserById(int id);
}
