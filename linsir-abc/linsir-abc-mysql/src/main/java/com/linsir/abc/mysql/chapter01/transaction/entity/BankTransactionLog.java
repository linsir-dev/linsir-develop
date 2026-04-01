package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 银行交易流水实体类
 * 
 * <p>记录银行账户的所有交易操作，用于审计和追溯</p>
 * 
 * <p>对应数据库表：bank_transaction_logs</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class BankTransactionLog {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 交易流水号
     * <p>唯一标识一笔交易，格式如：TXN202403010001</p>
     */
    private String transactionNo;
    
    /**
     * 账户ID
     * <p>关联的银行账户ID</p>
     */
    private Long accountId;
    
    /**
     * 交易类型
     * <p>1-存款，2-取款，3-转账入，4-转账出，5-冻结，6-解冻</p>
     */
    private Byte transactionType;
    
    /**
     * 交易金额
     * <p>正数表示收入，负数表示支出</p>
     */
    private BigDecimal amount;
    
    /**
     * 交易前余额
     */
    private BigDecimal balanceBefore;
    
    /**
     * 交易后余额
     */
    private BigDecimal balanceAfter;
    
    /**
     * 对方账户ID
     * <p>转账交易时记录对方账户</p>
     */
    private Long relatedAccountId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    // 交易类型常量
    
    /**
     * 交易类型：存款
     */
    public static final byte TYPE_DEPOSIT = 1;
    
    /**
     * 交易类型：取款
     */
    public static final byte TYPE_WITHDRAW = 2;
    
    /**
     * 交易类型：转账入
     */
    public static final byte TYPE_TRANSFER_IN = 3;
    
    /**
     * 交易类型：转账出
     */
    public static final byte TYPE_TRANSFER_OUT = 4;
    
    /**
     * 交易类型：冻结
     */
    public static final byte TYPE_FREEZE = 5;
    
    /**
     * 交易类型：解冻
     */
    public static final byte TYPE_UNFREEZE = 6;
    
    /**
     * 获取交易类型描述
     * 
     * @param type 交易类型代码
     * @return 交易类型描述
     */
    public static String getTypeDescription(Byte type) {
        if (type == null) {
            return "未知";
        }
        return switch (type) {
            case TYPE_DEPOSIT -> "存款";
            case TYPE_WITHDRAW -> "取款";
            case TYPE_TRANSFER_IN -> "转账入";
            case TYPE_TRANSFER_OUT -> "转账出";
            case TYPE_FREEZE -> "冻结";
            case TYPE_UNFREEZE -> "解冻";
            default -> "未知";
        };
    }
    
    /**
     * 是否为收入交易
     * 
     * @return true-收入，false-支出
     */
    public boolean isIncome() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * 是否为支出交易
     * 
     * @return true-支出，false-收入
     */
    public boolean isExpense() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
}
