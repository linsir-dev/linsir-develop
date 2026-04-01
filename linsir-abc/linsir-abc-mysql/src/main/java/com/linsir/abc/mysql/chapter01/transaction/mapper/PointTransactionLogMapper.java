package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.PointTransactionLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 积分交易流水Mapper接口
 * 
 * <p>提供积分交易流水的CRUD操作</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface PointTransactionLogMapper {
    
    /**
     * 根据ID查询积分交易流水
     * 
     * @param id 流水ID
     * @return 积分交易流水信息
     */
    @Select("SELECT * FROM point_transaction_logs WHERE id = #{id}")
    PointTransactionLog selectById(Long id);
    
    /**
     * 根据交易流水号查询
     * 
     * @param transactionNo 交易流水号
     * @return 积分交易流水信息
     */
    @Select("SELECT * FROM point_transaction_logs WHERE transaction_no = #{transactionNo}")
    PointTransactionLog selectByTransactionNo(String transactionNo);
    
    /**
     * 根据积分账户ID查询交易流水
     * 
     * @param pointAccountId 积分账户ID
     * @return 积分交易流水列表
     */
    @Select("SELECT * FROM point_transaction_logs WHERE point_account_id = #{pointAccountId} ORDER BY created_at DESC")
    List<PointTransactionLog> selectByPointAccountId(Long pointAccountId);
    
    /**
     * 根据积分账户ID和交易类型查询
     * 
     * @param pointAccountId 积分账户ID
     * @param transactionType 交易类型
     * @return 积分交易流水列表
     */
    @Select("SELECT * FROM point_transaction_logs WHERE point_account_id = #{pointAccountId} " +
            "AND transaction_type = #{transactionType} ORDER BY created_at DESC")
    List<PointTransactionLog> selectByPointAccountIdAndType(@Param("pointAccountId") Long pointAccountId, 
                                                             @Param("transactionType") Byte transactionType);
    
    /**
     * 根据来源查询交易流水
     * 
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @return 积分交易流水列表
     */
    @Select("SELECT * FROM point_transaction_logs WHERE source_type = #{sourceType} AND source_id = #{sourceId}")
    List<PointTransactionLog> selectBySource(@Param("sourceType") String sourceType, 
                                              @Param("sourceId") Long sourceId);
    
    /**
     * 插入积分交易流水
     * 
     * @param log 积分交易流水信息
     * @return 影响行数
     */
    @Insert("INSERT INTO point_transaction_logs (transaction_no, point_account_id, transaction_type, " +
            "points, balance_before, balance_after, source_type, source_id, remark, created_at) " +
            "VALUES (#{transactionNo}, #{pointAccountId}, #{transactionType}, " +
            "#{points}, #{balanceBefore}, #{balanceAfter}, #{sourceType}, #{sourceId}, #{remark}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointTransactionLog log);
    
    /**
     * 批量插入积分交易流水
     * 
     * @param logs 积分交易流水列表
     * @return 影响行数
     */
    int batchInsert(@Param("logs") List<PointTransactionLog> logs);
}
