package com.linsir.abc.mysql.chapter01.concurrency.service.impl;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import com.linsir.abc.mysql.chapter01.concurrency.entity.TransactionLog;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.AccountMapper;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.TransactionLogMapper;
import com.linsir.abc.mysql.chapter01.concurrency.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * 账户服务实现类
 * 演示各种并发控制方案
 *
 * <p>实现要点：</p>
 * <ul>
 *   <li>悲观锁：使用SELECT FOR UPDATE，按ID顺序加锁避免死锁</li>
 *   <li>乐观锁：使用版本号，冲突时抛出异常</li>
 *   <li>事务管理：所有写操作在事务中执行</li>
 *   <li>流水记录：每笔资金变动都记录交易流水</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final TransactionLogMapper transactionLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Account createAccount(Account account) {
        // 初始化默认值
        account.setVersion(0);
        account.setStatus(1);
        account.setBalance(account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO);
        account.setFrozenAmount(BigDecimal.ZERO);

        accountMapper.insert(account);
        log.info("创建账户成功：accountNo={}, id={}", account.getAccountNo(), account.getId());
        return account;
    }

    @Override
    public Account getAccountById(Long id) {
        return accountMapper.selectById(id);
    }

    @Override
    public Account getAccountByNo(String accountNo) {
        return accountMapper.selectByAccountNo(accountNo);
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountMapper.selectAll();
    }

    /**
     * 转账（悲观锁实现）
     * 关键点：
     * 1. 使用SELECT FOR UPDATE锁定两个账户，防止并发修改
     * 2. 按ID顺序加锁，避免死锁
     * 3. 同一事务内完成所有操作
     * 4. 记录交易流水
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferWithPessimisticLock(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("转账金额必须大于0");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("不能给自己转账");
        }

        // 按ID顺序加锁，避免死锁
        Account fromAccount;
        Account toAccount;
        if (fromAccountId < toAccountId) {
            fromAccount = accountMapper.selectByIdForUpdate(fromAccountId);
            toAccount = accountMapper.selectByIdForUpdate(toAccountId);
        } else {
            toAccount = accountMapper.selectByIdForUpdate(toAccountId);
            fromAccount = accountMapper.selectByIdForUpdate(fromAccountId);
        }

        // 校验账户
        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("账户不存在");
        }
        if (!fromAccount.isActive() || !toAccount.isActive()) {
            throw new RuntimeException("账户已冻结");
        }
        if (!fromAccount.hasEnoughBalance(amount)) {
            throw new RuntimeException("余额不足");
        }

        // 执行转账 - 转出
        int affected = accountMapper.updateBalance(fromAccountId, amount.negate());
        if (affected == 0) {
            throw new RuntimeException("转出失败");
        }

        // 执行转账 - 转入
        affected = accountMapper.updateBalance(toAccountId, amount);
        if (affected == 0) {
            throw new RuntimeException("转入失败");
        }

        // 记录交易流水
        recordTransaction(fromAccountId, TransactionLog.TYPE_TRANSFER_OUT, amount.negate(),
                fromAccount.getBalance(), fromAccount.getBalance().subtract(amount), toAccountId);
        recordTransaction(toAccountId, TransactionLog.TYPE_TRANSFER_IN, amount,
                toAccount.getBalance(), toAccount.getBalance().add(amount), fromAccountId);

        log.info("转账成功（悲观锁）：fromAccountId={} -> toAccountId={}, amount={}",
                fromAccountId, toAccountId, amount);
        return true;
    }

    /**
     * 转账（乐观锁实现）
     * 关键点：
     * 1. 先读取账户信息和版本号
     * 2. 更新时检查版本号是否变化
     * 3. 版本号变化则抛出异常，需要调用方重试
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean transferWithOptimisticLock(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // 参数校验
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("转账金额必须大于0");
        }
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("不能给自己转账");
        }

        // 读取账户信息（无锁）
        Account fromAccount = accountMapper.selectById(fromAccountId);
        Account toAccount = accountMapper.selectById(toAccountId);

        // 校验账户
        if (fromAccount == null || toAccount == null) {
            throw new RuntimeException("账户不存在");
        }
        if (!fromAccount.isActive() || !toAccount.isActive()) {
            throw new RuntimeException("账户已冻结");
        }
        if (!fromAccount.hasEnoughBalance(amount)) {
            throw new RuntimeException("余额不足");
        }

        // 先扣减转出账户（乐观锁）
        int affected = accountMapper.updateBalanceWithVersion(
                fromAccountId, amount.negate(), fromAccount.getVersion());
        if (affected == 0) {
            log.warn("转出账户版本冲突，accountId={}", fromAccountId);
            throw new RuntimeException("转账失败，请重试");
        }

        // 再增加转入账户（乐观锁）
        affected = accountMapper.updateBalanceWithVersion(
                toAccountId, amount, toAccount.getVersion());
        if (affected == 0) {
            log.warn("转入账户版本冲突，accountId={}", toAccountId);
            throw new RuntimeException("转账失败，请重试");
        }

        // 记录交易流水
        recordTransaction(fromAccountId, TransactionLog.TYPE_TRANSFER_OUT, amount.negate(),
                fromAccount.getBalance(), fromAccount.getBalance().subtract(amount), toAccountId);
        recordTransaction(toAccountId, TransactionLog.TYPE_TRANSFER_IN, amount,
                toAccount.getBalance(), toAccount.getBalance().add(amount), fromAccountId);

        log.info("转账成功（乐观锁）：fromAccountId={} -> toAccountId={}, amount={}",
                fromAccountId, toAccountId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rechargeWithPessimisticLock(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        Account account = accountMapper.selectByIdForUpdate(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }
        if (!account.isActive()) {
            throw new RuntimeException("账户已冻结");
        }

        int affected = accountMapper.updateBalance(accountId, amount);
        if (affected == 0) {
            throw new RuntimeException("充值失败");
        }

        recordTransaction(accountId, TransactionLog.TYPE_RECHARGE, amount,
                account.getBalance(), account.getBalance().add(amount), null);

        log.info("充值成功（悲观锁）：accountId={}, amount={}", accountId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rechargeWithOptimisticLock(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        Account account = accountMapper.selectById(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }
        if (!account.isActive()) {
            throw new RuntimeException("账户已冻结");
        }

        int affected = accountMapper.updateBalanceWithVersion(
                accountId, amount, account.getVersion());
        if (affected == 0) {
            throw new RuntimeException("充值失败，请重试");
        }

        recordTransaction(accountId, TransactionLog.TYPE_RECHARGE, amount,
                account.getBalance(), account.getBalance().add(amount), null);

        log.info("充值成功（乐观锁）：accountId={}, amount={}", accountId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean freezeAmount(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("冻结金额必须大于0");
        }

        Account account = accountMapper.selectByIdForUpdate(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }
        if (!account.isActive()) {
            throw new RuntimeException("账户已冻结");
        }
        if (!account.hasEnoughBalance(amount)) {
            throw new RuntimeException("余额不足");
        }

        int affected = accountMapper.freezeAmount(accountId, amount);
        if (affected == 0) {
            throw new RuntimeException("冻结失败");
        }

        recordTransaction(accountId, TransactionLog.TYPE_FREEZE, amount.negate(),
                account.getBalance(), account.getBalance().subtract(amount), null);

        log.info("冻结成功：accountId={}, amount={}", accountId, amount);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unfreezeAmount(Long accountId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("解冻金额必须大于0");
        }

        Account account = accountMapper.selectByIdForUpdate(accountId);
        if (account == null) {
            throw new RuntimeException("账户不存在");
        }

        BigDecimal frozenAmount = account.getFrozenAmount() != null ? account.getFrozenAmount() : BigDecimal.ZERO;
        if (frozenAmount.compareTo(amount) < 0) {
            throw new RuntimeException("冻结金额不足");
        }

        int affected = accountMapper.unfreezeAmount(accountId, amount);
        if (affected == 0) {
            throw new RuntimeException("解冻失败");
        }

        recordTransaction(accountId, TransactionLog.TYPE_UNFREEZE, amount,
                account.getBalance(), account.getBalance().add(amount), null);

        log.info("解冻成功：accountId={}, amount={}", accountId, amount);
        return true;
    }

    /**
     * 记录交易流水
     *
     * @param accountId       账户ID
     * @param type            交易类型
     * @param amount          交易金额
     * @param balanceBefore   交易前余额
     * @param balanceAfter    交易后余额
     * @param relatedAccountId 对方账户ID
     */
    private void recordTransaction(Long accountId, Integer type, BigDecimal amount,
                                   BigDecimal balanceBefore, BigDecimal balanceAfter, Long relatedAccountId) {
        String transactionNo = UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        TransactionLog logEntry = TransactionLog.builder()
                .transactionNo(transactionNo)
                .accountId(accountId)
                .transactionType(type)
                .amount(amount)
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .relatedAccountId(relatedAccountId)
                .build();
        transactionLogMapper.insert(logEntry);
    }
}
