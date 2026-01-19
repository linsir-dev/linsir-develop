package com.linsir.subject.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName : LoginController
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-12 01:16
 */

@Controller
@RequestMapping("/login")
public class LoginController {

    @GetMapping("")
    public String login()
    {
        return "login";
    }
}
