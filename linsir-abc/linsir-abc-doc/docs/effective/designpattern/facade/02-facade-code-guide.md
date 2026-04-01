# 外观模式 - 代码指南

> 本文档详细说明外观模式的代码实现和使用方法

***

## 一、项目结构

```
facade/
├── SubSystemA.java    # 子系统A
├── SubSystemB.java    # 子系统B
├── SubSystemC.java    # 子系统C
└── Facade.java        # 外观类
```

***

## 二、代码详解

### 2.1 SubSystemA - 子系统A

```java
package com.linsir.designpattern.facade;

public class SubSystemA {
    public void operationA() {
        System.out.println("子系统A的操作");
    }
}
```

---

### 2.2 SubSystemB - 子系统B

```java
package com.linsir.designpattern.facade;

public class SubSystemB {
    public void operationB() {
        System.out.println("子系统B的操作");
    }
}
```

---

### 2.3 SubSystemC - 子系统C

```java
package com.linsir.designpattern.facade;

public class SubSystemC {
    public void operationC() {
        System.out.println("子系统C的操作");
    }
}
```

---

### 2.4 Facade - 外观类

```java
package com.linsir.designpattern.facade;

public class Facade {
    private SubSystemA subSystemA;
    private SubSystemB subSystemB;
    private SubSystemC subSystemC;

    public Facade() {
        subSystemA = new SubSystemA();
        subSystemB = new SubSystemB();
        subSystemC = new SubSystemC();
    }

    public void operation() {
        subSystemA.operationA();
        subSystemB.operationB();
        subSystemC.operationC();
    }
}
```

***

## 三、使用示例

```java
public class FacadeTest {
    public static void main(String[] args) {
        // 使用外观类简化操作
        Facade facade = new Facade();
        facade.operation();
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-facade-overview.md)
