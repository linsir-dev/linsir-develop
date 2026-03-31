package com.linsir.abc.mysql.chapter01.concurrency.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 优惠券实体类
 * 对应数据库表：coupons
 * 用于演示秒杀、并发领取场景
 *
 * <p>核心字段说明：</p>
 * <ul>
 *   <li>total_quantity - 优惠券总发放数量</li>
 *   <li>remaining_quantity - 剩余可领取数量</li>
 *   <li>version - 乐观锁版本号，控制并发领取</li>
 *   <li>discount_amount - 固定金额优惠（与discount_percent互斥）</li>
 *   <li>discount_percent - 折扣比例（与discount_amount互斥）</li>
 * </ul>
 *
 * <p>优惠券类型：</p>
 * <ul>
 *   <li>满减券：discount_amount有值，满足min_order_amount可用</li>
 *   <li>折扣券：discount_percent有值，如0.8表示8折</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    /**
     * 优惠券ID
     * 主键，自增
     */
    private Long id;

    /**
     * 优惠券编码
     * 唯一标识，业务使用
     */
    private String couponCode;

    /**
     * 优惠券名称
     * 显示名称
     */
    private String couponName;

    /**
     * 总数量
     * 优惠券总发放数量
     */
    private Integer totalQuantity;

    /**
     * 剩余数量
     * 还可领取的数量
     */
    private Integer remainingQuantity;

    /**
     * 优惠金额
     * 固定金额优惠，如10元
     * 与discount_percent互斥
     */
    private BigDecimal discountAmount;

    /**
     * 折扣百分比
     * 折扣比例，如0.8表示8折
     * 与discount_amount互斥
     */
    private BigDecimal discountPercent;

    /**
     * 最低使用金额
     * 订单满多少可用，0表示无限制
     */
    private BigDecimal minOrderAmount;

    /**
     * 有效期开始时间
     */
    private LocalDateTime validStartTime;

    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;

    /**
     * 状态
     * 0-未开始，1-进行中，2-已结束
     */
    private Integer status;

    /**
     * 乐观锁版本号
     * 用于控制并发领取
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 检查优惠券是否有效
     * 有效条件：状态进行中、在有效期内、还有剩余数量
     *
     * @return 是否有效
     */
    public boolean isValid() {
        if (status == null || status != 1) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (validStartTime == null || validEndTime == null) {
            return false;
        }
        if (now.isBefore(validStartTime) || now.isAfter(validEndTime)) {
            return false;
        }
        int remaining = remainingQuantity != null ? remainingQuantity : 0;
        return remaining > 0;
    }

    /**
     * 检查是否可以领取
     *
     * @return 是否可以领取
     */
    public boolean canGrab() {
        return isValid();
    }

    /**
     * 计算优惠金额
     *
     * @param orderAmount 订单金额
     * @return 优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        // 检查最低使用金额
        BigDecimal minAmount = minOrderAmount != null ? minOrderAmount : BigDecimal.ZERO;
        if (orderAmount.compareTo(minAmount) < 0) {
            return BigDecimal.ZERO;
        }

        // 固定金额优惠
        if (discountAmount != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            return discountAmount.min(orderAmount);
        }

        // 折扣优惠
        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discount = orderAmount.multiply(BigDecimal.ONE.subtract(discountPercent));
            return discount.compareTo(orderAmount) > 0 ? orderAmount : discount;
        }

        return BigDecimal.ZERO;
    }
}
