package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.PointAccount;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 积分账户Mapper接口
 * 
 * <p>提供积分账户的CRUD操作</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface PointAccountMapper {
    
    /**
     * 根据ID查询积分账户
     * 
     * @param id 账户ID
     * @return 积分账户信息
     */
    @Select("SELECT * FROM point_accounts WHERE id = #{id}")
    PointAccount selectById(Long id);
    
    /**
     * 根据ID查询积分账户（加锁）
     * 
     * @param id 账户ID
     * @return 积分账户信息
     */
    @Select("SELECT * FROM point_accounts WHERE id = #{id} FOR UPDATE")
    PointAccount selectByIdForUpdate(Long id);
    
    /**
     * 根据用户ID查询积分账户
     * 
     * @param userId 用户ID
     * @return 积分账户信息
     */
    @Select("SELECT * FROM point_accounts WHERE user_id = #{userId}")
    PointAccount selectByUserId(Long userId);
    
    /**
     * 根据用户ID查询积分账户（加锁）
     * 
     * @param userId 用户ID
     * @return 积分账户信息
     */
    @Select("SELECT * FROM point_accounts WHERE user_id = #{userId} FOR UPDATE")
    PointAccount selectByUserIdForUpdate(Long userId);
    
    /**
     * 查询所有积分账户
     * 
     * @return 积分账户列表
     */
    @Select("SELECT * FROM point_accounts")
    List<PointAccount> selectAll();
    
    /**
     * 更新积分（乐观锁）
     * 
     * @param id 账户ID
     * @param points 变动积分（正数增加，负数减少）
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE point_accounts SET " +
            "available_points = available_points + #{points}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int updatePoints(@Param("id") Long id, 
                     @Param("points") Long points, 
                     @Param("version") Integer version);
    
    /**
     * 更新积分（带累计值）
     * <p>同时更新可用积分、累计获得或累计消费</p>
     * 
     * @param id 账户ID
     * @param availablePoints 可用积分变动
     * @param totalEarned 累计获得变动
     * @param totalConsumed 累计消费变动
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE point_accounts SET " +
            "available_points = available_points + #{availablePoints}, " +
            "total_earned = total_earned + #{totalEarned}, " +
            "total_consumed = total_consumed + #{totalConsumed}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int updatePointsWithTotal(@Param("id") Long id, 
                              @Param("availablePoints") Long availablePoints,
                              @Param("totalEarned") Long totalEarned,
                              @Param("totalConsumed") Long totalConsumed,
                              @Param("version") Integer version);
    
    /**
     * 冻结积分
     * 
     * @param id 账户ID
     * @param points 冻结积分数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE point_accounts SET " +
            "available_points = available_points - #{points}, " +
            "frozen_points = frozen_points + #{points}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND available_points >= #{points}")
    int freezePoints(@Param("id") Long id, 
                     @Param("points") Long points, 
                     @Param("version") Integer version);
    
    /**
     * 解冻积分
     * 
     * @param id 账户ID
     * @param points 解冻积分数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE point_accounts SET " +
            "available_points = available_points + #{points}, " +
            "frozen_points = frozen_points - #{points}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND frozen_points >= #{points}")
    int unfreezePoints(@Param("id") Long id, 
                       @Param("points") Long points, 
                       @Param("version") Integer version);
    
    /**
     * 扣减冻结积分（实际消费）
     * <p>兑换完成时调用，从冻结积分中扣减，同时增加累计消费</p>
     * 
     * @param id 账户ID
     * @param points 扣减积分数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE point_accounts SET " +
            "frozen_points = frozen_points - #{points}, " +
            "total_consumed = total_consumed + #{points}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND frozen_points >= #{points}")
    int deductFrozenPoints(@Param("id") Long id, 
                           @Param("points") Long points, 
                           @Param("version") Integer version);
    
    /**
     * 返还积分（取消兑换）
     * <p>取消兑换时调用，增加可用积分，减少累计消费</p>
     * 
     * @param id 账户ID
     * @param points 返还积分数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE point_accounts SET " +
            "available_points = available_points + #{points}, " +
            "total_consumed = total_consumed - #{points}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND total_consumed >= #{points}")
    int returnPoints(@Param("id") Long id, 
                     @Param("points") Long points, 
                     @Param("version") Integer version);
    
    /**
     * 插入积分账户
     * 
     * @param account 积分账户信息
     * @return 影响行数
     */
    @Insert("INSERT INTO point_accounts (user_id, available_points, frozen_points, " +
            "total_earned, total_consumed, version) " +
            "VALUES (#{userId}, #{availablePoints}, #{frozenPoints}, " +
            "#{totalEarned}, #{totalConsumed}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(PointAccount account);
    
    /**
     * 删除积分账户
     * 
     * @param id 账户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM point_accounts WHERE id = #{id}")
    int deleteById(Long id);
}
