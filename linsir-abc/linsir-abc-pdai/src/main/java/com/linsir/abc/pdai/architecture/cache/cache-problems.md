# 缓存会有哪些问题？如何解决？

## 一、缓存穿透

### 1.1 什么是缓存穿透？

**定义：**
缓存穿透是指查询一个一定不存在的数据，由于缓存是不命中时被动写的，并且出于容错考虑，如果从存储层查不到数据则不写入缓存，这将导致这个不存在的数据每次请求都要到存储层去查询，失去了缓存的意义。

**场景示例：**
```
客户端请求 → 缓存（未命中）→ 数据库（不存在）→ 返回null
客户端请求 → 缓存（未命中）→ 数据库（不存在）→ 返回null
客户端请求 → 缓存（未命中）→ 数据库（不存在）→ 返回null
...
```

### 1.2 缓存穿透的危害

**性能影响：**
- 大量请求直接打到数据库
- 数据库负载急剧增加
- 系统响应时间变长
- 可能导致数据库宕机

**安全风险：**
- 恶意攻击者利用不存在的key进行攻击
- 可能导致系统瘫痪
- 影响正常用户访问

### 1.3 解决方案

#### 方案一：缓存空对象

**实现原理：**
当查询不存在的数据时，将null值或空对象缓存起来，设置较短的过期时间。

**代码实现：**
```java
public class CachePenetrationSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    
    public User getUser(Long userId) {
        String key = "user:" + userId;
        User user = (User) redisTemplate.opsForValue().get(key);
        
        if (user != null) {
            if (user.getId() == -1L) {
                return null;
            }
            return user;
        }
        
        user = userRepository.findById(userId).orElse(null);
        
        if (user == null) {
            User emptyUser = new User();
            emptyUser.setId(-1L);
            redisTemplate.opsForValue().set(key, emptyUser, 5, TimeUnit.MINUTES);
        } else {
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
        }
        
        return user;
    }
}
```

**优点：**
- 实现简单
- 可以防止恶意攻击
- 减少数据库压力

**缺点：**
- 缓存了无用的数据
- 占用缓存空间
- 数据不一致时需要主动更新

#### 方案二：布隆过滤器

**实现原理：**
使用布隆过滤器判断key是否存在，如果布隆过滤器判断不存在，则直接返回，不查询数据库。

**代码实现：**
```java
public class BloomFilterSolution {
    private BloomFilter<Long> bloomFilter;
    @Autowired
    private UserRepository userRepository;
    
    @PostConstruct
    public void init() {
        bloomFilter = BloomFilter.create(
            Funnels.longFunnel(),
            1000000,
            0.01
        );
        
        List<User> users = userRepository.findAll();
        users.forEach(user -> bloomFilter.put(user.getId()));
    }
    
    public User getUser(Long userId) {
        if (!bloomFilter.mightContain(userId)) {
            return null;
        }
        
        return userRepository.findById(userId).orElse(null);
    }
}
```

**使用Redisson布隆过滤器：**
```java
public class RedissonBloomFilterSolution {
    @Autowired
    private RedissonClient redissonClient;
    
    @PostConstruct
    public void init() {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("userBloomFilter");
        bloomFilter.tryInit(1000000L, 0.01);
        
        List<User> users = userRepository.findAll();
        users.forEach(user -> bloomFilter.add(user.getId()));
    }
    
    public User getUser(Long userId) {
        RBloomFilter<Long> bloomFilter = redissonClient.getBloomFilter("userBloomFilter");
        if (!bloomFilter.contains(userId)) {
            return null;
        }
        
        return userRepository.findById(userId).orElse(null);
    }
}
```

**优点：**
- 内存占用小
- 查询速度快
- 可以有效防止缓存穿透

**缺点：**
- 存在误判率
- 不支持删除操作
- 需要提前初始化

#### 方案三：接口限流

**实现原理：**
对接口进行限流，防止恶意攻击。

**代码实现：**
```java
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/{userId}")
    @RateLimiter(value = 100, timeout = 1, timeUnit = TimeUnit.SECONDS)
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
}
```

## 二、缓存击穿

### 2.1 什么是缓存击穿？

**定义：**
缓存击穿是指某个热点key在缓存过期的瞬间，大量请求同时查询这个key，导致所有请求都直接打到数据库。

**场景示例：**
```
时间点T0：缓存过期
时间点T0：大量请求同时到达
请求1 → 缓存（未命中）→ 数据库查询 → 更新缓存
请求2 → 缓存（未命中）→ 数据库查询 → 更新缓存
请求3 → 缓存（未命中）→ 数据库查询 → 更新缓存
...
```

### 2.2 缓存击穿的危害

**性能影响：**
- 数据库瞬间承受大量请求
- 数据库负载急剧增加
- 可能导致数据库宕机

### 2.3 解决方案

#### 方案一：互斥锁

**实现原理：**
当缓存失效时，只允许一个线程查询数据库，其他线程等待。

**代码实现：**
```java
public class CacheBreakdownSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedissonClient redissonClient;
    
    public User getUser(Long userId) {
        String key = "user:" + userId;
        User user = (User) redisTemplate.opsForValue().get(key);
        
        if (user != null) {
            return user;
        }
        
        String lockKey = "lock:user:" + userId;
        RLock lock = redissonClient.getLock(lockKey);
        
        try {
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                user = (User) redisTemplate.opsForValue().get(key);
                if (user != null) {
                    return user;
                }
                
                user = userRepository.findById(userId).orElse(null);
                if (user != null) {
                    redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
                }
            } else {
                Thread.sleep(100);
                return getUser(userId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        
        return user;
    }
}
```

**优点：**
- 可以有效防止缓存击穿
- 保证数据一致性

**缺点：**
- 增加了系统复杂度
- 可能导致线程阻塞

#### 方案二：逻辑过期

**实现原理：**
在缓存中设置逻辑过期时间，而不是使用Redis的过期时间。当查询缓存时，检查逻辑过期时间，如果过期则异步更新缓存。

**代码实现：**
```java
public class LogicalExpireSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    public User getUser(Long userId) {
        String key = "user:" + userId;
        User user = (User) redisTemplate.opsForValue().get(key);
        
        if (user == null) {
            return null;
        }
        
        if (user.getExpireTime() < System.currentTimeMillis()) {
            executorService.submit(() -> {
                User newUser = userRepository.findById(userId).orElse(null);
                if (newUser != null) {
                    newUser.setExpireTime(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                    redisTemplate.opsForValue().set(key, newUser);
                }
            });
        }
        
        return user;
    }
}
```

**优点：**
- 可以有效防止缓存击穿
- 不会导致线程阻塞

**缺点：**
- 实现复杂
- 可能返回过期数据

#### 方案三：缓存预热

**实现原理：**
在缓存过期前，主动更新缓存，避免缓存失效。

**代码实现：**
```java
@Component
public class CacheWarmup {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    
    @Scheduled(cron = "0 */50 * * * ?")
    public void warmup() {
        List<User> hotUsers = userRepository.findHotUsers();
        hotUsers.forEach(user -> {
            String key = "user:" + user.getId();
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
        });
    }
}
```

**优点：**
- 可以有效防止缓存击穿
- 实现简单

**缺点：**
- 需要提前知道热点数据
- 可能浪费资源

## 三、缓存雪崩

### 3.1 什么是缓存雪崩？

**定义：**
缓存雪崩是指在某一个时间段，缓存集中失效，或者缓存服务器宕机，导致所有请求都打到数据库。

**场景示例：**
```
时间点T0：大量缓存同时过期
时间点T0：大量请求同时到达
请求1 → 缓存（未命中）→ 数据库查询
请求2 → 缓存（未命中）→ 数据库查询
请求3 → 缓存（未命中）→ 数据库查询
...
```

### 3.2 缓存雪崩的危害

**性能影响：**
- 数据库瞬间承受大量请求
- 数据库负载急剧增加
- 可能导致数据库宕机

### 3.3 解决方案

#### 方案一：随机过期时间

**实现原理：**
在设置缓存过期时间时，加上一个随机值，避免缓存同时失效。

**代码实现：**
```java
public class RandomExpireSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    private final Random random = new Random();
    
    public User getUser(Long userId) {
        String key = "user:" + userId;
        User user = (User) redisTemplate.opsForValue().get(key);
        
        if (user != null) {
            return user;
        }
        
        user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            long expireTime = 60 + random.nextInt(30);
            redisTemplate.opsForValue().set(key, user, expireTime, TimeUnit.MINUTES);
        }
        
        return user;
    }
}
```

**优点：**
- 实现简单
- 可以有效防止缓存雪崩

**缺点：**
- 缓存时间不一致
- 可能影响缓存命中率

#### 方案二：缓存高可用

**实现原理：**
使用Redis集群或哨兵模式，保证缓存的高可用。

**Redis哨兵配置：**
```yaml
spring:
  redis:
    sentinel:
      master: mymaster
      nodes:
        - sentinel1:26379
        - sentinel2:26379
        - sentinel3:26379
```

**Redis集群配置：**
```yaml
spring:
  redis:
    cluster:
      nodes:
        - redis1:6379
        - redis2:6379
        - redis3:6379
        - redis4:6379
        - redis5:6379
        - redis6:6379
      max-redirects: 3
```

**优点：**
- 保证缓存高可用
- 提高缓存性能

**缺点：**
- 增加系统复杂度
- 增加硬件成本

#### 方案三：限流降级

**实现原理：**
当缓存失效时，对请求进行限流，保护数据库。

**代码实现：**
```java
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/user/{userId}")
    @RateLimiter(value = 100, timeout = 1, timeUnit = TimeUnit.SECONDS)
    @HystrixCommand(fallbackMethod = "getUserFallback")
    public User getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }
    
    public User getUserFallback(Long userId) {
        return new User();
    }
}
```

**优点：**
- 可以有效保护数据库
- 保证系统可用性

**缺点：**
- 可能影响用户体验
- 需要实现降级逻辑

## 四、缓存一致性

### 4.1 什么是缓存一致性？

**定义：**
缓存一致性是指缓存中的数据与数据库中的数据保持一致。

**问题场景：**
```
线程1：更新数据库 → 删除缓存
线程2：查询缓存（未命中）→ 查询数据库 → 更新缓存
线程1：更新数据库（旧数据）
结果：缓存中是旧数据，数据库中是新数据
```

### 4.2 解决方案

#### 方案一：先更新数据库，再删除缓存

**实现原理：**
先更新数据库，再删除缓存，保证最终一致性。

**代码实现：**
```java
public class UpdateThenDeleteSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public void updateUser(User user) {
        userRepository.save(user);
        String key = "user:" + user.getId();
        redisTemplate.delete(key);
    }
}
```

**优点：**
- 实现简单
- 保证最终一致性

**缺点：**
- 可能出现短暂的不一致
- 删除缓存失败时需要重试

#### 方案二：延迟双删

**实现原理：**
先删除缓存，再更新数据库，延迟一段时间后再删除缓存。

**代码实现：**
```java
public class DelayDoubleDeleteSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    @Transactional
    public void updateUser(User user) {
        String key = "user:" + user.getId();
        redisTemplate.delete(key);
        
        userRepository.save(user);
        
        executorService.submit(() -> {
            try {
                Thread.sleep(500);
                redisTemplate.delete(key);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
```

**优点：**
- 可以有效保证一致性
- 适合高并发场景

**缺点：**
- 实现复杂
- 增加了延迟

#### 方案三：订阅数据库变更

**实现原理：**
订阅数据库的binlog，当数据库变更时，自动更新或删除缓存。

**使用Canal实现：**
```java
@Component
public class CanalClient {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @PostConstruct
    public void init() {
        CanalConnector connector = CanalConnectors.newSingleConnector(
            new InetSocketAddress("127.0.0.1", 11111),
            "example",
            "",
            ""
        );
        
        connector.connect();
        connector.subscribe(".*\\..*");
        connector.rollback();
        
        while (true) {
            Message message = connector.getWithoutAck(100);
            long batchId = message.getId();
            int size = message.getEntries().size();
            
            if (batchId != -1 && size > 0) {
                message.getEntries().forEach(entry -> {
                    if (entry.getEntryType() == EntryType.ROWDATA) {
                        RowChange rowChange = null;
                        try {
                            rowChange = RowChange.parseFrom(entry.getStoreValue());
                        } catch (InvalidProtocolBufferException e) {
                            e.printStackTrace();
                        }
                        
                        if (rowChange != null) {
                            EventType eventType = rowChange.getEventType();
                            if (eventType == EventType.UPDATE || eventType == EventType.DELETE) {
                                rowChange.getRowDatasList().forEach(rowData -> {
                                    String key = "user:" + rowData.getBeforeColumnsList().get(0).getValue();
                                    redisTemplate.delete(key);
                                });
                            }
                        }
                    }
                });
            }
            
            connector.ack(batchId);
        }
    }
}
```

**优点：**
- 可以实时保证一致性
- 不需要修改业务代码

**缺点：**
- 实现复杂
- 需要额外的中间件

## 五、缓存热点

### 5.1 什么是缓存热点？

**定义：**
缓存热点是指某些key被频繁访问，导致这些key所在的缓存节点负载过高。

**问题场景：**
```
大量请求 → 热点key → 单个缓存节点
结果：单个节点负载过高，可能宕机
```

### 5.2 解决方案

#### 方案一：热点数据多副本

**实现原理：**
将热点数据复制多份，分散到不同的缓存节点。

**代码实现：**
```java
public class HotDataReplicationSolution {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    
    public User getUser(Long userId) {
        String[] keys = {
            "user:" + userId + ":1",
            "user:" + userId + ":2",
            "user:" + userId + ":3"
        };
        
        int index = (int) (userId % keys.length);
        User user = (User) redisTemplate.opsForValue().get(keys[index]);
        
        if (user == null) {
            user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                for (String key : keys) {
                    redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
                }
            }
        }
        
        return user;
    }
}
```

**优点：**
- 可以分散负载
- 提高缓存性能

**缺点：**
- 占用更多缓存空间
- 数据一致性复杂

#### 方案二：本地缓存 + 分布式缓存

**实现原理：**
使用本地缓存缓存热点数据，减少对分布式缓存的访问。

**代码实现：**
```java
public class LocalCacheWithDistributedCacheSolution {
    private final Cache<String, User> localCache;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    
    public LocalCacheWithDistributedCacheSolution() {
        localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }
    
    public User getUser(Long userId) {
        String key = "user:" + userId;
        
        User user = localCache.getIfPresent(key);
        if (user != null) {
            return user;
        }
        
        user = (User) redisTemplate.opsForValue().get(key);
        if (user != null) {
            localCache.put(key, user);
            return user;
        }
        
        user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
            localCache.put(key, user);
        }
        
        return user;
    }
}
```

**优点：**
- 减少分布式缓存压力
- 提高访问速度

**缺点：**
- 本地缓存容量有限
- 数据一致性复杂

## 六、总结

缓存使用过程中会遇到各种问题，需要根据实际情况选择合适的解决方案：

**缓存穿透：**
- 缓存空对象：实现简单，适合小规模数据
- 布隆过滤器：内存占用小，适合大规模数据
- 接口限流：防止恶意攻击

**缓存击穿：**
- 互斥锁：保证数据一致性
- 逻辑过期：不阻塞线程
- 缓存预热：提前更新缓存

**缓存雪崩：**
- 随机过期时间：实现简单
- 缓存高可用：保证可用性
- 限流降级：保护数据库

**缓存一致性：**
- 先更新数据库再删除缓存：实现简单
- 延迟双删：适合高并发场景
- 订阅数据库变更：实时保证一致性

**缓存热点：**
- 热点数据多副本：分散负载
- 本地缓存 + 分布式缓存：提高性能

选择解决方案时需要考虑：
- 业务场景
- 数据规模
- 性能要求
- 一致性要求
- 实现复杂度