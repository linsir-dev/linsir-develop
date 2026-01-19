package com.linsir.configs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

@Configuration
public class ApplicationConfiguration {


    @Bean
    public GenericJackson2JsonRedisSerializer springSessionDefaultRedisSerializer() {
        String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        ObjectMapper objectMapper = new ObjectMapper();
        // 处理Date类型
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        objectMapper.registerModule(new JavaTimeModule()
                // 处理LocalDateTime类型
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)))
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))));
        // 序列化java对象时，将类的信息写入redis
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_ARRAY);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
