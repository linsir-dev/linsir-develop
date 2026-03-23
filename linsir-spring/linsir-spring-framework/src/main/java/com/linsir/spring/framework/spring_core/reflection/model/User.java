package com.linsir.spring.framework.spring_core.reflection.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * 用于反射工具示例中的数据模型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 静态字段 - 用于测试静态字段反射
     */
    public static final String DEFAULT_ROLE = "USER";

    /**
     * 私有静态字段
     */
    private static final String SYSTEM_VERSION = "1.0.0";

    /**
     * 构造方法：仅使用用户名
     */
    public User(String username) {
        this.username = username;
        this.createTime = LocalDateTime.now();
    }

    /**
     * 构造方法：使用用户名和邮箱
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.createTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createTime=" + createTime +
                '}';
    }
}
