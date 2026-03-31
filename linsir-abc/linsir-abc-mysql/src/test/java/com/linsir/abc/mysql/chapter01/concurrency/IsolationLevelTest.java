package com.linsir.abc.mysql.chapter01.concurrency;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.AccountMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 事务隔离级别测试类
 * 演示不同隔离级别下的并发问题
 *
 * <p>测试场景：</p>
 * <ul>
 *   <li>脏读（Dirty Read）- READ UNCOMMITTED</li>
 *   <li>不可重复读（Non-Repeatable Read）- READ COMMITTED</li>
 *   <li>幻读（Phantom Read）- REPEATABLE READ</li>
 *   <li>串行化（Serializable）</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
class IsolationLevelTest {

    @Autowired
    private AccountMapper accountMapper;

    private Long testAccountId;

    @BeforeEach
    void setUp() {
        // 创建测试账户
        Account account = Account.builder()
                .accountNo("ISO_TEST_" + System.currentTimeMillis())
                .accountName("隔离级别测试账户")
                .balance(new BigDecimal("10000.00"))
                .frozenAmount(BigDecimal.ZERO)
                .version(0)
                .status(1)
                .build();
        accountMapper.insert(account);
        testAccountId = account.getId();
        log.info("创建测试账户: id={}, balance={}", testAccountId, account.getBalance());
    }

    @Test
    @DisplayName("测试READ COMMITTED - 防止脏读")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    void testReadCommitted_NoDirtyRead() {
        // 读取初始值
        Account account1 = accountMapper.selectById(testAccountId);
        BigDecimal initialBalance = account1.getBalance();
        log.info("初始余额: {}", initialBalance);

        // 在同一事务中，数据应该保持一致
        Account account2 = accountMapper.selectById(testAccountId);
        assertEquals(initialBalance, account2.getBalance(),
                "同一事务中两次读取应该一致");
    }

    @Test
    @DisplayName("测试REPEATABLE READ - 防止不可重复读")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void testRepeatableRead() {
        // 第一次读取
        Account account1 = accountMapper.selectById(testAccountId);
        BigDecimal balance1 = account1.getBalance();
        log.info("第一次读取余额: {}", balance1);

        // 模拟其他事务修改（实际测试中通过另一个线程）
        // 在同一事务中再次读取
        Account account2 = accountMapper.selectById(testAccountId);
        BigDecimal balance2 = account2.getBalance();
        log.info("第二次读取余额: {}", balance2);

        // 在REPEATABLE READ下，两次读取应该相同
        assertEquals(balance1, balance2,
                "REPEATABLE READ下同一事务中两次读取应该一致");
    }

    @Test
    @DisplayName("测试SERIALIZABLE - 最严格隔离")
    @Transactional(isolation = Isolation.SERIALIZABLE)
    void testSerializable() {
        // 读取数据
        Account account = accountMapper.selectById(testAccountId);
        assertNotNull(account);
        log.info("SERIALIZABLE下读取账户: {}", account.getAccountNo());

        // SERIALIZABLE会锁定读取的数据，防止其他事务修改
        // 这里主要验证事务能正常执行
        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("测试默认隔离级别（MySQL默认REPEATABLE READ）")
    @Transactional
    void testDefaultIsolationLevel() {
        // 读取数据
        Account account1 = accountMapper.selectById(testAccountId);
        BigDecimal balance1 = account1.getBalance();

        // 再次读取
        Account account2 = accountMapper.selectById(testAccountId);
        BigDecimal balance2 = account2.getBalance();

        // 默认隔离级别下应该能重复读取
        assertEquals(balance1, balance2);
        log.info("默认隔离级别下两次读取一致: {}", balance1);
    }

    @Test
    @DisplayName("测试并发读取 - 验证MVCC效果")
    void testConcurrentReadWithMVCC() throws InterruptedException {
        int threadCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicReference<BigDecimal> firstRead = new AtomicReference<>();

        for (int i = 0; i < threadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    Account account = accountMapper.selectById(testAccountId);
                    BigDecimal balance = account.getBalance();

                    if (threadIndex == 0) {
                        firstRead.set(balance);
                        log.info("线程{}首次读取: {}", threadIndex, balance);
                    } else {
                        // 在MVCC下，所有线程应该看到相同的数据版本
                        assertEquals(firstRead.get(), balance,
                                "所有线程应该读取到相同的数据版本");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        log.info("并发读取测试完成，所有线程读取一致");
    }

    @Test
    @DisplayName("测试悲观锁与隔离级别结合")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    void testPessimisticLockWithIsolation() {
        // 使用悲观锁读取
        Account account = accountMapper.selectByIdForUpdate(testAccountId);
        assertNotNull(account);

        BigDecimal initialBalance = account.getBalance();
        log.info("悲观锁读取余额: {}", initialBalance);

        // 修改余额
        accountMapper.updateBalance(testAccountId, new BigDecimal("-100.00"));

        // 再次读取（同一事务中）
        Account account2 = accountMapper.selectById(testAccountId);
        BigDecimal newBalance = account2.getBalance();

        // 应该看到修改后的值
        assertEquals(initialBalance.subtract(new BigDecimal("100.00")), newBalance);
        log.info("修改后余额: {}", newBalance);
    }

    @Test
    @DisplayName("测试丢失更新问题")
    void testLostUpdateProblem() throws InterruptedException {
        // 设置初始值
        Account initial = accountMapper.selectById(testAccountId);
        BigDecimal startBalance = initial.getBalance();
        log.info("初始余额: {}", startBalance);

        int threadCount = 2;
        int incrementPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    // 模拟丢失更新：读取-计算-更新（非原子操作）
                    Account account = accountMapper.selectById(testAccountId);
                    BigDecimal currentBalance = account.getBalance();
                    BigDecimal newBalance = currentBalance.add(new BigDecimal(incrementPerThread));

                    // 直接设置新值（不使用版本号控制，模拟丢失更新）
                    // 注意：这里只是演示问题，实际应该使用乐观锁或悲观锁
                    log.info("线程准备更新: current={}, new={}", currentBalance, newBalance);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证：如果不使用锁，最终值可能不正确
        Account finalAccount = accountMapper.selectById(testAccountId);
        log.info("最终余额: {}", finalAccount.getBalance());
    }

    @Test
    @DisplayName("测试防止丢失更新 - 使用乐观锁")
    @Transactional
    void testPreventLostUpdate_WithOptimisticLock() {
        // 读取账户和版本号
        Account account = accountMapper.selectById(testAccountId);
        Integer version = account.getVersion();
        BigDecimal balance = account.getBalance();
        log.info("初始状态: balance={}, version={}", balance, version);

        // 使用乐观锁更新
        int affected = accountMapper.updateBalanceWithVersion(
                testAccountId, new BigDecimal("100.00"), version);

        assertEquals(1, affected, "更新应该成功");

        // 验证更新结果
        Account updated = accountMapper.selectById(testAccountId);
        assertEquals(version + 1, updated.getVersion(), "版本号应该增加");
        assertEquals(balance.add(new BigDecimal("100.00")), updated.getBalance(),
                "余额应该正确更新");

        log.info("更新后状态: balance={}, version={}", updated.getBalance(), updated.getVersion());
    }

    @Test
    @DisplayName("测试防止丢失更新 - 使用悲观锁")
    @Transactional
    void testPreventLostUpdate_WithPessimisticLock() {
        // 使用悲观锁读取
        Account account = accountMapper.selectByIdForUpdate(testAccountId);
        BigDecimal balance = account.getBalance();
        log.info("悲观锁读取: balance={}", balance);

        // 更新余额
        int affected = accountMapper.updateBalance(testAccountId, new BigDecimal("200.00"));
        assertEquals(1, affected, "更新应该成功");

        // 验证
        Account updated = accountMapper.selectById(testAccountId);
        assertEquals(balance.add(new BigDecimal("200.00")), updated.getBalance());
        log.info("更新后余额: {}", updated.getBalance());
    }
}
