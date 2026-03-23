# 任务执行模块扩展设计

## 1. 扩展目标

任务执行模块的扩展设计旨在提供灵活的扩展点，使开发者能够：

1. 自定义任务执行策略
2. 集成第三方线程池实现
3. 实现自定义调度策略
4. 添加任务监控和统计功能
5. 支持分布式任务调度

## 2. 扩展点设计

### 2.1 任务执行器扩展

#### 2.1.1 自定义 TaskExecutor

```java
/**
 * 自定义任务执行器示例：带日志记录的执行器
 */
public class LoggingTaskExecutor implements TaskExecutor {
    
    private final TaskExecutor delegate;
    private final Logger logger;
    
    public LoggingTaskExecutor(TaskExecutor delegate, Logger logger) {
        this.delegate = delegate;
        this.logger = logger;
    }
    
    @Override
    public void execute(Runnable task) {
        logger.info("开始执行任务");
        long startTime = System.currentTimeMillis();
        
        try {
            delegate.execute(() -> {
                try {
                    task.run();
                    logger.info("任务执行成功，耗时: {}ms", 
                        System.currentTimeMillis() - startTime);
                } catch (Exception e) {
                    logger.error("任务执行失败", e);
                    throw e;
                }
            });
        } catch (Exception e) {
            logger.error("提交任务失败", e);
            throw e;
        }
    }
}
```

#### 2.1.2 装饰器模式扩展

```java
/**
 * 任务执行器装饰器基类
 */
public abstract class TaskExecutorDecorator implements TaskExecutor {
    
    protected final TaskExecutor taskExecutor;
    
    public TaskExecutorDecorator(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    @Override
    public void execute(Runnable task) {
        taskExecutor.execute(decorate(task));
    }
    
    protected abstract Runnable decorate(Runnable task);
}

/**
 * 上下文传递装饰器
 */
public class ContextPropagatingTaskExecutor extends TaskExecutorDecorator {
    
    private final ThreadLocal<Map<String, Object>> contextHolder;
    
    public ContextPropagatingTaskExecutor(TaskExecutor taskExecutor,
            ThreadLocal<Map<String, Object>> contextHolder) {
        super(taskExecutor);
        this.contextHolder = contextHolder;
    }
    
    @Override
    protected Runnable decorate(Runnable task) {
        Map<String, Object> context = contextHolder.get();
        return () -> {
            contextHolder.set(context);
            try {
                task.run();
            } finally {
                contextHolder.remove();
            }
        };
    }
}
```

### 2.2 任务调度器扩展

#### 2.2.1 自定义 Trigger

```java
/**
 * 自定义触发器接口
 */
public interface Trigger {
    
    /**
     * 计算下一次执行时间
     * @param context 触发器上下文
     * @return 下一次执行时间，null 表示不再执行
     */
    Date nextExecutionTime(TriggerContext context);
}

/**
 * 触发器上下文
 */
public interface TriggerContext {
    Date lastScheduledExecutionTime();
    Date lastActualExecutionTime();
    Date lastCompletionTime();
}

/**
 * 自定义触发器：固定延迟触发器
 */
public class FixedDelayTrigger implements Trigger {
    
    private final long delay;
    
    public FixedDelayTrigger(long delay) {
        this.delay = delay;
    }
    
    @Override
    public Date nextExecutionTime(TriggerContext context) {
        Date lastCompletion = context.lastCompletionTime();
        return (lastCompletion != null) 
            ? new Date(lastCompletion.getTime() + delay)
            : new Date();
    }
}
```

#### 2.2.2 自定义 TaskScheduler

```java
/**
 * 支持自定义触发器的调度器
 */
public class TriggerTaskScheduler implements TaskScheduler {
    
    private final ScheduledExecutorService executor;
    private final Map<String, ScheduledFuture<?>> scheduledTasks = 
        new ConcurrentHashMap<>();
    
    public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
        ReschedulingRunnable runnable = new ReschedulingRunnable(
            task, trigger, this);
        return scheduleNextExecution(runnable);
    }
    
    private ScheduledFuture<?> scheduleNextExecution(ReschedulingRunnable task) {
        Date nextTime = task.getTrigger().nextExecutionTime(task.getContext());
        if (nextTime == null) {
            return null;
        }
        
        long delay = nextTime.getTime() - System.currentTimeMillis();
        return executor.schedule(task, delay, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 可重新调度的任务包装器
     */
    private class ReschedulingRunnable implements Runnable {
        
        private final Runnable delegate;
        private final Trigger trigger;
        private final TriggerTaskContext context;
        
        @Override
        public void run() {
            Date startTime = new Date();
            context.lastScheduledExecutionTime = startTime;
            
            try {
                delegate.run();
                context.lastCompletionTime = new Date();
            } catch (Exception e) {
                // 处理异常
            }
            
            // 重新调度
            scheduleNextExecution(this);
        }
    }
}
```

### 2.3 监控和统计扩展

#### 2.3.1 任务执行监控

```java
/**
 * 任务执行监听器
 */
public interface TaskExecutionListener {
    
    void onTaskStart(TaskExecutionInfo info);
    
    void onTaskComplete(TaskExecutionInfo info, Object result);
    
    void onTaskFailure(TaskExecutionInfo info, Throwable exception);
}

/**
 * 任务执行信息
 */
public class TaskExecutionInfo {
    private final String taskId;
    private final String taskName;
    private final Date submitTime;
    private Date startTime;
    private Date endTime;
    private Thread executingThread;
    
    // getters and setters
}

/**
 * 带监控的任务执行器
 */
public class MonitoringTaskExecutor implements TaskExecutor {
    
    private final TaskExecutor delegate;
    private final List<TaskExecutionListener> listeners = new CopyOnWriteArrayList<>();
    
    @Override
    public void execute(Runnable task) {
        String taskId = generateTaskId();
        TaskExecutionInfo info = new TaskExecutionInfo(taskId, 
            task.getClass().getName(), new Date());
        
        listeners.forEach(l -> l.onTaskStart(info));
        
        delegate.execute(() -> {
            info.setStartTime(new Date());
            info.setExecutingThread(Thread.currentThread());
            
            try {
                task.run();
                info.setEndTime(new Date());
                listeners.forEach(l -> l.onTaskComplete(info, null));
            } catch (Exception e) {
                info.setEndTime(new Date());
                listeners.forEach(l -> l.onTaskFailure(info, e));
                throw e;
            }
        });
    }
    
    public void addListener(TaskExecutionListener listener) {
        listeners.add(listener);
    }
}
```

#### 2.3.2 统计信息收集

```java
/**
 * 任务执行统计
 */
public class TaskExecutionStatistics {
    
    private final AtomicLong totalTasks = new AtomicLong(0);
    private final AtomicLong completedTasks = new AtomicLong(0);
    private final AtomicLong failedTasks = new AtomicLong(0);
    private final AtomicLong totalExecutionTime = new AtomicLong(0);
    
    public void recordTaskStart() {
        totalTasks.incrementAndGet();
    }
    
    public void recordTaskComplete(long executionTime) {
        completedTasks.incrementAndGet();
        totalExecutionTime.addAndGet(executionTime);
    }
    
    public void recordTaskFailure() {
        failedTasks.incrementAndGet();
    }
    
    public StatisticsSnapshot getSnapshot() {
        return new StatisticsSnapshot(
            totalTasks.get(),
            completedTasks.get(),
            failedTasks.get(),
            totalExecutionTime.get()
        );
    }
}
```

### 2.4 分布式任务调度扩展

#### 2.4.1 分布式锁接口

```java
/**
 * 分布式锁
 */
public interface DistributedLock {
    
    boolean tryLock(String lockKey, long waitTime, long leaseTime, 
            TimeUnit unit) throws InterruptedException;
    
    void unlock(String lockKey);
    
    boolean isLocked(String lockKey);
}

/**
 * 基于 Redis 的分布式锁实现
 */
public class RedisDistributedLock implements DistributedLock {
    
    private final StringRedisTemplate redisTemplate;
    
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, 
            TimeUnit unit) throws InterruptedException {
        String value = UUID.randomUUID().toString();
        long endTime = System.currentTimeMillis() + unit.toMillis(waitTime);
        
        while (System.currentTimeMillis() < endTime) {
            Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, value, leaseTime, unit);
            if (Boolean.TRUE.equals(success)) {
                return true;
            }
            Thread.sleep(100);
        }
        return false;
    }
    
    @Override
    public void unlock(String lockKey) {
        redisTemplate.delete(lockKey);
    }
}
```

#### 2.4.2 分布式任务调度器

```java
/**
 * 分布式任务调度器
 */
public class DistributedTaskScheduler implements TaskScheduler {
    
    private final TaskScheduler delegate;
    private final DistributedLock lock;
    private final String nodeId;
    
    public DistributedTaskScheduler(TaskScheduler delegate, 
            DistributedLock lock, String nodeId) {
        this.delegate = delegate;
        this.lock = lock;
        this.nodeId = nodeId;
    }
    
    @Override
    public ScheduledFuture<?> schedule(Runnable task, Date startTime) {
        String lockKey = generateLockKey(task);
        
        return delegate.schedule(() -> {
            try {
                if (lock.tryLock(lockKey, 5, 30, TimeUnit.SECONDS)) {
                    try {
                        task.run();
                    } finally {
                        lock.unlock(lockKey);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, startTime);
    }
    
    private String generateLockKey(Runnable task) {
        return "task:lock:" + task.getClass().getName() + ":" + nodeId;
    }
}
```

## 3. 与 Spring 框架集成

### 3.1 @Async 注解支持

```java
/**
 * @Async 注解处理器
 */
@Aspect
@Component
public class AsyncAnnotationAspect {
    
    private final AsyncTaskExecutor executor;
    
    @Around("@annotation(async)")
    public Object aroundAsync(ProceedingJoinPoint joinPoint, Async async) 
            throws Throwable {
        
        String executorName = async.value();
        AsyncTaskExecutor targetExecutor = resolveExecutor(executorName);
        
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        
        if (method.getReturnType() == Void.TYPE) {
            targetExecutor.execute(() -> {
                try {
                    joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
            return null;
        } else if (Future.class.isAssignableFrom(method.getReturnType())) {
            return targetExecutor.submit(() -> {
                try {
                    return joinPoint.proceed();
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            throw new IllegalArgumentException(
                "@Async 方法必须返回 void 或 Future");
        }
    }
}

/**
 * @Async 注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Async {
    String value() default "";
}
```

### 3.2 @Scheduled 注解支持

```java
/**
 * @Scheduled 注解处理器
 */
@Component
public class ScheduledAnnotationProcessor implements BeanPostProcessor {
    
    private final TaskScheduler scheduler;
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        Class<?> targetClass = bean.getClass();
        
        for (Method method : targetClass.getMethods()) {
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            if (scheduled != null) {
                processScheduled(bean, method, scheduled);
            }
        }
        
        return bean;
    }
    
    private void processScheduled(Object bean, Method method, Scheduled scheduled) {
        Runnable task = () -> {
            try {
                method.invoke(bean);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        
        if (!scheduled.cron().isEmpty()) {
            // Cron 表达式调度
            ((CronTaskScheduler) scheduler).scheduleWithCron(task, scheduled.cron());
        } else if (scheduled.fixedRate() > 0) {
            // 固定频率调度
            scheduler.scheduleAtFixedRate(task, scheduled.fixedRate());
        } else if (scheduled.fixedDelay() > 0) {
            // 固定延迟调度
            scheduler.scheduleWithFixedDelay(task, scheduled.fixedDelay());
        }
    }
}

/**
 * @Scheduled 注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {
    String cron() default "";
    long fixedRate() default -1;
    long fixedDelay() default -1;
    long initialDelay() default -1;
}
```

## 4. 最佳实践

### 4.1 线程池配置建议

```java
@Configuration
public class TaskExecutorConfig {
    
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // CPU 密集型任务配置
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors);
        executor.setMaxPoolSize(processors * 2);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("task-executor-");
        executor.setRejectedExecutionHandler(
            new ThreadPoolExecutor.CallerRunsPolicy());
        
        executor.initialize();
        return executor;
    }
    
    @Bean
    public ThreadPoolTaskExecutor ioTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // IO 密集型任务配置
        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors * 2);
        executor.setMaxPoolSize(processors * 4);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("io-task-executor-");
        
        executor.initialize();
        return executor;
    }
}
```

### 4.2 优雅关闭

```java
@Component
public class TaskExecutorShutdownHandler {
    
    private final List<ThreadPoolTaskExecutor> executors;
    
    @PreDestroy
    public void shutdown() {
        for (ThreadPoolTaskExecutor executor : executors) {
            executor.shutdown();
            try {
                if (!executor.getThreadPoolExecutor()
                        .awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
```

## 5. 未来扩展方向

### 5.1 计划中的扩展

1. **任务优先级支持**: 支持带优先级的任务队列
2. **任务依赖管理**: 支持任务之间的依赖关系
3. **动态线程池调整**: 运行时调整线程池参数
4. **任务持久化**: 支持任务持久化到数据库
5. **可视化监控**: Web 界面监控任务执行情况

### 5.2 与 Spring Boot 集成

```yaml
# application.yml
spring:
  task:
    execution:
      pool:
        core-size: 8
        max-size: 16
        queue-capacity: 100
        keep-alive: 60s
      thread-name-prefix: "async-"
    scheduling:
      pool:
        size: 5
      thread-name-prefix: "scheduled-"
```
