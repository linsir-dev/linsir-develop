package com.linsir.abc.mysql.chapter01.transaction.mapper;

import com.linsir.abc.mysql.chapter01.transaction.entity.ProductInventory;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品库存Mapper接口
 * 
 * <p>提供商品库存的CRUD操作和并发控制</p>
 * 
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface ProductInventoryMapper {
    
    /**
     * 根据ID查询商品库存
     * 
     * @param id 库存ID
     * @return 商品库存信息
     */
    @Select("SELECT * FROM product_inventory WHERE id = #{id}")
    ProductInventory selectById(Long id);
    
    /**
     * 根据ID查询商品库存（加锁）
     * 
     * @param id 库存ID
     * @return 商品库存信息
     */
    @Select("SELECT * FROM product_inventory WHERE id = #{id} FOR UPDATE")
    ProductInventory selectByIdForUpdate(Long id);
    
    /**
     * 根据商品ID查询库存
     * 
     * @param productId 商品ID
     * @return 商品库存信息
     */
    @Select("SELECT * FROM product_inventory WHERE product_id = #{productId}")
    ProductInventory selectByProductId(Long productId);
    
    /**
     * 根据商品ID查询库存（加锁）
     * 
     * @param productId 商品ID
     * @return 商品库存信息
     */
    @Select("SELECT * FROM product_inventory WHERE product_id = #{productId} FOR UPDATE")
    ProductInventory selectByProductIdForUpdate(Long productId);
    
    /**
     * 查询所有库存
     * 
     * @return 商品库存列表
     */
    @Select("SELECT * FROM product_inventory")
    List<ProductInventory> selectAll();
    
    /**
     * 更新可用库存（乐观锁）
     * 
     * @param id 库存ID
     * @param quantity 变动数量（正数增加，负数减少）
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_inventory SET " +
            "available_stock = available_stock + #{quantity}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int updateAvailableStock(@Param("id") Long id, 
                              @Param("quantity") Integer quantity, 
                              @Param("version") Integer version);
    
    /**
     * 锁定库存
     * <p>将可用库存转为锁定库存</p>
     * 
     * @param id 库存ID
     * @param quantity 锁定数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_inventory SET " +
            "available_stock = available_stock - #{quantity}, " +
            "locked_stock = locked_stock + #{quantity}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND available_stock >= #{quantity}")
    int lockStock(@Param("id") Long id, 
                  @Param("quantity") Integer quantity, 
                  @Param("version") Integer version);
    
    /**
     * 解锁库存
     * <p>将锁定库存转回可用库存</p>
     * 
     * @param id 库存ID
     * @param quantity 解锁数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_inventory SET " +
            "available_stock = available_stock + #{quantity}, " +
            "locked_stock = locked_stock - #{quantity}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND locked_stock >= #{quantity}")
    int unlockStock(@Param("id") Long id, 
                    @Param("quantity") Integer quantity, 
                    @Param("version") Integer version);
    
    /**
     * 扣减锁定库存
     * <p>兑换完成时调用，从锁定库存中扣减</p>
     * 
     * @param id 库存ID
     * @param quantity 扣减数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_inventory SET " +
            "locked_stock = locked_stock - #{quantity}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version} " +
            "AND locked_stock >= #{quantity}")
    int deductLockedStock(@Param("id") Long id, 
                          @Param("quantity") Integer quantity, 
                          @Param("version") Integer version);
    
    /**
     * 返还库存（取消兑换）
     * <p>取消兑换时调用，增加可用库存</p>
     * 
     * @param id 库存ID
     * @param quantity 返还数量
     * @param version 当前版本号
     * @return 影响行数
     */
    @Update("UPDATE product_inventory SET " +
            "available_stock = available_stock + #{quantity}, " +
            "version = version + 1, " +
            "updated_at = NOW() " +
            "WHERE id = #{id} AND version = #{version}")
    int returnStock(@Param("id") Long id, 
                    @Param("quantity") Integer quantity, 
                    @Param("version") Integer version);
    
    /**
     * 插入商品库存
     * 
     * @param inventory 商品库存信息
     * @return 影响行数
     */
    @Insert("INSERT INTO product_inventory (product_id, available_stock, locked_stock, version) " +
            "VALUES (#{productId}, #{availableStock}, #{lockedStock}, #{version})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductInventory inventory);
    
    /**
     * 删除商品库存
     * 
     * @param id 库存ID
     * @return 影响行数
     */
    @Delete("DELETE FROM product_inventory WHERE id = #{id}")
    int deleteById(Long id);
}
