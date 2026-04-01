# 代理模式 - 代码指南

> 本文档详细说明代理模式的代码实现和使用方法

***

## 一、项目结构

```
proxy/
├── Image.java           # 接口
├── RealImage.java       # 真实对象
└── ProxyImage.java      # 代理对象
```

***

## 二、代码详解

### 2.1 Image - 接口

```java
package com.linsir.designpattern.proxy;

public interface Image {
    void display();
}
```

---

### 2.2 RealImage - 真实对象

```java
package com.linsir.designpattern.proxy;

public class RealImage implements Image {
    private String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
        loadFromDisk();
    }

    private void loadFromDisk() {
        System.out.println("加载图片: " + fileName);
    }

    @Override
    public void display() {
        System.out.println("显示图片: " + fileName);
    }
}
```

**说明**：
- 构造函数中加载图片（开销大）
- 实现display()方法显示图片

---

### 2.3 ProxyImage - 代理对象

```java
package com.linsir.designpattern.proxy;

public class ProxyImage implements Image {
    private RealImage realImage;
    private String fileName;

    public ProxyImage(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void display() {
        if (realImage == null) {
            realImage = new RealImage(fileName);
        }
        realImage.display();
    }
}
```

**说明**：
- 持有RealImage的引用
- 延迟加载：第一次调用display()时才创建RealImage
- 控制对RealImage的访问

***

## 三、使用示例

```java
public class ProxyTest {
    public static void main(String[] args) {
        // 创建代理对象
        Image image = new ProxyImage("test.jpg");

        // 第一次调用：加载并显示
        System.out.println("第一次调用:");
        image.display();

        // 第二次调用：直接显示
        System.out.println("\n第二次调用:");
        image.display();
    }
}
```

**输出**：
```
第一次调用:
加载图片: test.jpg
显示图片: test.jpg

第二次调用:
显示图片: test.jpg
```

***

## 四、代理模式的应用场景

1. **虚拟代理**：延迟加载大对象
2. **保护代理**：控制访问权限
3. **远程代理**：访问远程对象
4. **智能引用**：在访问对象时执行额外操作（如计数、缓存等）

***

## 五、相关文档

- [设计模式概述](./01-proxy-overview.md)
