package com.linsir.spring.springboot.base.config;

import com.linsir.spring.springboot.base.startup.ApplicationStartupRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @ClassName : ApplicationConfig
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-10 20:09
 */

@Configuration
@EnableTransactionManagement
public class ApplicationConfig {

    @Bean
    public ApplicationStartupRunner schedulerRunner()
    {
        return new ApplicationStartupRunner();
    }

    @Bean
    public ExitCodeGenerator exitCodeGenerator() {
        return () -> 42;
    }
}
