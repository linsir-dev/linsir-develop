package com.linsir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.sql.SQLException;

@SpringBootApplication(scanBasePackages = "com.linsir")
public class Application {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        /*


        KafkaService kafkaService = new KafkaServiceImpl();
        RealNameNoteDetailService realNameNoteDetailService = new RealNameNoteDetailImpl();
        List<RealNameNoteDetail> realNameNoteDetailList = realNameNoteDetailService.getRealNameNoteDetails();
        realNameNoteDetailList.forEach( realNameNoteDetail -> {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String realNameNoteDetailJson = objectMapper.writeValueAsString(realNameNoteDetail);
                kafkaService.produce("topic1",realNameNoteDetailJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });*/

        /*
        * 基于 spring boot  进行 kafka 相关研究
        *
        * */
        SpringApplication.run(Application.class);

    }


}
