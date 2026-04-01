# 工厂模式 - 代码指南

> 本文档详细说明工厂模式的代码实现和使用方法

***

## 一、项目结构

```
factory/
├── IWork.java              # 工作接口
├── IWorkFactory.java       # 工厂接口
├── StudentWork.java        # 学生工作实现
├── TeacherWork.java        # 老师工作实现
├── StudentWorkFactory.java # 学生工作工厂
├── TeacherWorkFactory.java # 老师工作工厂
└── WorkManager.java        # 简单工厂
```

***

## 二、代码详解

### 2.1 IWork - 产品接口

```java
package com.linsir.designpattern.factory;

public interface IWork {
    void doWork();
}
```

**说明**：
- 定义产品的统一接口
- 所有具体产品都需要实现这个接口

---

### 2.2 StudentWork - 具体产品

```java
package com.linsir.designpattern.factory;

public class StudentWork implements IWork {
    @Override
    public void doWork() {
        System.out.println("学生做作业");
    }
}
```

---

### 2.3 TeacherWork - 具体产品

```java
package com.linsir.designpattern.factory;

public class TeacherWork implements IWork {
    @Override
    public void doWork() {
        System.out.println("老师批改作业");
    }
}
```

---

### 2.4 IWorkFactory - 工厂接口

```java
package com.linsir.designpattern.factory;

public interface IWorkFactory {
    IWork createWork();
}
```

**说明**：
- 定义工厂的接口
- 所有具体工厂都需要实现这个接口

---

### 2.5 StudentWorkFactory - 具体工厂

```java
package com.linsir.designpattern.factory;

public class StudentWorkFactory implements IWorkFactory {
    @Override
    public IWork createWork() {
        return new StudentWork();
    }
}
```

---

### 2.6 TeacherWorkFactory - 具体工厂

```java
package com.linsir.designpattern.factory;

public class TeacherWorkFactory implements IWorkFactory {
    @Override
    public IWork createWork() {
        return new TeacherWork();
    }
}
```

---

### 2.7 WorkManager - 简单工厂

```java
package com.linsir.designpattern.factory;

public class WorkManager {
    public static IWork getWork(String name) {
        if (name.equals("s")) {
            return new StudentWork();
        } else {
            return new TeacherWork();
        }
    }
}
```

**说明**：
- 静态工厂方法
- 通过参数决定创建哪种产品
- 不符合开闭原则

***

## 三、使用示例

### 3.1 工厂方法模式使用

```java
public class FactoryTest {
    public static void main(String[] args) {
        // 创建学生工作
        IWorkFactory studentFactory = new StudentWorkFactory();
        IWork studentWork = studentFactory.createWork();
        studentWork.doWork(); // 输出：学生做作业

        // 创建老师工作
        IWorkFactory teacherFactory = new TeacherWorkFactory();
        IWork teacherWork = teacherFactory.createWork();
        teacherWork.doWork(); // 输出：老师批改作业
    }
}
```

### 3.2 简单工厂模式使用

```java
public class SimpleFactoryTest {
    public static void main(String[] args) {
        // 创建学生工作
        IWork studentWork = WorkManager.getWork("s");
        studentWork.doWork(); // 输出：学生做作业

        // 创建老师工作
        IWork teacherWork = WorkManager.getWork("t");
        teacherWork.doWork(); // 输出：老师批改作业
    }
}
```

***

## 四、扩展示例

### 4.1 新增产品类型

```java
// 新增工人工作
public class WorkerWork implements IWork {
    @Override
    public void doWork() {
        System.out.println("工人做工");
    }
}

// 新增工人工作工厂
public class WorkerWorkFactory implements IWorkFactory {
    @Override
    public IWork createWork() {
        return new WorkerWork();
    }
}
```

**说明**：
- 新增产品只需新增具体产品和具体工厂
- 不需要修改原有代码
- 符合开闭原则

***

## 五、最佳实践

1. **优先使用工厂方法模式**：符合开闭原则，易于扩展
2. **简单工厂适用于产品类型较少**：产品类型固定且较少时可以使用
3. **结合配置文件**：可以通过配置文件指定工厂类型，实现动态创建

***

## 六、相关文档

- [设计模式概述](./01-factory-overview.md)
