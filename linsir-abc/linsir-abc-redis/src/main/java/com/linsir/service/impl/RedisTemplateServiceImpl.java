package com.linsir.service.impl;

import com.linsir.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisTemplateServiceImpl implements OperationService {



    @Autowired
    private RedisTemplate<String, String> redisTemplate;


    @Override
    public void set(String key, String value, long timeOut, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key,value,10,TimeUnit.SECONDS);
    }

    @Override
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setString(String key, String value, long offset) {
        redisTemplate.opsForValue().set(key, value, offset);
    }

    @Override
    public String getAndSet(String key, String value) {
        return redisTemplate.opsForValue().getAndSet(key, value);
    }

    @Override
    public void appendString(String key, String value) {
        redisTemplate.opsForValue().append(key, value);
    }

    @Override
    public Long getRedisStrSize(String key) {
        return redisTemplate.opsForValue().size(key);
    }

    @Override
    public long leftPush(String key, String value) {
        long result = 0;
        try {
            result = redisTemplate.opsForList().leftPush(key,value);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Long pushAll(String key, List<String> values) {
        return 0L;
    }


    @Override
    public long Listsize(String key) {
        return redisTemplate.opsForList().size(key);
    }


    @Override
    public String getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }



    @Override
    public void setSet(String key, Set<String> value) {

    }

    @Override
    public void getSet(String key, Set<String> value) {

    }

    @Override
    public void setHash(String key, String value) {

    }
}
