package com.linsir.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器
 * 
 * 处理首页相关的请求
 * 
 * @author linsir
 * @version 1.0.0
 */
@Controller
public class IndexController {

    /**
     * 首页
     * 
     * @return 首页视图
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 首页（显式路径）
     * 
     * @return 首页视图
     */
    @GetMapping("/index")
    public String indexPage() {
        return "index";
    }

    /**
     * EasyUI 示例页面
     * 
     * @return EasyUI 示例视图
     */
    @GetMapping("/easyui-demo")
    public String easyuiDemo() {
        return "easyui-demo";
    }
}
