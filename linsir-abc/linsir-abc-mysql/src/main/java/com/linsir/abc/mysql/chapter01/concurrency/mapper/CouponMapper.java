package com.linsir.abc.mysql.chapter01.concurrency.mapper;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 优惠券Mapper接口
 * 演示秒杀、并发领取场景
 *
 * <p>并发控制策略：</p>
 * <ul>
 *   <li>悲观锁：selectByIdForUpdate + deductQuantity，适用于高并发冲突场景</li>
 *   <li>乐观锁：deductQuantityWithVersion，适用于读多写少场景</li>
 * </ul>
 *
 * <p>防超卖机制：</p>
 * <ul>
 *   <li>数据库层面：remaining_quantity > 0 条件检查</li>
 *   <li>应用层面：版本号控制</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface CouponMapper {

    /**
     * 根据ID查询优惠券（无锁）
     *
     * @param id 优惠券ID
     * @return 优惠券信息
     */
    @Select("SELECT * FROM coupons WHERE id = #{id}")
    Coupon selectById(Long id);

    /**
     * 根据ID查询优惠券（悲观锁）
     * 使用FOR UPDATE锁定优惠券记录
     *
     * @param id 优惠券ID
     * @return 优惠券信息
     */
    @Select("SELECT * FROM coupons WHERE id = #{id} FOR UPDATE")
    Coupon selectByIdForUpdate(Long id);

    /**
     * 根据优惠券编码查询
     *
     * @param couponCode 优惠券编码
     * @return 优惠券信息
     */
    @Select("SELECT * FROM coupons WHERE coupon_code = #{couponCode}")
    Coupon selectByCouponCode(String couponCode);

    /**
     * 查询进行中的优惠券
     * 状态为进行中且在当前有效期内
     *
     * @return 优惠券列表
     */
    @Select("SELECT * FROM coupons WHERE status = 1 AND valid_start_time <= NOW() AND valid_end_time >= NOW()")
    List<Coupon> selectActiveCoupons();

    /**
     * 查询所有优惠券
     *
     * @return 优惠券列表
     */
    @Select("SELECT * FROM coupons ORDER BY id")
    List<Coupon> selectAll();

    /**
     * 插入优惠券
     *
     * @param coupon 优惠券信息
     * @return 影响行数
     */
    @Insert("INSERT INTO coupons (coupon_code, coupon_name, total_quantity, remaining_quantity, " +
            "discount_amount, discount_percent, min_order_amount, valid_start_time, valid_end_time, status, version) " +
            "VALUES (#{couponCode}, #{couponName}, #{totalQuantity}, #{remainingQuantity}, " +
            "#{discountAmount}, #{discountPercent}, #{minOrderAmount}, #{validStartTime}, #{validEndTime}, #{status}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Coupon coupon);

    /**
     * 扣减优惠券数量（悲观锁方式）
     * 需要配合selectByIdForUpdate使用
     *
     * @param id 优惠券ID
     * @return 影响行数
     */
    @Update("UPDATE coupons SET remaining_quantity = remaining_quantity - 1, updated_at = NOW() " +
            "WHERE id = #{id} AND remaining_quantity > 0")
    int deductQuantity(Long id);

    /**
     * 扣减优惠券数量（乐观锁方式）
     * 通过版本号控制并发，防止超卖
     *
     * @param id      优惠券ID
     * @param version 当前版本号
     * @return 影响行数，0表示版本冲突或已领完
     */
    @Update("UPDATE coupons SET remaining_quantity = remaining_quantity - 1, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND remaining_quantity > 0 AND version = #{version}")
    int deductQuantityWithVersion(@Param("id") Long id, @Param("version") Integer version);

    /**
     * 更新优惠券状态
     *
     * @param id     优惠券ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE coupons SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新剩余数量（用于测试重置）
     * 直接设置剩余数量
     *
     * @param id               优惠券ID
     * @param remainingQuantity 剩余数量
     * @return 影响行数
     */
    @Update("UPDATE coupons SET remaining_quantity = #{remainingQuantity}, updated_at = NOW() WHERE id = #{id}")
    int updateRemainingQuantity(@Param("id") Long id, @Param("remainingQuantity") Integer remainingQuantity);

    /**
     * 删除优惠券
     *
     * @param id 优惠券ID
     * @return 影响行数
     */
    @Delete("DELETE FROM coupons WHERE id = #{id}")
    int deleteById(Long id);
}
