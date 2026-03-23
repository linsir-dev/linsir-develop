package com.linsir.spring.framework.spring_core.reflection.service;

import com.linsir.spring.framework.spring_core.reflection.model.User;

import java.util.List;

/**
 * 用户服务接口
 * 用于 JDK 动态代理测试
 */
public interface IUserService {

    /**
     * 根据ID查询用户
     */
    User findById(Long id);

    /**
     * 查询所有用户
     */
    List<User> findAll();

    /**
     * 保存用户
     */
    User save(User user);

    /**
     * 删除用户
     */
    void deleteById(Long id);

    /**
     * 根据用户名查询用户
     */
    User findByUsername(String username);

    /**
     * 根据用户名和邮箱查询用户
     */
    User findByUsername(String username, String email);

    /**
     * 获取服务信息
     */
    String getServiceInfo();
}
