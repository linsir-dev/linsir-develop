# java.base 模块 - JDK 基础核心

## 概述

`java.base` 是 JDK 8 中最重要的模块，所有其他模块都依赖它。它包含了 Java 语言的核心类库，是 Java 应用程序运行的基础。

## 模块信息

| 属性 | 值 |
|------|-----|
| **模块名** | java.base |
| **说明** | 基础模块，所有模块依赖 |
| **核心包** | java.lang, java.util, java.io, java.nio, java.net |
| **重要性** | ⭐⭐⭐⭐⭐ |

---

## 核心包结构

```
java.base/
├── java/lang/               # 核心类（Object、String、Thread等）
├── java/util/               # 工具类（集合、Stream、日期等）
│   ├── function/           # 函数式接口（JDK 8新增）
│   ├── stream/             # Stream API（JDK 8新增）
│   └── concurrent/         # 并发包
├── java/io/                 # IO操作
├── java/nio/                # NIO（New IO）
│   ├── file/               # NIO.2 文件操作
│   └── charset/            # 字符集
├── java/net/                # 网络编程
├── java/time/               # JDK 8 新日期时间API
├── java/math/               # 数学运算
├── java/security/           # 安全基础
├── java/text/               # 文本处理
└── java/lang/reflect/       # 反射
```

---

## 一、java.lang 包 - 语言核心

### 1.1 核心类层次

```
java.lang.Object
├── java.lang.Class<T>
├── java.lang.String
├── java.lang.System
├── java.lang.Thread
├── java.lang.Runnable (接口)
├── java.lang.Exception
│   └── RuntimeException
├── java.lang.Error
└── 包装类
    ├── Boolean
    ├── Character
    ├── Byte
    ├── Short
    ├── Integer
    ├── Long
    ├── Float
    └── Double
```

### 1.2 Object 类详解

```java
public class Object {
    // 本地方法
    public native int hashCode();
    public final native Class<?> getClass();
    protected native Object clone() throws CloneNotSupportedException;
    public final native void notify();
    public final native void notifyAll();
    public final native void wait(long timeout) throws InterruptedException;
    
    // 普通方法
    public boolean equals(Object obj) { return (this == obj); }
    public String toString() { return getClass().getName() + "@" + Integer.toHexString(hashCode()); }
    protected void finalize() throws Throwable { }
}
```

**设计要点：**
- 所有类的根类
- 定义对象基本行为（equals、hashCode、toString）
- 提供线程同步机制（wait/notify）
- 支持克隆和垃圾回收

### 1.3 String 类详解

```java
public final class String implements java.io.Serializable, Comparable<String>, CharSequence {
    // JDK 8: char[] value
    // JDK 9+: byte[] value + byte coder
    private final char value[];
    private int hash; // 缓存哈希值
    
    // 核心方法
    public int length() { return value.length; }
    public char charAt(int index) { return value[index]; }
    public String substring(int beginIndex, int endIndex) { ... }
    public boolean equals(Object anObject) { ... }
    public int hashCode() { ... }
}
```

**不可变性优势：**
1. **线程安全**：多线程无需同步
2. **哈希缓存**：hashCode 可缓存，提高性能
3. **字符串常量池**：节省内存
4. **安全性**：参数传递不会被修改

### 1.4 System 类详解

```java
public final class System {
    // 标准流
    public static final InputStream in = null;
    public static final PrintStream out = null;
    public static final PrintStream err = null;
    
    // 数组拷贝（native方法，高效）
    public static native void arraycopy(Object src, int srcPos, 
                                        Object dest, int destPos, int length);
    
    // 系统属性
    public static String getProperty(String key);
    public static String getenv(String name);
    
    // 系统控制
    public static void exit(int status);
    public static void gc();
    public static long currentTimeMillis();
    public static long nanoTime();
}
```

### 1.5 Thread 类详解

```java
public class Thread implements Runnable {
    // 线程状态
    public enum State {
        NEW,           // 新建
        RUNNABLE,      // 可运行
        BLOCKED,       // 阻塞
        WAITING,       // 等待
        TIMED_WAITING, // 限时等待
        TERMINATED     // 终止
    }
    
    // 核心方法
    public synchronized void start();
    public void run();
    public final void join() throws InterruptedException;
    public static void sleep(long millis) throws InterruptedException;
    public static void yield();
    public void interrupt();
    public boolean isAlive();
}
```

---

## 二、java.util 包 - 工具类库

### 2.1 集合框架体系

```
Collection (接口)
├── List (接口)
│   ├── ArrayList        # 动态数组，查询快
│   ├── LinkedList       # 双向链表，增删快
│   └── Vector           # 线程安全（已过时）
│       └── Stack        # 栈（已过时，用Deque替代）
│
├── Set (接口)
│   ├── HashSet          # 哈希表实现，无序
│   ├── LinkedHashSet    # 保持插入顺序
│   └── TreeSet          # 红黑树实现，有序
│       └── NavigableSet
│
└── Queue (接口)
    ├── LinkedList       # 双端队列
    ├── PriorityQueue    # 优先队列（堆实现）
    └── ArrayDeque       # 数组双端队列

Map (接口)
├── HashMap              # 哈希表实现
├── LinkedHashMap        # 保持插入顺序
├── TreeMap              # 红黑树实现，有序
├── Hashtable            # 线程安全（已过时）
└── WeakHashMap          # 弱引用键
```

### 2.2 ArrayList 源码分析

```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    
    private static final int DEFAULT_CAPACITY = 10;
    private static final Object[] EMPTY_ELEMENTDATA = {};
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};
    
    transient Object[] elementData; // 存储元素的数组
    private int size;               // 实际元素数量
    
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

**关键特性：**
- **动态扩容**：容量不足时扩容1.5倍
- **RandomAccess**：支持快速随机访问
- **非线程安全**：多线程需外部同步

### 2.3 HashMap 源码分析（JDK 8）

```java
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // 16
    static final int MAXIMUM_CAPACITY = 1 << 30;
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;      // 链表转红黑树阈值
    static final int UNTREEIFY_THRESHOLD = 6;    // 红黑树转链表阈值
    static final int MIN_TREEIFY_CAPACITY = 64;  // 最小树化容量
    
    transient Node<K,V>[] table;  // 节点数组
    transient int size;           // 节点数量
    int threshold;                // 扩容阈值
    final float loadFactor;       // 负载因子
    
    // 基本节点
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
    }
    
    // 树节点（红黑树）
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent, left, right, prev;
        boolean red;
    }
}
```

**JDK 8 优化：**
1. **链表转红黑树**：链表长度超过8时转为红黑树，查找复杂度从O(n)降到O(log n)
2. **扩容优化**：重新计算hash时，只需判断hash新增的高位是0还是1
3. **尾插法**：避免多线程环境下的死循环问题

### 2.4 ConcurrentHashMap 源码分析

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V>
    implements ConcurrentMap<K,V>, Serializable {
    
    // 核心参数
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    static final int TREEIFY_THRESHOLD = 8;
    static final int MIN_TRANSFER_STRIDE = 16;
    
    // 特殊节点标记
    static final int MOVED = -1;     // 正在转移
    static final int TREEBIN = -2;   // 红黑树
    static final int RESERVED = -3;  // 保留
    
    // volatile保证可见性
    transient volatile Node<K,V>[] table;
    transient volatile int transferIndex;
    transient volatile long baseCount;
    transient volatile int sizeCtl;
}
```

**线程安全机制：**
1. **CAS操作**：使用 `Unsafe` 类的CAS方法进行无锁更新
2. **synchronized + CAS**：JDK 8 替代 JDK 7 的分段锁
3. **volatile**：保证数组引用的可见性
4. **红黑树**：解决hash冲突，提高并发性能

### 2.5 Stream API（JDK 8）

```java
// Stream 核心接口
public interface Stream<T> extends BaseStream<T, Stream<T>> {
    // 中间操作
    Stream<T> filter(Predicate<? super T> predicate);
    <R> Stream<R> map(Function<? super T, ? extends R> mapper);
    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);
    Stream<T> sorted();
    Stream<T> distinct();
    Stream<T> limit(long maxSize);
    Stream<T> skip(long n);
    Stream<T> peek(Consumer<? super T> action);
    
    // 终止操作
    void forEach(Consumer<? super T> action);
    <R, A> R collect(Collector<? super T, A, R> collector);
    long count();
    boolean anyMatch(Predicate<? super T> predicate);
    Optional<T> findFirst();
    Optional<T> reduce(BinaryOperator<T> accumulator);
}
```

**使用示例：**
```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// 过滤偶数，平方，排序，收集
List<Integer> result = numbers.stream()
    .filter(n -> n % 2 == 0)      // 过滤偶数
    .map(n -> n * n)               // 平方
    .sorted()                      // 排序
    .collect(Collectors.toList()); // 收集
```

---

## 三、java.io 包 - IO操作

### 3.1 IO 流体系

```
java.io
├── 字节流
│   ├── InputStream (抽象类)
│   │   ├── FileInputStream
│   │   ├── ByteArrayInputStream
│   │   ├── BufferedInputStream     # 缓冲包装
│   │   ├── DataInputStream         # 基本数据类型
│   │   └── ObjectInputStream       # 对象序列化
│   └── OutputStream (抽象类)
│       ├── FileOutputStream
│       ├── ByteArrayOutputStream
│       ├── BufferedOutputStream
│       ├── DataOutputStream
│       └── ObjectOutputStream
│
├── 字符流
│   ├── Reader (抽象类)
│   │   ├── FileReader
│   │   ├── BufferedReader          # 缓冲包装
│   │   ├── InputStreamReader       # 字节转字符
│   │   └── StringReader
│   └── Writer (抽象类)
│       ├── FileWriter
│       ├── BufferedWriter
│       ├── OutputStreamWriter
│       └── StringWriter
│
└── 文件操作
    ├── File
    ├── RandomAccessFile            # 随机访问
    └── FileDescriptor
```

### 3.2 装饰器模式

IO 流使用装饰器模式，可以灵活组合功能：

```java
// 基础流
FileInputStream fis = new FileInputStream("file.txt");

// 添加缓冲
BufferedInputStream bis = new BufferedInputStream(fis);

// 添加数据类型支持
DataInputStream dis = new DataInputStream(bis);

// 简写形式
DataInputStream dis = new DataInputStream(
    new BufferedInputStream(
        new FileInputStream("file.txt")
    )
);
```

---

## 四、java.nio 包 - New IO

### 4.1 NIO 核心组件

```
java.nio
├── Buffer（缓冲区）
│   ├── ByteBuffer          # 最常用
│   ├── CharBuffer
│   ├── ShortBuffer
│   ├── IntBuffer
│   ├── LongBuffer
│   ├── FloatBuffer
│   └── DoubleBuffer
│
├── Channel（通道）
│   ├── FileChannel         # 文件通道
│   ├── SocketChannel       # TCP客户端
│   ├── ServerSocketChannel # TCP服务端
│   └── DatagramChannel     # UDP
│
└── Selector（选择器）
    └── Selector            # 多路复用

java.nio.charset
├── Charset                 # 字符集
├── CharsetEncoder          # 编码器
└── CharsetDecoder          # 解码器

java.nio.file
├── Path                    # 路径
├── Paths                   # 路径工具
├── Files                   # 文件操作
├── FileSystem              # 文件系统
└── WatchService            # 文件监控
```

### 4.2 Buffer 详解

```java
public abstract class Buffer {
    // 核心属性
    private int mark = -1;      // 标记位置
    private int position = 0;   // 当前位置
    private int limit;          // 限制
    private int capacity;       // 容量
    
    // 核心方法
    public final int position() { return position; }
    public final Buffer position(int newPosition);
    public final int limit() { return limit; }
    public final Buffer limit(int newLimit);
    public final int capacity() { return capacity; }
    public final int remaining() { return limit - position; }
    public final boolean hasRemaining() { return position < limit; }
    
    // 翻转（读模式转写模式）
    public final Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
    
    // 清空
    public final Buffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }
}
```

**Buffer 状态转换：**
```
创建/清空(clear) → 写入数据 → 翻转(flip) → 读取数据 → 倒回(rewind)/清空(clear)
```

---

## 五、java.net 包 - 网络编程

### 5.1 核心类

```
java.net
├── Socket                  # TCP客户端
├── ServerSocket            # TCP服务端
├── DatagramSocket          # UDP
├── DatagramPacket          # UDP数据包
├── URL                     # URL解析
├── URLConnection           # URL连接
├── URI                     # URI解析
├── InetAddress             # IP地址
├── InetSocketAddress       # IP+端口
└── NetworkInterface        # 网络接口
```

### 5.2 Socket 编程示例

```java
// TCP 服务端
ServerSocket serverSocket = new ServerSocket(8080);
Socket clientSocket = serverSocket.accept();

// TCP 客户端
Socket socket = new Socket("localhost", 8080);

// 获取输入输出流
InputStream in = socket.getInputStream();
OutputStream out = socket.getOutputStream();
```

---

## 六、java.time 包 - 日期时间（JDK 8）

### 6.1 核心类

```
java.time
├── LocalDate               # 日期（年月日）
├── LocalTime               # 时间（时分秒）
├── LocalDateTime           # 日期时间
├── ZonedDateTime           # 带时区的日期时间
├── Instant                 # 时间戳
├── Duration                # 时间段（秒、纳秒）
├── Period                  # 日期间隔（年月日）
└── ZoneId                  # 时区ID

java.time.format
└── DateTimeFormatter       # 日期时间格式化

java.time.temporal
├── Temporal                # 时间对象接口
├── TemporalAccessor        # 时间访问接口
├── TemporalAdjuster        # 时间调整器
└── ChronoUnit              # 时间单位
```

### 6.2 使用示例

```java
// 当前日期时间
LocalDate today = LocalDate.now();
LocalTime now = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();

// 创建特定日期时间
LocalDate birthday = LocalDate.of(1990, 5, 15);

// 日期计算
LocalDate nextWeek = today.plusWeeks(1);
LocalDate lastMonth = today.minusMonths(1);

// 格式化
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
String formatted = dateTime.format(formatter);

// 解析
LocalDate parsed = LocalDate.parse("2024-03-26", DateTimeFormatter.ISO_LOCAL_DATE);
```

---

## 七、java.util.concurrent 包 - 并发编程

### 7.1 并发集合

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

### 7.2 线程池框架

```java
// 线程池接口
public interface Executor {
    void execute(Runnable command);
}

public interface ExecutorService extends Executor {
    <T> Future<T> submit(Callable<T> task);
    List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks);
    void shutdown();
    boolean awaitTermination(long timeout, TimeUnit unit);
}

// 线程池实现
public class ThreadPoolExecutor extends AbstractExecutorService {
    private volatile int corePoolSize;      // 核心线程数
    private volatile int maximumPoolSize;   // 最大线程数
    private volatile long keepAliveTime;    // 空闲存活时间
    private final BlockingQueue<Runnable> workQueue;  // 任务队列
    private volatile ThreadFactory threadFactory;     // 线程工厂
    private volatile RejectedExecutionHandler handler; // 拒绝策略
}

// 创建线程池
ExecutorService executor = new ThreadPoolExecutor(
    5,                      // 核心线程数
    10,                     // 最大线程数
    60L,                    // 空闲线程存活时间
    TimeUnit.SECONDS,       // 时间单位
    new LinkedBlockingQueue<>(100),  // 任务队列
    new ThreadFactory() {   // 线程工厂
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "pool-thread-" + count.incrementAndGet());
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
);
```

### 7.3 锁机制

```java
// 可重入锁
public class ReentrantLock implements Lock {
    public void lock();
    public void unlock();
    public boolean tryLock();
    public boolean tryLock(long timeout, TimeUnit unit);
    public Condition newCondition();
}

// 读写锁
public class ReentrantReadWriteLock implements ReadWriteLock {
    public Lock readLock();   // 读锁（共享）
    public Lock writeLock();  // 写锁（独占）
}

// 使用示例
ReentrantLock lock = new ReentrantLock();
lock.lock();
try {
    // 临界区
} finally {
    lock.unlock();
}
```

### 7.4 AQS（AbstractQueuedSynchronizer）

```java
public abstract class AbstractQueuedSynchronizer
    extends AbstractOwnableSynchronizer {
    
    private volatile int state;           // 同步状态
    private transient volatile Node head; // 队列头
    private transient volatile Node tail; // 队列尾
    
    // 模板方法
    protected boolean tryAcquire(int arg);    // 尝试获取锁
    protected boolean tryRelease(int arg);    // 尝试释放锁
    protected int tryAcquireShared(int arg);  // 尝试获取共享锁
    protected boolean tryReleaseShared(int arg); // 尝试释放共享锁
}
```

---

## 八、学习路径建议

### 8.1 入门阶段（1-2周）

1. **java.lang 基础**
   - Object、String、System 使用
   - 包装类与自动装箱拆箱
   - 异常处理机制

2. **java.util 基础**
   - ArrayList、HashMap 使用
   - 迭代器与增强for循环
   - 工具类（Arrays、Collections）

### 8.2 进阶阶段（3-4周）

1. **IO/NIO**
   - 字节流与字符流
   - 缓冲流与序列化
   - NIO Buffer 与 Channel

2. **并发编程**
   - Thread 与 Runnable
   - synchronized 与锁
   - 线程池与并发集合

3. **JDK 8 新特性**
   - Lambda 表达式
   - Stream API
   - 新日期时间 API

### 8.3 深入阶段（持续）

1. **源码阅读**
   - HashMap 源码分析
   - ConcurrentHashMap 源码分析
   - 线程池源码分析

2. **实践项目**
   - 实现简化版集合框架
   - 实现线程池
   - 实现 RPC 框架

---

## 九、总结

java.base 模块是 JDK 的核心，掌握它对于 Java 开发至关重要：

| 包 | 核心内容 | 重要性 |
|----|---------|--------|
| java.lang | Object、String、Thread、反射 | ⭐⭐⭐⭐⭐ |
| java.util | 集合框架、Stream、并发 | ⭐⭐⭐⭐⭐ |
| java.io | IO流、序列化 | ⭐⭐⭐⭐ |
| java.nio | NIO、文件操作 | ⭐⭐⭐⭐ |
| java.net | Socket、URL | ⭐⭐⭐ |
| java.time | 日期时间（JDK 8） | ⭐⭐⭐⭐ |
| java.util.concurrent | 并发编程 | ⭐⭐⭐⭐⭐ |

**学习建议：**
1. 先掌握基础类的使用
2. 深入理解集合框架原理
3. 掌握并发编程基础
4. 阅读源码加深理解
5. 多做实践项目
