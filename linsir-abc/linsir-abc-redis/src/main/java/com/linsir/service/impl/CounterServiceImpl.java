package com.linsir.service.impl;

import com.linsir.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 计数器服务实现类
 * 使用Redis实现分布式计数功能
 */
@Service
public class CounterServiceImpl implements CounterService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 计数器前缀
    private static final String COUNTER_PREFIX = "counter:";

    @Override
    public long increment(String key, long delta) {
        String counterKey = COUNTER_PREFIX + key;
        return redisTemplate.opsForValue().increment(counterKey, delta);
    }

    @Override
    public long decrement(String key, long delta) {
        String counterKey = COUNTER_PREFIX + key;
        return redisTemplate.opsForValue().decrement(counterKey, delta);
    }

    @Override
    public long getCount(String key) {
        String counterKey = COUNTER_PREFIX + key;
        Object value = redisTemplate.opsForValue().get(counterKey);
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        } else if (value instanceof Long) {
            return (Long) value;
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public boolean resetCounter(String key) {
        String counterKey = COUNTER_PREFIX + key;
        redisTemplate.opsForValue().set(counterKey, 0);
        return true;
    }

    @Override
    public boolean setCount(String key, long value) {
        String counterKey = COUNTER_PREFIX + key;
        redisTemplate.opsForValue().set(counterKey, value);
        return true;
    }

    @Override
    public boolean setCounterExpire(String key, long expireSeconds) {
        String counterKey = COUNTER_PREFIX + key;
        if (!redisTemplate.hasKey(counterKey)) {
            // 如果计数器不存在，先初始化
            redisTemplate.opsForValue().set(counterKey, 0);
        }
        redisTemplate.expire(counterKey, Duration.ofSeconds(expireSeconds));
        return true;
    }

    @Override
    public long getCounterExpire(String key) {
        String counterKey = COUNTER_PREFIX + key;
        if (!redisTemplate.hasKey(counterKey)) {
            return -1;
        }
        Object result = redisTemplate.getExpire(counterKey);
        if (result instanceof java.time.Duration) {
            java.time.Duration duration = (java.time.Duration) result;
            return duration.getSeconds();
        } else if (result instanceof Long) {
            return (Long) result;
        }
        return -1;
    }

    @Override
    public Map<String, Long> batchIncrement(Map<String, Long> keyDeltaMap) {
        Map<String, Long> resultMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : keyDeltaMap.entrySet()) {
            long value = increment(entry.getKey(), entry.getValue());
            resultMap.put(entry.getKey(), value);
        }
        return resultMap;
    }

    @Override
    public Map<String, Long> batchGetCount(Iterable<String> keys) {
        Map<String, Long> resultMap = new HashMap<>();
        for (String key : keys) {
            long value = getCount(key);
            resultMap.put(key, value);
        }
        return resultMap;
    }

    @Override
    public boolean exists(String key) {
        String counterKey = COUNTER_PREFIX + key;
        return redisTemplate.hasKey(counterKey);
    }

    @Override
    public boolean delete(String key) {
        String counterKey = COUNTER_PREFIX + key;
        return Boolean.TRUE.equals(redisTemplate.delete(counterKey));
    }
}
