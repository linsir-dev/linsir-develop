# 单例模式 - 代码指南

> 本文档详细说明单例模式的代码实现和使用方法

***

## 一、项目结构

```
singleton/
├── SingletonDemo1.java    # 懒汉模式（线程不安全）
├── SingletonDemo2.java    # 懒汉模式（线程安全）
├── SingletonDemo3.java    # 饿汉模式
├── SingletonDemo4.java    # 双重检查锁定
└── SingletonDemo5.java    # 静态内部类
```

***

## 二、代码详解

### 2.1 SingletonDemo1 - 懒汉模式（线程不安全）

```java
package com.linsir.designpattern.singleton;

public class SingletonDemo1 {
    // 懒汉模式，线程不安全
    private static SingletonDemo1 instance;
    
    // 私有构造函数，防止外部实例化
    private SingletonDemo1() {}
    
    public static SingletonDemo1 getInstance() {
        System.out.print("\n懒汉模式，线程不安全\n");
        if (instance == null) {
            instance = new SingletonDemo1();
        }
        return instance;
    }
}
```

**核心要点**：
- `private static SingletonDemo1 instance`：静态实例变量
- `private SingletonDemo1()`：私有构造函数
- `getInstance()`：静态方法返回实例

**线程安全问题**：
```java
// 多线程环境下可能出现问题：
// 线程A: if (instance == null) [true]
// 线程B: if (instance == null) [true]
// 线程A: instance = new SingletonDemo1()
// 线程B: instance = new SingletonDemo1()
// 结果：创建了两个实例
```

---

### 2.2 SingletonDemo2 - 懒汉模式（线程安全）

```java
package com.linsir.designpattern.singleton;

public class SingletonDemo2 {
    // 懒汉模式，线程安全
    private static SingletonDemo2 instance;
    
    private SingletonDemo2() {}
    
    // synchronized 保证线程安全
    public static synchronized SingletonDemo2 getInstance() {
        if (instance == null) {
            instance = new SingletonDemo2();
        }
        return instance;
    }
}
```

**核心要点**：
- `synchronized` 关键字修饰 `getInstance()` 方法
- 每次调用都需要获取锁，性能较低

---

### 2.3 SingletonDemo3 - 饿汉模式

```java
package com.linsir.designpattern.singleton;

public class SingletonDemo3 {
    // 饿汉模式：类加载时就创建实例
    private static SingletonDemo3 instance = new SingletonDemo3();
    
    private SingletonDemo3() {}
    
    public static SingletonDemo3 getInstance() {
        return instance;
    }
}
```

**核心要点**：
- `private static SingletonDemo3 instance = new SingletonDemo3()`：类加载时初始化
- 利用类加载机制保证线程安全
- 无需同步，性能高

---

### 2.4 SingletonDemo4 - 双重检查锁定

```java
package com.linsir.designpattern.singleton;

public class SingletonDemo4 {
    // volatile 防止指令重排序
    private volatile static SingletonDemo4 instance;
    
    private SingletonDemo4() {}
    
    public static SingletonDemo4 getInstance() {
        // 第一次检查：避免不必要的同步
        if (instance == null) {
            synchronized (SingletonDemo4.class) {
                // 第二次检查：确保只创建一次
                if (instance == null) {
                    instance = new SingletonDemo4();
                }
            }
        }
        return instance;
    }
}
```

**核心要点**：
- `volatile` 关键字：防止指令重排序，保证可见性
- 双重检查：减少同步开销，保证线程安全
- `instance = new SingletonDemo4()` 不是原子操作：
  1. 分配内存空间
  2. 初始化对象
  3. 将引用指向内存地址

**为什么需要 volatile**：
```java
// 没有 volatile 可能出现问题（指令重排序）：
// 1. 分配内存空间
// 3. 将引用指向内存地址（此时 instance != null，但未初始化）
// 2. 初始化对象

// 线程A执行到步骤3时，线程B进入：
// if (instance == null) [false，直接返回未初始化的对象]
```

---

### 2.5 SingletonDemo5 - 静态内部类

```java
package com.linsir.designpattern.singleton;

public class SingletonDemo5 {
    // 静态内部类
    private static class SingletonHolder {
        private static final SingletonDemo5 INSTANCE = new SingletonDemo5();
    }
    
    private SingletonDemo5() {}
    
    public static SingletonDemo5 getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
```

**核心要点**：
- `SingletonHolder` 静态内部类：延迟加载
- 类加载机制保证线程安全
- 无需同步，性能最优

**延迟加载原理**：
```java
// SingletonHolder 类只有在 getInstance() 被调用时才会加载
// 实现了延迟加载，又保证了线程安全
```

***

## 三、使用示例

### 3.1 基本使用

```java
public class SingletonTest {
    public static void main(String[] args) {
        // 获取单例实例
        SingletonDemo5 singleton1 = SingletonDemo5.getInstance();
        SingletonDemo5 singleton2 = SingletonDemo5.getInstance();
        
        // 验证是否为同一实例
        System.out.println(singleton1 == singleton2); // true
    }
}
```

### 3.2 多线程测试

```java
public class SingletonThreadTest {
    public static void main(String[] args) {
        // 创建100个线程获取单例
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                SingletonDemo5 singleton = SingletonDemo5.getInstance();
                System.out.println(Thread.currentThread().getName() + ": " + singleton.hashCode());
            }).start();
        }
    }
}
```

***

## 四、最佳实践

1. **推荐使用静态内部类方式**：延迟加载 + 线程安全 + 高性能
2. **避免使用线程不安全的懒汉模式**：除非确定是单线程环境
3. **考虑序列化问题**：如果需要序列化，实现 `readResolve()` 方法
4. **考虑反射攻击**：可以在构造函数中添加防护逻辑

***

## 五、相关文档

- [设计模式概述](./01-singleton-overview.md)
