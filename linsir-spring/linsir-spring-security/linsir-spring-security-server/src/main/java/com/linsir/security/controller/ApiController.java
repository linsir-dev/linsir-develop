package com.linsir.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * API 页面控制器
 * 
 * 处理 API 相关的页面请求
 * 
 * @author linsir
 * @version 1.0.0
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    /**
     * API 接口列表页面
     * 
     * @return API 接口列表视图
     */
    @GetMapping("/index")
    public String index() {
        return "api/index";
    }

    /**
     * Hello 测试页面
     * 
     * @return Hello 测试视图
     */
    @GetMapping("/hello-page")
    public String hello() {
        return "api/hello";
    }

    /**
     * SecurityContext 测试页面
     * 
     * @return SecurityContext 测试视图
     */
    @GetMapping("/security-context-page")
    public String securityContext() {
        return "api/security-context";
    }
}
