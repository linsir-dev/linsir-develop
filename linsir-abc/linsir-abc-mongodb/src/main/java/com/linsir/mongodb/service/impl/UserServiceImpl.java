package com.linsir.mongodb.service.impl;

import com.linsir.mongodb.entity.User;
import com.linsir.mongodb.repository.UserRepository;
import com.linsir.mongodb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 创建用户
     */
    @Override
    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * 根据ID查询用户
     */
    @Override
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    /**
     * 查询所有用户
     */
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * 根据名称查询用户
     */
    @Override
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }
    
    /**
     * 根据年龄范围查询用户
     */
    @Override
    public List<User> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge);
    }
    
    /**
     * 更新用户
     */
    @Override
    public User updateUser(String id, User user) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setName(user.getName());
            updatedUser.setAge(user.getAge());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setAddress(user.getAddress());
            updatedUser.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(updatedUser);
        }
        return null;
    }
    
    /**
     * 删除用户
     */
    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}