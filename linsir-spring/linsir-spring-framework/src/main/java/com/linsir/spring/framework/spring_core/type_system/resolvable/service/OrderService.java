package com.linsir.spring.framework.spring_core.type_system.resolvable.service;

import com.linsir.spring.framework.spring_core.type_system.resolvable.entity.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单服务实现类
 * 继承BaseService并指定泛型参数为Order和Long
 * 用于演示泛型参数的解析
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class OrderService implements BaseService<Order, Long> {

    /**
     * 模拟数据存储
     */
    private final Map<Long, Order> orderStore = new HashMap<>();

    /**
     * ID生成器
     */
    private long idGenerator = 1;

    @Override
    public Order findById(Long id) {
        return orderStore.get(id);
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orderStore.values());
    }

    @Override
    public Order save(Order entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator++);
        }
        orderStore.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        orderStore.remove(id);
    }

    @Override
    public Order update(Order entity) {
        if (entity.getId() != null && orderStore.containsKey(entity.getId())) {
            orderStore.put(entity.getId(), entity);
            return entity;
        }
        throw new IllegalArgumentException("Order not found with id: " + entity.getId());
    }

    /**
     * 根据订单号查询订单
     *
     * @param orderNo 订单号
     * @return 订单对象
     */
    public Order findByOrderNo(String orderNo) {
        for (Order order : orderStore.values()) {
            if (order.getOrderNo().equals(orderNo)) {
                return order;
            }
        }
        return null;
    }
}
