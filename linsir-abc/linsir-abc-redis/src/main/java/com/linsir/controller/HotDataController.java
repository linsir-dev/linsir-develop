package com.linsir.controller;

import com.linsir.service.HotDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/redis/hot-data")
public class HotDataController {

    @Autowired
    private HotDataService hotDataService;

    /**
     * 设置热点数据
     * @param key 数据键
     * @param value 数据值
     * @param expireSeconds 过期时间（秒）
     * @return 操作结果
     */
    @PostMapping("/set")
    public String setHotData(@RequestParam String key, @RequestParam String value, @RequestParam long expireSeconds) {
        hotDataService.setHotData(key, value, expireSeconds);
        return "设置热点数据成功: " + key + " = " + value + " (过期时间: " + expireSeconds + "秒)";
    }

    /**
     * 获取热点数据
     * @param key 数据键
     * @return 热点数据
     */
    @GetMapping("/get")
    public Object getHotData(@RequestParam String key) {
        Object value = hotDataService.getHotData(key);
        return value != null ? value : "数据不存在";
    }

    /**
     * 删除热点数据
     * @param key 数据键
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public String deleteHotData(@RequestParam String key) {
        hotDataService.deleteHotData(key);
        return "删除热点数据成功: " + key;
    }

    /**
     * 批量设置热点数据
     * @param dataMap 键值对映射
     * @param expireSeconds 过期时间（秒）
     * @return 操作结果
     */
    @PostMapping("/batch-set")
    public String batchSetHotData(@RequestParam Map<String, String> dataMap, @RequestParam long expireSeconds) {
        // 转换类型
        Map<String, Object> objectMap = new java.util.HashMap<>();
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            objectMap.put(entry.getKey(), entry.getValue());
        }
        hotDataService.batchSetHotData(objectMap, expireSeconds);
        return "批量设置热点数据成功，共 " + dataMap.size() + " 条数据";
    }

    /**
     * 批量获取热点数据
     * @param keys 数据键集合（逗号分隔）
     * @return 键值对映射
     */
    @GetMapping("/batch-get")
    public Map<String, Object> batchGetHotData(@RequestParam String keys) {
        String[] keyArray = keys.split(",");
        return hotDataService.batchGetHotData(keyArray);
    }

    /**
     * 刷新热点数据过期时间
     * @param key 数据键
     * @param expireSeconds 过期时间（秒）
     * @return 操作结果
     */
    @PostMapping("/refresh-expire")
    public String refreshHotDataExpire(@RequestParam String key, @RequestParam long expireSeconds) {
        boolean success = hotDataService.refreshHotDataExpire(key, expireSeconds);
        return success ? "刷新过期时间成功: " + key + " (新过期时间: " + expireSeconds + "秒)" : "刷新过期时间失败: " + key;
    }

    /**
     * 检查热点数据是否存在
     * @param key 数据键
     * @return 是否存在
     */
    @GetMapping("/exists")
    public boolean existsHotData(@RequestParam String key) {
        return hotDataService.existsHotData(key);
    }

    /**
     * 获取热点数据过期时间
     * @param key 数据键
     * @return 过期时间（秒）
     */
    @GetMapping("/ttl")
    public long getHotDataTtl(@RequestParam String key) {
        return hotDataService.getHotDataTtl(key);
    }

    /**
     * 热点商品示例
     * @param productId 商品ID
     * @return 商品信息
     */
    @GetMapping("/product/{productId}")
    public Object getHotProduct(@PathVariable String productId) {
        String key = "hot:product:" + productId;
        
        // 尝试从缓存获取
        Map<String, Object> productInfo = hotDataService.getHotData(key);
        
        if (productInfo == null) {
            // 缓存未命中，从数据库查询（这里模拟）
            productInfo = mockProductInfo(productId);
            
            // 存入缓存，设置过期时间为5分钟
            hotDataService.setHotData(key, productInfo, 300);
        }
        
        return productInfo;
    }

    /**
     * 模拟商品信息
     * @param productId 商品ID
     * @return 商品信息
     */
    private Map<String, Object> mockProductInfo(String productId) {
        Map<String, Object> productInfo = new java.util.HashMap<>();
        productInfo.put("id", productId);
        productInfo.put("name", "热门商品" + productId);
        productInfo.put("price", 99.99);
        productInfo.put("stock", 1000);
        productInfo.put("sales", 5000);
        productInfo.put("description", "这是一个热门商品");
        return productInfo;
    }
}
