# 单例模式

## 单例模式的定义

单例模式（Singleton Pattern）是一种创建型设计模式，它确保一个类只有一个实例，并提供一个全局访问点。

## 单例模式的结构

```
┌─────────────────┐
│   Singleton     │
├─────────────────┤
│ - instance      │
├─────────────────┤
│ + getInstance() │
└─────────────────┘
```

## 单例模式的实现方式

### 1. 饿汉式（Eager Initialization）

饿汉式在类加载时就创建实例，线程安全但可能造成资源浪费。

```java
public class Singleton {
    private static final Singleton instance = new Singleton();
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        return instance;
    }
}
```

**优点**：
- 线程安全
- 实现简单

**缺点**：
- 在类加载时就创建实例，可能造成资源浪费
- 无法实现延迟加载

### 2. 懒汉式（Lazy Initialization）

懒汉式在第一次调用时创建实例，但有线程安全问题。

```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

**优点**：
- 实现了延迟加载

**缺点**：
- 线程不安全

### 3. 懒汉式（线程安全）

懒汉式（线程安全）使用 synchronized 关键字保证线程安全，但性能较差。

```java
public class Singleton {
    private static Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static synchronized Singleton getInstance() {
        if (instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}
```

**优点**：
- 线程安全
- 实现了延迟加载

**缺点**：
- 性能较差，每次获取实例都需要加锁

### 4. 双重检查锁（Double-Checked Locking）

双重检查锁在第一次调用时创建实例，使用双重检查锁保证线程安全，性能较好。

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

**优点**：
- 线程安全
- 实现了延迟加载
- 性能较好

**缺点**：
- 实现稍复杂
- 需要使用 volatile 关键字

### 5. 静态内部类（Static Inner Class）

静态内部类利用类加载机制保证线程安全，实现了延迟加载，性能较好。

```java
public class Singleton {
    private Singleton() {
        // 私有构造函数
    }
    
    private static class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }
    
    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }
}
```

**优点**：
- 线程安全
- 实现了延迟加载
- 性能较好
- 实现简单

**缺点**：
- 无法传递参数

### 6. 枚举（Enum）

枚举是 Java 推荐的实现单例模式的方式，线程安全，防止反序列化重新创建新的对象。

```java
public enum Singleton {
    INSTANCE;
    
    public void doSomething() {
        // 业务逻辑
    }
}
```

**优点**：
- 线程安全
- 实现简单
- 防止反序列化重新创建新的对象
- 防止反射攻击

**缺点**：
- 无法实现延迟加载
- 无法继承

## 单例模式的优缺点

### 优点

1. **内存占用少**：单例模式只创建一个实例，减少了内存占用。

2. **全局访问**：单例模式提供了一个全局访问点，方便访问。

3. **避免资源冲突**：单例模式避免了多个实例之间的资源冲突。

4. **延迟加载**：某些实现方式支持延迟加载，提高了性能。

### 缺点

1. **违反单一职责原则**：单例模式既负责创建实例，又负责业务逻辑，违反了单一职责原则。

2. **难以测试**：单例模式的全局访问点使得单元测试变得困难。

3. **不支持继承**：单例模式的私有构造函数不支持继承。

4. **多线程问题**：某些实现方式在多线程环境下需要特殊处理。

## 单例模式的使用场景

### 1. 配置管理器

```java
public class ConfigManager {
    private static volatile ConfigManager instance;
    private Properties properties;
    
    private ConfigManager() {
        properties = new Properties();
        // 加载配置文件
    }
    
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
```

### 2. 数据库连接池

```java
public class ConnectionPool {
    private static volatile ConnectionPool instance;
    private List<Connection> connections;
    
    private ConnectionPool() {
        connections = new ArrayList<>();
        // 初始化连接池
    }
    
    public static ConnectionPool getInstance() {
        if (instance == null) {
            synchronized (ConnectionPool.class) {
                if (instance == null) {
                    instance = new ConnectionPool();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        // 获取连接
    }
    
    public void releaseConnection(Connection connection) {
        // 释放连接
    }
}
```

### 3. 日志管理器

```java
public class LogManager {
    private static volatile LogManager instance;
    private Logger logger;
    
    private LogManager() {
        logger = Logger.getLogger(LogManager.class.getName());
        // 初始化日志配置
    }
    
    public static LogManager getInstance() {
        if (instance == null) {
            synchronized (LogManager.class) {
                if (instance == null) {
                    instance = new LogManager();
                }
            }
        }
        return instance;
    }
    
    public void log(String message) {
        logger.info(message);
    }
}
```

### 4. 缓存管理器

```java
public class CacheManager {
    private static volatile CacheManager instance;
    private Map<String, Object> cache;
    
    private CacheManager() {
        cache = new ConcurrentHashMap<>();
    }
    
    public static CacheManager getInstance() {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager();
                }
            }
        }
        return instance;
    }
    
    public void put(String key, Object value) {
        cache.put(key, value);
    }
    
    public Object get(String key) {
        return cache.get(key);
    }
    
    public void remove(String key) {
        cache.remove(key);
    }
}
```

## 单例模式的注意事项

### 1. 序列化问题

单例模式在序列化和反序列化时可能会创建新的实例，需要实现 readResolve 方法。

```java
public class Singleton implements Serializable {
    private static volatile Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
    
    protected Object readResolve() {
        return getInstance();
    }
}
```

### 2. 反射问题

单例模式在反射时可能会创建新的实例，需要在构造函数中添加检查。

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {
        if (instance != null) {
            throw new IllegalStateException("Singleton already initialized");
        }
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

### 3. 多线程问题

单例模式在多线程环境下需要特殊处理，可以使用双重检查锁或静态内部类等方式。

### 4. 类加载问题

单例模式在类加载时可能会创建实例，需要根据实际情况选择合适的实现方式。

## 单例模式的最佳实践

### 1. 使用枚举

枚举是 Java 推荐的实现单例模式的方式，线程安全，防止反序列化重新创建新的对象。

```java
public enum Singleton {
    INSTANCE;
    
    public void doSomething() {
        // 业务逻辑
    }
}
```

### 2. 使用静态内部类

静态内部类利用类加载机制保证线程安全，实现了延迟加载，性能较好。

```java
public class Singleton {
    private Singleton() {
        // 私有构造函数
    }
    
    private static class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }
    
    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }
}
```

### 3. 使用双重检查锁

双重检查锁在第一次调用时创建实例，使用双重检查锁保证线程安全，性能较好。

```java
public class Singleton {
    private static volatile Singleton instance;
    
    private Singleton() {
        // 私有构造函数
    }
    
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
```

## 总结

单例模式是一种创建型设计模式，它确保一个类只有一个实例，并提供一个全局访问点。单例模式的实现方式包括饿汉式、懒汉式、懒汉式（线程安全）、双重检查锁、静态内部类和枚举。单例模式的优点包括内存占用少、全局访问、避免资源冲突和延迟加载。单例模式的缺点包括违反单一职责原则、难以测试、不支持继承和多线程问题。单例模式的使用场景包括配置管理器、数据库连接池、日志管理器和缓存管理器。单例模式的注意事项包括序列化问题、反射问题、多线程问题和类加载问题。单例模式的最佳实践包括使用枚举、使用静态内部类和使用双重检查锁。