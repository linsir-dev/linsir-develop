# 适配器模式 - 代码指南

> 本文档详细说明适配器模式的代码实现和使用方法

***

## 一、项目结构

```
adapter/
├── BigPort.java      # 目标接口
├── SmallPort.java    # 被适配者
└── SmallToBig.java   # 适配器
```

***

## 二、代码详解

### 2.1 BigPort - 目标接口

```java
package com.linsir.designpattern.adapter;

public interface BigPort {
    void useBigPort();
}
```

---

### 2.2 SmallPort - 被适配者

```java
package com.linsir.designpattern.adapter;

public class SmallPort {
    public void useSmallPort() {
        System.out.println("使用小端口");
    }
}
```

---

### 2.3 SmallToBig - 适配器

```java
package com.linsir.designpattern.adapter;

public class SmallToBig extends SmallPort implements BigPort {
    @Override
    public void useBigPort() {
        useSmallPort();
    }
}
```

***

## 三、使用示例

```java
public class AdapterTest {
    public static void main(String[] args) {
        // 使用适配器
        BigPort bigPort = new SmallToBig();
        bigPort.useBigPort();  // 输出：使用小端口
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-adapter-overview.md)
