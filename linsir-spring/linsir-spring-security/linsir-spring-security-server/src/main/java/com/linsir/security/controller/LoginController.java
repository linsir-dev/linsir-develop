package com.linsir.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 登录控制器
 * 
 * 处理登录页面相关的请求
 * 
 * @author linsir
 * @version 1.0.0
 */
@Controller
public class LoginController {

    /**
     * 登录页面
     * 
     * @return 登录页面视图
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
