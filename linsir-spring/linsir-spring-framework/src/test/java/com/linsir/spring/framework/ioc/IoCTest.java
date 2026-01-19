package com.linsir.spring.framework.ioc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Arrays;

/**
 * @ClassName : IoCTest
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-05 12:10
 */

@SpringJUnitConfig(locations = "/ioc-config.xml")
public class IoCTest {

    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    Person person;

    @Test
    void getBeansTest()
    {
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(
                System.out::println
        );
    }

    @Test
    void beanTest()
    {
        person.setName("linsir");
        System.out.println(person.getName());
    }
}
