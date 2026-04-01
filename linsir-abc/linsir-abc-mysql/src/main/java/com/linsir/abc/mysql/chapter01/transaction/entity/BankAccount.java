package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 银行账户实体类
 * 
 * <p>用于演示事务隔离级别和并发控制，包含账户基本信息、余额、冻结金额等字段</p>
 * 
 * <p>对应数据库表：bank_accounts</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class BankAccount {
    
    /**
     * 主键ID
     * <p>数据库自增主键</p>
     */
    private Long id;
    
    /**
     * 账户编号
     * <p>唯一标识一个银行账户，格式如：BK001</p>
     */
    private String accountNo;
    
    /**
     * 账户名称
     * <p>账户持有人的名称</p>
     */
    private String accountName;
    
    /**
     * 账户余额
     * <p>当前账户的总金额，包含冻结金额</p>
     */
    private BigDecimal balance;
    
    /**
     * 冻结金额
     * <p>被冻结不能使用的金额，如转账中的金额</p>
     */
    private BigDecimal frozenAmount;
    
    /**
     * 银行代码
     * <p>银行唯一代码，如：ICBC（工商银行）、CCB（建设银行）</p>
     */
    private String bankCode;
    
    /**
     * 银行名称
     * <p>银行的完整名称</p>
     */
    private String bankName;
    
    /**
     * 账户状态
     * <p>0-冻结，1-正常</p>
     */
    private Integer status;
    
    /**
     * 乐观锁版本号
     * <p>用于并发控制，防止更新丢失</p>
     */
    private Integer version;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 状态常量：账户冻结
     */
    public static final int STATUS_FROZEN = 0;
    
    /**
     * 状态常量：账户正常
     */
    public static final int STATUS_NORMAL = 1;
    
    /**
     * 获取可用余额
     * <p>可用余额 = 总余额 - 冻结金额</p>
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
     * 检查账户是否可用
     * 
     * @return true-账户正常可用，false-账户被冻结
     */
    public boolean isAvailable() {
        return status != null && status == STATUS_NORMAL;
    }
    
    /**
     * 检查余额是否充足
     * 
     * @param amount 需要检查的金额
     * @return true-余额充足，false-余额不足
     */
    public boolean hasSufficientBalance(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        return getAvailableBalance().compareTo(amount) >= 0;
    }
}
