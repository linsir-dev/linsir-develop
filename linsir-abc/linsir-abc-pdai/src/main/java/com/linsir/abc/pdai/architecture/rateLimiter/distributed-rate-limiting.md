# 在分布式环境下如何实现限流？

## 一、分布式限流概述

### 1.1 分布式限流的特点

**定义：**
分布式限流是指在分布式系统中实现的限流机制，限流状态存储在共享存储中，所有实例共享限流状态。

**特点：**
- 限流状态存储在共享存储中
- 所有实例共享限流状态
- 可以实现跨实例限流
- 依赖外部存储
- 实现相对复杂

**适用场景：**
- 分布式系统
- 微服务架构
- 需要跨实例限流的场景
- 需要精确限流的场景

### 1.2 分布式限流的优缺点

**优点：**
- 可以实现跨实例限流
- 限流状态持久化
- 实例重启后限流状态不丢失
- 可以精确限流

**缺点：**
- 实现相对复杂
- 依赖外部存储
- 性能比单机限流低
- 响应速度比单机限流慢

### 1.3 分布式限流的应用场景

**常见场景：**
- 分布式微服务
- 多实例部署的应用
- 需要全局限流的场景
- 需要精确限流的场景

## 二、基于Redis的分布式限流

### 2.1 基于Redis计数器的限流

**实现原理：**
使用Redis的INCR命令实现计数器限流。

**代码实现：**
```java
@Service
public class RedisCounterRateLimiter {
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final int limit;
    private final long interval;
    
    public RedisCounterRateLimiter(int limit, long interval, TimeUnit unit) {
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    public boolean tryAcquire(String key) {
        String redisKey = "rate:limiter:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - interval;
        
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        
        Long count = redisTemplate.opsForZSet().count(redisKey, windowStart, now);
        if (count != null && count < limit) {
            redisTemplate.opsForZSet().add(redisKey, UUID.randomUUID().toString(), now);
            redisTemplate.expire(redisKey, interval, TimeUnit.MILLISECONDS);
            return true;
        }
        
        return false;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getCount(String key) {
        String redisKey = "rate:limiter:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - interval;
        Long count = redisTemplate.opsForZSet().count(redisKey, windowStart, now);
        return count != null ? count : 0;
    }
}
```

**使用示例：**
```java
@RestController
public class UserController {
    @Autowired
    private RedisCounterRateLimiter rateLimiter;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<String> getUser(@PathVariable Long userId) {
        String key = "user:" + userId;
        if (!rateLimiter.tryAcquire(key)) {
            return ResponseEntity.status(429).body("请求过于频繁");
        }
        
        return ResponseEntity.ok("用户信息");
    }
}
```

### 2.2 基于Redis滑动窗口的限流

**实现原理：**
使用Redis的ZSet存储请求时间，实现滑动窗口限流。

**代码实现：**
```java
@Service
public class RedisSlidingWindowRateLimiter {
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final int limit;
    private final long windowSize;
    
    public RedisSlidingWindowRateLimiter(int limit, long windowSize, TimeUnit unit) {
        this.limit = limit;
        this.windowSize = unit.toMillis(windowSize);
    }
    
    public boolean tryAcquire(String key) {
        String redisKey = "rate:limiter:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize;
        
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        
        Long count = redisTemplate.opsForZSet().count(redisKey, windowStart, now);
        if (count != null && count < limit) {
            redisTemplate.opsForZSet().add(redisKey, UUID.randomUUID().toString(), now);
            redisTemplate.expire(redisKey, windowSize, TimeUnit.MILLISECONDS);
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
    
    public long getCount(String key) {
        String redisKey = "rate:limiter:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - windowSize;
        Long count = redisTemplate.opsForZSet().count(redisKey, windowStart, now);
        return count != null ? count : 0;
    }
}
```

### 2.3 基于Redis令牌桶的限流

**实现原理：**
使用Redis存储令牌桶状态，实现令牌桶限流。

**代码实现：**
```java
@Service
public class RedisTokenBucketRateLimiter {
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final int capacity;
    private final int rate;
    
    public RedisTokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(String key) {
        String redisKey = "rate:limiter:token:" + key;
        String nowKey = "rate:limiter:token:now:" + key;
        
        Long now = redisTemplate.opsForValue().increment(nowKey);
        if (now == 1) {
            redisTemplate.expire(nowKey, 1, TimeUnit.HOURS);
        }
        
        String script = 
            "local key = KEYS[1] " +
            "local capacity = tonumber(ARGV[1]) " +
            "local rate = tonumber(ARGV[2]) " +
            "local now = tonumber(ARGV[3]) " +
            "local lastRefillTime = tonumber(redis.call('HGET', key, 'lastRefillTime') or 0) " +
            "local tokens = tonumber(redis.call('HGET', key, 'tokens') or capacity) " +
            "local elapsed = now - lastRefillTime " +
            "local newTokens = math.floor(elapsed * rate / 1000) " +
            "tokens = math.min(capacity, tokens + newTokens) " +
            "redis.call('HMSET', key, 'tokens', tokens, 'lastRefillTime', now) " +
            "if tokens > 0 then " +
            "  redis.call('HINCRBY', key, 'tokens', -1) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, 
            Collections.singletonList(redisKey), 
            String.valueOf(capacity), 
            String.valueOf(rate), 
            String.valueOf(now));
        
        return result != null && result == 1;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getTokens(String key) {
        String redisKey = "rate:limiter:token:" + key;
        String tokens = redisTemplate.opsForHash().get(redisKey, "tokens");
        return tokens != null ? Integer.parseInt(tokens) : capacity;
    }
}
```

### 2.4 基于Redis漏桶的限流

**实现原理：**
使用Redis存储漏桶状态，实现漏桶限流。

**代码实现：**
```java
@Service
public class RedisLeakyBucketRateLimiter {
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    private final int capacity;
    private final int rate;
    
    public RedisLeakyBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(String key) {
        String redisKey = "rate:limiter:leaky:" + key;
        String nowKey = "rate:limiter:leaky:now:" + key;
        
        Long now = redisTemplate.opsForValue().increment(nowKey);
        if (now == 1) {
            redisTemplate.expire(nowKey, 1, TimeUnit.HOURS);
        }
        
        String script = 
            "local key = KEYS[1] " +
            "local capacity = tonumber(ARGV[1]) " +
            "local rate = tonumber(ARGV[2]) " +
            "local now = tonumber(ARGV[3]) " +
            "local lastLeakTime = tonumber(redis.call('HGET', key, 'lastLeakTime') or 0) " +
            "local water = tonumber(redis.call('HGET', key, 'water') or 0) " +
            "local elapsed = now - lastLeakTime " +
            "local leaked = math.floor(elapsed * rate / 1000) " +
            "water = math.max(0, water - leaked) " +
            "redis.call('HMSET', key, 'water', water, 'lastLeakTime', now) " +
            "if water < capacity then " +
            "  redis.call('HINCRBY', key, 'water', 1) " +
            "  return 1 " +
            "else " +
            "  return 0 " +
            "end";
        
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, 
            Collections.singletonList(redisKey), 
            String.valueOf(capacity), 
            String.valueOf(rate), 
            String.valueOf(now));
        
        return result != null && result == 1;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getWater(String key) {
        String redisKey = "rate:limiter:leaky:" + key;
        String water = redisTemplate.opsForHash().get(redisKey, "water");
        return water != null ? Integer.parseInt(water) : 0;
    }
}
```

## 三、基于Redisson的分布式限流

### 3.1 使用Redisson实现令牌桶限流

**代码实现：**
```java
@Service
public class RedissonTokenBucketRateLimiter {
    @Autowired
    private RedissonClient redissonClient;
    
    private final int capacity;
    private final int rate;
    
    public RedissonTokenBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, RateIntervalUnit.SECONDS);
        return rateLimiter.tryAcquire(1);
    }
    
    public boolean tryAcquire(String key, long timeout, TimeUnit unit) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, RateIntervalUnit.SECONDS);
        return rateLimiter.tryAcquire(1, timeout, unit);
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
}
```

**使用示例：**
```java
@RestController
public class UserController {
    @Autowired
    private RedissonTokenBucketRateLimiter rateLimiter;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<String> getUser(@PathVariable Long userId) {
        String key = "user:" + userId;
        if (!rateLimiter.tryAcquire(key)) {
            return ResponseEntity.status(429).body("请求过于频繁");
        }
        
        return ResponseEntity.ok("用户信息");
    }
}
```

### 3.2 使用Redisson实现漏桶限流

**代码实现：**
```java
@Service
public class RedissonLeakyBucketRateLimiter {
    @Autowired
    private RedissonClient redissonClient;
    
    private final int capacity;
    private final int rate;
    
    public RedissonLeakyBucketRateLimiter(int capacity, int rate) {
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, RateIntervalUnit.SECONDS);
        return rateLimiter.tryAcquire(1);
    }
    
    public boolean tryAcquire(String key, long timeout, TimeUnit unit) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, RateIntervalUnit.SECONDS);
        return rateLimiter.tryAcquire(1, timeout, unit);
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
}
```

## 四、基于Zookeeper的分布式限流

### 4.1 基于Zookeeper计数器的限流

**实现原理：**
使用Zookeeper的临时节点实现计数器限流。

**代码实现：**
```java
@Service
public class ZookeeperCounterRateLimiter {
    private final CuratorFramework client;
    private final int limit;
    private final long interval;
    
    public ZookeeperCounterRateLimiter(CuratorFramework client, int limit, long interval, TimeUnit unit) {
        this.client = client;
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    public boolean tryAcquire(String key) {
        String path = "/rate/limiter/" + key;
        
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded().forPath(path);
            }
            
            long now = System.currentTimeMillis();
            long windowStart = now - interval;
            
            List<String> children = client.getChildren().forPath(path);
            long count = children.stream()
                .filter(child -> {
                    try {
                        long timestamp = Long.parseLong(child);
                        return timestamp > windowStart;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .count();
            
            if (count < limit) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(path + "/" + now);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getCount(String key) {
        String path = "/rate/limiter/" + key;
        
        try {
            if (client.checkExists().forPath(path) == null) {
                return 0;
            }
            
            long now = System.currentTimeMillis();
            long windowStart = now - interval;
            
            List<String> children = client.getChildren().forPath(path);
            return children.stream()
                .filter(child -> {
                    try {
                        long timestamp = Long.parseLong(child);
                        return timestamp > windowStart;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })
                .count();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

### 4.2 基于Zookeeper令牌桶的限流

**实现原理：**
使用Zookeeper存储令牌桶状态，实现令牌桶限流。

**代码实现：**
```java
@Service
public class ZookeeperTokenBucketRateLimiter {
    private final CuratorFramework client;
    private final int capacity;
    private final int rate;
    
    public ZookeeperTokenBucketRateLimiter(CuratorFramework client, int capacity, int rate) {
        this.client = client;
        this.capacity = capacity;
        this.rate = rate;
    }
    
    public boolean tryAcquire(String key) {
        String path = "/rate/limiter/token/" + key;
        
        try {
            if (client.checkExists().forPath(path) == null) {
                client.create().creatingParentsIfNeeded().forPath(path);
            }
            
            long now = System.currentTimeMillis();
            
            byte[] data = client.getData().forPath(path);
            TokenBucketState state = data != null ? 
                new ObjectMapper().readValue(data, TokenBucketState.class) : 
                new TokenBucketState(capacity, now);
            
            long elapsed = now - state.getLastRefillTime();
            int newTokens = (int) (elapsed * rate / 1000);
            state.setTokens(Math.min(capacity, state.getTokens() + newTokens));
            state.setLastRefillTime(now);
            
            if (state.getTokens() > 0) {
                state.setTokens(state.getTokens() - 1);
                client.setData().forPath(path, new ObjectMapper().writeValueAsBytes(state));
                return true;
            }
            
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public int getRate() {
        return rate;
    }
    
    public int getTokens(String key) {
        String path = "/rate/limiter/token/" + key;
        
        try {
            if (client.checkExists().forPath(path) == null) {
                return capacity;
            }
            
            byte[] data = client.getData().forPath(path);
            TokenBucketState state = data != null ? 
                new ObjectMapper().readValue(data, TokenBucketState.class) : 
                new TokenBucketState(capacity, System.currentTimeMillis());
            
            return state.getTokens();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    static class TokenBucketState {
        private int tokens;
        private long lastRefillTime;
        
        public TokenBucketState() {}
        
        public TokenBucketState(int tokens, long lastRefillTime) {
            this.tokens = tokens;
            this.lastRefillTime = lastRefillTime;
        }
        
        public int getTokens() {
            return tokens;
        }
        
        public void setTokens(int tokens) {
            this.tokens = tokens;
        }
        
        public long getLastRefillTime() {
            return lastRefillTime;
        }
        
        public void setLastRefillTime(long lastRefillTime) {
            this.lastRefillTime = lastRefillTime;
        }
    }
}
```

## 五、基于数据库的分布式限流

### 5.1 基于MySQL计数器的限流

**实现原理：**
使用MySQL表存储计数器状态，实现计数器限流。

**数据库表：**
```sql
CREATE TABLE rate_limiter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    key VARCHAR(255) NOT NULL,
    count INT NOT NULL DEFAULT 0,
    window_start BIGINT NOT NULL,
    window_end BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_key_window (key, window_start, window_end)
);
```

**代码实现：**
```java
@Service
public class MysqlCounterRateLimiter {
    @Autowired
    private RateLimiterRepository rateLimiterRepository;
    
    private final int limit;
    private final long interval;
    
    public MysqlCounterRateLimiter(int limit, long interval, TimeUnit unit) {
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    @Transactional
    public boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        long windowStart = now - (now % interval);
        long windowEnd = windowStart + interval;
        
        RateLimiter rateLimiter = rateLimiterRepository.findByKeyAndWindowStartAndWindowEnd(
            key, windowStart, windowEnd);
        
        if (rateLimiter == null) {
            rateLimiter = new RateLimiter();
            rateLimiter.setKey(key);
            rateLimiter.setCount(1);
            rateLimiter.setWindowStart(windowStart);
            rateLimiter.setWindowEnd(windowEnd);
            rateLimiterRepository.save(rateLimiter);
            return true;
        }
        
        if (rateLimiter.getCount() < limit) {
            rateLimiter.setCount(rateLimiter.getCount() + 1);
            rateLimiterRepository.save(rateLimiter);
            return true;
        }
        
        return false;
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getInterval() {
        return interval;
    }
    
    public long getCount(String key) {
        long now = System.currentTimeMillis();
        long windowStart = now - (now % interval);
        long windowEnd = windowStart + interval;
        
        RateLimiter rateLimiter = rateLimiterRepository.findByKeyAndWindowStartAndWindowEnd(
            key, windowStart, windowEnd);
        
        return rateLimiter != null ? rateLimiter.getCount() : 0;
    }
}
```

### 5.2 基于Redis + MySQL的混合限流

**实现原理：**
使用Redis作为限流存储，MySQL作为持久化存储，实现混合限流。

**代码实现：**
```java
@Service
public class HybridRateLimiter {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RateLimiterRepository rateLimiterRepository;
    
    private final int limit;
    private final long interval;
    
    public HybridRateLimiter(int limit, long interval, TimeUnit unit) {
        this.limit = limit;
        this.interval = unit.toMillis(interval);
    }
    
    public boolean tryAcquire(String key) {
        String redisKey = "rate:limiter:" + key;
        long now = System.currentTimeMillis();
        long windowStart = now - interval;
        
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, windowStart);
        
        Long count = redisTemplate.opsForZSet().count(redisKey, windowStart, now);
        if (count != null && count < limit) {
            redisTemplate.opsForZSet().add(redisKey, UUID.randomUUID().toString(), now);
            redisTemplate.expire(redisKey, interval, TimeUnit.MILLISECONDS);
            
            asyncPersist(key, now);
            return true;
        }
        
        return false;
    }
    
    @Async
    public void asyncPersist(String key, long timestamp) {
        try {
            long windowStart = timestamp - (timestamp % interval);
            long windowEnd = windowStart + interval;
            
            RateLimiter rateLimiter = rateLimiterRepository.findByKeyAndWindowStartAndWindowEnd(
                key, windowStart, windowEnd);
            
            if (rateLimiter == null) {
                rateLimiter = new RateLimiter();
                rateLimiter.setKey(key);
                rateLimiter.setCount(1);
                rateLimiter.setWindowStart(windowStart);
                rateLimiter.setWindowEnd(windowEnd);
            } else {
                rateLimiter.setCount(rateLimiter.getCount() + 1);
            }
            
            rateLimiterRepository.save(rateLimiter);
        } catch (Exception e) {
            log.error("持久化限流数据失败", e);
        }
    }
    
    public int getLimit() {
        return limit;
    }
    
    public long getInterval() {
        return interval;
    }
}
```

## 六、分布式限流的最佳实践

### 6.1 选择合适的存储方案

**存储方案选择：**
- 高性能要求：选择Redis
- 高可用要求：选择Redis集群或Zookeeper
- 持久化要求：选择MySQL
- 混合方案：Redis + MySQL

### 6.2 合理设置限流参数

**参数设置：**
- 根据系统性能设置限流阈值
- 根据业务需求设置时间窗口
- 根据用户等级设置不同限流
- 定期评估和调整参数

### 6.3 做好限流监控

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

### 6.4 做好限流降级

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

### 6.5 做好限流日志

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

## 七、分布式限流 vs 单机限流

### 7.1 对比分析

| 特性 | 单机限流 | 分布式限流 |
|------|---------|-----------|
| 限流范围 | 单个实例 | 所有实例 |
| 存储位置 | 本地内存 | 共享存储 |
| 实现复杂度 | 简单 | 复杂 |
| 性能 | 高 | 中等 |
| 响应速度 | 快 | 中等 |
| 限流精度 | 中等 | 高 |
| 持久化 | 不支持 | 支持 |
| 跨实例限流 | 不支持 | 支持 |

### 7.2 选择建议

**选择单机限流：**
- 单机应用
- 对限流精度要求不高
- 性能要求高
- 不需要跨实例限流

**选择分布式限流：**
- 分布式系统
- 微服务架构
- 需要跨实例限流
- 需要精确限流

## 八、总结

分布式限流是在分布式系统中实现的限流机制，可以实现跨实例限流。

**关键要点：**
1. 分布式限流适合分布式系统
2. 分布式限流依赖外部存储
3. 分布式限流可以实现跨实例限流
4. 分布式限流限流状态持久化

**实现方式：**
1. 基于Redis的分布式限流
2. 基于Redisson的分布式限流
3. 基于Zookeeper的分布式限流
4. 基于数据库的分布式限流
5. 基于Redis + MySQL的混合限流

**最佳实践：**
1. 选择合适的存储方案
2. 合理设置限流参数
3. 做好限流监控
4. 做好限流降级
5. 做好限流日志

**注意事项：**
1. 分布式限流实现相对复杂
2. 分布式限流性能比单机限流低
3. 分布式限流依赖外部存储
4. 需要定期评估和调整限流策略