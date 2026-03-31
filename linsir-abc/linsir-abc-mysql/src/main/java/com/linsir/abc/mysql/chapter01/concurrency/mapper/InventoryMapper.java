package com.linsir.abc.mysql.chapter01.concurrency.mapper;

import com.linsir.abc.mysql.chapter01.concurrency.entity.Inventory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 库存Mapper接口
 * 演示库存扣减、锁库存等并发场景
 *
 * <p>库存操作模式：</p>
 * <ul>
 *   <li>直接扣减：deductStock 直接减少可用库存</li>
 *   <li>预占模式：lockStock 锁定库存，confirmDeduct 确认扣减，unlockStock 释放库存</li>
 * </ul>
 *
 * <p>锁机制：</p>
 * <ul>
 *   <li>selectByIdForUpdate - 悲观锁，用于需要强一致性的库存操作</li>
 *   <li>deductStockWithVersion - 乐观锁，通过版本号控制并发</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface InventoryMapper {

    /**
     * 根据ID查询库存（无锁）
     *
     * @param id 库存ID
     * @return 库存信息
     */
    @Select("SELECT * FROM inventory WHERE id = #{id}")
    Inventory selectById(Long id);

    /**
     * 根据ID查询库存（悲观锁）
     * 使用FOR UPDATE锁定记录
     *
     * @param id 库存ID
     * @return 库存信息
     */
    @Select("SELECT * FROM inventory WHERE id = #{id} FOR UPDATE")
    Inventory selectByIdForUpdate(Long id);

    /**
     * 根据商品ID和仓库ID查询库存
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存信息
     */
    @Select("SELECT * FROM inventory WHERE product_id = #{productId} AND warehouse_id = #{warehouseId}")
    Inventory selectByProductAndWarehouse(@Param("productId") Long productId, @Param("warehouseId") Integer warehouseId);

    /**
     * 根据商品ID和仓库ID查询库存（悲观锁）
     *
     * @param productId   商品ID
     * @param warehouseId 仓库ID
     * @return 库存信息
     */
    @Select("SELECT * FROM inventory WHERE product_id = #{productId} AND warehouse_id = #{warehouseId} FOR UPDATE")
    Inventory selectByProductAndWarehouseForUpdate(@Param("productId") Long productId, @Param("warehouseId") Integer warehouseId);

    /**
     * 根据ID查询库存（共享锁）
     * 使用LOCK IN SHARE MODE允许其他事务读取但阻止写入
     *
     * @param id 库存ID
     * @return 库存信息
     */
    @Select("SELECT * FROM inventory WHERE id = #{id} LOCK IN SHARE MODE")
    Inventory selectByIdLockInShareMode(Long id);

    /**
     * 查询所有库存
     *
     * @return 库存列表
     */
    @Select("SELECT * FROM inventory ORDER BY id")
    List<Inventory> selectAll();

    /**
     * 插入库存记录
     *
     * @param inventory 库存信息
     * @return 影响行数
     */
    @Insert("INSERT INTO inventory (product_id, warehouse_id, available_stock, locked_stock, version) " +
            "VALUES (#{productId}, #{warehouseId}, #{availableStock}, #{lockedStock}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Inventory inventory);

    /**
     * 扣减库存（悲观锁方式）
     * 直接扣减可用库存，需要外部加锁保证一致性
     *
     * @param id       库存ID
     * @param quantity 扣减数量
     * @return 影响行数
     */
    @Update("UPDATE inventory SET available_stock = available_stock - #{quantity}, updated_at = NOW() " +
            "WHERE id = #{id} AND available_stock >= #{quantity}")
    int deductStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 扣减库存（乐观锁方式）
     * 通过版本号控制并发
     *
     * @param id       库存ID
     * @param quantity 扣减数量
     * @param version  当前版本号
     * @return 影响行数，0表示版本冲突或库存不足
     */
    @Update("UPDATE inventory SET available_stock = available_stock - #{quantity}, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND available_stock >= #{quantity} AND version = #{version}")
    int deductStockWithVersion(@Param("id") Long id, @Param("quantity") Integer quantity, @Param("version") Integer version);

    /**
     * 锁定库存
     * 将可用库存转为锁定库存，用于预占模式
     *
     * @param id       库存ID
     * @param quantity 锁定数量
     * @return 影响行数
     */
    @Update("UPDATE inventory SET available_stock = available_stock - #{quantity}, locked_stock = locked_stock + #{quantity}, updated_at = NOW() " +
            "WHERE id = #{id} AND available_stock >= #{quantity}")
    int lockStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 释放库存
     * 将锁定库存释放回可用库存
     *
     * @param id       库存ID
     * @param quantity 释放数量
     * @return 影响行数
     */
    @Update("UPDATE inventory SET available_stock = available_stock + #{quantity}, locked_stock = locked_stock - #{quantity}, updated_at = NOW() " +
            "WHERE id = #{id} AND locked_stock >= #{quantity}")
    int unlockStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 确认扣减库存
     * 将锁定库存转为实际扣减（减少锁定库存）
     *
     * @param id       库存ID
     * @param quantity 确认数量
     * @return 影响行数
     */
    @Update("UPDATE inventory SET locked_stock = locked_stock - #{quantity}, updated_at = NOW() " +
            "WHERE id = #{id} AND locked_stock >= #{quantity}")
    int confirmDeduct(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 增加库存
     * 用于库存回补场景
     *
     * @param id       库存ID
     * @param quantity 增加数量
     * @return 影响行数
     */
    @Update("UPDATE inventory SET available_stock = available_stock + #{quantity}, updated_at = NOW() WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 更新库存（用于测试重置）
     * 直接设置库存数量和版本号
     *
     * @param id            库存ID
     * @param stock         库存数量
     * @param currentVersion 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE inventory SET available_stock = #{stock}, locked_stock = 0, version = version + 1, updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{currentVersion}")
    int updateStock(@Param("id") Long id, @Param("stock") Integer stock, @Param("currentVersion") Integer currentVersion);

    /**
     * 更新盘点时间
     *
     * @param id 库存ID
     * @return 影响行数
     */
    @Update("UPDATE inventory SET last_check_time = NOW(), updated_at = NOW() WHERE id = #{id}")
    int updateCheckTime(Long id);

    /**
     * 删除库存记录
     *
     * @param id 库存ID
     * @return 影响行数
     */
    @Delete("DELETE FROM inventory WHERE id = #{id}")
    int deleteById(Long id);
}
