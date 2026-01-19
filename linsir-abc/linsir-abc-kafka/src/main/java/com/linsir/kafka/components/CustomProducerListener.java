package com.linsir.kafka.components;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Component
public class CustomProducerListener implements ProducerListener<String,String> {

    private final static Logger logger = LoggerFactory.getLogger(CustomProducerListener.class);



    @Override
    public void onSuccess(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata) {
        logger.info("推送成功，推送数据：{},{}", producerRecord.value(),recordMetadata.timestamp());
    }

    @Override
    public void onError(ProducerRecord<String, String> producerRecord, RecordMetadata recordMetadata, Exception exception) {
        logger.error("推送失败，推送数据：{}，失败原因：{}", producerRecord.value(), exception.getMessage());
    }
}
