package com.linsir.spring.framework.init;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @ClassName : EntireAppInitializer
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-05 02:40
 */

public class EntireAppInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        System.out.println(configurableApplicationContext.getBeanFactory());
        System.out.println("applicationContext,初始化 使用初始化器（initializer）来指定配置");
    }
}
