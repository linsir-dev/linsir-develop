package com.linsir.abc.mysql.chapter01.concurrency.service.impl;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import com.linsir.abc.mysql.chapter01.concurrency.entity.UserCoupon;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.CouponMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.UserCouponMapper;
import com.linsir.abc.mysql.chapter01.concurrency.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 优惠券服务实现类
 * 演示秒杀场景下的并发控制
 *
 * <p>实现要点：</p>
 * <ul>
 *   <li>悲观锁：SELECT FOR UPDATE，适合高并发冲突场景</li>
 *   <li>乐观锁：版本号控制，适合读多写少场景</li>
 *   <li>防重复领取：唯一约束 uk_user_coupon</li>
 *   <li>防超卖：remaining_quantity > 0 条件检查</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Coupon createCoupon(Coupon coupon) {
        coupon.setVersion(0);
        coupon.setRemainingQuantity(coupon.getTotalQuantity());
        couponMapper.insert(coupon);
        log.info("创建优惠券成功：couponCode={}, id={}", coupon.getCouponCode(), coupon.getId());
        return coupon;
    }

    @Override
    public Coupon getCouponById(Long id) {
        return couponMapper.selectById(id);
    }

    @Override
    public List<Coupon> getActiveCoupons() {
        return couponMapper.selectActiveCoupons();
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return couponMapper.selectAll();
    }

    /**
     * 领取优惠券（悲观锁）
     * 使用SELECT FOR UPDATE防止超卖
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grabCouponWithPessimisticLock(Long userId, Long couponId) {
        // 检查用户是否已领取
        UserCoupon existing = userCouponMapper.selectByUserAndCoupon(userId, couponId);
        if (existing != null) {
            throw new RuntimeException("您已经领取过该优惠券");
        }

        // 加锁读取优惠券
        Coupon coupon = couponMapper.selectByIdForUpdate(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        if (!coupon.canGrab()) {
            throw new RuntimeException("优惠券已领完或已过期");
        }

        // 扣减数量
        int affected = couponMapper.deductQuantity(couponId);
        if (affected == 0) {
            throw new RuntimeException("优惠券领取失败");
        }

        // 记录用户优惠券
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .status(0)
                .build();
        userCouponMapper.insert(userCoupon);

        log.info("领取优惠券成功（悲观锁）：userId={}, couponId={}", userId, couponId);
        return true;
    }

    /**
     * 领取优惠券（乐观锁）
     * 使用版本号控制并发，适合高并发场景
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean grabCouponWithOptimisticLock(Long userId, Long couponId) {
        // 检查用户是否已领取
        UserCoupon existing = userCouponMapper.selectByUserAndCoupon(userId, couponId);
        if (existing != null) {
            throw new RuntimeException("您已经领取过该优惠券");
        }

        // 读取优惠券（无锁）
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        if (!coupon.canGrab()) {
            throw new RuntimeException("优惠券已领完或已过期");
        }

        // 乐观锁扣减
        int affected = couponMapper.deductQuantityWithVersion(couponId, coupon.getVersion());
        if (affected == 0) {
            log.warn("优惠券领取版本冲突，couponId={}", couponId);
            throw new RuntimeException("优惠券领取失败，请重试");
        }

        // 记录用户优惠券
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .status(0)
                .build();
        userCouponMapper.insert(userCoupon);

        log.info("领取优惠券成功（乐观锁）：userId={}, couponId={}", userId, couponId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        if (!userCoupon.isUnused()) {
            throw new RuntimeException("优惠券已使用或已过期");
        }

        int affected = userCouponMapper.updateStatusToUsed(userCouponId, orderId);
        if (affected == 0) {
            throw new RuntimeException("使用优惠券失败");
        }

        log.info("使用优惠券成功：userCouponId={}, orderId={}", userCouponId, orderId);
        return true;
    }

    @Override
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponMapper.selectByUserId(userId);
    }

    @Override
    public List<UserCoupon> getUserCouponsByStatus(Long userId, Integer status) {
        return userCouponMapper.selectByUserIdAndStatus(userId, status);
    }
}
