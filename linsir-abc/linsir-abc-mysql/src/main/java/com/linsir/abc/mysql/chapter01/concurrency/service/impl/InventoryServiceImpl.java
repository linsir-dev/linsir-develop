package com.linsir.abc.mysql.chapter01.concurrency.service.impl;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
import com.linsir.abc.mysql.chapter01.concurrency.mapper.InventoryMapper;
import com.linsir.abc.mysql.chapter01.concurrency.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 库存服务实现类
 * 演示库存并发控制方案
 *
 * <p>实现要点：</p>
 * <ul>
 *   <li>悲观锁：SELECT FOR UPDATE，适合强一致性场景</li>
 *   <li>乐观锁：版本号控制，适合高并发场景</li>
 *   <li>预占模式：lockStock -> confirmDeduct/unlockStock</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Inventory createInventory(Inventory inventory) {
        inventory.setVersion(0);
        inventory.setAvailableStock(inventory.getAvailableStock() != null ? inventory.getAvailableStock() : 0);
        inventory.setLockedStock(0);
        inventoryMapper.insert(inventory);
        log.info("创建库存成功：productId={}, warehouseId={}, id={}",
                inventory.getProductId(), inventory.getWarehouseId(), inventory.getId());
        return inventory;
    }

    @Override
    public Inventory getInventoryById(Long id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    public Inventory getInventoryByProduct(Long productId, Integer warehouseId) {
        return inventoryMapper.selectByProductAndWarehouse(productId, warehouseId);
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryMapper.selectAll();
    }

    /**
     * 扣减库存（悲观锁）
     * 使用SELECT FOR UPDATE锁定库存记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStockWithPessimisticLock(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("扣减数量必须大于0");
        }

        // 加锁并读取库存
        Inventory inventory = inventoryMapper.selectByIdForUpdate(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }

        if (!inventory.hasEnoughStock(quantity)) {
            throw new RuntimeException("库存不足");
        }

        // 扣减库存
        int affected = inventoryMapper.deductStock(inventoryId, quantity);
        if (affected == 0) {
            throw new RuntimeException("扣减库存失败");
        }

        log.info("扣减库存成功（悲观锁）：inventoryId={}, quantity={}", inventoryId, quantity);
        return true;
    }

    /**
     * 扣减库存（乐观锁）
     * 使用版本号控制并发
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStockWithOptimisticLock(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("扣减数量必须大于0");
        }

        // 读取库存（无锁）
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }

        if (!inventory.hasEnoughStock(quantity)) {
            throw new RuntimeException("库存不足");
        }

        // 乐观锁更新
        int affected = inventoryMapper.deductStockWithVersion(inventoryId, quantity, inventory.getVersion());
        if (affected == 0) {
            log.warn("库存扣减版本冲突，inventoryId={}", inventoryId);
            throw new RuntimeException("扣减库存失败，请重试");
        }

        log.info("扣减库存成功（乐观锁）：inventoryId={}, quantity={}", inventoryId, quantity);
        return true;
    }

    /**
     * 锁定库存
     * 预占库存模式，适用于订单创建但未支付场景
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockStock(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("锁定数量必须大于0");
        }

        Inventory inventory = inventoryMapper.selectByIdForUpdate(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }

        if (!inventory.hasEnoughStock(quantity)) {
            throw new RuntimeException("库存不足");
        }

        int affected = inventoryMapper.lockStock(inventoryId, quantity);
        if (affected == 0) {
            throw new RuntimeException("锁定库存失败");
        }

        log.info("锁定库存成功：inventoryId={}, quantity={}", inventoryId, quantity);
        return true;
    }

    /**
     * 释放库存
     * 将锁定的库存释放回可用库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unlockStock(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("释放数量必须大于0");
        }

        Inventory inventory = inventoryMapper.selectByIdForUpdate(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }

        if (!inventory.canUnlockStock(quantity)) {
            throw new RuntimeException("锁定库存不足");
        }

        int affected = inventoryMapper.unlockStock(inventoryId, quantity);
        if (affected == 0) {
            throw new RuntimeException("释放库存失败");
        }

        log.info("释放库存成功：inventoryId={}, quantity={}", inventoryId, quantity);
        return true;
    }

    /**
     * 确认扣减
     * 订单支付完成后，将锁定库存转为实际扣减
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmDeduct(Long inventoryId, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("确认数量必须大于0");
        }

        Inventory inventory = inventoryMapper.selectByIdForUpdate(inventoryId);
        if (inventory == null) {
            throw new RuntimeException("库存记录不存在");
        }

        if (!inventory.canUnlockStock(quantity)) {
            throw new RuntimeException("锁定库存不足");
        }

        int affected = inventoryMapper.confirmDeduct(inventoryId, quantity);
        if (affected == 0) {
            throw new RuntimeException("确认扣减失败");
        }

        log.info("确认扣减成功：inventoryId={}, quantity={}", inventoryId, quantity);
        return true;
    }

    /**
     * 库存盘点
     * 更新最后盘点时间
     */
    @Override
    public Inventory checkInventory(Long inventoryId) {
        Inventory inventory = inventoryMapper.selectById(inventoryId);
        if (inventory != null) {
            inventoryMapper.updateCheckTime(inventoryId);
            log.info("库存盘点：inventoryId={}, availableStock={}, lockedStock={}",
                    inventoryId, inventory.getAvailableStock(), inventory.getLockedStock());
        }
        return inventory;
    }
}
