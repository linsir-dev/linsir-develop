package com.linsir.subject.sms.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author linsir
 * @since 2023-12-12
 */
@Controller
@RequestMapping("/user")
public class UserController {


    @RequestMapping("")
    public String list()
    {
        return "user/user_list";
    }
}
