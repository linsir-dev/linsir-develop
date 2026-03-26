# Java 语法基础面试题汇总

## 文档信息

- **文档编号**: 03-grammar-interview-questions
- **所属模块**: core/grammar
- **对应设计文档**: [01-grammar-detailed-design.md](./01-grammar-detailed-design.md)
- **对应代码文档**: [02-grammar-code-guide.md](./02-grammar-code-guide.md)
- **创建日期**: 2026-03-27
- **版本**: 1.0.0

---

## 1. 数据类型 (datatype)

### 1.1 基本数据类型

#### Q1: Java 有哪些基本数据类型？它们的取值范围和默认值是什么？

**答案**:

| 类型 | 位数 | 字节 | 默认值 | 取值范围 |
|------|------|------|--------|----------|
| byte | 8 | 1 | 0 | -128 ~ 127 |
| short | 16 | 2 | 0 | -32768 ~ 32767 |
| int | 32 | 4 | 0 | -2³¹ ~ 2³¹-1 |
| long | 64 | 8 | 0L | -2⁶³ ~ 2⁶³-1 |
| float | 32 | 4 | 0.0f | 1.4E-45 ~ 3.4028235E38 |
| double | 64 | 8 | 0.0d | 4.9E-324 ~ 1.7976931348623157E308 |
| char | 16 | 2 | '\u0000' | 0 ~ 65535 |
| boolean | 1 | - | false | true/false |

**考点**: 考察对 Java 基础类型的掌握程度，特别是 char 是 16 位无符号整数，可以表示 Unicode 字符。

---

#### Q2: 什么是自动装箱和拆箱？原理是什么？

**答案**:

**自动装箱**: 将基本数据类型自动转换为对应的包装类
```java
Integer i = 10;  // 自动装箱，等价于 Integer.valueOf(10)
```

**自动拆箱**: 将包装类自动转换为对应的基本数据类型
```java
int n = i;  // 自动拆箱，等价于 i.intValue()
```

**原理**:
- 装箱调用包装类的 `valueOf()` 方法
- 拆箱调用包装类的 `xxxValue()` 方法

**缓存机制**:
- Integer: -128 ~ 127 缓存
- Byte/Short/Long: -128 ~ 127 缓存
- Character: 0 ~ 127 缓存
- Boolean: true/false 缓存

```java
Integer a = 100;   // 从缓存取
Integer b = 100;   // 从缓存取
System.out.println(a == b);  // true

Integer c = 200;   // 新建对象
Integer d = 200;   // 新建对象
System.out.println(c == d);  // false
```

---

#### Q3: float 和 double 的区别？为什么不能用浮点数表示精确金额？

**答案**:

**区别**:
- float: 32位单精度浮点数，有效位数约 6-7 位
- double: 64位双精度浮点数，有效位数约 15-16 位

**精度问题**:
浮点数使用二进制科学计数法表示，无法精确表示某些十进制小数（如 0.1）。

```java
System.out.println(0.1 + 0.2);  // 0.30000000000000004
```

**解决方案**: 使用 `BigDecimal` 进行精确计算
```java
BigDecimal a = new BigDecimal("0.1");
BigDecimal b = new BigDecimal("0.2");
System.out.println(a.add(b));  // 0.3
```

---

### 1.2 引用类型

#### Q4: String、StringBuilder、StringBuffer 的区别？

**答案**:

| 特性 | String | StringBuilder | StringBuffer |
|------|--------|---------------|--------------|
| 可变性 | 不可变 | 可变 | 可变 |
| 线程安全 | 安全（不可变） | 不安全 | 安全（synchronized） |
| 性能 | 低（频繁修改时） | 高 | 较高 |
| 使用场景 | 字符串常量 | 单线程字符串拼接 | 多线程字符串拼接 |

**String 不可变的原因**:
1. **安全性**: 字符串常量池共享，防止被篡改
2. **哈希缓存**: 可以缓存 hashCode，提高性能
3. **线程安全**: 天然线程安全，无需同步

**性能对比**:
```java
// 低效：创建大量临时对象
String s = "";
for (int i = 0; i < 1000; i++) {
    s += i;  // 每次创建新 String 对象
}

// 高效：使用 StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) {
    sb.append(i);
}
```

---

#### Q5: 什么是字符串常量池？String s = new String("abc") 创建了几个对象？

**答案**:

**字符串常量池**: JVM 为了节省内存和提高性能，为 String 类型开辟的存储区域，存储字符串字面量。

**对象创建分析**:
```java
String s1 = "abc";  // 1个对象（常量池）
String s2 = new String("abc");  // 2个对象（常量池 + 堆）
```

**intern() 方法**: 将字符串放入常量池，返回常量池引用
```java
String s1 = new String("abc");
String s2 = s1.intern();
String s3 = "abc";
System.out.println(s2 == s3);  // true
```

---

### 1.3 类型转换

#### Q6: 强制类型转换时会发生什么？什么是类型提升？

**答案**:

**强制类型转换**:
- 大类型转小类型可能丢失精度或溢出
- 浮点转整数会截断小数部分

```java
int i = (int) 3.99;  // 3，向零取整
int big = 128;
byte small = (byte) big;  // -128，溢出
```

**类型提升规则**:
1. byte/short/char 运算时提升为 int
2. 整个表达式提升到最大类型

```java
byte a = 10, b = 20;
// byte c = a + b;  // 编译错误，a+b 结果为 int
int c = a + b;  // 正确
```

---

## 2. 变量与常量 (variable)

### 2.1 变量作用域

#### Q7: 成员变量和局部变量的区别？静态变量有什么特点？

**答案**:

| 特性 | 成员变量 | 局部变量 | 静态变量 |
|------|----------|----------|----------|
| 定义位置 | 类中，方法外 | 方法内或参数 | 类中，带 static |
| 存储位置 | 堆内存 | 栈内存 | 方法区 |
| 生命周期 | 对象创建到销毁 | 方法执行期间 | 类加载到卸载 |
| 初始化 | 自动初始化 | 必须手动初始化 | 自动初始化 |
| 访问方式 | 对象引用 | 直接访问 | 类名或对象 |

**静态变量特点**:
- 被所有实例共享
- 类加载时初始化
- 可通过类名直接访问

---

### 2.2 常量与枚举

#### Q8: final 关键字的作用？编译期常量和运行期常量的区别？

**答案**:

**final 作用**:
- 修饰类：不可被继承
- 修饰方法：不可被重写
- 修饰变量：不可重新赋值

**常量类型**:

| 类型 | 定义 | 特点 |
|------|------|------|
| 编译期常量 | `static final int MAX = 100;` | 编译时确定，存入常量池 |
| 运行期常量 | `static final Date NOW = new Date();` | 运行时确定，可修改对象内容 |

**引用类型常量**:
```java
final StringBuilder sb = new StringBuilder("Hello");
sb.append(" World");  // 可以，修改对象内容
// sb = new StringBuilder();  // 错误，不能修改引用
```

---

#### Q9: 枚举相比常量类有什么优势？

**答案**:

**枚举优势**:
1. **类型安全**: 编译器检查，防止非法值
2. **单例保证**: 每个枚举常量都是单例
3. **可扩展**: 可添加字段、方法、实现接口
4. **内置方法**: values(), valueOf(), ordinal() 等

```java
public enum Status {
    ACTIVE(1, "激活"),
    INACTIVE(2, "未激活");
    
    private final int code;
    private final String desc;
    
    Status(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    // getters...
}
```

---

## 3. 运算符 (operator)

### 3.1 算术运算符

#### Q10: ++i 和 i++ 的区别？在表达式中如何使用？

**答案**:

**区别**:
- `++i`: 前缀自增，先增后用
- `i++`: 后缀自增，先用后增

```java
int i = 5;
int a = ++i;  // i=6, a=6

int j = 5;
int b = j++;  // j=6, b=5
```

**表达式中的陷阱**:
```java
int x = 5;
int y = x++ + ++x;  // 5 + 7 = 12
// x++ 先用5，x变为6
// ++x 先增到7，再用7
```

---

### 3.2 位运算符

#### Q11: & 和 && 的区别？| 和 || 的区别？

**答案**:

| 运算符 | 名称 | 特点 |
|--------|------|------|
| & | 按位与 | 不短路，两边都计算 |
| && | 逻辑与 | 短路，左边为 false 不计算右边 |
| \| | 按位或 | 不短路，两边都计算 |
| \|\| | 逻辑或 | 短路，左边为 true 不计算右边 |

**短路应用**:
```java
// 安全调用，避免 NullPointerException
if (obj != null && obj.getValue() > 0) {
    // obj 为 null 时不会调用 getValue()
}
```

---

#### Q12: 位运算有什么实际应用？

**答案**:

**1. 权限控制**:
```java
int READ = 1;     // 0001
int WRITE = 2;    // 0010
int EXECUTE = 4;  // 0100

int permission = READ | WRITE;  // 0011
boolean canRead = (permission & READ) != 0;  // true
```

**2. 高效乘除**:
```java
int a = 16 >> 2;  // 16 / 4 = 4
int b = 16 << 2;  // 16 * 4 = 64
```

**3. 判断奇偶**:
```java
boolean isOdd = (n & 1) == 1;
```

---

## 4. 流程控制 (controlflow)

### 4.1 条件语句

#### Q13: switch 语句支持哪些类型？Java 12+ 有什么改进？

**答案**:

**支持类型**:
- 基本类型：byte, short, int, char
- 包装类型：Byte, Short, Integer, Character
- 枚举类型：Enum
- 字符串：String（Java 7+）

**Java 12+ 增强**:
```java
// 传统写法
switch (day) {
    case MONDAY:
    case FRIDAY:
    case SUNDAY:
        System.out.println(6);
        break;
    // ...
}

// Java 12+ 箭头语法
switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> System.out.println(6);
    case TUESDAY                -> System.out.println(7);
    default                     -> System.out.println(8);
}

// Java 12+ 作为表达式
int numLetters = switch (day) {
    case MONDAY, FRIDAY, SUNDAY -> 6;
    case TUESDAY                -> 7;
    default -> {
        System.out.println("其他");
        yield 8;  // 使用 yield 返回值
    }
};
```

---

### 4.2 循环语句

#### Q14: 三种循环（for、while、do-while）的区别？如何选择？

**答案**:

| 循环 | 特点 | 适用场景 |
|------|------|----------|
| for | 已知循环次数 | 遍历数组、集合 |
| while | 条件控制 | 不确定循环次数 |
| do-while | 至少执行一次 | 需要先执行再判断 |

**增强 for 循环限制**:
- 不能修改集合结构（增删元素）
- 不能获取索引
- 是迭代器的语法糖

---

## 5. 数组 (array)

#### Q15: 数组和 ArrayList 的区别？

**答案**:

| 特性 | 数组 | ArrayList |
|------|------|-----------|
| 大小 | 固定 | 动态扩容 |
| 类型 | 基本类型 + 对象 | 只能对象（自动装箱） |
| 泛型 | 不支持 | 支持 |
| 性能 | 更快 | 稍慢 |
| 方法 | 无 | 丰富的方法 |

**数组转集合注意**:
```java
// 错误：返回的是固定大小的列表
List<String> list = Arrays.asList(array);

// 正确：创建可变列表
List<String> list = new ArrayList<>(Arrays.asList(array));
```

---

## 6. 方法 (method)

### 6.1 方法基础

#### Q16: 方法重载（Overload）和重写（Override）的区别？

**答案**:

| 特性 | 重载 (Overload) | 重写 (Override) |
|------|-----------------|-----------------|
| 位置 | 同一类中 | 子类中 |
| 方法名 | 相同 | 相同 |
| 参数 | 必须不同 | 必须相同 |
| 返回类型 | 可以不同 | 相同或子类型 |
| 访问修饰符 | 可以不同 | 不能更严格 |
| 异常 | 可以不同 | 不能更宽泛 |
| 多态性 | 编译时多态 | 运行时多态 |

---

### 6.2 参数传递

#### Q17: Java 是值传递还是引用传递？

**答案**:

**Java 只有值传递**:
- 基本类型：传递值的副本
- 引用类型：传递引用的副本（地址的副本）

**示例分析**:
```java
// 基本类型：值传递，方法内修改不影响原值
void modify(int x) { x = 100; }
int a = 10;
modify(a);  // a 仍然是 10

// 引用类型：传递引用副本，修改对象内容会影响原对象
void modify(int[] arr) { arr[0] = 100; }
int[] arr = {1, 2, 3};
modify(arr);  // arr[0] 变为 100

// 但重新赋值引用不会影响原引用
void reassign(int[] arr) { arr = new int[]{100}; }
reassign(arr);  // arr 不变
```

**String 特殊**: String 不可变，修改会创建新对象，所以表现得像值传递。

---

### 6.3 可变参数

#### Q18: 可变参数有什么限制？和数组参数的区别？

**答案**:

**限制**:
1. 必须是最后一个参数
2. 一个方法只能有一个可变参数
3. 重载时固定参数优先于可变参数

**与数组的区别**:
```java
// 数组参数：必须传递数组
void method(int[] arr) {}
method(new int[]{1, 2, 3});  // 必须传数组

// 可变参数：灵活传参
void method(int... nums) {}
method();           // 不传
method(1);          // 传一个
method(1, 2, 3);    // 传多个
method(new int[]{1, 2, 3});  // 传数组也可以
```

---

## 7. 面向对象 (oop)

### 7.1 封装、继承、多态

#### Q19: 面向对象的三大特性是什么？分别解释。

**答案**:

**1. 封装 (Encapsulation)**:
- 将数据和操作数据的方法绑定在一起
- 隐藏内部实现细节，暴露公共接口
- 提高安全性和可维护性

```java
public class Student {
    private String name;  // 私有属性
    
    public String getName() {  // 公共访问方法
        return name;
    }
    
    public void setName(String name) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
    }
}
```

**2. 继承 (Inheritance)**:
- 子类继承父类的属性和方法
- 实现代码复用，建立类层次结构
- Java 单继承，但支持多层继承

**3. 多态 (Polymorphism)**:
- 同一接口，不同实现
- 分为编译时多态（重载）和运行时多态（重写）
- 提高代码的灵活性和可扩展性

---

### 7.2 抽象类与接口

#### Q20: 抽象类和接口的区别？如何选择？

**答案**:

| 特性 | 抽象类 (abstract class) | 接口 (interface) |
|------|-------------------------|------------------|
| 方法实现 | 可以有具体方法 | Java 8+ 可以有默认方法 |
| 构造方法 | 有 | 无 |
| 成员变量 | 可以有各种类型 | 只能是 public static final |
| 继承/实现 | 单继承 | 多实现 |
| 访问修饰符 | 任意 | 默认 public |
| 设计目的 | "是什么"（is-a） | "能做什么"（can-do） |

**选择原则**:
- 需要默认实现 → 抽象类
- 需要多继承 → 接口
- 定义契约/能力 → 接口
- 代码复用 → 抽象类

---

## 8. 异常处理 (exception)

### 8.1 异常体系

#### Q21: 检查异常（Checked Exception）和运行时异常（Runtime Exception）的区别？

**答案**:

| 特性 | 检查异常 | 运行时异常 |
|------|----------|------------|
| 继承 | Exception | RuntimeException |
| 处理要求 | 必须处理（try-catch 或 throws） | 不强制处理 |
| 编译 | 不处理编译失败 | 编译通过 |
| 示例 | IOException, SQLException | NullPointerException, IllegalArgumentException |
| 使用场景 | 可恢复的外部错误 | 编程错误 |

**异常体系**:
```
Throwable
├── Error（严重错误，不处理）
│   └── OutOfMemoryError, StackOverflowError
└── Exception
    ├── RuntimeException（运行时异常）
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   └── IllegalArgumentException
    └── 其他检查异常
        ├── IOException
        └── SQLException
```

---

### 8.2 异常处理机制

#### Q22: finally 块一定会执行吗？什么情况下不会执行？

**答案**:

**一般情况下都会执行**，以下情况不会执行：
1. System.exit() 被调用
2. JVM 崩溃
3. 守护线程在 finally 执行前结束

**finally 执行时机**:
```java
try {
    return 1;  // finally 会在 return 前执行
} finally {
    System.out.println("finally");  // 先打印
}
// 返回 1
```

**try-with-resources**:
```java
// 自动关闭资源，更简洁
try (InputStream in = new FileInputStream("file.txt")) {
    // 使用资源
} catch (IOException e) {
    e.printStackTrace();
}
// 自动调用 in.close()
```

---

## 9. 泛型 (generic)

### 9.1 泛型基础

#### Q23: 什么是泛型擦除？为什么要擦除？

**答案**:

**泛型擦除**: Java 泛型在编译时擦除类型参数，替换为限定类型（无限定则替换为 Object），运行时无泛型信息。

**擦除原因**:
- 向后兼容：兼容 Java 5 之前的字节码
- JVM 不支持泛型：简化虚拟机实现

**示例**:
```java
// 编译前
List<String> list = new ArrayList<>();
list.add("hello");
String s = list.get(0);

// 编译后（擦除）
List list = new ArrayList();
list.add("hello");
String s = (String) list.get(0);  // 自动插入类型转换
```

**局限**:
- 不能创建泛型数组：`new T[10]` 错误
- 不能获取泛型类型：`T.class` 错误
- 运行时类型检查：`instanceof T` 错误

---

### 9.2 通配符

#### Q24: 什么是 PECS 原则？如何应用？

**答案**:

**PECS**: Producer Extends Consumer Super

| 通配符 | 含义 | 操作 | 角色 |
|--------|------|------|------|
| <? extends T> | 上界 | 只读 | Producer（生产者） |
| <? super T> | 下界 | 可写 | Consumer（消费者） |
| <?> | 无界 | 只读（除 null） | - |

**应用示例**:
```java
// Producer: 从列表读取数据
void readFrom(List<? extends Number> list) {
    Number n = list.get(0);  // 可以读
    // list.add(1);  // 错误，不能写
}

// Consumer: 向列表写入数据
void writeTo(List<? super Integer> list) {
    list.add(1);  // 可以写
    // Integer i = list.get(0);  // 错误，只能读 Object
}

// Collections.copy 源码
public static <T> void copy(List<? super T> dest, List<? extends T> src) {
    // src 是 Producer，dest 是 Consumer
}
```

---

## 10. 注解 (annotation)

### 10.1 元注解

#### Q25: 常用的元注解有哪些？作用是什么？

**答案**:

| 元注解 | 作用 |
|--------|------|
| @Retention | 注解保留策略（SOURCE/CLASS/RUNTIME） |
| @Target | 注解应用目标（类、方法、字段等） |
| @Documented | 包含在 Javadoc 中 |
| @Inherited | 子类继承该注解 |
| @Repeatable | 可重复注解（Java 8+） |

**保留策略**:
- SOURCE: 源码级别，编译后丢弃
- CLASS: 编译到字节码，运行时不可见（默认）
- RUNTIME: 运行时保留，可通过反射获取

---

### 10.2 自定义注解

#### Q26: 如何定义和使用自定义注解？

**答案**:

**定义注解**:
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MyAnnotation {
    String value() default "";  // 无默认值则必须指定
    int count() default 0;
}
```

**使用注解**:
```java
@MyAnnotation(value = "test", count = 5)
public void myMethod() {}
```

**反射获取**:
```java
Method method = clazz.getMethod("myMethod");
MyAnnotation anno = method.getAnnotation(MyAnnotation.class);
String value = anno.value();
int count = anno.count();
```

---

## 11. 综合面试题

#### Q27: == 和 equals() 的区别？

**答案**:

| 特性 | == | equals() |
|------|-----|----------|
| 基本类型 | 比较值 | 不能用 |
| 引用类型 | 比较地址 | 比较内容（默认比较地址） |
| String | 比较地址 | 比较内容（已重写） |

**String 比较**:
```java
String s1 = "abc";
String s2 = "abc";
String s3 = new String("abc");

s1 == s2;      // true（常量池）
s1 == s3;      // false（堆内存）
s1.equals(s3); // true（内容相同）
```

---

#### Q28: hashCode() 和 equals() 的关系？

**答案**:

**契约**:
1. 两个对象 equals 相等，hashCode 必须相等
2. hashCode 相等，equals 不一定相等（哈希冲突）

**重写原则**:
- 重写 equals 必须重写 hashCode
- 用于 HashMap、HashSet 等哈希集合

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Person person = (Person) o;
    return age == person.age && Objects.equals(name, person.name);
}

@Override
public int hashCode() {
    return Objects.hash(name, age);
}
```

---

#### Q29: 深拷贝和浅拷贝的区别？如何实现深拷贝？

**答案**:

| 特性 | 浅拷贝 | 深拷贝 |
|------|--------|--------|
| 基本类型 | 复制值 | 复制值 |
| 引用类型 | 复制引用 | 复制对象 |
| 独立性 | 共享内部对象 | 完全独立 |

**实现方式**:
1. **Cloneable 接口**: 重写 clone() 方法
2. **序列化**: 对象转字节流再转对象
3. **拷贝构造器**: 手动创建新对象
4. **第三方库**: Apache Commons Lang

```java
// 深拷贝 - 序列化方式
public Object deepCopy() throws Exception {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(bos);
    oos.writeObject(this);
    
    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
    ObjectInputStream ois = new ObjectInputStream(bis);
    return ois.readObject();
}
```

---

#### Q30: 序列化和反序列化是什么？如何实现？

**答案**:

**序列化**: 将对象转换为字节序列，便于存储或传输
**反序列化**: 将字节序列恢复为对象

**实现方式**:
1. **Serializable 接口**: Java 标准序列化
2. **Externalizable 接口**: 自定义序列化逻辑
3. **JSON/XML**: 跨语言序列化

**注意**:
- transient 修饰的字段不序列化
- static 字段不序列化
- serialVersionUID 用于版本控制

```java
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private transient String password;  // 不序列化
}
```

---

## 12. 总结

### 高频考点

| 排名 | 考点 | 出现频率 |
|------|------|----------|
| 1 | String/StringBuilder/StringBuffer | ★★★★★ |
| 2 | == vs equals() | ★★★★★ |
| 3 | 抽象类 vs 接口 | ★★★★★ |
| 4 | 异常处理机制 | ★★★★☆ |
| 5 | 泛型与通配符 | ★★★★☆ |
| 6 | 自动装箱拆箱与缓存 | ★★★★☆ |
| 7 | 多态实现原理 | ★★★★☆ |
| 8 | hashCode 与 equals | ★★★★☆ |
| 9 | 值传递 vs 引用传递 | ★★★☆☆ |
| 10 | 注解与反射 | ★★★☆☆ |

### 面试建议

1. **理解原理**: 不仅要知道是什么，还要知道为什么
2. **结合实际**: 准备实际项目中的应用案例
3. **对比记忆**: 相似概念对比学习（如抽象类 vs 接口）
4. **源码阅读**: 阅读 JDK 源码加深理解
5. **动手实践**: 编写示例代码验证知识点