package com.linsir.abc.mysql.chapter01.concurrency.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 账户实体类
 * 对应数据库表：accounts
 * 用于演示转账、充值等并发场景
 *
 * <p>核心字段说明：</p>
 * <ul>
 *   <li>balance - 账户余额，支持高精度计算</li>
 *   <li>frozen_amount - 冻结金额，用于预占资金场景</li>
 *   <li>version - 乐观锁版本号，用于并发控制</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    /**
     * 账户ID
     * 主键，自增
     */
    private Long id;

    /**
     * 账户编号
     * 唯一标识，业务使用
     */
    private String accountNo;

    /**
     * 账户名称
     * 用于显示和识别
     */
    private String accountName;

    /**
     * 账户余额
     * 当前可用余额，不包含冻结金额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     * 已锁定但未实际扣减的金额
     */
    private BigDecimal frozenAmount;

    /**
     * 乐观锁版本号
     * 用于乐观锁并发控制，每次更新自动递增
     */
    private Integer version;

    /**
     * 状态
     * 0-冻结，1-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 获取可用余额
     * 可用余额 = 总余额 - 冻结金额
     *
     * @return 可用余额
     */
    public BigDecimal getAvailableBalance() {
        if (balance == null) {
            return BigDecimal.ZERO;
        }
        if (frozenAmount == null) {
            return balance;
        }
        return balance.subtract(frozenAmount);
    }

    /**
     * 检查余额是否充足
     *
     * @param amount 需要检查的金额
     * @return 余额是否充足
     */
    public boolean hasEnoughBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return getAvailableBalance().compareTo(amount) >= 0;
    }

    /**
     * 检查账户是否正常
     *
     * @return 账户是否正常
     */
    public boolean isActive() {
        return status != null && status == 1;
    }
}
