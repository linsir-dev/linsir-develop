package com.linsir.abc.pdai.base.spidemo;

import java.util.ServiceLoader;

/**
 * SPI 机制示例
 * 展示如何使用 ServiceLoader 加载服务实现
 */
public class SPIDemo {

    public static void main(String[] args) {
        System.out.println("=== Java SPI 机制示例 ===\n");

        // 使用 ServiceLoader 加载服务实现
        ServiceLoader<HelloService> serviceLoader = ServiceLoader.load(HelloService.class);

        System.out.println("1. 加载所有服务实现:");
        int count = 1;
        for (HelloService service : serviceLoader) {
            System.out.println("   服务 " + count + ": " + service.getServiceName());
            service.sayHello();
            count++;
        }

        System.out.println("\n2. SPI 机制原理:");
        System.out.println("   - 服务接口: " + HelloService.class.getName());
        System.out.println("   - 实现类: ChineseHelloService 和 EnglishHelloService");
        System.out.println("   - 配置文件: META-INF/services/com.linsir.abc.pdai.Base.spidemo.HelloService");
        System.out.println("   - 加载器: ServiceLoader");

        System.out.println("\n3. 重新加载服务:");
        // 重新加载服务
        serviceLoader.reload();
        count = 1;
        for (HelloService service : serviceLoader) {
            System.out.println("   服务 " + count + ": " + service.getServiceName());
            count++;
        }

        System.out.println("\n=== SPI 机制优势 ===");
        System.out.println("1. 解耦: 服务接口与实现分离");
        System.out.println("2. 可插拔: 可在不修改代码的情况下替换实现");
        System.out.println("3. 自动发现: 运行时自动发现和加载服务实现");
        System.out.println("4. 多实现: 支持同一接口的多个实现");
    }
}
