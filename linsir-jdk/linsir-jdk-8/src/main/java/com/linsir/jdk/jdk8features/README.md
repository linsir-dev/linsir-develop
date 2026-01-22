# JDK 8 特征示例代码

## 目录结构

```
jdk8features/
├── AnonymousClassDemo.java              # 匿名类简写示例
├── ForEachLambdasDemo.java              # forEach Lambdas示例
├── MethodReferencesDemo.java            # 方法引用示例
├── FilterPredicateDemo.java             # Filter & Predicate常规用法
├── MapReduceDemo.java                   # Map&Reduce常规用法
├── CollectorsDemo.java                  # Collectors常规用法
├── FlatMapDemo.java                     # flatMap常规用法
├── DistinctDemo.java                    # distinct常规用法
├── CountDemo.java                       # count常规用法
├── MatchDemo.java                       # Match示例（anyMatch、allMatch、noneMatch）
├── MinMaxSummaryDemo.java               # min, max, summaryStatistics示例
├── PeekDemo.java                        # peek示例
├── FunctionalInterfaceDemo.java         # FunctionalInterface示例
├── BuiltInFunctionalInterfacesDemo.java # 内置四大函数接口示例
├── Jdk8FeaturesTest.java                # 综合测试类
└── README.md                            # 说明文档
```

## 示例代码说明

### 1. AnonymousClassDemo.java

演示JDK 8中匿名类的简写方式，包括：
- 传统匿名类的写法
- Lambda表达式简写方式
- 带参数的Lambda表达式

### 2. ForEachLambdasDemo.java

演示forEach结合Lambda表达式的用法，包括：
- 传统for循环
- 增强for循环
- forEach + Lambda表达式
- forEach + 方法引用

### 3. MethodReferencesDemo.java

演示方法引用的四种类型，包括：
- 构造引用：`ClassName::new`
- 对象::实例方法
- 类名::静态方法
- 类名::实例方法

### 4. FilterPredicateDemo.java

演示Filter和Predicate的常规用法，包括：
- 使用filter过滤元素
- 使用Predicate接口定义条件
- 组合多个Predicate条件

### 5. MapReduceDemo.java

演示Map和Reduce的常规用法，包括：
- 使用map转换元素
- 使用reduce求和
- 使用reduce求积
- 使用reduce求最大值

### 6. CollectorsDemo.java

演示Collectors的常规用法，包括：
- 收集到List
- 收集到Set（去重）
- 连接字符串
- 分组
- 统计

### 7. FlatMapDemo.java

演示flatMap的常规用法，包括：
- 扁平化嵌套列表
- 处理字符串并扁平化

### 8. DistinctDemo.java

演示distinct的常规用法，包括：
- 对数字列表去重
- 对字符串列表去重

### 9. CountDemo.java

演示count的常规用法，包括：
- 统计元素总数量
- 统计满足条件的元素数量

### 10. MatchDemo.java

演示Match操作的用法，包括：
- anyMatch：任意匹配
- allMatch：全部匹配
- noneMatch：无匹配

### 11. MinMaxSummaryDemo.java

演示min、max和summaryStatistics的用法，包括：
- 查找最小值
- 查找最大值
- 生成统计摘要

### 12. PeekDemo.java

演示peek的用法，包括：
- 使用peek调试Stream操作

### 13. FunctionalInterfaceDemo.java

演示函数式接口的用法，包括：
- 使用内置函数接口
- 使用自定义函数接口

### 14. BuiltInFunctionalInterfacesDemo.java

演示内置四大函数接口的用法，包括：
- Function<T, R>：接收T类型参数，返回R类型结果
- Consumer<T>：接收T类型参数，无返回值
- Supplier<T>：无参数，返回T类型结果
- Predicate<T>：接收T类型参数，返回boolean值

### 15. Jdk8FeaturesTest.java

综合测试类，运行所有示例代码。

## 运行测试

运行Jdk8FeaturesTest类的main方法，可以执行所有示例代码：

```bash
# 在linsir-jdk-8目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.jdk8features.Jdk8FeaturesTest"
```

## JDK 8 核心特征总结

### 1. Lambda表达式
- 允许将函数作为方法参数
- 简化匿名内部类的写法
- 语法：`(参数) -> 表达式` 或 `(参数) -> { 语句; }`

### 2. 方法引用
- 简化Lambda表达式的写法
- 四种类型：构造引用、对象::实例方法、类名::静态方法、类名::实例方法

### 3. Stream API
- 提供函数式风格的集合处理
- 支持链式操作
- 支持并行处理

### 4. 函数式接口
- 只有一个抽象方法的接口
- 使用@FunctionalInterface注解标记
- 内置四大函数接口：Function、Consumer、Supplier、Predicate

### 5. 其他改进
- Optional类：避免空指针异常
- 日期时间API：更简洁、更安全的日期时间处理
- 接口默认方法和静态方法：增强接口的能力

通过本示例代码，您可以全面了解JDK 8的主要特征和使用方法，为实际项目开发提供参考。