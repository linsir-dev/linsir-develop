package com.linsir.abc.mysql.chapter01.transaction.concurrent;

import com.linsir.abc.mysql.chapter01.transaction.service.IsolationDemoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 隔离级别测试
 *
 * <p>测试不同隔离级别下的并发行为</p>
 */
@SpringBootTest
class IsolationLevelTest {

    @Autowired
    private IsolationDemoService isolationDemoService;

    /**
     * 测试可重复读
     *
     * <p>验证REPEATABLE_READ级别下，同一事务内多次读取结果一致</p>
     */
    @Test
    void testRepeatableRead() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        // 线程1：执行可重复读演示
        executor.submit(() -> {
            try {
                BigDecimal[] balances = isolationDemoService.demonstrateRepeatableRead("ACC001");
                assert balances[0].equals(balances[1]) : "可重复读应该保证两次读取结果一致";
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        // 线程2：模拟并发修改
        executor.submit(() -> {
            try {
                Thread.sleep(500); // 等待线程1开始
                isolationDemoService.simulateConcurrentUpdate("ACC001", new BigDecimal("100.00"));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                latch.countDown();
            }
        });

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
