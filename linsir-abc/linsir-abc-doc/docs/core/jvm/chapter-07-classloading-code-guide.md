# 第7章 类加载机制 - 代码说明文档

## 一、代码结构

```
com.linsir.abc.core.jvm.classloading/
├── initialization/              # 7.2 类加载时机 - 被动引用示例
│   ├── SuperClass.java          # 父类，用于演示被动引用
│   ├── SubClass.java            # 子类，继承SuperClass
│   ├── ConstClass.java          # 常量类，演示编译期常量
│   └── PassiveReferenceDemo.java # 被动引用演示主类
├── process/                     # 7.3 类加载过程
│   └── ClinitDemo.java          # <clinit>()方法演示
├── loader/                      # 7.4 类加载器
│   ├── ClassLoaderHierarchy.java # 类加载器层次结构演示
│   └── CustomClassLoader.java   # 自定义类加载器
├── spi/                         # 7.4 SPI机制
│   ├── DataSourceService.java   # SPI服务接口
│   ├── MySQLDataSource.java     # MySQL数据源实现
│   ├── OracleDataSource.java    # Oracle数据源实现
│   └── SPIDemo.java             # SPI机制演示
├── ClassLoadingTest.java        # 统一测试入口
└── META-INF/services/           # SPI配置文件目录
    └── com.linsir.abc.core.jvm.classloading.spi.DataSourceService
```

## 二、代码作用

### 1. initialization包 - 类加载时机演示

#### SuperClass.java
- **作用**: 父类，用于演示被动引用场景
- **关键特性**:
  - 静态代码块输出初始化信息
  - 静态字段 `value = 123`
  - 静态常量 `CONSTANT`（编译期常量）

#### SubClass.java
- **作用**: 子类，继承SuperClass
- **关键特性**:
  - 静态代码块输出初始化信息
  - 子类特有的静态字段和常量
  - 用于演示通过子类引用父类静态字段时子类不初始化

#### ConstClass.java
- **作用**: 演示编译期常量与运行期常量的区别
- **关键特性**:
  - `HELLO_WORLD`: 编译期常量（String字面量）
  - `MAX_VALUE`: 编译期常量（基本类型）
  - `RUNTIME_CONSTANT`: 运行期常量（System.currentTimeMillis()）
  - `staticField`: 静态字段（非final）

#### PassiveReferenceDemo.java
- **作用**: 被动引用示例的主演示类
- **包含示例**:
  1. 通过子类引用父类的静态字段
  2. 通过数组定义来引用类
  3. 常量在编译阶段存入调用类的常量池
  4. 运行期常量会触发类初始化
  5. 引用子类的静态字段会触发子类初始化
  6. 创建子类实例会触发父子类初始化

### 2. process包 - 类加载过程演示

#### ClinitDemo.java
- **作用**: 演示<clinit>()方法的执行特点
- **内部类**:
  - `StaticOrderDemo`: 演示静态变量赋值顺序
  - `Parent/Child`: 演示父类静态初始化优先
  - `InterfaceA/InterfaceB/InterfaceImpl`: 演示接口初始化
  - `DeadLoopClass`: 演示<clinit>()的线程安全性
- **关键特性**:
  - 静态代码块按源文件顺序执行
  - 父类<clinit>()优先于子类执行
  - <clinit>()方法线程安全

### 3. loader包 - 类加载器演示

#### ClassLoaderHierarchy.java
- **作用**: 演示三层类加载器层次结构
- **包含内容**:
  - 启动类加载器（Bootstrap ClassLoader）
  - 扩展类加载器（Extension/Platform ClassLoader）
  - 应用程序类加载器（Application ClassLoader）
  - 双亲委派模型工作原理

#### CustomClassLoader.java
- **作用**: 自定义类加载器实现
- **关键特性**:
  - 支持打破双亲委派模型
  - 从指定路径加载类文件
  - 演示不同类加载器加载的类不相等
- **方法**:
  - `loadClass()`: 重写加载逻辑
  - `findClass()`: 从文件系统加载类

### 4. spi包 - SPI机制演示

#### DataSourceService.java
- **作用**: SPI服务接口定义
- **方法**:
  - `getDataSourceName()`: 获取数据源名称
  - `getConnection()`: 获取连接字符串
  - `testConnection()`: 测试连接

#### MySQLDataSource.java / OracleDataSource.java
- **作用**: SPI服务实现类
- **功能**: 分别实现MySQL和Oracle数据源

#### SPIDemo.java
- **作用**: SPI机制演示主类
- **包含内容**:
  - 基本SPI加载机制
  - 线程上下文类加载器
  - 自定义上下文类加载器
  - SPI工作原理说明

### 5. ClassLoadingTest.java
- **作用**: 统一测试入口
- **功能**: 运行所有类加载机制的测试用例

## 三、测试方法

### 1. 编译代码

```bash
cd linsir-abc/linsir-abc-core
mvn compile -q
```

### 2. 运行统一测试

```bash
java -cp target/classes com.linsir.abc.core.jvm.classloading.ClassLoadingTest
```

### 3. 单独运行各模块测试

#### 7.2 被动引用示例
```bash
java -cp target/classes com.linsir.abc.core.jvm.classloading.initialization.PassiveReferenceDemo
```

#### 7.3 <clinit>()方法
```bash
java -cp target/classes com.linsir.abc.core.jvm.classloading.process.ClinitDemo
```

#### 7.4 类加载器层次结构
```bash
java -cp target/classes com.linsir.abc.core.jvm.classloading.loader.ClassLoaderHierarchy
```

#### 7.4 自定义类加载器
```bash
java -cp target/classes com.linsir.abc.core.jvm.classloading.loader.CustomClassLoader
```

#### 7.4 SPI机制
```bash
java -cp target/classes com.linsir.abc.core.jvm.classloading.spi.SPIDemo
```

## 四、代码执行预期结果

### 1. 被动引用示例预期结果

#### 示例1：通过子类引用父类的静态字段
```
=== 示例1：通过子类引用父类的静态字段 ===
预期：只输出 'SuperClass init!'，不输出 'SubClass init!'
--- 开始测试 ---
SuperClass init!
SubClass.value = 123
--- 测试结束 ---
结论：对于静态字段，只有直接定义这个字段的类才会被初始化
```

#### 示例2：通过数组定义来引用类
```
=== 示例2：通过数组定义来引用类 ===
预期：不输出 'SuperClass init!'
--- 开始测试 ---
数组类型: [Lcom.linsir.abc.core.jvm.classloading.initialization.SuperClass;
数组长度: 10
--- 测试结束 ---
结论：数组类型由虚拟机自动生成，继承自java.lang.Object，不会触发元素类型的初始化
```

#### 示例3：常量在编译阶段存入调用类的常量池
```
=== 示例3：常量在编译阶段存入调用类的常量池 ===
预期：不输出 'ConstClass init!'
--- 开始测试 ---
ConstClass.HELLO_WORLD = hello world
ConstClass.MAX_VALUE = 100
--- 测试结束 ---
结论：编译期常量已存入当前类的常量池，本质上没有引用ConstClass
```

#### 示例4：运行期常量会触发类初始化
```
=== 示例4：运行期常量会触发类初始化 ===
预期：输出 'ConstClass init!'
--- 开始测试 ---
ConstClass init!
ConstClass.RUNTIME_CONSTANT = 1709107200000
--- 测试结束 ---
结论：运行期常量会触发类的初始化
```

#### 示例5：引用子类的静态字段会触发子类初始化
```
=== 示例5：引用子类的静态字段会触发子类初始化 ===
预期：先输出 'SuperClass init!'，再输出 'SubClass init!'
--- 开始测试 ---
SuperClass init!
SubClass init!
SubClass.subValue = 456
--- 测试结束 ---
结论：当初始化子类时，如果发现其父类还没有初始化，则会先触发父类的初始化
```

#### 示例6：创建子类实例会触发父子类初始化
```
=== 示例6：创建子类实例会触发父子类初始化 ===
预期：先输出 'SuperClass init!'，再输出 'SubClass init!'，最后输出构造方法信息
--- 开始测试 ---
SuperClass init!
SubClass init!
SubClass constructor executed!
创建实例: com.linsir.abc.core.jvm.classloading.initialization.SubClass
--- 测试结束 ---
结论：使用new关键字实例化对象时，会触发类的初始化
```

### 2. <clinit>()方法预期结果

#### 静态变量赋值顺序
```
=== 演示静态变量赋值顺序 ===
--- 开始测试 ---
StaticOrderDemo static block, value = 1
StaticOrderDemo.value = 2
--- 测试结束 ---
结论：静态代码块按源文件中出现的顺序执行
```

#### 父类静态初始化优先
```
=== 演示父类静态初始化优先 ===
--- 开始测试 ---
Parent static block
Child static block, B = 2
Child.B = 2
--- 测试结束 ---
结论：父类的<clinit>()方法优先于子类的<clinit>()方法执行
```

#### 接口初始化
```
=== 演示接口初始化 ===
--- 开始测试 ---
InterfaceImpl static block
InterfaceImpl.C = 3
--- 测试结束 ---
结论：实现类初始化时，接口也会初始化
```

### 3. 类加载器层次结构预期结果

```
=== 类加载器层次结构 ===
--- 开始测试 ---
当前类加载器: jdk.internal.loader.ClassLoaders$AppClassLoader@55f96302
父类加载器: jdk.internal.loader.ClassLoaders$PlatformClassLoader@3d646c37
祖父类加载器: null

类加载器层次结构:
  Bootstrap ClassLoader (启动类加载器)
       ↑
  Extension ClassLoader (扩展类加载器)
       ↑
  Application ClassLoader (应用程序类加载器)
       ↑
  Custom ClassLoader (自定义类加载器)
--- 测试结束 ---
```

### 4. 不同类加载器加载的类不相等预期结果

```
=== 演示不同类加载器加载的类不相等 ===
--- 开始测试 ---
obj.getClass(): class com.linsir.abc.core.jvm.classloading.loader.CustomClassLoader
obj.getClass().getClassLoader(): jdk.internal.loader.ClassLoaders$AppClassLoader@55f96302
obj instanceof CustomClassLoader: true
--- 测试结束 ---
结论：即使来源于同一个Class文件，不同类加载器加载的类也不相等
```

### 5. SPI机制预期结果

```
=== 演示基本的SPI加载机制 ===
--- 开始测试 ---
发现的服务实现:
  1. com.linsir.abc.core.jvm.classloading.spi.MySQLDataSource
     数据源名称: MySQL
     连接字符串: jdbc:mysql://localhost:3306/test
     类加载器: jdk.internal.loader.ClassLoaders$AppClassLoader@55f96302
  2. com.linsir.abc.core.jvm.classloading.spi.OracleDataSource
     数据源名称: Oracle
     连接字符串: jdbc:oracle:thin:@localhost:1521:ORCL
     类加载器: jdk.internal.loader.ClassLoaders$AppClassLoader@55f96302
--- 测试结束 ---
```

### 6. 线程上下文类加载器预期结果

```
=== 演示线程上下文类加载器 ===
--- 开始测试 ---
当前线程上下文类加载器: jdk.internal.loader.ClassLoaders$AppClassLoader@55f96302
系统类加载器: jdk.internal.loader.ClassLoaders$AppClassLoader@55f96302
两者是否相同: true

线程上下文类加载器的作用:
  1. SPI机制中，接口由启动类加载器加载
  2. 实现类在classpath中，由应用程序类加载器加载
  3. 启动类加载器无法加载classpath中的类
  4. 通过线程上下文类加载器打破双亲委派模型
--- 测试结束 ---
```

## 五、关键知识点总结

### 1. 类加载时机
- **主动引用**（会触发初始化）：
  - new实例化对象
  - 读取或设置静态字段（final编译期常量除外）
  - 调用静态方法
  - 反射调用
  - 初始化子类时父类未初始化
  - 执行main()方法的类

- **被动引用**（不会触发初始化）：
  - 通过子类引用父类静态字段
  - 定义数组类型
  - 引用编译期常量

### 2. 类加载过程
- **加载**：读取二进制字节流，生成Class对象
- **验证**：验证字节流安全性
- **准备**：为静态变量分配内存并设置默认值
- **解析**：符号引用转直接引用
- **初始化**：执行<clinit>()方法

### 3. <clinit>()方法特点
- 由编译器自动收集静态变量和静态代码块
- 按源文件顺序执行
- 父类<clinit>()优先执行
- 接口也有<clinit>()（但实现类初始化不强制要求接口初始化）
- 多线程安全（只有一个线程执行）

### 4. 类加载器
- **启动类加载器**：加载<JAVA_HOME>\lib下的类
- **扩展类加载器**：加载<JAVA_HOME>\lib\ext下的类
- **应用程序类加载器**：加载classpath下的类
- **双亲委派模型**：先委托父类加载器加载

### 5. 打破双亲委派模型
- **SPI机制**：使用线程上下文类加载器
- **OSGi**：模块化热部署
- **Tomcat**：Web应用隔离

### 6. SPI机制
- 服务接口由启动类加载器加载
- 服务实现由应用程序类加载器加载
- 通过线程上下文类加载器解决加载问题
- 配置文件路径：META-INF/services/接口全限定名
