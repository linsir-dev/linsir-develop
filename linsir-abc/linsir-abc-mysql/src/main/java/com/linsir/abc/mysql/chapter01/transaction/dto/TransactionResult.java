package com.linsir.abc.mysql.chapter01.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 事务结果DTO
 *
 * <p>用于返回事务操作的结果</p>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResult {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 业务单号
     * <p>转账单号或兑换单号</p>
     */
    private String businessNo;

    /**
     * 结果消息
     */
    private String message;

    /**
     * 响应时间
     */
    private LocalDateTime responseTime;

    /**
     * 创建成功结果
     *
     * @param businessNo 业务单号
     * @return 成功结果
     */
    public static TransactionResult success(String businessNo) {
        return new TransactionResult(true, businessNo, "操作成功", LocalDateTime.now());
    }

    /**
     * 创建成功结果
     *
     * @param businessNo 业务单号
     * @param message    成功消息
     * @return 成功结果
     */
    public static TransactionResult success(String businessNo, String message) {
        return new TransactionResult(true, businessNo, message, LocalDateTime.now());
    }

    /**
     * 创建失败结果
     *
     * @param message 错误消息
     * @return 失败结果
     */
    public static TransactionResult fail(String message) {
        return new TransactionResult(false, null, message, LocalDateTime.now());
    }

    /**
     * 创建失败结果
     *
     * @param businessNo 业务单号
     * @param message    错误消息
     * @return 失败结果
     */
    public static TransactionResult fail(String businessNo, String message) {
        return new TransactionResult(false, businessNo, message, LocalDateTime.now());
    }

    /**
     * 是否为成功结果
     *
     * @return true-成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 是否为失败结果
     *
     * @return true-失败
     */
    public boolean isFailed() {
        return !success;
    }
}
