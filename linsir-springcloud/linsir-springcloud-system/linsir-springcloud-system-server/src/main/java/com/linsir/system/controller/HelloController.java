package com.linsir.system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 *
 * @author linsir
 * @version 1.0.0
 */
@RestController
public class HelloController {

    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "linsir-springcloud-system-server");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }

    /**
     * Hello 接口
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello, Linsir Spring Cloud System Server!";
    }

    /**
     * 受保护接口 - 需要认证
     */
    @GetMapping("/api/protected")
    public Map<String, Object> protectedEndpoint() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "这是一个受保护的接口");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
