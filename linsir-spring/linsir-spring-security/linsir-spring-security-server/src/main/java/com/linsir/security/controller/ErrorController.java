package com.linsir.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 错误页面控制器
 * 
 * 处理错误页面相关的请求
 * 
 * @author linsir
 * @version 1.0.0
 */
@Controller
public class ErrorController {

    /**
     * 错误页面
     * 
     * @return 错误页面视图
     */
    @GetMapping("/error")
    public String error() {
        return "error";
    }
}
