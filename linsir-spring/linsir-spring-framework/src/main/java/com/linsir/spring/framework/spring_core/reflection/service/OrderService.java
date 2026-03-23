package com.linsir.spring.framework.spring_core.reflection.service;

import com.linsir.spring.framework.spring_core.reflection.model.Order;
import com.linsir.spring.framework.spring_core.reflection.model.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 订单服务层
 * 继承 BaseService，用于测试继承链反射
 */
@Service
public class OrderService extends BaseService<Order> {

    /**
     * 订单存储
     */
    private final Map<Long, Order> orderStore = new ConcurrentHashMap<>();

    /**
     * ID生成器
     */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 私有字段
     */
    private BigDecimal discountRate = new BigDecimal("0.95");

    /**
     * 构造方法
     */
    public OrderService() {
        super(Order.class);
    }

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(Long userId, BigDecimal amount) {
        Order order = new Order();
        order.setOrderId(idGenerator.getAndIncrement());
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setAmount(amount.multiply(discountRate));
        order.setStatus(Order.OrderStatus.PENDING);
        orderStore.put(order.getOrderId(), order);
        return order;
    }

    /**
     * 根据ID查询订单
     */
    @Override
    @Transactional(readOnly = true)
    public Order findById(Long orderId) {
        return orderStore.get(orderId);
    }

    /**
     * 保存订单（实现抽象方法）
     */
    @Override
    @Transactional
    public Order save(Order order) {
        if (order.getOrderId() == null) {
            order.setOrderId(idGenerator.getAndIncrement());
        }
        orderStore.put(order.getOrderId(), order);
        return order;
    }

    /**
     * 查询用户的所有订单
     */
    @Transactional(readOnly = true)
    public List<Order> findByUserId(Long userId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orderStore.values()) {
            if (order.getUserId().equals(userId)) {
                result.add(order);
            }
        }
        return result;
    }

    /**
     * 支付订单
     */
    @Transactional
    public void payOrder(Long orderId) {
        Order order = orderStore.get(orderId);
        if (order != null && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.PAID);
        }
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderStore.get(orderId);
        if (order != null && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CANCELLED);
        }
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + idGenerator.get();
    }

    /**
     * 获取折扣率
     */
    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    /**
     * 设置折扣率
     */
    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    /**
     * 清空所有订单
     */
    public void clear() {
        orderStore.clear();
        idGenerator.set(1);
    }

    /**
     * 获取订单数量
     */
    public long count() {
        return orderStore.size();
    }
}
