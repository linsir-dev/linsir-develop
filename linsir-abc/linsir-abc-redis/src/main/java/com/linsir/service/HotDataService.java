package com.linsir.service;

import java.util.Map;

public interface HotDataService {

    /**
     * 获取热点数据
     * @param key 数据键
     * @param <T> 数据类型
     * @return 热点数据
     */
    <T> T getHotData(String key);

    /**
     * 设置热点数据
     * @param key 数据键
     * @param value 数据值
     * @param expireSeconds 过期时间（秒）
     * @param <T> 数据类型
     */
    <T> void setHotData(String key, T value, long expireSeconds);

    /**
     * 删除热点数据
     * @param key 数据键
     */
    void deleteHotData(String key);

    /**
     * 批量获取热点数据
     * @param keys 数据键集合
     * @return 键值对映射
     */
    Map<String, Object> batchGetHotData(String... keys);

    /**
     * 批量设置热点数据
     * @param dataMap 键值对映射
     * @param expireSeconds 过期时间（秒）
     */
    void batchSetHotData(Map<String, Object> dataMap, long expireSeconds);

    /**
     * 刷新热点数据过期时间
     * @param key 数据键
     * @param expireSeconds 过期时间（秒）
     * @return 是否刷新成功
     */
    boolean refreshHotDataExpire(String key, long expireSeconds);

    /**
     * 检查热点数据是否存在
     * @param key 数据键
     * @return 是否存在
     */
    boolean existsHotData(String key);

    /**
     * 获取热点数据过期时间
     * @param key 数据键
     * @return 过期时间（秒），-1表示永不过期，-2表示不存在
     */
    long getHotDataTtl(String key);
}
