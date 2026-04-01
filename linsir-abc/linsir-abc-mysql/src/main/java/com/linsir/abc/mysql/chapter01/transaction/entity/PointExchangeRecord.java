package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 积分兑换记录实体类
 * 
 * <p>记录用户积分兑换商品的完整信息</p>
 * 
 * <p>对应数据库表：point_exchange_records</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class PointExchangeRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 兑换单号
     * <p>唯一标识一笔兑换记录，格式如：EXC202403010001</p>
     */
    private String exchangeNo;
    
    /**
     * 积分账户ID
     */
    private Long pointAccountId;
    
    /**
     * 商品ID
     */
    private Long productId;
    
    /**
     * 兑换数量
     */
    private Integer quantity;
    
    /**
     * 总积分
     * <p>此次兑换消耗的总积分</p>
     */
    private Long totalPoints;
    
    /**
     * 兑换状态
     * <p>0-处理中，1-成功，2-失败，3-已取消</p>
     */
    private Integer status;
    
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
     * 状态：已取消
     */
    public static final int STATUS_CANCELLED = 3;
    
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
            case STATUS_CANCELLED -> "已取消";
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
     * 是否为已取消状态
     * 
     * @return true-已取消
     */
    public boolean isCancelled() {
        return status != null && status == STATUS_CANCELLED;
    }
    
    /**
     * 标记为成功
     */
    public void markAsSuccess() {
        this.status = STATUS_SUCCESS;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为失败
     */
    public void markAsFailed() {
        this.status = STATUS_FAILED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为已取消
     */
    public void markAsCancelled() {
        this.status = STATUS_CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
}
