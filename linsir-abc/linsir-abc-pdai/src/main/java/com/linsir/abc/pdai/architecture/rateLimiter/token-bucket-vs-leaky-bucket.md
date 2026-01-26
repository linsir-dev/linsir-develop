# 限流令牌桶和漏桶对比

## 一、算法原理对比

### 1.1 令牌桶算法原理

**基本思想：**
令牌桶算法以固定速率向桶中放入令牌，请求到来时从桶中获取令牌，如果桶中没有令牌则拒绝请求。

**工作原理：**
```
令牌桶：
┌─────────────────┐
│  令牌 令牌 令牌  │ ← 以固定速率放入令牌
│  令牌 令牌 令牌  │
│  令牌 令牌 令牌  │
└─────────────────┘
        ↓
    获取令牌
        ↓
    处理请求
```

**核心特点：**
- 以固定速率生成令牌
- 桶有最大容量，超过容量的令牌会被丢弃
- 请求到来时从桶中获取令牌
- 如果桶中有令牌，请求通过，令牌减少
- 如果桶中没有令牌，请求被拒绝

### 1.2 漏桶算法原理

**基本思想：**
漏桶算法将请求放入漏桶，漏桶以固定速率处理请求，如果漏桶满了则拒绝请求。

**工作原理：**
```
请求 → 漏桶 → 以固定速率处理请求
       ↑
       桶满则拒绝
```

**核心特点：**
- 请求到来时放入漏桶
- 漏桶以固定速率处理请求
- 漏桶有最大容量
- 如果漏桶满了，拒绝请求
- 漏桶以固定速率漏水

### 1.3 原理对比

| 特性 | 令牌桶 | 漏桶 |
|------|--------|------|
| 核心思想 | 以固定速率生成令牌 | 以固定速率处理请求 |
| 令牌/请求处理 | 请求到来时获取令牌 | 请求到来时放入桶中 |
| 速率控制 | 控制令牌生成速率 | 控制请求处理速率 |
| 容量限制 | 桶有最大容量 | 桶有最大容量 |
| 溢出处理 | 超过容量的令牌被丢弃 | 桶满则拒绝请求 |

## 二、算法实现对比

### 2.1 令牌桶实现

**简单实现：**
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

**支持等待的实现：**
```java
public class TokenBucketRateLimiterWithWait {
    private final int capacity;
    private final int rate;
    private final AtomicInteger tokens = new AtomicInteger(0);
    private volatile long lastRefillTime = System.currentTimeMillis();
    
    public TokenBucketRateLimiterWithWait(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        
        while (true) {
            refill();
            
            if (tokens.get() > 0) {
                tokens.decrementAndGet();
                return true;
            }
            
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                return false;
            }
            
            long sleepTime = Math.min(remaining, 1000000000L / rate);
            Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
        }
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

### 2.2 漏桶实现

**简单实现：**
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

**支持等待的实现：**
```java
public class LeakyBucketRateLimiterWithWait {
    private final int capacity;
    private final int rate;
    private final AtomicInteger water = new AtomicInteger(0);
    private volatile long lastLeakTime = System.currentTimeMillis();
    
    public LeakyBucketRateLimiterWithWait(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        
        while (true) {
            leak();
            
            if (water.get() < capacity) {
                water.incrementAndGet();
                return true;
            }
            
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) {
                return false;
            }
            
            long sleepTime = Math.min(remaining, 1000000000L / rate);
            Thread.sleep(sleepTime / 1000000, (int) (sleepTime % 1000000));
        }
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

### 2.3 实现对比

| 特性 | 令牌桶 | 漏桶 |
|------|--------|------|
| 核心变量 | tokens（令牌数量） | water（水量） |
| 核心操作 | refill（补充令牌） | leak（漏水） |
| 限流判断 | tokens > 0 | water < capacity |
| 获取资源 | tokens-- | water++ |
| 资源恢复 | 定期补充令牌 | 定期漏水 |

## 三、性能对比

### 3.1 时间复杂度

**令牌桶：**
- tryAcquire：O(1)
- refill：O(1)
- 总体：O(1)

**漏桶：**
- tryAcquire：O(1)
- leak：O(1)
- 总体：O(1)

**结论：** 两种算法的时间复杂度相同，都是O(1)。

### 3.2 空间复杂度

**令牌桶：**
- 只需要存储令牌数量
- 空间复杂度：O(1)

**漏桶：**
- 只需要存储水量
- 空间复杂度：O(1)

**结论：** 两种算法的空间复杂度相同，都是O(1)。

### 3.3 性能测试

**测试代码：**
```java
public class RateLimiterPerformanceTest {
    private static final int THREAD_COUNT = 100;
    private static final int REQUEST_COUNT = 100000;
    
    public static void main(String[] args) throws InterruptedException {
        testTokenBucket();
        testLeakyBucket();
    }
    
    private static void testTokenBucket() throws InterruptedException {
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(1000, 100);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicLong successCount = new AtomicLong(0);
        AtomicLong failCount = new AtomicLong(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                for (int j = 0; j < REQUEST_COUNT / THREAD_COUNT; j++) {
                    if (limiter.tryAcquire()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                }
                latch.countDown();
            }).start();
        }
        
        latch.await();
        long endTime = System.currentTimeMillis();
        
        System.out.println("令牌桶算法:");
        System.out.println("成功请求: " + successCount.get());
        System.out.println("失败请求: " + failCount.get());
        System.out.println("总耗时: " + (endTime - startTime) + "ms");
    }
    
    private static void testLeakyBucket() throws InterruptedException {
        LeakyBucketRateLimiter limiter = new LeakyBucketRateLimiter(1000, 100);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicLong successCount = new AtomicLong(0);
        AtomicLong failCount = new AtomicLong(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            new Thread(() -> {
                for (int j = 0; j < REQUEST_COUNT / THREAD_COUNT; j++) {
                    if (limiter.tryAcquire()) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                }
                latch.countDown();
            }).start();
        }
        
        latch.await();
        long endTime = System.currentTimeMillis();
        
        System.out.println("漏桶算法:");
        System.out.println("成功请求: " + successCount.get());
        System.out.println("失败请求: " + failCount.get());
        System.out.println("总耗时: " + (endTime - startTime) + "ms");
    }
}
```

**结论：** 两种算法的性能基本相同，差异不大。

## 四、特性对比

### 4.1 突发流量处理

**令牌桶：**
- 可以处理突发流量
- 桶中可以积累令牌
- 突发流量可以快速消耗令牌

**示例：**
```java
TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(100, 10);

// 初始桶中有100个令牌
for (int i = 0; i < 100; i++) {
    boolean acquired = limiter.tryAcquire();
    System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
}
// 前100个请求全部通过
```

**漏桶：**
- 不能处理突发流量
- 桶满后拒绝请求
- 请求处理速率固定

**示例：**
```java
LeakyBucketRateLimiter limiter = new LeakyBucketRateLimiter(100, 10);

// 初始桶是空的
for (int i = 0; i < 100; i++) {
    boolean acquired = limiter.tryAcquire();
    System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
}
// 前100个请求全部通过，但后续请求会被限流
```

**对比结论：**
- 令牌桶可以处理突发流量
- 漏桶不能处理突发流量

### 4.2 流量平滑性

**令牌桶：**
- 流量相对平滑
- 可以处理突发流量
- 令牌生成速率固定

**漏桶：**
- 流量非常平滑
- 不能处理突发流量
- 请求处理速率固定

**对比结论：**
- 漏桶的流量平滑性更好
- 令牌桶可以处理突发流量

### 4.3 限流精度

**令牌桶：**
- 限流精度中等
- 可以处理突发流量
- 令牌生成速率固定

**漏桶：**
- 限流精度高
- 不能处理突发流量
- 请求处理速率固定

**对比结论：**
- 漏桶的限流精度更高
- 令牌桶可以处理突发流量

### 4.4 资源利用率

**令牌桶：**
- 资源利用率高
- 可以处理突发流量
- 桶中可以积累令牌

**漏桶：**
- 资源利用率中等
- 不能处理突发流量
- 桶满后拒绝请求

**对比结论：**
- 令牌桶的资源利用率更高
- 漏桶的资源利用率中等

## 五、适用场景对比

### 5.1 令牌桶适用场景

**适合场景：**
1. **需要处理突发流量的场景**
   - 电商秒杀
   - 抢红包
   - 限时抢购

2. **需要灵活调整限流策略的场景**
   - 根据系统负载动态调整
   - 根据用户等级设置不同限流
   - 根据时间段设置不同限流

3. **需要高资源利用率的场景**
   - 充分利用系统资源
   - 提高系统吞吐量

**示例：**
```java
@RestController
public class SeckillController {
    private final TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(10000, 1000);
    
    @PostMapping("/seckill")
    public ResponseEntity<String> seckill(@RequestParam Long productId) {
        if (!limiter.tryAcquire()) {
            return ResponseEntity.status(429).body("请求过于频繁");
        }
        
        return seckillService.seckill(productId);
    }
}
```

### 5.2 漏桶适用场景

**适合场景：**
1. **需要平滑流量的场景**
   - 保护数据库
   - 保护API接口
   - 保护第三方服务

2. **需要精确限流的场景**
   - 严格控制请求速率
   - 保护下游系统

3. **需要保护下游系统的场景**
   - 数据库访问限流
   - API接口限流
   - 第三方服务调用限流

**示例：**
```java
@RestController
public class ApiController {
    private final LeakyBucketRateLimiter limiter = new LeakyBucketRateLimiter(100, 10);
    
    @GetMapping("/api/data")
    public ResponseEntity<String> getData() {
        if (!limiter.tryAcquire()) {
            return ResponseEntity.status(429).body("请求过于频繁");
        }
        
        return ResponseEntity.ok(apiService.getData());
    }
}
```

### 5.3 场景对比

| 场景 | 令牌桶 | 漏桶 |
|------|--------|------|
| 电商秒杀 | 适合 | 不适合 |
| 抢红包 | 适合 | 不适合 |
| 限时抢购 | 适合 | 不适合 |
| 保护数据库 | 不适合 | 适合 |
| 保护API接口 | 不适合 | 适合 |
| 保护第三方服务 | 不适合 | 适合 |

## 六、优缺点对比

### 6.1 令牌桶优缺点

**优点：**
1. 可以处理突发流量
2. 资源利用率高
3. 灵活性高，可以调整令牌生成速率
4. 实现相对简单
5. 性能高，时间复杂度O(1)

**缺点：**
1. 限流精度中等
2. 突发流量可能导致后续请求被限流
3. 需要维护令牌桶
4. 实现相对复杂

### 6.2 漏桶优缺点

**优点：**
1. 流量平滑性好
2. 限流精度高
3. 实现相对简单
4. 性能高，时间复杂度O(1)
5. 可以保护下游系统

**缺点：**
1. 不能处理突发流量
2. 资源利用率中等
3. 可能导致请求延迟
4. 需要维护漏桶
5. 实现相对复杂

### 6.3 优缺点对比

| 特性 | 令牌桶 | 漏桶 |
|------|--------|------|
| 处理突发流量 | 优点 | 缺点 |
| 流量平滑性 | 中等 | 优点 |
| 限流精度 | 中等 | 优点 |
| 资源利用率 | 优点 | 中等 |
| 保护下游系统 | 中等 | 优点 |
| 实现复杂度 | 中等 | 中等 |
| 性能 | 优点 | 优点 |

## 七、选择建议

### 7.1 根据业务场景选择

**需要处理突发流量：**
- 选择令牌桶算法
- 优点：可以处理突发流量
- 缺点：限流精度中等

**需要平滑流量：**
- 选择漏桶算法
- 优点：流量平滑性好
- 缺点：不能处理突发流量

**需要精确限流：**
- 选择漏桶算法
- 优点：限流精度高
- 缺点：不能处理突发流量

**需要高资源利用率：**
- 选择令牌桶算法
- 优点：资源利用率高
- 缺点：限流精度中等

### 7.2 根据系统需求选择

**保护下游系统：**
- 选择漏桶算法
- 优点：可以保护下游系统
- 缺点：不能处理突发流量

**提高系统吞吐量：**
- 选择令牌桶算法
- 优点：资源利用率高
- 缺点：限流精度中等

**保证服务质量：**
- 选择漏桶算法
- 优点：流量平滑性好
- 缺点：不能处理突发流量

**应对突发流量：**
- 选择令牌桶算法
- 优点：可以处理突发流量
- 缺点：限流精度中等

### 7.3 选择流程

```
开始
  ↓
是否需要处理突发流量？
  ├─ 是 → 使用令牌桶算法
  └─ 否 → 是否需要平滑流量？
           ├─ 是 → 使用漏桶算法
           └─ 否 → 是否需要精确限流？
                    ├─ 是 → 使用漏桶算法
                    └─ 否 → 使用令牌桶算法
```

## 八、总结

令牌桶和漏桶是两种常用的限流算法，各有优缺点：

**令牌桶算法：**
- 优点：可以处理突发流量，资源利用率高
- 缺点：限流精度中等
- 适用场景：需要处理突发流量、需要高资源利用率

**漏桶算法：**
- 优点：流量平滑性好，限流精度高
- 缺点：不能处理突发流量
- 适用场景：需要平滑流量、需要精确限流、保护下游系统

**选择建议：**
1. 需要处理突发流量：选择令牌桶算法
2. 需要平滑流量：选择漏桶算法
3. 需要精确限流：选择漏桶算法
4. 需要高资源利用率：选择令牌桶算法
5. 保护下游系统：选择漏桶算法

**最佳实践：**
1. 根据业务场景选择合适的算法
2. 合理设置算法参数（容量、速率）
3. 做好限流监控
4. 做好限流降级
5. 定期评估和调整限流策略