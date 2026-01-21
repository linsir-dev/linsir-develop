# Java 泛型示例

本目录包含 Java 泛型的示例代码，展示了泛型的核心概念和使用方法。

## 目录结构
```
generic/
├── GenericClass.java         // 泛型类示例
├── GenericInterface.java     // 泛型接口示例
├── GenericInterfaceImpl.java // 泛型接口实现类
├── GenericMethod.java        // 泛型方法示例
└── GenericTest.java          // 测试类
```

## 示例代码功能

### 1. 泛型类 (`GenericClass.java`)
- 演示了如何定义泛型类
- 展示了类型参数 `T` 的使用
- 包含构造方法、getter/setter 方法和类型显示方法

### 2. 泛型接口 (`GenericInterface.java`)
- 演示了如何定义泛型接口
- 展示了类型参数 `T` 在接口中的使用

### 3. 泛型接口实现 (`GenericInterfaceImpl.java`)
- 演示了如何实现泛型接口
- 展示了在实现时指定具体类型（String）

### 4. 泛型方法 (`GenericMethod.java`)
- 演示了如何定义泛型方法
- 展示了类型边界的使用（`T extends Comparable<T>`）
- 包含两个示例方法：获取最大值和打印值及类型

### 5. 测试类 (`GenericTest.java`)
- 演示了所有泛型示例的使用方法
- 包括泛型类的实例化（使用不同类型）
- 泛型接口实现类的使用
- 泛型方法的调用

## Java 泛型的主要特征
1. **类型参数化**：使用 `<T>` 等形式定义类型参数
2. **类型安全**：在编译时检查类型，避免运行时类型转换错误
3. **代码复用**：通过泛型可以编写适用于多种类型的通用代码
4. **类型擦除**：运行时泛型信息被擦除，使用原始类型
5. **类型边界**：可以限制类型参数的范围（如 `extends Comparable<T>`）

## 运行示例
执行 `GenericTest.java` 类的 `main` 方法，即可看到完整的泛型示例运行结果。
