# 类加载器的层次结构

Java类加载器采用层次化的架构设计，主要包括以下四个层次，从顶层到底层依次为：

## 1. 启动类加载器（Bootstrap ClassLoader）
- **类型**：由C/C++实现的原生类加载器
- **职责**：加载Java核心类库，如`rt.jar`、`resources.jar`等
- **加载路径**：由`sun.boot.class.path`系统属性指定
- **特点**：
  - 是最顶层的类加载器，没有父加载器
  - 无法通过Java代码直接获取其实例（返回null）
  - 仅加载名称以`java.`、`javax.`、`sun.`等开头的类

## 2. 扩展类加载器（Extension ClassLoader）
- **类型**：由Java实现的类加载器，对应`sun.misc.Launcher$ExtClassLoader`
- **职责**：加载Java扩展类库，如`ext`目录中的类
- **加载路径**：由`java.ext.dirs`系统属性指定
- **特点**：
  - 父加载器是启动类加载器
  - 加载标准扩展目录中的类库

## 3. 应用程序类加载器（Application ClassLoader）
- **类型**：由Java实现的类加载器，对应`sun.misc.Launcher$AppClassLoader`
- **职责**：加载应用程序类路径上的类
- **加载路径**：由`java.class.path`系统属性指定（即CLASSPATH环境变量）
- **特点**：
  - 父加载器是扩展类加载器
  - 是默认的类加载器，通过`ClassLoader.getSystemClassLoader()`获取
  - 加载用户编写的类和第三方依赖库

## 4. 自定义类加载器（Custom ClassLoader）
- **类型**：由用户自定义的类加载器，继承自`java.lang.ClassLoader`
- **职责**：根据需要加载特定路径或格式的类
- **加载路径**：由用户自定义
- **特点**：
  - 父加载器通常是应用程序类加载器
  - 可实现特殊的加载逻辑，如加密类的加载、网络类的加载等

## 类加载器的协作机制：双亲委派模型

Java类加载器采用**双亲委派模型**（Parent Delegation Model）来协作工作：

1. **工作原理**：
   - 当一个类加载器收到类加载请求时，首先将请求委派给父加载器
   - 父加载器在自己的加载范围内尝试加载，如果无法加载（找不到类），则将请求返回给子加载器
   - 子加载器在自己的加载范围内尝试加载，如果仍无法加载，则抛出`ClassNotFoundException`

2. **优势**：
   - **安全性**：防止核心类被篡改，如用户无法自定义`java.lang.String`类
   - **一致性**：确保同一个类在不同类加载器中被加载时是同一个类
   - **避免重复加载**：父加载器已经加载过的类，子加载器无需重复加载

3. **破坏双亲委派模型的场景**：
   - 热部署：如OSGi框架
   - 自定义类加载器：如Tomcat的WebAppClassLoader
   - 线程上下文类加载器：用于加载SPI实现类