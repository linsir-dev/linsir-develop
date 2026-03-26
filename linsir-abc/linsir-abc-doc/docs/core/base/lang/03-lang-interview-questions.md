# java.lang 包面试题汇总

> 本文档基于 `java.lang` 包详细设计整理，按模块分类汇总常见面试题目及答案

---

## 目录

1. [Object 核心机制面试题](#一object-核心机制面试题)
2. [String 不可变性面试题](#二string-不可变性面试题)
3. [System 系统操作面试题](#三system-系统操作面试题)
4. [Thread 线程管理面试题](#四thread-线程管理面试题)
5. [反射机制面试题](#五反射机制面试题)
6. [包装类与自动装箱面试题](#六包装类与自动装箱面试题)

---

## 一、Object 核心机制面试题

### 1.1 equals() 和 hashCode() 方法

#### Q1: 谈谈你对 equals() 和 hashCode() 方法的理解？

**答案要点：**

**equals() 方法：**
- 定义在 `java.lang.Object` 类中，用于判断两个对象是否相等
- 默认实现是比较两个对象的引用地址（即 `==`）
- 重写规则：自反性、对称性、传递性、一致性、非空性

**hashCode() 方法：**
- 返回对象的哈希码，是一个 int 类型的整数
- 用于哈希表（如 HashMap、HashSet）中确定对象的存储位置
- 默认实现是基于对象的内存地址计算

**两者关系：**
- 如果两个对象通过 `equals()` 比较相等，则它们的 `hashCode()` 必须相同
- 如果两个对象的 `hashCode()` 相同，它们不一定相等（哈希冲突）
- **重写 equals() 时必须同时重写 hashCode()**，以维护契约关系

```java
// 重写示例
@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Person person = (Person) obj;
    return id == person.id;
}

@Override
public int hashCode() {
    return Objects.hash(id);
}
```

---

#### Q2: 在什么场景下需要重写 equals() 和 hashCode() 方法？

**答案要点：**

**需要重写的场景：**

1. **作为 HashMap 的 Key 或 HashSet 的元素**
   - 不重写会导致相同逻辑值的对象被视为不同对象
   - 造成数据重复或查找失败

2. **业务对象比较**
   - 需要根据业务逻辑（如 ID）判断对象是否相等
   - 而非默认的内存地址比较

3. **对象去重**
   - 使用集合去重时，需要正确识别逻辑相等的对象

**示例场景：**
```java
// 用户对象根据 userId 判断相等
public class User {
    private Long userId;
    private String name;
    // 重写 equals 和 hashCode 基于 userId
}

// 使用场景
Set<User> userSet = new HashSet<>();
userSet.add(new User(1L, "张三"));
userSet.add(new User(1L, "李四")); // 如果不重写，会被视为不同对象
```

---

#### Q3: 两个对象的 hashCode() 相同，equals() 一定为 true 吗？

**答案要点：**

**不一定。**

- **hashCode() 相同，equals() 可能为 false**：这是哈希冲突的情况
- **equals() 为 true，hashCode() 一定相同**：这是 Java 契约规定的

**经典例子：**
```java
// "重地" 和 "通话" 的 hashCode 相同，但 equals 为 false
System.out.println("重地".hashCode()); // 1179395
System.out.println("通话".hashCode()); // 1179395
System.out.println("重地".equals("通话")); // false
```

**原因：**
- hashCode 的取值范围是 int（约 42 亿），而可能的字符串数量是无限的
- 根据鸽巢原理，必然存在不同的对象有相同的 hashCode

---

### 1.2 toString() 方法

#### Q4: 为什么要重写 toString() 方法？

**答案要点：**

**默认实现的不足：**
- Object 类的默认实现返回：`类名@十六进制哈希码`
- 如：`com.example.Person@6d06d69c`
- 信息有限，不利于调试和日志记录

**重写的好处：**
1. **便于调试**：直观显示对象的关键属性
2. **日志友好**：记录有意义的对象信息
3. **问题排查**：快速定位对象状态

**最佳实践：**
```java
@Override
public String toString() {
    return "Person{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", age=" + age +
            '}';
}

// 或使用 Objects.toStringHelper (Guava)
// 或使用 Lombok 的 @ToString 注解
```

---

### 1.3 clone() 方法

#### Q5: 谈谈你对 clone() 方法的理解？深拷贝和浅拷贝有什么区别？

**答案要点：**

**clone() 方法：**
- 创建并返回对象的一个副本
- 需要实现 `Cloneable` 标记接口，否则抛出 `CloneNotSupportedException`
- 是 native 方法，效率较高

**浅拷贝（Shallow Copy）：**
- 复制对象本身和基本类型字段
- 引用类型字段只复制引用地址，不复制引用对象
- 原对象和副本共享引用对象

**深拷贝（Deep Copy）：**
- 复制对象本身和所有引用对象
- 原对象和副本完全独立
- 实现方式：递归 clone、序列化、拷贝构造器

```java
// 浅拷贝示例
public class Person implements Cloneable {
    private String name;
    private Address address; // 引用类型
    
    @Override
    public Person clone() throws CloneNotSupportedException {
        return (Person) super.clone(); // 浅拷贝
    }
}

// 深拷贝示例
@Override
public Person deepClone() throws CloneNotSupportedException {
    Person cloned = (Person) super.clone();
    cloned.address = this.address.clone(); // 复制引用对象
    return cloned;
}
```

---

#### Q6: 如何实现深拷贝？有哪些方式？

**答案要点：**

**方式一：递归 clone**
```java
@Override
public Person deepClone() throws CloneNotSupportedException {
    Person cloned = (Person) super.clone();
    cloned.address = this.address != null ? this.address.clone() : null;
    return cloned;
}
```

**方式二：序列化（推荐）**
```java
public Object deepCloneBySerialization() {
    try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(this);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

**方式三：拷贝构造器**
```java
public Person(Person other) {
    this.name = other.name;
    this.address = other.address != null ? new Address(other.address) : null;
}
```

**方式四：第三方库（如 Apache Commons Lang）**
```java
// 使用 SerializationUtils
Person cloned = SerializationUtils.clone(original);
```

---

## 二、String 不可变性面试题

### 2.1 String 不可变性

#### Q7: String 为什么是不可变的？

**答案要点：**

**String 不可变的原因（3层原理）：**

**1. 安全性（Security）**
- 字符串常用于存储敏感信息（如数据库连接、文件路径）
- 不可变性防止数据被恶意篡改
- 保证网络连接、文件操作的安全性

**2. 线程安全（Thread Safety）**
- 不可变对象天然线程安全，无需同步
- 多个线程可以安全共享同一个 String 实例
- 避免并发访问问题

**3. 性能优化（Performance）**
- **字符串常量池（String Pool）**：相同的字符串只存储一份
- hashCode 缓存：计算一次后缓存，提高哈希表性能
- 节省内存，提高程序效率

**底层实现：**
```java
public final class String 
    implements java.io.Serializable, Comparable<String>, CharSequence {
    
    private final char value[];  // final 修饰，引用不可变
    private int hash;            // 缓存 hashCode
    
    // 没有提供修改 value[] 的方法
}
```

---

#### Q8: String 不可变性的优缺点是什么？

**答案要点：**

**优点：**
1. **线程安全**：无需同步，可安全共享
2. **安全性**：防止被篡改，保护敏感数据
3. **性能优化**：常量池复用、hashCode 缓存
4. **适合作为 Map 的 Key**：不会因为修改导致哈希值变化

**缺点：**
1. **频繁修改性能差**：每次修改都创建新对象
2. **内存开销**：大量临时对象需要 GC
3. **不适合频繁拼接场景**：需要使用 StringBuilder

**解决方案：**
- 频繁修改时使用 `StringBuilder`（单线程）或 `StringBuffer`（多线程）

---

### 2.2 字符串常量池

#### Q9: 什么是字符串常量池（String Pool）？

**答案要点：**

**定义：**
- 字符串常量池是 JVM 在堆内存中开辟的一块特殊区域
- 用于存储字符串字面量，实现字符串复用

**工作原理：**
```java
String s1 = "abc";  // 常量池创建 "abc"，s1 指向它
String s2 = "abc";  // 常量池已有 "abc"，s2 指向同一对象
String s3 = new String("abc"); // 堆中创建新对象

System.out.println(s1 == s2); // true，同一对象
System.out.println(s1 == s3); // false，不同对象
```

**intern() 方法：**
```java
String s4 = s3.intern(); // 将字符串放入常量池
System.out.println(s1 == s4); // true
```

**JDK 版本差异：**
- JDK 6：常量池在永久代（PermGen）
- JDK 7+：常量池在堆内存，可自动垃圾回收

---

### 2.3 String、StringBuilder、StringBuffer 对比

#### Q10: String、StringBuilder、StringBuffer 有什么区别？

**答案要点：**

| 特性 | String | StringBuilder | StringBuffer |
|------|--------|---------------|--------------|
| **可变性** | 不可变 | 可变 | 可变 |
| **线程安全** | 安全（不可变） | 不安全 | 安全（synchronized）|
| **性能** | 低（频繁创建对象） | 高 | 较高（有同步开销）|
| **使用场景** | 字符串常量 | 单线程字符串操作 | 多线程字符串操作 |
| **JDK版本** | 1.0 | 1.5 | 1.0 |

**底层实现：**
```java
// String：final char[]
private final char value[];

// StringBuilder/StringBuffer：可变 char[]
char[] value;
```

**性能对比示例：**
```java
// 低效：创建大量临时对象
String s = "";
for (int i = 0; i < 10000; i++) {
    s += i; // 每次循环都创建新 String 对象
}

// 高效：只创建一个 StringBuilder
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10000; i++) {
    sb.append(i);
}
String s = sb.toString();
```

---

#### Q11: 字符串拼接用 "+" 还是 StringBuilder？

**答案要点：**

**编译器优化：**
- 字符串字面量拼接：编译器自动优化为 StringBuilder
```java
String s = "a" + "b" + "c"; // 编译时直接优化为 "abc"
```

- 循环中的拼接：编译器无法优化，每次循环都创建新对象
```java
// 编译后每次循环都创建 StringBuilder
String s = "";
for (int i = 0; i < n; i++) {
    s = s + i; // 低效！
}
```

**建议：**
1. **简单拼接（字面量）**：使用 `+`，编译器会优化
2. **循环或复杂拼接**：显式使用 `StringBuilder`
3. **多线程环境**：使用 `StringBuffer` 或 `StringBuilder` + 同步

---

## 三、System 系统操作面试题

### 3.1 System 类常用方法

#### Q12: System 类有哪些常用方法？

**答案要点：**

**常用方法：**

| 方法 | 作用 | 示例 |
|------|------|------|
| `System.currentTimeMillis()` | 获取当前时间（毫秒） | 计算代码执行时间 |
| `System.nanoTime()` | 获取高精度时间（纳秒） | 精确计时 |
| `System.arraycopy()` | 高效数组拷贝 | 比 for 循环快 |
| `System.getProperty()` | 获取系统属性 | 获取 Java 版本、OS 名称 |
| `System.setProperty()` | 设置系统属性 | 动态配置 |
| `System.getenv()` | 获取环境变量 | 读取 PATH 等 |
| `System.gc()` | 建议 JVM 进行 GC | 不保证立即执行 |
| `System.exit()` | 终止 JVM | 0 正常退出，非 0 异常 |

**arraycopy 示例：**
```java
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[5];
System.arraycopy(src, 0, dest, 0, 5);
// 比 for 循环快，因为是 native 方法
```

---

#### Q13: System.currentTimeMillis() 和 System.nanoTime() 有什么区别？

**答案要点：**

| 特性 | currentTimeMillis() | nanoTime() |
|------|---------------------|------------|
| **单位** | 毫秒 | 纳秒 |
| **基准** | 1970-01-01 00:00:00 UTC | 任意起点（JVM 启动时间）|
| **用途** | 获取当前时间 | 测量时间间隔 |
| **精度** | 毫秒级 | 纳秒级 |
| **跨 JVM** | 可比较 | 不可比较 |

**使用场景：**
```java
// 获取当前时间戳
long timestamp = System.currentTimeMillis();

// 测量代码执行时间
long start = System.nanoTime();
// ... 执行代码
long end = System.nanoTime();
long duration = end - start; // 纳秒
```

---

## 四、Thread 线程管理面试题

### 4.1 线程基础

#### Q14: 线程的生命周期有哪些状态？

**答案要点：**

**Java 线程的 6 种状态（Thread.State）：**

```
NEW（新建）
  ↓ start()
RUNNABLE（可运行）←→ BLOCKED（阻塞）
  ↓ wait()          ↓ 等待锁
WAITING（无限等待）  ↓ 获取锁
  ↓ notify()/notifyAll()
TIMED_WAITING（限时等待）
  ↓ 超时/中断
TERMINATED（终止）
```

| 状态 | 说明 | 触发条件 |
|------|------|----------|
| **NEW** | 新建状态 | 创建 Thread 对象，未调用 start() |
| **RUNNABLE** | 可运行 | 调用 start()，等待或正在运行 |
| **BLOCKED** | 阻塞 | 等待监视器锁（synchronized）|
| **WAITING** | 无限等待 | wait()、join()、LockSupport.park() |
| **TIMED_WAITING** | 限时等待 | sleep(time)、wait(time)、join(time) |
| **TERMINATED** | 终止 | run() 执行完毕或异常退出 |

**注意：**
- 没有 "运行中"（RUNNING）状态，RUNNABLE 包含就绪和运行
- BLOCKED 只针对 synchronized，Lock 的等待是 WAITING

---

#### Q15: 创建线程有哪几种方式？

**答案要点：**

**方式一：继承 Thread 类**
```java
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread running");
    }
}
MyThread t = new MyThread();
t.start();
```

**方式二：实现 Runnable 接口（推荐）**
```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable running");
    }
}
Thread t = new Thread(new MyRunnable());
t.start();

// Lambda 简化
Thread t = new Thread(() -> System.out.println("Running"));
```

**方式三：实现 Callable 接口（有返回值）**
```java
Callable<String> callable = () -> {
    return "Result";
};
FutureTask<String> futureTask = new FutureTask<>(callable);
new Thread(futureTask).start();
String result = futureTask.get(); // 阻塞获取结果
```

**方式四：线程池（最佳实践）**
```java
ExecutorService executor = Executors.newFixedThreadPool(10);
executor.submit(() -> System.out.println("Pool thread"));
executor.shutdown();
```

**推荐：**
- 优先使用线程池，避免频繁创建销毁线程
- 其次使用 Runnable（解耦任务和线程）

---

### 4.2 线程同步

#### Q16: sleep() 和 wait() 有什么区别？

**答案要点：**

| 特性 | sleep() | wait() |
|------|---------|--------|
| **所属类** | Thread | Object |
| **调用方式** | Thread.sleep() | object.wait() |
| **锁释放** | 不释放锁 | 释放锁 |
| **唤醒方式** | 超时自动唤醒 | notify()/notifyAll() |
| **使用场景** | 暂停执行 | 线程间通信 |
| **异常** | InterruptedException | InterruptedException |

**代码示例：**
```java
// sleep：不释放锁
synchronized (lock) {
    Thread.sleep(1000); // 持有锁睡眠
}

// wait：释放锁
synchronized (lock) {
    lock.wait(); // 释放锁，进入等待
    // 被唤醒后重新获取锁
}
```

---

#### Q17: 线程间如何通信？

**答案要点：**

**方式一：wait/notify（传统方式）**
```java
synchronized (sharedObject) {
    // 等待条件
    while (!condition) {
        sharedObject.wait();
    }
    // 执行业务
    sharedObject.notifyAll(); // 唤醒其他线程
}
```

**方式二：Lock/Condition（JUC）**
```java
Lock lock = new ReentrantLock();
Condition condition = lock.newCondition();

lock.lock();
try {
    while (!conditionMet) {
        condition.await();
    }
    condition.signalAll();
} finally {
    lock.unlock();
}
```

**方式三：阻塞队列**
```java
BlockingQueue<String> queue = new LinkedBlockingQueue<>();
// 生产者
queue.put("item");
// 消费者
String item = queue.take();
```

**方式四：CompletableFuture（异步编程）**
```java
CompletableFuture.supplyAsync(() -> produce())
    .thenApply(result -> process(result));
```

---

### 4.3 ThreadLocal

#### Q18: ThreadLocal 是什么？有什么使用场景？

**答案要点：**

**定义：**
- ThreadLocal 提供线程局部变量
- 每个线程拥有独立的变量副本，互不干扰

**原理：**
```java
// Thread 类中有 ThreadLocalMap
ThreadLocal.ThreadLocalMap threadLocals = null;

// ThreadLocal 的 set 方法
public void set(T value) {
    Thread t = Thread.currentThread();
    ThreadLocalMap map = getMap(t);
    if (map != null)
        map.set(this, value);
    else
        createMap(t, value);
}
```

**使用场景：**

1. **用户会话信息**
```java
public class UserContext {
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    
    public static void set(User user) {
        currentUser.set(user);
    }
    
    public static User get() {
        return currentUser.get();
    }
    
    public static void remove() {
        currentUser.remove(); // 防止内存泄漏
    }
}
```

2. **数据库连接管理**
```java
private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();
```

3. **事务管理**
4. **日期格式化（SimpleDateFormat 非线程安全）**

**注意事项：**
- 使用完必须调用 `remove()`，防止内存泄漏
- 线程池场景下尤其要注意清理

---

## 五、反射机制面试题

### 5.1 反射基础

#### Q19: 什么是 Java 反射？有什么优缺点？

**答案要点：**

**定义：**
- 反射（Reflection）是 Java 的核心特性之一
- 允许程序在运行时动态获取类的信息并操作类或对象
- 主要使用 `java.lang.reflect` 包

**核心类：**
- `Class`：类的元数据
- `Field`：类的字段
- `Method`：类的方法
- `Constructor`：类的构造器

**优点：**
1. **灵活性**：运行时动态创建对象、调用方法
2. **扩展性**：通过配置文件加载类（如 Spring）
3. **框架开发**：实现依赖注入、ORM 等

**缺点：**
1. **性能损耗**：比直接调用慢（JVM 优化后差距减小）
2. **安全性**：破坏封装，可访问私有成员
3. **可读性差**：代码复杂，难以维护
4. **编译时检查缺失**：运行时才发现错误

**基本使用：**
```java
// 获取 Class 对象
Class<?> clazz = Class.forName("com.example.Person");

// 创建实例
Object obj = clazz.newInstance();

// 获取方法
Method method = clazz.getMethod("sayHello", String.class);
method.invoke(obj, "World");

// 获取字段
Field field = clazz.getDeclaredField("name");
field.setAccessible(true); // 访问私有字段
field.set(obj, "张三");
```

---

#### Q20: 如何获取 Class 对象？

**答案要点：**

**三种方式：**

```java
// 方式一：类名.class（编译期确定）
Class<Person> clazz1 = Person.class;

// 方式二：对象.getClass()（运行时确定）
Person person = new Person();
Class<? extends Person> clazz2 = person.getClass();

// 方式三：Class.forName()（动态加载，最灵活）
Class<?> clazz3 = Class.forName("com.example.Person");
```

**比较：**

| 方式 | 使用场景 | 特点 |
|------|----------|------|
| `类名.class` | 编译时已知类型 | 类型安全，性能好 |
| `对象.getClass()` | 已有对象实例 | 获取实际类型 |
| `Class.forName()` | 运行时动态加载 | 最灵活，可加载配置中的类 |

**注意：**
- 三种方式获取的 Class 对象是同一个（JVM 保证单例）
```java
clazz1 == clazz2 == clazz3; // true
```

---

### 5.2 动态代理

#### Q21: 什么是动态代理？JDK 动态代理和 CGLIB 有什么区别？

**答案要点：**

**动态代理：**
- 在运行时动态创建代理类，无需手动编写
- 常用于 AOP、事务管理、日志记录等

**JDK 动态代理：**
```java
// 必须实现接口
public class JdkProxy implements InvocationHandler {
    private Object target;
    
    public Object createProxy(Object target) {
        this.target = target;
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            this
        );
    }
    
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("Before");
        Object result = method.invoke(target, args);
        System.out.println("After");
        return result;
    }
}
```

**对比：**

| 特性 | JDK 动态代理 | CGLIB |
|------|--------------|-------|
| **依赖** | 内置 JDK | 第三方库 |
| **要求** | 必须实现接口 | 不需要接口 |
| **原理** | 实现接口 | 继承类（生成子类）|
| **final 方法** | 支持 | 不支持（无法重写）|
| **性能** | 稍慢（反射调用）| 较快（方法索引）|
| **Spring 默认** | 有接口时用 | 无接口时用 |

**Spring 选择：**
- 目标类实现接口 → JDK 动态代理
- 目标类无接口 → CGLIB
- 可强制使用 CGLIB：`@EnableAspectJAutoProxy(proxyTargetClass = true)`

---

## 六、包装类与自动装箱面试题

### 6.1 包装类基础

#### Q22: Java 为什么要设计包装类？

**答案要点：**

**原因：**

1. **集合框架需要对象**
   - 集合（如 List、Map）只能存储对象，不能存储基本类型
   - `List<int>` 错误，`List<Integer>` 正确

2. **提供实用方法**
   - 类型转换：`Integer.parseInt()`、`String.valueOf()`
   - 常量值：`Integer.MAX_VALUE`
   - 工具方法：`Integer.toBinaryString()`

3. **泛型支持**
   - 泛型只能使用引用类型
   - `<T>` 中的 T 必须是对象

4. **null 值表示**
   - 基本类型有默认值，无法表示"无值"
   - 包装类可用 null 表示缺失值

**对应关系：**

| 基本类型 | 包装类 |
|----------|--------|
| byte | Byte |
| short | Short |
| int | Integer |
| long | Long |
| float | Float |
| double | Double |
| char | Character |
| boolean | Boolean |

---

### 6.2 自动装箱拆箱

#### Q23: 什么是自动装箱和拆箱？

**答案要点：**

**自动装箱（Autoboxing）：**
- 基本类型 → 包装类
- 编译器自动调用 `valueOf()`

**自动拆箱（Unboxing）：**
- 包装类 → 基本类型
- 编译器自动调用 `xxxValue()`

```java
// 自动装箱
Integer num = 10; // 实际：Integer.valueOf(10)

// 自动拆箱
int n = num; // 实际：num.intValue()

// 运算时自动拆箱
Integer a = 10;
Integer b = 20;
int sum = a + b; // 先拆箱再相加
```

**注意点：**
```java
// 可能抛出 NullPointerException
Integer num = null;
int n = num; // 自动拆箱：num.intValue() → NPE

// 比较陷阱
Integer a = 100;
Integer b = 100;
System.out.println(a == b); // true（缓存）

Integer c = 200;
Integer d = 200;
System.out.println(c == d); // false（超出缓存范围）
```

---

### 6.3 包装类缓存机制

#### Q24: 包装类的缓存机制是什么？

**答案要点：**

**缓存范围：**

| 包装类 | 缓存范围 |
|--------|----------|
| Byte | -128 ~ 127（全部缓存）|
| Short | -128 ~ 127 |
| Integer | -128 ~ 127（默认，可配置）|
| Long | -128 ~ 127 |
| Character | 0 ~ 127 |
| Boolean | true、false（全部缓存）|
| Float、Double | 不缓存 |

**原理：**
```java
// Integer.valueOf() 源码
public static Integer valueOf(int i) {
    if (i >= IntegerCache.low && i <= IntegerCache.high)
        return IntegerCache.cache[i + (-IntegerCache.low)];
    return new Integer(i);
}

// 缓存类
private static class IntegerCache {
    static final int low = -128;
    static final int high = 127; // 可通过 -XX:AutoBoxCacheMax=size 调整
    static final Integer cache[] = new Integer[high - low + 1];
}
```

**经典面试题：**
```java
Integer a = 100, b = 100;
Integer c = 200, d = 200;

System.out.println(a == b); // true（缓存内）
System.out.println(c == d); // false（缓存外，新建对象）

// 正确比较方式
System.out.println(c.equals(d)); // true
System.out.println(c.intValue() == d.intValue()); // true
```

---

#### Q25: Integer 的缓存范围可以调整吗？

**答案要点：**

**可以调整。**

**JVM 参数：**
```bash
-XX:AutoBoxCacheMax=1000
```

**效果：**
- 默认缓存范围：-128 ~ 127
- 调整后：-128 ~ 1000

**注意：**
- 只影响 Integer，不影响其他包装类
- 需要在 JVM 启动时设置
- 过大的缓存会占用更多内存

**示例：**
```java
// 默认情况下
Integer a = 200;
Integer b = 200;
System.out.println(a == b); // false

// 设置 -XX:AutoBoxCacheMax=500 后
Integer c = 200;
Integer d = 200;
System.out.println(c == d); // true
```

---

## 附录：面试题速查表

### Object 相关
| 问题 | 核心要点 |
|------|----------|
| equals 和 hashCode 关系 | equals 相等则 hashCode 必须相等；反之不然 |
| 为什么重写 equals 要重写 hashCode | 维护契约关系，保证 HashMap/HashSet 正常工作 |
| 深拷贝 vs 浅拷贝 | 浅拷贝共享引用对象；深拷贝完全独立 |

### String 相关
| 问题 | 核心要点 |
|------|----------|
| String 为什么不可变 | 安全、线程安全、常量池复用 |
| StringBuilder vs StringBuffer | Builder 非线程安全但快；Buffer 线程安全但慢 |
| 常量池位置 | JDK6 在永久代；JDK7+ 在堆内存 |

### Thread 相关
| 问题 | 核心要点 |
|------|----------|
| 线程状态 | NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED |
| sleep vs wait | sleep 不释放锁；wait 释放锁 |
| ThreadLocal 原理 | Thread 内部维护 ThreadLocalMap，key 是 ThreadLocal 弱引用 |

### 反射相关
| 问题 | 核心要点 |
|------|----------|
| 反射优缺点 | 灵活但性能差、破坏封装 |
| 获取 Class 三种方式 | 类名.class、对象.getClass()、Class.forName() |
| JDK 代理 vs CGLIB | JDK 需要接口；CGLIB 继承类 |

### 包装类相关
| 问题 | 核心要点 |
|------|----------|
| 自动装箱/拆箱 | 编译器自动插入 valueOf()/xxxValue() |
| Integer 缓存范围 | -128 ~ 127（可调整） |
| 比较陷阱 | 缓存内用 == 为 true；缓存外为 false；始终用 equals |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26  
**适用版本**: Java 8+
