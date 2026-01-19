package com.linsir.spring.framework.config;

import com.linsir.spring.framework.service.impl.MyServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName : AppConfig
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-04 13:41
 */

@Configuration
public class AppConfig {
    @Bean
    public MyServiceImpl myService() {
        return new MyServiceImpl();
    }
}
