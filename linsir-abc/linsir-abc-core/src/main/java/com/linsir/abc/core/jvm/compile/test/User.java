package com.linsir.abc.core.jvm.compile.test;

import com.linsir.abc.core.jvm.compile.annotation.AutoToString;

/**
 * 用户实体类 - 用于测试AutoToString注解处理器
 * <p>
 * 该类使用@AutoToString注解，编译时会自动生成toString实现。
 * 密码字段被排除在toString输出之外，以保护敏感信息。
 * </p>
 *
 * @author linsir
 * @version 1.0
 * @since 2026-03-28
 * @see AutoToString
 * @see UserToStringImpl
 */
@AutoToString(exclude = {"password"})
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
     * 密码（敏感信息，被排除在toString输出外）
     */
    private String password;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 静态字段（不会被包含在toString中）
     */
    private static final String DEFAULT_ROLE = "USER";

    /**
     * 构造方法
     *
     * @param id       用户ID
     * @param username 用户名
     * @param password 密码
     * @param age      年龄
     */
    public User(Long id, String username, String password, Integer age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.age = age;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取年龄
     *
     * @return 年龄
     */
    public Integer getAge() {
        return age;
    }

    /**
     * 设置年龄
     *
     * @param age 年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * 获取默认角色
     *
     * @return 默认角色
     */
    public static String getDefaultRole() {
        return DEFAULT_ROLE;
    }
}
