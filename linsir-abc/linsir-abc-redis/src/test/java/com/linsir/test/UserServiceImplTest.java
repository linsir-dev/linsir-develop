package com.linsir.test;

import com.linsir.entity.User;
import com.linsir.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    public void findByUsernameTest() {
        User user = userService.getUserByUsername("xxx");
        System.out.println(user);
    }

    @Test
    public void saveUserTest()
    {
        User user = new User();
        user.setUsername("yuxl");
        user.setPassword("123456");
        userService.save(user);
    }
}
