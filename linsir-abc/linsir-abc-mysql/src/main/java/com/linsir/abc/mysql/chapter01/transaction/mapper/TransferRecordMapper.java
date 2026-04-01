package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.TransferRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 转账记录Mapper接口
 * 
 * <p>提供转账记录的CRUD操作</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface TransferRecordMapper {
    
    /**
     * 根据ID查询转账记录
     * 
     * @param id 记录ID
     * @return 转账记录
     */
    @Select("SELECT * FROM transfer_records WHERE id = #{id}")
    TransferRecord selectById(Long id);
    
    /**
     * 根据转账单号查询
     * 
     * @param transferNo 转账单号
     * @return 转账记录
     */
    @Select("SELECT * FROM transfer_records WHERE transfer_no = #{transferNo}")
    TransferRecord selectByTransferNo(String transferNo);
    
    /**
     * 根据转出账户ID查询
     * 
     * @param fromAccountId 转出账户ID
     * @return 转账记录列表
     */
    @Select("SELECT * FROM transfer_records WHERE from_account_id = #{fromAccountId} ORDER BY created_at DESC")
    List<TransferRecord> selectByFromAccountId(Long fromAccountId);
    
    /**
     * 根据转入账户ID查询
     * 
     * @param toAccountId 转入账户ID
     * @return 转账记录列表
     */
    @Select("SELECT * FROM transfer_records WHERE to_account_id = #{toAccountId} ORDER BY created_at DESC")
    List<TransferRecord> selectByToAccountId(Long toAccountId);
    
    /**
     * 根据状态查询转账记录
     * 
     * @param status 状态
     * @return 转账记录列表
     */
    @Select("SELECT * FROM transfer_records WHERE status = #{status} ORDER BY created_at DESC")
    List<TransferRecord> selectByStatus(Integer status);
    
    /**
     * 插入转账记录
     * 
     * @param record 转账记录
     * @return 影响行数
     */
    @Insert("INSERT INTO transfer_records (transfer_no, from_account_id, to_account_id, " +
            "amount, fee, status, remark, created_at) " +
            "VALUES (#{transferNo}, #{fromAccountId}, #{toAccountId}, " +
            "#{amount}, #{fee}, #{status}, #{remark}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TransferRecord record);
    
    /**
     * 更新转账状态
     * 
     * @param transferNo 转账单号
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE transfer_records SET status = #{status}, completed_at = NOW() WHERE transfer_no = #{transferNo}")
    int updateStatus(@Param("transferNo") String transferNo, @Param("status") Integer status);
    
    /**
     * 更新转账记录
     * 
     * @param record 转账记录
     * @return 影响行数
     */
    @Update("UPDATE transfer_records SET status = #{status}, remark = #{remark}, " +
            "completed_at = #{completedAt} WHERE id = #{id}")
    int update(TransferRecord record);
}
