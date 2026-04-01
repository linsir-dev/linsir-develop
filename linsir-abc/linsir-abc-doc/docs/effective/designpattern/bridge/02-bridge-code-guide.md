# 桥接模式 - 代码指南

> 本文档详细说明桥接模式的代码实现和使用方法

***

## 一、项目结构

```
bridge/
├── Driver.java           # 实现化接口
├── MyDriver.java         # 具体实现化
├── DriverManager.java    # 抽象化
└── MyDriverManager.java  # 扩展抽象化
```

***

## 二、代码详解

### 2.1 Driver - 实现化接口

```java
package com.linsir.designpattern.bridge;

public interface Driver {
    void connect();
}
```

---

### 2.2 MyDriver - 具体实现化

```java
package com.linsir.designpattern.bridge;

public class MyDriver implements Driver {
    @Override
    public void connect() {
        System.out.println("MyDriver连接");
    }
}
```

---

### 2.3 DriverManager - 抽象化

```java
package com.linsir.designpattern.bridge;

public abstract class DriverManager {
    protected Driver driver;

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public abstract void connect();
}
```

---

### 2.4 MyDriverManager - 扩展抽象化

```java
package com.linsir.designpattern.bridge;

public class MyDriverManager extends DriverManager {
    @Override
    public void connect() {
        driver.connect();
    }
}
```

***

## 三、使用示例

```java
public class BridgeTest {
    public static void main(String[] args) {
        // 创建实现化
        Driver driver = new MyDriver();
        
        // 创建抽象化并设置实现化
        DriverManager manager = new MyDriverManager();
        manager.setDriver(driver);
        
        // 调用
        manager.connect();  // 输出：MyDriver连接
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-bridge-overview.md)
