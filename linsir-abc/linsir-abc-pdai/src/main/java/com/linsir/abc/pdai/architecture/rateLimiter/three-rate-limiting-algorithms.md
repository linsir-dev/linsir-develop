# 三种限流的算法

## 一、固定窗口算法

### 1.1 算法原理

**基本思想：**
固定窗口算法将时间划分为固定大小的窗口，在每个窗口内统计请求数量，当请求数量超过阈值时拒绝请求。

**工作原理：**
```
时间轴：|----窗口1----|----窗口2----|----窗口3----|
请求：  X  X  X     X  X  X     X  X  X
计数：  3            3            3
```

**算法步骤：**
1. 将时间划分为固定大小的窗口
2. 在每个窗口内统计请求数量
3. 当请求数量超过阈值时拒绝请求
4. 窗口结束后重置计数器

### 1.2 算法实现

**简单实现：**
```java
public class FixedWindowRateLimiter {
    private final int limit;
    private final long windowSize;
    private final AtomicLong counter = new AtomicLong(0);
    private volatile long windowStart = System.currentTimeMillis();
    
    public FixedWindowRateLimiter(int limit, long windowSize, TimeUnit unit) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
    }
    
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        
        if (now - windowStart >= windowSize) {
            counter.set(0);
            windowStart = now;
        }
        
        if (counter.incrementAndGet() <= limit) {
            return true;
        }
        
        counter.decrementAndGet();
        return false;
    }
}
```

**使用示例：**
```java
public class FixedWindowExample {
    public static void main(String[] args) {
        FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(100, 1, TimeUnit.MINUTES);
        
        for (int i = 0; i < 150; i++) {
            boolean acquired = limiter.tryAcquire();
            System.out.println("请求 " + i + ": " + (acquired ? "通过" : "拒绝"));
        }
    }
}
```

### 1.3 算法优缺点

**优点：**
- 实现简单，易于理解
- 内存占用小，只需要存储计数器
- 性能高，时间复杂度O(1)

**缺点：**
- 存在临界问题（边界效应）
- 限流不够精确
- 无法处理突发流量

**临界问题示例：**
```
窗口1 (0-1秒):  请求  请求  请求  ...  请求 (100个)
窗口2 (1-2秒):  请求  请求  请求  ...  请求 (100个)

在1秒边界处：
- 窗口1的最后100ms：100个请求
- 窗口2的前100ms：100个请求
- 实际上在200ms内有200个请求，远超限流阈值
```

### 1.4 适用场景

**适合场景：**
- 对限流精度要求不高的场景
- 请求分布相对均匀的场景
- 简单的限流需求

**不适合场景：**
- 对限流精度要求高的场景
- 请求分布不均匀的场景
- 需要处理突发流量的场景

## 二、滑动窗口算法

### 2.1 算法原理

**基本思想：**
滑动窗口算法将时间划分为更小的格子，使用滑动窗口统计请求数量，能够更精确地限流。

**工作原理：**
```
时间轴：|格子1|格子2|格子3|格子4|格子5|格子6|
请求：  X    X    X    X    X    X
滑动窗口：    |--------窗口--------|
计数：  0    2    3    3    2    0
```

**算法步骤：**
1. 将时间划分为更小的格子
2. 记录每个格子内的请求数量
3. 使用滑动窗口统计请求数量
4. 当请求数量超过阈值时拒绝请求
5. 定期清理过期的格子

### 2.2 算法实现

**基于队列的实现：**
```java
public class SlidingWindowRateLimiter {
    private final int limit;
    private final long windowSize;
    private final long slotSize;
    private final ConcurrentLinkedQueue<Long> queue = new ConcurrentLinkedQueue<>();
    
    public SlidingWindowRateLimiter(int limit, long windowSize, TimeUnit unit) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
        this.slotSize = windowSize / 10;
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
}
```

**基于环形数组的实现：**
```java
public class CircularSlidingWindowRateLimiter {
    private final int limit;
    private final long windowSize;
    private final int slotCount;
    private final AtomicLongArray counters;
    private final AtomicLongArray timestamps;
    private volatile long windowStart;
    
    public CircularSlidingWindowRateLimiter(int limit, long windowSize, TimeUnit unit, int slotCount) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
        this.slotCount = slotCount;
        this.counters = new AtomicLongArray(slotCount);
        this.timestamps = new AtomicLongArray(slotCount);
        this.windowStart = System.currentTimeMillis();
    }
    
    public synchronized boolean tryAcquire() {
        long now = System.currentTimeMillis();
        long elapsed = now - windowStart;
        
        if (elapsed >= windowSize) {
            resetWindow(now);
            elapsed = 0;
        }
        
        int slotIndex = (int) ((elapsed * slotCount) / windowSize);
        long slotTimestamp = timestamps.get(slotIndex);
        
        if (now - slotTimestamp >= windowSize) {
            counters.set(slotIndex, 0);
            timestamps.set(slotIndex, now);
        }
        
        long total = 0;
        for (int i = 0; i < slotCount; i++) {
            if (now - timestamps.get(i) < windowSize) {
                total += counters.get(i);
            }
        }
        
        if (total < limit) {
            counters.incrementAndGet(slotIndex);
            return true;
        }
        
        return false;
    }
    
    private void resetWindow(long now) {
        for (int i = 0; i < slotCount; i++) {
            counters.set(i, 0);
            timestamps.set(i, now);
        }
        windowStart = now;
    }
}
```

**使用示例：**
```java
public class SlidingWindowExample {
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

### 2.3 算法优缺点

**优点：**
- 限流精确，没有临界问题
- 可以平滑流量
- 内存占用相对较小

**缺点：**
- 实现相对复杂
- 需要存储请求时间
- 性能比固定窗口算法略低

### 2.4 适用场景

**适合场景：**
- 对限流精度要求高的场景
- 请求分布不均匀的场景
- 需要精确限流的场景

**不适合场景：**
- 对限流精度要求不高的场景
- 内存受限的场景
- 简单的限流需求

## 三、令牌桶算法

### 3.1 算法原理

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

**算法步骤：**
1. 以固定速率向桶中放入令牌
2. 桶有最大容量，超过容量的令牌会被丢弃
3. 请求到来时从桶中获取令牌
4. 如果桶中有令牌，请求通过，令牌减少
5. 如果桶中没有令牌，请求被拒绝

### 3.2 算法实现

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

**使用示例：**
```java
public class TokenBucketExample {
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

### 3.3 算法优缺点

**优点：**
- 可以处理突发流量
- 限流平滑
- 灵活性高，可以调整令牌生成速率

**缺点：**
- 实现相对复杂
- 需要维护令牌桶
- 突发流量可能导致后续请求被限流

### 3.4 适用场景

**适合场景：**
- 需要处理突发流量的场景
- 对限流平滑性要求高的场景
- 需要灵活调整限流策略的场景

**不适合场景：**
- 对限流精度要求极高的场景
- 内存受限的场景
- 简单的限流需求

## 四、漏桶算法

### 4.1 算法原理

**基本思想：**
漏桶算法将请求放入漏桶，漏桶以固定速率处理请求，如果漏桶满了则拒绝请求。

**工作原理：**
```
请求 → 漏桶 → 以固定速率处理请求
       ↑
       桶满则拒绝
```

**算法步骤：**
1. 请求到来时放入漏桶
2. 漏桶以固定速率处理请求
3. 如果漏桶满了，拒绝请求
4. 漏桶有最大容量

### 4.2 算法实现

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

**使用示例：**
```java
public class LeakyBucketExample {
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

### 4.3 算法优缺点

**优点：**
- 可以平滑流量
- 限流精确
- 实现相对简单

**缺点：**
- 不能处理突发流量
- 需要维护漏桶
- 可能导致请求延迟

### 4.4 适用场景

**适合场景：**
- 需要平滑流量的场景
- 对限流精度要求高的场景
- 保护下游系统的场景

**不适合场景：**
- 需要处理突发流量的场景
- 对响应时间要求高的场景
- 简单的限流需求

## 五、算法对比

### 5.1 性能对比

| 算法 | 时间复杂度 | 空间复杂度 | 性能 |
|------|-----------|-----------|------|
| 固定窗口 | O(1) | O(1) | 高 |
| 滑动窗口 | O(1) | O(n) | 中 |
| 令牌桶 | O(1) | O(1) | 高 |
| 漏桶 | O(1) | O(1) | 高 |

### 5.2 精度对比

| 算法 | 限流精度 | 临界问题 | 突发流量 |
|------|---------|---------|---------|
| 固定窗口 | 低 | 存在 | 不支持 |
| 滑动窗口 | 高 | 不存在 | 不支持 |
| 令牌桶 | 中 | 不存在 | 支持 |
| 漏桶 | 高 | 不存在 | 不支持 |

### 5.3 适用场景对比

| 算法 | 适用场景 | 不适用场景 |
|------|---------|-----------|
| 固定窗口 | 简单限流、请求均匀 | 精确限流、突发流量 |
| 滑动窗口 | 精确限流、请求不均匀 | 简单限流、内存受限 |
| 令牌桶 | 突发流量、灵活限流 | 精确限流、内存受限 |
| 漏桶 | 平滑流量、保护下游 | 突发流量、响应时间要求高 |

## 六、算法选择建议

### 6.1 根据业务场景选择

**简单限流：**
- 选择固定窗口算法
- 优点：实现简单，性能高
- 缺点：存在临界问题

**精确限流：**
- 选择滑动窗口算法
- 优点：限流精确，没有临界问题
- 缺点：实现相对复杂

**突发流量：**
- 选择令牌桶算法
- 优点：可以处理突发流量
- 缺点：实现相对复杂

**平滑流量：**
- 选择漏桶算法
- 优点：可以平滑流量
- 缺点：不能处理突发流量

### 6.2 根据性能要求选择

**高性能要求：**
- 选择固定窗口算法或令牌桶算法
- 优点：时间复杂度O(1)，性能高
- 缺点：固定窗口存在临界问题

**高精度要求：**
- 选择滑动窗口算法或漏桶算法
- 优点：限流精确
- 缺点：滑动窗口空间复杂度O(n)

### 6.3 根据资源限制选择

**内存受限：**
- 选择固定窗口算法或令牌桶算法
- 优点：空间复杂度O(1)
- 缺点：固定窗口存在临界问题

**CPU受限：**
- 选择固定窗口算法或令牌桶算法
- 优点：时间复杂度O(1)
- 缺点：固定窗口存在临界问题

## 七、总结

三种限流算法各有优缺点，需要根据实际场景选择合适的算法：

**固定窗口算法：**
- 优点：实现简单，性能高
- 缺点：存在临界问题，限流不够精确
- 适用场景：简单限流、请求均匀

**滑动窗口算法：**
- 优点：限流精确，没有临界问题
- 缺点：实现相对复杂，需要存储请求时间
- 适用场景：精确限流、请求不均匀

**令牌桶算法：**
- 优点：可以处理突发流量，限流平滑
- 缺点：实现相对复杂，需要维护令牌桶
- 适用场景：突发流量、灵活限流

**漏桶算法：**
- 优点：可以平滑流量，限流精确
- 缺点：不能处理突发流量，可能导致请求延迟
- 适用场景：平滑流量、保护下游

**选择建议：**
1. 简单限流：选择固定窗口算法
2. 精确限流：选择滑动窗口算法
3. 突发流量：选择令牌桶算法
4. 平滑流量：选择漏桶算法