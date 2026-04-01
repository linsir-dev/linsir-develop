# 抽象工厂模式 - 代码指南

> 本文档详细说明抽象工厂模式的代码实现和使用方法

***

## 一、项目结构

```
abstractFactory/
├── AbstractFactory.java      # 抽象工厂
├── ConcreateFactory1.java    # 具体工厂1
├── ConcreateFactory2.java    # 具体工厂2
├── ProductA.java             # 抽象产品A
├── ProductA1.java            # 具体产品A1
├── ProductA2.java            # 具体产品A2
├── ProductB.java             # 抽象产品B
├── ProductB1.java            # 具体产品B1
└── ProductB2.java            # 具体产品B2
```

***

## 二、代码详解

### 2.1 AbstractFactory - 抽象工厂

```java
package com.linsir.designpattern.abstractFactory;

public interface AbstractFactory {
    ProductA createProductA();
    ProductB createProductB();
}
```

**说明**：
- 声明创建抽象产品对象的操作接口
- 每个方法对应一种产品的创建

---

### 2.2 ConcreateFactory1 - 具体工厂1

```java
package com.linsir.designpattern.abstractFactory;

public class ConcreateFactory1 implements AbstractFactory {
    @Override
    public ProductA createProductA() {
        return new ProductA1();
    }

    @Override
    public ProductB createProductB() {
        return new ProductB1();
    }
}
```

---

### 2.3 ConcreateFactory2 - 具体工厂2

```java
package com.linsir.designpattern.abstractFactory;

public class ConcreateFactory2 implements AbstractFactory {
    @Override
    public ProductA createProductA() {
        return new ProductA2();
    }

    @Override
    public ProductB createProductB() {
        return new ProductB2();
    }
}
```

---

### 2.4 ProductA - 抽象产品A

```java
package com.linsir.designpattern.abstractFactory;

public interface ProductA {
    void use();
}
```

---

### 2.5 ProductA1 - 具体产品A1

```java
package com.linsir.designpattern.abstractFactory;

public class ProductA1 implements ProductA {
    @Override
    public void use() {
        System.out.println("使用产品A1");
    }
}
```

---

### 2.6 ProductA2 - 具体产品A2

```java
package com.linsir.designpattern.abstractFactory;

public class ProductA2 implements ProductA {
    @Override
    public void use() {
        System.out.println("使用产品A2");
    }
}
```

---

### 2.7 ProductB - 抽象产品B

```java
package com.linsir.designpattern.abstractFactory;

public interface ProductB {
    void eat();
}
```

---

### 2.8 ProductB1 - 具体产品B1

```java
package com.linsir.designpattern.abstractFactory;

public class ProductB1 implements ProductB {
    @Override
    public void eat() {
        System.out.println("食用产品B1");
    }
}
```

---

### 2.9 ProductB2 - 具体产品B2

```java
package com.linsir.designpattern.abstractFactory;

public class ProductB2 implements ProductB {
    @Override
    public void eat() {
        System.out.println("食用产品B2");
    }
}
```

***

## 三、使用示例

```java
public class AbstractFactoryTest {
    public static void main(String[] args) {
        // 使用工厂1创建产品系列1
        AbstractFactory factory1 = new ConcreateFactory1();
        ProductA productA1 = factory1.createProductA();
        ProductB productB1 = factory1.createProductB();
        productA1.use();  // 输出：使用产品A1
        productB1.eat();  // 输出：食用产品B1

        // 使用工厂2创建产品系列2
        AbstractFactory factory2 = new ConcreateFactory2();
        ProductA productA2 = factory2.createProductA();
        ProductB productB2 = factory2.createProductB();
        productA2.use();  // 输出：使用产品A2
        productB2.eat();  // 输出：食用产品B2
    }
}
```

***

## 四、与工厂方法模式的区别

| 特性 | 工厂方法模式 | 抽象工厂模式 |
|------|-------------|-------------|
| 产品等级 | 一个产品等级 | 多个产品等级 |
| 工厂数量 | 一个产品对应一个工厂 | 一个产品系列对应一个工厂 |
| 复杂度 | 较低 | 较高 |
| 扩展性 | 易于扩展新产品 | 难以扩展新产品等级 |

***

## 五、相关文档

- [设计模式概述](./01-abstract-factory-overview.md)
