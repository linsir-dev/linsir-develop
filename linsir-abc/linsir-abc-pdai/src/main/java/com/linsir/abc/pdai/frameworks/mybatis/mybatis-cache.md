# MyBatis的缓存机制？

## 1. 缓存概述

缓存是MyBatis的重要特性之一，它可以显著提高系统性能，减少数据库访问次数。MyBatis提供了多级缓存机制，包括一级缓存（Session缓存）和二级缓存（Namespace缓存），开发者可以根据实际需求进行配置和使用。

## 2. 缓存的优势

### 2.1 性能提升
- 减少数据库访问次数，降低数据库压力
- 提高查询响应速度，提升用户体验
- 减轻网络传输负担

### 2.2 系统稳定性
- 降低数据库并发访问量，提高系统稳定性
- 减少数据库死锁和超时的可能性

### 2.3 扩展性
- 支持多种缓存实现，包括内存缓存、Redis、Ehcache等
- 可以根据业务需求选择合适的缓存策略

## 3. MyBatis的缓存架构

### 3.1 缓存层级

| 缓存级别 | 作用域 | 实现类 | 默认状态 |
|---------|--------|--------|----------|
| 一级缓存 | SqlSession | PerpetualCache | 开启（不可关闭） |
| 二级缓存 | Mapper Namespace | PerpetualCache + 装饰器 | 关闭（可开启） |
| 第三方缓存 | 全局 | Redis、Ehcache等 | 需配置 |

### 3.2 缓存执行流程

1. **查询请求**：应用程序发起查询请求
2. **二级缓存检查**：先检查二级缓存是否存在数据
3. **一级缓存检查**：如果二级缓存未命中，检查一级缓存
4. **数据库查询**：如果一级缓存也未命中，查询数据库
5. **缓存更新**：将查询结果存入一级缓存和二级缓存
6. **返回结果**：返回查询结果给应用程序

## 4. 一级缓存（Session缓存）

### 4.1 一级缓存概述

**定义：** 一级缓存是MyBatis默认开启的缓存，作用域为SqlSession，在同一个SqlSession中，相同的查询会被缓存，后续相同的查询会直接从缓存中获取结果，而不会再次访问数据库。

**实现：** 基于`PerpetualCache`类实现，底层使用HashMap存储缓存数据。

### 4.2 一级缓存的工作原理

**缓存键的生成：**
- MappedStatement的ID（即Mapper方法的全限定名）
- SQL语句
- 参数值
- RowBounds（分页参数）
- 环境ID

**缓存生命周期：**
- 创建：SqlSession创建时
- 更新：执行查询操作时
- 清理：执行增删改操作、调用`clearCache()`方法或SqlSession关闭时

### 4.3 一级缓存的示例

**示例1：相同SqlSession中的缓存**

```java
// 创建SqlSession
SqlSession sqlSession = sqlSessionFactory.openSession();

// 第一次查询，从数据库获取
User user1 = sqlSession.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第一次查询：" + user1);

// 第二次查询，从一级缓存获取
User user2 = sqlSession.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第二次查询：" + user2);
System.out.println("两次查询的对象是否相同：" + (user1 == user2)); // true

// 关闭SqlSession
sqlSession.close();
```

**示例2：增删改操作会清理缓存**

```java
// 创建SqlSession
SqlSession sqlSession = sqlSessionFactory.openSession();

// 第一次查询，从数据库获取
User user1 = sqlSession.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第一次查询：" + user1);

// 执行更新操作，会清理一级缓存
User user = new User();
user.setId(1);
user.setName("Updated Name");
sqlSession.update("com.example.mapper.UserMapper.update", user);
sqlSession.commit();

// 第二次查询，从数据库获取（因为缓存已被清理）
User user2 = sqlSession.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第二次查询：" + user2);
System.out.println("两次查询的对象是否相同：" + (user1 == user2)); // false

// 关闭SqlSession
sqlSession.close();
```

**示例3：不同SqlSession的缓存**

```java
// 创建第一个SqlSession
SqlSession sqlSession1 = sqlSessionFactory.openSession();
// 第一次查询，从数据库获取
User user1 = sqlSession1.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第一次查询（SqlSession1）：" + user1);
// 关闭SqlSession1
sqlSession1.close();

// 创建第二个SqlSession
SqlSession sqlSession2 = sqlSessionFactory.openSession();
// 第二次查询，从数据库获取（因为是不同的SqlSession）
User user2 = sqlSession2.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第二次查询（SqlSession2）：" + user2);
System.out.println("两次查询的对象是否相同：" + (user1 == user2)); // false
// 关闭SqlSession2
sqlSession2.close();
```

### 4.4 一级缓存的优缺点

**优点：**
- 默认开启，无需配置
- 实现简单，性能提升明显
- 适合同一事务内的多次查询

**缺点：**
- 作用域小，仅在SqlSession内有效
- 不同SqlSession之间无法共享缓存
- 可能导致脏读（如果在不同SqlSession中修改了相同数据）

## 5. 二级缓存（Namespace缓存）

### 5.1 二级缓存概述

**定义：** 二级缓存是MyBatis的全局缓存，作用域为Mapper Namespace，多个SqlSession可以共享同一个Namespace的缓存数据。

**实现：** 基于`PerpetualCache`类实现，底层使用HashMap存储缓存数据，并通过装饰器模式提供额外功能（如LRU、FIFO等缓存淘汰策略）。

### 5.2 二级缓存的配置

**步骤1：在mybatis-config.xml中开启全局缓存**

```xml
<configuration>
    <settings>
        <!-- 开启全局二级缓存，默认为true -->
        <setting name="cacheEnabled" value="true"/>
    </settings>
</configuration>
```

**步骤2：在Mapper.xml中配置缓存**

```xml
<!-- 方式1：默认配置 -->
<mapper namespace="com.example.mapper.UserMapper">
    <cache/>
    <!-- 其他SQL语句 -->
</mapper>

<!-- 方式2：自定义配置 -->
<mapper namespace="com.example.mapper.UserMapper">
    <cache 
        eviction="LRU" 
        flushInterval="60000" 
        size="1024" 
        readOnly="false"/>
    <!-- 其他SQL语句 -->
</mapper>
```

**缓存配置属性：**
| 属性 | 描述 | 默认值 | 可选值 |
|------|------|--------|--------|
| `eviction` | 缓存淘汰策略 | LRU | LRU、FIFO、SOFT、WEAK |
| `flushInterval` | 缓存刷新间隔（毫秒） | 不设置 | 正整数 |
| `size` | 缓存容量 | 1024 | 正整数 |
| `readOnly` | 是否只读 | false | true、false |
| `type` | 自定义缓存实现类 | PerpetualCache | 实现Cache接口的类 |

### 5.3 二级缓存的工作原理

**缓存键的生成：**
- MappedStatement的ID（即Mapper方法的全限定名）
- SQL语句
- 参数值
- RowBounds（分页参数）
- 环境ID

**缓存生命周期：**
- 创建：Mapper Namespace被加载时
- 更新：执行查询操作时
- 清理：执行增删改操作、调用`flushCache`方法或缓存过期时

### 5.4 二级缓存的示例

**示例1：启用二级缓存**

```xml
<!-- UserMapper.xml -->
<mapper namespace="com.example.mapper.UserMapper">
    <cache 
        eviction="LRU" 
        flushInterval="30000" 
        size="100" 
        readOnly="false"/>
        
    <select id="findById" parameterType="int" resultType="User">
        SELECT * FROM users WHERE id = #{id}
    </select>
    
    <update id="update" parameterType="User">
        UPDATE users SET name = #{name}, age = #{age} WHERE id = #{id}
    </update>
</mapper>
```

```java
// 创建第一个SqlSession
SqlSession sqlSession1 = sqlSessionFactory.openSession();
// 第一次查询，从数据库获取
User user1 = sqlSession1.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第一次查询（SqlSession1）：" + user1);
// 关闭SqlSession1，此时数据会被写入二级缓存
sqlSession1.close();

// 创建第二个SqlSession
SqlSession sqlSession2 = sqlSessionFactory.openSession();
// 第二次查询，从二级缓存获取
User user2 = sqlSession2.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第二次查询（SqlSession2）：" + user2);
System.out.println("两次查询的对象是否相同：" + (user1 == user2)); // false（因为二级缓存默认是可读写的，会返回对象的副本）
// 关闭SqlSession2
sqlSession2.close();
```

**示例2：配置只读缓存**

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <cache 
        eviction="LRU" 
        size="100" 
        readOnly="true"/>
</mapper>
```

```java
// 创建第一个SqlSession
SqlSession sqlSession1 = sqlSessionFactory.openSession();
// 第一次查询，从数据库获取
User user1 = sqlSession1.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第一次查询（SqlSession1）：" + user1);
// 关闭SqlSession1，此时数据会被写入二级缓存
sqlSession1.close();

// 创建第二个SqlSession
SqlSession sqlSession2 = sqlSessionFactory.openSession();
// 第二次查询，从二级缓存获取
User user2 = sqlSession2.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第二次查询（SqlSession2）：" + user2);
System.out.println("两次查询的对象是否相同：" + (user1 == user2)); // true（因为二级缓存是只读的，返回相同的对象）
// 关闭SqlSession2
sqlSession2.close();
```

**示例3：缓存刷新**

```java
// 创建第一个SqlSession
SqlSession sqlSession1 = sqlSessionFactory.openSession();
// 第一次查询，从数据库获取
User user1 = sqlSession1.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第一次查询：" + user1);
// 关闭SqlSession1，数据写入二级缓存
sqlSession1.close();

// 创建第二个SqlSession
SqlSession sqlSession2 = sqlSessionFactory.openSession();
// 执行更新操作，会清理二级缓存
User user = new User();
user.setId(1);
user.setName("Updated Name");
sqlSession2.update("com.example.mapper.UserMapper.update", user);
sqlSession2.commit();
// 第二次查询，从数据库获取（因为缓存已被清理）
User user2 = sqlSession2.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第二次查询：" + user2);
// 关闭SqlSession2
sqlSession2.close();

// 创建第三个SqlSession
SqlSession sqlSession3 = sqlSessionFactory.openSession();
// 第三次查询，从二级缓存获取（使用新的数据）
User user3 = sqlSession3.selectOne("com.example.mapper.UserMapper.findById", 1);
System.out.println("第三次查询：" + user3);
// 关闭SqlSession3
sqlSession3.close();
```

### 5.5 二级缓存的优缺点

**优点：**
- 作用域大，多个SqlSession可以共享缓存
- 可以配置缓存策略和过期时间
- 适用于查询频繁、修改较少的数据

**缺点：**
- 配置相对复杂
- 可能导致脏读（如果多个Namespace修改了相关数据）
- 序列化和反序列化会带来性能开销
- 不适合频繁修改的数据

## 6. 缓存淘汰策略

### 6.1 内置淘汰策略

| 策略 | 描述 | 适用场景 |
|------|------|----------|
| LRU（Least Recently Used） | 最近最少使用，移除最长时间未被使用的对象 | 大多数场景，默认策略 |
| FIFO（First In First Out） | 先进先出，按对象进入缓存的顺序移除 | 数据更新频率稳定的场景 |
| SOFT | 软引用，基于垃圾回收器状态和软引用规则移除对象 | 内存敏感的场景 |
| WEAK | 弱引用，更积极地基于垃圾回收器状态和弱引用规则移除对象 | 内存非常有限的场景 |

### 6.2 自定义淘汰策略

**步骤：**
1. 实现`Cache`接口
2. 在Mapper.xml中配置自定义缓存

**示例：**

```java
public class CustomCache implements Cache {
    private final String id;
    private final Map<Object, Object> cache;
    
    public CustomCache(String id) {
        this.id = id;
        this.cache = new LinkedHashMap<>(1024, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                return size() > 1000; // 自定义缓存大小限制
            }
        };
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public int getSize() {
        return cache.size();
    }
    
    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }
    
    @Override
    public Object getObject(Object key) {
        return cache.get(key);
    }
    
    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }
    
    @Override
    public void clear() {
        cache.clear();
    }
    
    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }
}
```

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <cache type="com.example.cache.CustomCache"/>
</mapper>
```

## 7. 第三方缓存集成

MyBatis支持集成第三方缓存框架，如Redis、Ehcache、Memcached等，以提供更强大的缓存功能。

### 7.1 集成Ehcache

**步骤1：添加依赖**

```xml
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-ehcache</artifactId>
    <version>1.2.3</version>
</dependency>
<dependency>
    <groupId>net.sf.ehcache</groupId>
    <artifactId>ehcache</artifactId>
    <version>2.10.9.2</version>
</dependency>
```

**步骤2：配置Ehcache**

创建`ehcache.xml`文件：

```xml
<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:noNamespaceSchemaLocation="http://ehcache.org/ehcache.xsd"
         updateCheck="false">
    <diskStore path="java.io.tmpdir/ehcache"/>
    
    <defaultCache
            maxElementsInMemory="10000"
            eternal="false"
            timeToIdleSeconds="120"
            timeToLiveSeconds="120"
            overflowToDisk="true"
            maxElementsOnDisk="10000000"
            diskPersistent="false"
            diskExpiryThreadIntervalSeconds="120"
            memoryStoreEvictionPolicy="LRU"/>
    
    <cache name="com.example.mapper.UserMapper"
           maxElementsInMemory="1000"
           eternal="false"
           timeToIdleSeconds="300"
           timeToLiveSeconds="600"
           overflowToDisk="true"/>
</ehcache>
```

**步骤3：在Mapper.xml中配置**

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <cache type="org.mybatis.caches.ehcache.EhcacheCache"/>
</mapper>
```

### 7.2 集成Redis

**步骤1：添加依赖**

```xml
<dependency>
    <groupId>org.mybatis.caches</groupId>
    <artifactId>mybatis-redis</artifactId>
    <version>1.0.0-beta2</version>
</dependency>
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>4.3.1</version>
</dependency>
```

**步骤2：配置Redis**

创建`redis.properties`文件：

```properties
redis.host=localhost
redis.port=6379
redis.password=
redis.database=0
redis.timeout=5000
```

**步骤3：在Mapper.xml中配置**

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <cache type="org.mybatis.caches.redis.RedisCache"/>
</mapper>
```

### 7.3 集成Caffeine

**步骤1：添加依赖**

```xml
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
    <version>3.1.1</version>
</dependency>
```

**步骤2：实现Caffeine缓存**

```java
public class CaffeineCache implements Cache {
    private final String id;
    private final com.github.benmanes.caffeine.cache.Cache<Object, Object> cache;
    
    public CaffeineCache(String id) {
        this.id = id;
        this.cache = com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public int getSize() {
        return (int) cache.estimatedSize();
    }
    
    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }
    
    @Override
    public Object getObject(Object key) {
        return cache.getIfPresent(key);
    }
    
    @Override
    public Object removeObject(Object key) {
        Object value = cache.getIfPresent(key);
        cache.invalidate(key);
        return value;
    }
    
    @Override
    public void clear() {
        cache.invalidateAll();
    }
    
    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }
}
```

**步骤3：在Mapper.xml中配置**

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <cache type="com.example.cache.CaffeineCache"/>
</mapper>
```

## 8. 缓存的性能优化

### 8.1 缓存策略优化

**策略：**
- 选择合适的缓存淘汰策略（如LRU适用于大多数场景）
- 设置合理的缓存大小和过期时间
- 对不同类型的数据使用不同的缓存策略

**示例：**

```xml
<!-- 热点数据，缓存时间长 -->
<cache 
    eviction="LRU" 
    flushInterval="3600000" 
    size="5000" 
    readOnly="true"/>

<!-- 普通数据，缓存时间中等 -->
<cache 
    eviction="LRU" 
    flushInterval="600000" 
    size="1000" 
    readOnly="false"/>

<!-- 频繁更新的数据，缓存时间短 -->
<cache 
    eviction="LRU" 
    flushInterval="60000" 
    size="500" 
    readOnly="false"/>
```

### 8.2 序列化优化

**策略：**
- 使用更高效的序列化方式（如Protobuf、Kryo）
- 减少序列化对象的大小
- 对于只读缓存，避免序列化和反序列化开销

**示例：**

```java
// 使用Kryo序列化
public class KryoSerializer {
    private static final Kryo kryo = new Kryo();
    
    public static byte[] serialize(Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        kryo.writeObject(output, object);
        output.flush();
        return bos.toByteArray();
    }
    
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Input input = new Input(bytes);
        return kryo.readObject(input, clazz);
    }
}
```

### 8.3 缓存预热

**策略：**
- 系统启动时预加载热点数据到缓存
- 使用定时任务定期刷新缓存
- 避免缓存雪崩

**示例：**

```java
@Component
public class CacheWarmer implements ApplicationRunner {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 预热用户缓存
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            List<User> users = sqlSession.selectList("com.example.mapper.UserMapper.findAll");
            // 执行查询后，数据会被写入缓存
        } finally {
            sqlSession.close();
        }
    }
}
```

### 8.4 缓存监控

**策略：**
- 监控缓存命中率
- 监控缓存大小和内存使用情况
- 及时发现和解决缓存问题

**示例：**

```java
public class CacheMonitor {
    private final Cache cache;
    private long hits = 0;
    private long misses = 0;
    
    public CacheMonitor(Cache cache) {
        this.cache = cache;
    }
    
    public Object get(Object key) {
        Object value = cache.getObject(key);
        if (value != null) {
            hits++;
        } else {
            misses++;
        }
        return value;
    }
    
    public double getHitRate() {
        long total = hits + misses;
        return total > 0 ? (double) hits / total : 0;
    }
    
    public void reset() {
        hits = 0;
        misses = 0;
    }
}
```

## 9. 缓存的最佳实践

### 9.1 缓存使用策略

**适合使用缓存的场景：**
- 查询频繁、修改较少的数据（如配置信息、字典数据）
- 热点数据（如热门商品、用户信息）
- 计算密集型的查询结果

**不适合使用缓存的场景：**
- 频繁修改的数据
- 实时性要求高的数据
- 数据量过大的数据
- 敏感数据

### 9.2 缓存配置最佳实践

**一级缓存：**
- 保持默认开启状态
- 注意在增删改操作后，缓存会被自动清理

**二级缓存：**
- 对查询频繁、修改较少的Mapper开启二级缓存
- 设置合理的缓存大小和过期时间
- 对于只读数据，使用`readOnly="true"`提高性能
- 对于关联查询，使用`cache-ref`共享缓存

**示例：**

```xml
<!-- 共享缓存 -->
<mapper namespace="com.example.mapper.UserMapper">
    <cache 
        eviction="LRU" 
        flushInterval="300000" 
        size="1000" 
        readOnly="true"/>
</mapper>

<mapper namespace="com.example.mapper.UserExtMapper">
    <!-- 引用UserMapper的缓存 -->
    <cache-ref namespace="com.example.mapper.UserMapper"/>
</mapper>
```

### 9.3 缓存一致性

**策略：**
- 使用事务保证缓存和数据库的一致性
- 增删改操作后及时清理相关缓存
- 对于分布式系统，使用消息队列或发布-订阅模式同步缓存

**示例：**

```java
@Transactional
public void updateUser(User user) {
    // 更新数据库
    userMapper.update(user);
    // 清理缓存
    sqlSession.clearCache();
    // 或者使用Redis的发布-订阅模式通知其他节点清理缓存
    redisTemplate.convertAndSend("cache:user:update", user.getId());
}
```

### 9.4 缓存安全性

**策略：**
- 缓存中不存储敏感信息（如密码、令牌）
- 对缓存数据进行加密（如果必要）
- 设置合理的缓存过期时间
- 监控缓存访问权限

## 10. 常见问题与解决方案

### 10.1 问题：缓存不一致

**原因：**
- 多个SqlSession修改了相同数据
- 二级缓存未及时清理
- 分布式环境下缓存同步问题

**解决方案：**
- 使用事务保证数据一致性
- 增删改操作后及时清理缓存
- 对于分布式系统，使用Redis等分布式缓存
- 设置合理的缓存过期时间

**示例：**

```java
// 错误示例
public void updateUser(User user) {
    // 先更新数据库
    userMapper.update(user);
    // 再查询数据（此时可能从缓存获取到旧数据）
    return userMapper.findById(user.getId());
}

// 正确示例
@Transactional
public User updateUser(User user) {
    // 更新数据库
    userMapper.update(user);
    // 清理缓存
    sqlSession.clearCache();
    // 重新查询
    return userMapper.findById(user.getId());
}
```

### 10.2 问题：缓存雪崩

**原因：**
- 大量缓存同时过期
- 系统启动时缓存未预热
- 缓存服务器故障

**解决方案：**
- 为缓存设置随机的过期时间
- 系统启动时预热热点数据
- 使用分布式缓存，提高可用性
- 实现缓存降级策略

**示例：**

```java
// 设置随机过期时间
public void setCacheWithRandomExpiry(String key, Object value) {
    int baseExpiry = 3600; // 基础过期时间（秒）
    int randomExpiry = ThreadLocalRandom.current().nextInt(0, 300); // 随机过期时间（0-5分钟）
    redisTemplate.opsForValue().set(key, value, baseExpiry + randomExpiry, TimeUnit.SECONDS);
}
```

### 10.3 问题：缓存穿透

**原因：**
- 查询不存在的数据，缓存和数据库都未命中
- 恶意攻击，大量查询不存在的数据

**解决方案：**
- 对不存在的数据也进行缓存（缓存空值）
- 使用布隆过滤器过滤不存在的数据
- 限制查询频率，防止恶意攻击

**示例：**

```java
public User findUserById(int id) {
    String key = "user:" + id;
    User user = (User) redisTemplate.opsForValue().get(key);
    
    if (user == null) {
        // 从数据库查询
        user = userMapper.findById(id);
        if (user != null) {
            // 缓存存在的数据
            redisTemplate.opsForValue().set(key, user, 3600, TimeUnit.SECONDS);
        } else {
            // 缓存空值，防止缓存穿透
            redisTemplate.opsForValue().set(key, null, 60, TimeUnit.SECONDS);
        }
    }
    
    return user;
}
```

### 10.4 问题：缓存击穿

**原因：**
- 热点数据过期，导致大量请求同时访问数据库

**解决方案：**
- 使用互斥锁，只允许一个线程更新缓存
- 延长热点数据的缓存时间
- 定期刷新热点数据，避免过期

**示例：**

```java
public User findHotUser(int id) {
    String key = "hot_user:" + id;
    User user = (User) redisTemplate.opsForValue().get(key);
    
    if (user == null) {
        // 使用互斥锁，只允许一个线程更新缓存
        String lockKey = "lock:user:" + id;
        if (redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 3, TimeUnit.SECONDS)) {
            try {
                // 从数据库查询
                user = userMapper.findById(id);
                if (user != null) {
                    // 缓存热点数据，时间较长
                    redisTemplate.opsForValue().set(key, user, 36000, TimeUnit.SECONDS);
                }
            } finally {
                // 释放锁
                redisTemplate.delete(lockKey);
            }
        } else {
            // 其他线程等待一段时间后重试
            Thread.sleep(100);
            return findHotUser(id);
        }
    }
    
    return user;
}
```

### 10.5 问题：序列化失败

**原因：**
- 缓存对象未实现Serializable接口
- 序列化对象包含不可序列化的字段
- 序列化和反序列化版本不兼容

**解决方案：**
- 确保缓存对象实现Serializable接口
- 对不可序列化的字段使用transient关键字
- 保持序列化和反序列化版本一致
- 使用更高效的序列化方式

**示例：**

```java
// 正确实现
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private transient String password; // 敏感字段，不序列化
    
    // getter和setter方法
}
```

## 11. 缓存的实际应用案例

### 11.1 案例1：电商系统商品缓存

**需求：**
- 商品详情页访问量大
- 商品信息更新频率相对较低
- 需要保证数据一致性

**实现：**

```xml
<mapper namespace="com.example.mapper.ProductMapper">
    <cache 
        eviction="LRU" 
        flushInterval="1800000" 
        size="5000" 
        readOnly="true"/>
        
    <select id="findById" parameterType="int" resultType="Product">
        SELECT * FROM products WHERE id = #{id}
    </select>
    
    <update id="update" parameterType="Product">
        UPDATE products SET name = #{name}, price = #{price} WHERE id = #{id}
    </update>
</mapper>
```

```java
@Service
public class ProductService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    @Cacheable(value = "product", key = "#id")
    public Product findProductById(int id) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectOne("com.example.mapper.ProductMapper.findById", id);
        } finally {
            sqlSession.close();
        }
    }
    
    @CacheEvict(value = "product", key = "#product.id")
    public void updateProduct(Product product) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            sqlSession.update("com.example.mapper.ProductMapper.update", product);
            sqlSession.commit();
        } finally {
            sqlSession.close();
        }
    }
}
```

### 11.2 案例2：用户会话缓存

**需求：**
- 用户登录后，会话信息需要快速访问
- 会话信息更新频率较低
- 需要支持多服务器部署

**实现：**

```xml
<mapper namespace="com.example.mapper.UserMapper">
    <cache type="org.mybatis.caches.redis.RedisCache"/>
    
    <select id="findById" parameterType="int" resultType="User">
        SELECT * FROM users WHERE id = #{id}
    </select>
</mapper>
```

```java
@Service
public class UserService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    public User getUserSession(int userId) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            return sqlSession.selectOne("com.example.mapper.UserMapper.findById", userId);
        } finally {
            sqlSession.close();
        }
    }
    
    // 登录时将用户信息存入Redis
    public void login(User user) {
        // 生成token
        String token = generateToken();
        // 将用户信息存入Redis
        redisTemplate.opsForValue().set("session:" + token, user, 3600, TimeUnit.SECONDS);
        // 返回token给客户端
    }
    
    // 验证会话
    public User validateSession(String token) {
        return (User) redisTemplate.opsForValue().get("session:" + token);
    }
}
```

### 11.3 案例3：报表数据缓存

**需求：**
- 报表数据计算复杂，耗时较长
- 报表数据更新频率较低（如每日更新）
- 需要快速响应报表查询请求

**实现：**

```xml
<mapper namespace="com.example.mapper.ReportMapper">
    <cache 
        eviction="LRU" 
        flushInterval="86400000" 
        size="1000" 
        readOnly="true"/>
        
    <select id="getSalesReport" parameterType="map" resultType="SalesReport">
        SELECT 
            DATE(order_time) as date,
            SUM(amount) as totalAmount,
            COUNT(*) as orderCount
        FROM orders
        WHERE order_time BETWEEN #{startDate} AND #{endDate}
        GROUP BY DATE(order_time)
        ORDER BY date
    </select>
</mapper>
```

```java
@Service
public class ReportService {
    @Autowired
    private SqlSessionFactory sqlSessionFactory;
    
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点刷新缓存
    public void refreshReportCache() {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            // 预加载今日报表数据
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", LocalDate.now().minusDays(30));
            params.put("endDate", LocalDate.now());
            sqlSession.selectList("com.example.mapper.ReportMapper.getSalesReport", params);
        } finally {
            sqlSession.close();
        }
    }
    
    public List<SalesReport> getSalesReport(LocalDate startDate, LocalDate endDate) {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("startDate", startDate);
            params.put("endDate", endDate);
            return sqlSession.selectList("com.example.mapper.ReportMapper.getSalesReport", params);
        } finally {
            sqlSession.close();
        }
    }
}
```

## 12. 总结

MyBatis的缓存机制是提高系统性能的重要手段，通过合理配置和使用缓存，可以显著减少数据库访问次数，提高查询响应速度。MyBatis提供了多级缓存架构：

1. **一级缓存（Session缓存）**：默认开启，作用域为SqlSession，适合同一事务内的多次查询。

2. **二级缓存（Namespace缓存）**：默认关闭，需要手动开启，作用域为Mapper Namespace，多个SqlSession可以共享缓存。

3. **第三方缓存**：支持集成Redis、Ehcache、Caffeine等第三方缓存框架，提供更强大的缓存功能。

使用缓存的最佳实践：
- 根据业务需求选择合适的缓存策略
- 设置合理的缓存大小和过期时间
- 保证缓存与数据库的一致性
- 监控缓存性能和命中率
- 避免缓存穿透、缓存雪崩等问题

通过掌握MyBatis的缓存机制，可以构建高性能、高可靠性的应用系统，提升用户体验，降低系统运行成本。