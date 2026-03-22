package com.linsir.spring.framework.spring_core.type_system.resolvable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserService extends BaseService with concrete types
 * Demonstrates how ResolvableType can resolve T=User, ID=Long
 */
public class UserService extends BaseService<User, Long> {

    private Map<Long, User> userStore = new HashMap<>();
    private long nextId = 0;

    @Override
    public User findById(Long id) {
        return userStore.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    @Override
    public void save(User entity) {
        userStore.put(nextId++, entity);
    }

    @Override
    public void deleteById(Long id) {
        userStore.remove(id);
    }
}
