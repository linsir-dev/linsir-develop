package com.linsir.spring.framework.spring_core.reflection.service;

import com.linsir.spring.framework.spring_core.reflection.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户数据访问层
 * 模拟数据库操作
 */
@Repository
public class UserRepository {

    /**
     * 内存数据存储
     */
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();

    /**
     * ID生成器
     */
    private final AtomicLong idGenerator = new AtomicLong(1);

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userStore.get(id);
    }

    /**
     * 根据用户名查询用户
     */
    public User findByUsername(String username) {
        return userStore.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    /**
     * 保存用户
     */
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.getAndIncrement());
        }
        userStore.put(user.getId(), user);
        return user;
    }

    /**
     * 删除用户
     */
    public void deleteById(Long id) {
        userStore.remove(id);
    }

    /**
     * 更新用户
     */
    public User update(User user) {
        if (user.getId() == null || !userStore.containsKey(user.getId())) {
            throw new IllegalArgumentException("User not found: " + user.getId());
        }
        userStore.put(user.getId(), user);
        return user;
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        userStore.clear();
        idGenerator.set(1);
    }

    /**
     * 获取用户数量
     */
    public long count() {
        return userStore.size();
    }
}
