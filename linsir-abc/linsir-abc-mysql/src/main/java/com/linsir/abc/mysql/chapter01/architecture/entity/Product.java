package com.linsir.abc.mysql.chapter01.architecture.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 * 对应数据库表：products
 * 用于存储商品信息
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    /**
     * 商品ID
     * 主键，自增
     */
    private Long id;

    /**
     * 商品编码
     * 唯一标识
     */
    private String productCode;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 分类ID
     */
    private Integer categoryId;

    /**
     * 售价
     */
    private BigDecimal price;

    /**
     * 成本价
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 状态
     * 0-下架，1-上架
     */
    private Integer status;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 检查商品是否上架
     *
     * @return true-上架
     */
    public boolean isOnSale() {
        return status != null && status == 1;
    }

    /**
     * 检查库存是否充足
     *
     * @param requiredQuantity 需要的数量
     * @return true-库存充足
     */
    public boolean hasEnoughStock(int requiredQuantity) {
        return stock != null && stock >= requiredQuantity;
    }

    /**
     * 扣减库存
     *
     * @param quantity 扣减数量
     * @return true-扣减成功
     */
    public boolean deductStock(int quantity) {
        if (hasEnoughStock(quantity)) {
            this.stock -= quantity;
            return true;
        }
        return false;
    }
}
