package com.linsir.service.impl;

import com.linsir.service.OperationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class OperationServiceImpl implements OperationService {



    @Autowired
    public RedisOperations<String,String> redisOperations;


    @Override
    public void set(String key, String value, long timeOut, TimeUnit timeUnit) {
        redisOperations.opsForValue().set(key, value, timeOut, timeUnit);
    }

    @Override
    public void setString(String key, String value) {
    }

    @Override
    public void setString(String key, String value, long offset) {
    }


    @Override
    public String getString(String key) {
        return null;
    }

    @Override
    public String getAndSet(String key, String value) {
        return "";
    }

    @Override
    public void appendString(String key, String value) {

    }

    @Override
    public Long getRedisStrSize(String key) {
        return 0L;
    }

    @Override
    public long leftPush(String key, String value) {
        return 0L;
    }

    @Override
    public Long pushAll(String key, List<String> values) {
        return 0L;
    }


    @Override
    public long Listsize(String key) {
        return 0;
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
