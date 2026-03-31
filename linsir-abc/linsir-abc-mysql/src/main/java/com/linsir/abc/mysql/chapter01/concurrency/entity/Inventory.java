package com.linsir.abc.mysql.chapter01.concurrency.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 库存实体类
 * 对应数据库表：inventory
 * 用于演示库存扣减、秒杀等并发场景
 *
 * <p>核心字段说明：</p>
 * <ul>
 *   <li>available_stock - 可用库存，可立即销售的库存</li>
 *   <li>locked_stock - 锁定库存，已预占但未确认扣减的库存</li>
 *   <li>version - 乐观锁版本号，用于并发控制</li>
 * </ul>
 *
 * <p>库存模式：</p>
 * <ul>
 *   <li>预占模式：下单时锁定库存，支付成功后确认扣减</li>
 *   <li>直接扣减：下单时直接扣减库存</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    /**
     * 库存ID
     * 主键，自增
     */
    private Long id;

    /**
     * 商品ID
     * 关联products表
     */
    private Long productId;

    /**
     * 仓库ID
     * 支持多仓库库存管理，默认1
     */
    private Integer warehouseId;

    /**
     * 可用库存
     * 可立即销售的库存数量
     */
    private Integer availableStock;

    /**
     * 锁定库存
     * 已预占但未确认扣减的库存数量
     */
    private Integer lockedStock;

    /**
     * 乐观锁版本号
     * 用于乐观锁并发控制
     */
    private Integer version;

    /**
     * 最后盘点时间
     * 记录最后一次库存盘点时间
     */
    private LocalDateTime lastCheckTime;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 获取实际库存总量
     * 实际库存 = 可用库存 + 锁定库存
     *
     * @return 实际库存总量
     */
    public Integer getTotalStock() {
        int available = availableStock != null ? availableStock : 0;
        int locked = lockedStock != null ? lockedStock : 0;
        return available + locked;
    }

    /**
     * 检查是否有足够库存
     *
     * @param quantity 需要数量
     * @return 是否足够
     */
    public boolean hasEnoughStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        int available = availableStock != null ? availableStock : 0;
        return available >= quantity;
    }

    /**
     * 检查是否有足够锁定库存可释放
     *
     * @param quantity 需要释放的数量
     * @return 是否可释放
     */
    public boolean canUnlockStock(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            return false;
        }
        int locked = lockedStock != null ? lockedStock : 0;
        return locked >= quantity;
    }
}
