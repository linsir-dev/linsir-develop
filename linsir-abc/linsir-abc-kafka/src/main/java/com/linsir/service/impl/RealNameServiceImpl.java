package com.linsir.service.impl;

import com.linsir.service.RealNameService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;


@Service
public class RealNameServiceImpl implements RealNameService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendRealName(String msg) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>("topic1",msg);
        CompletableFuture<SendResult<String, String>> future =kafkaTemplate.send(record);
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                ex.printStackTrace();
            }
        });
    }
}
