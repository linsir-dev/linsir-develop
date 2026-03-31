package com.linsir.abc.mysql.chapter01.concurrency.mapper;

import com.linsir.abc.mysql.chapter01.concurrency.entity.UserCoupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 用户优惠券Mapper接口
 * 记录用户领取的优惠券信息
 *
 * <p>唯一约束：</p>
 * <ul>
 *   <li>uk_user_coupon - (user_id, coupon_id) 一个用户只能领取一张同类型优惠券</li>
 * </ul>
 *
 * <p>状态流转：</p>
 * <ul>
 *   <li>0-未使用 -> 1-已使用（使用优惠券）</li>
 *   <li>0-未使用 -> 2-已过期（过期处理）</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface UserCouponMapper {

    /**
     * 根据ID查询用户优惠券
     *
     * @param id ID
     * @return 用户优惠券信息
     */
    @Select("SELECT * FROM user_coupons WHERE id = #{id}")
    UserCoupon selectById(Long id);

    /**
     * 根据用户ID和优惠券ID查询
     * 用于检查用户是否已领取该优惠券
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 用户优惠券信息
     */
    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    UserCoupon selectByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);

    /**
     * 查询用户的所有优惠券
     *
     * @param userId 用户ID
     * @return 用户优惠券列表
     */
    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserCoupon> selectByUserId(Long userId);

    /**
     * 查询用户特定状态的优惠券
     *
     * @param userId 用户ID
     * @param status 状态
     * @return 用户优惠券列表
     */
    @Select("SELECT * FROM user_coupons WHERE user_id = #{userId} AND status = #{status} ORDER BY created_at DESC")
    List<UserCoupon> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 查询优惠券的所有领取记录
     *
     * @param couponId 优惠券ID
     * @return 用户优惠券列表
     */
    @Select("SELECT * FROM user_coupons WHERE coupon_id = #{couponId} ORDER BY created_at DESC")
    List<UserCoupon> selectByCouponId(Long couponId);

    /**
     * 插入用户优惠券记录
     *
     * @param userCoupon 用户优惠券信息
     * @return 影响行数
     */
    @Insert("INSERT INTO user_coupons (user_id, coupon_id, status, grab_time) " +
            "VALUES (#{userId}, #{couponId}, #{status}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserCoupon userCoupon);

    /**
     * 更新用户优惠券状态为已使用
     *
     * @param id      用户优惠券ID
     * @param orderId 使用订单ID
     * @return 影响行数
     */
    @Update("UPDATE user_coupons SET status = 1, use_time = NOW(), order_id = #{orderId} WHERE id = #{id} AND status = 0")
    int updateStatusToUsed(@Param("id") Long id, @Param("orderId") Long orderId);

    /**
     * 更新用户优惠券状态
     *
     * @param id     用户优惠券ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE user_coupons SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 批量更新过期优惠券状态
     * 将过期的未使用优惠券标记为已过期
     *
     * @param couponId 优惠券ID
     * @return 影响行数
     */
    @Update("UPDATE user_coupons SET status = 2 WHERE coupon_id = #{couponId} AND status = 0")
    int markExpiredByCouponId(Long couponId);

    /**
     * 删除用户优惠券记录
     *
     * @param id 用户优惠券ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_coupons WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计优惠券领取数量
     *
     * @param couponId 优惠券ID
     * @return 领取数量
     */
    @Select("SELECT COUNT(*) FROM user_coupons WHERE coupon_id = #{couponId}")
    Long countByCouponId(Long couponId);
}
