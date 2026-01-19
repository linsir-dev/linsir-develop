package com.linsir.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;

@Service
public class RedisClient {

    private static final Charset CODE = Charset.forName("UTF-8");


    @Autowired
    private  RedisTemplate<String, String> redisTemplate;

    public static void nullCheck(Object... args) {
        for (Object obj : args) {
            if (obj == null) {
                throw new IllegalArgumentException("redis argument can not be null!");
            }
        }
    }

    public static byte[] keyBytes(String key) {
        nullCheck(key);
        return key.getBytes(CODE);
    }

    private static byte[] valBytes(String field) {
        return field.getBytes(CODE);
    }



    public  long hIncr(String key, String field, Integer cnt) {
      return redisTemplate.execute((RedisCallback<Long>)con -> con.hIncrBy(keyBytes(key), valBytes(field), cnt));
    }



}
