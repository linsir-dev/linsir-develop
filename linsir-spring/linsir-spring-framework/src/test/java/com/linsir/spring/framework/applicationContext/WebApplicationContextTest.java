package com.linsir.spring.framework.applicationContext;

import com.linsir.spring.framework.config.AppTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

/**
 * @ClassName : WebApplicationContextTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-05 02:16
 */



@SpringJUnitWebConfig(classes = {AppTestConfig.class})
public class WebApplicationContextTest {

    @Autowired
    WebApplicationContext webApplicationContext;


    @Test
    void aboutWebApplicationContextTest()
    {
        Arrays.stream(webApplicationContext.getBeanDefinitionNames()).forEach(
                System.out::println
        );
    }
}
