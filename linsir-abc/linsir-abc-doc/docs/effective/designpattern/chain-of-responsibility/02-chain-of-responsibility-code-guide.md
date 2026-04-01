# 责任链模式 - 代码指南

> 本文档详细说明责任链模式的代码实现和使用方法

***

## 一、项目结构

```
chainOfResponsibility/
├── Handler.java         # 处理者接口
├── ConcreteHandlerA.java # 具体处理者A
├── ConcreteHandlerB.java # 具体处理者B
└── Client.java          # 客户端
```

***

## 二、代码详解

### 2.1 Handler - 处理者接口

```java
package com.linsir.designpattern.chainOfResponsibility;

public abstract class Handler {
    protected Handler successor;

    public void setSuccessor(Handler successor) {
        this.successor = successor;
    }

    public abstract void handleRequest(int request);
}
```

---

### 2.2 ConcreteHandlerA - 具体处理者A

```java
package com.linsir.designpattern.chainOfResponsibility;

public class ConcreteHandlerA extends Handler {
    @Override
    public void handleRequest(int request) {
        if (request >= 0 && request < 10) {
            System.out.println("处理者A处理请求: " + request);
        } else if (successor != null) {
            successor.handleRequest(request);
        }
    }
}
```

---

### 2.3 ConcreteHandlerB - 具体处理者B

```java
package com.linsir.designpattern.chainOfResponsibility;

public class ConcreteHandlerB extends Handler {
    @Override
    public void handleRequest(int request) {
        if (request >= 10 && request < 20) {
            System.out.println("处理者B处理请求: " + request);
        } else if (successor != null) {
            successor.handleRequest(request);
        }
    }
}
```

***

## 三、使用示例

```java
public class ChainOfResponsibilityTest {
    public static void main(String[] args) {
        Handler handlerA = new ConcreteHandlerA();
        Handler handlerB = new ConcreteHandlerB();

        // 设置责任链
        handlerA.setSuccessor(handlerB);

        // 发送请求
        handlerA.handleRequest(5);   // 处理者A处理
        handlerA.handleRequest(15);  // 处理者B处理
    }
}
```

***

## 四、相关文档

- [设计模式概述](./01-chain-of-responsibility-overview.md)
