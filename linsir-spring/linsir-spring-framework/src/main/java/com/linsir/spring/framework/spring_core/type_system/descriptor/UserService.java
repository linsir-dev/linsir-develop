package com.linsir.spring.framework.spring_core.type_system.descriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserService for TypeDescriptor demo
 * Demonstrates property access and type description
 */
public class UserService {

    private Map<Long, User> userStore = new HashMap<>();
    private String serviceName;
    private int maxUsers;

    public UserService() {
        this.serviceName = "DefaultUserService";
        this.maxUsers = 100;
    }

    public UserService(String serviceName, int maxUsers) {
        this.serviceName = serviceName;
        this.maxUsers = maxUsers;
    }

    public User findById(Long id) {
        return userStore.get(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }

    public void save(User user) {
        userStore.put((long) userStore.size(), user);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Map<Long, User> getUserStore() {
        return userStore;
    }
}
