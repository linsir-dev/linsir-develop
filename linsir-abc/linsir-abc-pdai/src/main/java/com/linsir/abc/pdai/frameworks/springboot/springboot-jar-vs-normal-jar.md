# SpringBoot 打成jar和普通的jar有什么区别？

## 1. 概述

Spring Boot应用打包成的JAR文件与普通JAR文件在结构、加载方式、运行方式等方面存在显著差异。理解这些差异对于Spring Boot开发和部署非常重要。

## 2. 普通JAR文件

### 2.1 结构
普通JAR文件是Java Archive的标准格式，包含编译后的类文件和资源文件。

```
myapp-1.0.0.jar
├── com/
│   └── example/
│       ├── Application.class
│       ├── controller/
│       └── service/
├── META-INF/
│   ├── MANIFEST.MF
│   └── maven/
└── application.properties
```

### 2.2 MANIFEST.MF
普通JAR的MANIFEST.MF文件相对简单：

```
Manifest-Version: 1.0
Created-By: Maven Jar Plugin 3.3.1
Build-Jdk-Spec: 17
Implementation-Title: myapp
Implementation-Version: 1.0.0
Main-Class: com.example.Application
```

### 2.3 运行方式
普通JAR需要依赖外部类路径：

```bash
java -cp myapp-1.0.0.jar:lib/* com.example.Application
```

## 3. Spring Boot可执行JAR

### 3.1 结构
Spring Boot可执行JAR采用特殊的嵌套结构：

```
myapp-1.0.0.jar
├── BOOT-INF/
│   ├── classes/              # 应用类文件
│   │   ├── com/
│   │   │   └── example/
│   │   │       ├── Application.class
│   │   │       ├── controller/
│   │   │       └── service/
│   │   └── application.properties
│   └── lib/                 # 依赖库
│       ├── spring-boot-3.2.0.jar
│       ├── spring-web-6.1.0.jar
│       ├── spring-context-6.1.0.jar
│       ├── jackson-databind-2.15.0.jar
│       └── ...
├── META-INF/
│   ├── MANIFEST.MF          # 清单文件
│   └── spring.factories
└── org/
    └── springframework/
        └── boot/
            └── loader/      # Spring Boot加载器
                ├── JarLauncher.class
                ├── LaunchedURLClassLoader.class
                └── ...
```

### 3.2 MANIFEST.MF
Spring Boot可执行JAR的MANIFEST.MF文件包含更多启动信息：

```
Manifest-Version: 1.0
Created-By: Maven Jar Plugin 3.3.1
Build-Jdk-Spec: 17
Implementation-Title: myapp
Implementation-Version: 1.0.0
Main-Class: org.springframework.boot.loader.JarLauncher
Start-Class: com.example.Application
Spring-Boot-Version: 3.2.0
Spring-Boot-Classes: BOOT-INF/classes/
Spring-Boot-Lib: BOOT-INF/lib/
```

### 3.3 运行方式
Spring Boot可执行JAR可以直接运行：

```bash
java -jar myapp-1.0.0.jar
```

## 4. 主要区别

### 4.1 依赖管理

**普通JAR：**
- 不包含依赖库
- 需要外部类路径
- 依赖管理复杂

**Spring Boot可执行JAR：**
- 包含所有依赖库
- 无需外部依赖
- 依赖管理简单

### 4.2 类加载机制

**普通JAR：**
- 使用标准类加载器
- 类路径由`-cp`参数指定
- 按类路径顺序加载类

**Spring Boot可执行JAR：**
- 使用自定义类加载器`LaunchedURLClassLoader`
- 特殊的类加载顺序
- 优先加载应用类，然后加载依赖类

### 4.3 启动方式

**普通JAR：**
```bash
java -cp myapp.jar:lib/* com.example.Application
```

**Spring Boot可执行JAR：**
```bash
java -jar myapp.jar
```

### 4.4 文件大小

**普通JAR：**
- 较小，只包含应用类
- 需要额外的依赖JAR文件

**Spring Boot可执行JAR：**
- 较大，包含所有依赖
- 单个文件即可运行

### 4.5 部署方式

**普通JAR：**
- 需要部署多个文件
- 需要配置类路径
- 部署复杂

**Spring Boot可执行JAR：**
- 只需部署一个文件
- 无需配置类路径
- 部署简单

## 5. Spring Boot类加载机制

### 5.1 JarLauncher
`JarLauncher`是Spring Boot可执行JAR的入口类。

```java
public class JarLauncher extends ExecutableArchiveLauncher {
    static final String BOOT_INF_CLASSES = "BOOT-INF/classes/";
    static final String BOOT_INF_LIB = "BOOT-INF/lib/";

    public JarLauncher() {
    }

    protected JarLauncher(Archive archive) {
        super(archive);
    }

    @Override
    protected boolean isNestedArchive(Archive.Entry entry) {
        if (entry.isDirectory()) {
            return entry.getName().equals(BOOT_INF_CLASSES);
        }
        return entry.getName().startsWith(BOOT_INF_LIB);
    }

    public static void main(String[] args) throws Exception {
        new JarLauncher().launch(args);
    }
}
```

### 5.2 LaunchedURLClassLoader
`LaunchedURLClassLoader`是Spring Boot的自定义类加载器。

```java
public class LaunchedURLClassLoader extends URLClassLoader {
    private final boolean defineClassFirst;

    public LaunchedURLClassLoader(boolean defineClassFirst, URL[] urls, ClassLoader parent) {
        super(urls, parent);
        this.defineClassFirst = defineClassFirst;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (this.defineClassFirst) {
            Class<?> loadedClass = findLoadedClass(name);
            if (loadedClass == null) {
                try {
                    loadedClass = findClass(name);
                }
                catch (ClassNotFoundException ex) {
                    loadedClass = super.loadClass(name, resolve);
                }
            }
            if (resolve) {
                resolveClass(loadedClass);
            }
            return loadedClass;
        }
        return super.loadClass(name, resolve);
    }
}
```

### 5.3 类加载顺序
Spring Boot的类加载顺序：

1. 首先加载`BOOT-INF/classes/`中的应用类
2. 然后加载`BOOT-INF/lib/`中的依赖类
3. 最后加载JDK和系统类

## 6. 打包配置

### 6.1 使用spring-boot-maven-plugin
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### 6.2 打包命令
```bash
mvn clean package
```

### 6.3 生成的文件
```
target/
├── myapp-1.0.0.jar          # 可执行JAR
└── myapp-1.0.0.jar.original # 原始JAR
```

## 7. 使用场景

### 7.1 使用普通JAR的场景
- 作为库被其他项目引用
- 需要共享依赖
- 文件大小敏感
- 需要灵活的类路径配置

### 7.2 使用Spring Boot可执行JAR的场景
- 独立运行的应用
- 微服务架构
- 容器化部署
- 云原生应用

## 8. 优缺点对比

### 8.1 普通JAR

**优点：**
- 文件大小小
- 可以共享依赖
- 类路径配置灵活

**缺点：**
- 部署复杂
- 需要管理依赖
- 启动命令复杂

### 8.2 Spring Boot可执行JAR

**优点：**
- 部署简单
- 无需管理依赖
- 启动命令简单
- 适合容器化部署

**缺点：**
- 文件大小大
- 依赖重复
- 类加载机制复杂

## 9. 实际应用

### 9.1 普通JAR作为库
```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>my-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 9.2 Spring Boot可执行JAR作为应用
```bash
java -jar myapp.jar
```

### 9.3 Docker部署
**普通JAR：**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/myapp.jar /app/myapp.jar
COPY lib/ /app/lib/
CMD ["java", "-cp", "/app/myapp.jar:/app/lib/*", "com.example.Application"]
```

**Spring Boot可执行JAR：**
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/myapp.jar /app/myapp.jar
CMD ["java", "-jar", "/app/myapp.jar"]
```

## 10. 总结

### 10.1 主要区别

| 特性 | 普通JAR | Spring Boot可执行JAR |
|------|---------|-------------------|
| **依赖管理** | 不包含依赖 | 包含所有依赖 |
| **类加载** | 标准类加载器 | 自定义类加载器 |
| **启动方式** | `java -cp` | `java -jar` |
| **文件大小** | 较小 | 较大 |
| **部署方式** | 复杂 | 简单 |
| **使用场景** | 作为库 | 作为应用 |

### 10.2 选择建议

**使用普通JAR：**
- 开发库或工具类
- 需要被其他项目引用
- 文件大小敏感

**使用Spring Boot可执行JAR：**
- 开发独立应用
- 微服务架构
- 容器化部署

理解Spring Boot可执行JAR和普通JAR的区别，有助于根据实际需求选择合适的打包方式。Spring Boot的可执行JAR通过自定义类加载器和特殊的文件结构，实现了"一次构建，到处运行"的目标，大大简化了应用的部署和管理。