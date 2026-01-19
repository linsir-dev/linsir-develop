package com.linsir.spring.springboot.base.init;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;

/**
 * @ClassName : WebApplicationContextInitializer
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-10 20:58
 */

public class WebApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        System.out.println("系统名称："+applicationContext.getApplicationName());
        System.out.println("激活环境");
        Arrays.stream(applicationContext.getEnvironment().getActiveProfiles()).forEach(System.out::println);

        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(System.out::println);
        System.out.println("做");
    }
}
