# 进程API (Process Handle)示例

## 概述

本目录包含JDK 9+中进程API (Process Handle)的示例代码。Process Handle API是JDK 9引入的新特性，用于管理和监控操作系统进程，提供了获取进程信息、监控进程状态、列出所有进程等功能。

## 目录结构

```
process/
├── ProcessHandleDemo.java     # 进程API的示例代码
├── ProcessHandleTest.java     # 测试类
└── README.md                  # 说明文档
```

## 示例代码说明

### 1. ProcessHandleDemo.java

演示JDK 9+中进程API的各种用法，包括：

- **获取当前进程信息**：获取当前进程的ID、命令、参数、启动时间等信息
- **启动新进程**：使用ProcessBuilder启动新进程，并获取其ProcessHandle
- **监控进程状态**：注册进程终止监听器，监控进程的终止状态
- **列出所有进程**：获取系统中所有进程的信息
- **查找特定进程**：根据进程ID查找特定进程
- **进程树操作**：获取父进程和子进程的信息
- **计算进程运行时间**：计算进程从启动到现在的运行时间
- **终止进程**：启动并终止一个测试进程

### 2. ProcessHandleTest.java

测试类，运行所有示例方法，展示进程API的功能。

## 运行测试

运行ProcessHandleTest类的main方法，可以测试所有功能：

```bash
# 在linsir-jdk-11目录下执行
mvn exec:java -Dexec.mainClass="com.linsir.jdk.process.ProcessHandleTest"
```

## 进程API的核心功能

### 1. ProcessHandle接口

ProcessHandle接口表示一个本地操作系统进程，提供了以下核心方法：

- **pid()**：返回进程ID
- **isAlive()**：检查进程是否存活
- **info()**：返回进程信息
- **children()**：返回子进程流
- **parent()**：返回父进程
- **onExit()**：返回一个CompletableFuture，用于监控进程终止
- **destroy()**：尝试优雅终止进程
- **destroyForcibly()**：强制终止进程
- **supportsNormalTermination()**：检查进程是否支持优雅终止

### 2. ProcessHandle.Info接口

ProcessHandle.Info接口提供了获取进程详细信息的方法：

- **command()**：返回进程的命令路径
- **arguments()**：返回进程的命令行参数
- **startInstant()**：返回进程的启动时间
- **totalCpuDuration()**：返回进程使用的CPU时间
- **user()**：返回启动进程的用户

### 3. 静态方法

ProcessHandle类提供了以下静态方法：

- **current()**：返回当前进程的ProcessHandle
- **of(long pid)**：根据进程ID返回ProcessHandle
- **allProcesses()**：返回系统中所有进程的流

## 进程API的使用场景

### 适合使用Process Handle API的场景

1. **进程监控**：监控系统中进程的状态和资源使用情况
2. **进程管理**：启动、终止和管理系统进程
3. **系统诊断**：获取系统进程信息，用于诊断系统问题
4. **进程间通信**：通过进程ID识别和管理相关进程
5. **服务管理**：管理后台服务进程的启动和停止

### 示例场景

- **监控后台服务**：监控后台服务进程的状态，确保服务正常运行
- **系统资源监控**：监控系统中占用资源较多的进程
- **进程生命周期管理**：管理应用程序的子进程，确保子进程正确启动和终止
- **系统诊断工具**：开发系统诊断工具，获取系统进程信息

## 代码优势

1. **统一的API**：提供了统一的API来管理和监控进程，替代了之前依赖于本地方法的实现
2. **功能丰富**：提供了获取进程信息、监控进程状态、列出所有进程等丰富的功能
3. **异步处理**：通过CompletableFuture提供了异步监控进程终止的能力
4. **流式操作**：支持流式操作，方便处理多个进程
5. **跨平台**：提供了跨平台的进程管理能力，无需关心底层操作系统的差异

## 注意事项

1. **JDK版本**：Process Handle API是JDK 9+的特性，在JDK 8及以下版本中无法使用
2. **权限限制**：某些操作可能需要特定的系统权限，例如获取其他用户进程的信息
3. **平台差异**：不同操作系统的进程管理机制可能存在差异，某些功能可能在不同平台上表现不同
4. **资源管理**：启动新进程时，需要注意资源管理，避免资源泄漏
5. **进程终止**：终止进程时，应该优先使用destroy()方法尝试优雅终止，只有在必要时才使用destroyForcibly()方法强制终止

## 实际应用示例

### 1. 监控后台服务

```java
public void monitorService(String serviceName) {
    // 查找名为serviceName的进程
    Optional<ProcessHandle> serviceProcess = ProcessHandle.allProcesses()
            .filter(ph -> {
                Optional<String> command = ph.info().map(ProcessHandle.Info::command);
                return command.isPresent() && command.get().contains(serviceName);
            })
            .findFirst();
    
    if (serviceProcess.isPresent()) {
        ProcessHandle ph = serviceProcess.get();
        System.out.println("服务进程ID: " + ph.pid());
        
        // 监控进程状态
        ph.onExit().thenAccept(p -> {
            System.out.println("服务进程已终止: " + p.pid());
            // 可以在这里添加重启服务的逻辑
        });
    } else {
        System.out.println("未找到服务进程: " + serviceName);
    }
}
```

### 2. 系统资源监控

```java
public void monitorResourceUsage() {
    System.out.println("占用CPU较多的进程:");
    ProcessHandle.allProcesses()
            .forEach(ph -> {
                Optional<Duration> cpuTime = ph.info().map(ProcessHandle.Info::totalCpuDuration);
                if (cpuTime.isPresent()) {
                    long millis = cpuTime.get().toMillis();
                    if (millis > 1000) { // 只显示CPU时间超过1秒的进程
                        System.out.printf("PID: %d, CPU时间: %d ms, 命令: %s%n",
                                ph.pid(),
                                millis,
                                ph.info().map(ProcessHandle.Info::command).orElse(Optional.empty()));
                    }
                }
            });
}
```

通过本示例代码，您可以了解JDK 9+中进程API的使用方式和优势，为实际项目中的进程管理和监控提供参考。