package com.linsir.kafka.configs;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConsumerConfiguration {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS_CONFIG;

    public Map<String, Object> consumerConfigs()
    {
        Map<String, Object> props=new HashMap<String, Object>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,BOOTSTRAP_SERVERS_CONFIG);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return props;
    }



    @Bean
    public KafkaMessageListenerContainer  kafkaMessageListenerContainer()
    {
        ContainerProperties containerProperties= new ContainerProperties("topic1");
        containerProperties.setMessageListener(new MessageListener<String,String>() {
            @Override
            public void onMessage(ConsumerRecord<String, String> data) {
                System.out.println(data);
            }
        });
        ConsumerFactory<String,String> consumerFactory= new DefaultKafkaConsumerFactory<>(consumerConfigs());

        return new KafkaMessageListenerContainer<String,String>(consumerFactory,containerProperties);
    }

}
