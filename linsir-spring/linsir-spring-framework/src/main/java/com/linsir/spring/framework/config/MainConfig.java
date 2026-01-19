package com.linsir.spring.framework.config;

import com.linsir.spring.framework.service.MyService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @ClassName : MainConfig
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-04 14:28
 */

public class MainConfig {
    public static void main(String[] args) {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService myService = ctx.getBean(MyService.class);
        myService.doService();

    }
}
