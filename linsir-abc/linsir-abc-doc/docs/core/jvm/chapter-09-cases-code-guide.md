# 第9章 虚拟机字节码执行引擎 - 代码说明文档

## 一、代码结构

### 1.1 整体架构

```
jvm/remote/
├── exception/                          # 异常定义包
│   ├── CompileException.java          # 编译异常
│   └── ExecuteException.java          # 执行异常
├── model/                             # 数据模型包
│   ├── ExecuteRequest.java            # 执行请求
│   └── ExecuteResult.java             # 执行结果
├── compiler/                          # 编译器包
│   └── DynamicCompiler.java           # 动态Java编译器
├── classloader/                       # 类加载器包
│   └── HotSwapClassLoader.java        # 热替换类加载器
├── context/                           # 上下文包
│   └── ExecuteContext.java            # 执行上下文
├── server/                            # 服务端包
│   └── RemoteExecuteServer.java       # 远程执行服务器
└── client/                            # 客户端包
    └── RemoteExecuteClient.java       # 远程执行客户端
```

### 1.2 类关系图

```
RemoteExecuteClient  --->  ExecuteRequest  --->  RemoteExecuteServer
        |                                              |
        |                                              v
        |                                    DynamicCompiler
        |                                              |
        |                                              v
        |                                    HotSwapClassLoader
        |                                              |
        |                                              v
        |                                    ExecuteContext
        |                                              |
        v                                              v
   ExecuteResult  <--------------------------- 执行结果返回
```

## 二、代码作用

### 2.1 异常类

#### CompileException.java
- **作用**：在动态编译过程中发生错误时抛出
- **使用场景**：
  - Java源代码语法错误
  - 编译器无法找到依赖类
  - 其他编译相关错误

#### ExecuteException.java
- **作用**：在远程代码执行过程中发生错误时抛出
- **使用场景**：
  - 代码执行超时
  - 代码抛出运行时异常
  - 安全管理器阻止执行

### 2.2 数据模型

#### ExecuteRequest.java
- **作用**：封装客户端发送到服务器的执行请求数据
- **核心属性**：
  - `className`：类名（全限定名）
  - `sourceCode`：Java源代码
  - `contextObjects`：上下文对象映射

#### ExecuteResult.java
- **作用**：封装服务器执行代码后的返回结果
- **核心属性**：
  - `success`：执行是否成功
  - `output`：标准输出内容
  - `result`：返回值
  - `exception`：异常信息

### 2.3 核心组件

#### DynamicCompiler.java - 动态Java编译器
- **功能**：将Java源代码字符串编译为字节码
- **核心特性**：
  - 在内存中完成编译，不生成文件
  - 支持自定义编译选项
  - 提供编译诊断信息
  - 支持批量编译
- **关键方法**：
  - `compile(String className, String sourceCode)`：编译单个类
  - `compileBatch(Map<String, String> sources)`：批量编译

#### HotSwapClassLoader.java - 热替换类加载器
- **功能**：加载动态编译的类，支持类的热替换
- **核心特性**：
  - 每个实例加载独立的类，实现隔离
  - 支持从内存字节码加载类
  - 优先加载本地类，打破双亲委派
- **关键方法**：
  - `addClass(String className, byte[] bytecode)`：添加类字节码
  - `loadClass(String name)`：加载类（优先本地）
  - `clear()`：清理资源

#### ExecuteContext.java - 执行上下文
- **功能**：提供执行时的输入输出流和上下文对象传递
- **核心特性**：
  - 捕获执行期间的输出
  - 传递上下文对象（如Spring容器、数据库连接等）
  - 管理执行参数
- **关键方法**：
  - `redirectSystemOut()`：重定向系统输出
  - `restoreSystemOut()`：恢复系统输出
  - `addContextObject()`：添加上下文对象
  - `getContextObject()`：获取上下文对象

#### RemoteExecuteServer.java - 远程执行服务器
- **功能**：接收客户端发送的Java代码，编译执行后返回结果
- **核心特性**：
  - 支持多客户端并发连接
  - 使用线程池处理请求
  - 每个请求使用独立的类加载器，实现代码隔离
  - 支持上下文对象传递
- **关键方法**：
  - `start()`：启动服务器
  - `stop()`：停止服务器

#### RemoteExecuteClient.java - 远程执行客户端
- **功能**：向服务器发送Java代码并获取执行结果
- **核心特性**：
  - 支持发送Java源代码到远程服务器执行
  - 支持传递上下文对象
  - 自动提取类名
  - 支持超时设置
- **关键方法**：
  - `execute(String sourceCode)`：执行代码（自动提取类名）
  - `execute(String className, String sourceCode, Map<String, Object> contextObjects)`：完整执行

## 三、测试方法

### 3.1 测试类位置

```
src/test/java/com/linsir/abc/core/jvm/remote/RemoteExecuteTest.java
```

### 3.2 测试用例说明

#### 3.2.1 动态编译器测试

| 测试方法 | 测试内容 | 预期结果 |
|---------|---------|---------|
| `testDynamicCompilerSimple` | 编译简单类 | 编译成功，返回字节码数组 |
| `testDynamicCompilerWithExecute` | 编译包含execute方法的类 | 编译成功，字节码有效 |
| `testDynamicCompilerError` | 编译错误代码 | 抛出CompileException |
| `testDynamicCompilerBatch` | 批量编译多个类 | 所有类编译成功 |

#### 3.2.2 热替换类加载器测试

| 测试方法 | 测试内容 | 预期结果 |
|---------|---------|---------|
| `testHotSwapClassLoaderSingle` | 加载单个类 | 类加载成功，可正常调用方法 |
| `testHotSwapClassLoaderIsolation` | 类隔离测试 | 不同加载器加载的类不相同 |
| `testHotSwapClassLoaderSystemClass` | 系统类委托 | 系统类由父类加载器加载 |

#### 3.2.3 执行上下文测试

| 测试方法 | 测试内容 | 预期结果 |
|---------|---------|---------|
| `testExecuteContextOutput` | 输出捕获 | System.out输出被正确捕获 |
| `testExecuteContextObjects` | 上下文对象管理 | 对象添加、获取、移除正常 |
| `testExecuteContextParameters` | 参数管理 | 参数设置、获取正常 |

#### 3.2.4 远程执行测试

| 测试方法 | 测试内容 | 预期结果 |
|---------|---------|---------|
| `testRemoteExecuteSimple` | 简单代码执行 | 执行成功，返回预期结果 |
| `testRemoteExecuteWithContext` | 带上下文对象执行 | 上下文对象传递成功 |
| `testRemoteExecuteException` | 异常处理 | 异常被正确捕获和返回 |
| `testRemoteExecuteIsolation` | 多次执行隔离 | 每次执行使用独立类加载器 |
| `testRemoteExecuteComputation` | 计算任务执行 | 计算结果正确 |

### 3.3 运行测试

```bash
cd linsir-abc/linsir-abc-core
mvn test -Dtest=RemoteExecuteTest
```

## 四、代码执行预期结果

### 4.1 动态编译器执行结果

```
编译成功，字节码大小: 288 bytes
编译成功，字节码大小: 541 bytes
批量编译成功，共编译 3 个类
```

### 4.2 热替换类加载器执行结果

```
热替换类加载器测试通过
类隔离测试通过
系统类委托测试通过
```

### 4.3 执行上下文执行结果

```
输出捕获测试通过
上下文对象管理测试通过
参数管理测试通过
```

### 4.4 远程执行执行结果

```
远程执行服务器已启动，端口: 19999
接受客户端连接: /127.0.0.1
收到执行请求: RemoteTest1
执行结果: 成功
远程执行测试通过
输出: Remote execution works!

接受客户端连接: /127.0.0.1
收到执行请求: RemoteTest2
执行结果: 成功
带上下文对象的远程执行测试通过

接受客户端连接: /127.0.0.1
收到执行请求: RemoteTest3
执行结果: 失败
异常处理测试通过

接受客户端连接: /127.0.0.1
收到执行请求: Counter
执行结果: 成功
接受客户端连接: /127.0.0.1
收到执行请求: Counter
执行结果: 成功
多次执行隔离测试通过

接受客户端连接: /127.0.0.1
收到执行请求: Calculator
执行结果: 成功
计算任务测试通过
输出: Sum of 1 to 100 = 5050

远程执行服务器已停止
```

### 4.5 完整测试结果

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## 五、使用示例

### 5.1 服务端启动

```java
RemoteExecuteServer server = new RemoteExecuteServer(9999);
server.start();  // 阻塞方法，直到服务器停止
```

### 5.2 客户端执行代码

```java
RemoteExecuteClient client = new RemoteExecuteClient("localhost", 9999);

String sourceCode =
    "import com.linsir.abc.core.jvm.remote.context.ExecuteContext;\n" +
    "public class HelloWorld {\n" +
    "    public Object execute(ExecuteContext context) {\n" +
    "        context.getOut().println(\"Hello, Remote Execution!\");\n" +
    "        return \"Success\";\n" +
    "    }\n" +
    "}";

ExecuteResult result = client.execute(sourceCode);
System.out.println(result.getOutput());
```

### 5.3 带上下文对象执行

```java
Map<String, Object> context = new HashMap<>();
context.put("service", myService);
context.put("config", configMap);

ExecuteResult result = client.execute(sourceCode, context);
```

## 六、注意事项

1. **类加载器隔离**：每个执行请求使用独立的类加载器，确保代码隔离
2. **资源清理**：执行完成后自动清理类加载器，避免内存泄漏
3. **系统类委托**：java.*、javax.*、sun.* 开头的类委托给父类加载器
4. **执行方法约定**：远程执行的类必须包含 `public Object execute(ExecuteContext context)` 方法
5. **JDK要求**：必须使用JDK运行（非JRE），因为需要使用 `javax.tools.JavaCompiler`
