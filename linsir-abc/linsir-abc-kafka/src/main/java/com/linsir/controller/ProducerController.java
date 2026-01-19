package com.linsir.controller;

import com.linsir.kafka.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send/")
public class ProducerController {

    @Autowired
    private KafkaService kafkaService;

    @RequestMapping(value = "topic1", method = {RequestMethod.GET,RequestMethod.POST})
    public String topic1Send(){
        kafkaService.produce("topic1","msg");
        return "success";
    }
}
