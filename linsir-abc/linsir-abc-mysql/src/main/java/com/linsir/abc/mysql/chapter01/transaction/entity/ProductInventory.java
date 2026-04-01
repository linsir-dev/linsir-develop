package com.linsir.abc.mysql.chapter01.transaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 商品库存实体类
 * 
 * <p>记录兑换商品的库存信息，用于演示并发控制和死锁场景</p>
 * 
 * <p>对应数据库表：product_inventory</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Data
public class ProductInventory {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 商品ID
     * <p>关联的兑换商品ID</p>
     */
    private Long productId;
    
    /**
     * 可用库存
     * <p>当前可以兑换的库存数量</p>
     */
    private Integer availableStock;
    
    /**
     * 锁定库存
     * <p>已被锁定但未完成兑换的库存</p>
     */
    private Integer lockedStock;
    
    /**
     * 乐观锁版本号
     * <p>用于并发控制</p>
     */
    private Integer version;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 锁定库存
     * <p>将可用库存转为锁定库存</p>
     * 
     * @param quantity 要锁定的数量
     * @throws IllegalStateException 如果库存不足
     */
    public void lockStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("锁定数量必须为正数");
        }
        if (this.availableStock == null || this.availableStock < quantity) {
            throw new IllegalStateException("库存不足，无法锁定");
        }
        if (this.lockedStock == null) {
            this.lockedStock = 0;
        }
        this.availableStock -= quantity;
        this.lockedStock += quantity;
    }
    
    /**
     * 解锁库存
     * <p>将锁定库存转回可用库存</p>
     * 
     * @param quantity 要解锁的数量
     */
    public void unlockStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("解锁数量必须为正数");
        }
        if (this.lockedStock == null || this.lockedStock < quantity) {
            throw new IllegalStateException("锁定库存不足，无法解锁");
        }
        this.lockedStock -= quantity;
        this.availableStock += quantity;
    }
    
    /**
     * 扣减库存
     * <p>从锁定库存中扣减（兑换完成时调用）</p>
     * 
     * @param quantity 要扣减的数量
     */
    public void deductStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("扣减数量必须为正数");
        }
        if (this.lockedStock == null || this.lockedStock < quantity) {
            throw new IllegalStateException("锁定库存不足，无法扣减");
        }
        this.lockedStock -= quantity;
    }
    
    /**
     * 增加库存
     * 
     * @param quantity 要增加的数量
     */
    public void addStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("增加数量必须为正数");
        }
        if (this.availableStock == null) {
            this.availableStock = 0;
        }
        this.availableStock += quantity;
    }
    
    /**
     * 检查库存是否充足
     * 
     * @param quantity 需要检查的数量
     * @return true-库存充足
     */
    public boolean hasSufficientStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        return this.availableStock != null && this.availableStock >= quantity;
    }
    
    /**
     * 获取总库存
     * <p>总库存 = 可用库存 + 锁定库存</p>
     * 
     * @return 总库存
     */
    public Integer getTotalStock() {
        int available = this.availableStock != null ? this.availableStock : 0;
        int locked = this.lockedStock != null ? this.lockedStock : 0;
        return available + locked;
    }
}
