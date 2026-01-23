package com.linsir.controller;

import com.linsir.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 优惠活动控制器
 * 提供优惠活动相关的API接口
 */
@RestController
@RequestMapping("/redis/promotion")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    /**
     * 创建优惠活动
     * @param promotionId 活动ID
     * @param name 活动名称
     * @param discount 折扣
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param expireSeconds 过期时间
     * @return 操作结果
     */
    @PostMapping("/create")
    public String createPromotion(
            @RequestParam String promotionId,
            @RequestParam String name,
            @RequestParam double discount,
            @RequestParam long startTime,
            @RequestParam long endTime,
            @RequestParam long expireSeconds) {
        boolean result = promotionService.createPromotion(promotionId, name, discount, startTime, endTime, expireSeconds);
        return result ? "创建优惠活动成功" : "创建优惠活动失败";
    }

    /**
     * 获取优惠活动信息
     * @param promotionId 活动ID
     * @return 活动信息
     */
    @GetMapping("/get")
    public Map<String, Object> getPromotion(@RequestParam String promotionId) {
        return promotionService.getPromotion(promotionId);
    }

    /**
     * 检查活动是否有效
     * @param promotionId 活动ID
     * @return 是否有效
     */
    @GetMapping("/valid")
    public boolean isPromotionValid(@RequestParam String promotionId) {
        return promotionService.isPromotionValid(promotionId);
    }

    /**
     * 取消优惠活动
     * @param promotionId 活动ID
     * @return 操作结果
     */
    @PostMapping("/cancel")
    public String cancelPromotion(@RequestParam String promotionId) {
        boolean result = promotionService.cancelPromotion(promotionId);
        return result ? "取消优惠活动成功" : "取消优惠活动失败";
    }

    /**
     * 获取所有有效活动
     * @return 有效活动列表
     */
    @GetMapping("/valid-all")
    public Map<String, Map<String, Object>> getAllValidPromotions() {
        return promotionService.getAllValidPromotions();
    }

    /**
     * 更新优惠活动
     * @param promotionId 活动ID
     * @param promotionInfo 活动信息
     * @param expireSeconds 过期时间
     * @return 操作结果
     */
    @PostMapping("/update")
    public String updatePromotion(
            @RequestParam String promotionId,
            @RequestParam Map<String, Object> promotionInfo,
            @RequestParam long expireSeconds) {
        boolean result = promotionService.updatePromotion(promotionId, promotionInfo, expireSeconds);
        return result ? "更新优惠活动成功" : "更新优惠活动失败";
    }
}
