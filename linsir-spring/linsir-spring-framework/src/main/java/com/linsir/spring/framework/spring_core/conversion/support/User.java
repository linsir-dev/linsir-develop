package com.linsir.spring.framework.spring_core.conversion.support;

/**
 * 用户实体类
 * 用于测试类型转换
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-23
 */
public class User {

    private String name;
    private Integer age;
    private String email;

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{name='" + name + "', age=" + age + ", email='" + email + "'}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return java.util.Objects.equals(name, user.name) &&
                java.util.Objects.equals(age, user.age) &&
                java.util.Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, age, email);
    }
}
