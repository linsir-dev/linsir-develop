package com.linsir.kafka.service;

import org.apache.kafka.clients.admin.TopicDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AdminClientService {

    @Autowired
    private KafkaAdmin kafkaAdmin;



    public Map<String,TopicDescription> describe( String topicName )
    {
      return   kafkaAdmin.describeTopics(topicName);
    }

}
