package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransferRequest;
import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.BankTransactionLog;
import com.linsir.abc.mysql.chapter01.transaction.entity.TransferRecord;
import com.linsir.abc.mysql.chapter01.transaction.mapper.BankAccountMapper;
import com.linsir.abc.mysql.chapter01.transaction.mapper.BankTransactionLogMapper;
import com.linsir.abc.mysql.chapter01.transaction.mapper.TransferRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.UUID;

/**
 * 银行转账服务实现类
 *
 * <p>实现银行转账业务逻辑，演示事务的ACID特性</p>
 *
 * <p>事务传播行为说明：</p>
 * <ul>
 *   <li>REQUIRED: 默认行为，如果当前有事务则加入，没有则创建新事务</li>
 *   <li>REQUIRES_NEW: 创建新事务，如果当前有事务则挂起</li>
 *   <li>NESTED: 嵌套事务，可以独立回滚</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BankTransferServiceImpl implements BankTransferService {

    private final BankAccountMapper accountMapper;
    private final BankTransactionLogMapper transactionLogMapper;
    private final TransferRecordMapper transferRecordMapper;

    /**
     * 执行转账
     *
     * <p>事务特性演示：</p>
     * <ul>
     *   <li>原子性：扣减转出账户余额和增加转入账户余额作为一个原子操作</li>
     *   <li>一致性：转账前后总金额不变</li>
     *   <li>隔离性：使用READ_COMMITTED隔离级别，避免脏读</li>
     *   <li>持久性：通过Redo Log保证事务提交后数据不丢失</li>
     * </ul>
     *
     * @param request 转账请求
     * @return 事务结果
     */
    @Override
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class,
            timeout = 30
    )
    public TransactionResult transfer(TransferRequest request) {
        // 参数校验
        String validationError = request.getValidationError();
        if (validationError != null) {
            return TransactionResult.fail(validationError);
        }

        String transferNo = generateTransferNo();
        log.info("开始转账，单号：{}，从 {} 到 {}，金额：{}",
                transferNo, request.getFromAccountNo(),
                request.getToAccountNo(), request.getAmount());

        try {
            // 1. 查询转出账户
            BankAccount fromAccount = accountMapper.selectByAccountNo(request.getFromAccountNo());
            if (fromAccount == null) {
                return TransactionResult.fail("转出账户不存在");
            }
            if (!fromAccount.isAvailable()) {
                return TransactionResult.fail("转出账户已被冻结");
            }

            // 2. 检查余额
            if (!fromAccount.hasSufficientBalance(request.getAmount())) {
                return TransactionResult.fail("余额不足，当前可用余额：" + fromAccount.getAvailableBalance());
            }

            // 3. 查询转入账户
            BankAccount toAccount = accountMapper.selectByAccountNo(request.getToAccountNo());
            if (toAccount == null) {
                return TransactionResult.fail("转入账户不存在");
            }
            if (!toAccount.isAvailable()) {
                return TransactionResult.fail("转入账户已被冻结");
            }

            // 4. 创建转账记录
            TransferRecord record = new TransferRecord();
            record.setTransferNo(transferNo);
            record.setFromAccountId(fromAccount.getId());
            record.setToAccountId(toAccount.getId());
            record.setAmount(request.getAmount());
            record.setFee(BigDecimal.ZERO);
            record.setStatus(TransferRecord.STATUS_PROCESSING);
            record.setRemark(request.getRemark());
            record.setCreatedAt(LocalDateTime.now());
            transferRecordMapper.insert(record);

            // 5. 扣减转出账户余额（使用乐观锁防止并发问题）
            int affected = accountMapper.updateBalance(
                    fromAccount.getId(),
                    request.getAmount().negate(),
                    fromAccount.getVersion()
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("转出账户余额已被修改，请重试");
            }

            // 6. 记录转出流水
            BankTransactionLog fromLog = createTransactionLog(
                    fromAccount.getId(),
                    BankTransactionLog.TYPE_TRANSFER_OUT,
                    request.getAmount().negate(),
                    fromAccount.getBalance(),
                    fromAccount.getBalance().subtract(request.getAmount()),
                    toAccount.getId(),
                    "转账给" + toAccount.getAccountName()
            );
            transactionLogMapper.insert(fromLog);

            // 7. 增加转入账户余额
            affected = accountMapper.updateBalance(
                    toAccount.getId(),
                    request.getAmount(),
                    toAccount.getVersion()
            );
            if (affected == 0) {
                throw new ConcurrentModificationException("转入账户余额已被修改，请重试");
            }

            // 8. 记录转入流水
            BankTransactionLog toLog = createTransactionLog(
                    toAccount.getId(),
                    BankTransactionLog.TYPE_TRANSFER_IN,
                    request.getAmount(),
                    toAccount.getBalance(),
                    toAccount.getBalance().add(request.getAmount()),
                    fromAccount.getId(),
                    "接收" + fromAccount.getAccountName() + "转账"
            );
            transactionLogMapper.insert(toLog);

            // 9. 更新转账记录状态为成功
            transferRecordMapper.updateStatus(transferNo, TransferRecord.STATUS_SUCCESS);

            log.info("转账成功，单号：{}", transferNo);
            return TransactionResult.success(transferNo, "转账成功");

        } catch (Exception e) {
            log.error("转账失败，单号：{}，错误：{}", transferNo, e.getMessage());
            // 更新转账记录状态为失败
            transferRecordMapper.updateStatus(transferNo, TransferRecord.STATUS_FAILED);
            throw e; // 抛出异常触发事务回滚
        }
    }

    /**
     * 跨行转账
     *
     * <p>模拟分布式事务场景，实际场景中可能需要调用其他银行的接口</p>
     *
     * @param fromAccountNo 转出账户
     * @param toAccountNo   转入账户
     * @param amount        金额
     * @return 事务结果
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransactionResult crossBankTransfer(String fromAccountNo, String toAccountNo, BigDecimal amount) {
        // 实际场景中可能需要调用其他银行的接口
        // 这里演示本地事务的处理
        TransferRequest request = new TransferRequest();
        request.setFromAccountNo(fromAccountNo);
        request.setToAccountNo(toAccountNo);
        request.setAmount(amount);
        request.setRemark("跨行转账");

        return transfer(request);
    }

    /**
     * 批量转账
     *
     * <p>注意：长事务的风险，建议将批量操作拆分为多个小事务</p>
     *
     * @param requests 转账请求列表
     * @return 事务结果列表
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, timeout = 300)
    public List<TransactionResult> batchTransfer(List<TransferRequest> requests) {
        // 建议：将批量操作拆分为多个小事务
        // 或者使用批量插入等优化手段
        return requests.stream()
                .map(this::transfer)
                .toList();
    }

    @Override
    public BankAccount getAccount(String accountNo) {
        return accountMapper.selectByAccountNo(accountNo);
    }

    @Override
    public TransferRecord getTransferRecord(String transferNo) {
        return transferRecordMapper.selectByTransferNo(transferNo);
    }

    @Override
    public List<TransferRecord> getTransferRecordsByAccount(String accountNo) {
        BankAccount account = accountMapper.selectByAccountNo(accountNo);
        if (account == null) {
            return List.of();
        }
        return transferRecordMapper.selectByFromAccountId(account.getId());
    }

    /**
     * 生成转账单号
     *
     * @return 转账单号
     */
    private String generateTransferNo() {
        return "TRF" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
    }

    /**
     * 创建交易流水记录
     *
     * @param accountId      账户ID
     * @param type           交易类型
     * @param amount         交易金额
     * @param before         交易前余额
     * @param after          交易后余额
     * @param relatedId      对方账户ID
     * @param remark         备注
     * @return 交易流水记录
     */
    private BankTransactionLog createTransactionLog(Long accountId, Byte type,
                                                      BigDecimal amount, BigDecimal before,
                                                      BigDecimal after, Long relatedId, String remark) {
        BankTransactionLog log = new BankTransactionLog();
        log.setTransactionNo("TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase());
        log.setAccountId(accountId);
        log.setTransactionType(type);
        log.setAmount(amount);
        log.setBalanceBefore(before);
        log.setBalanceAfter(after);
        log.setRelatedAccountId(relatedId);
        log.setRemark(remark);
        log.setCreatedAt(LocalDateTime.now());
        return log;
    }
}
