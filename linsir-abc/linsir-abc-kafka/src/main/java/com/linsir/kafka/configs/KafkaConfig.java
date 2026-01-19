package com.linsir.kafka.configs;


import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class KafkaConfig {


    private static final String BROKER_LIST = "192.168.1.99:9092";
    private static final String GROUP_ID = "group1";
    private static final String AUTO_OFFSET_RESET = "earliest";

    private static final String KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";
    private static final String KEY_DESERIALIZER = "key.deserializer";
    private static final String VALUE_DESERIALIZER = "value.deserializer";



    private final Properties properties = new Properties();

    public KafkaConfig() {
        properties.put("bootstrap.servers", BROKER_LIST);
        properties.put("group.id", GROUP_ID);
        properties.put(ProducerConfig.ACKS_CONFIG,"1");
        properties.put("key.serializer", KEY_SERIALIZER);
        properties.put("value.serializer", VALUE_SERIALIZER);

       properties.put("key.deserializer", KEY_DESERIALIZER);
       properties.put("value.deserializer", VALUE_DESERIALIZER);
       properties.put("auto.offset.reset", AUTO_OFFSET_RESET);

    }

    public Properties getProperties() {
        return properties;
    }

}
