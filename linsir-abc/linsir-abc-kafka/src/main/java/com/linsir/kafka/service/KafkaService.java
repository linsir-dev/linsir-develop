package com.linsir.kafka.service;

import java.sql.SQLException;

public interface KafkaService {


    public void consume(String topic);

    public void produce(String topic, String message);

    public void produce() throws SQLException;
}
