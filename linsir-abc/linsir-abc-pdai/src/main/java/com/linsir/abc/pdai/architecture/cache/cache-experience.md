# 使用缓存的经验？

## 一、缓存设计原则

### 1.1 缓存粒度原则

**原则说明：**
- 粗粒度缓存：缓存整个对象或对象集合
- 细粒度缓存：缓存对象的某个字段或部分数据

**选择标准：**
- 读多写少：选择粗粒度缓存
- 读写均衡：选择中等粒度缓存
- 写多读少：选择细粒度缓存或不缓存

**实践经验：**
```java
// 粗粒度缓存 - 适合读多写少的场景
@Cacheable(value = "product", key = "#productId")
public Product getProduct(Long productId) {
    return productRepository.findById(productId).orElse(null);
}

// 细粒度缓存 - 适合写多读少的场景
public String getProductName(Long productId) {
    String key = "product:name:" + productId;
    String name = redisTemplate.opsForValue().get(key);
    if (name == null) {
        name = productRepository.findNameById(productId);
        redisTemplate.opsForValue().set(key, name, 1, TimeUnit.HOURS);
    }
    return name;
}

public BigDecimal getProductPrice(Long productId) {
    String key = "product:price:" + productId;
    String price = redisTemplate.opsForValue().get(key);
    if (price == null) {
        price = productRepository.findPriceById(productId).toString();
        redisTemplate.opsForValue().set(key, price, 1, TimeUnit.HOURS);
    }
    return new BigDecimal(price);
}
```

### 1.2 缓存过期策略

**过期策略选择：**
- 固定过期时间：适合数据更新频率固定的场景
- 随机过期时间：适合防止缓存雪崩的场景
- 滑动过期时间：适合热点数据的场景
- 绝对过期时间：适合数据有明确时效性的场景

**实践经验：**
```java
// 固定过期时间
redisTemplate.opsForValue().set(key, value, 30, TimeUnit.MINUTES);

// 随机过期时间 - 防止缓存雪崩
long expireTime = 30 + random.nextInt(10);
redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MINUTES);

// 滑动过期时间 - 适合热点数据
Cache<String, String> cache = Caffeine.newBuilder()
    .expireAfterAccess(10, TimeUnit.MINUTES)
    .build();

// 绝对过期时间 - 适合有明确时效性的数据
redisTemplate.opsForValue().set(key, value, Duration.between(LocalDateTime.now(), expireTime));
```

### 1.3 缓存容量规划

**容量规划原则：**
- 根据业务需求规划缓存容量
- 考虑数据增长趋势
- 预留一定的缓冲空间
- 定期评估和调整容量

**实践经验：**
```java
// 本地缓存容量规划
Cache<String, Object> cache = Caffeine.newBuilder()
    .maximumSize(10000)  // 根据业务需求设置
    .build();

// Redis容量规划
// 1. 评估数据量
long dataSize = productRepository.count();
// 2. 评估单个对象大小
long objectSize = estimateObjectSize(new Product());
// 3. 计算所需容量
long requiredCapacity = dataSize * objectSize;
// 4. 预留缓冲空间
long bufferCapacity = (long) (requiredCapacity * 1.2);
```

## 二、缓存使用经验

### 2.1 缓存预热

**缓存预热的重要性：**
- 避免系统启动后的大量缓存未命中
- 提升系统启动后的性能
- 提高用户体验

**实践经验：**
```java
@Component
public class CacheWarmup {
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @PostConstruct
    public void warmup() {
        log.info("开始缓存预热...");
        
        // 预热热点商品
        List<Product> hotProducts = productService.findHotProducts();
        hotProducts.forEach(product -> {
            String key = "product:" + product.getId();
            redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
        });
        
        // 预热分类商品
        List<Category> categories = categoryService.findAll();
        categories.forEach(category -> {
            List<Product> products = productService.findByCategoryId(category.getId());
            String key = "category:products:" + category.getId();
            redisTemplate.opsForValue().set(key, products, 1, TimeUnit.HOURS);
        });
        
        log.info("缓存预热完成，预热商品数量: {}", hotProducts.size());
    }
}
```

### 2.2 缓存监控

**监控指标：**
- 缓存命中率
- 缓存响应时间
- 缓存容量使用率
- 缓存错误率

**实践经验：**
```java
@Component
public class CacheMonitor {
    @Autowired
    private MeterRegistry meterRegistry;
    
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
    
    @Scheduled(fixedRate = 60000)
    public void printCacheStats() {
        double hitRate = getCacheHitRate("product");
        log.info("缓存命中率: {}", hitRate);
        
        if (hitRate < 0.8) {
            log.warn("缓存命中率过低，请检查缓存配置");
        }
    }
    
    private double getCacheHitRate(String cacheName) {
        double hits = meterRegistry.counter("cache.hit", "cache", cacheName).count();
        double misses = meterRegistry.counter("cache.miss", "cache", cacheName).count();
        return hits / (hits + misses);
    }
}
```

### 2.3 缓存降级

**降级策略：**
- 缓存不可用时，直接查询数据库
- 数据库不可用时，返回默认值或错误信息
- 限流降级，保护系统

**实践经验：**
```java
@Service
public class ProductService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductRepository productRepository;
    
    @HystrixCommand(
        fallbackMethod = "getProductFallback",
        commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "20"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")
        }
    )
    public Product getProduct(Long productId) {
        String key = "product:" + productId;
        Product product = (Product) redisTemplate.opsForValue().get(key);
        
        if (product != null) {
            return product;
        }
        
        product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
        }
        
        return product;
    }
    
    public Product getProductFallback(Long productId) {
        log.warn("缓存降级，直接查询数据库: productId={}", productId);
        return productRepository.findById(productId).orElse(null);
    }
}
```

## 三、缓存优化经验

### 3.1 缓存命中率优化

**优化策略：**
- 合理设置缓存过期时间
- 使用多级缓存
- 缓存热点数据
- 避免缓存雪崩

**实践经验：**
```java
@Service
public class OptimizedCacheService {
    private final Cache<String, Object> localCache;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductRepository productRepository;
    
    public OptimizedCacheService() {
        localCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();
    }
    
    public Product getProduct(Long productId) {
        String key = "product:" + productId;
        
        // 第一级：本地缓存
        Product product = (Product) localCache.getIfPresent(key);
        if (product != null) {
            return product;
        }
        
        // 第二级：Redis缓存
        product = (Product) redisTemplate.opsForValue().get(key);
        if (product != null) {
            localCache.put(key, product);
            return product;
        }
        
        // 第三级：数据库
        product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            // 随机过期时间，防止缓存雪崩
            long expireTime = 60 + ThreadLocalRandom.current().nextInt(30);
            redisTemplate.opsForValue().set(key, product, expireTime, TimeUnit.MINUTES);
            localCache.put(key, product);
        }
        
        return product;
    }
}
```

### 3.2 缓存响应时间优化

**优化策略：**
- 使用本地缓存
- 优化序列化方式
- 使用连接池
- 批量操作

**实践经验：**
```java
@Configuration
public class RedisConfig {
    
    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
            .commandTimeout(Duration.ofSeconds(3))
            .shutdownTimeout(Duration.ZERO)
            .poolConfig(new GenericObjectPoolConfig<>())
            .build();
        
        return new LettuceConnectionFactory(config, clientConfig);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // 使用Protobuf序列化，提高性能
        ProtobufRedisSerializer<Object> serializer = new ProtobufRedisSerializer<>(Object.class);
        
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }
}
```

### 3.3 缓存容量优化

**优化策略：**
- 使用压缩
- 使用更高效的数据结构
- 定期清理无用数据
- 使用LRU淘汰策略

**实践经验：**
```java
@Service
public class CompressedCacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setCompressed(String key, Object value) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(bos);
            ObjectOutputStream oos = new ObjectOutputStream(gzip);
            oos.writeObject(value);
            oos.close();
            gzip.close();
            bos.close();
            
            byte[] compressed = bos.toByteArray();
            redisTemplate.opsForValue().set(key, compressed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object getCompressed(String key) {
        try {
            byte[] compressed = (byte[]) redisTemplate.opsForValue().get(key);
            if (compressed == null) {
                return null;
            }
            
            ByteArrayInputStream bis = new ByteArrayInputStream(compressed);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            ObjectInputStream ois = new ObjectInputStream(gzip);
            Object value = ois.readObject();
            ois.close();
            gzip.close();
            bis.close();
            
            return value;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
```

## 四、缓存故障处理经验

### 4.1 缓存故障检测

**检测方法：**
- 健康检查
- 心跳检测
- 监控告警

**实践经验：**
```java
@Component
public class CacheHealthCheck {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Scheduled(fixedRate = 30000)
    public void healthCheck() {
        try {
            String ping = redisTemplate.getConnectionFactory()
                .getConnection()
                .ping();
            
            if (!"PONG".equals(ping)) {
                log.error("Redis健康检查失败: ping={}", ping);
                alertService.sendAlert("Redis健康检查失败");
            }
        } catch (Exception e) {
            log.error("Redis健康检查异常", e);
            alertService.sendAlert("Redis健康检查异常: " + e.getMessage());
        }
    }
}
```

### 4.2 缓存故障恢复

**恢复策略：**
- 自动重连
- 故障转移
- 数据恢复

**实践经验：**
```java
@Service
public class CacheRecoveryService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductRepository productRepository;
    
    @Retryable(
        value = {RedisConnectionFailureException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void recoverCache() {
        log.info("开始恢复缓存...");
        
        List<Product> products = productRepository.findAll();
        products.forEach(product -> {
            String key = "product:" + product.getId();
            redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
        });
        
        log.info("缓存恢复完成，恢复商品数量: {}", products.size());
    }
    
    @Recover
    public void recoverCacheFallback(RedisConnectionFailureException e) {
        log.error("缓存恢复失败", e);
        alertService.sendAlert("缓存恢复失败: " + e.getMessage());
    }
}
```

### 4.3 缓存故障降级

**降级策略：**
- 缓存不可用时，直接查询数据库
- 数据库不可用时，返回默认值
- 限流降级，保护系统

**实践经验：**
```java
@Service
public class CacheFallbackService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductRepository productRepository;
    
    public Product getProductWithFallback(Long productId) {
        try {
            String key = "product:" + productId;
            Product product = (Product) redisTemplate.opsForValue().get(key);
            
            if (product != null) {
                return product;
            }
            
            product = productRepository.findById(productId).orElse(null);
            if (product != null) {
                redisTemplate.opsForValue().set(key, product, 1, TimeUnit.HOURS);
            }
            
            return product;
        } catch (Exception e) {
            log.error("缓存异常，降级到数据库查询", e);
            return productRepository.findById(productId).orElse(null);
        }
    }
}
```

## 五、缓存安全经验

### 5.1 缓存数据安全

**安全措施：**
- 数据加密
- 访问控制
- 数据脱敏

**实践经验：**
```java
@Service
public class SecureCacheService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void setSecure(String key, Object value) {
        try {
            // 加密数据
            String encrypted = encrypt(value);
            redisTemplate.opsForValue().set(key, encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public Object getSecure(String key) {
        try {
            String encrypted = (String) redisTemplate.opsForValue().get(key);
            if (encrypted == null) {
                return null;
            }
            
            // 解密数据
            return decrypt(encrypted);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private String encrypt(Object value) {
        return AESUtil.encrypt(JSON.toJSONString(value));
    }
    
    private Object decrypt(String encrypted) {
        String json = AESUtil.decrypt(encrypted);
        return JSON.parseObject(json, Object.class);
    }
}
```

### 5.2 缓存访问安全

**安全措施：**
- 访问认证
- 访问授权
- 访问限流

**实践经验：**
```java
@RestController
public class SecureCacheController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(value = 100, timeout = 1, timeUnit = TimeUnit.SECONDS)
    public Product getProduct(@PathVariable Long productId) {
        return productService.getProduct(productId);
    }
}
```

## 六、缓存运维经验

### 6.1 缓存扩容

**扩容策略：**
- 垂直扩容：增加单个节点的资源
- 水平扩容：增加节点数量
- 数据迁移：平滑迁移数据

**实践经验：**
```bash
# Redis集群扩容
1. 添加新节点
redis-cli --cluster add-node new_node:6379 existing_node:6379

2. 分配槽位
redis-cli --cluster reshard existing_node:6379

3. 迁移数据
redis-cli --cluster fix existing_node:6379
```

### 6.2 缓存迁移

**迁移策略：**
- 双写迁移：同时写入新旧缓存
- 灰度迁移：逐步迁移流量
- 全量迁移：一次性迁移所有数据

**实践经验：**
```java
@Service
public class CacheMigrationService {
    @Autowired
    private RedisTemplate<String, Object> oldRedisTemplate;
    @Autowired
    private RedisTemplate<String, Object> newRedisTemplate;
    
    public void migrate() {
        Set<String> keys = oldRedisTemplate.keys("*");
        keys.forEach(key -> {
            Object value = oldRedisTemplate.opsForValue().get(key);
            newRedisTemplate.opsForValue().set(key, value);
        });
    }
}
```

### 6.3 缓存备份

**备份策略：**
- 定期备份
- 增量备份
- 异地备份

**实践经验：**
```bash
# Redis备份
redis-cli BGSAVE

# RDB文件备份
cp /var/lib/redis/dump.rdb /backup/dump_$(date +%Y%m%d).rdb

# AOF文件备份
cp /var/lib/redis/appendonly.aof /backup/appendonly_$(date +%Y%m%d).aof
```

## 七、总结

使用缓存需要综合考虑多个方面：

**设计原则：**
- 合理选择缓存粒度
- 合理设置缓存过期时间
- 合理规划缓存容量

**使用经验：**
- 做好缓存预热
- 做好缓存监控
- 做好缓存降级

**优化经验：**
- 优化缓存命中率
- 优化缓存响应时间
- 优化缓存容量

**故障处理：**
- 做好故障检测
- 做好故障恢复
- 做好故障降级

**安全经验：**
- 保证缓存数据安全
- 保证缓存访问安全

**运维经验：**
- 做好缓存扩容
- 做好缓存迁移
- 做好缓存备份

**最佳实践：**
1. 根据业务场景选择合适的缓存方案
2. 合理设置缓存参数，避免缓存问题
3. 做好缓存监控，及时发现和解决问题
4. 做好缓存降级，保证系统可用性
5. 做好缓存安全，保护数据安全
6. 做好缓存运维，保证缓存稳定运行

**注意事项：**
- 不要过度使用缓存
- 不要忽视缓存一致性
- 不要忽视缓存安全
- 不要忽视缓存监控
- 不要忽视缓存运维