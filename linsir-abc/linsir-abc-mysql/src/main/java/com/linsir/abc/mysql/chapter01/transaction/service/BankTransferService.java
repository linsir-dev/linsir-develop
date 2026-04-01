package com.linsir.abc.mysql.chapter01.transaction.service;

import com.linsir.abc.mysql.chapter01.transaction.dto.TransactionResult;
import com.linsir.abc.mysql.chapter01.transaction.dto.TransferRequest;
import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import com.linsir.abc.mysql.chapter01.transaction.entity.TransferRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * 银行转账服务接口
 *
 * <p>用于演示事务的ACID特性</p>
 *
 * @author linsir
 * @since 1.0.0
 */
public interface BankTransferService {

    /**
     * 执行转账
     * <p>演示事务的原子性：转账操作要么全部成功，要么全部失败</p>
     *
     * @param request 转账请求
     * @return 事务结果
     */
    TransactionResult transfer(TransferRequest request);

    /**
     * 跨行转账
     * <p>演示分布式事务场景</p>
     *
     * @param fromAccountNo 转出账户
     * @param toAccountNo   转入账户
     * @param amount        金额
     * @return 事务结果
     */
    TransactionResult crossBankTransfer(String fromAccountNo, String toAccountNo, BigDecimal amount);

    /**
     * 批量转账
     * <p>演示长事务的处理</p>
     *
     * @param requests 转账请求列表
     * @return 事务结果列表
     */
    List<TransactionResult> batchTransfer(List<TransferRequest> requests);

    /**
     * 查询账户余额
     * <p>用于演示不同隔离级别下的读取结果</p>
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    BankAccount getAccount(String accountNo);

    /**
     * 查询转账记录
     *
     * @param transferNo 转账单号
     * @return 转账记录
     */
    TransferRecord getTransferRecord(String transferNo);

    /**
     * 查询账户的所有转账记录
     *
     * @param accountNo 账户编号
     * @return 转账记录列表
     */
    List<TransferRecord> getTransferRecordsByAccount(String accountNo);
}
