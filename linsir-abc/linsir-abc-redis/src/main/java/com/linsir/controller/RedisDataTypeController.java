package com.linsir.controller;

import com.linsir.service.RedisDataTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/redis/data-type")
public class RedisDataTypeController {

    @Autowired
    private RedisDataTypeService redisDataTypeService;

    // String 类型操作
    @PostMapping("/string/set")
    public String setString(@RequestParam String key, @RequestParam String value) {
        redisDataTypeService.setString(key, value);
        return "设置成功: " + key + " = " + value;
    }

    @GetMapping("/string/get")
    public String getString(@RequestParam String key) {
        String value = redisDataTypeService.getString(key);
        return "获取结果: " + key + " = " + value;
    }

    @PostMapping("/string/increment")
    public String incrementString(@RequestParam String key, @RequestParam long delta) {
        redisDataTypeService.incrementString(key, delta);
        return "自增成功: " + key + " 增加 " + delta;
    }

    @PostMapping("/string/decrement")
    public String decrementString(@RequestParam String key, @RequestParam long delta) {
        redisDataTypeService.decrementString(key, delta);
        return "自减成功: " + key + " 减少 " + delta;
    }

    // List 类型操作
    @PostMapping("/list/add")
    public String addToList(@RequestParam String key, @RequestParam String... values) {
        redisDataTypeService.addToList(key, values);
        return "添加到列表成功: " + key + " = " + String.join(", ", values);
    }

    @GetMapping("/list/get")
    public List<String> getList(@RequestParam String key, @RequestParam long start, @RequestParam long end) {
        return redisDataTypeService.getList(key, start, end);
    }

    @PostMapping("/list/remove")
    public String removeFromList(@RequestParam String key, @RequestParam long count, @RequestParam String value) {
        redisDataTypeService.removeFromList(key, count, value);
        return "从列表移除成功: " + key + " 中的 " + value + " (" + count + "个)";
    }

    @GetMapping("/list/size")
    public long getListSize(@RequestParam String key) {
        return redisDataTypeService.getListSize(key);
    }

    // Set 类型操作
    @PostMapping("/set/add")
    public String addToSet(@RequestParam String key, @RequestParam String... values) {
        redisDataTypeService.addToSet(key, values);
        return "添加到集合成功: " + key + " = " + String.join(", ", values);
    }

    @GetMapping("/set/get")
    public Set<String> getSet(@RequestParam String key) {
        return redisDataTypeService.getSet(key);
    }

    @PostMapping("/set/remove")
    public String removeFromSet(@RequestParam String key, @RequestParam String... values) {
        redisDataTypeService.removeFromSet(key, values);
        return "从集合移除成功: " + key + " 中的 " + String.join(", ", values);
    }

    @GetMapping("/set/size")
    public long getSetSize(@RequestParam String key) {
        return redisDataTypeService.getSetSize(key);
    }

    @GetMapping("/set/intersection")
    public Set<String> getSetIntersection(@RequestParam String key1, @RequestParam String key2) {
        return redisDataTypeService.getSetIntersection(key1, key2);
    }

    @GetMapping("/set/union")
    public Set<String> getSetUnion(@RequestParam String key1, @RequestParam String key2) {
        return redisDataTypeService.getSetUnion(key1, key2);
    }

    // Hash 类型操作
    @PostMapping("/hash/put")
    public String putToHash(@RequestParam String key, @RequestParam String field, @RequestParam String value) {
        redisDataTypeService.putToHash(key, field, value);
        return "添加到哈希成功: " + key + "." + field + " = " + value;
    }

    @GetMapping("/hash/get")
    public String getFromHash(@RequestParam String key, @RequestParam String field) {
        String value = redisDataTypeService.getFromHash(key, field);
        return "获取哈希结果: " + key + "." + field + " = " + value;
    }

    @PostMapping("/hash/putAll")
    public String putAllToHash(@RequestParam String key, @RequestParam Map<String, String> map) {
        redisDataTypeService.putAllToHash(key, map);
        return "批量添加到哈希成功: " + key;
    }

    @GetMapping("/hash/getAll")
    public Map<String, String> getHash(@RequestParam String key) {
        return redisDataTypeService.getHash(key);
    }

    @PostMapping("/hash/remove")
    public String removeFromHash(@RequestParam String key, @RequestParam String... fields) {
        redisDataTypeService.removeFromHash(key, fields);
        return "从哈希移除成功: " + key + " 中的 " + String.join(", ", fields);
    }

    @GetMapping("/hash/size")
    public long getHashSize(@RequestParam String key) {
        return redisDataTypeService.getHashSize(key);
    }

    // ZSet 类型操作
    @PostMapping("/zset/add")
    public String addToZSet(@RequestParam String key, @RequestParam String value, @RequestParam double score) {
        redisDataTypeService.addToZSet(key, value, score);
        return "添加到有序集合成功: " + key + " = " + value + " (score: " + score + ")";
    }

    @GetMapping("/zset/get")
    public Set<String> getZSetRange(@RequestParam String key, @RequestParam long start, @RequestParam long end) {
        return redisDataTypeService.getZSetRange(key, start, end);
    }

    @GetMapping("/zset/getByScore")
    public Set<String> getZSetRangeByScore(@RequestParam String key, @RequestParam double min, @RequestParam double max) {
        return redisDataTypeService.getZSetRangeByScore(key, min, max);
    }

    @PostMapping("/zset/remove")
    public String removeFromZSet(@RequestParam String key, @RequestParam String... values) {
        redisDataTypeService.removeFromZSet(key, values);
        return "从有序集合移除成功: " + key + " 中的 " + String.join(", ", values);
    }

    @GetMapping("/zset/size")
    public long getZSetSize(@RequestParam String key) {
        return redisDataTypeService.getZSetSize(key);
    }

    @GetMapping("/zset/score")
    public Double getZSetScore(@RequestParam String key, @RequestParam String value) {
        return redisDataTypeService.getZSetScore(key, value);
    }

    // 通用操作
    @DeleteMapping("/key/delete")
    public String deleteKey(@RequestParam String key) {
        redisDataTypeService.deleteKey(key);
        return "删除键成功: " + key;
    }

    @GetMapping("/key/exists")
    public boolean existsKey(@RequestParam String key) {
        return redisDataTypeService.existsKey(key);
    }

    @PostMapping("/key/expire")
    public String expireKey(@RequestParam String key, @RequestParam long seconds) {
        redisDataTypeService.expireKey(key, seconds);
        return "设置过期时间成功: " + key + " (" + seconds + "秒)";
    }

    @GetMapping("/key/ttl")
    public long getKeyTtl(@RequestParam String key) {
        return redisDataTypeService.getKeyTtl(key);
    }
}
