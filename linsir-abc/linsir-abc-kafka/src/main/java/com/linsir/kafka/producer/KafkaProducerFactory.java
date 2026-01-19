package com.linsir.kafka.producer;

import com.linsir.kafka.configs.KafkaConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public class KafkaProducerFactory extends ProducerFactory{

    private KafkaProducer<String, String> producer;


    @Override
    public Producer createProducer() {
        KafkaConfig kafkaConfig = new KafkaConfig();
        Properties props = kafkaConfig.getProperties();
        Producer<String, String> producer = new KafkaProducer<String,String>(props);
        return producer;
    }


}
