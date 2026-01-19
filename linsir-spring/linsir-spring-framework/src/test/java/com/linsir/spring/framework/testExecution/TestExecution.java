package com.linsir.spring.framework.testExecution;

import com.linsir.spring.framework.config.DataSourceTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;

/**
 * @ClassName : TestExecution
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-08 00:23
 */

@ContextConfiguration
public class TestExecution {


    @Test
    public void beanTest()
    {

    }
}
