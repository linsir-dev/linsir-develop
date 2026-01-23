# 局部变量类型推断示例

## 概述

本目录包含JDK 10+中局部变量类型推断（var关键字）的示例代码。局部变量类型推断是JDK 10引入的新特性，它允许使用var关键字来声明局部变量，而不需要显式指定类型，编译器会根据初始化值推断变量的类型。

## 目录结构

```
vartypeinference/
├── VarTypeInferenceDemo.java     # 局部变量类型推断的示例代码
├── VarTypeInferenceTest.java     # 测试类
└── README.md                     # 说明文档
```

## 示例代码说明

### 1. VarTypeInferenceDemo.java

演示JDK 10+中局部变量类型推断的各种用法，包括：

- **基本类型的局部变量类型推断**：整数、浮点、布尔、字符、字符串等类型
- **数组类型的局部变量类型推断**：整型数组、字符串数组等
- **集合类型的局部变量类型推断**：List、Set、Map等集合类型
- **循环中的局部变量类型推断**：for循环、增强for循环、forEach循环
- **方法返回值的局部变量类型推断**：使用var接收方法返回值
- **Stream操作中的局部变量类型推断**：使用var接收Stream操作的结果
- **局部变量类型推断的限制**：说明var关键字的使用限制

### 2. VarTypeInferenceTest.java

测试类，运行所有示例方法，展示局部变量类型推断的功能。

## 运行测试

运行VarTypeInferenceTest类的main方法，可以测试所有功能：

```bash
# 在linsir-jdk-11目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.vartypeinference.VarTypeInferenceTest"
```

## 局部变量类型推断的使用场景

### 适合使用var的场景

1. **简化代码**：当变量类型名称较长时，使用var可以简化代码
   ```java
   // 传统写法
   Map<String, List<Map<String, Object>>> complexMap = new HashMap<>();
   
   // 使用var
   var complexMap = new HashMap<String, List<Map<String, Object>>>();
   ```

2. **提高可读性**：当变量类型从初始化值中可以明显看出时，使用var可以提高可读性
   ```java
   // 传统写法
   List<String> names = List.of("Alice", "Bob", "Charlie");
   
   // 使用var
   var names = List.of("Alice", "Bob", "Charlie");
   ```

3. **减少重复**：当变量类型在初始化时已经指定，使用var可以避免重复
   ```java
   // 传统写法
   String message = "Hello, World!";
   
   // 使用var
   var message = "Hello, World!";
   ```

4. **循环变量**：在循环中使用var可以简化代码
   ```java
   // 传统写法
   for (String name : names) {
       System.out.println(name);
   }
   
   // 使用var
   for (var name : names) {
       System.out.println(name);
   }
   ```

5. **Stream操作**：在Stream操作中使用var可以简化代码
   ```java
   // 传统写法
   List<Integer> evenNumbers = numbers.stream()
           .filter(n -> n % 2 == 0)
           .collect(Collectors.toList());
   
   // 使用var
   var evenNumbers = numbers.stream()
           .filter(n -> n % 2 == 0)
           .collect(Collectors.toList());
   ```

### 不适合使用var的场景

1. **类型不明确**：当变量类型从初始化值中无法明显看出时，不建议使用var
   ```java
   // 不推荐
   var result = someMethod(); // 不清楚返回类型
   
   // 推荐
   String result = someMethod(); // 明确返回类型
   ```

2. **需要明确类型**：当需要明确指定类型时，不建议使用var
   ```java
   // 不推荐
   var value = 10; // 可能是int或Integer
   
   // 推荐
   Integer value = 10; // 明确是Integer类型
   ```

3. **代码可读性差**：当使用var会降低代码可读性时，不建议使用var
   ```java
   // 不推荐
   var x = 10;
   var y = 20;
   var z = x + y;
   
   // 推荐
   int x = 10;
   int y = 20;
   int sum = x + y;
   ```

## 局部变量类型推断的限制

1. **只能用于局部变量**：var只能用于局部变量，不能用于字段
   ```java
   // 错误
   // private var field = "value";
   ```

2. **不能用于方法参数**：var不能用于方法参数
   ```java
   // 错误
   // public void method(var param) {}
   ```

3. **不能用于方法返回类型**：var不能用于方法返回类型
   ```java
   // 错误
   // public var method() { return 10; }
   ```

4. **变量必须初始化**：使用var声明的变量必须初始化
   ```java
   // 错误
   // var uninitialized;
   
   // 正确
   var initialized = "value";
   ```

5. **不能用于数组声明的左侧**：var不能用于数组声明的左侧
   ```java
   // 错误
   // var[] array = new int[5];
   
   // 正确
   var array = new int[5];
   ```

6. **JDK 10中不能用于lambda参数**：在JDK 10中，var不能用于lambda参数，但在JDK 11+中可以
   ```java
   // JDK 10中错误，JDK 11+中正确
   // numbers.forEach(var number -> System.out.println(number));
   ```

## 代码优势

1. **简化代码**：减少了重复的类型声明，使代码更简洁
2. **提高可读性**：当类型从上下文可以明显看出时，使用var可以提高可读性
3. **减少维护成本**：当类型发生变化时，只需要修改初始化值，不需要修改变量声明
4. **与现代语言对齐**：与其他现代编程语言（如JavaScript、Kotlin等）的语法更加一致

## 注意事项

1. **JDK版本**：局部变量类型推断是JDK 10+的特性，在JDK 9及以下版本中无法使用
2. **IDE支持**：确保使用的IDE支持JDK 10+的特性
3. **代码风格**：在团队开发中，应该统一使用var的风格，避免混合使用var和显式类型声明
4. **可读性优先**：在使用var时，应该优先考虑代码的可读性，而不是盲目追求简洁

通过本示例代码，您可以了解JDK 10+中局部变量类型推断的使用方式和优势，为实际项目开发提供参考。