package com.linsir.abc.mysql.chapter01.concurrency;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Coupon;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 并发场景测试类
 * 模拟真实的高并发业务场景
 *
 * <p>测试场景：</p>
 * <ul>
 *   <li>秒杀场景 - 高并发下库存扣减</li>
 *   <li>红包雨 - 高并发下优惠券领取</li>
 *   <li>转账高峰 - 多账户并发转账</li>
 *   <li>混合场景 - 多种操作同时发生</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class ConcurrentScenarioTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private CouponService couponService;

    private Long accountAId;
    private Long accountBId;
    private Long accountCId;
    private Long inventoryId;
    private Long couponId;

    @BeforeEach
    void setUp() {
        // 创建测试账户A
        Account accountA = Account.builder()
                .accountNo("SCENE_A_" + System.currentTimeMillis())
                .accountName("场景测试账户A")
                .balance(new BigDecimal("100000.00"))
                .build();
        accountService.createAccount(accountA);
        accountAId = accountA.getId();

        // 创建测试账户B
        Account accountB = Account.builder()
                .accountNo("SCENE_B_" + (System.currentTimeMillis() + 1))
                .accountName("场景测试账户B")
                .balance(new BigDecimal("100000.00"))
                .build();
        accountService.createAccount(accountB);
        accountBId = accountB.getId();

        // 创建测试账户C
        Account accountC = Account.builder()
                .accountNo("SCENE_C_" + (System.currentTimeMillis() + 2))
                .accountName("场景测试账户C")
                .balance(new BigDecimal("100000.00"))
                .build();
        accountService.createAccount(accountC);
        accountCId = accountC.getId();

        // 创建测试库存（秒杀商品，库存100）
        Inventory inventory = Inventory.builder()
                .productId(System.currentTimeMillis())
                .warehouseId(1)
                .availableStock(100)
                .build();
        inventoryService.createInventory(inventory);
        inventoryId = inventory.getId();

        // 创建测试优惠券（限量100张）
        Coupon coupon = Coupon.builder()
                .couponCode("SCENE_COUPON_" + System.currentTimeMillis())
                .couponName("场景测试优惠券")
                .totalQuantity(100)
                .remainingQuantity(100)
                .discountAmount(new BigDecimal("50.00"))
                .validStartTime(LocalDateTime.now().minusDays(1))
                .validEndTime(LocalDateTime.now().plusDays(7))
                .status(1)
                .build();
        couponService.createCoupon(coupon);
        couponId = coupon.getId();

        log.info("测试数据准备完成: A={}, B={}, C={}, inventory={}, coupon={}",
                accountAId, accountBId, accountCId, inventoryId, couponId);
    }

    @Test
    @DisplayName("秒杀场景 - 100人抢100件商品（悲观锁）")
    void testSeckillScenario_PessimisticLock() throws InterruptedException {
        int buyerCount = 100;
        int stock = 100;

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(buyerCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < buyerCount; i++) {
            executor.submit(() -> {
                try {
                    // 每人购买1件
                    boolean success = inventoryService.deductStockWithPessimisticLock(inventoryId, 1);
                    if (success) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.debug("购买失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Inventory finalInventory = inventoryService.getInventoryById(inventoryId);
        log.info("秒杀结果: 成功={}, 失败={}, 剩余库存={}",
                successCount.get(), failCount.get(), finalInventory.getAvailableStock());

        assertEquals(stock, successCount.get(), "成功购买数应该等于库存数");
        assertEquals(0, finalInventory.getAvailableStock(), "库存应该售罄");
    }

    @Test
    @DisplayName("秒杀场景 - 100人抢100件商品（乐观锁）")
    void testSeckillScenario_OptimisticLock() throws InterruptedException {
        int buyerCount = 100;
        int stock = 100;

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(buyerCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < buyerCount; i++) {
            executor.submit(() -> {
                try {
                    // 每人购买1件
                    boolean success = inventoryService.deductStockWithOptimisticLock(inventoryId, 1);
                    if (success) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.debug("购买失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Inventory finalInventory = inventoryService.getInventoryById(inventoryId);
        log.info("秒杀结果(乐观锁): 成功={}, 失败={}, 剩余库存={}",
                successCount.get(), failCount.get(), finalInventory.getAvailableStock());

        // 乐观锁下可能有部分失败，但不应该超卖
        assertTrue(successCount.get() <= stock, "不应该超卖");
        assertEquals(stock - successCount.get(), finalInventory.getAvailableStock(),
                "剩余库存应该正确");
    }

    @Test
    @DisplayName("红包雨场景 - 100人抢100张优惠券（悲观锁）")
    void testCouponGrabScenario_PessimisticLock() throws InterruptedException {
        int userCount = 100;
        int couponQuantity = 100;

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(userCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < userCount; i++) {
            final Long userId = (long) (i + 10000); // 不同用户
            executor.submit(() -> {
                try {
                    boolean success = couponService.grabCouponWithPessimisticLock(userId, couponId);
                    if (success) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    log.debug("领取优惠券失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Coupon finalCoupon = couponService.getCouponById(couponId);
        log.info("红包雨结果: 成功={}, 失败={}, 剩余={}",
                successCount.get(), failCount.get(), finalCoupon.getRemainingQuantity());

        assertEquals(couponQuantity, successCount.get(), "成功领取数应该等于优惠券数量");
        assertEquals(0, finalCoupon.getRemainingQuantity(), "优惠券应该被领完");
    }

    @Test
    @DisplayName("转账高峰 - 多账户并发转账")
    void testTransferPeakScenario() throws InterruptedException {
        int threadCount = 50;
        int transfersPerThread = 10;
        BigDecimal transferAmount = new BigDecimal("10.00");

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount * transfersPerThread);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                for (int j = 0; j < transfersPerThread; j++) {
                    try {
                        // 循环转账：A->B->C->A
                        switch ((threadIndex + j) % 3) {
                            case 0:
                                accountService.transferWithPessimisticLock(accountAId, accountBId, transferAmount);
                                break;
                            case 1:
                                accountService.transferWithPessimisticLock(accountBId, accountCId, transferAmount);
                                break;
                            case 2:
                                accountService.transferWithPessimisticLock(accountCId, accountAId, transferAmount);
                                break;
                        }
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.debug("转账失败: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证总余额守恒
        Account accountA = accountService.getAccountById(accountAId);
        Account accountB = accountService.getAccountById(accountBId);
        Account accountC = accountService.getAccountById(accountCId);

        BigDecimal totalBalance = accountA.getBalance()
                .add(accountB.getBalance())
                .add(accountC.getBalance());

        log.info("转账高峰结果: 成功={}, 失败={}, 总余额={}",
                successCount.get(), failCount.get(), totalBalance);

        assertEquals(0, totalBalance.compareTo(new BigDecimal("300000.00")),
                "总余额应该保持不变（资金守恒）");
    }

    @Test
    @DisplayName("混合场景 - 转账、库存、优惠券同时进行")
    void testMixedScenario() throws InterruptedException {
        int threadCount = 30;

        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount * 3);
        AtomicInteger transferSuccess = new AtomicInteger(0);
        AtomicInteger stockSuccess = new AtomicInteger(0);
        AtomicInteger couponSuccess = new AtomicInteger(0);

        // 转账线程
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    accountService.transferWithPessimisticLock(
                            accountAId, accountBId, new BigDecimal("1.00"));
                    transferSuccess.incrementAndGet();
                } catch (Exception e) {
                    log.debug("转账失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 库存扣减线程
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    inventoryService.deductStockWithPessimisticLock(inventoryId, 1);
                    stockSuccess.incrementAndGet();
                } catch (Exception e) {
                    log.debug("扣减库存失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // 优惠券领取线程
        for (int i = 0; i < threadCount; i++) {
            final Long userId = (long) (i + 20000);
            executor.submit(() -> {
                try {
                    couponService.grabCouponWithPessimisticLock(userId, couponId);
                    couponSuccess.incrementAndGet();
                } catch (Exception e) {
                    log.debug("领取优惠券失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(60, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Account accountA = accountService.getAccountById(accountAId);
        Account accountB = accountService.getAccountById(accountBId);
        Inventory inventory = inventoryService.getInventoryById(inventoryId);
        Coupon coupon = couponService.getCouponById(couponId);

        log.info("混合场景结果: 转账成功={}, 库存扣减成功={}, 优惠券领取成功={}",
                transferSuccess.get(), stockSuccess.get(), couponSuccess.get());
        log.info("最终状态: A余额={}, B余额={}, 库存剩余={}, 优惠券剩余={}",
                accountA.getBalance(), accountB.getBalance(),
                inventory.getAvailableStock(), coupon.getRemainingQuantity());

        // 验证资金守恒
        BigDecimal totalBalance = accountA.getBalance()
                .add(accountB.getBalance())
                .add(accountService.getAccountById(accountCId).getBalance());
        assertEquals(0, totalBalance.compareTo(new BigDecimal("300000.00")), "总余额应该守恒");

        // 验证库存不超卖
        assertTrue(stockSuccess.get() <= 100, "库存不应该超卖");

        // 验证优惠券不超发
        assertTrue(couponSuccess.get() <= 100, "优惠券不应该超发");
    }

    @Test
    @DisplayName("预占库存场景 - 下单锁定，支付确认")
    void testStockPreoccupationScenario() throws InterruptedException {
        int orderCount = 50;
        int stockPerOrder = 2;
        int totalStock = 100;

        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(orderCount);
        AtomicInteger lockSuccess = new AtomicInteger(0);
        AtomicInteger confirmSuccess = new AtomicInteger(0);

        for (int i = 0; i < orderCount; i++) {
            executor.submit(() -> {
                try {
                    // 步骤1：锁定库存
                    boolean locked = inventoryService.lockStock(inventoryId, stockPerOrder);
                    if (locked) {
                        lockSuccess.incrementAndGet();

                        // 模拟支付处理（50%概率支付成功）
                        if (Math.random() > 0.5) {
                            // 支付成功，确认扣减
                            inventoryService.confirmDeduct(inventoryId, stockPerOrder);
                            confirmSuccess.incrementAndGet();
                        } else {
                            // 支付失败，释放库存
                            inventoryService.unlockStock(inventoryId, stockPerOrder);
                        }
                    }
                } catch (Exception e) {
                    log.debug("库存操作失败: {}", e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        Inventory finalInventory = inventoryService.getInventoryById(inventoryId);
        int lockedStock = finalInventory.getLockedStock();
        int availableStock = finalInventory.getAvailableStock();
        int totalRemaining = availableStock + lockedStock;

        log.info("预占库存结果: 锁定成功={}, 确认成功={}, 可用库存={}, 锁定库存={}",
                lockSuccess.get(), confirmSuccess.get(), availableStock, lockedStock);

        // 验证库存守恒
        assertEquals(totalStock, totalRemaining + confirmSuccess.get() * stockPerOrder,
                "库存总量应该守恒");
    }

    @Test
    @DisplayName("资金冻结场景 - 预占资金后转账")
    void testFundFreezeScenario() throws InterruptedException {
        BigDecimal freezeAmount = new BigDecimal("5000.00");
        BigDecimal transferAmount = new BigDecimal("3000.00");

        // 步骤1：冻结资金
        boolean frozen = accountService.freezeAmount(accountAId, freezeAmount);
        assertTrue(frozen, "冻结应该成功");

        Account accountAAfterFreeze = accountService.getAccountById(accountAId);
        log.info("冻结后: 余额={}, 冻结金额={}",
                accountAAfterFreeze.getBalance(), accountAAfterFreeze.getFrozenAmount());

        assertEquals(0, accountAAfterFreeze.getBalance().compareTo(new BigDecimal("95000.00")),
                "余额应该减少");
        assertEquals(0, accountAAfterFreeze.getFrozenAmount().compareTo(freezeAmount),
                "冻结金额应该正确");

        // 步骤2：从可用余额转账（应该成功）
        boolean transferSuccess = accountService.transferWithPessimisticLock(
                accountAId, accountBId, transferAmount);
        assertTrue(transferSuccess, "转账应该成功");

        Account accountAAfterTransfer = accountService.getAccountById(accountAId);
        Account accountBAfterTransfer = accountService.getAccountById(accountBId);
        log.info("转账后: A余额={}, A冻结={}, B余额={}",
                accountAAfterTransfer.getBalance(),
                accountAAfterTransfer.getFrozenAmount(),
                accountBAfterTransfer.getBalance());

        assertEquals(0, accountAAfterTransfer.getBalance().compareTo(new BigDecimal("92000.00")),
                "余额应该正确减少");

        // 步骤3：解冻资金
        boolean unfrozen = accountService.unfreezeAmount(accountAId, freezeAmount);
        assertTrue(unfrozen, "解冻应该成功");

        Account accountAFinal = accountService.getAccountById(accountAId);
        log.info("解冻后: 余额={}, 冻结金额={}",
                accountAFinal.getBalance(), accountAFinal.getFrozenAmount());

        assertEquals(0, accountAFinal.getBalance().compareTo(new BigDecimal("97000.00")),
                "解冻后余额应该恢复");
        assertEquals(0, accountAFinal.getFrozenAmount().compareTo(BigDecimal.ZERO),
                "冻结金额应该为0");
    }
}
