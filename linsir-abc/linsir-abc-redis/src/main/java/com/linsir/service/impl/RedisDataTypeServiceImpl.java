package com.linsir.service.impl;

import com.linsir.service.RedisDataTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RedisDataTypeServiceImpl implements RedisDataTypeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // String 类型操作
    @Override
    public void setString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public String getString(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void incrementString(String key, long delta) {
        redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public void decrementString(String key, long delta) {
        redisTemplate.opsForValue().decrement(key, delta);
    }

    // List 类型操作
    @Override
    public void addToList(String key, String... values) {
        for (String value : values) {
            redisTemplate.opsForList().rightPush(key, value);
        }
    }

    @Override
    public List<String> getList(String key, long start, long end) {
        List<Object> objects = redisTemplate.opsForList().range(key, start, end);
        return objects.stream().map(Object::toString).toList();
    }

    @Override
    public void removeFromList(String key, long count, String value) {
        redisTemplate.opsForList().remove(key, count, value);
    }

    @Override
    public long getListSize(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // Set 类型操作
    @Override
    public void addToSet(String key, String... values) {
        for (String value : values) {
            redisTemplate.opsForSet().add(key, value);
        }
    }

    @Override
    public Set<String> getSet(String key) {
        Set<Object> objects = redisTemplate.opsForSet().members(key);
        return objects.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public void removeFromSet(String key, String... values) {
        for (String value : values) {
            redisTemplate.opsForSet().remove(key, value);
        }
    }

    @Override
    public long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    @Override
    public Set<String> getSetIntersection(String key1, String key2) {
        Set<Object> objects = redisTemplate.opsForSet().intersect(key1, key2);
        return objects.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public Set<String> getSetUnion(String key1, String key2) {
        Set<Object> objects = redisTemplate.opsForSet().union(key1, key2);
        return objects.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());
    }

    // Hash 类型操作
    @Override
    public void putToHash(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    @Override
    public String getFromHash(String key, String field) {
        Object value = redisTemplate.opsForHash().get(key, field);
        return value != null ? value.toString() : null;
    }

    @Override
    public void putAllToHash(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public Map<String, String> getHash(String key) {
        Map<Object, Object> hashMap = redisTemplate.opsForHash().entries(key);
        Map<String, String> result = new java.util.HashMap<>();
        for (Map.Entry<Object, Object> entry : hashMap.entrySet()) {
            result.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return result;
    }

    @Override
    public void removeFromHash(String key, String... fields) {
        for (String field : fields) {
            redisTemplate.opsForHash().delete(key, field);
        }
    }

    @Override
    public long getHashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    // ZSet 类型操作
    @Override
    public void addToZSet(String key, String value, double score) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    @Override
    public Set<String> getZSetRange(String key, long start, long end) {
        Set<Object> objects = redisTemplate.opsForZSet().range(key, start, end);
        return objects.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public Set<String> getZSetRangeByScore(String key, double min, double max) {
        Set<Object> objects = redisTemplate.opsForZSet().rangeByScore(key, min, max);
        return objects.stream().map(Object::toString).collect(java.util.stream.Collectors.toSet());
    }

    @Override
    public void removeFromZSet(String key, String... values) {
        for (String value : values) {
            redisTemplate.opsForZSet().remove(key, value);
        }
    }

    @Override
    public long getZSetSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    @Override
    public Double getZSetScore(String key, String value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    // 通用操作
    @Override
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public boolean existsKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public void expireKey(String key, long seconds) {
        redisTemplate.expire(key, java.time.Duration.ofSeconds(seconds));
    }

    @Override
    public long getKeyTtl(String key) {
        Object result = redisTemplate.getExpire(key);
        if (result instanceof java.time.Duration) {
            java.time.Duration duration = (java.time.Duration) result;
            return duration.getSeconds();
        } else if (result instanceof Long) {
            return (Long) result;
        }
        return -1;
    }
}
