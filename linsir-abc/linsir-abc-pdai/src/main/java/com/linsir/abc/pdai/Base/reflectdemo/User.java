package com.linsir.abc.pdai.base.reflectdemo;

/**
 * 用户类，作为反射操作的目标
 */
public class User {
    // 公共字段
    public String name;
    // 私有字段
    private int age;
    // 受保护字段
    protected String email;
    // 默认访问权限字段
    String address;

    /**
     * 无参构造方法
     */
    public User() {
    }

    /**
     * 有参构造方法
     */
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * 公共方法
     */
    public void sayHello() {
        System.out.println("Hello, my name is " + name);
    }

    /**
     * 带参数的公共方法
     */
    public void sayHello(String message) {
        System.out.println("Hello, " + message + ", my name is " + name);
    }

    /**
     * 私有方法
     */
    private void privateMethod() {
        System.out.println("This is a private method. Age: " + age);
    }

    /**
     * 受保护方法
     */
    protected void protectedMethod() {
        System.out.println("This is a protected method. Email: " + email);
    }

    /**
     * 默认访问权限方法
     */
    void defaultMethod() {
        System.out.println("This is a default method. Address: " + address);
    }

    // getter 和 setter 方法
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
