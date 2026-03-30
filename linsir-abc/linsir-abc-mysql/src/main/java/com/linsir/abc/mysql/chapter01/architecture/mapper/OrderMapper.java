package com.linsir.abc.mysql.chapter01.architecture.mapper;

import com.linsir.abc.mysql.chapter01.architecture.entity.Order;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据访问接口
 * 对应数据库表：orders
 *
 * 职责：
 * 1. 订单数据的CRUD操作
 * 2. 订单状态管理
 * 3. 订单统计查询
 *
 * @author linsir
 * @since 1.0.0
 */
@Mapper
public interface OrderMapper {

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单对象
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Order findById(Long id);

    /**
     * 根据订单编号查询
     *
     * @param orderNo 订单编号
     * @return 订单对象
     */
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo}")
    Order findByOrderNo(String orderNo);

    /**
     * 根据用户ID查询订单
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Order> findByUserId(Long userId);

    /**
     * 查询所有订单
     *
     * @return 订单列表
     */
    @Select("SELECT * FROM orders ORDER BY created_at DESC")
    List<Order> findAll();

    /**
     * 根据状态查询订单
     *
     * @param status 状态
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE status = #{status} ORDER BY created_at DESC")
    List<Order> findByStatus(Integer status);

    /**
     * 插入订单
     *
     * @param order 订单对象
     * @return 影响行数
     */
    @Insert("INSERT INTO orders (order_no, user_id, total_amount, discount_amount, pay_amount, " +
            "status, pay_type, pay_time, ship_time, complete_time, remark, created_at, updated_at) " +
            "VALUES (#{orderNo}, #{userId}, #{totalAmount}, #{discountAmount}, #{payAmount}, " +
            "#{status}, #{payType}, #{payTime}, #{shipTime}, #{completeTime}, #{remark}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);

    /**
     * 更新订单
     *
     * @param order 订单对象
     * @return 影响行数
     */
    @Update("UPDATE orders SET total_amount = #{totalAmount}, discount_amount = #{discountAmount}, " +
            "pay_amount = #{payAmount}, status = #{status}, pay_type = #{payType}, " +
            "pay_time = #{payTime}, ship_time = #{shipTime}, complete_time = #{completeTime}, " +
            "remark = #{remark}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Order order);

    /**
     * 更新订单状态
     *
     * @param orderId   订单ID
     * @param status    新状态
     * @param updatedAt 更新时间
     * @return 影响行数
     */
    @Update("UPDATE orders SET status = #{status}, updated_at = #{updatedAt} WHERE id = #{orderId}")
    int updateStatus(@Param("orderId") Long orderId,
                     @Param("status") Integer status,
                     @Param("updatedAt") LocalDateTime updatedAt);

    /**
     * 删除订单
     *
     * @param id 订单ID
     * @return 影响行数
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);

    /**
     * 统计订单数量
     *
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders")
    long count();

    /**
     * 统计用户订单数量
     *
     * @param userId 用户ID
     * @return 订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE user_id = #{userId}")
    long countByUserId(Long userId);

    /**
     * 统计订单总金额
     *
     * @return 总金额
     */
    @Select("SELECT COALESCE(SUM(pay_amount), 0) FROM orders WHERE status IN (1, 2, 3)")
    BigDecimal sumPayAmount();
}
