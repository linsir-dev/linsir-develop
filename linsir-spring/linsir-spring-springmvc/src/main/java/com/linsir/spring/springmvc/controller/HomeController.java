package com.linsir.spring.springmvc.controller;

import com.linsir.spring.springmvc.entiy.User;
import com.linsir.spring.springmvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ClassName : HomeController
 * @Description : 首页
 * @Author : Linsir
 * @Date: 2023-12-09 02:06
 */

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private UserService userService;


    @RequestMapping("")
    public ModelAndView home()
    {
        User user = userService.getById(1);

        return  new ModelAndView("home","user",user);
    }

    @RequestMapping("index")
    public String index()
    {
        return "index";
    }
}
