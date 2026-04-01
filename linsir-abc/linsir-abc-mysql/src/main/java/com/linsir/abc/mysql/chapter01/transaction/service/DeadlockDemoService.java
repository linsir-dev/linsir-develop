package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 死锁演示服务
 *
 * <p>用于演示和解决死锁问题</p>
 *
 * <p>死锁产生的四个必要条件：</p>
 * <ul>
 *   <li>互斥条件：资源一次只能被一个事务占用</li>
 *   <li>请求与保持条件：事务持有资源的同时请求新资源</li>
 *   <li>不剥夺条件：已获得的资源不能被强制剥夺</li>
 *   <li>循环等待条件：事务之间形成循环等待资源的关系</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeadlockDemoService {

    private final BankAccountMapper accountMapper;

    /**
     * 演示死锁场景
     *
     * <p>死锁场景：</p>
     * <ol>
     *   <li>事务A锁定账户1</li>
     *   <li>事务B锁定账户2</li>
     *   <li>事务A尝试锁定账户2（等待）</li>
 *   <li>事务B尝试锁定账户1（等待）</li>
 *   <li>死锁发生</li>
 * </ol>
 *
 * @param accountNo1 账户1编号
 * @param accountNo2 账户2编号
 */
public void demonstrateDeadlock(String accountNo1, String accountNo2) {
    log.info("演示死锁场景，账户1：{}，账户2：{}", accountNo1, accountNo2);

    // 事务A
    CompletableFuture<Void> futureA = CompletableFuture.runAsync(() -> {
        transactionA(accountNo1, accountNo2);
    });

    // 事务B
    CompletableFuture<Void> futureB = CompletableFuture.runAsync(() -> {
        transactionB(accountNo2, accountNo1);
    });

    try {
        CompletableFuture.allOf(futureA, futureB).get(10, TimeUnit.SECONDS);
    } catch (Exception e) {
        log.error("死锁演示完成，发生异常：{}", e.getMessage());
    }
}

/**
 * 事务A：先锁定账户1，再锁定账户2
 *
 * @param accountNo1 账户1
 * @param accountNo2 账户2
 */
@Transactional
public void transactionA(String accountNo1, String accountNo2) {
    try {
        log.info("事务A：开始，准备锁定账户1：{}", accountNo1);

        // 锁定账户1
        BankAccount account1 = accountMapper.selectByAccountNoForUpdate(accountNo1);
        log.info("事务A：已锁定账户1，余额：{}", account1.getBalance());

        // 模拟业务处理
        Thread.sleep(1000);

        log.info("事务A：准备锁定账户2：{}", accountNo2);

        // 尝试锁定账户2（可能死锁）
        BankAccount account2 = accountMapper.selectByAccountNoForUpdate(accountNo2);
        log.info("事务A：已锁定账户2，余额：{}", account2.getBalance());

        // 执行转账
        accountMapper.updateBalance(account1.getId(), new BigDecimal("-100"), account1.getVersion());
        accountMapper.updateBalance(account2.getId(), new BigDecimal("100"), account2.getVersion());

        log.info("事务A：完成");

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("事务A：被中断");
    } catch (Exception e) {
        log.error("事务A：发生异常 - {}", e.getMessage());
        throw e;
    }
}

/**
 * 事务B：先锁定账户2，再锁定账户1
 *
 * @param accountNo2 账户2
 * @param accountNo1 账户1
 */
@Transactional
public void transactionB(String accountNo2, String accountNo1) {
    try {
        log.info("事务B：开始，准备锁定账户2：{}", accountNo2);

        // 锁定账户2
        BankAccount account2 = accountMapper.selectByAccountNoForUpdate(accountNo2);
        log.info("事务B：已锁定账户2，余额：{}", account2.getBalance());

        // 模拟业务处理
        Thread.sleep(1000);

        log.info("事务B：准备锁定账户1：{}", accountNo1);

        // 尝试锁定账户1（可能死锁）
        BankAccount account1 = accountMapper.selectByAccountNoForUpdate(accountNo1);
        log.info("事务B：已锁定账户1，余额：{}", account1.getBalance());

        // 执行转账
        accountMapper.updateBalance(account2.getId(), new BigDecimal("-100"), account2.getVersion());
        accountMapper.updateBalance(account1.getId(), new BigDecimal("100"), account1.getVersion());

        log.info("事务B：完成");

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("事务B：被中断");
    } catch (Exception e) {
        log.error("事务B：发生异常 - {}", e.getMessage());
        throw e;
    }
}

/**
 * 安全的转账操作（避免死锁）
 *
 * <p>避免死锁的方法：</p>
 * <ul>
 *   <li>按固定顺序获取锁</li>
 *   <li>设置锁等待超时</li>
 *   <li>使用乐观锁代替悲观锁</li>
 * </ul>
 *
 * @param fromAccountNo 转出账户
 * @param toAccountNo   转入账户
 * @param amount        金额
 */
@Transactional
public void safeTransfer(String fromAccountNo, String toAccountNo, BigDecimal amount) {
    log.info("安全转账，从 {} 到 {}，金额 {}", fromAccountNo, toAccountNo, amount);

    // 按账户ID排序，确保锁定顺序一致
    String firstLock = fromAccountNo.compareTo(toAccountNo) < 0 ? fromAccountNo : toAccountNo;
    String secondLock = fromAccountNo.compareTo(toAccountNo) < 0 ? toAccountNo : fromAccountNo;

    // 先锁定ID较小的账户
    BankAccount firstAccount = accountMapper.selectByAccountNoForUpdate(firstLock);
    log.info("已锁定账户：{}", firstLock);

    // 再锁定ID较大的账户
    BankAccount secondAccount = accountMapper.selectByAccountNoForUpdate(secondLock);
    log.info("已锁定账户：{}", secondLock);

    // 确定转出和转入账户
    BankAccount fromAccount = fromAccountNo.equals(firstLock) ? firstAccount : secondAccount;
    BankAccount toAccount = toAccountNo.equals(firstLock) ? firstAccount : secondAccount;

    // 检查余额
    if (fromAccount.getBalance().compareTo(amount) < 0) {
        throw new RuntimeException("余额不足");
    }

    // 执行转账
    accountMapper.updateBalance(fromAccount.getId(), amount.negate(), fromAccount.getVersion());
    accountMapper.updateBalance(toAccount.getId(), amount, toAccount.getVersion());

    log.info("安全转账完成");
}

/**
 * 使用乐观锁进行转账（避免死锁）
 *
 * <p>乐观锁不需要显式锁定，通过版本号控制并发</p>
 *
 * @param fromAccountNo 转出账户
 * @param toAccountNo   转入账户
 * @param amount        金额
 * @return 是否成功
 */
public boolean optimisticTransfer(String fromAccountNo, String toAccountNo, BigDecimal amount) {
    log.info("乐观锁转账，从 {} 到 {}，金额 {}", fromAccountNo, toAccountNo, amount);

    int maxRetries = 3;
    for (int i = 0; i < maxRetries; i++) {
        try {
            // 查询账户（不加锁）
            BankAccount fromAccount = accountMapper.selectByAccountNo(fromAccountNo);
            BankAccount toAccount = accountMapper.selectByAccountNo(toAccountNo);

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                log.error("余额不足");
                return false;
            }

            // 更新转出账户（乐观锁）
            int affected = accountMapper.updateBalance(
                    fromAccount.getId(),
                    amount.negate(),
                    fromAccount.getVersion()
            );

            if (affected == 0) {
                log.warn("乐观锁冲突，转出账户已被修改，重试 {}/{}", i + 1, maxRetries);
                continue;
            }

            // 更新转入账户（乐观锁）
            affected = accountMapper.updateBalance(
                    toAccount.getId(),
                    amount,
                    toAccount.getVersion()
            );

            if (affected == 0) {
                log.warn("乐观锁冲突，转入账户已被修改，重试 {}/{}", i + 1, maxRetries);
                // 这里应该回滚转出账户的扣减，简化处理
                continue;
            }

            log.info("乐观锁转账成功");
            return true;

        } catch (Exception e) {
            log.error("转账异常：{}", e.getMessage());
        }
    }

    log.error("乐观锁转账失败，重试次数用尽");
    return false;
}
}
