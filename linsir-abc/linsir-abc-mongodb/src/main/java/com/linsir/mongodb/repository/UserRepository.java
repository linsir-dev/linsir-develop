package com.linsir.mongodb.repository;

import com.linsir.mongodb.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户仓库接口
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * 根据名称查询用户
     */
    List<User> findByName(String name);
    
    /**
     * 根据年龄范围查询用户
     */
    List<User> findByAgeBetween(Integer minAge, Integer maxAge);
    
    /**
     * 根据邮箱查询用户
     */
    User findByEmail(String email);
}