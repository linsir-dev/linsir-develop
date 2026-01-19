package com.linsir.kafka.configs;

import com.linsir.kafka.components.PListener;
import jakarta.annotation.Resource;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ProducerConfiguration {


    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS_CONFIG;

    @Value("${spring.kafka.producer.key-serializer}")
    private String KEY_SERIALIZER_CONFIG;

    @Value("${spring.kafka.producer.value-serializer}")
    private String VALUE_SERIALIZER_CONFIG;


    @Resource
    private ProducerListener producerListener;

    @Resource
    private PListener pListener;


    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS_CONFIG);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER_CONFIG);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_CONFIG);
        return props;
    }

    @Bean
    public ProducerFactory<String,String> producerFactory() {
        Map<String, Object> configs =  producerConfigs();

        ProducerFactory<String,String> producerFactory = new DefaultKafkaProducerFactory<>(configs);
        producerFactory.addListener(pListener);
        return producerFactory;
    }

    @Bean
    public KafkaTemplate<String,String> kafkaTemplate() {
        KafkaTemplate<String,String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        kafkaTemplate.setProducerListener(producerListener);
        return kafkaTemplate;
    }
    
}
