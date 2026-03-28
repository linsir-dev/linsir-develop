package com.linsir.abc.core.jvm.compile.test;

import com.linsir.abc.core.jvm.compile.annotation.AutoToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类 - 用于测试AutoToString注解处理器（包含父类字段）
 * <p>
 * 该类使用@AutoToString注解并设置includeSuper=true，
 * 编译时会自动生成包含父类字段的toString实现。
 * </p>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-28
 * @see AutoToString
 * @see BaseEntity
 * @see ProductToStringImpl
 */
@AutoToString(includeSuper = true, exclude = {"costPrice"})
public class Product extends BaseEntity {

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 销售价格
     */
    private BigDecimal price;

    /**
     * 成本价格（敏感信息，被排除）
     */
    private BigDecimal costPrice;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 构造方法
     *
     * @param id          商品ID
     * @param createTime  创建时间
     * @param updateTime  更新时间
     * @param name        商品名称
     * @param description 商品描述
     * @param price       销售价格
     * @param costPrice   成本价格
     * @param stock       库存数量
     */
    public Product(Long id, LocalDateTime createTime, LocalDateTime updateTime,
                   String name, String description, BigDecimal price,
                   BigDecimal costPrice, Integer stock) {
        super(id, createTime, updateTime);
        this.name = name;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.stock = stock;
    }

    /**
     * 获取商品名称
     *
     * @return 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商品名称
     *
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取商品描述
     *
     * @return 商品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置商品描述
     *
     * @param description 商品描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取销售价格
     *
     * @return 销售价格
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置销售价格
     *
     * @param price 销售价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取成本价格
     *
     * @return 成本价格
     */
    public BigDecimal getCostPrice() {
        return costPrice;
    }

    /**
     * 设置成本价格
     *
     * @param costPrice 成本价格
     */
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    /**
     * 获取库存数量
     *
     * @return 库存数量
     */
    public Integer getStock() {
        return stock;
    }

    /**
     * 设置库存数量
     *
     * @param stock 库存数量
     */
    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
