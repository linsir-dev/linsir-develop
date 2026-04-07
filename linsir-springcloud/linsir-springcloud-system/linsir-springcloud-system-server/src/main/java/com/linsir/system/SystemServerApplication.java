package com.linsir.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统服务启动类
 *
 * @author linsir
 * @version 1.0.0
 */
@SpringBootApplication
public class SystemServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemServerApplication.class, args);
        System.out.println("========================================");
        System.out.println("  Linsir Spring Cloud System Server");
        System.out.println("  启动成功！");
        System.out.println("========================================");
    }
}
