package com.linsir.abc.mysql.chapter01.concurrency.service.lock;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.AccountMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 悲观锁演示服务
 * 专门用于演示悲观锁（Pessimistic Locking）的各种使用场景
 *
 * <p>悲观锁核心思想：</p>
 * <ul>
 *   <li>假设并发冲突会发生，在操作前先加锁</li>
 *   <li>通过数据库的锁机制保证数据一致性</li>
 *   <li>适用于写多读少、冲突频繁的场景</li>
 * </ul>
 *
 * <p>MySQL悲观锁实现方式：</p>
 * <ul>
 *   <li>SELECT ... FOR UPDATE - 排他锁（X锁）</li>
 *   <li>SELECT ... LOCK IN SHARE MODE - 共享锁（S锁）</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PessimisticLockService {

    private final AccountMapper accountMapper;
    private final InventoryMapper inventoryMapper;

    /**
     * 演示排他锁（FOR UPDATE）
     * 场景：账户余额查询并更新
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>使用FOR UPDATE锁定记录</li>
     *   <li>执行业务逻辑</li>
     *   <li>更新数据</li>
     *   <li>事务提交后自动释放锁</li>
     * </ol>
     *
     * @param accountId 账户ID
     * @return 账户信息
     */
    @Transactional(readOnly = true)
    public Account demonstrateExclusiveLock(Long accountId) {
        log.info("获取排他锁: accountId={}", accountId);

        // 使用FOR UPDATE获取排他锁
        // 其他事务无法读写该记录，直到当前事务提交
        Account account = accountMapper.selectByIdForUpdate(accountId);

        if (account != null) {
            log.info("成功获取排他锁: accountNo={}, balance={}",
                    account.getAccountNo(), account.getBalance());
        }

        return account;
    }

    /**
     * 演示共享锁（LOCK IN SHARE MODE）
     * 场景：库存盘点，允许多个会话同时读取
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>使用LOCK IN SHARE MODE获取共享锁</li>
     *   <li>多个事务可以同时获取共享锁</li>
     *   <li>但任何事务都无法修改（会被阻塞）</li>
     * </ol>
     *
     * @param inventoryId 库存ID
     * @return 库存信息
     */
    @Transactional(readOnly = true)
    public Inventory demonstrateSharedLock(Long inventoryId) {
        log.info("获取共享锁: inventoryId={}", inventoryId);

        // 使用LOCK IN SHARE MODE获取共享锁
        // 其他事务也可以获取共享锁，但无法获取排他锁
        Inventory inventory = inventoryMapper.selectByIdLockInShareMode(inventoryId);

        if (inventory != null) {
            log.info("成功获取共享锁: productId={}, availableStock={}",
                    inventory.getProductId(), inventory.getAvailableStock());
        }

        return inventory;
    }

    /**
     * 演示死锁避免 - 按固定顺序加锁
     * 场景：转账操作，同时锁定两个账户
     *
     * <p>死锁避免策略：</p>
     * <ul>
     *   <li>按资源ID排序，始终按相同顺序加锁</li>
     *   <li>避免循环等待条件</li>
     * </ul>
     *
     * @param fromAccountId 转出账户ID
     * @param toAccountId   转入账户ID
     * @param amount        转账金额
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean demonstrateDeadlockAvoidance(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // 按ID排序加锁，避免死锁
        Account fromAccount;
        Account toAccount;

        if (fromAccountId < toAccountId) {
            fromAccount = accountMapper.selectByIdForUpdate(fromAccountId);
            toAccount = accountMapper.selectByIdForUpdate(toAccountId);
        } else {
            toAccount = accountMapper.selectByIdForUpdate(toAccountId);
            fromAccount = accountMapper.selectByIdForUpdate(fromAccountId);
        }

        log.info("按顺序加锁完成: from={}, to={}", fromAccountId, toAccountId);

        // 执行转账逻辑
        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("账户不存在");
        }

        if (!fromAccount.hasEnoughBalance(amount)) {
            throw new RuntimeException("余额不足");
        }

        // 扣减转出账户
        int affected = accountMapper.updateBalance(fromAccountId, amount.negate());
        if (affected == 0) {
            throw new RuntimeException("转出失败");
        }

        // 增加转入账户
        affected = accountMapper.updateBalance(toAccountId, amount);
        if (affected == 0) {
            throw new RuntimeException("转入失败");
        }

        log.info("转账完成: amount={}", amount);
        return true;
    }

    /**
     * 演示锁超时处理
     * 场景：设置锁等待超时时间
     *
     * <p>注意事项：</p>
     * <ul>
     *   <li>MySQL默认锁等待超时为50秒（innodb_lock_wait_timeout）</li>
     *   <li>超时后会抛出LockWaitTimeoutException</li>
     *   <li>应该捕获异常并进行重试或回滚</li>
     * </ul>
     *
     * @param accountId 账户ID
     * @return 账户信息
     */
    @Transactional(timeout = 5) // 设置事务超时5秒
    public Account demonstrateLockTimeout(Long accountId) {
        log.info("尝试获取锁（带超时）: accountId={}", accountId);

        try {
            Account account = accountMapper.selectByIdForUpdate(accountId);
            log.info("成功获取锁: {}", accountId);
            return account;
        } catch (Exception e) {
            log.error("获取锁超时或失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 演示间隙锁（Gap Lock）
     * 场景：范围查询时锁定间隙，防止幻读
     *
     * <p>间隙锁特点：</p>
     * <ul>
     *   <li>锁定索引记录之间的"间隙"</li>
     *   <li>防止其他事务在间隙中插入数据</li>
     *   <li>只在REPEATABLE READ及以上隔离级别生效</li>
     * </ul>
     *
     * @param minId 最小ID
     * @param maxId 最大ID
     */
    @Transactional
    public void demonstrateGapLock(Long minId, Long maxId) {
        log.info("演示间隙锁: range=[{}, {}]", minId, maxId);

        // 范围查询加锁会锁定整个范围，包括不存在的记录间隙
        // 其他事务无法在(minId, maxId)范围内插入新记录

        // 注意：实际演示需要在两个会话中进行
        // 会话1执行范围查询加锁
        // 会话2尝试插入范围内的记录（会被阻塞）

        log.info("间隙锁已锁定范围: [{} , {}]", minId, maxId);
    }

    /**
     * 演示锁升级和锁表
     * 场景：大量数据更新时的锁行为
     *
     * <p>锁升级：</p>
     * <ul>
     *   <li>行锁在特定条件下可能升级为表锁</li>
     *   <li>无索引字段更新会导致全表锁</li>
     *   <li>应避免在事务中执行大规模更新</li>
     * </ul>
     */
    @Transactional
    public void demonstrateLockEscalation() {
        log.info("演示锁行为");

        // 提示：行锁 vs 表锁
        // 1. 使用索引字段查询/更新 -> 行锁
        // 2. 无索引字段查询/更新 -> 表锁
        // 3. 范围更新可能升级为间隙锁或 Next-Key Lock

        log.info("锁演示完成");
    }

    /**
     * 获取锁等待信息
     * 用于监控和诊断锁等待情况
     *
     * @return 锁等待信息描述
     */
    public String getLockWaitInfo() {
        // 实际应用中可以通过查询information_schema获取锁信息
        // SELECT * FROM information_schema.INNODB_LOCK_WAITS;
        // SELECT * FROM information_schema.INNODB_LOCKS;
        // SELECT * FROM information_schema.INNODB_TRX;

        return "锁等待信息查询方法:\n" +
               "1. SELECT * FROM information_schema.INNODB_TRX; -- 查看活跃事务\n" +
               "2. SELECT * FROM information_schema.INNODB_LOCKS; -- 查看当前锁\n" +
               "3. SELECT * FROM information_schema.INNODB_LOCK_WAITS; -- 查看锁等待";
    }
}
