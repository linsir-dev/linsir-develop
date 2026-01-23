package com.linsir.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisDataTypeService {

    // String 类型操作
    void setString(String key, String value);
    String getString(String key);
    void incrementString(String key, long delta);
    void decrementString(String key, long delta);

    // List 类型操作
    void addToList(String key, String... values);
    List<String> getList(String key, long start, long end);
    void removeFromList(String key, long count, String value);
    long getListSize(String key);

    // Set 类型操作
    void addToSet(String key, String... values);
    Set<String> getSet(String key);
    void removeFromSet(String key, String... values);
    long getSetSize(String key);
    Set<String> getSetIntersection(String key1, String key2);
    Set<String> getSetUnion(String key1, String key2);

    // Hash 类型操作
    void putToHash(String key, String field, String value);
    String getFromHash(String key, String field);
    void putAllToHash(String key, Map<String, String> map);
    Map<String, String> getHash(String key);
    void removeFromHash(String key, String... fields);
    long getHashSize(String key);

    // ZSet 类型操作
    void addToZSet(String key, String value, double score);
    Set<String> getZSetRange(String key, long start, long end);
    Set<String> getZSetRangeByScore(String key, double min, double max);
    void removeFromZSet(String key, String... values);
    long getZSetSize(String key);
    Double getZSetScore(String key, String value);

    // 通用操作
    void deleteKey(String key);
    boolean existsKey(String key);
    void expireKey(String key, long seconds);
    long getKeyTtl(String key);
}
