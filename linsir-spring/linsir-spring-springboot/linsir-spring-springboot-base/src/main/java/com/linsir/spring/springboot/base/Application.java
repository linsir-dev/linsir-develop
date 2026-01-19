package com.linsir.spring.springboot.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @ClassName : Application
 * @Description : 启动
 * @Author : Linsir
 * @Date: 2023-11-30 13:05
 */

/*
        *   Springboot2版本开始增加了WebApplicationType的类型，其定义如下：
        *   1.不需要再web容器的环境下运行，也就是普通的工程  NONE
        *   2.基于servlet的Web项目 SERVLET
        *   3.应式web应用==reactive web Spring5版本的新特性 REACTIVE
        *
        *
        *
        *   1、deduceWebApplicationType() : 推断应用的类型 ，创建的是一个 SERVLET 应用还是 REACTIVE应用或者是 NONE
        *
        *   2、setInitializers((Collection) getSpringFactoriesInstances(ApplicationContextInitializer.class))：初始化 classpath 下的所有的可用的 ApplicationContextInitializer。
        *
        *
        *
        *
        * */

@SpringBootApplication  // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
