package com.linsir.kafka.listeners;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;

public class KMessageListener implements MessageListener<String, String> {
    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        System.out.println(data);
    }
}
