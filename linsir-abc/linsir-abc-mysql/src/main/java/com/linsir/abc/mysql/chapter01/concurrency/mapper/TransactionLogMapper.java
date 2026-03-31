package com.linsir.abc.mysql.chapter01.concurrency.mapper;

import com.linsir.abc.mysql.chapter01.concurrency.entity.TransactionLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 交易流水Mapper接口
 * 记录所有资金变动历史
 *
 * <p>设计原则：</p>
 * <ul>
 *   <li>不可变：交易记录一旦创建不可修改，保证审计追踪的可靠性</li>
 *   <li>完整性：记录交易前余额和交易后余额，便于对账</li>
 *   <li>关联性：转账场景记录对方账户ID</li>
 * </ul>
 *
 * <p>查询场景：</p>
 * <ul>
 *   <li>按账户查询：查询特定账户的所有交易</li>
 *   <li>按类型查询：查询特定类型的交易（如充值、转账）</li>
 *   <li>按时间查询：查询特定时间段的交易</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface TransactionLogMapper {

    /**
     * 根据ID查询交易流水
     *
     * @param id 流水ID
     * @return 交易流水信息
     */
    @Select("SELECT * FROM transaction_logs WHERE id = #{id}")
    TransactionLog selectById(Long id);

    /**
     * 根据交易流水号查询
     *
     * @param transactionNo 交易流水号
     * @return 交易流水信息
     */
    @Select("SELECT * FROM transaction_logs WHERE transaction_no = #{transactionNo}")
    TransactionLog selectByTransactionNo(String transactionNo);

    /**
     * 查询账户的所有交易流水
     *
     * @param accountId 账户ID
     * @return 交易流水列表
     */
    @Select("SELECT * FROM transaction_logs WHERE account_id = #{accountId} ORDER BY created_at DESC")
    List<TransactionLog> selectByAccountId(Long accountId);

    /**
     * 查询账户特定类型的交易流水
     *
     * @param accountId       账户ID
     * @param transactionType 交易类型
     * @return 交易流水列表
     */
    @Select("SELECT * FROM transaction_logs WHERE account_id = #{accountId} AND transaction_type = #{transactionType} ORDER BY created_at DESC")
    List<TransactionLog> selectByAccountIdAndType(@Param("accountId") Long accountId, @Param("transactionType") Integer transactionType);

    /**
     * 查询特定类型的所有交易流水
     *
     * @param transactionType 交易类型
     * @return 交易流水列表
     */
    @Select("SELECT * FROM transaction_logs WHERE transaction_type = #{transactionType} ORDER BY created_at DESC")
    List<TransactionLog> selectByTransactionType(Integer transactionType);

    /**
     * 查询所有交易流水
     *
     * @return 交易流水列表
     */
    @Select("SELECT * FROM transaction_logs ORDER BY created_at DESC")
    List<TransactionLog> selectAll();

    /**
     * 插入交易流水记录
     * 交易记录一旦创建不可修改
     *
     * @param transactionLog 交易流水信息
     * @return 影响行数
     */
    @Insert("INSERT INTO transaction_logs (transaction_no, account_id, transaction_type, amount, " +
            "balance_before, balance_after, related_account_id, remark, created_at) " +
            "VALUES (#{transactionNo}, #{accountId}, #{transactionType}, #{amount}, " +
            "#{balanceBefore}, #{balanceAfter}, #{relatedAccountId}, #{remark}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TransactionLog transactionLog);

    /**
     * 根据账户ID删除交易流水
     * 通常用于清理测试数据
     *
     * @param accountId 账户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM transaction_logs WHERE account_id = #{accountId}")
    int deleteByAccountId(Long accountId);

    /**
     * 根据ID删除交易流水
     *
     * @param id 流水ID
     * @return 影响行数
     */
    @Delete("DELETE FROM transaction_logs WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计账户交易数量
     *
     * @param accountId 账户ID
     * @return 交易数量
     */
    @Select("SELECT COUNT(*) FROM transaction_logs WHERE account_id = #{accountId}")
    Long countByAccountId(Long accountId);

    /**
     * 统计特定类型的交易数量
     *
     * @param transactionType 交易类型
     * @return 交易数量
     */
    @Select("SELECT COUNT(*) FROM transaction_logs WHERE transaction_type = #{transactionType}")
    Long countByTransactionType(Integer transactionType);
}
