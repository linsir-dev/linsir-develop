package com.linsir.spring.framework.spring_core.type_system.resolvable.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 用于演示泛型类型解析
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class Order {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 下单时间
     */
    private LocalDateTime createTime;

    public Order() {
    }

    public Order(Long id, String orderNo, BigDecimal amount, LocalDateTime createTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.amount = amount;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", amount=" + amount +
                ", createTime=" + createTime +
                '}';
    }
}
