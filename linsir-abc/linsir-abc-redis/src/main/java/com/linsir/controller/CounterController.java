package com.linsir.controller;

import com.linsir.service.CounterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 计数器控制器
 * 提供计数器相关的API接口
 */
@RestController
@RequestMapping("/redis/counter")
public class CounterController {

    @Autowired
    private CounterService counterService;

    /**
     * 增加计数
     * @param key 计数器键
     * @param delta 增加的值
     * @return 增加后的计数值
     */
    @PostMapping("/increment")
    public long increment(
            @RequestParam String key,
            @RequestParam(required = false, defaultValue = "1") long delta) {
        return counterService.increment(key, delta);
    }

    /**
     * 减少计数
     * @param key 计数器键
     * @param delta 减少的值
     * @return 减少后的计数值
     */
    @PostMapping("/decrement")
    public long decrement(
            @RequestParam String key,
            @RequestParam(required = false, defaultValue = "1") long delta) {
        return counterService.decrement(key, delta);
    }

    /**
     * 获取计数值
     * @param key 计数器键
     * @return 计数值
     */
    @GetMapping("/get")
    public long getCount(@RequestParam String key) {
        return counterService.getCount(key);
    }

    /**
     * 重置计数器
     * @param key 计数器键
     * @return 操作结果
     */
    @PostMapping("/reset")
    public String resetCounter(@RequestParam String key) {
        boolean result = counterService.resetCounter(key);
        return result ? "重置计数器成功" : "重置计数器失败";
    }

    /**
     * 设置计数值
     * @param key 计数器键
     * @param value 计数值
     * @return 操作结果
     */
    @PostMapping("/set")
    public String setCount(
            @RequestParam String key,
            @RequestParam long value) {
        boolean result = counterService.setCount(key, value);
        return result ? "设置计数值成功" : "设置计数值失败";
    }

    /**
     * 设置计数器过期时间
     * @param key 计数器键
     * @param expireSeconds 过期时间（秒）
     * @return 操作结果
     */
    @PostMapping("/set-expire")
    public String setCounterExpire(
            @RequestParam String key,
            @RequestParam long expireSeconds) {
        boolean result = counterService.setCounterExpire(key, expireSeconds);
        return result ? "设置过期时间成功" : "设置过期时间失败";
    }

    /**
     * 获取计数器过期时间
     * @param key 计数器键
     * @return 过期时间（秒）
     */
    @GetMapping("/get-expire")
    public long getCounterExpire(@RequestParam String key) {
        return counterService.getCounterExpire(key);
    }

    /**
     * 批量增加计数
     * @param keyDeltaMap 键值对映射
     * @return 增加后的计数值映射
     */
    @PostMapping("/batch-increment")
    public Map<String, Long> batchIncrement(@RequestParam Map<String, Long> keyDeltaMap) {
        return counterService.batchIncrement(keyDeltaMap);
    }

    /**
     * 批量获取计数值
     * @param keys 计数器键集合（逗号分隔）
     * @return 计数值映射
     */
    @GetMapping("/batch-get")
    public Map<String, Long> batchGetCount(@RequestParam String keys) {
        String[] keyArray = keys.split(",");
        return counterService.batchGetCount(java.util.Arrays.asList(keyArray));
    }

    /**
     * 检查计数器是否存在
     * @param key 计数器键
     * @return 是否存在
     */
    @GetMapping("/exists")
    public boolean exists(@RequestParam String key) {
        return counterService.exists(key);
    }

    /**
     * 删除计数器
     * @param key 计数器键
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public String delete(@RequestParam String key) {
        boolean result = counterService.delete(key);
        return result ? "删除计数器成功" : "删除计数器失败";
    }
}
