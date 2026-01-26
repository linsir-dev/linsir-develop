# 在单机情况下如何实现限流？

## 一、单机限流概述

### 1.1 单机限流的特点

**定义：**
单机限流是指在单个应用实例内部实现的限流机制，限流状态存储在本地内存中。

**特点：**
- 限流状态存储在本地内存中
- 不依赖外部存储
- 实现简单，性能高
- 只能限制单个实例的请求
- 不适合分布式环境

**适用场景：**
- 单机应用
- 对限流精度要求不高的场景
- 性能要求高的场景
- 不需要跨实例限流的场景

### 1.2 单机限流的优缺点

**优点：**
- 实现简单
- 性能高
- 不依赖外部存储
- 响应速度快

**缺点：**
- 只能限制单个实例的请求
- 不适合分布式环境
- 实例重启后限流状态丢失
- 无法实现跨实例限流

### 1.3 单机限流的应用场景

**常见场景：**
- 单机Web应用
- 单机API服务
- 单机微服务实例
- 本地工具应用

## 二、基于本地内存的限流实现

### 2.1 基于计数器的限流

**实现原理：**
使用计数器记录一定时间窗口内的请求数量，当请求数量超过阈值时拒绝请求。

**代码实现：**
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
    
    public int getLimit() {
        return limit;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getCount() {
        return counter.get();
    }
}
```

**使用示例：**
```java
public class CounterRateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        CounterRateLimiter limiter = new CounterRateLimiter(100, 1, TimeUnit.MINUTES);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
            Thread.sleep(10);
        }
    }
}
```

### 2.2 基于滑动窗口的限流

**实现原理：**
使用滑动窗口记录请求时间，计算时间窗口内的请求数量。

**代码实现：**
```java
public class SlidingWindowRateLimiter {
    private final int limit;
    private final long windowSize;
    private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
    
    public SlidingWindowRateLimiter(int limit, long windowSize, TimeUnit unit) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
    }
    
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        
        while (!queue.isEmpty() && now - queue.peek() >= windowSize) {
            queue.poll();
        }
        
        if (queue.size() < limit) {
            queue.offer(now);
            return true;
        }
        
        return false;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getWindowSize() {
        return windowSize;
    }
    
    public int getCount() {
        return queue.size();
    }
}
```

**使用示例：**
```java
public class SlidingWindowRateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(100, 1, TimeUnit.MINUTES);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
            Thread.sleep(10);
        }
    }
}
```

### 2.3 基于令牌桶的限流

**实现原理：**
以固定速率向桶中放入令牌，请求到来时从桶中获取令牌。

**代码实现：**
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
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getTokens() {
        return tokens.get();
    }
}
```

**使用示例：**
```java
public class TokenBucketRateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(100, 10);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
            Thread.sleep(50);
        }
    }
}
```

### 2.4 基于漏桶的限流

**实现原理：**
请求进入漏桶，漏桶以固定速率处理请求。

**代码实现：**
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
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getWater() {
        return water.get();
    }
}
```

**使用示例：**
```java
public class LeakyBucketRateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        LeakyBucketRateLimiter limiter = new LeakyBucketRateLimiter(100, 10);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
            Thread.sleep(50);
        }
    }
}
```

## 三、基于Guava Cache的限流实现

### 3.1 使用Guava Cache实现限流

**实现原理：**
使用Guava Cache存储请求时间，计算时间窗口内的请求数量。

**代码实现：**
```java
public class GuavaCacheRateLimiter {
    private final int limit;
    private final long windowSize;
    private final Cache<String, Long> cache;
    
    public GuavaCacheRateLimiter(int limit, long windowSize, TimeUnit unit) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
        this.cache = CacheBuilder.newBuilder()
            .expireAfterWrite(windowSize, unit)
            .build();
    }
    
    public synchronized boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        long count = cache.asMap().values().stream()
            .filter(time -> now - time < windowSize)
            .count();
        
        if (count < limit) {
            cache.put(key + ":" + now, now);
            return true;
        }
        
        return false;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getWindowSize() {
        return windowSize;
    }
    
    public int getCount() {
        long now = System.currentTimeMillis();
        return (int) cache.asMap().values().stream()
            .filter(time -> now - time < windowSize)
            .count();
    }
}
```

**使用示例：**
```java
public class GuavaCacheRateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        GuavaCacheRateLimiter limiter = new GuavaCacheRateLimiter(100, 1, TimeUnit.MINUTES);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire("user:" + i);
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
            Thread.sleep(10);
        }
    }
}
```

### 3.2 使用Guava Cache实现令牌桶

**代码实现：**
```java
public class GuavaTokenBucketRateLimiter {
    private final int capacity;
    private final int rate;
    private final Cache<String, AtomicInteger> tokenCache;
    
    public GuavaTokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokenCache = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();
    }
    
    public boolean tryAcquire(String key) {
        AtomicInteger tokens = tokenCache.get(key, () -> new AtomicInteger(capacity));
        
        synchronized (tokens) {
            refill(tokens);
            
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            
            return false;
        }
    }
    
    private void refill(AtomicInteger tokens) {
        long now = System.currentTimeMillis();
        long lastRefillTime = tokens.get() % 1000000;
        long elapsed = now - lastRefillTime;
        
        if (elapsed > 0) {
            int newTokens = (int) (elapsed * rate / 1000);
            int currentTokens = tokens.get() / 1000000;
            tokens.set(Math.min(capacity, currentTokens + newTokens) * 1000000 + now);
        }
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getTokens(String key) {
        AtomicInteger tokens = tokenCache.getIfPresent(key);
        if (tokens == null) {
            return capacity;
        }
        return tokens.get() / 1000000;
    }
}
```

## 四、基于Caffeine的限流实现

### 4.1 使用Caffeine实现限流

**实现原理：**
使用Caffeine存储请求时间，计算时间窗口内的请求数量。

**代码实现：**
```java
public class CaffeineRateLimiter {
    private final int limit;
    private final long windowSize;
    private final Cache<String, Long> cache;
    
    public CaffeineRateLimiter(int limit, long windowSize, TimeUnit unit) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
        this.cache = Caffeine.newBuilder()
            .expireAfterWrite(windowSize, unit)
            .build();
    }
    
    public synchronized boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        long count = cache.asMap().values().stream()
            .filter(time -> now - time < windowSize)
            .count();
        
        if (count < limit) {
            cache.put(key + ":" + now, now);
            return true;
        }
        
        return false;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getWindowSize() {
        return windowSize;
    }
    
    public int getCount() {
        long now = System.currentTimeMillis();
        return (int) cache.asMap().values().stream()
            .filter(time -> now - time < windowSize)
            .count();
    }
}
```

**使用示例：**
```java
public class CaffeineRateLimiterExample {
    public static void main(String[] args) throws InterruptedException {
        CaffeineRateLimiter limiter = new CaffeineRateLimiter(100, 1, TimeUnit.MINUTES);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire("user:" + i);
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
            Thread.sleep(10);
        }
    }
}
```

### 4.2 使用Caffeine实现令牌桶

**代码实现：**
```java
public class CaffeineTokenBucketRateLimiter {
    private final int capacity;
    private final int rate;
    private final Cache<String, AtomicInteger> tokenCache;
    
    public CaffeineTokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
        this.tokenCache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .build();
    }
    
    public boolean tryAcquire(String key) {
        AtomicInteger tokens = tokenCache.get(key, () -> new AtomicInteger(capacity));
        
        synchronized (tokens) {
            refill(tokens);
            
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            
            return false;
        }
    }
    
    private void refill(AtomicInteger tokens) {
        long now = System.currentTimeMillis();
        long lastRefillTime = tokens.get() % 1000000;
        long elapsed = now - lastRefillTime;
        
        if (elapsed > 0) {
            int newTokens = (int) (elapsed * rate / 1000);
            int currentTokens = tokens.get() / 1000000;
            tokens.set(Math.min(capacity, currentTokens + newTokens) * 1000000 + now);
        }
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getTokens(String key) {
        AtomicInteger tokens = tokenCache.getIfPresent(key);
        if (tokens == null) {
            return capacity;
        }
        return tokens.get() / 1000000;
    }
}
```

## 五、基于Semaphore的限流实现

### 5.1 使用Semaphore实现限流

**实现原理：**
使用Semaphore控制并发访问数量。

**代码实现：**
```java
public class SemaphoreRateLimiter {
    private final Semaphore semaphore;
    
    public SemaphoreRateLimiter(int permits) {
        this.semaphore = new Semaphore(permits);
    }
    
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }
    
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return semaphore.tryAcquire(timeout, unit);
    }
    
    public void acquire() throws InterruptedException {
        semaphore.acquire();
    }
    
    public void release() {
        semaphore.release();
    }
    
    public int getAvailablePermits() {
        return semaphore.availablePermits();
    }
}
```

**使用示例：**
```java
public class SemaphoreRateLimiterExample {
    public static void main(String[] args) {
        SemaphoreRateLimiter limiter = new SemaphoreRateLimiter(10);
        
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                if (limiter.tryAcquire()) {
                    try {
                        System.out.println("请求通过: " + Thread.currentThread().getName());
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        limiter.release();
                    }
                } else {
                    System.out.println("请求拒绝: " + Thread.currentThread().getName());
                }
            }).start();
        }
    }
}
```

### 5.2 使用Semaphore实现时间窗口限流

**代码实现：**
```java
public class SemaphoreWindowRateLimiter {
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;
    
    public SemaphoreWindowRateLimiter(int permits, long interval, TimeUnit unit) {
        this.semaphore = new Semaphore(permits);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        
        scheduler.scheduleAtFixedRate(() -> {
            int available = semaphore.availablePermits();
            if (available < permits) {
                semaphore.release(permits - available);
            }
        }, interval, interval, unit);
    }
    
    public boolean tryAcquire() {
        return semaphore.tryAcquire();
    }
    
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        return semaphore.tryAcquire(timeout, unit);
    }
    
    public void shutdown() {
        scheduler.shutdown();
    }
}
```

## 六、基于Atomic的限流实现

### 6.1 使用Atomic实现计数器限流

**代码实现：**
```java
public class AtomicCounterRateLimiter {
    private final int limit;
    private final long interval;
    private final AtomicLong counter = new AtomicLong(0);
    private final AtomicLong lastResetTime = new AtomicLong(System.currentTimeMillis());
    
    public AtomicCounterRateLimiter(int limit, long interval, TimeUnit unit) {
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    public boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long lastReset = lastResetTime.get();
        
        if (now - lastReset >= interval) {
            if (lastResetTime.compareAndSet(lastReset, now)) {
                counter.set(0);
            }
        }
        
        long current = counter.incrementAndGet();
        if (current <= limit) {
            return true;
        }
        
        counter.decrementAndGet();
        return false;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getCount() {
        return counter.get();
    }
}
```

### 6.2 使用Atomic实现令牌桶限流

**代码实现：**
```java
public class AtomicTokenBucketRateLimiter {
    private final int capacity;
    private final int rate;
    private final AtomicInteger tokens = new AtomicInteger(capacity);
    private final AtomicLong lastRefillTime = new AtomicLong(System.currentTimeMillis());
    
    public AtomicTokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire() {
        refill();
        
        while (true) {
            int current = tokens.get();
            if (current <= 0) {
                return false;
            }
            
            if (tokens.compareAndSet(current, current - 1)) {
                return true;
            }
        }
    }
    
    private void refill() {
        long now = System.currentTimeMillis();
        long lastRefill = lastRefillTime.get();
        
        if (now - lastRefill > 0) {
            if (lastRefillTime.compareAndSet(lastRefill, now)) {
                long elapsed = now - lastRefill;
                int newTokens = (int) (elapsed * rate / 1000);
                
                while (true) {
                    int current = tokens.get();
                    int updated = Math.min(capacity, current + newTokens);
                    
                    if (tokens.compareAndSet(current, updated)) {
                        break;
                    }
                }
            }
        }
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getTokens() {
        return tokens.get();
    }
}
```

## 七、单机限流的最佳实践

### 7.1 选择合适的限流算法

**算法选择：**
- 简单场景：使用计数器限流
- 精确限流：使用滑动窗口限流
- 突发流量：使用令牌桶限流
- 平滑流量：使用漏桶限流

### 7.2 合理设置限流参数

**参数设置：**
- 根据系统性能设置限流阈值
- 根据业务需求设置时间窗口
- 根据用户等级设置不同限流
- 定期评估和调整参数

### 7.3 做好限流监控

**监控指标：**
- 限流触发次数
- 限流触发率
- 限流响应时间
- 限流错误率

**监控实现：**
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

### 7.4 做好限流降级

**降级策略：**
- 限流触发时返回错误信息
- 限流触发时返回降级数据
- 限流触发时排队等待

**降级实现：**
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
    
    public ResponseEntity<String> getUserFallback(Long userId) {
        return ResponseEntity.status(429)
            .body("请求过于频繁，请稍后再试");
    }
}
```

### 7.5 做好限流日志

**日志记录：**
- 记录限流触发时间
- 记录限流触发原因
- 记录限流触发次数
- 记录限流触发用户

**日志实现：**
```java
@Component
public class RateLimiterLogger {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterLogger.class);
    
    public void logRateLimit(String key, String userId) {
        logger.warn("限流触发: key={}, userId={}", key, userId);
    }
}
```

## 八、总结

单机限流是在单个应用实例内部实现的限流机制，具有实现简单、性能高的特点。

**关键要点：**
1. 单机限流适合单机应用
2. 单机限流不依赖外部存储
3. 单机限流性能高
4. 单机限流只能限制单个实例的请求

**实现方式：**
1. 基于本地内存的限流
2. 基于Guava Cache的限流
3. 基于Caffeine的限流
4. 基于Semaphore的限流
5. 基于Atomic的限流

**最佳实践：**
1. 选择合适的限流算法
2. 合理设置限流参数
3. 做好限流监控
4. 做好限流降级
5. 做好限流日志

**注意事项：**
1. 单机限流不适合分布式环境
2. 实例重启后限流状态丢失
3. 无法实现跨实例限流
4. 需要定期评估和调整限流策略