package com.linsir.abc.mysql.chapter01.concurrency.service;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户服务接口
 * 演示转账、充值等并发场景
 *
 * <p>并发控制方案：</p>
 * <ul>
 *   <li>悲观锁：使用SELECT FOR UPDATE保证事务隔离性，适合强一致性场景</li>
 *   <li>乐观锁：使用版本号控制并发，适合高并发读多写少场景</li>
 * </ul>
 *
 * <p>核心功能：</p>
 * <ul>
 *   <li>转账：支持悲观锁和乐观锁两种实现</li>
 *   <li>充值：支持悲观锁和乐观锁两种实现</li>
 *   <li>冻结/解冻：资金预占和释放</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
public interface AccountService {

    /**
     * 创建账户
     *
     * @param account 账户信息
     * @return 创建的账户
     */
    Account createAccount(Account account);

    /**
     * 根据ID查询账户
     *
     * @param id 账户ID
     * @return 账户信息
     */
    Account getAccountById(Long id);

    /**
     * 根据账户编号查询账户
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    Account getAccountByNo(String accountNo);

    /**
     * 查询所有账户
     *
     * @return 账户列表
     */
    List<Account> getAllAccounts();

    /**
     * 转账（悲观锁实现）
     * 使用SELECT FOR UPDATE锁定两个账户，保证事务隔离性
     * 按ID顺序加锁，避免死锁
     *
     * @param fromAccountId 转出账户ID
     * @param toAccountId   转入账户ID
     * @param amount        转账金额
     * @return 是否成功
     */
    boolean transferWithPessimisticLock(Long fromAccountId, Long toAccountId, BigDecimal amount);

    /**
     * 转账（乐观锁实现）
     * 使用版本号控制并发，更新时检查版本号
     * 版本冲突时抛出异常，需要调用方重试
     *
     * @param fromAccountId 转出账户ID
     * @param toAccountId   转入账户ID
     * @param amount        转账金额
     * @return 是否成功
     */
    boolean transferWithOptimisticLock(Long fromAccountId, Long toAccountId, BigDecimal amount);

    /**
     * 充值（悲观锁）
     * 使用SELECT FOR UPDATE锁定账户
     *
     * @param accountId 账户ID
     * @param amount    充值金额
     * @return 是否成功
     */
    boolean rechargeWithPessimisticLock(Long accountId, BigDecimal amount);

    /**
     * 充值（乐观锁）
     * 使用版本号控制并发
     *
     * @param accountId 账户ID
     * @param amount    充值金额
     * @return 是否成功
     */
    boolean rechargeWithOptimisticLock(Long accountId, BigDecimal amount);

    /**
     * 冻结金额
     * 将余额转为冻结金额，用于预占资金场景
     *
     * @param accountId 账户ID
     * @param amount    冻结金额
     * @return 是否成功
     */
    boolean freezeAmount(Long accountId, BigDecimal amount);

    /**
     * 解冻金额
     * 将冻结金额转回余额
     *
     * @param accountId 账户ID
     * @param amount    解冻金额
     * @return 是否成功
     */
    boolean unfreezeAmount(Long accountId, BigDecimal amount);
}
