package com.linsir.spring.framework.applicationContext;

import com.linsir.spring.framework.config.AppTestConfig;
import com.linsir.spring.framework.init.EntireAppInitializer;
import com.linsir.spring.framework.init.InstantiationTracingBeanPostProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;

/**
 * @ClassName : ContextTest
 * @Description :
 * @Author : Linsir  import org.springframework.context.ApplicationContextInitializer;
 * @Date: 2023-12-04 19:02
 */

@SpringJUnitConfig(classes = {AppTestConfig.class},initializers = {EntireAppInitializer.class})
public class ApplicationContextTest {


    @Autowired
    ApplicationContext applicationContext;


    @Test
    void test() {
        String name = applicationContext.getApplicationName();
            System.out.println(name);
        }


    @Test
    void beanTest()
    {
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(System.out::println);
    }
}
