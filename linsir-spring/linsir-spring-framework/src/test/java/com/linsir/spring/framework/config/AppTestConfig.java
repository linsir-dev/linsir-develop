package com.linsir.spring.framework.config;

import com.linsir.spring.framework.service.MyService;
import com.linsir.spring.framework.service.impl.MyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : AppConfig
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-04 19:52
 */

@Configuration
public class AppTestConfig {

    @Bean
    public MyService myService()
    {
        return  new MyServiceImpl();
    }
}
