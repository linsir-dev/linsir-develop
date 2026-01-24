package com.linsir.mongodb.service;

import com.linsir.mongodb.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {
    
    /**
     * 创建用户
     */
    User createUser(User user);
    
    /**
     * 根据ID查询用户
     */
    Optional<User> getUserById(String id);
    
    /**
     * 查询所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据名称查询用户
     */
    List<User> getUsersByName(String name);
    
    /**
     * 根据年龄范围查询用户
     */
    List<User> getUsersByAgeRange(Integer minAge, Integer maxAge);
    
    /**
     * 更新用户
     */
    User updateUser(String id, User user);
    
    /**
     * 删除用户
     */
    void deleteUser(String id);
}