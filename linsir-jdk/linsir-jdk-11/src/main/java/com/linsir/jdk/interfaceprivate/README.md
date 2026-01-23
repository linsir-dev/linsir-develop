# 接口中使用私有方法示例

## 概述

本目录包含JDK 9+中接口使用私有方法的示例代码。JDK 9引入了在接口中定义私有方法和私有静态方法的功能，这使得接口中的默认方法和静态方法可以共享代码，提高了代码的可维护性。

## 目录结构

```
interfaceprivate/
├── InterfaceWithPrivateMethods.java  # 包含私有方法的接口
├── InterfaceImpl.java                # 接口的实现类
├── InterfacePrivateMethodsTest.java  # 测试类
└── README.md                         # 说明文档
```

## 接口中私有方法的特性

### JDK 9之前的限制
- 接口中只能有抽象方法、默认方法和静态方法
- 默认方法和静态方法中的代码无法在多个方法之间共享

### JDK 9的改进
- 引入了私有方法和私有静态方法
- 私有方法只能在接口内部被默认方法调用
- 私有静态方法只能在接口内部被静态方法调用
- 实现类无法访问接口中的私有方法

## 示例代码说明

### 1. InterfaceWithPrivateMethods.java

定义了一个包含私有方法的接口，包括：

- **抽象方法**：`abstractMethod()`
- **默认方法**：`defaultMethod1()`、`defaultMethod2()`
- **静态方法**：`staticMethod1()`、`staticMethod2()`
- **私有方法**：`privateHelperMethod()`、`privateHelperMethodWithParam(String)`
- **私有静态方法**：`privateStaticHelperMethod()`、`privateStaticHelperMethodWithParam(String)`

### 2. InterfaceImpl.java

实现了InterfaceWithPrivateMethods接口，展示了：

- 实现抽象方法的方式
- 无法访问接口中私有方法的限制（注释掉的代码会编译错误）

### 3. InterfacePrivateMethodsTest.java

测试类，运行所有方法，展示：

- 抽象方法的调用
- 默认方法的调用（会触发私有方法的执行）
- 静态方法的调用（会触发私有静态方法的执行）

## 运行测试

运行InterfacePrivateMethodsTest类的main方法，可以测试所有功能：

```bash
# 在linsir-jdk-11目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.interfaceprivate.InterfacePrivateMethodsTest"
```

## 私有方法的使用场景

### 适合使用私有方法的场景

1. **代码复用**：当多个默认方法需要共享相同的代码逻辑时
2. **逻辑封装**：将复杂的业务逻辑封装在私有方法中，提高代码可读性
3. **避免重复**：避免在多个默认方法或静态方法中重复相同的代码
4. **实现细节隐藏**：将实现细节隐藏在接口内部，不对外暴露

### 示例场景

- **验证逻辑**：多个默认方法需要相同的参数验证逻辑
- **数据转换**：多个方法需要相同的数据转换逻辑
- **日志记录**：多个方法需要相同的日志记录逻辑
- **计算逻辑**：多个方法需要相同的计算逻辑

## 注意事项

1. **访问限制**：私有方法只能在接口内部被调用，实现类无法访问
2. **JDK版本**：私有方法是JDK 9+的特性，在JDK 8及以下版本中无法使用
3. **方法签名**：私有方法的签名规则与普通方法相同
4. **使用原则**：只在确实需要共享代码时使用私有方法，避免过度使用

## 代码优势

1. **提高代码可维护性**：通过代码复用，减少了重复代码
2. **提高代码可读性**：将复杂逻辑封装在私有方法中，使默认方法和静态方法更简洁
3. **实现细节隐藏**：接口的实现细节不对外暴露，只暴露必要的方法
4. **符合封装原则**：将相关的代码逻辑封装在一起，提高代码的内聚性

通过本示例代码，您可以了解JDK 9+中接口私有方法的使用方式和优势，为实际项目中的接口设计提供参考。