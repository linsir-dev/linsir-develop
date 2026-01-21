# Java 自定义注解示例

本目录包含 Java 自定义注解的示例代码，展示了注解的定义、使用和处理方法。

## 目录结构
```
annotationdemo/
├── LinAnnotation.java         // 类级别的自定义注解
├── LinMethodAnnotation.java   // 方法级别的自定义注解
├── LinFieldAnnotation.java    // 字段级别的自定义注解
├── AnnotationDemo.java        // 使用注解的示例类
├── AnnotationProcessor.java   // 注解处理器
└── AnnotationTest.java        // 测试类
```

## 示例代码功能

### 1. 自定义注解定义

#### 类级别注解 (`LinAnnotation.java`)
- 定义了可应用于类的注解
- 包含 value、name、version 属性
- 保留策略为 RUNTIME，可在运行时访问

#### 方法级别注解 (`LinMethodAnnotation.java`)
- 定义了可应用于方法的注解
- 包含 value、author、date、tags 属性
- 其中 date 属性为必填项

#### 字段级别注解 (`LinFieldAnnotation.java`)
- 定义了可应用于字段的注解
- 包含 value、required、description 属性
- 用于标记字段的验证规则和描述信息

### 2. 注解使用示例 (`AnnotationDemo.java`)
- 演示如何在类、方法和字段上应用自定义注解
- 包含完整的 getter/setter 方法和业务方法
- 所有方法都应用了相应的注解，包含详细的元数据

### 3. 注解处理 (`AnnotationProcessor.java`)
- 通过反射获取和处理注解信息
- 支持处理类级别、字段级别和方法级别的注解
- 展示了如何提取注解的属性值并进行相应处理

### 4. 测试演示 (`AnnotationTest.java`)
- 演示整个注解系统的使用流程
- 包含对象创建、信息显示和注解处理的完整示例
- 展示了注解在实际应用中的价值

## 注解的应用场景
1. **配置类和方法的行为**：通过注解配置程序的运行方式
2. **生成文档**：使用注解生成API文档
3. **运行时反射处理**：通过反射获取注解信息并执行相应逻辑
4. **代码检查和验证**：在编译时或运行时验证代码的正确性
5. **框架配置**：如 Spring 中的 @Component、@Autowired 等注解

## 运行示例
执行 `AnnotationTest.java` 类的 `main` 方法，即可看到完整的注解示例运行结果，包括注解信息的提取和处理过程。
