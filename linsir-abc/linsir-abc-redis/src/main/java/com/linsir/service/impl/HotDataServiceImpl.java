package com.linsir.service.impl;

import com.linsir.service.HotDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class HotDataServiceImpl implements HotDataService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public <T> T getHotData(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public <T> void setHotData(String key, T value, long expireSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(expireSeconds));
    }

    @Override
    public void deleteHotData(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Map<String, Object> batchGetHotData(String... keys) {
        Map<String, Object> result = new HashMap<>();
        for (String key : keys) {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    @Override
    public void batchSetHotData(Map<String, Object> dataMap, long expireSeconds) {
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            redisTemplate.opsForValue().set(entry.getKey(), entry.getValue(), Duration.ofSeconds(expireSeconds));
        }
    }

    @Override
    public boolean refreshHotDataExpire(String key, long expireSeconds) {
        return redisTemplate.expire(key, expireSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean existsHotData(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public long getHotDataTtl(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null ? ttl : -2;
    }
}
