package com.linsir.controller;

import com.linsir.kafka.service.AdminClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@RestController
@RequestMapping("/topic/")
public class TopicController {

    @Autowired
    private AdminClientService  adminClientService;


    @GetMapping("describe/{tName}")
    public List<String> describe(@PathVariable("tName") String tName)
    {
        List<String> list = new ArrayList<>();
        adminClientService.describe(tName).forEach((k,v)->{
            System.out.println(k+":"+v);
            list.add(k+":"+v);
        }

        );
        return list;
    }
}
