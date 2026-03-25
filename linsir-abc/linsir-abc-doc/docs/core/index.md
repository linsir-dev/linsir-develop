# JDK 8 源码架构全景分析

## 一、JDK 8 整体架构概览

### 1.1 源码目录结构

```
jdk8-src/
├── java.base/                    # 基础模块 - 核心API
│   ├── java/lang/               # 核心类（Object、String、Thread等）
│   ├── java/util/               # 工具类（集合、Stream、日期等）
│   ├── java/io/                 # IO操作
│   ├── java/nio/                # NIO（New IO）
│   ├── java/net/                # 网络编程
│   ├── java/time/               # JDK 8 新日期时间API
│   ├── java/util/function/      # 函数式接口
│   ├── java/util/stream/        # Stream API
│   ├── java/util/concurrent/    # 并发包
│   ├── java/math/               # 数学运算
│   ├── java/security/           # 安全
│   ├── java/text/               # 文本处理
│   └── java/lang/reflect/       # 反射
├── java.desktop/                # GUI相关（AWT、Swing）
├── java.sql/                    # 数据库操作（JDBC）
├── java.xml/                    # XML处理
├── java.rmi/                    # 远程方法调用
├── java.scripting/              # 脚本引擎
├── java.compiler/               # 编译器API
├── java.instrument/             # 字节码插桩
├── java.logging/                # 日志
├── java.management/             # JMX管理
├── java.naming/                 # JNDI命名服务
├── java.prefs/                  # 偏好设置
├── java.sql.rowset/             # JDBC RowSet
└── jdk/                         # JDK内部实现
    ├── jdk/internal/            # 内部类
    ├── jdk/nashorn/             # JavaScript引擎
    └── jdk/jfr/                 # Java Flight Recorder
```

### 1.2 JDK 8 模块体系

| 模块 | 说明 | 核心包 |
|------|------|--------|
| **java.base** | 基础模块，所有模块依赖 | java.lang, java.util, java.io |
| **java.desktop** | 桌面GUI | java.awt, javax.swing |
| **java.sql** | 数据库访问 | java.sql, javax.sql |
| **java.xml** | XML处理 | javax.xml |
| **java.rmi** | 远程调用 | java.rmi |
| **java.management** | 管理和监控 | java.lang.management |
| **java.logging** | 日志 | java.util.logging |
| **java.security** | 安全 | java.security |

---

## 二、java.lang 包源码架构

### 2.1 Object 类 - 万物之源

```java
public class Object {
    // 本地方法：获取对象的哈希码
    public native int hashCode();
    
    // 本地方法：获取对象的Class对象
    public final native Class<?> getClass();
    
    // 本地方法：创建对象的副本
    protected native Object clone() throws CloneNotSupportedException;
    
    // 本地方法：唤醒等待的线程
    public final native void notify();
    public final native void notifyAll();
    public final native void wait(long timeout) throws InterruptedException;
    
    // 垃圾回收前调用
    protected void finalize() throws Throwable { }
    
    // 比较相等性
    public boolean equals(Object obj) {
        return (this == obj);
    }
    
    // 字符串表示
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
}
```

**核心设计：**
- 所有类的根类
- 定义了对象的基本行为
- 提供线程同步机制（wait/notify）
- 支持克隆和垃圾回收

### 2.2 String 类 - 不可变字符串

```
java.lang.String
├── 核心字段
│   ├── value[]: char/byte      # 字符数组（JDK 9+ 使用byte[]）
│   ├── hash: int               # 缓存的哈希值
│   └── hashIsZero: boolean     # 哈希值是否为0
│
├── 构造方法
│   ├── String()                # 空字符串
│   ├── String(String original) # 拷贝构造
│   ├── String(char[] value)    # 从字符数组
│   └── String(byte[] bytes, Charset charset) # 从字节数组
│
├── 核心方法
│   ├── length()                # 长度
│   ├── charAt(int index)       # 获取字符
│   ├── substring(int begin, int end) # 子串
│   ├── indexOf(String str)     # 查找位置
│   ├── equals(Object anObject) # 相等比较
│   ├── hashCode()              # 哈希码
│   ├── concat(String str)      # 连接
│   ├── replace(char old, char new) # 替换
│   ├── split(String regex)     # 分割
│   └── trim()                  # 去空格
│
├── 静态方法
│   ├── valueOf(Object obj)     # 转为字符串
│   ├── format(String format, Object... args) # 格式化
│   ├── join(CharSequence delimiter, CharSequence... elements) # JDK 8新增
│   └── compare(String s1, String s2) # 比较
```

**不可变性的优势：**
1. **线程安全**：多线程环境下无需同步
2. **哈希缓存**：hashCode可缓存，提高性能
3. **字符串常量池**：节省内存
4. **安全**：参数传递不会被修改

### 2.3 System 类 - 系统操作

```
java.lang.System
├── 标准流
│   ├── out: PrintStream        # 标准输出
│   ├── err: PrintStream        # 标准错误
│   └── in: InputStream         # 标准输入
│
├── 环境信息
│   ├── getProperty(String key) # 获取系统属性
│   ├── getenv(String name)     # 获取环境变量
│   └── getProperties()         # 获取所有属性
│
├── 数组操作
│   ├── arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
│   └── currentTimeMillis()     # 当前时间毫秒
│
├── 系统控制
│   ├── exit(int status)        # 退出JVM
│   ├── gc()                    # 建议垃圾回收
│   └── runFinalization()       # 运行finalize方法
│
└── 安全管理
    ├── setSecurityManager(SecurityManager s)
    └── getSecurityManager()
```

### 2.4 Thread 类 - 线程管理

```
java.lang.Thread
├── 线程状态
│   ├── NEW                     # 新建
│   ├── RUNNABLE                # 可运行
│   ├── BLOCKED                 # 阻塞
│   ├── WAITING                 # 等待
│   ├── TIMED_WAITING           # 限时等待
│   └── TERMINATED              # 终止
│
├── 核心方法
│   ├── start()                 # 启动线程
│   ├── run()                   # 线程执行体
│   ├── join()                  # 等待线程结束
│   ├── sleep(long millis)      # 睡眠
│   ├── yield()                 # 让出CPU
│   ├── interrupt()             # 中断线程
│   └── isAlive()               # 是否存活
│
├── 线程属性
│   ├── setName(String name)    # 设置名称
│   ├── setPriority(int priority) # 设置优先级
│   ├── setDaemon(boolean on)   # 设置守护线程
│   └── setUncaughtExceptionHandler(UncaughtExceptionHandler eh)
│
└── 线程局部变量
    └── ThreadLocal<T>
        ├── get()               # 获取值
        ├── set(T value)        # 设置值
        └── remove()            # 移除值
```

### 2.5 Class 类 - 运行时类型信息

```
java.lang.Class<T>
├── 类信息获取
│   ├── getName()               # 类全名
│   ├── getSimpleName()         # 简单名
│   ├── getPackage()            # 包信息
│   ├── getSuperclass()         # 父类
│   ├── getInterfaces()         # 接口
│   └── getModifiers()          # 修饰符
│
├── 成员获取
│   ├── getFields()             # 获取字段
│   ├── getMethods()            # 获取方法
│   ├── getConstructors()       # 获取构造器
│   ├── getDeclaredFields()     # 获取声明的字段
│   └── getDeclaredMethods()    # 获取声明的方法
│
├── 实例创建
│   ├── newInstance()           # 创建实例（已废弃）
│   ├── getConstructor().newInstance() # 推荐方式
│   └── getDeclaredConstructor().newInstance()
│
├── 注解处理
│   ├── getAnnotation(Class<A> annotationClass)
│   ├── getAnnotations()
│   └── isAnnotationPresent(Class<? extends Annotation> annotationClass)
│
└── 类型检查
    ├── isInstance(Object obj)  # 是否实例
    ├── isAssignableFrom(Class<?> cls) # 是否可赋值
    ├── isInterface()           # 是否接口
    ├── isArray()               # 是否数组
    └── isPrimitive()           # 是否基本类型
```

---

## 三、java.util 包源码架构

### 3.1 集合框架体系

```
java.util.Collection
├── List（有序、可重复）
│   ├── ArrayList              # 动态数组实现
│   ├── LinkedList             # 双向链表实现
│   └── Vector                 # 线程安全动态数组
│       └── Stack              # 栈（继承Vector）
│
├── Set（无序、不可重复）
│   ├── HashSet                # 哈希表实现
│   ├── LinkedHashSet          # 保持插入顺序
│   └── TreeSet                # 红黑树实现（有序）
│       └── NavigableSet接口
│
└── Queue（队列）
    ├── LinkedList             # 双端队列
    ├── PriorityQueue          # 优先队列（堆实现）
    └── ArrayDeque             # 数组双端队列

java.util.Map
├── HashMap                    # 哈希表实现
├── LinkedHashMap              # 保持插入顺序
├── TreeMap                    # 红黑树实现（有序）
├── Hashtable                  # 线程安全哈希表
└── WeakHashMap                # 弱引用键
```

### 3.2 ArrayList 源码分析

```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    
    // 默认初始容量
    private static final int DEFAULT_CAPACITY = 10;
    
    // 空数组（用于空实例）
    private static final Object[] EMPTY_ELEMENTDATA = {};
    
    // 默认空数组（用于默认构造）
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    
    // 存储元素的数组
    transient Object[] elementData;
    
    // 实际元素数量
    private int size;
    
    // 扩容机制
    private void grow(int minCapacity) {
        int oldCapacity = elementData.length;
        // 扩容1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
}
```

**关键设计：**
- **动态扩容**：容量不足时扩容1.5倍
- **数组拷贝**：使用 `Arrays.copyOf` 扩容
- **快速随机访问**：支持 `RandomAccess` 接口
- **非线程安全**：多线程需外部同步或使用 `Collections.synchronizedList`

### 3.3 HashMap 源码分析

```java
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    
    // 默认初始容量（必须是2的幂）
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 16
    
    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30;
    
    // 默认负载因子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    // 链表转红黑树阈值
    static final int TREEIFY_THRESHOLD = 8;
    
    // 红黑树转链表阈值
    static final int UNTREEIFY_THRESHOLD = 6;
    
    // 最小树化容量
    static final int MIN_TREEIFY_CAPACITY = 64;
    
    // 节点数组
    transient Node<K,V>[] table;
    
    // 节点数量
    transient int size;
    
    // 扩容阈值
    int threshold;
    
    // 负载因子
    final float loadFactor;
    
    // 基本节点类
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
    }
    
    // 树节点（红黑树）
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;
        boolean red;
    }
}
```

**JDK 8 优化：**
1. **链表转红黑树**：当链表长度超过8时转为红黑树，查找复杂度从O(n)降到O(log n)
2. **扩容优化**：重新计算hash时，只需要判断hash新增的高位是0还是1
3. **尾插法**：避免多线程环境下的死循环问题

### 3.4 ConcurrentHashMap 源码分析

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {
    
    // 最大容量
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    
    // 默认容量
    private static final int DEFAULT_CAPACITY = 16;
    
    // 负载因子
    private static final float LOAD_FACTOR = 0.75f;
    
    // 链表转树阈值
    static final int TREEIFY_THRESHOLD = 8;
    
    // 树转链表阈值
    static final int UNTREEIFY_THRESHOLD = 6;
    
    // 最小树化容量
    static final int MIN_TREEIFY_CAPACITY = 64;
    
    // 最小转移步长
    private static final int MIN_TRANSFER_STRIDE = 16;
    
    // 扩容时生成标记
    private static final int MOVED     = -1; // 正在转移
    private static final int TREEBIN   = -2; // 红黑树
    private static final int RESERVED  = -3; // 保留
    
    // 节点数组（volatile保证可见性）
    transient volatile Node<K,V>[] table;
    
    // 下一个要转移的表索引
    private transient volatile int transferIndex;
    
    // 基础计数器
    private transient volatile long baseCount;
    
    // 控制标识
    private transient volatile int sizeCtl;
    
    // 转移时的临时表
    private transient volatile Node<K,V>[] nextTable;
}
```

**线程安全机制：**
1. **CAS操作**：使用 `Unsafe` 类的CAS方法进行无锁更新
2. **分段锁**：JDK 7使用Segment，JDK 8使用synchronized + CAS
3. **volatile**：保证数组引用的可见性
4. **红黑树**：解决hash冲突，提高并发性能

---

## 四、java.io 和 java.nio 包架构

### 4.1 IO 流体系

```
java.io
├── 字节流
│   ├── InputStream
│   │   ├── FileInputStream
│   │   ├── ByteArrayInputStream
│   │   ├── BufferedInputStream
│   │   ├── DataInputStream
│   │   └── ObjectInputStream
│   └── OutputStream
│       ├── FileOutputStream
│       ├── ByteArrayOutputStream
│       ├── BufferedOutputStream
│       ├── DataOutputStream
│       └── ObjectOutputStream
│
├── 字符流
│   ├── Reader
│   │   ├── FileReader
│   │   ├── BufferedReader
│   │   ├── InputStreamReader
│   │   └── StringReader
│   └── Writer
│       ├── FileWriter
│       ├── BufferedWriter
│       ├── OutputStreamWriter
│       └── StringWriter
│
└── 文件操作
    ├── File
    ├── RandomAccessFile
    └── FileDescriptor
```

### 4.2 NIO 核心组件

```
java.nio
├── Buffer（缓冲区）
│   ├── ByteBuffer
│   ├── CharBuffer
│   ├── ShortBuffer
│   ├── IntBuffer
│   ├── LongBuffer
│   ├── FloatBuffer
│   └── DoubleBuffer
│
├── Channel（通道）
│   ├── FileChannel
│   ├── SocketChannel
│   ├── ServerSocketChannel
│   └── DatagramChannel
│
└── Selector（选择器）
    └── Selector

java.nio.charset
├── Charset
├── CharsetEncoder
└── CharsetDecoder

java.nio.file
├── Path
├── Paths
├── Files
├── FileSystem
└── WatchService
```

---

## 五、java.util.concurrent 并发包架构

### 5.1 并发集合

```
java.util.concurrent
├── ConcurrentHashMap           # 线程安全HashMap
├── CopyOnWriteArrayList        # 写时复制List
├── CopyOnWriteArraySet         # 写时复制Set
├── ConcurrentLinkedQueue       # 并发链表队列
├── ConcurrentLinkedDeque       # 并发双端队列
├── LinkedBlockingQueue         # 链表阻塞队列
├── ArrayBlockingQueue          # 数组阻塞队列
├── PriorityBlockingQueue       # 优先阻塞队列
├── DelayQueue                  # 延迟队列
├── SynchronousQueue            # 同步队列
└── LinkedTransferQueue         # 传输队列
```

### 5.2 线程池框架

```
java.util.concurrent
├── Executor                    # 执行器接口
│   └── execute(Runnable command)
│
├── ExecutorService             # 带生命周期管理
│   ├── submit()                # 提交任务
│   ├── invokeAll()             # 批量执行
│   ├── shutdown()              # 优雅关闭
│   └── awaitTermination()      # 等待终止
│
├── ThreadPoolExecutor          # 线程池实现
│   ├── corePoolSize            # 核心线程数
│   ├── maximumPoolSize         # 最大线程数
│   ├── keepAliveTime           # 空闲存活时间
│   ├── workQueue               # 任务队列
│   ├── threadFactory           # 线程工厂
│   └── rejectedExecutionHandler # 拒绝策略
│
├── ScheduledExecutorService    # 定时任务
│   ├── schedule()              # 延迟执行
│   ├── scheduleAtFixedRate()   # 固定频率
│   └── scheduleWithFixedDelay() # 固定延迟
│
└── Executors                   # 工厂类
    ├── newFixedThreadPool()    # 固定大小线程池
    ├── newCachedThreadPool()   # 缓存线程池
    ├── newSingleThreadExecutor() # 单线程池
    └── newScheduledThreadPool() # 定时线程池
```

### 5.3 锁机制

```
java.util.concurrent.locks
├── Lock                        # 锁接口
│   ├── lock()                  # 获取锁
│   ├── unlock()                # 释放锁
│   ├── tryLock()               # 尝试获取锁
│   └── newCondition()          # 创建条件
│
├── ReentrantLock               # 可重入锁
│   ├── fair                    # 公平锁标志
│   └── sync                    # 同步器
│
├── ReadWriteLock               # 读写锁接口
│   ├── readLock()              # 读锁
│   └── writeLock()             # 写锁
│
├── ReentrantReadWriteLock      # 可重入读写锁
│
├── Condition                   # 条件变量
│   ├── await()                 # 等待
│   ├── signal()                # 唤醒一个
│   └── signalAll()             # 唤醒所有
│
└── LockSupport                 # 锁支持类
    ├── park()                  # 阻塞线程
    └── unpark()                # 唤醒线程
```

### 5.4 AQS（AbstractQueuedSynchronizer）

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer
    implements java.io.Serializable {
    
    // 同步状态（volatile）
    private volatile int state;
    
    // 等待队列头节点
    private transient volatile Node head;
    
    // 等待队列尾节点
    private transient volatile Node tail;
    
    // 独占线程
    private transient Thread exclusiveOwnerThread;
    
    // 等待节点
    static final class Node {
        volatile int waitStatus;      // 等待状态
        volatile Node prev;           // 前驱节点
        volatile Node next;           // 后继节点
        volatile Thread thread;       // 绑定的线程
        Node nextWaiter;              // 下一个等待节点
    }
    
    // 获取锁（模板方法）
    public final void acquire(int arg) {
        if (!tryAcquire(arg) &&
            acquireQueued(addWaiter(Node.EXCLUSIVE), arg))
            selfInterrupt();
    }
    
    // 释放锁（模板方法）
    public final boolean release(int arg) {
        if (tryRelease(arg)) {
            Node h = head;
            if (h != null && h.waitStatus != 0)
                unparkSuccessor(h);
            return true;
        }
        return false;
    }
}
```

**AQS 核心设计：**
- **状态变量**：`state` 表示同步状态
- **CLH队列**：FIFO等待队列
- **模板方法模式**：子类实现 `tryAcquire`/`tryRelease`
- **CAS操作**：保证原子性

---

## 六、java.net 网络编程架构

### 6.1 Socket 编程

```
java.net
├── Socket                      # 客户端套接字
│   ├── connect()               # 连接服务器
│   ├── getInputStream()        # 获取输入流
│   ├── getOutputStream()       # 获取输出流
│   └── close()                 # 关闭连接
│
├── ServerSocket                # 服务器套接字
│   ├── bind()                  # 绑定地址
│   ├── accept()                # 接受连接
│   └── close()                 # 关闭
│
├── DatagramSocket              # UDP套接字
│   ├── send()                  # 发送数据包
│   └── receive()               # 接收数据包
│
├── DatagramPacket              # 数据包
│
├── InetAddress                 # IP地址
│   ├── getByName()             # 通过主机名获取
│   ├── getLocalHost()          # 获取本机地址
│   └── getHostAddress()        # 获取IP地址字符串
│
├── URL                         # 统一资源定位符
│   ├── openConnection()        # 打开连接
│   ├── openStream()            # 打开输入流
│   └── getContent()            # 获取内容
│
└── URLConnection               # URL连接
    ├── connect()               # 建立连接
    ├── getInputStream()        # 获取输入流
    └── getOutputStream()       # 获取输出流
```

---

## 七、JDK 8 新特性模块（详细版）

### 7.1 Lambda 表达式与函数式接口

**Lambda 表达式语法：**
```java
// 无参数
() -> System.out.println("Hello")

// 单个参数
x -> x * 2

// 多个参数
(x, y) -> x + y

// 代码块
(x, y) -> {
    int sum = x + y;
    return sum;
}

// 方法引用
String::length
System.out::println
```

**函数式接口详解：**

| 接口 | 方法签名 | 用途 | 示例 |
|------|----------|------|------|
| Consumer<T> | void accept(T t) | 消费数据 | list.forEach(System.out::println) |
| Supplier<T> | T get() | 生产数据 | () -> new Random().nextInt() |
| Function<T,R> | R apply(T t) | 转换数据 | s -> s.length() |
| Predicate<T> | boolean test(T t) | 判断条件 | s -> s.length() > 5 |
| UnaryOperator<T> | T apply(T t) | 一元操作 | x -> x * 2 |
| BinaryOperator<T> | T apply(T t1, T t2) | 二元操作 | (x, y) -> x + y |

### 7.2 Stream API 详解

**Stream 创建方式：**
```java
// 从集合创建
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream1 = list.stream();
Stream<String> parallelStream = list.parallelStream();

// 从数组创建
Stream<String> stream2 = Arrays.stream(new String[]{"a", "b"});

// 使用Stream.of
Stream<String> stream3 = Stream.of("a", "b", "c");

// 创建无限流
Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2);
Stream<Double> randomStream = Stream.generate(Math::random);

// 空流
Stream<String> emptyStream = Stream.empty();
```

**中间操作（惰性求值）：**

| 操作 | 说明 | 示例 |
|------|------|------|
| filter | 过滤 | stream.filter(s -> s.length() > 3) |
| map | 映射 | stream.map(String::toUpperCase) |
| flatMap | 扁平化 | stream.flatMap(List::stream) |
| distinct | 去重 | stream.distinct() |
| sorted | 排序 | stream.sorted(Comparator.reverseOrder()) |
| peek | 查看 | stream.peek(System.out::println) |
| limit | 限制 | stream.limit(10) |
| skip | 跳过 | stream.skip(5) |

**终结操作（触发执行）：**

| 操作 | 说明 | 返回类型 |
|------|------|----------|
| forEach | 遍历 | void |
| collect | 收集 | R |
| reduce | 归约 | Optional<T> |
| count | 计数 | long |
| anyMatch | 任意匹配 | boolean |
| allMatch | 全部匹配 | boolean |
| noneMatch | 无匹配 | boolean |
| findFirst | 查找第一个 | Optional<T> |
| findAny | 查找任意 | Optional<T> |
| min/max | 最小/最大 | Optional<T> |
| toArray | 转为数组 | Object[] |
| iterator | 获取迭代器 | Iterator<T> |

### 7.3 方法引用

```java
// 静态方法引用
Function<String, Integer> parseInt = Integer::parseInt;

// 实例方法引用（特定对象）
String prefix = "Hello, ";
Function<String, String> addPrefix = prefix::concat;

// 实例方法引用（任意对象）
Function<String, Integer> getLength = String::length;

// 构造方法引用
Supplier<List<String>> listFactory = ArrayList::new;
Function<Integer, List<String>> sizedListFactory = ArrayList::new;

// 数组构造方法引用
Function<Integer, String[]> arrayFactory = String[]::new;
```

### 7.4 接口默认方法和静态方法

```java
public interface MyInterface {
    // 抽象方法
    void abstractMethod();
    
    // 默认方法（JDK 8新增）
    default void defaultMethod() {
        System.out.println("Default implementation");
    }
    
    // 静态方法（JDK 8新增）
    static void staticMethod() {
        System.out.println("Static method in interface");
    }
}
```

**默认方法冲突解决规则：**
1. 类优先原则：类中的方法优先于接口默认方法
2. 子接口优先：子接口默认方法优先于父接口
3. 必须显式选择：使用 `InterfaceName.super.methodName()`

### 7.5 Optional 类

```java
// 创建Optional
Optional<String> empty = Optional.empty();
Optional<String> of = Optional.of("value");              // 不能为null
Optional<String> ofNullable = Optional.ofNullable(null); // 可以为null

// 判断和获取
if (optional.isPresent()) {
    String value = optional.get();
}

// 推荐用法
optional.ifPresent(System.out::println);

// 提供默认值
String result = optional.orElse("default");
String result2 = optional.orElseGet(() -> getDefaultValue());
String result3 = optional.orElseThrow(() -> new RuntimeException("Not found"));

// 链式操作
optional.map(String::toUpperCase)
        .filter(s -> s.length() > 5)
        .ifPresent(System.out::println);

// 扁平化映射
optional.flatMap(s -> Optional.of(s.toUpperCase()));
```

### 7.6 新日期时间 API

```java
// 创建日期时间
LocalDate date = LocalDate.now();
LocalTime time = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();

// 指定值创建
LocalDate specificDate = LocalDate.of(2024, 3, 15);
LocalTime specificTime = LocalTime.of(14, 30, 0);

// 解析字符串
LocalDate parsedDate = LocalDate.parse("2024-03-15");

// 日期计算
LocalDate tomorrow = date.plusDays(1);
LocalDate lastMonth = date.minusMonths(1);

// 时区处理
ZonedDateTime zoned = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

// 格式化
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String formatted = dateTime.format(formatter);

// 时间戳
Instant instant = Instant.now();
long epochMilli = instant.toEpochMilli();

// 时间段
Duration duration = Duration.between(startTime, endTime);
Period period = Period.between(startDate, endDate);
```

### 7.7 CompletableFuture 异步编程

```java
// 创建CompletableFuture
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // 异步执行
    return "Result";
});

// 链式操作
future.thenApply(result -> result.toUpperCase())      // 转换结果
      .thenAccept(System.out::println)                // 消费结果
      .thenRun(() -> System.out.println("Done"));     // 无参数操作

// 组合多个Future
CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");

// 等待所有完成
CompletableFuture<Void> all = CompletableFuture.allOf(future1, future2);

// 等待任意一个完成
CompletableFuture<Object> any = CompletableFuture.anyOf(future1, future2);

// 异常处理
future.exceptionally(ex -> {
    System.out.println("Error: " + ex.getMessage());
    return "Default";
});

future.handle((result, ex) -> {
    if (ex != null) {
        return "Error: " + ex.getMessage();
    }
    return result;
});
```

---

## 八、JVM 相关类

### 8.1 Runtime 类

```java
public class Runtime {
    // 单例模式
    private static Runtime currentRuntime = new Runtime();
    
    public static Runtime getRuntime() {
        return currentRuntime;
    }
    
    // 内存管理
    public long freeMemory()     // 空闲内存
    public long totalMemory()    // 总内存
    public long maxMemory()      // 最大内存
    
    // 垃圾回收
    public void gc()
    
    // 执行系统命令
    public Process exec(String command)
    
    // 添加关闭钩子
    public void addShutdownHook(Thread hook)
}
```

### 8.2 Process 类

```java
public abstract class Process {
    // 获取输出流（连接到子进程的标准输入）
    public abstract OutputStream getOutputStream();
    
    // 获取输入流（连接到子进程的标准输出）
    public abstract InputStream getInputStream();
    
    // 获取错误流
    public abstract InputStream getErrorStream();
    
    // 等待进程结束
    public abstract int waitFor() throws InterruptedException;
    
    // 获取退出值
    public abstract int exitValue();
    
    // 销毁进程
    public abstract void destroy();
}
```

---

## 九、完整学习路径

### 阶段一：Java 基础（4-6周）

#### 第1-2周：java.lang 包
- Object、String、System 源码
- 包装类（Integer、Long等）
- Class 类与反射基础
- Thread 与线程基础

#### 第3-4周：java.util 基础
- 集合框架体系
- ArrayList、LinkedList 源码
- HashMap、TreeMap 源码
- 迭代器与比较器

#### 第5-6周：IO与异常
- IO流体系
- 文件操作
- 异常处理机制
- 序列化与反序列化

### 阶段二：高级特性（4-6周）

#### 第7-8周：并发编程
- Thread 高级特性
- synchronized 与锁
- volatile 与内存可见性
- java.util.concurrent 包

#### 第9-10周：NIO与网络
- NIO Buffer与Channel
- Selector 多路复用
- Socket 编程
- HTTP 通信

#### 第11-12周：JVM基础
- 类加载机制
- 内存模型
- 垃圾回收
- 性能监控

### 阶段三：JDK 8 新特性（3-4周）

#### 第13-14周：函数式编程
- Lambda 表达式
- 函数式接口
- 方法引用
- Stream API

#### 第15-16周：新API
- Optional
- 新日期时间 API
- 接口默认方法
- CompletableFuture

### 阶段四：源码实战（持续）

#### 实践项目
1. **实现简化版集合框架**
   - 自定义 ArrayList、HashMap
   - 理解扩容、hash冲突解决

2. **实现线程池**
   - 理解任务调度
   - 掌握线程复用

3. **实现 RPC 框架**
   - 网络通信
   - 序列化
   - 动态代理

4. **实现 Web 服务器**
   - NIO 多路复用
   - HTTP 协议解析
   - 线程池处理请求

---

## 十、源码阅读工具与方法

### 10.1 推荐工具

| 工具 | 用途 | 推荐指数 |
|------|------|----------|
| IntelliJ IDEA | 源码阅读、调试 | ⭐⭐⭐⭐⭐ |
| Eclipse | 源码阅读 | ⭐⭐⭐⭐ |
| Source Insight | 代码浏览 | ⭐⭐⭐⭐ |
| Sublime Text + 插件 | 轻量级编辑 | ⭐⭐⭐ |

### 10.2 阅读方法

```
1. 自上而下
   从入口方法开始，逐步深入
   
2. 自下而上
   从基础类开始，理解构建块
   
3. 带着问题
   先有问题，再找答案
   
4. 画图辅助
   类图、时序图、流程图
   
5. 调试验证
   设置断点，单步跟踪
   
6. 写测试
   验证理解，加深记忆
```

### 10.3 调试技巧

1. **条件断点**：只在特定条件下触发
2. **日志断点**：不暂停，只输出日志
3. **异常断点**：捕获特定异常
4. **字段观察点**：监视字段变化
5. **方法断点**：进入/退出方法时触发

---

## 十一、总结

JDK 源码学习的层次：

| 层次 | 目标 | 时间 |
|------|------|------|
| **会用** | 掌握API使用 | 1-2个月 |
| **理解** | 理解实现原理 | 3-6个月 |
| **掌握** | 能独立实现 | 6-12个月 |
| **精通** | 能优化改进 | 1-2年 |

**学习建议：**
1. 不要试图阅读所有源码，选择核心模块深入
2. 结合实际问题学习，带着目的阅读
3. 多动手实践，写测试验证理解
4. 持续学习，JDK在不断演进
5. 参与开源社区，与他人交流

**核心价值：**
- 深入理解Java语言本质
- 提升代码设计和实现能力
- 轻松应对高级面试
- 成为优秀的Java工程师

持续学习，不断精进！
