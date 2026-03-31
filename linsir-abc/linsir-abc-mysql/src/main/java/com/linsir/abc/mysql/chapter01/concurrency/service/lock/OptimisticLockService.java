package com.linsir.abc.mysql.chapter01.concurrency.service.lock;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.AccountMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.CouponMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * 乐观锁演示服务
 * 专门用于演示乐观锁（Optimistic Locking）的各种使用场景
 *
 * <p>乐观锁核心思想：</p>
 * <ul>
 *   <li>假设并发冲突很少发生，不加锁直接操作</li>
 *   <li>提交时检查数据是否被其他事务修改</li>
 *   <li>适用于读多写少、冲突较少的场景</li>
 * </ul>
 *
 * <p>乐观锁实现方式：</p>
 * <ul>
 *   <li>版本号机制（Version）- 推荐</li>
 *   <li>时间戳机制（Timestamp）</li>
 *   <li>CAS（Compare And Swap）</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticLockService {

    private final AccountMapper accountMapper;
    private final InventoryMapper inventoryMapper;
    private final CouponMapper couponMapper;

    /**
     * 演示基本乐观锁更新
     * 场景：账户余额更新
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>读取记录，获取当前版本号</li>
     *   <li>执行业务逻辑</li>
     *   <li>更新时检查版本号是否变化</li>
     *   <li>版本号变化则更新失败</li>
     * </ol>
     *
     * @param accountId 账户ID
     * @param amount    变动金额
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean demonstrateBasicOptimisticLock(Long accountId, BigDecimal amount) {
        log.info("乐观锁更新: accountId={}, amount={}", accountId, amount);

        // 1. 读取记录（无锁）
        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }

        Integer currentVersion = account.getVersion();
        log.info("当前版本号: {}", currentVersion);

        // 2. 执行业务逻辑（这里简化处理）
        if (!account.hasEnoughBalance(amount)) {
            throw new RuntimeException("余额不足");
        }

        // 3. 乐观锁更新
        int affected = accountMapper.updateBalanceWithVersion(
                accountId, amount.negate(), currentVersion);

        if (affected == 0) {
            log.warn("乐观锁冲突: accountId={}, version={}", accountId, currentVersion);
            throw new RuntimeException("数据已被其他事务修改，请重试");
        }

        log.info("乐观锁更新成功: accountId={}, newVersion={}", accountId, currentVersion + 1);
        return true;
    }

    /**
     * 演示带重试机制的乐观锁
     * 场景：高并发下自动重试
     *
     * <p>重试策略：</p>
     * <ul>
     *   <li>设置最大重试次数</li>
     *   <li>每次重试前重新读取最新数据</li>
     *   <li>重试间隔可以递增（退避策略）</li>
     * </ul>
     *
     * @param accountId   账户ID
     * @param amount      变动金额
     * @param maxRetries  最大重试次数
     * @return 是否成功
     */
    public boolean demonstrateOptimisticLockWithRetry(Long accountId, BigDecimal amount, int maxRetries) {
        log.info("乐观锁带重试: accountId={}, maxRetries={}", accountId, maxRetries);

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return demonstrateBasicOptimisticLock(accountId, amount);
            } catch (RuntimeException e) {
                if (e.getMessage().contains("请重试") && attempt < maxRetries) {
                    log.info("第{}次尝试失败，准备重试...", attempt);
                    // 可以在这里添加退避延迟
                    try {
                        Thread.sleep(10 * attempt); // 递增延迟
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("重试被中断");
                    }
                } else {
                    throw e;
                }
            }
        }

        return false;
    }

    /**
     * 演示库存扣减乐观锁
     * 场景：高并发秒杀
     *
     * <p>关键点：</p>
     * <ul>
     *   <li>先检查库存是否充足</li>
     *   <li>使用版本号控制并发</li>
     *   <li>失败时返回特定错误码</li>
     * </ul>
     *
     * @param inventoryId 库存ID
     * @param quantity    扣减数量
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean demonstrateInventoryOptimisticLock(Long inventoryId, Integer quantity) {
        log.info("库存乐观锁扣减: inventoryId={}, quantity={}", inventoryId, quantity);

        // 1. 读取库存
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存不存在");
        }

        // 2. 检查库存
        if (!inventory.hasEnoughStock(quantity)) {
            throw new RuntimeException("库存不足");
        }

        Integer currentVersion = inventory.getVersion();

        // 3. 乐观锁扣减
        int affected = inventoryMapper.deductStockWithVersion(
                inventoryId, quantity, currentVersion);

        if (affected == 0) {
            log.warn("库存扣减冲突: inventoryId={}", inventoryId);
            throw new RuntimeException("库存已被其他用户抢购，请重试");
        }

        log.info("库存扣减成功: inventoryId={}, remaining={}",
                inventoryId, inventory.getAvailableStock() - quantity);
        return true;
    }

    /**
     * 演示优惠券领取乐观锁
     * 场景：高并发优惠券秒杀
     *
     * @param userId   用户ID
     * @param couponId 优惠券ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean demonstrateCouponOptimisticLock(Long userId, Long couponId) {
        log.info("优惠券乐观锁领取: userId={}, couponId={}", userId, couponId);

        // 1. 读取优惠券
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new RuntimeException("优惠券不存在");
        }

        // 2. 检查是否可领取
        if (!coupon.canGrab()) {
            throw new RuntimeException("优惠券已领完或已过期");
        }

        Integer currentVersion = coupon.getVersion();

        // 3. 乐观锁扣减
        int affected = couponMapper.deductQuantityWithVersion(couponId, currentVersion);

        if (affected == 0) {
            log.warn("优惠券领取冲突: couponId={}", couponId);
            throw new RuntimeException("优惠券已被抢完，请重试");
        }

        log.info("优惠券领取成功: userId={}, couponId={}", userId, couponId);
        return true;
    }

    /**
     * 演示批量乐观锁更新
     * 场景：批量操作时的冲突处理
     *
     * <p>注意事项：</p>
     * <ul>
     *   <li>批量更新时冲突概率更高</li>
     *   <li>建议分批处理，减小冲突范围</li>
     *   <li>记录失败项，单独重试</li>
     * </ul>
     *
     * @param accountIds 账户ID数组
     * @param amount     变动金额
     * @return 成功更新的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public int demonstrateBatchOptimisticLock(Long[] accountIds, BigDecimal amount) {
        log.info("批量乐观锁更新: count={}, amount={}", accountIds.length, amount);

        int successCount = 0;
        int failCount = 0;

        for (Long accountId : accountIds) {
            try {
                // 每个账户单独处理，避免一个失败影响其他
                Account account = accountMapper.selectById(accountId);
                if (account == null) {
                    log.warn("账户不存在: {}", accountId);
                    failCount++;
                    continue;
                }

                int affected = accountMapper.updateBalanceWithVersion(
                        accountId, amount, account.getVersion());

                if (affected > 0) {
                    successCount++;
                } else {
                    log.warn("更新失败（版本冲突）: {}", accountId);
                    failCount++;
                }
            } catch (Exception e) {
                log.error("更新异常: accountId={}, error={}", accountId, e.getMessage());
                failCount++;
            }
        }

        log.info("批量更新完成: 成功={}, 失败={}", successCount, failCount);
        return successCount;
    }

    /**
     * 演示乐观锁与悲观锁对比
     * 说明两种锁的适用场景
     *
     * @return 对比说明
     */
    public String demonstrateLockComparison() {
        String comparison = """
            乐观锁 vs 悲观锁对比:
            
            【乐观锁】
            - 实现方式: 版本号/时间戳
            - 加锁时机: 不加锁，提交时检查
            - 冲突处理: 失败重试
            - 适用场景: 读多写少，冲突少
            - 优点: 无锁等待，并发高
            - 缺点: 冲突时重试开销大
            
            【悲观锁】
            - 实现方式: SELECT FOR UPDATE
            - 加锁时机: 操作前加锁
            - 冲突处理: 阻塞等待
            - 适用场景: 写多读少，冲突多
            - 优点: 数据一致性高
            - 缺点: 有锁等待，并发低
            
            【选择建议】
            - 冲突概率 < 10%: 使用乐观锁
            - 冲突概率 > 20%: 使用悲观锁
            - 中间情况: 根据业务特点选择
            """;

        log.info("锁对比:\n{}", comparison);
        return comparison;
    }

    /**
     * 通用重试模板
     * 用于包装任何可能冲突的操作
     *
     * @param operation  操作
     * @param maxRetries 最大重试次数
     * @param <T>        返回类型
     * @return 操作结果
     */
    public <T> T executeWithRetry(Supplier<T> operation, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return operation.get();
            } catch (RuntimeException e) {
                if (e.getMessage().contains("请重试") && attempt < maxRetries) {
                    log.info("操作失败，第{}次重试...", attempt);
                    try {
                        Thread.sleep(10L * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("重试被中断");
                    }
                } else {
                    throw e;
                }
            }
        }
        throw new RuntimeException("超过最大重试次数");
    }
}
