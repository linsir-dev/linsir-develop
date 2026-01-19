package com.linsir.kafka.producer;


import org.apache.kafka.clients.producer.Producer;

public abstract class ProducerFactory {

    public abstract Producer createProducer();

}
