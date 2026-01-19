package com.linsir.spring.framework.environment;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.AopTestUtils;

/**
 * @ClassName : EnvironmentTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-07 00:33
 */

public class EnvironmentTest {

    private MockEnvironment environment = new MockEnvironment();

    @BeforeEach
    void setUp()
    {
        environment.setProperty("key1","xxx");
    }

}
