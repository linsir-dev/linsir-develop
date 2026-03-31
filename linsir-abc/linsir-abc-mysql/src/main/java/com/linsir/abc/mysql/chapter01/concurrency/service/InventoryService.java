package com.linsir.abc.mysql.chapter01.concurrency.service;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;

import java.util.List;

/**
 * 库存服务接口
 * 演示库存扣减、秒杀等并发场景
 *
 * <p>库存操作模式：</p>
 * <ul>
 *   <li>直接扣减：立即减少可用库存</li>
 *   <li>预占模式：先锁定库存，确认后再扣减，支持取消释放</li>
 * </ul>
 *
 * <p>并发控制方案：</p>
 * <ul>
 *   <li>悲观锁：SELECT FOR UPDATE，适合强一致性场景</li>
 *   <li>乐观锁：版本号控制，适合高并发场景</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
public interface InventoryService {

    /**
     * 创建库存记录
     *
     * @param inventory 库存信息
     * @return 创建的库存
     */
    Inventory createInventory(Inventory inventory);

    /**
     * 根据ID查询库存
     *
     * @param id 库存ID
     * @return 库存信息
     */
    Inventory getInventoryById(Long id);

    /**
     * 根据商品ID和仓库ID查询库存
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存信息
     */
    Inventory getInventoryByProduct(Long productId, Integer warehouseId);

    /**
     * 查询所有库存
     *
     * @return 库存列表
     */
    List<Inventory> getAllInventory();

    /**
     * 扣减库存（悲观锁）
     * 使用SELECT FOR UPDATE锁定库存记录
     *
     * @param inventoryId 库存ID
     * @param quantity    扣减数量
     * @return 是否成功
     */
    boolean deductStockWithPessimisticLock(Long inventoryId, Integer quantity);

    /**
     * 扣减库存（乐观锁）
     * 使用版本号控制并发
     *
     * @param inventoryId 库存ID
     * @param quantity    扣减数量
     * @return 是否成功
     */
    boolean deductStockWithOptimisticLock(Long inventoryId, Integer quantity);

    /**
     * 锁定库存（预占库存模式）
     * 将可用库存转为锁定库存
     *
     * @param inventoryId 库存ID
     * @param quantity    锁定数量
     * @return 是否成功
     */
    boolean lockStock(Long inventoryId, Integer quantity);

    /**
     * 释放库存
     * 将锁定库存释放回可用库存
     *
     * @param inventoryId 库存ID
     * @param quantity    释放数量
     * @return 是否成功
     */
    boolean unlockStock(Long inventoryId, Integer quantity);

    /**
     * 确认扣减库存
     * 将锁定库存转为实际扣减
     *
     * @param inventoryId 库存ID
     * @param quantity    确认数量
     * @return 是否成功
     */
    boolean confirmDeduct(Long inventoryId, Integer quantity);

    /**
     * 库存盘点
     * 更新最后盘点时间
     *
     * @param inventoryId 库存ID
     * @return 库存信息
     */
    Inventory checkInventory(Long inventoryId);
}
