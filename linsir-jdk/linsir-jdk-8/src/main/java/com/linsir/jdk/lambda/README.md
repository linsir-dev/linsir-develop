# Lambda表达式示例代码

## Lambda表达式概念

Lambda表达式是JDK 8引入的重要特性，它允许我们以更简洁的方式编写函数式代码，提高了代码的可读性和可维护性。Lambda表达式是一种匿名函数，它没有名称，但有参数列表、函数体和返回类型（可选）。

## Lambda表达式语法

Lambda表达式的基本语法结构如下：

```java
(parameters) -> expression
或
(parameters) -> { statements; }
```

## 示例代码说明

本目录包含以下示例代码：

1. **LambdaBasicDemo.java**：基本使用示例，展示了无参数、单个参数、多个参数、代码块等Lambda表达式用法。
2. **LambdaCollectionDemo.java**：集合操作示例，展示了遍历、过滤、映射、排序、聚合等操作。
3. **LambdaThreadDemo.java**：线程创建和自定义函数式接口示例。
4. **LambdaTest.java**：测试类，用于运行所有示例代码。

## 运行测试

运行LambdaTest类的main方法，可以测试所有Lambda表达式示例代码的功能：

```bash
# 在linsir-jdk-8目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.lambda.LambdaTest"
```

## 功能展示

Lambda表达式的主要功能点包括：

1. **简化代码**：相比匿名内部类，Lambda表达式的代码更加简洁明了。
2. **函数式编程**：促进了函数式编程风格，使代码更加模块化和可测试。
3. **集合操作**：与Stream API结合使用，可以方便地进行集合操作。
4. **线程创建**：简化了线程创建的代码。
5. **自定义函数式接口**：可以与自定义的函数式接口配合使用。
6. **方法引用**：可以使用方法引用进一步简化代码。

## 注意事项

- Lambda表达式需要与函数式接口配合使用。
- Lambda表达式可以访问外部变量，但这些变量必须是final或effectively final。
- 对于简单的表达式，可以省略return关键字和大括号。
- 对于复杂的逻辑，需要使用大括号和return关键字。