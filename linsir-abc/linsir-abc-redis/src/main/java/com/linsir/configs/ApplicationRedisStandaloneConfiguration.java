package com.linsir.configs;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class ApplicationRedisStandaloneConfiguration {


    @Resource
    private GenericJackson2JsonRedisSerializer springSessionDefaultRedisSerializer;


    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration("redis-11744.c270.us-east-1-3.ec2.redns.redis-cloud.com",11744);
        redisStandaloneConfiguration.setPassword("AcxmzCMgAykYIzysZA7Y7d6b2Rjcekyp");
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }


    @Bean
    public <K,V> RedisTemplate<K, V> redisTemplate() {
        Jackson2JsonRedisSerializer<Object> objectJackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);
       RedisTemplate redisTemplate = new RedisTemplate();
       redisTemplate.setDefaultSerializer(springSessionDefaultRedisSerializer);
       redisTemplate.setHashValueSerializer(new StringRedisSerializer());
       redisTemplate.setHashValueSerializer(springSessionDefaultRedisSerializer);
       redisTemplate.setConnectionFactory(jedisConnectionFactory());
       return  redisTemplate;
    }
}
