package com.linsir.abc.mysql.chapter01.concurrency.mapper;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Account;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 账户Mapper接口
 * 演示悲观锁、乐观锁、行锁等并发控制机制
 *
 * <p>锁机制说明：</p>
 * <ul>
 *   <li>selectById - 无锁查询，可能读到脏数据</li>
 *   <li>selectByIdForUpdate - 悲观锁（排他锁），阻塞其他事务读写</li>
 *   <li>selectByIdLockInShareMode - 共享锁，允许其他事务读，阻塞写</li>
 *   <li>updateBalanceWithVersion - 乐观锁，通过版本号控制并发</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface AccountMapper {

    /**
     * 根据ID查询账户（无锁）
     * 普通查询，不使用任何锁
     *
     * @param id 账户ID
     * @return 账户信息
     */
    @Select("SELECT * FROM accounts WHERE id = #{id}")
    Account selectById(Long id);

    /**
     * 根据ID查询账户（悲观锁 - FOR UPDATE）
     * 使用行级排他锁，阻塞其他事务的读写操作
     * 适用于需要强一致性的场景，如转账
     *
     * @param id 账户ID
     * @return 账户信息
     */
    @Select("SELECT * FROM accounts WHERE id = #{id} FOR UPDATE")
    Account selectByIdForUpdate(Long id);

    /**
     * 根据ID查询账户（共享锁 - LOCK IN SHARE MODE）
     * 使用行级共享锁，允许其他事务读取，阻塞写入
     * 适用于读取时需要防止数据被修改的场景
     *
     * @param id 账户ID
     * @return 账户信息
     */
    @Select("SELECT * FROM accounts WHERE id = #{id} LOCK IN SHARE MODE")
    Account selectByIdLockInShareMode(Long id);

    /**
     * 根据账户编号查询账户
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @Select("SELECT * FROM accounts WHERE account_no = #{accountNo}")
    Account selectByAccountNo(String accountNo);

    /**
     * 根据账户编号查询账户（悲观锁）
     *
     * @param accountNo 账户编号
     * @return 账户信息
     */
    @Select("SELECT * FROM accounts WHERE account_no = #{accountNo} FOR UPDATE")
    Account selectByAccountNoForUpdate(String accountNo);

    /**
     * 查询所有正常状态的账户
     *
     * @return 账户列表
     */
    @Select("SELECT * FROM accounts WHERE status = 1 ORDER BY id")
    List<Account> selectAll();

    /**
     * 插入账户
     *
     * @param account 账户信息
     * @return 影响行数
     */
    @Insert("INSERT INTO accounts (account_no, account_name, balance, frozen_amount, version, status) " +
            "VALUES (#{accountNo}, #{accountName}, #{balance}, #{frozenAmount}, #{version}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Account account);

    /**
     * 更新账户余额（悲观锁方式）
     * 直接更新，依赖外部锁保证一致性
     *
     * @param id     账户ID
     * @param amount 变动金额（正数增加，负数减少）
     * @return 影响行数
     */
    @Update("UPDATE accounts SET balance = balance + #{amount}, updated_at = NOW() WHERE id = #{id}")
    int updateBalance(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 更新账户余额（乐观锁方式）
     * 通过版本号控制并发，更新时检查版本号是否匹配
     * 如果版本号不匹配，说明数据已被其他事务修改，更新失败
     *
     * @param id      账户ID
     * @param amount  变动金额
     * @param version 当前版本号
     * @return 影响行数，0表示版本冲突
     */
    @Update("UPDATE accounts SET balance = balance + #{amount}, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int updateBalanceWithVersion(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("version") Integer version);

    /**
     * 冻结金额
     * 将余额转为冻结金额
     *
     * @param id     账户ID
     * @param amount 冻结金额
     * @return 影响行数
     */
    @Update("UPDATE accounts SET balance = balance - #{amount}, frozen_amount = frozen_amount + #{amount}, updated_at = NOW() " +
            "WHERE id = #{id} AND balance >= #{amount}")
    int freezeAmount(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 解冻金额
     * 将冻结金额转回余额
     *
     * @param id     账户ID
     * @param amount 解冻金额
     * @return 影响行数
     */
    @Update("UPDATE accounts SET balance = balance + #{amount}, frozen_amount = frozen_amount - #{amount}, updated_at = NOW() " +
            "WHERE id = #{id} AND frozen_amount >= #{amount}")
    int unfreezeAmount(@Param("id") Long id, @Param("amount") BigDecimal amount);

    /**
     * 更新账户状态
     *
     * @param id     账户ID
     * @param status 状态
     * @return 影响行数
     */
    @Update("UPDATE accounts SET status = #{status}, updated_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 删除账户
     *
     * @param id 账户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM accounts WHERE id = #{id}")
    int deleteById(Long id);
}
