package com.linsir.kafka.service;


import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;


@Service
public class KafkaServiceImpl implements KafkaService{

    private final static Logger logger = LoggerFactory.getLogger(KafkaServiceImpl.class);


    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;


    @Override
    public void consume(String topic) {

    }

    @Override
    public void produce(String topic, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,
                0,
                System.currentTimeMillis(),
                "msgid",
                message
        );
        CompletableFuture<SendResult<String, String>> future= kafkaTemplate.send(topic, message);
        future.whenComplete((result, ex)->{
           if (ex!=null)
           {
               logger.error(ex.getMessage());
           }
           logger.info(result.toString());
        });
    }

    @Override
    public void produce() throws SQLException {

    }


    /*private RealNameNoteDetailService realNameNoteDetailService;

    private KafkaProducerFactory producerFactory;

    public KafkaServiceImpl() throws SQLException, ClassNotFoundException {
         producerFactory = new KafkaProducerFactory();
         this.realNameNoteDetailService = new RealNameNoteDetailImpl();
    }
    private KafkaConsumer<String,String> consumer ;

    @Override
    public void consume(String topic) {
        //consumer.p
    }

    @Override
    public void produce(String topic, final String message) {
        KafkaProducer<String,String> producer = (KafkaProducer<String, String>) producerFactory.createProducer();
        producer.send(new ProducerRecord<String,String>(topic,message), new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                System.out.println("topic:"+metadata.topic()+"\noffset:"+metadata.offset()+"\npartition:"+metadata.partition()+"\ntimestamp:"+metadata.timestamp());
                System.out.println(message);
            }
        });
        producer.close();
    }

    public void produce() throws SQLException {
        realNameNoteDetailService.getRealNameNoteDetails().forEach(realNameNoteDetail -> {
            produce("topic1", "messagesss");
        });
    }*/
}
