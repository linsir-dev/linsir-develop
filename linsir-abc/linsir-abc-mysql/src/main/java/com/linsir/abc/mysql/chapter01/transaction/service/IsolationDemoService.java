package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.mapper.BankAccountMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 事务隔离级别演示服务
 *
 * <p>用于演示不同隔离级别下的并发问题</p>
 *
 * <p>隔离级别说明：</p>
 * <ul>
 *   <li>READ_UNCOMMITTED: 读未提交，可能出现脏读</li>
 *   <li>READ_COMMITTED: 读已提交，可能出现不可重复读</li>
 *   <li>REPEATABLE_READ: 可重复读（MySQL默认），可能出现幻读</li>
 *   <li>SERIALIZABLE: 串行化，完全隔离但性能最差</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IsolationDemoService {

    private final BankAccountMapper accountMapper;

    /**
     * 演示脏读（Dirty Read）
     *
     * <p>脏读：一个事务读取了另一个事务未提交的数据</p>
     *
     * <p>场景：</p>
     * <ol>
     *   <li>事务A修改账户余额，但不提交</li>
     *   <li>事务B读取账户余额（READ_UNCOMMITTED）</li>
     *   <li>事务A回滚</li>
     *   <li>事务B读取到的是脏数据</li>
     * </ol>
     *
     * @param accountNo 账户编号
     * @return 读取到的余额
     */
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public BigDecimal demonstrateDirtyRead(String accountNo) {
        log.info("演示脏读，账户：{}，隔离级别：READ_UNCOMMITTED", accountNo);
        BankAccount account = accountMapper.selectByAccountNo(accountNo);
        if (account != null) {
            log.info("读取到余额：{}", account.getBalance());
            return account.getBalance();
        }
        return null;
    }

    /**
     * 演示不可重复读（Non-repeatable Read）
     *
     * <p>不可重复读：在同一个事务中，多次读取同一数据返回不同结果</p>
     *
     * <p>场景：</p>
     * <ol>
     *   <li>事务A读取账户余额（READ_COMMITTED）</li>
     *   <li>事务B修改账户余额并提交</li>
     *   <li>事务A再次读取账户余额，结果不同</li>
     * </ol>
     *
     * @param accountNo 账户编号
     * @return 两次读取的余额
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BigDecimal[] demonstrateNonRepeatableRead(String accountNo) throws InterruptedException {
        log.info("演示不可重复读，账户：{}，隔离级别：READ_COMMITTED", accountNo);

        // 第一次读取
        BankAccount account1 = accountMapper.selectByAccountNo(accountNo);
        BigDecimal balance1 = account1 != null ? account1.getBalance() : null;
        log.info("第一次读取余额：{}", balance1);

        // 模拟等待其他事务修改
        Thread.sleep(2000);

        // 第二次读取
        BankAccount account2 = accountMapper.selectByAccountNo(accountNo);
        BigDecimal balance2 = account2 != null ? account2.getBalance() : null;
        log.info("第二次读取余额：{}", balance2);

        return new BigDecimal[]{balance1, balance2};
    }

    /**
     * 演示可重复读（Repeatable Read）
     *
     * <p>可重复读：在同一个事务中，多次读取同一数据返回相同结果</p>
     *
     * <p>MySQL通过MVCC实现可重复读</p>
     *
     * @param accountNo 账户编号
     * @return 两次读取的余额
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public BigDecimal[] demonstrateRepeatableRead(String accountNo) throws InterruptedException {
        log.info("演示可重复读，账户：{}，隔离级别：REPEATABLE_READ", accountNo);

        // 第一次读取
        BankAccount account1 = accountMapper.selectByAccountNo(accountNo);
        BigDecimal balance1 = account1 != null ? account1.getBalance() : null;
        log.info("第一次读取余额：{}", balance1);

        // 模拟等待其他事务修改
        Thread.sleep(2000);

        // 第二次读取（应该与第一次相同）
        BankAccount account2 = accountMapper.selectByAccountNo(accountNo);
        BigDecimal balance2 = account2 != null ? account2.getBalance() : null;
        log.info("第二次读取余额：{}", balance2);

        return new BigDecimal[]{balance1, balance2};
    }

    /**
     * 演示幻读（Phantom Read）
     *
     * <p>幻读：在同一个事务中，两次查询返回的行数不同</p>
     *
     * <p>在MySQL的REPEATABLE_READ隔离级别下，幻读通常不会发生，
     * 因为InnoDB使用Next-Key Locking防止幻读</p>
     *
     * @return 两次查询的账户数量
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public int[] demonstratePhantomRead() throws InterruptedException {
        log.info("演示幻读，隔离级别：REPEATABLE_READ");

        // 第一次查询
        int count1 = accountMapper.selectAll().size();
        log.info("第一次查询账户数量：{}", count1);

        // 模拟等待其他事务插入
        Thread.sleep(2000);

        // 第二次查询
        int count2 = accountMapper.selectAll().size();
        log.info("第二次查询账户数量：{}", count2);

        return new int[]{count1, count2};
    }

    /**
     * 演示串行化（Serializable）
     *
     * <p>串行化：完全隔离，事务串行执行</p>
     *
     * <p>性能最差，但完全避免并发问题</p>
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public BankAccount demonstrateSerializable(String accountNo) {
        log.info("演示串行化，账户：{}，隔离级别：SERIALIZABLE", accountNo);
        return accountMapper.selectByAccountNo(accountNo);
    }

    /**
     * 模拟并发修改
     *
     * <p>用于配合隔离级别演示</p>
     *
     * @param accountNo 账户编号
     * @param amount    修改金额
     */
    public void simulateConcurrentUpdate(String accountNo, BigDecimal amount) {
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000); // 延迟1秒执行
                updateBalance(accountNo, amount);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * 修改账户余额
     *
     * @param accountNo 账户编号
     * @param amount    修改金额
     */
    @Transactional
    public void updateBalance(String accountNo, BigDecimal amount) {
        BankAccount account = accountMapper.selectByAccountNoForUpdate(accountNo);
        if (account != null) {
            int affected = accountMapper.updateBalance(account.getId(), amount, account.getVersion());
            if (affected > 0) {
                log.info("并发修改成功，账户：{}，金额：{}", accountNo, amount);
            }
        }
    }
}
