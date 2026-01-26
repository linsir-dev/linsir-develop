# 基于数据库如何实现分布式锁？有什么缺陷？

## 基于数据库实现分布式锁的方案

### 1. 基于数据库表实现

#### 1.1 创建锁表

```sql
CREATE TABLE `distributed_lock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lock_name` varchar(255) NOT NULL COMMENT '锁名称',
  `lock_value` varchar(255) NOT NULL COMMENT '锁持有者标识',
  `expire_time` datetime NOT NULL COMMENT '锁过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lock_name` (`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分布式锁表';
```

#### 1.2 获取锁

```java
public boolean tryLock(String lockName, String lockValue, int expireSeconds) {
    String sql = "INSERT INTO distributed_lock (lock_name, lock_value, expire_time) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? SECOND))";
    try {
        int result = jdbcTemplate.update(sql, lockName, lockValue, expireSeconds);
        return result > 0;
    } catch (DuplicateKeyException e) {
        return false;
    }
}
```

#### 1.3 释放锁

```java
public boolean unlock(String lockName, String lockValue) {
    String sql = "DELETE FROM distributed_lock WHERE lock_name = ? AND lock_value = ?";
    int result = jdbcTemplate.update(sql, lockName, lockValue);
    return result > 0;
}
```

#### 1.4 续期锁

```java
public boolean renewLock(String lockName, String lockValue, int expireSeconds) {
    String sql = "UPDATE distributed_lock SET expire_time = DATE_ADD(NOW(), INTERVAL ? SECOND) WHERE lock_name = ? AND lock_value = ?";
    int result = jdbcTemplate.update(sql, expireSeconds, lockName, lockValue);
    return result > 0;
}
```

### 2. 基于数据库乐观锁实现

#### 2.1 资源表添加版本号字段

```sql
CREATE TABLE `resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `count` int(11) NOT NULL DEFAULT 0,
  `version` int(11) NOT NULL DEFAULT 0 COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 2.2 获取锁并更新资源

```java
public boolean updateResourceWithLock(Long resourceId, int delta) {
    while (true) {
        Resource resource = resourceRepository.findById(resourceId);
        int oldVersion = resource.getVersion();
        int newCount = resource.getCount() + delta;
        
        if (newCount < 0) {
            return false;
        }
        
        String sql = "UPDATE resource SET count = ?, version = version + 1 WHERE id = ? AND version = ?";
        int result = jdbcTemplate.update(sql, newCount, resourceId, oldVersion);
        
        if (result > 0) {
            return true;
        }
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
```

### 3. 基于数据库悲观锁实现

#### 3.1 使用SELECT ... FOR UPDATE

```java
@Transactional
public boolean updateResourceWithPessimisticLock(Long resourceId, int delta) {
    String sql = "SELECT * FROM resource WHERE id = ? FOR UPDATE";
    Resource resource = jdbcTemplate.queryForObject(sql, new Object[]{resourceId}, new BeanPropertyRowMapper<>(Resource.class));
    
    int newCount = resource.getCount() + delta;
    if (newCount < 0) {
        return false;
    }
    
    resource.setCount(newCount);
    resourceRepository.save(resource);
    return true;
}
```

#### 3.2 使用行锁

```java
@Transactional
public boolean lockRow(String lockName, String lockValue, int expireSeconds) {
    String sql = "SELECT * FROM distributed_lock WHERE lock_name = ? FOR UPDATE";
    try {
        DistributedLock lock = jdbcTemplate.queryForObject(sql, new Object[]{lockName}, new BeanPropertyRowMapper<>(DistributedLock.class));
        
        if (lock.getExpireTime().before(new Date())) {
            String updateSql = "UPDATE distributed_lock SET lock_value = ?, expire_time = DATE_ADD(NOW(), INTERVAL ? SECOND) WHERE lock_name = ?";
            jdbcTemplate.update(updateSql, lockValue, expireSeconds, lockName);
            return true;
        }
        return false;
    } catch (EmptyResultDataAccessException e) {
        String insertSql = "INSERT INTO distributed_lock (lock_name, lock_value, expire_time) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? SECOND))";
        jdbcTemplate.update(insertSql, lockName, lockValue, expireSeconds);
        return true;
    }
}
```

### 4. 基于数据库唯一索引实现

#### 4.1 创建唯一索引

```sql
CREATE TABLE `distributed_lock_v2` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lock_name` varchar(255) NOT NULL COMMENT '锁名称',
  `lock_value` varchar(255) NOT NULL COMMENT '锁持有者标识',
  `expire_time` datetime NOT NULL COMMENT '锁过期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lock_name` (`lock_name`),
  KEY `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

#### 4.2 获取锁

```java
public boolean tryLock(String lockName, String lockValue, int expireSeconds) {
    try {
        String sql = "INSERT INTO distributed_lock_v2 (lock_name, lock_value, expire_time) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? SECOND))";
        jdbcTemplate.update(sql, lockName, lockValue, expireSeconds);
        return true;
    } catch (DuplicateKeyException e) {
        return false;
    }
}
```

#### 4.3 释放锁

```java
public boolean unlock(String lockName, String lockValue) {
    String sql = "DELETE FROM distributed_lock_v2 WHERE lock_name = ? AND lock_value = ?";
    int result = jdbcTemplate.update(sql, lockName, lockValue);
    return result > 0;
}
```

## 基于数据库实现分布式锁的缺陷

### 1. 性能问题

#### 1.1 数据库压力大

- 每次获取锁和释放锁都需要访问数据库
- 高并发场景下，数据库会成为性能瓶颈
- 数据库连接池资源消耗大

#### 1.2 响应时间长

- 数据库操作涉及网络IO、磁盘IO
- 相比Redis等内存数据库，响应时间较长
- 在分布式环境下，网络延迟会进一步增加响应时间

### 2. 可靠性问题

#### 2.1 单点故障

- 数据库是单点，一旦数据库宕机，所有锁服务不可用
- 即使是主从架构，主从切换期间也会导致锁服务不可用
- 需要额外的高可用架构来保证数据库的可靠性

#### 2.2 死锁问题

- 如果客户端获取锁后崩溃，没有及时释放锁
- 虽然可以设置过期时间，但过期时间设置不合理会导致问题
- 过期时间太短，业务还没执行完锁就过期了
- 过期时间太长，客户端崩溃后锁长时间无法释放

#### 2.3 锁误释放

- 如果多个客户端使用相同的lockValue，可能导致误删锁
- 需要保证lockValue的唯一性
- 释放锁时需要验证lockValue是否匹配

### 3. 并发问题

#### 3.1 并发获取锁失败率高

- 在高并发场景下，多个客户端同时尝试获取锁
- 只有一个客户端能成功，其他客户端都会失败
- 失败的客户端需要重试，增加系统负载

#### 3.2 锁竞争激烈

- 如果锁的粒度太粗，会导致大量客户端竞争同一个锁
- 需要合理设计锁的粒度，减少锁竞争

### 4. 扩展性问题

#### 4.1 水平扩展困难

- 数据库的水平扩展相对复杂
- 分库分表会增加锁实现的复杂度
- 跨库事务的实现难度大

#### 4.2 容量限制

- 数据库的连接数有限
- 高并发场景下，连接数可能成为瓶颈
- 需要优化连接池配置

### 5. 运维问题

#### 5.1 锁表维护

- 需要定期清理过期的锁记录
- 需要监控锁表的大小和性能
- 需要处理锁表的异常情况

#### 5.2 监控困难

- 难以实时监控锁的获取和释放情况
- 难以统计锁的等待时间和持有时间
- 难以定位锁相关的性能问题

### 6. 功能限制

#### 6.1 不支持可重入锁

- 基于数据库的方案难以实现可重入锁
- 需要额外的设计来支持可重入

#### 6.2 不支持公平锁

- 难以实现按照请求顺序获取锁
- 客户端获取锁的顺序是随机的

#### 6.3 不支持锁续期

- 虽然可以手动实现锁续期，但实现复杂
- 需要额外的定时任务来续期
- 续期失败可能导致锁过期

## 优化方案

### 1. 使用连接池

```java
@Bean
public DataSource dataSource() {
    HikariDataSource dataSource = new HikariDataSource();
    dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/lock_db");
    dataSource.setUsername("root");
    dataSource.setPassword("password");
    dataSource.setMaximumPoolSize(50);
    dataSource.setMinimumIdle(10);
    dataSource.setConnectionTimeout(30000);
    return dataSource;
}
```

### 2. 使用缓存

```java
public boolean tryLockWithCache(String lockName, String lockValue, int expireSeconds) {
    String cacheKey = "lock:" + lockName;
    Boolean cached = redisTemplate.opsForValue().setIfAbsent(cacheKey, lockValue, expireSeconds, TimeUnit.SECONDS);
    if (cached != null && cached) {
        return true;
    }
    return tryLock(lockName, lockValue, expireSeconds);
}
```

### 3. 使用异步处理

```java
@Async
public CompletableFuture<Boolean> tryLockAsync(String lockName, String lockValue, int expireSeconds) {
    return CompletableFuture.supplyAsync(() -> tryLock(lockName, lockValue, expireSeconds));
}
```

### 4. 使用批量操作

```java
public boolean tryLockBatch(List<String> lockNames, String lockValue, int expireSeconds) {
    String sql = "INSERT INTO distributed_lock (lock_name, lock_value, expire_time) VALUES (?, ?, DATE_ADD(NOW(), INTERVAL ? SECOND))";
    try {
        jdbcTemplate.batchUpdate(sql, lockNames.stream()
            .map(lockName -> new Object[]{lockName, lockValue, expireSeconds})
            .collect(Collectors.toList()));
        return true;
    } catch (DuplicateKeyException e) {
        return false;
    }
}
```

## 总结

基于数据库实现分布式锁是一种简单直观的方案，适用于并发量不高的场景。但是，这种方案存在性能差、可靠性低、扩展性差等问题。在高并发场景下，建议使用Redis、Zookeeper等专门的分布式锁方案。

如果必须使用数据库实现分布式锁，可以考虑以下优化措施：
1. 使用连接池优化数据库连接
2. 使用缓存减少数据库访问
3. 使用异步处理提高并发能力
4. 使用批量操作减少数据库访问次数
5. 合理设置锁的过期时间
6. 实现锁续期机制
7. 监控锁的使用情况，及时发现问题
