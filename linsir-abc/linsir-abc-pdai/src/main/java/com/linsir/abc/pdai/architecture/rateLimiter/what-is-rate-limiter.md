# 什么是限流？

## 一、限流的基本概念

### 1.1 什么是限流？

**定义：**
限流（Rate Limiting）是一种通过控制请求的速率来保护系统免受过载影响的技术手段。它通过限制在一定时间窗口内允许通过的请求数量，来防止系统因过载而崩溃或性能严重下降。

**核心思想：**
- 保护系统资源不被耗尽
- 保证系统的稳定性和可用性
- 为所有用户提供公平的服务
- 防止恶意攻击和滥用

**限流的基本原理：**
```
┌─────────────┐
│   客户端     │
└──────┬──────┘
       │ 请求
       ▼
┌─────────────┐
│   限流器     │◀────── 限流规则
└──────┬──────┘
       │ 通过/拒绝
       ▼
┌─────────────┐
│   服务端     │
└─────────────┘
```

### 1.2 为什么需要限流？

**保护系统稳定性：**
- 防止系统因过载而崩溃
- 避免资源耗尽（CPU、内存、数据库连接等）
- 保证系统在高并发下的可用性

**防止恶意攻击：**
- 防止DDoS攻击
- 防止恶意爬虫
- 防止暴力破解

**保证服务质量：**
- 为所有用户提供公平的服务
- 防止某些用户占用过多资源
- 保证核心功能的可用性

**控制成本：**
- 降低服务器资源消耗
- 减少数据库压力
- 降低运营成本

## 二、限流的应用场景

### 2.1 Web应用限流

**场景描述：**
Web应用通常需要限制每个用户或每个IP的访问频率，防止恶意请求和过载。

**常见限流策略：**
- 每个用户每分钟最多请求100次
- 每个IP每分钟最多请求50次
- 每个API接口每秒最多处理1000个请求

**示例：**
```java
@RestController
public class UserController {
    
    @GetMapping("/user/{userId}")
    @RateLimiter(value = 100, timeout = 1, timeUnit = TimeUnit.MINUTES)
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
}
```

### 2.2 API接口限流

**场景描述：**
API接口需要限制调用频率，防止被滥用和过载。

**常见限流策略：**
- 免费用户每分钟最多调用100次
- 付费用户每分钟最多调用1000次
- VIP用户不限流

**示例：**
```java
@RestController
public class ApiController {
    
    @GetMapping("/api/data")
    @RateLimiter(value = 1000, timeout = 1, timeUnit = TimeUnit.MINUTES)
    public ApiResponse getData() {
        return apiService.getData();
    }
}
```

### 2.3 数据库访问限流

**场景描述：**
数据库访问需要限制频率，防止数据库过载。

**常见限流策略：**
- 每秒最多执行1000个查询
- 每秒最多执行100个更新
- 每秒最多执行10个删除

**示例：**
```java
@Service
public class DatabaseService {
    
    @RateLimiter(value = 1000, timeout = 1, timeUnit = TimeUnit.SECONDS)
    public List<User> queryUsers() {
        return userRepository.findAll();
    }
}
```

### 2.4 消息队列限流

**场景描述：**
消息队列需要限制消费速率，防止消费者过载。

**常见限流策略：**
- 每秒最多消费100条消息
- 每分钟最多消费5000条消息
- 根据消费者性能动态调整消费速率

**示例：**
```java
@Component
public class MessageConsumer {
    
    @RateLimiter(value = 100, timeout = 1, timeUnit = TimeUnit.SECONDS)
    @RabbitListener(queues = "messageQueue")
    public void consumeMessage(Message message) {
        messageService.processMessage(message);
    }
}
```

### 2.5 第三方服务调用限流

**场景描述：**
调用第三方服务时需要限制频率，防止超出第三方服务的限制。

**常见限流策略：**
- 每秒最多调用10次
- 每分钟最多调用600次
- 每天最多调用10000次

**示例：**
```java
@Service
public class ThirdPartyService {
    
    @RateLimiter(value = 10, timeout = 1, timeUnit = TimeUnit.SECONDS)
    public ThirdPartyResponse callThirdPartyApi() {
        return thirdPartyClient.callApi();
    }
}
```

## 三、限流的维度

### 3.1 按用户限流

**限流维度：**
- 用户ID
- 用户名
- 用户邮箱

**适用场景：**
- 防止单个用户过度使用系统
- 为不同等级用户提供不同限流策略

**示例：**
```java
@RateLimiter(
    value = 100,
    timeout = 1,
    timeUnit = TimeUnit.MINUTES,
    key = "#userId"
)
public User getUser(Long userId) {
    return userService.getUser(userId);
}
```

### 3.2 按IP限流

**限流维度：**
- 客户端IP地址
- 代理服务器IP地址

**适用场景：**
- 防止恶意IP攻击
- 防止爬虫抓取

**示例：**
```java
@RateLimiter(
    value = 50,
    timeout = 1,
    timeUnit = TimeUnit.MINUTES,
    key = "#request.remoteAddr"
)
public User getUser(HttpServletRequest request, Long userId) {
    return userService.getUser(userId);
}
```

### 3.3 按接口限流

**限流维度：**
- API接口路径
- API接口方法

**适用场景：**
- 保护核心接口
- 为不同接口设置不同限流策略

**示例：**
```java
@RateLimiter(
    value = 1000,
    timeout = 1,
    timeUnit = TimeUnit.SECONDS,
    key = "'getUser'"
)
public User getUser(Long userId) {
    return userService.getUser(userId);
}
```

### 3.4 按系统限流

**限流维度：**
- 整个系统
- 整个应用

**适用场景：**
- 保护整个系统不过载
- 在系统资源紧张时降级服务

**示例：**
```java
@RateLimiter(
    value = 10000,
    timeout = 1,
    timeUnit = TimeUnit.SECONDS,
    key = "'system'"
)
public User getUser(Long userId) {
    return userService.getUser(userId);
}
```

## 四、限流的策略

### 4.1 拒绝策略

**策略描述：**
当请求超过限流阈值时，直接拒绝请求，返回错误信息。

**优点：**
- 实现简单
- 能够快速响应
- 保护系统资源

**缺点：**
- 用户体验较差
- 可能导致请求失败

**示例：**
```java
@RestController
public class UserController {
    
    @GetMapping("/user/{userId}")
    @RateLimiter(
        value = 100,
        timeout = 1,
        timeUnit = TimeUnit.MINUTES,
        fallbackMethod = "rateLimitExceeded"
    )
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
    
    public ResponseEntity<String> rateLimitExceeded(Long userId) {
        return ResponseEntity.status(429)
            .body("请求过于频繁，请稍后再试");
    }
}
```

### 4.2 排队策略

**策略描述：**
当请求超过限流阈值时，将请求放入队列，等待处理。

**优点：**
- 不会拒绝请求
- 用户体验较好

**缺点：**
- 需要维护队列
- 可能导致请求延迟

**示例：**
```java
@Service
public class QueueRateLimiter {
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(1000);
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    
    public void submit(Runnable task) {
        if (!queue.offer(task)) {
            throw new RateLimitExceededException("队列已满，请稍后再试");
        }
    }
    
    @PostConstruct
    public void start() {
        executor.submit(() -> {
            while (true) {
                try {
                    Runnable task = queue.take();
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }
}
```

### 4.3 降级策略

**策略描述：**
当请求超过限流阈值时，返回降级数据或执行降级逻辑。

**优点：**
- 保证系统可用性
- 用户体验较好

**缺点：**
- 可能返回不完整的数据
- 需要实现降级逻辑

**示例：**
```java
@RestController
public class UserController {
    
    @GetMapping("/user/{userId}")
    @RateLimiter(
        value = 100,
        timeout = 1,
        timeUnit = TimeUnit.MINUTES,
        fallbackMethod = "getUserFallback"
    )
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
    
    public User getUserFallback(Long userId) {
        return new User(userId, "默认用户");
    }
}
```

### 4.4 动态调整策略

**策略描述：**
根据系统负载动态调整限流阈值。

**优点：**
- 能够适应系统负载变化
- 提高系统资源利用率

**缺点：**
- 实现复杂
- 需要监控系统负载

**示例：**
```java
@Service
public class DynamicRateLimiter {
    private volatile int currentLimit = 100;
    
    @Scheduled(fixedRate = 5000)
    public void adjustLimit() {
        double cpuUsage = getCpuUsage();
        double memoryUsage = getMemoryUsage();
        
        if (cpuUsage > 80 || memoryUsage > 80) {
            currentLimit = (int) (currentLimit * 0.8);
        } else if (cpuUsage < 50 && memoryUsage < 50) {
            currentLimit = (int) (currentLimit * 1.2);
        }
    }
    
    public boolean tryAcquire() {
        return acquire(currentLimit);
    }
    
    private double getCpuUsage() {
        return 0.0;
    }
    
    private double getMemoryUsage() {
        return 0.0;
    }
}
```

## 五、限流的实现方式

### 5.1 基于计数器的限流

**实现原理：**
使用计数器记录一定时间窗口内的请求数量，当请求数量超过阈值时拒绝请求。

**优点：**
- 实现简单
- 性能高

**缺点：**
- 存在临界问题
- 不够精确

**示例：**
```java
public class CounterRateLimiter {
    private final int limit;
    private final long interval;
    private final AtomicLong counter = new AtomicLong(0);
    private volatile long lastResetTime = System.currentTimeMillis();
    
    public CounterRateLimiter(int limit, long interval, TimeUnit unit) {
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        if (now - lastResetTime >= interval) {
            counter.set(0);
            lastResetTime = now;
        }
        
        if (counter.incrementAndGet() <= limit) {
            return true;
        }
        
        counter.decrementAndGet();
        return false;
    }
}
```

### 5.2 基于滑动窗口的限流

**实现原理：**
使用滑动窗口记录请求时间，计算时间窗口内的请求数量。

**优点：**
- 比计数器更精确
- 避免临界问题

**缺点：**
- 实现相对复杂
- 需要存储请求时间

**示例：**
```java
public class SlidingWindowRateLimiter {
    private final int limit;
    private final long interval;
    private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
    
    public SlidingWindowRateLimiter(int limit, long interval, TimeUnit unit) {
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        
        while (!queue.isEmpty() && now - queue.peek() >= interval) {
            queue.poll();
        }
        
        if (queue.size() < limit) {
            queue.offer(now);
            return true;
        }
        
        return false;
    }
}
```

### 5.3 基于令牌桶的限流

**实现原理：**
以固定速率向桶中放入令牌，请求到来时从桶中获取令牌，如果桶中没有令牌则拒绝请求。

**优点：**
- 可以处理突发流量
- 限流平滑

**缺点：**
- 实现相对复杂
- 需要维护令牌桶

**示例：**
```java
public class TokenBucketRateLimiter {
    private final int capacity;
    private final int rate;
    private final AtomicInteger tokens = new AtomicInteger(0);
    private volatile long lastRefillTime = System.currentTimeMillis();
    
    public TokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public synchronized boolean tryAcquire() {
        refill();
        
        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        
        return false;
    }
    
    private void refill() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        
        if (elapsed > 0) {
            int newTokens = (int) (elapsed * rate / 1000);
            tokens.set(Math.min(capacity, tokens.get() + newTokens));
            lastRefillTime = now;
        }
    }
}
```

### 5.4 基于漏桶的限流

**实现原理：**
请求进入漏桶，漏桶以固定速率处理请求，如果漏桶满了则拒绝请求。

**优点：**
- 可以平滑流量
- 限流精确

**缺点：**
- 不能处理突发流量
- 实现相对复杂

**示例：**
```java
public class LeakyBucketRateLimiter {
    private final int capacity;
    private final int rate;
    private final AtomicInteger water = new AtomicInteger(0);
    private volatile long lastLeakTime = System.currentTimeMillis();
    
    public LeakyBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public synchronized boolean tryAcquire() {
        leak();
        
        if (water.get() < capacity) {
            water.incrementAndGet();
            return true;
        }
        
        return false;
    }
    
    private void leak() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastLeakTime;
        
        if (elapsed > 0) {
            int leaked = (int) (elapsed * rate / 1000);
            water.set(Math.max(0, water.get() - leaked));
            lastLeakTime = now;
        }
    }
}
```

## 六、限流的最佳实践

### 6.1 合理设置限流阈值

**设置原则：**
- 根据系统性能设置阈值
- 根据业务需求设置阈值
- 根据用户等级设置不同阈值
- 定期评估和调整阈值

**示例：**
```java
@RateLimiter(
    value = 1000,
    timeout = 1,
    timeUnit = TimeUnit.SECONDS,
    key = "#userId",
    fallbackMethod = "getUserFallback"
)
public User getUser(Long userId) {
    return userService.getUser(userId);
}
```

### 6.2 选择合适的限流算法

**算法选择：**
- 简单场景：使用计数器
- 需要精确限流：使用滑动窗口
- 需要处理突发流量：使用令牌桶
- 需要平滑流量：使用漏桶

### 6.3 合理设置限流维度

**维度选择：**
- 用户级别：按用户限流
- IP级别：按IP限流
- 接口级别：按接口限流
- 系统级别：按系统限流

### 6.4 合理设置限流策略

**策略选择：**
- 对用户体验要求高：使用排队策略
- 对系统稳定性要求高：使用拒绝策略
- 对数据完整性要求高：使用降级策略
- 对资源利用率要求高：使用动态调整策略

### 6.5 做好限流监控

**监控指标：**
- 限流触发次数
- 限流触发率
- 限流响应时间
- 限流错误率

**示例：**
```java
@Component
public class RateLimiterMonitor {
    private final MeterRegistry meterRegistry;
    
    public RateLimiterMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void recordRateLimit(String key) {
        meterRegistry.counter("rate.limit", "key", key).increment();
    }
    
    public void recordRateLimitExceeded(String key) {
        meterRegistry.counter("rate.limit.exceeded", "key", key).increment();
    }
}
```

## 七、总结

限流是保护系统的重要手段，通过控制请求的速率来防止系统过载。

**关键要点：**
1. 限流可以保护系统稳定性，防止系统过载
2. 限流可以防止恶意攻击，保护系统安全
3. 限流可以保证服务质量，为用户提供公平的服务
4. 限流可以控制成本，降低资源消耗

**选择限流方案时需要考虑：**
- 业务场景
- 系统性能
- 用户体验
- 实现复杂度

**最佳实践：**
1. 合理设置限流阈值
2. 选择合适的限流算法
3. 合理设置限流维度
4. 合理设置限流策略
5. 做好限流监控