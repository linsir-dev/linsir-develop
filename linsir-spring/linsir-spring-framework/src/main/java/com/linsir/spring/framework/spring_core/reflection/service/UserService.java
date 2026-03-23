package com.linsir.spring.framework.spring_core.reflection.service;

import com.linsir.spring.framework.spring_core.reflection.model.Autowired;
import com.linsir.spring.framework.spring_core.reflection.model.Transactional;
import com.linsir.spring.framework.spring_core.reflection.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务层
 * 用于反射工具示例中的服务类
 */
@Service
public class UserService implements IUserService {

    /**
     * 用户仓库 - 用于测试依赖注入
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 私有字段 - 用于测试私有字段反射
     */
    private String secretKey = "default-secret";

    /**
     * 静态字段
     */
    public static final String SERVICE_NAME = "UserService";

    /**
     * 根据ID查询用户
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * 查询所有用户
     */
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * 保存用户
     */
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * 私有方法 - 用于测试私有方法反射
     */
    private String generateToken(Long userId) {
        return "token-" + userId + "-" + System.currentTimeMillis();
    }

    /**
     * 受保护方法 - 用于测试继承链方法反射
     */
    protected void logOperation(String operation) {
        System.out.println("[UserService] Operation: " + operation);
    }

    /**
     * 获取服务信息
     */
    @Override
    public String getServiceInfo() {
        return "UserService - User Management Service";
    }

    /**
     * 重载方法1：根据用户名查询
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * 重载方法2：根据用户名和邮箱查询
     */
    public User findByUsername(String username, String email) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getEmail().equals(email)) {
            return user;
        }
        return null;
    }

    /**
     * 获取私有字段值 - 用于测试验证
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * 设置私有字段值
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
