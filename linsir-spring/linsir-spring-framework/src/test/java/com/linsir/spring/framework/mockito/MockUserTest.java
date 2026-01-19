package com.linsir.spring.framework.mockito;

import com.linsir.spring.framework.config.AppConfig;
import com.linsir.spring.framework.mock.User;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.util.Assert;

/**
 * @ClassName : MockUserTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-06 14:26
 */

@SpringJUnitConfig(classes = AppConfig.class)
public class MockUserTest {


    private UserDao userDao;

    @Autowired
    private UserService userService;


    @Test
    public void getUserByIdTest()
    {
        Mockito.when(userDao.getUserById( 3)).thenReturn( new User( 200, "I'm mock 3",30));

        User user = userService.getUserById( 1);

        Assert.notNull(user,"cc");

    }


}
