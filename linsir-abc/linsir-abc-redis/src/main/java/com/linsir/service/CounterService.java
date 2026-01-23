package com.linsir.service;

import java.util.Map;

/**
 * 计数器服务接口
 * 用于管理各种场景下的计数器，使用Redis实现分布式计数功能
 */
public interface CounterService {

    /**
     * 增加计数
     * @param key 计数器键
     * @param delta 增加的值
     * @return 增加后的计数值
     */
    long increment(String key, long delta);

    /**
     * 减少计数
     * @param key 计数器键
     * @param delta 减少的值
     * @return 减少后的计数值
     */
    long decrement(String key, long delta);

    /**
     * 获取计数值
     * @param key 计数器键
     * @return 计数值
     */
    long getCount(String key);

    /**
     * 重置计数器
     * @param key 计数器键
     * @return 是否重置成功
     */
    boolean resetCounter(String key);

    /**
     * 设置计数值
     * @param key 计数器键
     * @param value 计数值
     * @return 是否设置成功
     */
    boolean setCount(String key, long value);

    /**
     * 设置计数器过期时间
     * @param key 计数器键
     * @param expireSeconds 过期时间（秒）
     * @return 是否设置成功
     */
    boolean setCounterExpire(String key, long expireSeconds);

    /**
     * 获取计数器过期时间
     * @param key 计数器键
     * @return 过期时间（秒），-1表示永不过期
     */
    long getCounterExpire(String key);

    /**
     * 批量增加计数
     * @param keyDeltaMap 键值对映射，key为计数器键，value为增加的值
     * @return 增加后的计数值映射
     */
    Map<String, Long> batchIncrement(Map<String, Long> keyDeltaMap);

    /**
     * 批量获取计数值
     * @param keys 计数器键集合
     * @return 计数值映射
     */
    Map<String, Long> batchGetCount(Iterable<String> keys);

    /**
     * 检查计数器是否存在
     * @param key 计数器键
     * @return 是否存在
     */
    boolean exists(String key);

    /**
     * 删除计数器
     * @param key 计数器键
     * @return 是否删除成功
     */
    boolean delete(String key);
}
