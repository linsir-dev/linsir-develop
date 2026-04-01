package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 转账记录实体类
 * 
 * <p>记录银行转账操作的完整信息，用于演示事务的原子性和一致性</p>
 * 
 * <p>对应数据库表：transfer_records</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class TransferRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 转账单号
     * <p>唯一标识一笔转账交易，格式如：TRF202403010001</p>
     */
    private String transferNo;
    
    /**
     * 转出账户ID
     */
    private Long fromAccountId;
    
    /**
     * 转入账户ID
     */
    private Long toAccountId;
    
    /**
     * 转账金额
     */
    private BigDecimal amount;
    
    /**
     * 手续费
     * <p>跨行转账可能产生的手续费</p>
     */
    private BigDecimal fee;
    
    /**
     * 转账状态
     * <p>0-处理中，1-成功，2-失败</p>
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    // 状态常量
    
    /**
     * 状态：处理中
     */
    public static final int STATUS_PROCESSING = 0;
    
    /**
     * 状态：成功
     */
    public static final int STATUS_SUCCESS = 1;
    
    /**
     * 状态：失败
     */
    public static final int STATUS_FAILED = 2;
    
    /**
     * 获取状态描述
     * 
     * @param status 状态代码
     * @return 状态描述
     */
    public static String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case STATUS_PROCESSING -> "处理中";
            case STATUS_SUCCESS -> "成功";
            case STATUS_FAILED -> "失败";
            default -> "未知";
        };
    }
    
    /**
     * 是否为处理中状态
     * 
     * @return true-处理中
     */
    public boolean isProcessing() {
        return status != null && status == STATUS_PROCESSING;
    }
    
    /**
     * 是否为成功状态
     * 
     * @return true-成功
     */
    public boolean isSuccess() {
        return status != null && status == STATUS_SUCCESS;
    }
    
    /**
     * 是否为失败状态
     * 
     * @return true-失败
     */
    public boolean isFailed() {
        return status != null && status == STATUS_FAILED;
    }
    
    /**
     * 获取实际到账金额
     * 
     * @return 实际到账金额（转账金额 - 手续费）
     */
    public BigDecimal getActualAmount() {
        if (amount == null) {
            return BigDecimal.ZERO;
        }
        if (fee == null) {
            return amount;
        }
        return amount.subtract(fee);
    }
}
