# 单例模式 (Singleton Pattern)

> 确保一个类只有一个实例，并提供一个全局访问点

***

## 一、模式概述

### 1.1 定义

单例模式（Singleton Pattern）是一种创建型设计模式，它确保一个类只有一个实例，并提供一个全局访问点来访问这个唯一实例。

### 1.2 适用场景

- 需要频繁创建和销毁的对象
- 需要控制资源访问，如数据库连接池、线程池
- 需要全局配置信息的类
- 需要频繁访问的共享资源

### 1.3 优缺点

| 优点 | 缺点 |
|------|------|
| 内存中只有一个实例，减少内存开销 | 违反单一职责原则，一个类负责创建和管理实例 |
| 避免频繁创建和销毁对象，提高性能 | 多线程环境下需要考虑线程安全问题 |
| 全局访问点，方便管理 | 扩展困难，单例类通常难以继承 |

***

## 二、实现方式

### 2.1 懒汉模式（线程不安全）

```java
public class SingletonDemo1 {
    // 懒汉模式，线程不安全
    private static SingletonDemo1 instance;
    
    public static SingletonDemo1 getInstance() {
        if (instance == null) {
            instance = new SingletonDemo1();
        }
        return instance;
    }
}
```

**特点**：
- 延迟加载，第一次使用时才创建实例
- 线程不安全，多线程环境下可能创建多个实例
- 适用于单线程环境

### 2.2 懒汉模式（线程安全）

```java
public class SingletonDemo2 {
    // 懒汉模式，线程安全
    private static SingletonDemo2 instance;
    
    public static synchronized SingletonDemo2 getInstance() {
        if (instance == null) {
            instance = new SingletonDemo2();
        }
        return instance;
    }
}
```

**特点**：
- 通过 `synchronized` 关键字保证线程安全
- 每次获取实例都需要同步，性能较低
- 适用于多线程环境，但性能要求不高

### 2.3 饿汉模式

```java
public class SingletonDemo3 {
    // 饿汉模式
    private static SingletonDemo3 instance = new SingletonDemo3();
    
    public static SingletonDemo3 getInstance() {
        return instance;
    }
}
```

**特点**：
- 类加载时就创建实例
- 线程安全，无需同步
- 可能浪费内存（实例未被使用）

### 2.4 双重检查锁定（DCL）

```java
public class SingletonDemo4 {
    // 双重检查锁定
    private volatile static SingletonDemo4 instance;
    
    public static SingletonDemo4 getInstance() {
        if (instance == null) {
            synchronized (SingletonDemo4.class) {
                if (instance == null) {
                    instance = new SingletonDemo4();
                }
            }
        }
        return instance;
    }
}
```

**特点**：
- 延迟加载，线程安全
- 使用 `volatile` 关键字防止指令重排序
- 第一次创建实例时需要同步，后续无需同步
- 适用于多线程环境，性能要求高

### 2.5 静态内部类

```java
public class SingletonDemo5 {
    // 静态内部类
    private static class SingletonHolder {
        private static final SingletonDemo5 INSTANCE = new SingletonDemo5();
    }
    
    public static SingletonDemo5 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

**特点**：
- 延迟加载，线程安全
- 利用类加载机制保证线程安全
- 无需同步，性能最优
- 推荐使用的方式

***

## 三、实现对比

| 实现方式 | 线程安全 | 延迟加载 | 性能 | 推荐度 |
|----------|----------|----------|------|--------|
| 懒汉（线程不安全） | ❌ | ✅ | 高 | ⭐ |
| 懒汉（线程安全） | ✅ | ✅ | 低 | ⭐⭐ |
| 饿汉 | ✅ | ❌ | 高 | ⭐⭐⭐ |
| 双重检查锁定 | ✅ | ✅ | 高 | ⭐⭐⭐⭐ |
| 静态内部类 | ✅ | ✅ | 高 | ⭐⭐⭐⭐⭐ |

***

## 四、注意事项

1. **反射攻击**：可以通过反射调用私有构造函数创建多个实例
2. **序列化问题**：反序列化时会创建新实例，需要实现 `readResolve()` 方法
3. **克隆问题**：如果实现了 `Cloneable` 接口，需要重写 `clone()` 方法

***

## 五、相关文档

- [代码指南](./02-singleton-code-guide.md)
