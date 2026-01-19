package com.linsir.kafka.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Listeners {

    private final static Logger logger = LoggerFactory.getLogger(Listeners.class);


    @KafkaListener(topics = "topic1",groupId = "group-id1")
    public void listen(String data) {
        logger.info("Received message from topic1................: " + data);
    }
}
