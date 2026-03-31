package com.linsir.abc.mysql;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MySQL架构学习模块启动类
 *
 * 功能说明：
 * 1. 模拟MySQL三层架构（客户端层、服务层、存储引擎层）
 * 2. 演示连接管理、SQL解析、查询优化、存储引擎等核心概念
 * 3. 提供完整的CRUD操作示例
 *
 * 启动方式：
 * 1. 直接运行main方法
 * 2. 命令行: mvn spring-boot:run
 * 3. 打包后: java -jar linsir-abc-mysql-1.0.0-SNAPSHOT.jar
 *
 * @author linsir
 * @since 1.0.0
 */
@SpringBootApplication
@EnableScheduling
@MapperScan({"com.linsir.abc.mysql.chapter01.architecture.mapper", "com.linsir.abc.mysql.chapter01.concurrency.mapper"})
public class MySQLApplication {

    /**
     * 应用入口
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MySQLApplication.class, args);
        System.out.println("========================================");
        System.out.println("  MySQL Architecture Learning Module    ");
        System.out.println("  应用启动成功！                         ");
        System.out.println("  端口: 8081                            ");
        System.out.println("  数据库: linsir-abc-mysql              ");
        System.out.println("========================================");
    }
}
