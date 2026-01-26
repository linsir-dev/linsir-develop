# Tomcat中Executor

## 1. 概述

Executor是Tomcat中的线程池组件，负责管理处理请求的线程。通过Executor，Tomcat可以高效地处理大量并发请求，提高系统的并发处理能力和资源利用率。

## 2. Executor接口

### 2.1 Executor接口定义

Executor接口定义了线程池的基本功能。

```java
public interface Executor {
    String getName();
    void setName(String name);
    
    void execute(Runnable command);
    
    void start() throws LifecycleException;
    void stop() throws LifecycleException;
    
    int getThreadPriority();
    void setThreadPriority(int threadPriority);
    
    boolean isDaemon();
    void setDaemon(boolean daemon);
    
    int getThreadPriority();
    void setThreadPriority(int threadPriority);
    
    String getNamePrefix();
    void setNamePrefix(String namePrefix);
    
    int getMaxThreads();
    void setMaxThreads(int maxThreads);
    
    int getMinSpareThreads();
    void setMinSpareThreads(int minSpareThreads);
    
    int getMaxIdleTime();
    void setMaxIdleTime(int maxIdleTime);
    
    long getThreadRenewalDelay();
    void setThreadRenewalDelay(long threadRenewalDelay);
}
```

## 3. StandardThreadExecutor

### 3.1 StandardThreadExecutor概述

StandardThreadExecutor是Tomcat默认的线程池实现，基于Java的ThreadPoolExecutor。

**主要特性**：
- 动态调整线程数量
- 线程复用
- 任务队列管理
- 拒绝策略

### 3.2 StandardThreadExecutor实现

```java
public class StandardThreadExecutor extends LifecycleMBeanBase implements Executor, ResizableExecutor {
    
    protected static final StringManager sm = StringManager.getManager(StandardThreadExecutor.class);
    
    protected int threadPriority = Thread.NORM_PRIORITY;
    protected boolean daemon = true;
    protected String namePrefix = "tomcat-exec-";
    protected int maxThreads = 200;
    protected int minSpareThreads = 25;
    protected int maxIdleTime = 60000;
    protected long threadRenewalDelay = 1000L;
    
    protected ThreadPoolExecutor executor = null;
    protected TaskQueue taskqueue = null;
    protected AtomicInteger activeThreads = new AtomicInteger(0);
    
    public StandardThreadExecutor() {
        super();
    }
    
    @Override
    public void execute(Runnable command) {
        if (executor != null) {
            executor.execute(command);
        } else {
            throw new IllegalStateException(sm.getString("standardThreadExecutor.notStarted"));
        }
    }
    
    @Override
    protected void initInternal() throws LifecycleException {
        super.initInternal();
        
        taskqueue = new TaskQueue(maxQueueSize);
        TaskThreadFactory tf = new TaskThreadFactory(namePrefix, daemon, getThreadPriority());
        
        executor = new ThreadPoolExecutor(
            getMinSpareThreads(),
            getMaxThreads(),
            maxIdleTime,
            TimeUnit.MILLISECONDS,
            taskqueue,
            tf);
        
        executor.setThreadRenewalDelay(threadRenewalDelay);
        
        if (getPrestartminSpareThreads()) {
            executor.prestartAllCoreThreads();
        }
    }
    
    @Override
    protected void startInternal() throws LifecycleException {
        super.startInternal();
        taskqueue.setParent(executor);
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        taskqueue = null;
    }
    
    @Override
    public int getMaxThreads() {
        return maxThreads;
    }
    
    @Override
    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
        if (executor != null) {
            executor.setMaximumPoolSize(maxThreads);
        }
    }
    
    @Override
    public int getMinSpareThreads() {
        return minSpareThreads;
    }
    
    @Override
    public void setMinSpareThreads(int minSpareThreads) {
        this.minSpareThreads = minSpareThreads;
        if (executor != null) {
            executor.setCorePoolSize(minSpareThreads);
        }
    }
    
    @Override
    public int getMaxIdleTime() {
        return maxIdleTime;
    }
    
    @Override
    public void setMaxIdleTime(int maxIdleTime) {
        this.maxIdleTime = maxIdleTime;
        if (executor != null) {
            executor.setKeepAliveTime(maxIdleTime, TimeUnit.MILLISECONDS);
        }
    }
    
    @Override
    public long getThreadRenewalDelay() {
        return threadRenewalDelay;
    }
    
    @Override
    public void setThreadRenewalDelay(long threadRenewalDelay) {
        this.threadRenewalDelay = threadRenewalDelay;
        if (executor != null) {
            executor.setThreadRenewalDelay(threadRenewalDelay);
        }
    }
    
    @Override
    public int getThreadPriority() {
        return threadPriority;
    }
    
    @Override
    public void setThreadPriority(int threadPriority) {
        this.threadPriority = threadPriority;
    }
    
    @Override
    public boolean isDaemon() {
        return daemon;
    }
    
    @Override
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }
    
    @Override
    public String getNamePrefix() {
        return namePrefix;
    }
    
    @Override
    public void setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
    }
    
    @Override
    public void resizePool(int corePoolSize, int maximumPoolSize) {
        if (executor == null) {
            return;
        }
        
        executor.setCorePoolSize(corePoolSize);
        executor.setMaximumPoolSize(maximumPoolSize);
    }
    
    @Override
    public int getActiveCount() {
        return activeThreads.get();
    }
}
```

## 4. TaskQueue

### 4.1 TaskQueue概述

TaskQueue是Tomcat自定义的任务队列，继承自LinkedBlockingQueue。

**主要特性**：
- 动态调整线程数量
- 任务队列满时创建新线程
- 支持任务优先级

### 4.2 TaskQueue实现

```java
public class TaskQueue extends LinkedBlockingQueue<Runnable> {
    
    private static final long serialVersionUID = 1L;
    
    private transient volatile ThreadPoolExecutor parent = null;
    
    public TaskQueue(int capacity) {
        super(capacity);
    }
    
    public void setParent(ThreadPoolExecutor parent) {
        this.parent = parent;
    }
    
    @Override
    public boolean offer(Runnable o) {
        if (parent == null) {
            return super.offer(o);
        }
        
        if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
            return super.offer(o);
        }
        
        if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
            return false;
        }
        
        return super.offer(o);
    }
    
    @Override
    public boolean offer(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (parent == null) {
            return super.offer(o, timeout, unit);
        }
        
        if (parent.getPoolSize() == parent.getMaximumPoolSize()) {
            return super.offer(o, timeout, unit);
        }
        
        if (parent.getPoolSize() < parent.getMaximumPoolSize()) {
            return false;
        }
        
        return super.offer(o, timeout, unit);
    }
}
```

## 5. TaskThreadFactory

### 5.1 TaskThreadFactory概述

TaskThreadFactory是Tomcat的线程工厂，用于创建工作线程。

**主要功能**：
- 设置线程名称
- 设置线程优先级
- 设置线程为守护线程

### 5.2 TaskThreadFactory实现

```java
public class TaskThreadFactory implements ThreadFactory {
    
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private final boolean daemon;
    private final int priority;
    
    public TaskThreadFactory(String namePrefix, boolean daemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.namePrefix = namePrefix;
        this.daemon = daemon;
        this.priority = priority;
    }
    
    @Override
    public Thread newThread(Runnable r) {
        TaskThread t = new TaskThread(group, r, 
            namePrefix + threadNumber.getAndIncrement());
        t.setDaemon(daemon);
        t.setPriority(priority);
        return t;
    }
}
```

## 6. TaskThread

### 6.1 TaskThread概述

TaskThread是Tomcat的工作线程，继承自Thread。

**主要功能**：
- 执行任务
- 管理线程状态
- 支持线程中断

### 6.2 TaskThread实现

```java
public class TaskThread extends Thread {
    
    private static final StringManager sm = StringManager.getManager(TaskThread.class);
    
    private final AtomicInteger activeThreads;
    private Runnable task;
    private volatile boolean done = false;
    private volatile boolean idle = false;
    private volatile boolean stopping = false;
    
    public TaskThread(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
        this.activeThreads = new AtomicInteger(0);
    }
    
    @Override
    public void run() {
        while (!done && !stopping) {
            try {
                idle = true;
                task = getTask();
                idle = false;
                
                if (task == null) {
                    continue;
                }
                
                activeThreads.incrementAndGet();
                
                try {
                    task.run();
                } catch (Exception e) {
                    log.error(sm.getString("taskThread.exception", getName()), e);
                } finally {
                    activeThreads.decrementAndGet();
                    task = null;
                }
            } catch (InterruptedException e) {
                if (stopping) {
                    break;
                }
            }
        }
    }
    
    protected Runnable getTask() throws InterruptedException {
        if (parent != null) {
            return parent.getQueue().poll(keepAliveTime, TimeUnit.MILLISECONDS);
        }
        return null;
    }
    
    public void stopNow() {
        stopping = true;
        interrupt();
    }
    
    public boolean isIdle() {
        return idle;
    }
    
    public boolean isStopping() {
        return stopping;
    }
    
    public int getActiveCount() {
        return activeThreads.get();
    }
}
```

## 7. Executor配置

### 7.1 server.xml配置

在server.xml中配置Executor。

```xml
<Server>
    <Service name="Catalina">
        <!-- 配置Executor -->
        <Executor name="tomcatThreadPool"
                 namePrefix="tomcat-http-"
                 maxThreads="200"
                 minSpareThreads="25"
                 maxIdleTime="60000"
                 prestartminSpareThreads="true"/>
        
        <!-- 配置Connector使用Executor -->
        <Connector executor="tomcatThreadPool"
                  port="8080"
                  protocol="HTTP/1.1"
                  connectionTimeout="20000"
                  redirectPort="8443"/>
        
        <Engine name="Catalina" defaultHost="localhost">
            <Host name="localhost" appBase="webapps" unpackWARs="true">
                <!-- Context配置 -->
            </Host>
        </Engine>
    </Service>
</Server>
```

### 7.2 配置参数说明

| 参数 | 说明 | 默认值 | 推荐值 |
|-----|------|-------|-------|
| name | Executor名称 | - | tomcatThreadPool |
| namePrefix | 线程名称前缀 | tomcat-exec- | tomcat-http- |
| maxThreads | 最大线程数 | 200 | 200-500 |
| minSpareThreads | 最小空闲线程数 | 25 | 25-50 |
| maxIdleTime | 最大空闲时间(ms) | 60000 | 60000 |
| prestartminSpareThreads | 是否预启动核心线程 | false | true |
| daemon | 是否为守护线程 | true | true |
| threadPriority | 线程优先级 | 5 (NORM_PRIORITY) | 5 |

## 8. Executor使用场景

### 8.1 Connector使用Executor

Connector可以使用Executor来处理请求。

```xml
<Executor name="tomcatThreadPool"
         namePrefix="tomcat-http-"
         maxThreads="200"
         minSpareThreads="25"
         maxIdleTime="60000"
         prestartminSpareThreads="true"/>

<Connector executor="tomcatThreadPool"
          port="8080"
          protocol="HTTP/1.1"
          connectionTimeout="20000"
          redirectPort="8443"/>
```

### 8.2 自定义组件使用Executor

自定义组件也可以使用Executor来执行异步任务。

```java
public class MyComponent implements Lifecycle {
    
    private Executor executor;
    
    public void setExecutor(Executor executor) {
        this.executor = executor;
    }
    
    public void executeAsync(Runnable task) {
        if (executor != null) {
            executor.execute(task);
        } else {
            new Thread(task).start();
        }
    }
    
    @Override
    public void init() throws LifecycleException {
        if (executor instanceof Lifecycle) {
            ((Lifecycle) executor).init();
        }
    }
    
    @Override
    public void start() throws LifecycleException {
        if (executor instanceof Lifecycle) {
            ((Lifecycle) executor).start();
        }
    }
    
    @Override
    public void stop() throws LifecycleException {
        if (executor instanceof Lifecycle) {
            ((Lifecycle) executor).stop();
        }
    }
    
    @Override
    public void destroy() throws LifecycleException {
        if (executor instanceof Lifecycle) {
            ((Lifecycle) executor).destroy();
        }
    }
}
```

## 9. 性能优化

### 9.1 线程池大小优化

根据应用特点调整线程池大小。

```java
public class ThreadPoolOptimizer {
    
    public static int calculateOptimalThreadPoolSize() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        long targetUtilization = 0.8;
        long waitTime = 100;
        long computeTime = 50;
        
        int poolSize = (int) (cpuCores * targetUtilization * (1 + waitTime / computeTime));
        
        return Math.max(poolSize, 1);
    }
    
    public static void printThreadPoolStats(Executor executor) {
        if (executor instanceof StandardThreadExecutor) {
            StandardThreadExecutor stdExecutor = (StandardThreadExecutor) executor;
            
            System.out.println("Max Threads: " + stdExecutor.getMaxThreads());
            System.out.println("Min Spare Threads: " + stdExecutor.getMinSpareThreads());
            System.out.println("Max Idle Time: " + stdExecutor.getMaxIdleTime());
            System.out.println("Active Threads: " + stdExecutor.getActiveCount());
        }
    }
}
```

### 9.2 监控线程池状态

```java
public class ThreadPoolMonitor {
    
    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolMonitor.class);
    
    public static void monitorThreadPool(Executor executor) {
        if (executor instanceof StandardThreadExecutor) {
            StandardThreadExecutor stdExecutor = (StandardThreadExecutor) executor;
            
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                int activeThreads = stdExecutor.getActiveCount();
                int maxThreads = stdExecutor.getMaxThreads();
                double utilization = (double) activeThreads / maxThreads * 100;
                
                logger.info("ThreadPool Stats - Active: {}, Max: {}, Utilization: {}%", 
                    activeThreads, maxThreads, String.format("%.2f", utilization));
                
                if (utilization > 80) {
                    logger.warn("ThreadPool utilization is high: {}%", 
                        String.format("%.2f", utilization));
                }
            }, 0, 5, TimeUnit.SECONDS);
        }
    }
}
```

## 10. 最佳实践

### 10.1 Executor配置建议

1. **合理设置线程数**：根据CPU核心数和应用特点设置合适的线程数
2. **设置最小空闲线程**：保持一定数量的空闲线程，避免频繁创建和销毁
3. **设置最大空闲时间**：及时回收空闲线程，释放资源
4. **预启动核心线程**：在启动时预启动核心线程，提高响应速度
5. **设置线程优先级**：根据任务重要性设置合适的线程优先级

### 10.2 性能调优

```xml
<!-- 高并发场景 -->
<Executor name="tomcatThreadPool"
         namePrefix="tomcat-http-"
         maxThreads="500"
         minSpareThreads="50"
         maxIdleTime="30000"
         prestartminSpareThreads="true"/>

<!-- 低延迟场景 -->
<Executor name="tomcatThreadPool"
         namePrefix="tomcat-http-"
         maxThreads="200"
         minSpareThreads="100"
         maxIdleTime="10000"
         prestartminSpareThreads="true"/>

<!-- 资源受限场景 -->
<Executor name="tomcatThreadPool"
         namePrefix="tomcat-http-"
         maxThreads="100"
         minSpareThreads="10"
         maxIdleTime="60000"
         prestartminSpareThreads="false"/>
```

### 10.3 错误处理

```java
public class SafeExecutor implements Executor {
    
    private static final Logger logger = LoggerFactory.getLogger(SafeExecutor.class);
    
    private Executor delegate;
    
    public SafeExecutor(Executor delegate) {
        this.delegate = delegate;
    }
    
    @Override
    public void execute(Runnable command) {
        try {
            delegate.execute(() -> {
                try {
                    command.run();
                } catch (Throwable t) {
                    logger.error("Task execution failed", t);
                }
            });
        } catch (RejectedExecutionException e) {
            logger.error("Task rejected", e);
            
            try {
                command.run();
            } catch (Throwable t) {
                logger.error("Task execution failed", t);
            }
        }
    }
    
    @Override
    public void start() throws LifecycleException {
        if (delegate instanceof Lifecycle) {
            ((Lifecycle) delegate).start();
        }
    }
    
    @Override
    public void stop() throws LifecycleException {
        if (delegate instanceof Lifecycle) {
            ((Lifecycle) delegate).stop();
        }
    }
}
```

## 11. 总结

Tomcat的Executor机制是其核心功能之一，具有以下特点：

1. **线程池管理**：基于ThreadPoolExecutor实现，高效管理线程
2. **动态调整**：根据负载动态调整线程数量
3. **任务队列**：使用TaskQueue管理待执行任务
4. **线程复用**：复用工作线程，减少创建和销毁开销
5. **灵活配置**：支持多种配置参数，满足不同场景需求

理解Tomcat的Executor机制，对于：
- 优化Tomcat性能
- 提高并发处理能力
- 合理利用系统资源
- 排查性能问题

都具有重要意义。Tomcat的Executor机制是其优秀架构设计的重要体现，为高并发Web应用提供了强大的支持。