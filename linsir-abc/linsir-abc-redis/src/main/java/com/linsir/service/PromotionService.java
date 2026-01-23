package com.linsir.service;

import java.util.Map;

/**
 * 优惠活动服务接口
 * 用于管理限时优惠活动，使用Redis存储活动信息并设置过期时间
 */
public interface PromotionService {

    /**
     * 创建限时优惠活动
     * @param promotionId 活动ID
     * @param name 活动名称
     * @param discount 折扣（如0.8表示8折）
     * @param startTime 开始时间（时间戳，毫秒）
     * @param endTime 结束时间（时间戳，毫秒）
     * @param expireSeconds 过期时间（秒），建议设置为活动结束时间后一段时间
     * @return 是否创建成功
     */
    boolean createPromotion(String promotionId, String name, double discount, long startTime, long endTime, long expireSeconds);

    /**
     * 获取活动信息
     * @param promotionId 活动ID
     * @return 活动信息Map，包含name、discount、startTime、endTime等字段
     */
    Map<String, Object> getPromotion(String promotionId);

    /**
     * 检查活动是否有效
     * @param promotionId 活动ID
     * @return 是否有效
     */
    boolean isPromotionValid(String promotionId);

    /**
     * 取消活动
     * @param promotionId 活动ID
     * @return 是否取消成功
     */
    boolean cancelPromotion(String promotionId);

    /**
     * 获取所有当前有效的活动
     * @return 有效活动Map，key为活动ID，value为活动信息
     */
    Map<String, Map<String, Object>> getAllValidPromotions();

    /**
     * 更新活动信息
     * @param promotionId 活动ID
     * @param promotionInfo 活动信息Map
     * @param expireSeconds 过期时间（秒）
     * @return 是否更新成功
     */
    boolean updatePromotion(String promotionId, Map<String, Object> promotionInfo, long expireSeconds);
}
