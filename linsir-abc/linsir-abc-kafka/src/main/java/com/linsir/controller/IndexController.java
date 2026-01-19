package com.linsir.controller;

import com.linsir.service.RealNameService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
@RequestMapping("/")
public class IndexController {

    @Value("${spring.kafka.bootstrap-servers}")
    private String servers;


    @Autowired
    private RealNameService realNameService;


    @Resource
    private DataSource dataSource;

    @GetMapping("")
    public String index()
    {
        System.out.println(dataSource);
        return servers;
    }

    /*@GetMapping("send/{mgs}")
    public String send(@PathVariable("mgs") String msg)
    {
        realNameService.sendRealName(msg);
        return "ok";
    }*/

}
