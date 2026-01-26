# 如何保证接口的幂等性？

## 一、接口幂等性保证方案概述

接口幂等性是分布式系统设计中的重要问题，需要从多个层面进行保证。本文将介绍多种保证接口幂等性的方案，包括数据库层面、缓存层面、业务层面、消息队列层面等。

### 1.1 幂等性保证方案分类

**数据库层面**
- 唯一索引
- 乐观锁
- 悲观锁

**缓存层面**
- Redis缓存
- 分布式锁
- Token机制

**业务层面**
- 状态机
- 业务逻辑判断
- 去重表

**消息队列层面**
- 消息去重
- 消息幂等消费
- 事务消息

### 1.2 方案选择原则

**性能优先**
- 选择性能最优的方案
- 避免过度设计
- 合理使用缓存

**可靠性优先**
- 保证数据一致性
- 避免数据丢失
- 保证系统稳定

**复杂度优先**
- 选择简单易实现的方案
- 降低系统复杂度
- 提高可维护性

## 二、数据库层面方案

### 2.1 唯一索引

**原理**
- 在数据库表中创建唯一索引
- 利用数据库的唯一约束保证数据唯一性
- 如果插入重复数据，数据库会抛出异常

**实现步骤**
1. 在数据库表中创建唯一索引
2. 插入数据时捕获唯一索引异常
3. 如果捕获到异常，则查询已存在的数据

**实现示例**

```sql
-- 创建订单表，添加唯一索引
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    public Order createOrder(OrderCreateDTO dto) {
        try {
            Order order = new Order();
            order.setOrderNo(dto.getOrderNo());
            order.setUserId(dto.getUserId());
            order.setProductId(dto.getProductId());
            order.setAmount(dto.getAmount());
            order.setStatus(OrderStatus.PENDING);
            orderMapper.insert(order);
            return order;
        } catch (DuplicateKeyException e) {
            log.warn("订单已存在，订单号：{}", dto.getOrderNo());
            return orderMapper.getByOrderNo(dto.getOrderNo());
        }
    }
}
```

**优缺点**
- 优点：实现简单，可靠性高
- 缺点：依赖数据库，性能较低

**适用场景**
- 订单创建
- 支付记录
- 用户注册

### 2.2 乐观锁

**原理**
- 为数据添加版本号字段
- 更新数据时检查版本号
- 如果版本号不匹配，则拒绝更新

**实现步骤**
1. 在数据库表中添加版本号字段
2. 查询数据时获取版本号
3. 更新数据时检查版本号

**实现示例**

```sql
-- 创建订单表，添加版本号字段
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `version` int(11) NOT NULL DEFAULT '0' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderMapper.selectById(orderId);
        
        int result = orderMapper.updateStatus(orderId, status, order.getVersion());
        if (result == 0) {
            throw new RuntimeException("订单状态更新失败，可能已被其他请求修改");
        }
        
        return orderMapper.selectById(orderId);
    }
}

@Mapper
public interface OrderMapper {
    
    @Update("UPDATE `order` SET status = #{status}, version = version + 1 WHERE id = #{id} AND version = #{version}")
    int updateStatus(@Param("id") Long id, @Param("status") OrderStatus status, @Param("version") Integer version);
}
```

**优缺点**
- 优点：性能较好，不会阻塞
- 缺点：需要重试机制，冲突时需要重试

**适用场景**
- 订单状态更新
- 库存扣减
- 积分增加

### 2.3 悲观锁

**原理**
- 使用SELECT ... FOR UPDATE获取排他锁
- 锁定数据后其他请求无法修改
- 提交事务后释放锁

**实现步骤**
1. 使用SELECT ... FOR UPDATE查询数据
2. 检查数据状态
3. 更新数据
4. 提交事务

**实现示例**

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Transactional
    public Order updateStatus(Long orderId, OrderStatus status) {
        Order order = orderMapper.selectByIdForUpdate(orderId);
        
        if (order.getStatus() == status) {
            return order;
        }
        
        order.setStatus(status);
        orderMapper.updateById(order);
        
        return order;
    }
}

@Mapper
public interface OrderMapper {
    
    @Select("SELECT * FROM `order` WHERE id = #{id} FOR UPDATE")
    Order selectByIdForUpdate(@Param("id") Long id);
}
```

**优缺点**
- 优点：实现简单，可靠性高
- 缺点：性能较差，会阻塞其他请求

**适用场景**
- 订单状态更新
- 库存扣减
- 账户余额更新

## 三、缓存层面方案

### 3.1 Redis缓存

**原理**
- 使用Redis缓存请求结果
- 如果请求已处理，则直接返回缓存结果
- 如果请求未处理，则处理请求并缓存结果

**实现步骤**
1. 为每个请求生成唯一标识
2. 检查Redis缓存中是否存在请求结果
3. 如果存在，则直接返回缓存结果
4. 如果不存在，则处理请求并缓存结果

**实现示例**

```java
@Service
public class PaymentService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public Payment pay(PaymentDTO dto) {
        String requestId = dto.getRequestId();
        String key = "payment:" + requestId;
        
        Payment payment = (Payment) redisTemplate.opsForValue().get(key);
        if (payment != null) {
            log.info("支付记录已存在，请求ID：{}", requestId);
            return payment;
        }
        
        payment = createPayment(dto);
        
        redisTemplate.opsForValue().set(key, payment, 1, TimeUnit.HOURS);
        
        return payment;
    }
    
    private Payment createPayment(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setRequestId(dto.getRequestId());
        payment.setOrderId(dto.getOrderId());
        payment.setAmount(dto.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentMapper.insert(payment);
        return payment;
    }
}
```

**优缺点**
- 优点：性能高，实现简单
- 缺点：依赖Redis，需要考虑缓存失效

**适用场景**
- 支付接口
- 订单创建
- 积分增加

### 3.2 分布式锁

**原理**
- 使用分布式锁保证同一时间只有一个请求执行
- 如果获取锁失败，则说明请求重复
- 如果获取锁成功，则执行业务逻辑

**实现步骤**
1. 为每个请求生成唯一标识
2. 尝试获取分布式锁
3. 如果获取锁失败，则查询已处理的结果
4. 如果获取锁成功，则处理请求并释放锁

**实现示例**

```java
@Service
public class PaymentService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public Payment pay(PaymentDTO dto) {
        String lockKey = "payment_lock:" + dto.getOrderId();
        String requestId = UUID.randomUUID().toString();
        
        try {
            boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey, 
                requestId, 
                10, 
                TimeUnit.SECONDS
            );
            
            if (!locked) {
                log.info("获取分布式锁失败，订单ID：{}", dto.getOrderId());
                return paymentMapper.getByOrderId(dto.getOrderId());
            }
            
            Payment payment = paymentMapper.getByOrderId(dto.getOrderId());
            if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
                return payment;
            }
            
            payment = createPayment(dto);
            return payment;
            
        } finally {
            releaseLock(lockKey, requestId);
        }
    }
    
    private void releaseLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Collections.singletonList(lockKey), requestId);
    }
}
```

**优缺点**
- 优点：可靠性高，适用于分布式环境
- 缺点：性能较低，依赖Redis

**适用场景**
- 支付接口
- 订单创建
- 库存扣减

### 3.3 Token机制

**原理**
- 客户端请求前先获取Token
- 提交请求时携带Token
- 服务器验证Token，如果Token已使用，则拒绝请求

**实现步骤**
1. 客户端请求获取Token接口
2. 服务器生成Token并缓存
3. 客户端提交请求时携带Token
4. 服务器验证Token，如果Token有效，则处理请求并删除Token

**实现示例**

```java
@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @GetMapping
    public Result<String> getToken() {
        String token = UUID.randomUUID().toString();
        String key = "token:" + token;
        redisTemplate.opsForValue().set(key, "1", 1, TimeUnit.HOURS);
        return Result.success(token);
    }
}

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @PostMapping
    public Result<PaymentVO> pay(@RequestBody @Valid PaymentDTO dto, @RequestHeader("X-Token") String token) {
        String key = "token:" + token;
        
        Boolean exists = redisTemplate.hasKey(key);
        if (!exists) {
            return Result.fail("Token无效或已过期");
        }
        
        redisTemplate.delete(key);
        
        Payment payment = paymentService.pay(dto);
        PaymentVO vo = BeanUtil.copyProperties(payment, PaymentVO.class);
        return Result.success(vo);
    }
}
```

**优缺点**
- 优点：实现简单，适用于前端防重复提交
- 缺点：需要前端配合，不适用于后端重试

**适用场景**
- 表单提交
- 支付接口
- 订单创建

## 四、业务层面方案

### 4.1 状态机

**原理**
- 使用状态机管理业务状态
- 状态转换是单向的，不可逆
- 如果状态已经转换，则不允许重复转换

**实现步骤**
1. 定义业务状态
2. 定义状态转换规则
3. 检查当前状态
4. 如果状态已转换，则拒绝重复转换

**实现示例**

```java
public enum PaymentStatus {
    PENDING(0, "待支付"),
    SUCCESS(1, "支付成功"),
    FAILED(2, "支付失败"),
    REFUND(3, "已退款");
    
    private final Integer code;
    private final String desc;
    
    PaymentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public boolean canTransitionTo(PaymentStatus targetStatus) {
        switch (this) {
            case PENDING:
                return targetStatus == SUCCESS || targetStatus == FAILED;
            case SUCCESS:
                return targetStatus == REFUND;
            case FAILED:
                return false;
            case REFUND:
                return false;
            default:
                return false;
        }
    }
}

@Service
public class PaymentService {
    
    public Payment pay(PaymentDTO dto) {
        Payment payment = paymentMapper.getByOrderId(dto.getOrderId());
        
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("支付已成功，订单ID：{}", dto.getOrderId());
            return payment;
        }
        
        if (!payment.getStatus().canTransitionTo(PaymentStatus.SUCCESS)) {
            throw new RuntimeException("当前状态不允许支付");
        }
        
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentMapper.updateById(payment);
        
        return payment;
    }
}
```

**优缺点**
- 优点：实现简单，业务逻辑清晰
- 缺点：需要定义完整的状态转换规则

**适用场景**
- 订单状态管理
- 支付状态管理
- 工单状态管理

### 4.2 业务逻辑判断

**原理**
- 通过业务逻辑判断请求是否重复
- 如果请求重复，则直接返回已处理的结果
- 如果请求不重复，则处理请求

**实现步骤**
1. 根据业务规则判断请求是否重复
2. 如果请求重复，则查询已处理的结果
3. 如果请求不重复，则处理请求

**实现示例**

```java
@Service
public class PaymentService {
    
    public Payment pay(PaymentDTO dto) {
        Payment payment = paymentMapper.getByOrderId(dto.getOrderId());
        
        if (payment != null && payment.getStatus() == PaymentStatus.SUCCESS) {
            log.info("支付已成功，订单ID：{}", dto.getOrderId());
            return payment;
        }
        
        if (payment != null && payment.getStatus() == PaymentStatus.FAILED) {
            log.info("支付已失败，订单ID：{}", dto.getOrderId());
            return payment;
        }
        
        payment = createPayment(dto);
        return payment;
    }
}
```

**优缺点**
- 优点：实现简单，不依赖外部系统
- 缺点：需要根据业务规则判断，可能不够准确

**适用场景**
- 支付接口
- 订单创建
- 积分增加

### 4.3 去重表

**原理**
- 创建去重表，记录已处理的请求
- 处理请求前先查询去重表
- 如果请求已处理，则直接返回已处理的结果

**实现步骤**
1. 创建去重表
2. 处理请求前先查询去重表
3. 如果请求已处理，则返回已处理的结果
4. 如果请求未处理，则处理请求并插入去重表

**实现示例**

```sql
-- 创建去重表
CREATE TABLE `request_deduplication` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `request_id` varchar(64) NOT NULL COMMENT '请求ID',
  `request_type` varchar(32) NOT NULL COMMENT '请求类型',
  `request_data` text COMMENT '请求数据',
  `response_data` text COMMENT '响应数据',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '处理状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请求去重表';
```

```java
@Service
public class PaymentService {
    
    @Autowired
    private RequestDeduplicationMapper deduplicationMapper;
    
    public Payment pay(PaymentDTO dto) {
        String requestId = dto.getRequestId();
        
        RequestDeduplication deduplication = deduplicationMapper.getByRequestId(requestId);
        if (deduplication != null) {
            log.info("请求已处理，请求ID：{}", requestId);
            return JSON.parseObject(deduplication.getResponseData(), Payment.class);
        }
        
        Payment payment = createPayment(dto);
        
        deduplication = new RequestDeduplication();
        deduplication.setRequestId(requestId);
        deduplication.setRequestType("PAYMENT");
        deduplication.setRequestData(JSON.toJSONString(dto));
        deduplication.setResponseData(JSON.toJSONString(payment));
        deduplication.setStatus(1);
        deduplicationMapper.insert(deduplication);
        
        return payment;
    }
}
```

**优缺点**
- 优点：实现简单，可靠性高
- 缺点：需要额外的去重表，增加存储成本

**适用场景**
- 支付接口
- 订单创建
- 积分增加

## 五、消息队列层面方案

### 5.1 消息去重

**原理**
- 为每条消息生成唯一标识
- 消费消息前先检查消息是否已消费
- 如果消息已消费，则直接跳过

**实现步骤**
1. 为每条消息生成唯一标识
2. 消费消息前先检查消息是否已消费
3. 如果消息已消费，则直接跳过
4. 如果消息未消费，则消费消息并记录

**实现示例**

```java
@Service
public class OrderService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @KafkaListener(topics = "order-create")
    public void createOrder(String message) {
        OrderMessage orderMessage = JSON.parseObject(message, OrderMessage.class);
        String messageId = orderMessage.getMessageId();
        String key = "message:" + messageId;
        
        Boolean exists = redisTemplate.hasKey(key);
        if (exists) {
            log.info("消息已消费，消息ID：{}", messageId);
            return;
        }
        
        try {
            Order order = new Order();
            order.setOrderNo(orderMessage.getOrderNo());
            order.setUserId(orderMessage.getUserId());
            order.setProductId(orderMessage.getProductId());
            order.setAmount(orderMessage.getAmount());
            order.setStatus(OrderStatus.PENDING);
            orderMapper.insert(order);
            
            redisTemplate.opsForValue().set(key, "1", 1, TimeUnit.DAYS);
            
        } catch (Exception e) {
            log.error("创建订单失败", e);
            throw e;
        }
    }
}
```

**优缺点**
- 优点：实现简单，可靠性高
- 缺点：依赖Redis，需要考虑缓存失效

**适用场景**
- 订单创建
- 支付处理
- 积分增加

### 5.2 消息幂等消费

**原理**
- 使用数据库的唯一约束保证消息幂等消费
- 如果消息已消费，则数据库会抛出异常
- 捕获异常后查询已消费的结果

**实现步骤**
1. 在数据库表中创建唯一约束
2. 消费消息时插入数据
3. 如果插入失败，则查询已消费的结果

**实现示例**

```sql
-- 创建订单表，添加唯一约束
CREATE TABLE `order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_no` varchar(64) NOT NULL COMMENT '订单号',
  `message_id` varchar(64) NOT NULL COMMENT '消息ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  UNIQUE KEY `uk_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';
```

```java
@Service
public class OrderService {
    
    @KafkaListener(topics = "order-create")
    public void createOrder(String message) {
        OrderMessage orderMessage = JSON.parseObject(message, OrderMessage.class);
        
        try {
            Order order = new Order();
            order.setOrderNo(orderMessage.getOrderNo());
            order.setMessageId(orderMessage.getMessageId());
            order.setUserId(orderMessage.getUserId());
            order.setProductId(orderMessage.getProductId());
            order.setAmount(orderMessage.getAmount());
            order.setStatus(OrderStatus.PENDING);
            orderMapper.insert(order);
            
        } catch (DuplicateKeyException e) {
            log.warn("订单已创建，消息ID：{}", orderMessage.getMessageId());
        }
    }
}
```

**优缺点**
- 优点：实现简单，可靠性高
- 缺点：依赖数据库，性能较低

**适用场景**
- 订单创建
- 支付处理
- 积分增加

### 5.3 事务消息

**原理**
- 使用事务消息保证消息的可靠投递
- 发送消息和业务操作在同一个事务中
- 如果业务操作失败，则回滚消息

**实现步骤**
1. 发送事务消息
2. 执行业务操作
3. 如果业务操作成功，则提交消息
4. 如果业务操作失败，则回滚消息

**实现示例**

```java
@Service
public class OrderService {
    
    @Autowired
    private TransactionMQProducer producer;
    
    @Transactional
    public void createOrder(OrderCreateDTO dto) {
        Order order = new Order();
        order.setOrderNo(dto.getOrderNo());
        order.setUserId(dto.getUserId());
        order.setProductId(dto.getProductId());
        order.setAmount(dto.getAmount());
        order.setStatus(OrderStatus.PENDING);
        orderMapper.insert(order);
        
        Message message = new Message("order-create", JSON.toJSONString(order).getBytes());
        producer.sendMessageInTransaction(message, null);
    }
}
```

**优缺点**
- 优点：可靠性高，保证消息不丢失
- 缺点：实现复杂，性能较低

**适用场景**
- 订单创建
- 支付处理
- 积分增加

## 六、方案对比

### 6.1 性能对比

| 方案 | 性能 | 适用场景 |
|------|------|----------|
| 唯一索引 | 中 | 订单创建、支付记录 |
| 乐观锁 | 高 | 订单状态更新、库存扣减 |
| 悲观锁 | 低 | 订单状态更新、库存扣减 |
| Redis缓存 | 高 | 支付接口、订单创建 |
| 分布式锁 | 中 | 支付接口、订单创建 |
| Token机制 | 高 | 表单提交、支付接口 |
| 状态机 | 高 | 订单状态管理、支付状态管理 |
| 业务逻辑判断 | 高 | 支付接口、订单创建 |
| 去重表 | 中 | 支付接口、订单创建 |
| 消息去重 | 高 | 订单创建、支付处理 |
| 消息幂等消费 | 中 | 订单创建、支付处理 |
| 事务消息 | 低 | 订单创建、支付处理 |

### 6.2 复杂度对比

| 方案 | 复杂度 | 适用场景 |
|------|--------|----------|
| 唯一索引 | 低 | 订单创建、支付记录 |
| 乐观锁 | 中 | 订单状态更新、库存扣减 |
| 悲观锁 | 低 | 订单状态更新、库存扣减 |
| Redis缓存 | 中 | 支付接口、订单创建 |
| 分布式锁 | 高 | 支付接口、订单创建 |
| Token机制 | 中 | 表单提交、支付接口 |
| 状态机 | 中 | 订单状态管理、支付状态管理 |
| 业务逻辑判断 | 低 | 支付接口、订单创建 |
| 去重表 | 中 | 支付接口、订单创建 |
| 消息去重 | 中 | 订单创建、支付处理 |
| 消息幂等消费 | 低 | 订单创建、支付处理 |
| 事务消息 | 高 | 订单创建、支付处理 |

### 6.3 可靠性对比

| 方案 | 可靠性 | 适用场景 |
|------|--------|----------|
| 唯一索引 | 高 | 订单创建、支付记录 |
| 乐观锁 | 中 | 订单状态更新、库存扣减 |
| 悲观锁 | 高 | 订单状态更新、库存扣减 |
| Redis缓存 | 中 | 支付接口、订单创建 |
| 分布式锁 | 高 | 支付接口、订单创建 |
| Token机制 | 中 | 表单提交、支付接口 |
| 状态机 | 高 | 订单状态管理、支付状态管理 |
| 业务逻辑判断 | 中 | 支付接口、订单创建 |
| 去重表 | 高 | 支付接口、订单创建 |
| 消息去重 | 中 | 订单创建、支付处理 |
| 消息幂等消费 | 高 | 订单创建、支付处理 |
| 事务消息 | 高 | 订单创建、支付处理 |

## 七、最佳实践

### 7.1 方案选择

**简单场景**
- 优先使用唯一索引
- 优先使用业务逻辑判断
- 优先使用状态机

**复杂场景**
- 优先使用Redis缓存
- 优先使用分布式锁
- 优先使用去重表

**分布式场景**
- 优先使用分布式锁
- 优先使用消息去重
- 优先使用事务消息

### 7.2 组合使用

**数据库+缓存**
- 使用数据库保证数据一致性
- 使用缓存提高性能

```java
@Service
public class PaymentService {
    
    public Payment pay(PaymentDTO dto) {
        String key = "payment:" + dto.getRequestId();
        
        Payment payment = (Payment) redisTemplate.opsForValue().get(key);
        if (payment != null) {
            return payment;
        }
        
        try {
            payment = createPayment(dto);
            redisTemplate.opsForValue().set(key, payment, 1, TimeUnit.HOURS);
            return payment;
        } catch (DuplicateKeyException e) {
            payment = paymentMapper.getByRequestId(dto.getRequestId());
            redisTemplate.opsForValue().set(key, payment, 1, TimeUnit.HOURS);
            return payment;
        }
    }
}
```

**分布式锁+状态机**
- 使用分布式锁保证并发安全
- 使用状态机保证业务逻辑

```java
@Service
public class PaymentService {
    
    public Payment pay(PaymentDTO dto) {
        String lockKey = "payment_lock:" + dto.getOrderId();
        String requestId = UUID.randomUUID().toString();
        
        try {
            boolean locked = redisTemplate.opsForValue().setIfAbsent(
                lockKey, 
                requestId, 
                10, 
                TimeUnit.SECONDS
            );
            
            if (!locked) {
                return paymentMapper.getByOrderId(dto.getOrderId());
            }
            
            Payment payment = paymentMapper.getByOrderId(dto.getOrderId());
            
            if (payment.getStatus() == PaymentStatus.SUCCESS) {
                return payment;
            }
            
            if (!payment.getStatus().canTransitionTo(PaymentStatus.SUCCESS)) {
                throw new RuntimeException("当前状态不允许支付");
            }
            
            payment.setStatus(PaymentStatus.SUCCESS);
            paymentMapper.updateById(payment);
            
            return payment;
            
        } finally {
            releaseLock(lockKey, requestId);
        }
    }
}
```

### 7.3 注意事项

**性能优化**
- 合理使用缓存
- 避免过度设计
- 选择合适的方案

**可靠性保证**
- 保证数据一致性
- 避免数据丢失
- 保证系统稳定

**监控告警**
- 监控接口调用次数
- 监控重复请求次数
- 监控接口响应时间

## 八、总结

接口幂等性是分布式系统设计中的重要问题，需要根据不同的业务场景选择合适的幂等性保证方案。

### 核心要点

1. **数据库层面**：唯一索引、乐观锁、悲观锁
2. **缓存层面**：Redis缓存、分布式锁、Token机制
3. **业务层面**：状态机、业务逻辑判断、去重表
4. **消息队列层面**：消息去重、消息幂等消费、事务消息

### 方案选择

1. **简单场景**：唯一索引、业务逻辑判断、状态机
2. **复杂场景**：Redis缓存、分布式锁、去重表
3. **分布式场景**：分布式锁、消息去重、事务消息

### 最佳实践

1. **组合使用**：数据库+缓存、分布式锁+状态机
2. **性能优化**：合理使用缓存、避免过度设计
3. **可靠性保证**：保证数据一致性、避免数据丢失
4. **监控告警**：监控接口调用次数、监控重复请求次数

通过合理设计和实现接口幂等性，可以有效保证系统的数据一致性和稳定性，提高系统的可靠性和用户体验。
