package com.linsir.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {


    @RequestMapping(value = "index",method = {RequestMethod.GET, RequestMethod.POST})
    public String index()
    {
        return "Hello World!";
    }

}
