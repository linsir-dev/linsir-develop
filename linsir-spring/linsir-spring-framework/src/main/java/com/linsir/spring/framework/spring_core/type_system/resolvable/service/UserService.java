package com.linsir.spring.framework.spring_core.type_system.resolvable.service;

import com.linsir.spring.framework.spring_core.type_system.resolvable.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户服务实现类
 * 继承BaseService并指定泛型参数为User和Long
 * 用于演示泛型参数的解析
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
public class UserService implements BaseService<User, Long> {

    /**
     * 模拟数据存储
     */
    private final Map<Long, User> userStore = new HashMap<>();

    /**
     * ID生成器
     */
    private long idGenerator = 1;

    @Override
    public User findById(Long id) {
        return userStore.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            entity.setId(idGenerator++);
        }
        userStore.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public void deleteById(Long id) {
        userStore.remove(id);
    }

    @Override
    public User update(User entity) {
        if (entity.getId() != null && userStore.containsKey(entity.getId())) {
            userStore.put(entity.getId(), entity);
            return entity;
        }
        throw new IllegalArgumentException("User not found with id: " + entity.getId());
    }

    /**
     * 根据用户名查询用户
     *
     * @param name 用户名
     * @return 用户列表
     */
    public List<User> findByName(String name) {
        List<User> result = new ArrayList<>();
        for (User user : userStore.values()) {
            if (user.getName().equals(name)) {
                result.add(user);
            }
        }
        return result;
    }
}
