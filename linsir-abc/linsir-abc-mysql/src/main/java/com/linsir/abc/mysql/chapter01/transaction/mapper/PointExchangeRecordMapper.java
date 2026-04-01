package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.PointExchangeRecord;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 积分兑换记录Mapper接口
 * 
 * <p>提供积分兑换记录的CRUD操作</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface PointExchangeRecordMapper {
    
    /**
     * 根据ID查询积分兑换记录
     * 
     * @param id 记录ID
     * @return 积分兑换记录
     */
    @Select("SELECT * FROM point_exchange_records WHERE id = #{id}")
    PointExchangeRecord selectById(Long id);
    
    /**
     * 根据兑换单号查询
     * 
     * @param exchangeNo 兑换单号
     * @return 积分兑换记录
     */
    @Select("SELECT * FROM point_exchange_records WHERE exchange_no = #{exchangeNo}")
    PointExchangeRecord selectByExchangeNo(String exchangeNo);
    
    /**
     * 根据积分账户ID查询
     * 
     * @param pointAccountId 积分账户ID
     * @return 积分兑换记录列表
     */
    @Select("SELECT * FROM point_exchange_records WHERE point_account_id = #{pointAccountId} ORDER BY created_at DESC")
    List<PointExchangeRecord> selectByPointAccountId(Long pointAccountId);
    
    /**
     * 根据商品ID查询
     * 
     * @param productId 商品ID
     * @return 积分兑换记录列表
     */
    @Select("SELECT * FROM point_exchange_records WHERE product_id = #{productId} ORDER BY created_at DESC")
    List<PointExchangeRecord> selectByProductId(Long productId);
    
    /**
     * 根据状态查询
     * 
     * @param status 状态
     * @return 积分兑换记录列表
     */
    @Select("SELECT * FROM point_exchange_records WHERE status = #{status} ORDER BY created_at DESC")
    List<PointExchangeRecord> selectByStatus(Integer status);
    
    /**
     * 插入积分兑换记录
     * 
     * @param record 积分兑换记录
     * @return 影响行数
     */
    @Insert("INSERT INTO point_exchange_records (exchange_no, point_account_id, product_id, " +
            "quantity, total_points, status, created_at) " +
            "VALUES (#{exchangeNo}, #{pointAccountId}, #{productId}, " +
            "#{quantity}, #{totalPoints}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointExchangeRecord record);
    
    /**
     * 更新兑换状态
     * 
     * @param exchangeNo 兑换单号
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE point_exchange_records SET status = #{status}, completed_at = NOW() WHERE exchange_no = #{exchangeNo}")
    int updateStatus(@Param("exchangeNo") String exchangeNo, @Param("status") Integer status);
    
    /**
     * 更新积分兑换记录
     * 
     * @param record 积分兑换记录
     * @return 影响行数
     */
    @Update("UPDATE point_exchange_records SET status = #{status}, completed_at = #{completedAt} WHERE id = #{id}")
    int update(PointExchangeRecord record);
}
