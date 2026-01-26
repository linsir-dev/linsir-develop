# 在开发中缓存具体如何实现？

## 一、本地缓存实现

### 1.1 使用ConcurrentHashMap实现本地缓存

**基本实现：**
```java
public class LocalCache {
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    
    public void put(String key, Object value) {
        cache.put(key, value);
    }
    
    public Object get(String key) {
        return cache.get(key);
    }
    
    public void remove(String key) {
        cache.remove(key);
    }
    
    public void clear() {
        cache.clear();
    }
}
```

**带过期时间的实现：**
```java
public class ExpiringCache {
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    
    public void put(String key, Object value, long expireTime, TimeUnit unit) {
        CacheEntry entry = new CacheEntry(value, System.currentTimeMillis() + unit.toMillis(expireTime));
        cache.put(key, entry);
        
        executor.schedule(() -> {
            cache.remove(key);
        }, expireTime, unit);
    }
    
    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.getValue();
    }
    
    static class CacheEntry {
        private final Object value;
        private final long expireTime;
        
        public CacheEntry(Object value, long expireTime) {
            this.value = value;
            this.expireTime = expireTime;
        }
        
        public Object getValue() {
            return value;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
}
```

### 1.2 使用Guava Cache实现本地缓存

**基本使用：**
```java
public class GuavaCacheExample {
    private final LoadingCache<String, String> cache;
    
    public GuavaCacheExample() {
        cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build(new CacheLoader<String, String>() {
                @Override
                public String load(String key) {
                    return loadFromDatabase(key);
                }
            });
    }
    
    public String get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String loadFromDatabase(String key) {
        return "value_" + key;
    }
    
    public void printStats() {
        CacheStats stats = cache.stats();
        System.out.println("命中率: " + stats.hitRate());
        System.out.println("加载次数: " + stats.loadCount());
        System.out.println("加载失败次数: " + stats.loadExceptionCount());
    }
}
```

**基于权重的缓存：**
```java
public class WeightedCacheExample {
    private final LoadingCache<String, byte[]> cache;
    
    public WeightedCacheExample() {
        cache = CacheBuilder.newBuilder()
            .maximumWeight(100 * 1024 * 1024) // 100MB
            .weigher((String key, byte[] value) -> value.length)
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, byte[]>() {
                @Override
                public byte[] load(String key) {
                    return loadImage(key);
                }
            });
    }
    
    private byte[] loadImage(String key) {
        return new byte[0];
    }
}
```

### 1.3 使用Caffeine实现本地缓存

**基本使用：**
```java
public class CaffeineCacheExample {
    private final Cache<String, String> cache;
    
    public CaffeineCacheExample() {
        cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build();
    }
    
    public String get(String key) {
        return cache.get(key, k -> loadFromDatabase(k));
    }
    
    private String loadFromDatabase(String key) {
        return "value_" + key;
    }
    
    public void printStats() {
        CacheStats stats = cache.stats();
        System.out.println("命中率: " + stats.hitRate());
        System.out.println("加载次数: " + stats.loadCount());
    }
}
```

**异步刷新：**
```java
public class AsyncRefreshCacheExample {
    private final AsyncCache<String, String> cache;
    
    public AsyncRefreshCacheExample() {
        cache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .buildAsync();
    }
    
    public CompletableFuture<String> get(String key) {
        return cache.get(key, k -> CompletableFuture.supplyAsync(() -> loadFromDatabase(k)));
    }
    
    private String loadFromDatabase(String key) {
        return "value_" + key;
    }
}
```

## 二、Redis缓存实现

### 2.1 Spring Boot集成Redis

**依赖配置：**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

**配置文件：**
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 3000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
        max-wait: -1ms
```

**Redis配置类：**
```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        );
        serializer.setObjectMapper(mapper);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofHours(1))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)))
            .disableCachingNullValues();
        
        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }
}
```

### 2.2 使用RedisTemplate操作Redis

**字符串操作：**
```java
@Service
public class RedisStringService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }
    
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }
    
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }
    
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }
    
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }
    
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }
}
```

**哈希操作：**
```java
@Service
public class RedisHashService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }
    
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }
    
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }
    
    public void hDelete(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }
    
    public Boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }
}
```

**列表操作：**
```java
@Service
public class RedisListService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public Long lPush(String key, Object... values) {
        return redisTemplate.opsForList().leftPush(key, values);
    }
    
    public Long rPush(String key, Object... values) {
        return redisTemplate.opsForList().rightPush(key, values);
    }
    
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }
    
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }
    
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }
    
    public Long lSize(String key) {
        return redisTemplate.opsForList().size(key);
    }
}
```

**集合操作：**
```java
@Service
public class RedisSetService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }
    
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }
    
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }
    
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }
    
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
}
```

### 2.3 使用Spring Cache注解

**启用缓存：**
```java
@SpringBootApplication
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

**缓存注解使用：**
```java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Cacheable(value = "user", key = "#userId")
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    @Cacheable(value = "user", key = "#userId", unless = "#result == null")
    public User getUserWithCondition(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    @CachePut(value = "user", key = "#user.id")
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "user", key = "#userId")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @CacheEvict(value = "user", allEntries = true)
    public void clearAllUsers() {
    }
    
    @Caching(
        cacheable = @Cacheable(value = "user", key = "#userId"),
        evict = @CacheEvict(value = "userList", allEntries = true)
    )
    public User getUserAndClearList(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
```

**自定义缓存注解：**
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(value = "user", key = "#userId")
public @interface UserCacheable {
    String key() default "";
    long expire() default 3600;
}

@Service
public class UserService {
    
    @UserCacheable(key = "#userId", expire = 7200)
    public User getUser(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}
```

## 三、多级缓存实现

### 3.1 本地缓存 + Redis缓存

**多级缓存实现：**
```java
@Service
public class MultiLevelCacheService {
    private final Cache<String, Object> localCache;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserRepository userRepository;
    
    public MultiLevelCacheService() {
        localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }
    
    public User getUser(Long userId) {
        String key = "user:" + userId;
        
        User user = (User) localCache.getIfPresent(key);
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
    
    public void updateUser(User user) {
        userRepository.save(user);
        String key = "user:" + user.getId();
        redisTemplate.delete(key);
        localCache.invalidate(key);
    }
}
```

### 3.2 使用Spring Cache实现多级缓存

**自定义缓存管理器：**
```java
public class MultiLevelCacheManager implements CacheManager {
    private final CacheManager localCacheManager;
    private final CacheManager redisCacheManager;
    
    public MultiLevelCacheManager(CacheManager localCacheManager, CacheManager redisCacheManager) {
        this.localCacheManager = localCacheManager;
        this.redisCacheManager = redisCacheManager;
    }
    
    @Override
    public Cache getCache(String name) {
        Cache localCache = localCacheManager.getCache(name);
        Cache redisCache = redisCacheManager.getCache(name);
        return new MultiLevelCache(name, localCache, redisCache);
    }
    
    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new HashSet<>();
        names.addAll(localCacheManager.getCacheNames());
        names.addAll(redisCacheManager.getCacheNames());
        return names;
    }
}

public class MultiLevelCache implements Cache {
    private final String name;
    private final Cache localCache;
    private final Cache redisCache;
    
    public MultiLevelCache(String name, Cache localCache, Cache redisCache) {
        this.name = name;
        this.localCache = localCache;
        this.redisCache = redisCache;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Object getNativeCache() {
        return this;
    }
    
    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = localCache.get(key);
        if (wrapper != null) {
            return wrapper;
        }
        wrapper = redisCache.get(key);
        if (wrapper != null) {
            localCache.put(key, wrapper.get());
        }
        return wrapper;
    }
    
    @Override
    public <T> T get(Object key, Class<T> type) {
        T value = localCache.get(key, type);
        if (value != null) {
            return value;
        }
        value = redisCache.get(key, type);
        if (value != null) {
            localCache.put(key, value);
        }
        return value;
    }
    
    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T value = localCache.get(key, valueLoader);
        if (value != null) {
            return value;
        }
        value = redisCache.get(key, valueLoader);
        if (value != null) {
            localCache.put(key, value);
        }
        return value;
    }
    
    @Override
    public void put(Object key, Object value) {
        localCache.put(key, value);
        redisCache.put(key, value);
    }
    
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper wrapper = localCache.putIfAbsent(key, value);
        redisCache.putIfAbsent(key, value);
        return wrapper;
    }
    
    @Override
    public void evict(Object key) {
        localCache.evict(key);
        redisCache.evict(key);
    }
    
    @Override
    public void clear() {
        localCache.clear();
        redisCache.clear();
    }
}
```

## 四、缓存序列化实现

### 4.1 JSON序列化

**使用Jackson序列化：**
```java
public class JsonRedisSerializer<T> implements RedisSerializer<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> type;
    
    public JsonRedisSerializer(Class<T> type) {
        this.type = type;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not serialize JSON", e);
        }
    }
    
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, type);
        } catch (IOException e) {
            throw new SerializationException("Could not deserialize JSON", e);
        }
    }
}
```

### 4.2 Protobuf序列化

**使用Protobuf序列化：**
```java
public class ProtobufRedisSerializer<T> implements RedisSerializer<T> {
    private final Parser<T> parser;
    private final T defaultInstance;
    
    public ProtobufRedisSerializer(Parser<T> parser, T defaultInstance) {
        this.parser = parser;
        this.defaultInstance = defaultInstance;
    }
    
    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        return ((Message) t).toByteArray();
    }
    
    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return parser.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            throw new SerializationException("Could not deserialize protobuf", e);
        }
    }
}
```

## 五、缓存监控实现

### 5.1 缓存监控指标

**缓存监控实现：**
```java
@Component
public class CacheMonitor {
    private final MeterRegistry meterRegistry;
    
    public CacheMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }
    
    public void recordCacheHit(String cacheName) {
        meterRegistry.counter("cache.hit", "cache", cacheName).increment();
    }
    
    public void recordCacheMiss(String cacheName) {
        meterRegistry.counter("cache.miss", "cache", cacheName).increment();
    }
    
    public void recordCacheLoad(String cacheName, long duration) {
        meterRegistry.timer("cache.load", "cache", cacheName)
            .record(duration, TimeUnit.MILLISECONDS);
    }
    
    public void recordCacheEviction(String cacheName) {
        meterRegistry.counter("cache.eviction", "cache", cacheName).increment();
    }
    
    public double getCacheHitRate(String cacheName) {
        double hits = meterRegistry.counter("cache.hit", "cache", cacheName).count();
        double misses = meterRegistry.counter("cache.miss", "cache", cacheName).count();
        return hits / (hits + misses);
    }
}
```

### 5.2 缓存健康检查

**缓存健康检查：**
```java
@Component
public class CacheHealthIndicator implements HealthIndicator {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Override
    public Health health() {
        try {
            String ping = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
            if ("PONG".equals(ping)) {
                return Health.up()
                    .withDetail("status", "Redis is available")
                    .build();
            }
            return Health.down()
                .withDetail("status", "Redis ping failed")
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## 六、总结

在开发中实现缓存需要考虑多个方面：

**选择合适的缓存方案：**
- 本地缓存：适合单机应用，访问速度快
- 分布式缓存：适合分布式系统，容量大、可扩展
- 多级缓存：结合本地缓存和分布式缓存的优势

**合理使用缓存框架：**
- Guava Cache：功能强大，适合本地缓存
- Caffeine：性能更好，适合本地缓存
- Spring Cache：简化缓存使用，支持多种缓存实现

**注意缓存序列化：**
- JSON序列化：可读性好，但性能较差
- Protobuf序列化：性能好，但可读性差
- 根据场景选择合适的序列化方式

**做好缓存监控：**
- 监控缓存命中率
- 监控缓存响应时间
- 监控缓存容量使用率
- 做好缓存健康检查