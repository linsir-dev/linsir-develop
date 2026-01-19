package com.linsir.kafka.components;

import org.apache.kafka.clients.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Component;

@Component
public class PListener implements ProducerFactory.Listener<String, String> {

    private final static Logger logger = LoggerFactory.getLogger(PListener.class);


    @Override
    public void producerAdded(String id, Producer<String, String> producer) {

        logger.info("Producer added to topic: " + id);
        logger.info(producer.toString());
    }

    @Override
    public void producerRemoved(String id, Producer<String, String> producer) {
        logger.info("Producer removed from topic: " + id);
        logger.info(producer.toString());
    }
}
