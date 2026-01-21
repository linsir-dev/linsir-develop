# 线程创建方式示例

## 功能说明

本示例代码展示了Java中创建线程的三种主要方式：

1. **继承Thread类**：通过继承Thread类并重写run()方法来创建线程
2. **实现Runnable接口**：通过实现Runnable接口的run()方法来创建线程
3. **实现Callable接口**：通过实现Callable接口的call()方法来创建线程，支持返回值和异常抛出

同时还展示了使用lambda表达式简化线程创建的方法。

## 示例代码结构

- **ThreadCreationDemo.java**：包含五个部分的示例代码：
  - **MyThread**：继承Thread类的示例
  - **MyRunnable**：实现Runnable接口的示例
  - **MyCallable**：实现Callable接口的示例
  - **testLambdaRunnable**：使用lambda表达式实现Runnable的示例
  - **testLambdaCallable**：使用lambda表达式实现Callable的示例

## 运行说明

1. 编译并运行ThreadCreationDemo.java文件
2. 观察控制台输出，了解不同线程创建方式的执行流程
3. 注意三种线程创建方式的区别：
   - 继承Thread类：直接创建线程对象，重写run()方法
   - 实现Runnable接口：创建Runnable对象，作为参数传递给Thread构造器
   - 实现Callable接口：创建Callable对象，包装为FutureTask，再传递给Thread构造器
4. 注意Callable与Runnable的区别：
   - Callable的call()方法可以返回值，Runnable的run()方法不能
   - Callable的call()方法可以抛出异常，Runnable的run()方法不能
   - Callable需要通过FutureTask获取执行结果

## 核心知识点

- **Thread类**：Java中表示线程的类，每个Thread对象代表一个线程
- **Runnable接口**：定义了线程执行的任务，只有一个run()方法
- **Callable接口**：类似Runnable，但支持返回值和异常抛出，有一个call()方法
- **FutureTask**：实现了RunnableFuture接口，用于包装Callable对象，获取执行结果
- **lambda表达式**：Java 8+的特性，可以简化接口实现的代码

## 示例场景

- **MyThread**：展示了通过继承Thread类创建线程的基本方式
- **MyRunnable**：展示了通过实现Runnable接口创建线程的方式，适合资源共享的场景
- **MyCallable**：展示了通过实现Callable接口创建线程的方式，适合需要返回结果的场景
- **lambda Runnable**：展示了使用lambda表达式简化Runnable实现的方式
- **lambda Callable**：展示了使用lambda表达式简化Callable实现的方式

## 优缺点对比

| 创建方式 | 优点 | 缺点 |
|---------|------|------|
| 继承Thread类 | 代码简单，直接调用start()方法 | 不能继承其他类（Java单继承） |
| 实现Runnable接口 | 可以继承其他类，适合资源共享 | 不能返回结果，不能抛出受检异常 |
| 实现Callable接口 | 可以返回结果，可以抛出异常 | 代码相对复杂，需要FutureTask包装 |

## 最佳实践

- 推荐使用实现Runnable接口的方式创建线程，避免单继承的限制
- 当需要返回结果或处理异常时，使用实现Callable接口的方式
- 在Java 8+中，推荐使用lambda表达式简化代码
- 对于复杂的线程池操作，推荐使用ExecutorService框架