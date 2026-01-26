# 如何设计一个秒杀系统？

## 一、秒杀系统的特点和挑战

### 1.1 秒杀系统的特点

**瞬时高并发**
- 秒杀活动通常在特定时间点开始，大量用户同时抢购
- 请求量可能在短时间内达到平时的几十倍甚至上百倍
- 需要应对瞬间的高并发流量

**库存有限**
- 秒杀商品库存数量有限，通常只有几百或几千件
- 需要严格控制库存，防止超卖
- 需要保证库存扣减的准确性

**时间敏感**
- 秒杀活动时间有限，通常只有几分钟或几十分钟
- 需要精确控制活动时间
- 需要实时显示剩余时间和库存

**用户体验**
- 用户期望快速响应，页面加载流畅
- 需要提供实时的抢购结果反馈
- 需要防止恶意刷单

### 1.2 秒杀系统的挑战

**高并发处理**
- 如何处理瞬间的高并发请求
- 如何保证系统的稳定性和可用性
- 如何避免系统崩溃

**库存控制**
- 如何防止超卖
- 如何保证库存扣减的准确性
- 如何处理库存不足的情况

**性能优化**
- 如何提高系统响应速度
- 如何减少数据库压力
- 如何优化系统资源使用

**安全性**
- 如何防止恶意刷单
- 如何防止作弊
- 如何保护用户隐私

## 二、秒杀系统的架构设计

### 2.1 整体架构

```
┌─────────────────────────────────────────────────────────────┐
│                         CDN层                                 │
│  静态资源分发（图片、CSS、JS、HTML）                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         负载均衡层                            │
│  Nginx/LVS/HAProxy - 请求分发和负载均衡                       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         Web层                                │
│  多台Web服务器 - 处理HTTP请求                               │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         服务层                               │
│  秒杀服务、订单服务、用户服务、商品服务                        │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         缓存层                               │
│  Redis集群 - 缓存热点数据、库存、限流                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         消息队列层                            │
│  Kafka/RabbitMQ - 异步处理订单、削峰填谷                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                         数据层                               │
│  MySQL集群 - 持久化存储、订单数据、库存数据                    │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 分层设计

**前端层**
- 静态资源CDN加速
- 页面静态化
- 前端限流（按钮防抖、验证码）

**网关层**
- 限流
- 黑名单过滤
- 请求路由

**服务层**
- 秒杀服务
- 订单服务
- 用户服务
- 商品服务

**数据层**
- Redis缓存
- MySQL数据库
- 消息队列

## 三、秒杀系统的技术方案

### 3.1 前端优化

**静态资源CDN**
```html
<!-- 使用CDN加速静态资源 -->
<link rel="stylesheet" href="https://cdn.example.com/css/style.css">
<script src="https://cdn.example.com/js/app.js"></script>
<img src="https://cdn.example.com/images/product.jpg">
```

**页面静态化**
```java
@Service
public class PageStaticService {
    
    public void generateStaticPage(Long productId) {
        Product product = productService.getById(productId);
        String html = generateHtml(product);
        saveToFile(productId, html);
    }
    
    private String generateHtml(Product product) {
        return "<html>...</html>";
    }
    
    private void saveToFile(Long productId, String html) {
        String filePath = "/static/products/" + productId + ".html";
        Files.write(Paths.get(filePath), html.getBytes());
    }
}
```

**前端限流**
```javascript
// 按钮防抖
function throttle(func, delay) {
    let lastCall = 0;
    return function(...args) {
        const now = new Date().getTime();
        if (now - lastCall < delay) {
            return;
        }
        lastCall = now;
        return func.apply(this, args);
    };
}

// 使用防抖
const seckillBtn = document.getElementById('seckill-btn');
seckillBtn.addEventListener('click', throttle(seckill, 1000));
```

### 3.2 网关层优化

**限流**
```java
@Component
public class RateLimiterFilter implements GlobalFilter {
    
    private final RateLimiter rateLimiter = RateLimiter.create(1000);
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!rateLimiter.tryAcquire()) {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
}
```

**黑名单过滤**
```java
@Component
public class BlacklistFilter implements GlobalFilter {
    
    @Autowired
    private BlacklistService blacklistService;
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String ip = getClientIp(exchange);
        if (blacklistService.isBlacklisted(ip)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }
    
    private String getClientIp(ServerWebExchange exchange) {
        return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
    }
}
```

### 3.3 服务层优化

**秒杀服务**
```java
@Service
public class SeckillService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public SeckillResult seckill(Long userId, Long productId) {
        // 1. 校验用户
        if (!validateUser(userId)) {
            return SeckillResult.fail("用户不存在");
        }
        
        // 2. 校验商品
        Product product = productService.getById(productId);
        if (product == null) {
            return SeckillResult.fail("商品不存在");
        }
        
        // 3. 校验库存
        String stockKey = "stock:" + productId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            return SeckillResult.fail("库存不足");
        }
        
        // 4. 发送订单消息
        OrderMessage message = new OrderMessage(userId, productId);
        kafkaTemplate.send("seckill-orders", JSON.toJSONString(message));
        
        return SeckillResult.success("抢购成功");
    }
    
    private boolean validateUser(Long userId) {
        return userService.exists(userId);
    }
}
```

**订单服务**
```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductService productService;
    
    @KafkaListener(topics = "seckill-orders")
    public void processOrder(String message) {
        OrderMessage orderMessage = JSON.parseObject(message, OrderMessage.class);
        
        try {
            // 创建订单
            Order order = new Order();
            order.setUserId(orderMessage.getUserId());
            order.setProductId(orderMessage.getProductId());
            order.setStatus(OrderStatus.PENDING);
            orderMapper.insert(order);
            
            // 扣减库存
            productService.decrementStock(orderMessage.getProductId());
            
        } catch (Exception e) {
            log.error("处理订单失败", e);
        }
    }
}
```

### 3.4 缓存层优化

**库存缓存**
```java
@Service
public class StockService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public void initStock(Long productId, Integer stock) {
        String stockKey = "stock:" + productId;
        redisTemplate.opsForValue().set(stockKey, stock);
    }
    
    public boolean decrementStock(Long productId) {
        String stockKey = "stock:" + productId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        return stock >= 0;
    }
    
    public Integer getStock(Long productId) {
        String stockKey = "stock:" + productId;
        return (Integer) redisTemplate.opsForValue().get(stockKey);
    }
}
```

**限流缓存**
```java
@Service
public class RateLimitService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public boolean allowRequest(Long userId, Long productId) {
        String key = "rate_limit:" + userId + ":" + productId;
        Long count = redisTemplate.opsForValue().increment(key);
        
        if (count == 1) {
            redisTemplate.expire(key, 1, TimeUnit.MINUTES);
        }
        
        return count <= 10;
    }
}
```

### 3.5 数据层优化

**数据库优化**
```sql
-- 创建订单表
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 创建商品表
CREATE TABLE `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '商品名称',
  `stock` int(11) NOT NULL COMMENT '库存',
  `price` decimal(10,2) NOT NULL COMMENT '价格',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

**库存扣减**
```java
@Mapper
public interface ProductMapper {
    
    @Update("UPDATE product SET stock = stock - 1 WHERE id = #{id} AND stock > 0")
    int decrementStock(@Param("id") Long id);
}
```

## 四、秒杀系统的优化策略

### 4.1 限流策略

**用户限流**
```java
@Component
public class UserRateLimiter {
    
    private final Map<Long, RateLimiter> userRateLimiters = new ConcurrentHashMap<>();
    
    public boolean allowRequest(Long userId) {
        RateLimiter rateLimiter = userRateLimiters.computeIfAbsent(
            userId, 
            k -> RateLimiter.create(10)
        );
        return rateLimiter.tryAcquire();
    }
}
```

**IP限流**
```java
@Component
public class IpRateLimiter {
    
    private final Map<String, RateLimiter> ipRateLimiters = new ConcurrentHashMap<>();
    
    public boolean allowRequest(String ip) {
        RateLimiter rateLimiter = ipRateLimiters.computeIfAbsent(
            ip, 
            k -> RateLimiter.create(100)
        );
        return rateLimiter.tryAcquire();
    }
}
```

### 4.2 缓存策略

**多级缓存**
```java
@Service
public class ProductService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Cacheable(value = "product", key = "#id")
    public Product getById(Long id) {
        return productMapper.selectById(id);
    }
    
    @CacheEvict(value = "product", key = "#id")
    public void updateProduct(Product product) {
        productMapper.updateById(product);
    }
}
```

**缓存预热**
```java
@Component
public class CacheWarmupService {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private StockService stockService;
    
    @Scheduled(cron = "0 0 0 * * ?")
    public void warmupCache() {
        List<Product> products = productService.getSeckillProducts();
        for (Product product : products) {
            stockService.initStock(product.getId(), product.getStock());
        }
    }
}
```

### 4.3 异步处理

**消息队列**
```java
@Service
public class SeckillService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public void seckill(Long userId, Long productId) {
        SeckillMessage message = new SeckillMessage(userId, productId);
        kafkaTemplate.send("seckill-topic", JSON.toJSONString(message));
    }
}
```

**异步处理订单**
```java
@Service
public class OrderService {
    
    @KafkaListener(topics = "seckill-topic")
    public void processOrder(String message) {
        SeckillMessage seckillMessage = JSON.parseObject(message, SeckillMessage.class);
        createOrder(seckillMessage.getUserId(), seckillMessage.getProductId());
    }
    
    @Transactional
    public void createOrder(Long userId, Long productId) {
        Order order = new Order();
        order.setUserId(userId);
        order.setProductId(productId);
        orderMapper.insert(order);
    }
}
```

### 4.4 数据库优化

**读写分离**
```java
@Configuration
public class DataSourceConfig {
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource masterDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.slave")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }
    
    @Bean
    public DataSource routingDataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("master", masterDataSource());
        targetDataSources.put("slave", slaveDataSource());
        
        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setDefaultTargetDataSource(masterDataSource());
        routingDataSource.setTargetDataSources(targetDataSources);
        return routingDataSource;
    }
}
```

**分库分表**
```java
@Configuration
public class ShardingConfig {
    
    @Bean
    public DataSource shardingDataSource() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        
        TableRuleConfiguration orderTableRule = new TableRuleConfiguration();
        orderTableRule.setLogicTable("order");
        orderTableRule.setActualDataNodes("ds${0..1}.order_${0..1}");
        orderTableRule.setTableShardingStrategyConfig(
            new InlineShardingStrategyConfiguration("id", "order_${id % 2}")
        );
        
        shardingRuleConfig.getTableRuleConfigs().add(orderTableRule);
        
        return ShardingDataSourceFactory.createDataSource(
            createDataSourceMap(), 
            shardingRuleConfig, 
            new Properties()
        );
    }
    
    private Map<String, DataSource> createDataSourceMap() {
        Map<String, DataSource> result = new HashMap<>();
        result.put("ds0", createDataSource("ds0"));
        result.put("ds1", createDataSource("ds1"));
        return result;
    }
    
    private DataSource createDataSource(String name) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/" + name);
        dataSource.setUsername("root");
        dataSource.setPassword("password");
        return dataSource;
    }
}
```

## 五、秒杀系统的实现示例

### 5.1 秒杀接口

```java
@RestController
@RequestMapping("/seckill")
public class SeckillController {
    
    @Autowired
    private SeckillService seckillService;
    
    @Autowired
    private RateLimitService rateLimitService;
    
    @PostMapping("/{productId}")
    public Result seckill(@PathVariable Long productId, @RequestHeader("userId") Long userId) {
        try {
            // 限流
            if (!rateLimitService.allowRequest(userId, productId)) {
                return Result.fail("请求过于频繁");
            }
            
            // 执行秒杀
            SeckillResult result = seckillService.seckill(userId, productId);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("秒杀失败", e);
            return Result.fail("秒杀失败");
        }
    }
}
```

### 5.2 秒杀服务

```java
@Service
public class SeckillService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    public SeckillResult seckill(Long userId, Long productId) {
        // 1. 校验用户
        if (!validateUser(userId)) {
            return SeckillResult.fail("用户不存在");
        }
        
        // 2. 校验商品
        Product product = productService.getById(productId);
        if (product == null) {
            return SeckillResult.fail("商品不存在");
        }
        
        // 3. 校验库存
        String stockKey = "stock:" + productId;
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            return SeckillResult.fail("库存不足");
        }
        
        // 4. 校验重复购买
        String userKey = "user:" + userId + ":" + productId;
        Boolean exists = redisTemplate.hasKey(userKey);
        if (exists) {
            redisTemplate.opsForValue().increment(stockKey);
            return SeckillResult.fail("重复购买");
        }
        
        // 5. 标记用户
        redisTemplate.opsForValue().set(userKey, "1", 1, TimeUnit.HOURS);
        
        // 6. 发送订单消息
        OrderMessage message = new OrderMessage(userId, productId);
        kafkaTemplate.send("seckill-orders", JSON.toJSONString(message));
        
        return SeckillResult.success("抢购成功");
    }
    
    private boolean validateUser(Long userId) {
        return userService.exists(userId);
    }
}
```

### 5.3 订单服务

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductService productService;
    
    @KafkaListener(topics = "seckill-orders")
    public void processOrder(String message) {
        OrderMessage orderMessage = JSON.parseObject(message, OrderMessage.class);
        
        try {
            // 创建订单
            Order order = new Order();
            order.setUserId(orderMessage.getUserId());
            order.setProductId(orderMessage.getProductId());
            order.setStatus(OrderStatus.PENDING);
            orderMapper.insert(order);
            
            // 扣减库存
            int result = productService.decrementStock(orderMessage.getProductId());
            if (result == 0) {
                order.setStatus(OrderStatus.FAILED);
                orderMapper.updateById(order);
            }
            
        } catch (Exception e) {
            log.error("处理订单失败", e);
        }
    }
}
```

## 六、总结

秒杀系统是一个典型的高并发、低延迟、高可用的系统，需要从前端、网关、服务、缓存、数据库等多个层面进行优化。

### 核心要点

1. **前端优化**：CDN加速、页面静态化、前端限流
2. **网关优化**：限流、黑名单过滤、请求路由
3. **服务优化**：异步处理、消息队列、分布式锁
4. **缓存优化**：多级缓存、缓存预热、缓存更新
5. **数据库优化**：读写分离、分库分表、索引优化

### 关键技术

1. **限流**：用户限流、IP限流、接口限流
2. **缓存**：Redis缓存、多级缓存、缓存预热
3. **异步**：消息队列、异步处理、削峰填谷
4. **分布式**：分布式锁、分布式事务、分布式缓存

### 最佳实践

1. **分层设计**：前端层、网关层、服务层、数据层
2. **限流降级**：多维度限流、服务降级、熔断保护
3. **缓存优先**：缓存热点数据、减少数据库压力
4. **异步处理**：使用消息队列、异步处理订单
5. **监控告警**：实时监控、及时告警、快速响应

通过以上技术方案和优化策略，可以构建一个高性能、高可用、高并发的秒杀系统。
