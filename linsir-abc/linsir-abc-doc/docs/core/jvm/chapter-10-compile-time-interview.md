# 第10章 前端编译与优化 - 面试题总结

## 一、Javac编译器相关面试题

### 1.1 编译流程

#### 面试题1：请简述Javac编译器的工作流程

**答案要点**：
Javac编译器的工作流程分为以下几个阶段：

1. **词法分析**：将源代码的字符流转变为标记（Token）集合
2. **语法分析**：根据Token序列构造抽象语法树（AST）
3. **填充符号表**：记录源代码中出现的各种标识符及其作用域和绑定信息
4. **注解处理**：执行插入式注解处理器，可修改AST
5. **语义分析**：进行类型检查、数据流分析等
6. **字节码生成**：生成符合JVM规范的字节码文件

**记忆口诀**：词法语法填符号，注解语义生字节

---

#### 面试题2：什么是抽象语法树（AST）？

**答案要点**：
- **定义**：AST是源代码语法结构的一种抽象表示，以树状形式表现编程语言的语法结构
- **作用**：
  - 是编译器进行后续处理的基础数据结构
  - 每个节点代表源代码中的一个结构（如类、方法、表达式）
  - 不依赖于源代码的具体语法细节
- **示例**：
```
CompilationUnit
├── ClassDef (Hello)
│   ├── MethodDef (main)
│   │   ├── Block
│   │   │   └── Exec
│   │   │       └── Apply (println)
```

---

#### 面试题3：符号表的作用是什么？

**答案要点**：
- **定义**：由一组符号地址和符号信息构成的表格
- **作用**：
  - 记录每个符号的名称、类型、作用域
  - 支持语义检查（如类型检查、访问权限检查）
  - 为后续代码生成提供符号地址信息
- **内容**：类名、方法名、变量名等标识符及其绑定信息

---

#### 面试题4：Javac编译器和JIT编译器有什么区别？

**答案要点**：

| 特性 | Javac（前端编译器） | JIT（后端编译器） |
|------|-------------------|------------------|
| **执行时机** | 编译期（.java → .class） | 运行期（字节码 → 机器码） |
| **输入** | Java源代码 | 字节码 |
| **输出** | 字节码文件 | 本地机器码 |
| **优化重点** | 语法糖消解、类型擦除 | 热点代码优化、内联、逃逸分析 |
| **执行方式** | 一次性编译 | 多次调用后触发编译 |

---

### 1.2 编译优化

#### 面试题5：什么是常量折叠（Constant Folding）？

**答案要点**：
- **定义**：编译器在编译期对常量表达式进行计算，直接用计算结果替换表达式
- **示例**：
```java
// 源代码
int a = 1 + 2 + 3;

// 编译后等效代码
int a = 6;  // 编译器直接计算结果
```
- **作用**：减少运行时计算开销

---

#### 面试题6：Java中的条件编译是如何实现的？

**答案要点**：
- **实现方式**：利用编译期常量优化
- **原理**：编译器在语义分析阶段进行常量折叠，对于值为`false`的编译期常量条件，会移除对应的代码块
- **示例**：
```java
public static final boolean DEBUG = false;

public void test() {
    if (DEBUG) {
        System.out.println("Debug mode");  // 编译后这段代码会被移除
    }
    System.out.println("Production mode");
}
```
- **注意**：与C/C++的预处理器不同，这是编译器的优化行为

---

## 二、泛型与类型擦除面试题

### 2.1 类型擦除基础

#### 面试题7：什么是Java泛型的类型擦除（Type Erasure）？

**答案要点**：
- **定义**：Java泛型只在程序源码中存在，在编译后的字节码文件中，泛型类型被替换为原生类型（Raw Type），并插入相应的强制类型转换代码
- **原因**：为了保持与Java 1.5之前版本的向后兼容性
- **擦除规则**：
  - 无界类型参数（`<T>`）擦除为`Object`
  - 有界类型参数（`<T extends Number>`）擦除为边界类型`Number`
  - 多个边界（`<T extends A & B>`）擦除为第一个边界`A`

---

#### 面试题8：类型擦除的具体过程是怎样的？

**答案要点**：
```java
// 源代码
public class GenericExample<T> {
    private T data;
    public void setData(T data) { this.data = data; }
    public T getData() { return data; }
}

// 编译后的等效代码
public class GenericExample {
    private Object data;
    public void setData(Object data) { this.data = data; }
    public Object getData() { return data; }
}
```

**擦除过程**：
1. 将泛型类型参数替换为擦除后的类型
2. 在必要时插入强制类型转换
3. 生成桥接方法（Bridge Method）保持多态性

---

#### 面试题9：什么是桥接方法（Bridge Method）？

**答案要点**：
- **定义**：当子类继承泛型父类或实现泛型接口时，编译器生成的用于保持多态性的方法
- **作用**：确保类型擦除后方法签名仍然匹配，维持多态性
- **示例**：
```java
// 源代码
public interface Comparable<T> {
    int compareTo(T o);
}

public class Integer implements Comparable<Integer> {
    public int compareTo(Integer o) { ... }
}

// 编译器生成的桥接方法
public class Integer implements Comparable {
    // 桥接方法
    public int compareTo(Object o) {
        return compareTo((Integer) o);
    }
    // 实际方法
    public int compareTo(Integer o) { ... }
}
```

---

### 2.2 泛型使用陷阱

#### 面试题10：`List<String>`和`List<Integer>`在运行时有什么区别？

**答案要点**：
- **答案**：运行时没有任何区别，都是`List`类型
- **原因**：类型擦除机制导致泛型信息在运行时不存在
- **验证**：
```java
List<String> strList = new ArrayList<>();
List<Integer> intList = new ArrayList<>();
System.out.println(strList.getClass() == intList.getClass());  // true
```

---

#### 面试题11：为什么不能创建泛型数组？

**答案要点**：
- **原因**：类型擦除导致数组类型检查失效
- **问题示例**：
```java
// 如果允许创建泛型数组
List<String>[] array = new List<String>[10];
Object[] objArray = array;
objArray[0] = new ArrayList<Integer>();  // 编译通过，运行时报错
```
- **解决方案**：使用`List<List<String>>`代替数组

---

#### 面试题12：以下代码能否编译通过？为什么？

```java
public void test(List<String> list) { }
public void test(List<Integer> list) { }
```

**答案要点**：
- **答案**：不能编译通过
- **原因**：类型擦除后两个方法签名相同（都是`List`），导致方法重载冲突
- **编译错误**：`test(List) clashes with test(List)`

---

## 三、自动装箱与拆箱面试题

### 3.1 基础概念

#### 面试题13：什么是自动装箱（Autoboxing）和自动拆箱（Unboxing）？

**答案要点**：
- **自动装箱**：编译器自动将基本数据类型转换为对应的包装器类型
  - 示例：`Integer a = 10;` → `Integer a = Integer.valueOf(10);`
- **自动拆箱**：编译器自动将包装器类型转换为对应的基本数据类型
  - 示例：`int b = a;` → `int b = a.intValue();`
- **引入版本**：Java 5（JDK 1.5）
- **本质**：编译器提供的语法糖

---

#### 面试题14：自动装箱的缓存机制是什么？

**答案要点**：
- **缓存范围**：
  - `Integer`：-128 ~ 127
  - `Byte`、`Short`、`Long`：-128 ~ 127
  - `Character`：0 ~ 127
  - `Boolean`：true和false
- **实现原理**：使用静态缓存数组，通过`valueOf()`方法获取缓存对象
- **示例**：
```java
Integer a = 100;  // 从缓存获取
Integer b = 100;  // 从缓存获取
System.out.println(a == b);  // true（同一对象）

Integer c = 200;  // 超出缓存范围，创建新对象
Integer d = 200;
System.out.println(c == d);  // false（不同对象）
```

---

### 3.2 常见陷阱

#### 面试题15：以下代码的输出是什么？为什么？

```java
Integer a = 100;
Integer b = 100;
System.out.println(a == b);

Integer c = 200;
Integer d = 200;
System.out.println(c == d);
```

**答案要点**：
- **输出**：`true`和`false`
- **原因**：
  - 100在缓存范围内（-128~127），`==`比较的是同一对象，返回true
  - 200超出缓存范围，创建了两个不同的对象，`==`比较返回false
- **建议**：包装类比较应使用`equals()`方法

---

#### 面试题16：自动拆箱可能引发什么问题？

**答案要点**：
- **空指针异常（NullPointerException）**：
```java
Integer a = null;
int b = a;  // 编译通过，运行时报NullPointerException
```
- **性能问题**：频繁的装箱拆箱会创建大量临时对象，增加GC压力
- **精度丢失**：
```java
Double d = 1.9;
int i = d;  // 拆箱后截断，i = 1
```

---

## 四、遍历循环（增强for循环）面试题

#### 面试题17：增强for循环的底层实现是什么？

**答案要点**：
- **本质**：编译器提供的语法糖，编译后转换为迭代器或数组索引遍历
- **集合遍历**：转换为Iterator遍历
```java
// 源代码
for (String s : list) { }

// 编译后
for (Iterator<String> it = list.iterator(); it.hasNext(); ) {
    String s = it.next();
}
```
- **数组遍历**：转换为索引遍历
```java
// 源代码
for (int i : array) { }

// 编译后
for (int j = 0; j < array.length; j++) {
    int i = array[j];
}
```

---

#### 面试题18：在增强for循环中能否修改集合元素？

**答案要点**：
- **修改元素属性**：可以
```java
for (User user : userList) {
    user.setName("newName");  // 可以修改
}
```
- **增删元素**：不可以，会抛出`ConcurrentModificationException`
```java
for (String s : list) {
    if (s.equals("a")) {
        list.remove(s);  // 抛出ConcurrentModificationException
    }
}
```
- **原因**：增强for循环底层使用Iterator，修改集合结构会导致modCount不一致

---

## 五、注解处理器面试题

### 5.1 基础概念

#### 面试题19：什么是注解处理器（Annotation Processor）？

**答案要点**：
- **定义**：Java编译器（javac）在编译阶段对源码中注解进行扫描和处理的机制
- **标准**：基于JSR 269（Pluggable Annotation Processing API）
- **作用**：
  - 编译期代码生成（如Lombok、MapStruct）
  - 编译期代码检查
  - 自动生成配置文件
- **特点**：
  - 在编译期间执行，不修改原始源码
  - 可以读取、修改、添加抽象语法树（AST）元素
  - 支持多轮次处理

---

#### 面试题20：注解处理器的生命周期是怎样的？

**答案要点**：
1. **初始化阶段**：`init(ProcessingEnvironment)`
   - 获取Messager（消息报告器）
   - 获取Filer（文件创建器）
   - 获取ElementUtils（元素工具）
   - 获取TypeUtils（类型工具）

2. **处理阶段**：`process(Set<? extends TypeElement>, RoundEnvironment)`
   - 遍历被注解的元素
   - 生成辅助代码或执行检查
   - 返回true表示已处理

3. **结束阶段**：编译器继续后续编译流程

---

#### 面试题21：如何注册一个注解处理器？

**答案要点**：
- **方式一**：SPI机制（Service Provider Interface）
  - 在`META-INF/services/javax.annotation.processing.Processor`文件中添加处理器全限定名
  ```
  com.example.MyProcessor
  ```

- **方式二**：使用`@AutoService`注解（Google Auto库）
  ```java
  @AutoService(Processor.class)
  public class MyProcessor extends AbstractProcessor { }
  ```

- **方式三**：Maven编译器插件配置
  ```xml
  <annotationProcessors>
      <annotationProcessor>com.example.MyProcessor</annotationProcessor>
  </annotationProcessors>
  ```

---

### 5.2 实现细节

#### 面试题22：AbstractProcessor的四个关键方法是什么？

**答案要点**：
1. **`getSupportedSourceVersion()`**：返回支持的Java版本
2. **`getSupportedAnnotationTypes()`**：返回支持的注解类型集合
3. **`init(ProcessingEnvironment)`**：初始化方法，获取处理环境
4. **`process(Set<? extends TypeElement>, RoundEnvironment)`**：核心处理方法

---

#### 面试题23：注解处理器中的RoundEnvironment是什么？

**答案要点**：
- **定义**：当前处理轮次的环境，提供访问被注解元素的方法
- **核心方法**：
  - `getElementsAnnotatedWith(Class<? extends Annotation> a)`：获取被指定注解标记的元素
  - `processingOver()`：判断是否最后一轮处理
- **多轮次处理**：如果处理器修改了AST，会触发新的处理轮次

---

#### 面试题24：Lombok的实现原理是什么？

**答案要点**：
1. **注解定义**：定义`@Getter`、`@Setter`、`@ToString`等注解
2. **处理器实现**：实现`javax.annotation.processing.Processor`接口
3. **AST操作**：使用`com.sun.source.tree`包操作抽象语法树
4. **代码注入**：在语法树中插入方法节点

**关键区别**：
- 标准注解处理器只能生成新文件
- Lombok通过非公开API直接修改已有AST节点

---

### 5.3 应用场景

#### 面试题25：注解处理器有哪些典型应用场景？

**答案要点**：

| 框架 | 作用 |
|------|------|
| **Lombok** | 自动生成getter/setter/toString/构造器等 |
| **MapStruct** | 自动生成类型转换代码（DTO ↔ Entity） |
| **Dagger** | 编译期依赖注入代码生成 |
| **AutoValue** | 自动生成值对象（不可变对象）代码 |
| **ButterKnife** | Android视图绑定代码生成 |
| **Room** | Android数据库访问代码生成 |

---

## 六、综合面试题

#### 面试题26：以下代码编译后会生成哪些额外方法？

```java
public class Test {
    private int value;
}
```

**答案要点**：
- **默认构造器**：编译器会自动生成无参构造器
```java
public Test() { }
```
- **类构造器`<clinit>()`**：如果有静态变量或静态代码块
- **实例构造器`<init>()`**：编译器会生成，调用父类构造器

---

#### 面试题27：Java语法糖有哪些？请举例说明

**答案要点**：

| 语法糖 | 说明 | 编译后转换 |
|--------|------|-----------|
| **泛型** | 类型参数化 | 类型擦除，插入强制转换 |
| **自动装箱/拆箱** | 基本类型与包装类自动转换 | 调用valueOf()/xxxValue() |
| **增强for循环** | 简化遍历语法 | Iterator或索引遍历 |
| **变长参数** | `method(String... args)` | 转换为数组参数 |
| **枚举** | `enum Color { RED, GREEN }` | 继承Enum的类 |
| **内部类** | 类中定义类 | 生成独立的类文件 |
| **lambda表达式** | `(x) -> x + 1` | 生成静态方法+invokedynamic |
| **try-with-resources** | 自动关闭资源 | 生成finally块关闭资源 |

---

#### 面试题28：什么是编译期常量和运行期常量？

**答案要点**：
- **编译期常量**：
  - 使用`final`修饰的基本类型或String
  - 在编译时确定值
  - 会被直接内联到使用处
  ```java
  public static final int MAX = 100;  // 编译期常量
  ```

- **运行期常量**：
  - 使用`final`修饰但值在运行时才确定
  - 不会被内联
  ```java
  public static final int MAX = new Random().nextInt();  // 运行期常量
  ```

---

#### 面试题29：如何查看Java代码编译后的字节码？

**答案要点**：
- **javap命令**：JDK自带的反编译工具
  ```bash
  javap -c Test.class          # 显示字节码指令
  javap -v Test.class          # 显示详细信息（包括常量池）
  javap -p Test.class          # 显示私有成员
  ```

- **IDE插件**：
  - IntelliJ IDEA：View → Show Bytecode
  - Eclipse：Bytecode Visualizer插件

- **在线工具**：
  - https://javap.yawk.at/

---

#### 面试题30：前端编译优化和JIT编译优化有什么区别？

**答案要点**：

| 优化类型 | 执行时机 | 优化内容 | 示例 |
|----------|----------|----------|------|
| **前端编译优化** | 编译期 | 语法糖消解、常量折叠 | 泛型擦除、自动装箱 |
| **JIT编译优化** | 运行期 | 热点代码优化 | 方法内联、逃逸分析、锁消除 |

**JIT优化特点**：
- 基于运行时的性能数据（热点探测）
- 可以进行推测性优化（如内联虚方法）
- 支持去优化（Deoptimization）回退到解释执行

---

## 七、面试技巧总结

### 7.1 回答框架

**问题类型1：概念解释类**
1. 给出明确定义
2. 说明作用和目的
3. 举例说明
4. 提及注意事项或陷阱

**问题类型2：原理分析类**
1. 简述整体流程
2. 分步骤详细说明
3. 举例验证
4. 总结关键点

**问题类型3：对比分析类**
1. 分别说明两者概念
2. 使用表格对比关键差异
3. 举例说明使用场景
4. 给出选择建议

### 7.2 高频考点

1. **必考点**：类型擦除、自动装箱缓存、语法糖
2. **重点**：注解处理器原理、编译流程
3. **难点**：桥接方法、JIT与前端编译的区别
4. **陷阱题**：`==`与`equals`在包装类的使用

### 7.3 答题注意事项

1. **避免绝对化表述**：如"所有"、"一定"等
2. **区分编译期和运行期**：明确说明优化发生的时机
3. **结合实际案例**：用代码示例支撑观点
4. **提及版本差异**：如Java 5引入的泛型、自动装箱等
