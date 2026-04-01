package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.BankTransactionLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 银行交易流水Mapper接口
 * 
 * <p>提供银行交易流水的CRUD操作</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface BankTransactionLogMapper {
    
    /**
     * 根据ID查询交易流水
     * 
     * @param id 流水ID
     * @return 交易流水信息
     */
    @Select("SELECT * FROM bank_transaction_logs WHERE id = #{id}")
    BankTransactionLog selectById(Long id);
    
    /**
     * 根据交易流水号查询
     * 
     * @param transactionNo 交易流水号
     * @return 交易流水信息
     */
    @Select("SELECT * FROM bank_transaction_logs WHERE transaction_no = #{transactionNo}")
    BankTransactionLog selectByTransactionNo(String transactionNo);
    
    /**
     * 根据账户ID查询交易流水列表
     * 
     * @param accountId 账户ID
     * @return 交易流水列表
     */
    @Select("SELECT * FROM bank_transaction_logs WHERE account_id = #{accountId} ORDER BY created_at DESC")
    List<BankTransactionLog> selectByAccountId(Long accountId);
    
    /**
     * 根据账户ID和交易类型查询
     * 
     * @param accountId 账户ID
     * @param transactionType 交易类型
     * @return 交易流水列表
     */
    @Select("SELECT * FROM bank_transaction_logs WHERE account_id = #{accountId} " +
            "AND transaction_type = #{transactionType} ORDER BY created_at DESC")
    List<BankTransactionLog> selectByAccountIdAndType(@Param("accountId") Long accountId, 
                                                       @Param("transactionType") Byte transactionType);
    
    /**
     * 插入交易流水
     * 
     * @param log 交易流水信息
     * @return 影响行数
     */
    @Insert("INSERT INTO bank_transaction_logs (transaction_no, account_id, transaction_type, " +
            "amount, balance_before, balance_after, related_account_id, remark, created_at) " +
            "VALUES (#{transactionNo}, #{accountId}, #{transactionType}, " +
            "#{amount}, #{balanceBefore}, #{balanceAfter}, #{relatedAccountId}, #{remark}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BankTransactionLog log);
    
    /**
     * 批量插入交易流水
     * 
     * @param logs 交易流水列表
     * @return 影响行数
     */
    int batchInsert(@Param("logs") List<BankTransactionLog> logs);
}
