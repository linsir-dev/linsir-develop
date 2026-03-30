package com.linsir.abc.mysql.chapter01.architecture.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单实体类
 * 对应数据库表：orders
 * 用于存储订单信息，支持复杂查询和事务演示
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * 订单ID
     * 主键，自增
     */
    private Long id;

    /**
     * 订单编号
     * 唯一标识，业务使用
     */
    private String orderNo;

    /**
     * 用户ID
     * 关联users表
     */
    private Long userId;

    /**
     * 订单总金额
     * 原始金额，未优惠前
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     * 实际支付金额 = 总金额 - 优惠金额
     */
    private BigDecimal payAmount;

    /**
     * 状态
     * 0-待支付，1-已支付，2-已发货，3-已完成，4-已取消
     */
    private Integer status;

    /**
     * 支付方式
     * 1-支付宝，2-微信，3-银行卡
     */
    private Integer payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 完成时间
     */
    private LocalDateTime completeTime;

    /**
     * 订单备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 订单明细列表
     * 非数据库字段，关联order_items表
     */
    private List<OrderItem> items;

    /**
     * 计算订单状态描述
     *
     * @return 状态描述
     */
    public String getStatusDesc() {
        return switch (status) {
            case 0 -> "待支付";
            case 1 -> "已支付";
            case 2 -> "已发货";
            case 3 -> "已完成";
            case 4 -> "已取消";
            default -> "未知";
        };
    }

    /**
     * 检查订单是否可支付
     *
     * @return true-可支付
     */
    public boolean isPayable() {
        return status != null && status == 0;
    }

    /**
     * 检查订单是否可取消
     *
     * @return true-可取消
     */
    public boolean isCancellable() {
        return status != null && (status == 0 || status == 1);
    }

    /**
     * 计算实付金额
     * 根据总金额和优惠金额计算
     */
    public void calculatePayAmount() {
        if (totalAmount != null && discountAmount != null) {
            this.payAmount = totalAmount.subtract(discountAmount);
        }
    }
}
