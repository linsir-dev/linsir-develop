package com.linsir.abc.mysql.chapter01.concurrency.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户优惠券实体类
 * 对应数据库表：user_coupons
 * 记录用户领取的优惠券信息
 *
 * <p>核心字段说明：</p>
 * <ul>
 *   <li>user_id - 领取用户ID</li>
 *   <li>coupon_id - 优惠券ID</li>
 *   <li>status - 使用状态：0-未使用，1-已使用，2-已过期</li>
 *   <li>use_time - 使用时间</li>
 *   <li>order_id - 使用订单ID</li>
 * </ul>
 *
 * <p>唯一约束：</p>
 * <ul>
 *   <li>uk_user_coupon - 一个用户只能领取一张同类型优惠券</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    /**
     * ID
     * 主键，自增
     */
    private Long id;

    /**
     * 用户ID
     * 关联users表
     */
    private Long userId;

    /**
     * 优惠券ID
     * 关联coupons表
     */
    private Long couponId;

    /**
     * 状态
     * 0-未使用，1-已使用，2-已过期
     */
    private Integer status;

    /**
     * 使用时间
     * 使用优惠券的时间
     */
    private LocalDateTime useTime;

    /**
     * 使用订单ID
     * 记录使用在哪个订单
     */
    private Long orderId;

    /**
     * 领取时间
     * 用户领取优惠券的时间
     */
    private LocalDateTime grabTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 检查优惠券是否未使用
     *
     * @return 是否未使用
     */
    public boolean isUnused() {
        return status != null && status == 0;
    }

    /**
     * 检查优惠券是否已使用
     *
     * @return 是否已使用
     */
    public boolean isUsed() {
        return status != null && status == 1;
    }

    /**
     * 检查优惠券是否已过期
     *
     * @return 是否已过期
     */
    public boolean isExpired() {
        return status != null && status == 2;
    }

    /**
     * 标记为已使用
     *
     * @param orderId 使用订单ID
     */
    public void markAsUsed(Long orderId) {
        this.status = 1;
        this.useTime = LocalDateTime.now();
        this.orderId = orderId;
    }
}
