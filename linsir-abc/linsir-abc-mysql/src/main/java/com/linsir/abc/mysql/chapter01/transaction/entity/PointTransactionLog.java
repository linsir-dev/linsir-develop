package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 积分交易流水实体类
 * 
 * <p>记录积分账户的所有交易操作，用于审计和追溯</p>
 * 
 * <p>对应数据库表：point_transaction_logs</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class PointTransactionLog {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 交易流水号
     * <p>唯一标识一笔积分交易</p>
     */
    private String transactionNo;
    
    /**
     * 积分账户ID
     */
    private Long pointAccountId;
    
    /**
     * 交易类型
     * <p>1-获得，2-消费，3-冻结，4-解冻，5-过期</p>
     */
    private Byte transactionType;
    
    /**
     * 积分数量
     * <p>正数表示增加，负数表示减少</p>
     */
    private Long points;
    
    /**
     * 交易前积分
     */
    private Long balanceBefore;
    
    /**
     * 交易后积分
     */
    private Long balanceAfter;
    
    /**
     * 来源类型
     * <p>ORDER-订单，EXCHANGE-兑换，ACTIVITY-活动，REGISTER-注册</p>
     */
    private String sourceType;
    
    /**
     * 来源ID
     * <p>关联的业务ID</p>
     */
    private Long sourceId;
    
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
     * 交易类型：获得积分
     */
    public static final byte TYPE_EARN = 1;
    
    /**
     * 交易类型：消费积分
     */
    public static final byte TYPE_CONSUME = 2;
    
    /**
     * 交易类型：冻结积分
     */
    public static final byte TYPE_FREEZE = 3;
    
    /**
     * 交易类型：解冻积分
     */
    public static final byte TYPE_UNFREEZE = 4;
    
    /**
     * 交易类型：积分过期
     */
    public static final byte TYPE_EXPIRE = 5;
    
    // 来源类型常量
    
    /**
     * 来源类型：订单
     */
    public static final String SOURCE_ORDER = "ORDER";
    
    /**
     * 来源类型：兑换
     */
    public static final String SOURCE_EXCHANGE = "EXCHANGE";
    
    /**
     * 来源类型：活动
     */
    public static final String SOURCE_ACTIVITY = "ACTIVITY";
    
    /**
     * 来源类型：注册
     */
    public static final String SOURCE_REGISTER = "REGISTER";
    
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
            case TYPE_EARN -> "获得积分";
            case TYPE_CONSUME -> "消费积分";
            case TYPE_FREEZE -> "冻结积分";
            case TYPE_UNFREEZE -> "解冻积分";
            case TYPE_EXPIRE -> "积分过期";
            default -> "未知";
        };
    }
    
    /**
     * 是否为收入交易
     * 
     * @return true-收入
     */
    public boolean isIncome() {
        return points != null && points > 0;
    }
    
    /**
     * 是否为支出交易
     * 
     * @return true-支出
     */
    public boolean isExpense() {
        return points != null && points < 0;
    }
}
