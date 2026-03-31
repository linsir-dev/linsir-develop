package com.linsir.abc.mysql.chapter01.concurrency;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 死锁测试类
 * 演示死锁的产生和避免
 *
 * <p>测试场景：</p>
 * <ul>
 *   <li>死锁演示 - 两个事务以相反顺序加锁</li>
 *   <li>死锁避免 - 按固定顺序加锁</li>
 * </ul>
 *
 * <p>死锁避免策略：</p>
 * <ul>
 *   <li>按资源ID排序加锁</li>
 *   <li>设置锁超时</li>
 *   <li>使用乐观锁替代悲观锁</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class DeadlockTest {

    @Autowired
    private AccountService accountService;

    private Long accountAId;
    private Long accountBId;

    @BeforeEach
    void setUp() {
        // 创建测试账户A
        Account accountA = Account.builder()
                .accountNo("DEADLOCK_A_" + System.currentTimeMillis())
                .accountName("死锁测试账户A")
                .balance(new BigDecimal("10000.00"))
                .build();
        accountService.createAccount(accountA);
        accountAId = accountA.getId();

        // 创建测试账户B
        Account accountB = Account.builder()
                .accountNo("DEADLOCK_B_" + (System.currentTimeMillis() + 1))
                .accountName("死锁测试账户B")
                .balance(new BigDecimal("10000.00"))
                .build();
        accountService.createAccount(accountB);
        accountBId = accountB.getId();

        log.info("创建测试账户: A={}, B={}", accountAId, accountBId);
    }

    @Test
    @DisplayName("测试转账死锁避免 - 按ID顺序加锁")
    void testTransferDeadlockAvoidance() throws InterruptedException {
        int threadCount = 10;
        int iterations = 10;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * iterations);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        // 交替进行 A->B 和 B->A 的转账
                        if ((threadIndex + j) % 2 == 0) {
                            accountService.transferWithPessimisticLock(
                                    accountAId, accountBId, new BigDecimal("10.00"));
                        } else {
                            accountService.transferWithPessimisticLock(
                                    accountBId, accountAId, new BigDecimal("10.00"));
                        }
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.error("转账失败: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("转账测试完成: 成功={}, 失败={}, 完成={}",
                successCount.get(), failCount.get(), completed);

        // 验证账户余额一致性
        Account accountA = accountService.getAccountById(accountAId);
        Account accountB = accountService.getAccountById(accountBId);

        BigDecimal totalBalance = accountA.getBalance().add(accountB.getBalance());
        assertEquals(0, totalBalance.compareTo(new BigDecimal("20000.00")),
                "总余额应该保持不变（无资金损失）");

        // 由于按ID顺序加锁，不应该出现死锁
        assertTrue(successCount.get() > 0, "应该有成功的转账");
    }

    @Test
    @DisplayName("测试乐观锁无死锁")
    void testOptimisticLockNoDeadlock() throws InterruptedException {
        int threadCount = 10;
        int iterations = 20;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * iterations);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        // 交替进行 A->B 和 B->A 的转账
                        if ((threadIndex + j) % 2 == 0) {
                            accountService.transferWithOptimisticLock(
                                    accountAId, accountBId, new BigDecimal("1.00"));
                        } else {
                            accountService.transferWithOptimisticLock(
                                    accountBId, accountAId, new BigDecimal("1.00"));
                        }
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        // 乐观锁冲突是正常的，不算错误
                        if (!e.getMessage().contains("请重试")) {
                            log.error("转账失败: {}", e.getMessage());
                        }
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("乐观锁转账测试完成: 成功={}, 失败={}, 完成={}",
                successCount.get(), failCount.get(), completed);

        // 验证账户余额一致性
        Account accountA = accountService.getAccountById(accountAId);
        Account accountB = accountService.getAccountById(accountBId);

        BigDecimal totalBalance = accountA.getBalance().add(accountB.getBalance());
        assertEquals(0, totalBalance.compareTo(new BigDecimal("20000.00")),
                "总余额应该保持不变");

        // 乐观锁不会出现死锁，但可能有版本冲突
        assertTrue(completed, "所有操作应该完成（无死锁）");
    }

    @Test
    @DisplayName("测试资金冻结与转账并发")
    void testFreezeAndTransferConcurrent() throws InterruptedException {
        int threadCount = 5;
        int iterations = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount * 2);
        CountDownLatch latch = new CountDownLatch(threadCount * iterations * 2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 冻结线程
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        accountService.freezeAmount(accountAId, new BigDecimal("10.00"));
                        Thread.sleep(10); // 模拟业务处理
                        accountService.unfreezeAmount(accountAId, new BigDecimal("10.00"));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.warn("冻结操作失败: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        // 转账线程
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        accountService.transferWithPessimisticLock(
                                accountAId, accountBId, new BigDecimal("5.00"));
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        log.warn("转账失败: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("并发测试完成: 成功={}, 失败={}, 完成={}",
                successCount.get(), failCount.get(), completed);

        // 验证账户状态
        Account accountA = accountService.getAccountById(accountAId);
        Account accountB = accountService.getAccountById(accountBId);

        // 冻结金额应该为0（所有冻结都已解冻）
        assertEquals(0, accountA.getFrozenAmount().compareTo(BigDecimal.ZERO),
                "冻结金额应该为0");

        // 总余额应该守恒
        BigDecimal totalBalance = accountA.getBalance().add(accountB.getBalance());
        assertEquals(0, totalBalance.compareTo(new BigDecimal("20000.00")),
                "总余额应该保持不变");

        assertTrue(completed, "所有操作应该完成");
    }

    @Test
    @DisplayName("测试多账户循环转账")
    void testMultiAccountCircularTransfer() throws InterruptedException {
        // 创建额外的测试账户
        Account accountC = Account.builder()
                .accountNo("DEADLOCK_C_" + System.currentTimeMillis())
                .accountName("死锁测试账户C")
                .balance(new BigDecimal("10000.00"))
                .build();
        accountService.createAccount(accountC);
        Long accountCId = accountC.getId();

        int threadCount = 6;
        int iterations = 5;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * iterations);
        AtomicInteger successCount = new AtomicInteger(0);

        // 循环转账：A->B->C->A
        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                for (int j = 0; j < iterations; j++) {
                    try {
                        switch ((threadIndex + j) % 3) {
                            case 0:
                                accountService.transferWithPessimisticLock(
                                        accountAId, accountBId, new BigDecimal("1.00"));
                                break;
                            case 1:
                                accountService.transferWithPessimisticLock(
                                        accountBId, accountCId, new BigDecimal("1.00"));
                                break;
                            case 2:
                                accountService.transferWithPessimisticLock(
                                        accountCId, accountAId, new BigDecimal("1.00"));
                                break;
                        }
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        log.error("转账失败: {}", e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }

        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("循环转账测试完成: 成功={}, 完成={}", successCount.get(), completed);

        // 验证总余额
        Account accountA = accountService.getAccountById(accountAId);
        Account accountB = accountService.getAccountById(accountBId);
        Account accountC2 = accountService.getAccountById(accountCId);

        BigDecimal totalBalance = accountA.getBalance()
                .add(accountB.getBalance())
                .add(accountC2.getBalance());
        assertEquals(0, totalBalance.compareTo(new BigDecimal("30000.00")),
                "总余额应该保持不变");

        assertTrue(completed, "所有操作应该完成（无死锁）");
    }
}
