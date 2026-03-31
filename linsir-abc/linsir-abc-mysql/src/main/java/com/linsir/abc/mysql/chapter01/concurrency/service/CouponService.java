package com.linsir.abc.mysql.chapter01.concurrency.service;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import com.linsir.abc.mysql.chapter01.concurrency.entity.UserCoupon;

import java.util.List;

/**
 * 优惠券服务接口
 * 演示秒杀、并发领取场景
 *
 * <p>防超卖机制：</p>
 * <ul>
 *   <li>数据库层面：remaining_quantity > 0 条件检查</li>
 *   <li>应用层面：版本号控制（乐观锁）或行锁（悲观锁）</li>
 *   <li>唯一约束：一个用户只能领取一张同类型优惠券</li>
 * </ul>
 *
 * <p>并发控制方案：</p>
 * <ul>
 *   <li>悲观锁：SELECT FOR UPDATE，适合高并发冲突场景</li>
 *   <li>乐观锁：版本号控制，适合读多写少场景</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
public interface CouponService {

    /**
     * 创建优惠券
     *
     * @param coupon 优惠券信息
     * @return 创建的优惠券
     */
    Coupon createCoupon(Coupon coupon);

    /**
     * 根据ID查询优惠券
     *
     * @param id 优惠券ID
     * @return 优惠券信息
     */
    Coupon getCouponById(Long id);

    /**
     * 查询进行中的优惠券
     *
     * @return 优惠券列表
     */
    List<Coupon> getActiveCoupons();

    /**
     * 查询所有优惠券
     *
     * @return 优惠券列表
     */
    List<Coupon> getAllCoupons();

    /**
     * 领取优惠券（悲观锁）
     * 使用SELECT FOR UPDATE防止超卖
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean grabCouponWithPessimisticLock(Long userId, Long couponId);

    /**
     * 领取优惠券（乐观锁）
     * 使用版本号控制并发
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    boolean grabCouponWithOptimisticLock(Long userId, Long couponId);

    /**
     * 使用优惠券
     *
     * @param userCouponId 用户优惠券ID
     * @param orderId      订单ID
     * @return 是否成功
     */
    boolean useCoupon(Long userCouponId, Long orderId);

    /**
     * 查询用户的优惠券
     *
     * @param userId 用户ID
     * @return 用户优惠券列表
     */
    List<UserCoupon> getUserCoupons(Long userId);

    /**
     * 查询用户特定状态的优惠券
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 用户优惠券列表
     */
    List<UserCoupon> getUserCouponsByStatus(Long userId, Integer status);
}
