package com.linsir.abc.core.base.lang.object;

import java.util.Objects;

/**
 * Object方法重写示例类
 * 
 * 本类演示了如何正确重写Object类的核心方法：
 * - equals(): 判断对象相等性
 * - hashCode(): 生成对象的哈希码
 * - toString(): 对象的字符串表示
 * - clone(): 对象的克隆（浅拷贝/深拷贝）
 * 
 * 设计要点：
 * 1. equals和hashCode必须同时重写，保持契约一致性
 * 2. hashCode的计算要基于equals中使用的字段
 * 3. toString应提供有意义的对象信息
 * 4. clone方法需要实现Cloneable接口
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ObjectMethodOverride implements Cloneable {
    
    /**
     * 对象唯一标识
     */
    private final Long id;
    
    /**
     * 对象名称
     */
    private String name;
    
    /**
     * 对象年龄
     */
    private Integer age;
    
    /**
     * 关联对象（用于演示深拷贝）
     */
    private ObjectMethodOverride reference;
    
    /**
     * 构造方法
     * 
     * @param id 对象ID
     * @param name 对象名称
     * @param age 对象年龄
     */
    public ObjectMethodOverride(Long id, String name, Integer age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    /**
     * 获取对象ID
     * 
     * @return 对象ID
     */
    public Long getId() {
        return id;
    }
    
    /**
     * 获取对象名称
     * 
     * @return 对象名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置对象名称
     * 
     * @param name 对象名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 获取对象年龄
     * 
     * @return 对象年龄
     */
    public Integer getAge() {
        return age;
    }
    
    /**
     * 设置对象年龄
     * 
     * @param age 对象年龄
     */
    public void setAge(Integer age) {
        this.age = age;
    }
    
    /**
     * 获取关联对象
     * 
     * @return 关联对象
     */
    public ObjectMethodOverride getReference() {
        return reference;
    }
    
    /**
     * 设置关联对象
     * 
     * @param reference 关联对象
     */
    public void setReference(ObjectMethodOverride reference) {
        this.reference = reference;
    }
    
    /**
     * 重写equals方法
     * 
     * 契约：
     * 1. 自反性：x.equals(x) 必须返回true
     * 2. 对称性：x.equals(y) 为true，则 y.equals(x) 也必须为true
     * 3. 传递性：x.equals(y)为true且y.equals(z)为true，则x.equals(z)必须为true
     * 4. 一致性：多次调用结果相同
     * 5. 非空性：x.equals(null) 必须返回false
     * 
     * @param obj 要比较的对象
     * @return 如果相等返回true，否则返回false
     */
    @Override
    public boolean equals(Object obj) {
        // 1. 检查是否为同一对象引用
        if (this == obj) {
            return true;
        }
        
        // 2. 检查对象是否为null
        if (obj == null) {
            return false;
        }
        
        // 3. 检查对象类型是否相同
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        // 4. 转换为具体类型并比较字段
        ObjectMethodOverride other = (ObjectMethodOverride) obj;
        
        // 比较id字段（使用Objects.equals处理null情况）
        return Objects.equals(this.id, other.id);
    }
    
    /**
     * 重写hashCode方法
     * 
     * 契约：
     * 1. 在Java程序执行期间，对同一对象多次调用hashCode()应返回相同整数
     * 2. 如果两个对象equals相等，则hashCode必须相等
     * 3. 如果两个对象equals不等，hashCode不要求必须不等（但最好不等以提高性能）
     * 
     * @return 对象的哈希码
     */
    @Override
    public int hashCode() {
        // 基于equals中使用的字段计算hashCode
        // 使用Objects.hash可以简化计算，也可以手动计算：
        // return (id == null) ? 0 : id.hashCode();
        return Objects.hash(id);
    }
    
    /**
     * 重写toString方法
     * 
     * 提供对象的有意义字符串表示，便于调试和日志输出
     * 
     * @return 对象的字符串表示
     */
    @Override
    public String toString() {
        return "ObjectMethodOverride{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", reference=" + (reference != null ? reference.getId() : null) +
                '}';
    }
    
    /**
     * 重写clone方法 - 浅拷贝
     * 
     * 浅拷贝：只复制对象本身及其基本类型字段，引用类型字段只复制引用
     * 
     * @return 克隆的对象
     * @throws CloneNotSupportedException 如果对象不支持克隆
     */
    @Override
    protected ObjectMethodOverride clone() throws CloneNotSupportedException {
        // 调用父类的clone方法进行浅拷贝
        return (ObjectMethodOverride) super.clone();
    }
    
    /**
     * 深拷贝方法
     * 
     * 深拷贝：不仅复制对象本身，还递归复制所有引用类型字段
     * 
     * @return 深拷贝的对象
     * @throws CloneNotSupportedException 如果对象不支持克隆
     */
    public ObjectMethodOverride deepClone() throws CloneNotSupportedException {
        // 1. 先进行浅拷贝
        ObjectMethodOverride cloned = (ObjectMethodOverride) super.clone();
        
        // 2. 对引用类型字段进行深拷贝
        if (this.reference != null) {
            cloned.reference = this.reference.deepClone();
        }
        
        return cloned;
    }
    
    /**
     * 使用序列化实现深拷贝（备选方案）
     * 
     * 这种方法不需要实现Cloneable接口，但需要实现Serializable接口
     * 
     * @return 深拷贝的对象
     * @throws Exception 序列化或反序列化异常
     */
    public ObjectMethodOverride deepCloneBySerialization() throws Exception {
        // 注意：这里简化演示，实际实现需要完整的序列化代码
        // 可以使用ByteArrayOutputStream和ObjectOutputStream进行序列化
        // 然后使用ByteArrayInputStream和ObjectInputStream进行反序列化
        throw new UnsupportedOperationException("需要实现Serializable接口");
    }
}
