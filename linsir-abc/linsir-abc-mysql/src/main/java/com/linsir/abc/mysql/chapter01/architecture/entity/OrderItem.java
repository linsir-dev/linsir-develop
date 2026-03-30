package com.linsir.abc.mysql.chapter01.architecture.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细实体类
 * 对应数据库表：order_items
 * 用于存储订单商品明细，支持JOIN查询演示
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * 明细ID
     * 主键，自增
     */
    private Long id;

    /**
     * 订单ID
     * 关联orders表
     */
    private Long orderId;

    /**
     * 商品ID
     * 关联products表
     */
    private Long productId;

    /**
     * 商品名称
     * 冗余存储，避免JOIN查询
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 小计金额
     * = 单价 × 数量
     */
    private BigDecimal subtotal;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 计算小计金额
     */
    public void calculateSubtotal() {
        if (productPrice != null && quantity != null) {
            this.subtotal = productPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
