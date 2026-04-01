package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.BankAccount;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 银行账户Mapper接口
 * 
 * <p>提供银行账户的CRUD操作和并发控制方法</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface BankAccountMapper {
    
    /**
     * 根据ID查询账户
     * 
     * @param id 账户ID
     * @return 账户信息
     */
    @Select("SELECT * FROM bank_accounts WHERE id = #{id}")
    BankAccount selectById(Long id);
    
    /**
     * 根据ID查询账户（加锁）
     * <p>使用SELECT FOR UPDATE实现悲观锁</p>
     * 
     * @param id 账户ID
     * @return 账户信息
     */
    @Select("SELECT * FROM bank_accounts WHERE id = #{id} FOR UPDATE")
    BankAccount selectByIdForUpdate(Long id);
    
    /**
     * 根据账户编号查询
     * 
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @Select("SELECT * FROM bank_accounts WHERE account_no = #{accountNo}")
    BankAccount selectByAccountNo(String accountNo);
    
    /**
     * 根据账户编号查询（加锁）
     * <p>使用SELECT FOR UPDATE实现悲观锁</p>
     * 
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @Select("SELECT * FROM bank_accounts WHERE account_no = #{accountNo} FOR UPDATE")
    BankAccount selectByAccountNoForUpdate(String accountNo);
    
    /**
     * 查询所有正常状态的账户
     * 
     * @return 账户列表
     */
    @Select("SELECT * FROM bank_accounts WHERE status = 1")
    List<BankAccount> selectAll();
    
    /**
     * 根据银行代码查询账户列表
     * <p>用于演示幻读问题</p>
     * 
     * @param bankCode 银行代码
     * @return 账户列表
     */
    @Select("SELECT * FROM bank_accounts WHERE bank_code = #{bankCode}")
    List<BankAccount> selectByBankCode(String bankCode);
    
    /**
     * 根据银行代码查询账户列表（加锁）
     * 
     * @param bankCode 银行代码
     * @return 账户列表
     */
    @Select("SELECT * FROM bank_accounts WHERE bank_code = #{bankCode} FOR UPDATE")
    List<BankAccount> selectByBankCodeForUpdate(String bankCode);
    
    /**
     * 更新余额（乐观锁）
     * <p>通过版本号实现乐观锁，防止并发更新问题</p>
     * 
     * @param id 账户ID
     * @param amount 变动金额（正数增加，负数减少）
     * @param version 当前版本号
     * @return 影响行数，0表示更新失败（版本冲突）
     */
    @Update("UPDATE bank_accounts SET " +
            "balance = balance + #{amount}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int updateBalance(@Param("id") Long id, 
                      @Param("amount") BigDecimal amount, 
                      @Param("version") Integer version);
    
    /**
     * 冻结金额
     * <p>将可用余额转为冻结金额</p>
     * 
     * @param id 账户ID
     * @param amount 冻结金额
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE bank_accounts SET " +
            "frozen_amount = frozen_amount + #{amount}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND balance - frozen_amount >= #{amount}")
    int freezeAmount(@Param("id") Long id, 
                     @Param("amount") BigDecimal amount, 
                     @Param("version") Integer version);
    
    /**
     * 解冻金额
     * <p>将冻结金额转回可用余额</p>
     * 
     * @param id 账户ID
     * @param amount 解冻金额
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE bank_accounts SET " +
            "frozen_amount = frozen_amount - #{amount}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND frozen_amount >= #{amount}")
    int unfreezeAmount(@Param("id") Long id, 
                       @Param("amount") BigDecimal amount, 
                       @Param("version") Integer version);
    
    /**
     * 插入账户
     * 
     * @param account 账户信息
     * @return 影响行数
     */
    @Insert("INSERT INTO bank_accounts (account_no, account_name, balance, " +
            "frozen_amount, bank_code, bank_name, status, version) " +
            "VALUES (#{accountNo}, #{accountName}, #{balance}, " +
            "#{frozenAmount}, #{bankCode}, #{bankName}, #{status}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BankAccount account);
    
    /**
     * 更新账户状态
     * 
     * @param id 账户ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE bank_accounts SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除账户
     * 
     * @param id 账户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM bank_accounts WHERE id = #{id}")
    int deleteById(Long id);
}
