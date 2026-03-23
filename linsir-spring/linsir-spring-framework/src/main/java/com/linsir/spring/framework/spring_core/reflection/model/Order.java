package com.linsir.spring.framework.spring_core.reflection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 用于反射工具示例中的数据模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单状态
     */
    private OrderStatus status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING,    // 待支付
        PAID,       // 已支付
        SHIPPED,    // 已发货
        COMPLETED,  // 已完成
        CANCELLED   // 已取消
    }

    /**
     * 内部类：订单项
     * 用于测试内部类的反射操作
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private Long itemId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderNo='" + orderNo + '\'' +
                ", userId=" + userId +
                ", amount=" + amount +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
