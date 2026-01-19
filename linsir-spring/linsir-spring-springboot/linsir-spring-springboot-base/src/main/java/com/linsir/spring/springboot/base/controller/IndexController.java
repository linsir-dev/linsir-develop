package com.linsir.spring.springboot.base.controller;

import com.linsir.spring.springboot.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName : IndexController
 * @Description : 首页相关
 * @Author : Linsir
 * @Date: 2023-11-30 16:29
 */

@RestController
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public String index()
    {
        userService.doSS();
        return "hello world";
    }
}
