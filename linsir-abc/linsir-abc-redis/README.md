# linsir-abc-redis 项目使用说明

## 项目简介

linsir-abc-redis 是一个基于 Spring Boot 和 Redis 的综合示例项目，展示了如何在 Java 应用中集成和使用 Redis 进行缓存、会话管理等操作。项目包含了多个功能模块，如用户管理、缓存操作、事件发布、订单管理等，是学习 Redis 在 Spring Boot 应用中使用的实用示例。

## 技术栈

- **核心框架**：Spring Boot 3.x
- **数据访问**：Spring Data JPA、MySQL
- **缓存**：Redis、Spring Cache
- **Redis客户端**：Jedis
- **序列化**：Jackson
- **测试框架**：JUnit 5
- **构建工具**：Maven

## 环境要求

1. **JDK**：Java 17+
2. **MySQL**：8.0+
3. **Redis**：6.0+
4. **Maven**：3.8+

## 项目结构

```
linsir-abc-redis/
├── src/
│   ├── main/
│   │   ├── java/com/linsir/
│   │   │   ├── components/       # 组件（事件监听器等）
│   │   │   ├── configs/          # 配置类
│   │   │   ├── constants/        # 常量定义
│   │   │   ├── controller/       # API控制器
│   │   │   ├── entity/           # 实体类
│   │   │   ├── repository/       # 数据访问层
│   │   │   ├── service/          # 服务层
│   │   │   │   └── impl/         # 服务实现
│   │   │   └── Application.java  # 应用启动类
│   │   └── resources/
│   │       ├── application.yaml      # 主配置文件
│   │       └── application-dev.yaml  # 开发环境配置
│   └── test/                     # 测试代码
└── pom.xml                      # Maven配置
```

## 主要配置

### 1. Redis配置

Redis连接配置位于 `ApplicationRedisStandaloneConfiguration.java`：

```java
@Bean
public RedisConnectionFactory jedisConnectionFactory() {
    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration("127.0.0.1",6379);
    //redisStandaloneConfiguration.setPassword("password"); // 如需密码，取消注释并设置
    return new JedisConnectionFactory(redisStandaloneConfiguration);
}
```

### 2. 缓存配置

缓存配置位于 `RedisConfiguration.java`：

```java
@Bean
public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5))  // 默认缓存时间5分钟
            .enableTimeToIdle()
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(springSessionDefaultRedisSerializer));
    return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(defaults)
            .build();
}
```

### 3. 数据库配置

数据库连接配置位于 `application-dev.yaml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/linsir-abc-redis?characterEncoding=utf8
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 主要功能模块

### 1. 缓存服务（CacheService）

使用 Spring Cache 注解实现缓存操作，支持自动缓存、更新和删除。

**核心方法**：
- `addPerson(Person person)`：添加人员信息
- `findById(String id)`：根据ID查询人员（自动缓存）
- `updatePerson(Person person)`：更新人员信息（自动更新缓存）
- `deletePerson(String id)`：删除人员信息（自动删除缓存）

**缓存注解说明**：
- `@Cacheable`：方法执行前检查缓存，存在则返回缓存值
- `@CachePut`：方法执行后更新缓存
- `@CacheEvict`：删除缓存

### 2. 用户服务（UserService）

提供用户管理功能，支持用户的增删改查操作。

**核心方法**：
- `save(User user)`：保存用户信息
- `getUserById(int id)`：根据ID查询用户
- `getUserByUsername(String username)`：根据用户名查询用户

### 3. 事件发布服务（EventPublisherService）

使用 Spring 事件机制实现事件的发布和监听。

**主要事件**：
- `PersonSaveEvent`：人员保存事件
- `NotifyMsgEvent`：通知消息事件

### 4. 订单服务（GoodsOrderService）

提供商品订单的管理功能。

### 5. 银行服务（BankService）

提供银行相关的操作功能。

### 6. 评论服务（CommentService）

提供评论相关的操作功能。

### 7. Redis数据类型服务（RedisDataTypeService）

提供Redis各种数据类型的操作功能，支持String、List、Set、Hash、ZSet等类型的完整操作。

**核心方法**：
- `setString(String key, String value)`：设置字符串值
- `getString(String key)`：获取字符串值
- `addToList(String key, String value)`：添加元素到列表
- `getList(String key, long start, long end)`：获取列表范围内的元素
- `addToSet(String key, String... values)`：添加元素到集合
- `getSet(String key)`：获取集合所有元素
- `setSetIntersection(String key1, String key2, String destKey)`：计算集合交集
- `putToHash(String key, String hashKey, Object value)`：添加哈希字段
- `getFromHash(String key, String hashKey)`：获取哈希字段值
- `addToZSet(String key, String value, double score)`：添加元素到有序集合
- `getZSetByScore(String key, double minScore, double maxScore)`：根据分数范围获取有序集合元素

### 8. 热点数据缓存服务（HotDataService）

提供热点数据的缓存管理功能，支持数据的设置、获取、过期时间管理等操作。

**核心方法**：
- `setHotData(String key, T value, long expireSeconds)`：设置热点数据
- `getHotData(String key)`：获取热点数据
- `deleteHotData(String key)`：删除热点数据
- `batchSetHotData(Map<String, Object> dataMap, long expireSeconds)`：批量设置热点数据
- `batchGetHotData(List<String> keys)`：批量获取热点数据
- `refreshHotDataExpire(String key, long expireSeconds)`：刷新热点数据过期时间
- `getHotDataTtl(String key)`：获取热点数据剩余过期时间

### 9. 优惠活动服务（PromotionService）

提供限时优惠活动的管理功能，支持活动的创建、验证和取消操作。

**核心方法**：
- `createPromotion(String promotionId, String name, double discount, long startTime, long endTime, long expireSeconds)`：创建优惠活动
- `getPromotion(String promotionId)`：获取活动信息
- `isPromotionValid(String promotionId)`：验证活动是否有效
- `cancelPromotion(String promotionId)`：取消优惠活动
- `getAllValidPromotions()`：获取所有有效活动

### 10. 手机验证码服务（SmsVerificationService）

提供手机验证码的生成、发送和验证功能，支持验证码的过期时间和尝试次数限制。

**核心方法**：
- `sendCode(String phoneNumber, long expireSeconds, int maxAttempts)`：发送验证码
- `verifyCode(String phoneNumber, String code)`：验证验证码
- `canSendCode(String phoneNumber, long coolDownSeconds)`：检查是否可以发送验证码
- `clearCode(String phoneNumber)`：清除验证码
- `getCodeTtl(String phoneNumber)`：获取验证码剩余过期时间

### 11. 计数器服务（CounterService）

提供基于Redis的分布式计数器功能，支持计数的增加、减少、获取和重置操作。

**核心方法**：
- `increment(String key, long delta)`：增加计数
- `decrement(String key, long delta)`：减少计数
- `getCount(String key)`：获取计数值
- `resetCounter(String key)`：重置计数器
- `setCount(String key, long value)`：设置计数值
- `setExpire(String key, long expireSeconds)`：设置过期时间
- `getExpire(String key)`：获取过期时间
- `batchIncrement(Map<String, Long> deltaMap)`：批量增加计数
- `batchGetCount(List<String> keys)`：批量获取计数
- `deleteCounter(String key)`：删除计数器

### 12. 评论和点赞服务（CommentLikeService）

提供评论、回复和点赞的管理功能，支持评论的点赞、取消点赞、获取评论列表和热门评论等操作。

**核心方法**：
- `likeComment(long commentId, long userId)`：点赞评论
- `unlikeComment(long commentId, long userId)`：取消点赞评论
- `getCommentLikeCount(long commentId)`：获取评论的点赞数
- `hasLikedComment(long commentId, long userId)`：检查用户是否已点赞评论
- `getArticleComments(long articleId, int page, int size)`：获取文章的评论列表
- `getCommentReplies(long commentId, int page, int size)`：获取评论的回复列表
- `getHotComments(long articleId, int limit)`：获取热门评论
- `incrementCommentViewCount(long commentId)`：增加评论的浏览量
- `getCommentViewCount(long commentId)`：获取评论的浏览量

## 使用示例

### 1. 缓存操作示例

```java
// 注入缓存服务
@Autowired
private CacheService cacheService;

// 添加人员
Person person = new Person();
person.setName("张三");
person.setAge(30);
cacheService.addPerson(person);

// 查询人员（首次查询会缓存）
Person cachedPerson = cacheService.findById("1");

// 更新人员（会自动更新缓存）
cachedPerson.setAge(31);
cacheService.updatePerson(cachedPerson);

// 删除人员（会自动删除缓存）
cacheService.deletePerson("1");
```

### 2. 用户操作示例

```java
// 注入用户服务
@Autowired
private UserService userService;

// 保存用户
User user = new User();
user.setUsername("admin");
user.setPassword("123456");
userService.save(user);

// 根据ID查询用户
User userById = userService.getUserById(1);

// 根据用户名查询用户
User userByUsername = userService.getUserByUsername("admin");
```

### 3. RedisTemplate 操作示例

```java
// 注入RedisTemplate
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 设置值
redisTemplate.opsForValue().set("key", "value", 1, TimeUnit.MINUTES);

// 获取值
Object value = redisTemplate.opsForValue().get("key");

// 删除值
redisTemplate.delete("key");

// 操作哈希
redisTemplate.opsForHash().put("hashKey", "field", "value");
Object hashValue = redisTemplate.opsForHash().get("hashKey", "field");

// 操作列表
redisTemplate.opsForList().leftPush("listKey", "value1");
redisTemplate.opsForList().leftPush("listKey", "value2");
List<Object> list = redisTemplate.opsForList().range("listKey", 0, -1);
```

## 测试说明

项目包含多个测试类，用于测试各个服务的功能：

- `BankServiceTest.java`：测试银行服务
- `CacheTest.java`：测试缓存功能
- `CommentServiceTest.java`：测试评论服务
- `EventPublisherServiceImplTest.java`：测试事件发布服务
- `GoodsOrderServiceTest.java`：测试订单服务
- `OperationServiceTest.java`：测试操作服务
- `RedisTemplateServiceImplTest.java`：测试RedisTemplate服务
- `UserServiceImplTest.java`：测试用户服务

### 运行测试

使用 Maven 运行测试：

```bash
mvn test
```

## 项目启动

### 1. 环境准备

1. **启动MySQL服务**
2. **创建数据库**：`CREATE DATABASE linsir-abc-redis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
3. **启动Redis服务**：默认端口6379，无密码

### 2. 构建项目

```bash
mvn clean package
```

### 3. 运行项目

```bash
java -jar target/linsir-abc-redis-1.0.0.jar
```

或者在IDE中直接运行 `Application.java` 类。

## API接口

项目提供了以下主要API接口：

- **缓存接口**：`/cache/`
- **订单接口**：`/goods/`
- **操作接口**：`/operation/`
- **登录接口**：`/login/`
- **首页接口**：`/`
- **Redis数据类型接口**：`/redis/data-type/`
- **热点数据缓存接口**：`/redis/hot-data/`
- **优惠活动接口**：`/redis/promotion/`
- **手机验证码接口**：`/redis/sms/`
- **计数器接口**：`/redis/counter/`
- **评论和点赞接口**：`/redis/comment/`

## 常见问题

### 1. Redis连接失败

**问题**：应用启动时提示Redis连接失败
**解决方案**：
- 检查Redis服务是否运行
- 检查Redis配置是否正确（主机、端口、密码）
- 检查防火墙是否阻止Redis连接

### 2. 数据库连接失败

**问题**：应用启动时提示数据库连接失败
**解决方案**：
- 检查MySQL服务是否运行
- 检查数据库配置是否正确（主机、端口、用户名、密码）
- 检查数据库是否存在

### 3. 缓存不生效

**问题**：使用@Cacheable注解的方法没有缓存效果
**解决方案**：
- 检查@EnableCaching注解是否启用
- 检查缓存配置是否正确
- 检查方法参数是否适合作为缓存键

### 4. 序列化问题

**问题**：Redis存储的对象无法正确序列化或反序列化
**解决方案**：
- 确保实体类实现了Serializable接口
- 检查Jackson配置是否正确
- 对于复杂对象，考虑自定义序列化器

## 项目扩展

### 1. 添加Redis集群支持

修改 `ApplicationRedisStandaloneConfiguration.java`，使用 `RedisClusterConfiguration` 替代 `RedisStandaloneConfiguration`：

```java
@Bean
public RedisConnectionFactory jedisConnectionFactory() {
    RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
    clusterConfiguration.addClusterNode(new RedisNode("127.0.0.1", 6379));
    clusterConfiguration.addClusterNode(new RedisNode("127.0.0.1", 6380));
    clusterConfiguration.addClusterNode(new RedisNode("127.0.0.1", 6381));
    return new JedisConnectionFactory(clusterConfiguration);
}
```

### 2. 添加Redis哨兵支持

使用 `RedisSentinelConfiguration` 配置哨兵模式：

```java
@Bean
public RedisConnectionFactory jedisConnectionFactory() {
    RedisSentinelConfiguration sentinelConfiguration = new RedisSentinelConfiguration();
    sentinelConfiguration.setMaster("mymaster");
    sentinelConfiguration.addSentinel(new RedisNode("127.0.0.1", 26379));
    sentinelConfiguration.addSentinel(new RedisNode("127.0.0.1", 26380));
    return new JedisConnectionFactory(sentinelConfiguration);
}
```

### 3. 自定义缓存键生成器

实现 `KeyGenerator` 接口，自定义缓存键的生成规则：

```java
@Bean
public KeyGenerator customKeyGenerator() {
    return (target, method, params) -> {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getClass().getName());
        sb.append(":").append(method.getName());
        for (Object param : params) {
            sb.append(":").append(param.toString());
        }
        return sb.toString();
    };
}
```

然后在缓存注解中使用：

```java
@Cacheable(keyGenerator = "customKeyGenerator")
```

## 总结

linsir-abc-redis 项目是一个综合的 Spring Boot + Redis 示例项目，展示了如何在 Java 应用中集成和使用 Redis 进行缓存、会话管理、分布式计数、限时优惠活动、手机验证码、评论和点赞等操作。项目结构清晰，功能完整，包含了多个实用的功能模块，可以作为学习 Redis 在 Spring Boot 应用中使用的参考资料。

通过本项目，您可以学习到：
- Redis 与 Spring Boot 的集成方法
- Spring Cache 注解的使用
- Redis 缓存的配置和管理
- Spring 事件机制的应用
- 基于 Redis 的会话管理
- 常见的 Redis 操作示例
- Redis 在限时优惠活动中的应用
- Redis 在手机验证码系统中的应用
- Redis 在分布式计数器中的应用
- Redis 在评论和点赞系统中的应用

希望本项目对您的学习和开发有所帮助！

---

**更新时间**：2026-01-24
**版本**：1.5.0