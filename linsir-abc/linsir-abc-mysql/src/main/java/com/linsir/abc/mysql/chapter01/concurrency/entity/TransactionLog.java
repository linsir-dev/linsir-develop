package com.linsir.abc.mysql.chapter01.concurrency.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交易流水实体类
 * 对应数据库表：transaction_logs
 * 记录所有资金变动历史，支持审计和追溯
 *
 * <p>核心字段说明：</p>
 * <ul>
 *   <li>transaction_type - 交易类型：1-充值，2-提现，3-转账入，4-转账出，5-冻结，6-解冻</li>
 *   <li>amount - 交易金额（正数表示增加，负数表示减少）</li>
 *   <li>balance_before - 交易前余额</li>
 *   <li>balance_after - 交易后余额</li>
 *   <li>related_account_id - 对方账户ID（转账时使用）</li>
 * </ul>
 *
 * <p>交易类型说明：</p>
 * <ul>
 *   <li>1-充值：账户余额增加</li>
 *   <li>2-提现：账户余额减少</li>
 *   <li>3-转账入：从其他账户转入</li>
 *   <li>4-转账出：转出到其他账户</li>
 *   <li>5-冻结：余额转为冻结金额</li>
 *   <li>6-解冻：冻结金额转回余额</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLog {

    /**
     * 流水ID
     * 主键，自增
     */
    private Long id;

    /**
     * 交易流水号
     * 唯一标识，用于查询和对账
     */
    private String transactionNo;

    /**
     * 账户ID
     * 发生交易的账户
     */
    private Long accountId;

    /**
     * 交易类型
     * 1-充值，2-提现，3-转账入，4-转账出，5-冻结，6-解冻
     */
    private Integer transactionType;

    /**
     * 交易金额
     * 正数表示增加，负数表示减少
     */
    private BigDecimal amount;

    /**
     * 交易前余额
     * 交易发生前的账户余额
     */
    private BigDecimal balanceBefore;

    /**
     * 交易后余额
     * 交易发生后的账户余额
     */
    private BigDecimal balanceAfter;

    /**
     * 对方账户ID
     * 转账时记录对方账户
     */
    private Long relatedAccountId;

    /**
     * 备注
     * 交易说明
     */
    private String remark;

    /**
     * 创建时间
     * 交易发生时间
     */
    private LocalDateTime createdAt;

    // ==================== 交易类型常量 ====================

    /**
     * 充值
     */
    public static final int TYPE_RECHARGE = 1;

    /**
     * 提现
     */
    public static final int TYPE_WITHDRAW = 2;

    /**
     * 转账入
     */
    public static final int TYPE_TRANSFER_IN = 3;

    /**
     * 转账出
     */
    public static final int TYPE_TRANSFER_OUT = 4;

    /**
     * 冻结
     */
    public static final int TYPE_FREEZE = 5;

    /**
     * 解冻
     */
    public static final int TYPE_UNFREEZE = 6;

    /**
     * 获取交易类型名称
     *
     * @return 交易类型名称
     */
    public String getTransactionTypeName() {
        if (transactionType == null) {
            return "未知";
        }
        switch (transactionType) {
            case TYPE_RECHARGE:
                return "充值";
            case TYPE_WITHDRAW:
                return "提现";
            case TYPE_TRANSFER_IN:
                return "转账入";
            case TYPE_TRANSFER_OUT:
                return "转账出";
            case TYPE_FREEZE:
                return "冻结";
            case TYPE_UNFREEZE:
                return "解冻";
            default:
                return "未知";
        }
    }

    /**
     * 检查交易是否成功
     * 通过比较余额变化验证
     *
     * @return 是否成功
     */
    public boolean isSuccessful() {
        if (balanceBefore == null || balanceAfter == null || amount == null) {
            return false;
        }
        BigDecimal expected = balanceBefore.add(amount);
        return balanceAfter.compareTo(expected) == 0;
    }
}
