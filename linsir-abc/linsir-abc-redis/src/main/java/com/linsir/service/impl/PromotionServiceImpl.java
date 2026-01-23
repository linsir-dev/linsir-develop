package com.linsir.service.impl;

import com.linsir.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 优惠活动服务实现类
 * 使用Redis存储活动信息，设置过期时间，实现限时活动功能
 */
@Service
public class PromotionServiceImpl implements PromotionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 活动前缀
    private static final String PROMOTION_PREFIX = "promotion:";

    @Override
    public boolean createPromotion(String promotionId, String name, double discount, long startTime, long endTime, long expireSeconds) {
        String key = PROMOTION_PREFIX + promotionId;
        
        Map<String, Object> promotionInfo = new HashMap<>();
        promotionInfo.put("name", name);
        promotionInfo.put("discount", discount);
        promotionInfo.put("startTime", startTime);
        promotionInfo.put("endTime", endTime);
        promotionInfo.put("status", "ACTIVE");
        
        // 存储活动信息到Redis
        redisTemplate.opsForHash().putAll(key, promotionInfo);
        // 设置过期时间
        redisTemplate.expire(key, Duration.ofSeconds(expireSeconds));
        
        return true;
    }

    @Override
    public Map<String, Object> getPromotion(String promotionId) {
        String key = PROMOTION_PREFIX + promotionId;
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            resultMap.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        return resultMap;
    }

    @Override
    public boolean isPromotionValid(String promotionId) {
        String key = PROMOTION_PREFIX + promotionId;
        
        // 检查活动是否存在
        if (!redisTemplate.hasKey(key)) {
            return false;
        }
        
        // 获取活动信息
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        Map<String, Object> promotionInfo = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            promotionInfo.put(String.valueOf(entry.getKey()), entry.getValue());
        }
        
        // 检查活动状态
        if (!"ACTIVE".equals(promotionInfo.get("status"))) {
            return false;
        }
        
        // 检查时间范围
        long currentTime = System.currentTimeMillis();
        long startTime = (long) promotionInfo.get("startTime");
        long endTime = (long) promotionInfo.get("endTime");
        
        return currentTime >= startTime && currentTime <= endTime;
    }

    @Override
    public boolean cancelPromotion(String promotionId) {
        String key = PROMOTION_PREFIX + promotionId;
        
        if (!redisTemplate.hasKey(key)) {
            return false;
        }
        
        // 更新活动状态为取消
        redisTemplate.opsForHash().put(key, "status", "CANCELLED");
        return true;
    }

    @Override
    public Map<String, Map<String, Object>> getAllValidPromotions() {
        Map<String, Map<String, Object>> validPromotions = new HashMap<>();
        
        // 获取所有活动键
        Set<String> promotionKeys = redisTemplate.keys(PROMOTION_PREFIX + "*");
        
        if (promotionKeys != null) {
            for (String key : promotionKeys) {
                // 提取活动ID
                String promotionId = key.substring(PROMOTION_PREFIX.length());
                
                // 检查活动是否有效
                if (isPromotionValid(promotionId)) {
                    Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
                    Map<String, Object> promotionInfo = new HashMap<>();
                    for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
                        promotionInfo.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                    validPromotions.put(promotionId, promotionInfo);
                }
            }
        }
        
        return validPromotions;
    }

    @Override
    public boolean updatePromotion(String promotionId, Map<String, Object> promotionInfo, long expireSeconds) {
        String key = PROMOTION_PREFIX + promotionId;
        
        if (!redisTemplate.hasKey(key)) {
            return false;
        }
        
        // 更新活动信息
        for (Map.Entry<String, Object> entry : promotionInfo.entrySet()) {
            redisTemplate.opsForHash().put(key, entry.getKey(), entry.getValue());
        }
        
        // 重新设置过期时间
        redisTemplate.expire(key, Duration.ofSeconds(expireSeconds));
        
        return true;
    }
}
