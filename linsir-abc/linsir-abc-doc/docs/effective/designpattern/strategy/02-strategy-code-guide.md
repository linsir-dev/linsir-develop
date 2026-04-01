# 策略模式 - 代码指南

> 本文档详细说明策略模式的代码实现和使用方法

***

## 一、项目结构

```
strategy/
├── MathOperation.java      # 策略接口
├── Addition.java           # 加法策略
├── Subtraction.java        # 减法策略
├── Multiplication.java     # 乘法策略
└── Calculator.java         # 上下文
```

***

## 二、代码详解

### 2.1 MathOperation - 策略接口

```java
package com.linsir.designpattern.strategy;

public interface MathOperation {
    int calculate(int num1, int num2);
}
```

---

### 2.2 Addition - 加法策略

```java
package com.linsir.designpattern.strategy;

public class Addition implements MathOperation {
    @Override
    public int calculate(int num1, int num2) {
        return num1 + num2;
    }
}
```

---

### 2.3 Subtraction - 减法策略

```java
package com.linsir.designpattern.strategy;

public class Subtraction implements MathOperation {
    @Override
    public int calculate(int num1, int num2) {
        return num1 - num2;
    }
}
```

---

### 2.4 Multiplication - 乘法策略

```java
package com.linsir.designpattern.strategy;

public class Multiplication implements MathOperation {
    @Override
    public int calculate(int num1, int num2) {
        return num1 * num2;
    }
}
```

---

### 2.5 Calculator - 上下文

```java
package com.linsir.designpattern.strategy;

public class Calculator {
    private MathOperation operation;

    public void setOperation(MathOperation operation) {
        this.operation = operation;
    }

    public int execute(int num1, int num2) {
        return operation.calculate(num1, num2);
    }
}
```

***

## 三、使用示例

```java
public class StrategyTest {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();

        // 使用加法策略
        calculator.setOperation(new Addition());
        System.out.println("10 + 5 = " + calculator.execute(10, 5));

        // 使用减法策略
        calculator.setOperation(new Subtraction());
        System.out.println("10 - 5 = " + calculator.execute(10, 5));

        // 使用乘法策略
        calculator.setOperation(new Multiplication());
        System.out.println("10 * 5 = " + calculator.execute(10, 5));
    }
}
```

**输出**：
```
10 + 5 = 15
10 - 5 = 5
10 * 5 = 50
```

***

## 四、与简单工厂模式的区别

| 特性 | 简单工厂模式 | 策略模式 |
|------|-------------|---------|
| 目的 | 创建对象 | 封装算法 |
| 关注点 | 对象的创建 | 算法的选择和使用 |
| 客户端角色 | 告诉工厂创建什么 | 告诉上下文使用什么策略 |

***

## 五、相关文档

- [设计模式概述](./01-strategy-overview.md)
