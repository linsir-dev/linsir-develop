package com.linsir.abc.mysql.chapter01.architecture.mapper;

import com.linsir.abc.mysql.chapter01.architecture.entity.OrderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单明细数据访问接口
 * 对应数据库表：order_items
 *
 * 职责：
 * 1. 订单明细的CRUD操作
 * 2. 根据订单ID查询明细
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface OrderItemMapper {

    /**
     * 根据ID查询明细
     *
     * @param id 明细ID
     * @return 明细对象
     */
    @Select("SELECT * FROM order_items WHERE id = #{id}")
    OrderItem findById(Long id);

    /**
     * 根据订单ID查询明细列表
     *
     * @param orderId 订单ID
     * @return 明细列表
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * 插入明细
     *
     * @param item 明细对象
     * @return 影响行数
     */
    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_price, quantity, subtotal, created_at) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productPrice}, #{quantity}, #{subtotal}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItem item);

    /**
     * 批量插入明细
     *
     * @param items 明细列表
     * @return 影响行数
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 更新明细
     *
     * @param item 明细对象
     * @return 影响行数
     */
    @Update("UPDATE order_items SET product_name = #{productName}, product_price = #{productPrice}, " +
            "quantity = #{quantity}, subtotal = #{subtotal} WHERE id = #{id}")
    int update(OrderItem item);

    /**
     * 删除明细
     *
     * @param id 明细ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_items WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 根据订单ID删除明细
     *
     * @param orderId 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    int deleteByOrderId(Long orderId);
}
