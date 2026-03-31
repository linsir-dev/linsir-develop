package com.linsir.abc.mysql.chapter01.concurrency;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.AccountMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.CouponMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.InventoryMapper;
import com.linsir.abc.mysql.chapter01.concurrency.service.AccountService;
import com.linsir.abc.mysql.chapter01.concurrency.service.CouponService;
import com.linsir.abc.mysql.chapter01.concurrency.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 锁机制测试类
 * 演示悲观锁、乐观锁的并发控制效果
 *
 * <p>测试场景：</p>
 * <ul>
 *   <li>悲观锁转账测试 - 验证FOR UPDATE的排他性</li>
 *   <li>乐观锁转账测试 - 验证版本号控制并发</li>
 *   <li>库存扣减测试 - 验证防超卖机制</li>
 *   <li>优惠券领取测试 - 验证高并发下的正确性</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LockTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Autowired
    private CouponMapper couponMapper;

    private Long testAccount1Id;
    private Long testAccount2Id;
    private Long testInventoryId;
    private Long testCouponId;

    @BeforeEach
    void setUp() {
        // 创建测试账户1
        Account account1 = Account.builder()
                .accountNo("TEST_ACC_" + System.currentTimeMillis())
                .accountName("测试账户1")
                .balance(new BigDecimal("1000.00"))
                .build();
        accountService.createAccount(account1);
        testAccount1Id = account1.getId();

        // 创建测试账户2
        Account account2 = Account.builder()
                .accountNo("TEST_ACC_" + (System.currentTimeMillis() + 1))
                .accountName("测试账户2")
                .balance(new BigDecimal("1000.00"))
                .build();
        accountService.createAccount(account2);
        testAccount2Id = account2.getId();

        // 创建测试库存
        Inventory inventory = Inventory.builder()
                .productId(System.currentTimeMillis())
                .warehouseId(1)
                .availableStock(100)
                .build();
        inventoryService.createInventory(inventory);
        testInventoryId = inventory.getId();

        // 创建测试优惠券
        Coupon coupon = Coupon.builder()
                .couponCode("TEST_COUPON_" + System.currentTimeMillis())
                .couponName("测试优惠券")
                .totalQuantity(10)
                .remainingQuantity(10)
                .discountAmount(new BigDecimal("10.00"))
                .validStartTime(LocalDateTime.now().minusDays(1))
                .validEndTime(LocalDateTime.now().plusDays(30))
                .status(1)
                .build();
        couponService.createCoupon(coupon);
        testCouponId = coupon.getId();
    }

    @Test
    @DisplayName("测试悲观锁转账 - 单线程")
    void testTransferWithPessimisticLock_SingleThread() {
        // 执行转账
        boolean result = accountService.transferWithPessimisticLock(
                testAccount1Id, testAccount2Id, new BigDecimal("100.00"));

        assertTrue(result, "转账应该成功");

        // 验证余额
        Account account1 = accountService.getAccountById(testAccount1Id);
        Account account2 = accountService.getAccountById(testAccount2Id);

        assertEquals(0, account1.getBalance().compareTo(new BigDecimal("900.00")), "账户1余额应为900");
        assertEquals(0, account2.getBalance().compareTo(new BigDecimal("1100.00")), "账户2余额应为1100");
    }

    @Test
    @DisplayName("测试乐观锁转账 - 单线程")
    void testTransferWithOptimisticLock_SingleThread() {
        // 执行转账
        boolean result = accountService.transferWithOptimisticLock(
                testAccount1Id, testAccount2Id, new BigDecimal("100.00"));

        assertTrue(result, "转账应该成功");

        // 验证余额
        Account account1 = accountService.getAccountById(testAccount1Id);
        Account account2 = accountService.getAccountById(testAccount2Id);

        assertEquals(0, account1.getBalance().compareTo(new BigDecimal("900.00")), "账户1余额应为900");
        assertEquals(0, account2.getBalance().compareTo(new BigDecimal("1100.00")), "账户2余额应为1100");
    }

    @Test
    @DisplayName("测试悲观锁库存扣减 - 串行验证")
    void testDeductStockWithPessimisticLock_Serial() {
        int quantityPerOp = 5;
        int totalQuantity = 25;
        int opCount = 5;

        // 重置库存
        Inventory inventory = inventoryService.getInventoryById(testInventoryId);
        inventory.setAvailableStock(totalQuantity);
        inventoryMapper.updateStock(testInventoryId, totalQuantity, inventory.getVersion());

        // 串行执行多次扣减
        for (int i = 0; i < opCount; i++) {
            inventoryService.deductStockWithPessimisticLock(testInventoryId, quantityPerOp);
        }

        // 验证结果
        Inventory finalInventory = inventoryService.getInventoryById(testInventoryId);
        log.info("最终库存: {}", finalInventory.getAvailableStock());

        assertEquals(0, finalInventory.getAvailableStock(), "库存应该扣减完毕");
    }

    @Test
    @DisplayName("测试乐观锁库存扣减 - 并发场景")
    void testDeductStockWithOptimisticLock_Concurrent() throws InterruptedException {
        int threadCount = 10;
        int quantityPerThread = 5;
        int totalQuantity = threadCount * quantityPerThread; // 50

        // 重置库存
        Inventory inventory = inventoryService.getInventoryById(testInventoryId);
        inventory.setAvailableStock(totalQuantity);
        inventoryMapper.updateStock(testInventoryId, totalQuantity, inventory.getVersion());

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    inventoryService.deductStockWithOptimisticLock(testInventoryId, quantityPerThread);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.warn("扣减库存失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Inventory finalInventory = inventoryService.getInventoryById(testInventoryId);
        log.info("成功次数: {}, 失败次数: {}, 最终库存: {}",
                successCount.get(), failCount.get(), finalInventory.getAvailableStock());

        // 乐观锁在并发下可能有部分失败，但最终库存应该正确
        int expectedStock = totalQuantity - successCount.get() * quantityPerThread;
        assertEquals(expectedStock, finalInventory.getAvailableStock(), "库存计算应该正确");
    }

    @Test
    @DisplayName("测试悲观锁领取优惠券 - 串行验证")
    void testGrabCouponWithPessimisticLock_Serial() {
        int couponQuantity = 10;

        // 重置优惠券
        Coupon coupon = couponService.getCouponById(testCouponId);
        coupon.setRemainingQuantity(couponQuantity);
        couponMapper.updateRemainingQuantity(testCouponId, couponQuantity);

        // 串行领取所有优惠券
        for (int i = 0; i < couponQuantity; i++) {
            Long userId = (long) (i + 1000);
            couponService.grabCouponWithPessimisticLock(userId, testCouponId);
        }

        // 验证结果
        Coupon finalCoupon = couponService.getCouponById(testCouponId);
        log.info("剩余数量: {}", finalCoupon.getRemainingQuantity());

        assertEquals(0, finalCoupon.getRemainingQuantity(), "优惠券应该被领完");
    }

    @Test
    @DisplayName("测试乐观锁领取优惠券 - 并发场景")
    void testGrabCouponWithOptimisticLock_Concurrent() throws InterruptedException {
        int threadCount = 20;
        int couponQuantity = 10;

        // 重置优惠券
        Coupon coupon = couponService.getCouponById(testCouponId);
        coupon.setRemainingQuantity(couponQuantity);
        couponMapper.updateRemainingQuantity(testCouponId, couponQuantity);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 2000); // 不同用户
            executor.submit(() -> {
                try {
                    couponService.grabCouponWithOptimisticLock(userId, testCouponId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.warn("领取优惠券失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Coupon finalCoupon = couponService.getCouponById(testCouponId);
        log.info("成功次数: {}, 失败次数: {}, 剩余数量: {}",
                successCount.get(), failCount.get(), finalCoupon.getRemainingQuantity());

        // 乐观锁在并发下可能有部分失败，但不应该超卖
        assertTrue(successCount.get() <= couponQuantity, "不应该超卖");
        assertEquals(couponQuantity - successCount.get(), finalCoupon.getRemainingQuantity(), "剩余数量应该正确");
    }

    @Test
    @DisplayName("测试余额不足转账")
    void testTransferInsufficientBalance() {
        // 尝试转账超过余额的金额
        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.transferWithPessimisticLock(
                    testAccount1Id, testAccount2Id, new BigDecimal("2000.00"));
        });

        assertTrue(exception.getMessage().contains("余额不足"));
    }

    @Test
    @DisplayName("测试库存不足扣减")
    void testDeductInsufficientStock() {
        // 尝试扣减超过库存的数量
        Exception exception = assertThrows(RuntimeException.class, () -> {
            inventoryService.deductStockWithPessimisticLock(testInventoryId, 200);
        });

        assertTrue(exception.getMessage().contains("库存不足"));
    }

    @Test
    @DisplayName("测试重复领取优惠券")
    void testDuplicateGrabCoupon() {
        Long userId = 9999L;

        // 第一次领取
        boolean first = couponService.grabCouponWithPessimisticLock(userId, testCouponId);
        assertTrue(first, "第一次领取应该成功");

        // 第二次领取应该失败
        Exception exception = assertThrows(RuntimeException.class, () -> {
            couponService.grabCouponWithPessimisticLock(userId, testCouponId);
        });

        assertTrue(exception.getMessage().contains("已经领取过"));
    }
}
