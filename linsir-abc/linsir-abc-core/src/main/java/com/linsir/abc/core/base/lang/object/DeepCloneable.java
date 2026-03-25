package com.linsir.abc.core.base.lang.object;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 深拷贝接口及实现示例
 *
 * 本接口定义了深拷贝的能力，并提供多种实现方式：
 * 1. 递归克隆（实现Cloneable接口）
 * 2. 序列化克隆（实现Serializable接口）
 * 3. 拷贝构造器方式
 *
 * 深拷贝 vs 浅拷贝：
 * - 浅拷贝：只复制对象本身，引用类型字段共享引用
 * - 深拷贝：递归复制对象及其所有引用类型字段
 *
 * @param <T> 要克隆的对象类型
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public interface DeepCloneable<T> extends Cloneable, Serializable {

    /**
     * 执行深拷贝
     *
     * @return 深拷贝的对象
     * @throws CloneNotSupportedException 如果对象不支持克隆
     */
    T deepClone() throws CloneNotSupportedException;

    /**
     * 使用序列化进行深拷贝
     *
     * 优点：实现简单，自动处理所有引用类型
     * 缺点：性能较差，要求所有对象都实现Serializable
     *
     * @return 深拷贝的对象
     * @throws IOException 序列化或反序列化异常
     * @throws ClassNotFoundException 类找不到异常
     */
    @SuppressWarnings("unchecked")
    default T deepCloneBySerialization() throws IOException, ClassNotFoundException {
        // 1. 将对象序列化为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        oos.close();

        // 2. 从字节数组反序列化为新对象
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        T cloned = (T) ois.readObject();
        ois.close();

        return cloned;
    }
}

/**
 * 深拷贝示例类 - 部门
 *
 * 演示如何实现深拷贝接口
 */
class Department implements DeepCloneable<Department> {

    private static final long serialVersionUID = 1L;

    /**
     * 部门ID
     */
    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 部门员工列表（引用类型，需要深拷贝）
     */
    private List<Employee> employees;

    /**
     * 上级部门（自引用，需要处理循环引用问题）
     */
    private Department parentDepartment;

    /**
     * 构造方法
     */
    public Department() {
        this.employees = new ArrayList<>();
    }

    /**
     * 拷贝构造器 - 另一种深拷贝实现方式
     *
     * @param other 要拷贝的部门对象
     */
    public Department(Department other) {
        this.id = other.id;
        this.name = other.name;

        // 深拷贝员工列表
        this.employees = new ArrayList<>();
        for (Employee emp : other.employees) {
            this.employees.add(new Employee(emp));
        }

        // 注意：这里不拷贝parentDepartment，避免无限递归
        // 实际应用中可能需要特殊处理循环引用
        this.parentDepartment = other.parentDepartment;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Employee> getEmployees() { return employees; }
    public void setEmployees(List<Employee> employees) { this.employees = employees; }

    public Department getParentDepartment() { return parentDepartment; }
    public void setParentDepartment(Department parentDepartment) { this.parentDepartment = parentDepartment; }

    /**
     * 添加员工
     *
     * @param employee 员工对象
     */
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    /**
     * 实现深拷贝 - 递归克隆方式
     *
     * @return 深拷贝的部门对象
     * @throws CloneNotSupportedException 如果对象不支持克隆
     */
    @Override
    public Department deepClone() throws CloneNotSupportedException {
        // 1. 浅拷贝基本字段
        Department cloned = (Department) super.clone();

        // 2. 深拷贝员工列表
        cloned.employees = new ArrayList<>();
        for (Employee emp : this.employees) {
            cloned.employees.add(emp.deepClone());
        }

        // 3. 处理上级部门（避免无限递归，只复制引用）
        // 实际应用中可能需要更复杂的处理
        if (this.parentDepartment != null) {
            // 这里只复制引用，避免循环引用导致的栈溢出
            // 如果需要完整深拷贝，需要使用其他策略
            cloned.parentDepartment = this.parentDepartment;
        }

        return cloned;
    }

    @Override
    public String toString() {
        return "Department{id=" + id + ", name='" + name + "', employees=" + employees.size() + "}";
    }
}

/**
 * 深拷贝示例类 - 员工
 */
class Employee implements DeepCloneable<Employee> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Double salary;
    private Address address;

    public Employee() {}

    public Employee(Long id, String name, Double salary, Address address) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.address = address;
    }

    /**
     * 拷贝构造器
     */
    public Employee(Employee other) {
        this.id = other.id;
        this.name = other.name;
        this.salary = other.salary;
        this.address = other.address != null ? new Address(other.address) : null;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    @Override
    public Employee deepClone() throws CloneNotSupportedException {
        Employee cloned = (Employee) super.clone();

        // 深拷贝地址对象
        if (this.address != null) {
            cloned.address = this.address.deepClone();
        }

        return cloned;
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', salary=" + salary + "}";
    }
}

/**
 * 深拷贝示例类 - 地址
 */
class Address implements DeepCloneable<Address> {

    private static final long serialVersionUID = 1L;

    private String province;
    private String city;
    private String street;

    public Address() {}

    public Address(String province, String city, String street) {
        this.province = province;
        this.city = city;
        this.street = street;
    }

    /**
     * 拷贝构造器
     */
    public Address(Address other) {
        this.province = other.province;
        this.city = other.city;
        this.street = other.street;
    }

    // Getters and Setters
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    @Override
    public Address deepClone() throws CloneNotSupportedException {
        // Address只有基本类型字段，浅拷贝即可
        return (Address) super.clone();
    }

    @Override
    public String toString() {
        return province + " " + city + " " + street;
    }
}
