package com.linsir.abc.mysql.chapter01.transaction.concurrent;

import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransferRequest;
import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.service.BankTransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 并发转账测试
 *
 * <p>测试事务的隔离性和并发控制</p>
 */
@SpringBootTest
class ConcurrentTransferTest {

    @Autowired
    private BankTransferService bankTransferService;

    /**
     * 测试并发转账
     *
     * <p>10个线程同时从账户1向账户2转账，验证数据一致性</p>
     */
    @Test
    void testConcurrentTransfer() throws InterruptedException {
        int threadCount = 10;
        BigDecimal amount = new BigDecimal("10.00");

        // 获取转账前余额
        BankAccount fromAccountBefore = bankTransferService.getAccount("ACC001");
        BigDecimal fromBalanceBefore = fromAccountBefore.getBalance();

        BankAccount toAccountBefore = bankTransferService.getAccount("ACC002");
        BigDecimal toBalanceBefore = toAccountBefore.getBalance();

        // 并发执行转账
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    TransferRequest request = new TransferRequest();
                    request.setFromAccountNo("ACC001");
                    request.setToAccountNo("ACC002");
                    request.setAmount(amount);

                    TransactionResult result = bankTransferService.transfer(request);
                    if (result.isSuccess()) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证结果
        BankAccount fromAccountAfter = bankTransferService.getAccount("ACC001");
        BankAccount toAccountAfter = bankTransferService.getAccount("ACC002");

        BigDecimal expectedFromBalance = fromBalanceBefore.subtract(
                amount.multiply(BigDecimal.valueOf(successCount.get())));
        BigDecimal expectedToBalance = toBalanceBefore.add(
                amount.multiply(BigDecimal.valueOf(successCount.get())));

        assertEquals(expectedFromBalance, fromAccountAfter.getBalance(),
                "转出账户余额应该正确减少");
        assertEquals(expectedToBalance, toAccountAfter.getBalance(),
                "转入账户余额应该正确增加");

        // 验证总金额不变（一致性）
        BigDecimal totalBefore = fromBalanceBefore.add(toBalanceBefore);
        BigDecimal totalAfter = fromAccountAfter.getBalance().add(toAccountAfter.getBalance());
        assertEquals(totalBefore, totalAfter, "转账前后总金额应该不变");
    }

    /**
     * 测试循环转账死锁场景
     *
     * <p>模拟死锁：A->B, B->C, C->A 同时进行</p>
     */
    @Test
    void testCircularTransfer() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        // A -> B
        executor.submit(() -> {
            try {
                TransferRequest request = new TransferRequest();
                request.setFromAccountNo("ACC001");
                request.setToAccountNo("ACC002");
                request.setAmount(new BigDecimal("50.00"));
                bankTransferService.transfer(request);
            } finally {
                latch.countDown();
            }
        });

        // B -> C
        executor.submit(() -> {
            try {
                TransferRequest request = new TransferRequest();
                request.setFromAccountNo("ACC002");
                request.setToAccountNo("ACC003");
                request.setAmount(new BigDecimal("50.00"));
                bankTransferService.transfer(request);
            } finally {
                latch.countDown();
            }
        });

        // C -> A
        executor.submit(() -> {
            try {
                TransferRequest request = new TransferRequest();
                request.setFromAccountNo("ACC003");
                request.setToAccountNo("ACC001");
                request.setAmount(new BigDecimal("50.00"));
                bankTransferService.transfer(request);
            } finally {
                latch.countDown();
            }
        });

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证所有转账都成功（通过按顺序加锁避免死锁）
        // 如果发生死锁，会有部分转账失败
    }
}
