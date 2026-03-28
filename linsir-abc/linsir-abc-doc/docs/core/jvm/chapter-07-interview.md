# 第7章 虚拟机类加载机制 - 面试题汇总

## 目录

1. [类加载基础](#一类加载基础)
2. [类加载过程](#二类加载过程)
3. [类加载器](#三类加载器)
4. [双亲委派模型](#四双亲委派模型)
5. [破坏双亲委派模型](#五破坏双亲委派模型)
6. [模块化与实战](#六模块化与实战)
7. [面试技巧](#七面试技巧)

---

## 一、类加载基础

### 1.1 什么是类加载机制？

**Q1：什么是Java类加载机制？**

**答案：**

类加载机制是指虚拟机把描述类的数据从Class文件加载到内存，并对数据进行校验、转换解析和初始化，最终形成可以被虚拟机直接使用的Java类型的过程。

**类加载的特点：**
1. **运行时加载**：Java的类加载、连接和初始化都在程序运行期间完成
2. **动态扩展**：Java的动态扩展特性依赖运行期动态加载和动态连接
3. **灵活性**：可以在运行时从多种来源加载类（网络、数据库、动态生成等）

**类加载的生命周期：**
```
加载 → 验证 → 准备 → 解析 → 初始化 → 使用 → 卸载
     └────────连接────────┘
```

---

### 1.2 类加载时机

**Q2：什么时候会触发类的初始化？**

**答案：**

《Java虚拟机规范》规定了**6种必须立即初始化**的情况：

| 场景 | 触发条件 |
|------|----------|
| **new关键字** | 使用new实例化对象 |
| **静态字段/方法** | 读取/设置静态字段（非final）、调用静态方法 |
| **反射调用** | 使用java.lang.reflect包的方法 |
| **父类未初始化** | 初始化子类时发现父类未初始化 |
| **主类** | 虚拟机启动时包含main()方法的类 |
| **动态语言** | 使用MethodHandle解析特定方法句柄 |
| **默认方法** | 接口定义了default方法，实现类初始化时 |

**字节码指令触发：**
- `new`：创建对象
- `getstatic`/`putstatic`：访问静态字段
- `invokestatic`：调用静态方法

---

**Q3：什么是被动引用？举例说明**

**答案：**

**被动引用**是指引用类的方式不会触发类的初始化。

**三种典型情况：**

**1. 通过子类引用父类的静态字段**
```java
public class SuperClass {
    static {
        System.out.println("SuperClass init!");
    }
    public static int value = 123;
}

public class SubClass extends SuperClass {
    static {
        System.out.println("SubClass init!");
    }
}

// 测试
System.out.println(SubClass.value);
// 输出：SuperClass init! 和 123
// 不会输出 SubClass init!
```

**2. 通过数组定义引用类**
```java
SuperClass[] sca = new SuperClass[10];
// 不会触发SuperClass的初始化
// 数组类型由虚拟机自动生成
```

**3. 引用常量**
```java
public class ConstClass {
    static {
        System.out.println("ConstClass init!");
    }
    public static final String HELLO = "hello world";
}

System.out.println(ConstClass.HELLO);
// 不会输出 ConstClass init!
// 常量在编译期已存入调用类的常量池
```

---

## 二、类加载过程

### 2.1 加载阶段

**Q4：类加载的"加载"阶段做了哪些事情？**

**答案：**

加载阶段需要完成三件事：

1. **获取二进制字节流**
   - 通过类的全限定名获取Class文件
   - 来源不限于文件系统（网络、数据库、动态生成等）

2. **转化为运行时数据结构**
   - 将静态存储结构转化为方法区的运行时数据结构

3. **生成Class对象**
   - 在堆中生成java.lang.Class对象
   - 作为访问方法区数据的入口

**获取字节流的多种方式：**
| 方式 | 应用场景 |
|------|----------|
| 本地文件系统 | 普通Java应用 |
| 网络 | Applet、Web Start |
| 压缩包 | JAR、EAR、WAR |
| 数据库 | 特殊场景 |
| 运行时计算生成 | 动态代理、CGLIB |
| 其他文件 | JSP编译生成 |
| 加密文件 | 代码保护 |

---

**Q5：数组类是如何加载的？**

**答案：**

**数组类的特殊性：**
- 数组类本身**不通过类加载器**创建
- 由Java虚拟机**直接在内存中动态构造**
- 但数组的**元素类型**需要类加载器加载

**数组类创建规则：**

| 数组类型 | 组件类型 | 类加载器 |
|----------|----------|----------|
| `int[]` | int（基本类型） | 引导类加载器 |
| `String[]` | String（引用类型） | 加载String的类加载器 |
| `Object[][]` | Object[]（数组类型） | 加载Object的类加载器 |

**数组类的继承关系：**
- 所有数组类都继承自 `java.lang.Object`
- 实现了 `Cloneable` 和 `Serializable` 接口

---

### 2.2 验证阶段

**Q6：验证阶段有哪些验证内容？**

**答案：**

验证阶段分为**4个阶段**：

**1. 文件格式验证**
- 魔数是否为0xCAFEBABE
- 版本号是否在当前JVM支持范围
- 常量池常量类型是否合法
- UTF-8编码是否正确

**2. 元数据验证**
- 是否有父类（除Object外）
- 是否继承了final类
- 是否实现了所有抽象方法
- 字段/方法是否与父类冲突

**3. 字节码验证**
- 操作数栈类型与指令是否匹配
- 跳转指令是否合法
- 类型转换是否有效
- **JDK 6+使用StackMapTable优化**

**4. 符号引用验证**
- 全限定名是否能找到对应类
- 字段/方法是否存在
- 访问权限是否允许

---

### 2.3 准备阶段

**Q7：准备阶段为类变量分配内存并设置什么值？**

**答案：**

**准备阶段的特点：**
1. **只分配类变量**（static修饰），不分配实例变量
2. **设置零值**，不是程序中指定的初始值

**基本数据类型的零值：**

| 数据类型 | 零值 |
|----------|------|
| int | 0 |
| long | 0L |
| short | (short)0 |
| char | '\u0000' |
| byte | (byte)0 |
| boolean | false |
| float | 0.0f |
| double | 0.0d |
| reference | null |

**特殊情况——常量：**
```java
public static int value = 123;      // 准备阶段：value = 0
public static final int CONST = 123; // 准备阶段：CONST = 123
```

**原因：**
- 普通静态变量：赋值在`<clinit>()`中执行
- final常量：编译期生成ConstantValue属性，准备阶段直接赋值

---

### 2.4 解析阶段

**Q8：什么是符号引用和直接引用？**

**答案：**

**符号引用（Symbolic Reference）：**
- 以符号描述引用目标
- 可以是任意字面量
- 与内存布局无关
- 例如：`java/lang/Object`，`println`，`(Ljava/lang/String;)V`

**直接引用（Direct Reference）：**
- 直接指向目标的指针、偏移量或句柄
- 与内存布局相关
- 在类加载的解析阶段生成

**解析时机：**
- 类加载的**解析阶段**
- 部分符号引用**运行时**才解析（动态绑定）

**需要解析的符号引用类型：**
- 类或接口
- 字段
- 类方法
- 接口方法
- 方法类型
- 方法句柄
- 调用点限定符

---

**Q9：字段解析和方法解析的步骤是什么？**

**答案：**

**字段解析步骤：**
1. 在本类中查找匹配的字段
2. 在实现的接口及父接口中递归查找
3. 在父类中递归查找
4. 失败则抛出 `NoSuchFieldError`

**方法解析步骤：**
1. 检查是否为接口（是则抛出`IncompatibleClassChangeError`）
2. 在本类中查找匹配的方法
3. 在父类中递归查找
4. 在实现的接口中查找（找到说明是抽象类，抛`AbstractMethodError`）
5. 失败则抛出 `NoSuchMethodError`

---

### 2.5 初始化阶段

**Q10：`<clinit>()`方法是什么？有什么特点？**

**答案：**

**`<clinit>()`方法**是类构造器，由编译器自动收集：
- 所有类变量的赋值动作
- 静态语句块（static{}）中的语句

**特点：**

| 特性 | 说明 |
|------|------|
| **执行顺序** | 按源文件中出现顺序收集 |
| **父类优先** | 父类`<clinit>()`先执行 |
| **非必需** | 无静态内容则不生成 |
| **线程安全** | JVM保证多线程下正确同步 |
| **接口也有** | 接口变量赋值会生成 |

**与`<init>()`的区别：**
| 特性 | `<clinit>()` | `<init>()` |
|------|--------------|------------|
| 类型 | 类构造器 | 实例构造器 |
| 内容 | 静态变量+静态代码块 | 实例变量+构造方法 |
| 父类调用 | 自动保证父类先执行 | 显式调用super() |
| 执行次数 | 类加载时一次 | 每次创建对象 |

**线程安全示例：**
```java
public class DeadLoopClass {
    static {
        if (true) {
            System.out.println(Thread.currentThread() + " init");
            while (true) {}  // 阻塞
        }
    }
}

// 多线程测试：只有一个线程能执行<clinit>()，其他线程阻塞
```

---

**Q11：以下代码的输出结果是什么？**

```java
public class Test {
    public static void main(String[] args) {
        System.out.println(Sub.B);
    }
}

class Parent {
    public static int A = 1;
    static {
        A = 2;
    }
}

class Sub extends Parent {
    public static int B = A;
}
```

**答案：**

输出：`2`

**执行流程：**
1. 触发Sub类初始化
2. 发现Parent未初始化，先初始化Parent
3. Parent的`<clinit>()`执行：A = 1 → A = 2
4. Sub的`<clinit>()`执行：B = A（此时A=2）
5. 输出B的值：2

---

## 三、类加载器

### 3.1 类加载器基础

**Q12：类加载器的作用是什么？**

**答案：**

类加载器的作用：
1. **加载类**：通过全限定名获取二进制字节流
2. **确定类唯一性**：类加载器 + 类本身 = 类的唯一标识

**类的唯一性：**
```java
// 同一个Class文件，不同类加载器加载，不是同一个类
ClassLoader loader1 = new MyClassLoader();
ClassLoader loader2 = new MyClassLoader();

Class<?> c1 = loader1.loadClass("MyClass");
Class<?> c2 = loader2.loadClass("MyClass");

System.out.println(c1 == c2);  // false
System.out.println(c1.equals(c2));  // false
```

---

**Q13：JVM有哪些内置类加载器？**

**答案：**

**三层类加载器架构：**

| 类加载器 | 实现 | 加载范围 | 说明 |
|----------|------|----------|------|
| **启动类加载器**<br>(Bootstrap) | C++ | `<JAVA_HOME>\lib` | 虚拟机自身部分，无法被Java引用 |
| **扩展类加载器**<br>(Extension) | Java | `<JAVA_HOME>\lib\ext` | `sun.misc.Launcher$ExtClassLoader` |
| **应用程序类加载器**<br>(Application) | Java | `classpath` | `sun.misc.Launcher$AppClassLoader`<br>默认类加载器 |

**获取类加载器：**
```java
// 获取系统类加载器（应用程序类加载器）
ClassLoader appLoader = ClassLoader.getSystemClassLoader();

// 获取扩展类加载器
ClassLoader extLoader = appLoader.getParent();

// 获取启动类加载器（返回null）
ClassLoader bootstrapLoader = extLoader.getParent();  // null

// 获取某个类的类加载器
ClassLoader loader = MyClass.class.getClassLoader();
```

---

### 3.2 自定义类加载器

**Q14：如何自定义类加载器？**

**答案：**

**自定义类加载器步骤：**

1. **继承ClassLoader**
2. **重写findClass()**（推荐）或 loadClass()
3. **调用defineClass()** 将字节数组转为Class对象

**示例代码：**
```java
public class MyClassLoader extends ClassLoader {
    private String classPath;
    
    public MyClassLoader(String classPath) {
        this.classPath = classPath;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 1. 获取类的字节码
        byte[] classData = loadClassData(name);
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        // 2. 定义Class对象
        return defineClass(name, classData, 0, classData.length);
    }
    
    private byte[] loadClassData(String name) {
        // 从文件系统、网络等加载字节码
        String path = classPath + File.separatorChar 
                     + name.replace('.', File.separatorChar) + ".class";
        try (InputStream is = new FileInputStream(path);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int length;
            while ((length = is.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
```

**使用自定义类加载器：**
```java
MyClassLoader loader = new MyClassLoader("/path/to/classes");
Class<?> clazz = loader.loadClass("com.example.MyClass");
Object obj = clazz.newInstance();
```

---

## 四、双亲委派模型

### 4.1 双亲委派模型原理

**Q15：什么是双亲委派模型？**

**答案：**

**双亲委派模型**要求除了顶层的启动类加载器外，其余的类加载器都应有自己的父类加载器。

**工作过程：**
1. 类加载器收到加载请求
2. 首先委派给父类加载器
3. 父类加载器继续向上委派
4. 父类加载器无法完成时，子类加载器才尝试加载

**代码实现：**
```java
protected synchronized Class<?> loadClass(String name, boolean resolve) 
        throws ClassNotFoundException {
    // 1. 检查是否已加载
    Class<?> c = findLoadedClass(name);
    if (c == null) {
        try {
            if (parent != null) {
                // 2. 委派给父类
                c = parent.loadClass(name, false);
            } else {
                // 3. 委派给启动类加载器
                c = findBootstrapClassOrNull(name);
            }
        } catch (ClassNotFoundException e) {
            // 父类无法加载
        }
        if (c == null) {
            // 4. 自己加载
            c = findClass(name);
        }
    }
    if (resolve) {
        resolveClass(c);
    }
    return c;
}
```

---

**Q16：双亲委派模型的优点是什么？**

**答案：**

**两大优点：**

**1. 避免类的重复加载**
- 先委托父类加载，确保同一个类只被加载一次
- 保证Class对象的唯一性

**2. 保证Java核心API的安全性**
- 防止核心类库被篡改
- 例如：用户自定义`java.lang.Object`类不会被加载

**安全示例：**
```java
// 用户自定义恶意Object类
package java.lang;

public class Object {
    // 恶意代码
}

// 由于双亲委派模型，启动类加载器会优先加载JDK自带的Object
// 用户自定义的Object永远不会被加载
```

---

**Q17：为什么叫"双亲"委派？**

**答案：**

"双亲"是翻译问题，英文原文是"Parent Delegation Model"，直译为"父类委派模型"。

**原因：**
1. 类加载器之间的父子关系是通过**组合**（Composition）实现的
2. 不是Java的继承关系
3. 每个类加载器都持有父加载器的引用

```java
public abstract class ClassLoader {
    // 父类加载器
    private final ClassLoader parent;
    
    protected ClassLoader(ClassLoader parent) {
        this.parent = parent;
    }
}
```

---

## 五、破坏双亲委派模型

### 5.1 三次破坏

**Q18：双亲委派模型被破坏过几次？分别是什么场景？**

**答案：**

**三次破坏：**

| 次数 | 场景 | 原因 | 解决方案 |
|------|------|------|----------|
| **第一次** | JDK 1.2之前 | 自定义类加载器必须覆盖loadClass() | JDK 1.2引入findClass() |
| **第二次** | SPI机制 | 接口由启动类加载器加载，实现类在classpath | 线程上下文类加载器 |
| **第三次** | OSGi/热部署 | 需要模块隔离和热替换 | 自定义类加载器架构 |

---

**Q19：什么是线程上下文类加载器？为什么需要它？**

**答案：**

**线程上下文类加载器（Thread Context ClassLoader）**是JDK 1.2引入的，用于解决SPI机制的问题。

**问题场景：**
```
JDBC示例：
- java.sql.Driver接口：由启动类加载器加载
- mysql-connector-java实现：在classpath，由应用程序类加载器加载
- 启动类加载器无法加载classpath中的类
```

**解决方案：**
```java
// 获取线程上下文类加载器（默认是应用程序类加载器）
ClassLoader contextLoader = Thread.currentThread().getContextClassLoader();

// 使用上下文类加载器加载类
Class<?> clazz = Class.forName("com.mysql.jdbc.Driver", true, contextLoader);
```

**JDBC源码示例：**
```java
// DriverManager.loadInitialDrivers()
ServiceLoader<Driver> loadedDrivers = ServiceLoader.load(Driver.class);
// ServiceLoader使用线程上下文类加载器

public static <S> ServiceLoader<S> load(Class<S> service) {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    return ServiceLoader.load(service, cl);
}
```

---

**Q20：Tomcat为什么破坏双亲委派模型？**

**答案：**

**Tomcat类加载器架构：**
```
      Bootstrap
          |
       Common
       /     \
  Catalina  Shared
            /    \
       Webapp1  Webapp2
       (每个Web应用有自己的类加载器)
```

**破坏原因：**

| 需求 | 说明 |
|------|------|
| **隔离性** | 不同Web应用使用不同版本的类库（如Spring 3和Spring 5） |
| **热部署** | 可以独立重新加载某个Web应用 |
| **共享** | 公共类库放在Shared类加载器中 |

**WebAppClassLoader特点：**
- 先尝试自己加载（破坏双亲委派）
- 自己加载不了再委派给父类
- 确保Web应用使用自己的类库版本

```java
// WebAppClassLoader的加载逻辑（简化）
public Class<?> loadClass(String name) {
    // 1. 先自己尝试加载
    Class<?> clazz = findClass(name);
    if (clazz != null) {
        return clazz;
    }
    // 2. 委派给父类
    return super.loadClass(name);
}
```

---

## 六、模块化与实战

### 6.1 Java模块化

**Q21：JDK 9模块化系统对类加载器有什么影响？**

**答案：**

**JDK 9类加载器的变化：**

| 变化 | 说明 |
|------|------|
| **扩展类加载器被取代** | 平台类加载器（Platform ClassLoader）取代扩展类加载器 |
| **启动类加载器改变** | 由Java代码实现（使用JVMCI），不再是纯C++ |
| **继承关系变化** | 使用`jdk.internal.loader.ClassLoaders`下的类 |

**模块化类加载过程：**
1. 确定类所属的模块
2. 根据模块依赖关系加载
3. 双亲委派模型仍然适用
4. 增加模块级别的访问控制

**模块声明（module-info.java）：**
```java
module com.example.module {
    exports com.example.api;           // 导出包
    requires java.sql;                  // 依赖模块
    provides MyService with MyImpl;    // 提供服务
    opens com.example.entity;          // 开放反射
}
```

---

### 6.2 类加载异常

**Q22：ClassNotFoundException和NoClassDefFoundError有什么区别？**

**答案：**

| 特性 | ClassNotFoundException | NoClassDefFoundError |
|------|------------------------|---------------------|
| **类型** | 受检异常（Exception） | 错误（Error） |
| **发生时机** | 运行时动态加载类 | 编译时存在，运行时找不到 |
| **常见原因** | 类名错误、类不在classpath | 依赖缺失、类加载失败 |
| **触发方式** | Class.forName()、loadClass() | new、静态引用 |

**ClassNotFoundException示例：**
```java
try {
    Class.forName("com.example.MissingClass");
} catch (ClassNotFoundException e) {
    // 类不存在或不在classpath
}
```

**NoClassDefFoundError示例：**
```java
// 编译时A类存在
public class B {
    public void method() {
        new A();  // 编译通过
    }
}

// 运行时A类被删除或依赖缺失
// 抛出NoClassDefFoundError: A
```

---

**Q23：如何解决类加载冲突（jar hell）？**

**答案：**

**问题原因：**
- 同一个jar包的不同版本共存
- 不同jar包包含相同全限定名的类

**解决方案：**

**1. 使用Maven/Gradle依赖管理**
```xml
<!-- 排除冲突依赖 -->
<dependency>
    <groupId>com.example</groupId>
    <artifactId>library</artifactId>
    <version>1.0</version>
    <exclusions>
        <exclusion>
            <groupId>conflicting.group</groupId>
            <artifactId>conflicting-artifact</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

**2. 使用OSGi模块化**
- 每个Bundle有自己的类加载器
- 明确声明依赖和导出包

**3. 使用自定义类加载器**
- 不同版本使用不同类加载器
- 实现类隔离

**4. 使用Shade插件**
```xml
<!-- Maven Shade Plugin -->
<relocations>
    <relocation>
        <pattern>com.google.common</pattern>
        <shadedPattern>shaded.com.google.common</shadedPattern>
    </relocation>
</relocations>
```

---

### 6.3 实战场景

**Q24：如何实现类的热部署（热替换）？**

**答案：**

**热部署原理：**
- 使用新的类加载器重新加载修改后的类
- 替换旧的类加载器

**简单实现：**
```java
public class HotSwapClassLoader extends ClassLoader {
    private String classPath;
    
    public HotSwapClassLoader(String classPath) {
        super(HotSwapClassLoader.class.getClassLoader());
        this.classPath = classPath;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] classData = loadClassData(name);
        return defineClass(name, classData, 0, classData.length);
    }
    
    // 每次重新读取文件，不使用缓存
    private byte[] loadClassData(String name) {
        // 从文件系统读取最新的class文件
    }
}

// 使用
public class HotDeployDemo {
    public static void main(String[] args) throws Exception {
        while (true) {
            // 每次创建新的类加载器
            HotSwapClassLoader loader = new HotSwapClassLoader("/path/to/classes");
            Class<?> clazz = loader.loadClass("MyService");
            Object service = clazz.newInstance();
            
            // 调用方法
            clazz.getMethod("execute").invoke(service);
            
            Thread.sleep(5000);  // 5秒后重新加载
        }
    }
}
```

**限制：**
- 不能修改方法签名
- 不能修改类/方法修饰符
- 不能修改继承关系

---

**Q25：如何排查类加载问题？**

**答案：**

**排查工具：**

```bash
# 1. 打印类加载日志
java -verbose:class Main 2>&1 | grep MyClass

# 2. 使用jcmd查看类加载统计
jcmd <pid> VM.classloader_stats

# 3. 使用jmap查看类加载器
jmap -clstats <pid>

# 4. 打印类加载器委托链
java -Dsun.misc.URLClassPath.debug=true Main
```

**排查步骤：**
1. 确认类是否在classpath中
2. 查看类从哪个jar加载
3. 检查是否有多个版本的类
4. 确认类加载器层次结构

**代码排查：**
```java
// 查看类的类加载器
Class<?> clazz = MyClass.class;
ClassLoader loader = clazz.getClassLoader();
System.out.println("ClassLoader: " + loader);

// 查看类加载路径
URLClassLoader urlLoader = (URLClassLoader) loader;
for (URL url : urlLoader.getURLs()) {
    System.out.println(url);
}
```

---

## 七、面试技巧

### 7.1 高频考点

**Q26：类加载机制面试的高频考点有哪些？**

**答案：**

**必考点（>80%）：**
1. 类加载过程（5个阶段）
2. 双亲委派模型及原理
3. 类加载器层次结构
4. 被动引用示例

**常考点（50-80%）：**
1. 破坏双亲委派模型的场景
2. 线程上下文类加载器
3. `<clinit>()` vs `<init>()`
4. 类加载异常处理

**进阶考点（<50%）：**
1. 自定义类加载器
2. 热部署实现
3. 模块化系统
4. OSGi原理

---

### 7.2 回答思路

**Q27：如何回答"请介绍一下Java类加载机制"？**

**答案：**

**回答框架：**

**1. 概述（30秒）**
- 类加载机制定义
- 类加载的生命周期

**2. 类加载过程（1分钟）**
- 加载、验证、准备、解析、初始化
- 重点讲初始化的触发条件

**3. 类加载器（1分钟）**
- 三层类加载器架构
- 双亲委派模型

**4. 实际应用（30秒）**
- SPI机制
- Tomcat类加载器
- 热部署

**示例回答：**
> Java类加载机制是指虚拟机将Class文件加载到内存，经过验证、准备、解析、初始化，最终形成可用Java类型的过程。
>
> 类加载过程包括5个阶段：加载阶段获取二进制字节流并生成Class对象；验证阶段确保字节流安全；准备阶段为类变量分配内存；解析阶段将符号引用转为直接引用；初始化阶段执行类构造器。
>
> Java使用三层类加载器架构：启动类加载器加载核心类库，扩展类加载器加载扩展目录，应用程序类加载器加载classpath。双亲委派模型要求先委派父类加载器，确保类只加载一次且核心API安全。
>
> 实际应用中，SPI机制通过线程上下文类加载器破坏双亲委派；Tomcat为每个Web应用创建独立类加载器实现隔离和热部署。

---

### 7.3 实战演练

**Q28：手写一个自定义类加载器，实现加密类的加载**

**答案：**

**需求：**
- 对Class文件进行简单加密（如XOR）
- 自定义类加载器解密后加载

**实现：**

**1. 加密工具：**
```java
public class ClassEncryptor {
    private static final byte KEY = 0x55;  // 加密密钥
    
    // 加密/解密（XOR对称）
    public static byte[] encrypt(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ KEY);
        }
        return result;
    }
    
    public static void main(String[] args) throws Exception {
        // 读取原始class文件
        byte[] classData = Files.readAllBytes(Paths.get("MyClass.class"));
        // 加密
        byte[] encrypted = encrypt(classData);
        // 保存加密后的文件
        Files.write(Paths.get("MyClass.class.enc"), encrypted);
    }
}
```

**2. 解密类加载器：**
```java
public class DecryptClassLoader extends ClassLoader {
    private String classPath;
    private static final byte KEY = 0x55;
    
    public DecryptClassLoader(String classPath) {
        this.classPath = classPath;
    }
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 加载加密后的class文件
        byte[] encryptedData = loadEncryptedClassData(name);
        // 解密
        byte[] classData = decrypt(encryptedData);
        // 定义Class
        return defineClass(name, classData, 0, classData.length);
    }
    
    private byte[] loadEncryptedClassData(String name) {
        String path = classPath + File.separator 
                     + name.replace('.', File.separatorChar) + ".class.enc";
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            return null;
        }
    }
    
    private byte[] decrypt(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = (byte) (data[i] ^ KEY);
        }
        return result;
    }
}
```

**3. 使用：**
```java
public class Test {
    public static void main(String[] args) throws Exception {
        DecryptClassLoader loader = new DecryptClassLoader("/path/to/encrypted/classes");
        Class<?> clazz = loader.loadClass("com.example.SecretClass");
        Object obj = clazz.newInstance();
        // 使用反射调用方法...
    }
}
```

---

## 附录：常见面试题速查表

| 问题 | 关键词 | 难度 |
|------|--------|------|
| 类加载过程 | 加载→验证→准备→解析→初始化 | ⭐ |
| 初始化触发条件 | new、静态字段/方法、反射、父类 | ⭐ |
| 被动引用 | 子类引用父类静态、数组、常量 | ⭐⭐ |
| 双亲委派模型 | 启动→扩展→应用程序 | ⭐ |
| 破坏双亲委派 | SPI、线程上下文类加载器 | ⭐⭐ |
| 类加载器作用 | 加载类、确定类唯一性 | ⭐ |
| `<clinit>()`特点 | 静态变量+静态代码块、线程安全 | ⭐⭐ |
| 自定义类加载器 | 继承ClassLoader、重写findClass | ⭐⭐⭐ |
| ClassNotFoundException vs NoClassDefFoundError | 动态加载 vs 编译存在运行缺失 | ⭐⭐ |
| 热部署原理 | 新类加载器重新加载 | ⭐⭐⭐ |

---

## 参考资源

1. **官方文档**：《The Java Virtual Machine Specification》
2. **书籍**：《深入理解Java虚拟机》
3. **工具**：javap、jcmd、jmap
4. **源码**：java.lang.ClassLoader
