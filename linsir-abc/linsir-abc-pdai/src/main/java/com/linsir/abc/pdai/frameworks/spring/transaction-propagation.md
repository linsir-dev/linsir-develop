# Spring事务中有哪几种事务传播行为？

## 事务传播行为概述

**事务传播行为**（Transaction Propagation Behavior）定义了一个事务方法被另一个事务方法调用时，事务如何传播。具体来说，它规定了在以下场景中事务的行为：

1. 当一个事务方法调用另一个事务方法时
2. 当一个非事务方法调用一个事务方法时
3. 当一个事务方法调用一个非事务方法时

事务传播行为是Spring事务管理的核心概念之一，它允许开发者精细控制事务的边界和行为。

### 事务传播行为的重要性

事务传播行为的重要性体现在以下几个方面：

1. **控制事务边界**：精确控制事务的开始和结束
2. **处理嵌套事务**：解决方法嵌套调用时的事务问题
3. **优化性能**：避免不必要的事务创建和提交
4. **保证数据一致性**：确保业务逻辑的原子性
5. **处理异常情况**：合理处理事务中的异常

### Spring中的事务传播行为

Spring提供了七种事务传播行为，定义在`TransactionDefinition`接口中：

| 传播行为 | 常量 | 描述 |
|---------|------|------|
| REQUIRED | PROPAGATION_REQUIRED | 如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务 |
| SUPPORTS | PROPAGATION_SUPPORTS | 如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务方式执行 |
| MANDATORY | PROPAGATION_MANDATORY | 如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常 |
| REQUIRES_NEW | PROPAGATION_REQUIRES_NEW | 创建一个新的事务，如果当前存在事务，则暂停当前事务 |
| NOT_SUPPORTED | PROPAGATION_NOT_SUPPORTED | 以非事务方式执行操作，如果当前存在事务，则暂停当前事务 |
| NEVER | PROPAGATION_NEVER | 以非事务方式执行操作，如果当前存在事务，则抛出异常 |
| NESTED | PROPAGATION_NESTED | 如果当前存在事务，则在嵌套事务内执行；如果当前没有事务，则创建一个新的事务 |

## 详解Spring事务传播行为

### 1. REQUIRED (默认)

**定义**：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。

**特点**：
- 这是Spring默认的事务传播行为
- 最常用的传播行为
- 保证所有操作在同一个事务中执行

**适用场景**：
- 大多数业务操作
- 需要保证原子性的操作
- 方法调用链中的核心业务逻辑

**代码示例**：

```java
@Service
public class UserService {
    @Autowired
    private OrderService orderService;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void createUserWithOrder(User user, Order order) {
        // 创建用户
        saveUser(user);
        // 创建订单（如果当前有事务，会加入该事务）
        orderService.createOrder(order);
    }
    
    private void saveUser(User user) {
        // 保存用户逻辑
    }
}

@Service
public class OrderService {
    @Transactional(propagation = Propagation.REQUIRED)
    public void createOrder(Order order) {
        // 创建订单逻辑
    }
}
```

**执行流程**：
1. 调用`createUserWithOrder`，创建新事务
2. 执行`saveUser`，在当前事务中
3. 调用`createOrder`，加入当前事务
4. 所有操作在同一个事务中提交或回滚

### 2. SUPPORTS

**定义**：如果当前存在事务，则加入该事务；如果当前没有事务，则以非事务方式执行。

**特点**：
- 支持事务，但不强制要求
- 依赖于调用方的事务状态
- 适合查询操作

**适用场景**：
- 只读操作
- 报表查询
- 数据统计
- 不修改数据的操作

**代码示例**：

```java
@Service
public class UserService {
    @Autowired
    private ReportService reportService;
    
    @Transactional
    public void processUserAndGenerateReport(User user) {
        // 处理用户（在事务中）
        updateUser(user);
        // 生成报表（如果当前有事务，会加入该事务）
        reportService.generateUserReport(user.getId());
    }
    
    private void updateUser(User user) {
        // 更新用户逻辑
    }
}

@Service
public class ReportService {
    @Transactional(propagation = Propagation.SUPPORTS)
    public Report generateUserReport(Long userId) {
        // 生成报表逻辑（只读操作）
        return report;
    }
}
```

**执行流程**：
1. 调用`processUserAndGenerateReport`，创建新事务
2. 执行`updateUser`，在当前事务中
3. 调用`generateUserReport`，加入当前事务
4. 所有操作在同一个事务中提交

### 3. MANDATORY

**定义**：如果当前存在事务，则加入该事务；如果当前没有事务，则抛出异常。

**特点**：
- 强制要求存在事务
- 确保操作必须在事务中执行
- 适合关键业务操作

**适用场景**：
- 关键业务操作
- 必须在事务中执行的操作
- 依赖于其他事务的操作

**代码示例**：

```java
@Service
public class UserService {
    @Autowired
    private AuditService auditService;
    
    @Transactional
    public void updateUser(User user) {
        // 更新用户
        saveUser(user);
        // 记录审计日志（必须在事务中执行）
        auditService.logUserUpdate(user);
    }
    
    private void saveUser(User user) {
        // 保存用户逻辑
    }
}

@Service
public class AuditService {
    @Transactional(propagation = Propagation.MANDATORY)
    public void logUserUpdate(User user) {
        // 记录审计日志逻辑
    }
}
```

**执行流程**：
1. 调用`updateUser`，创建新事务
2. 执行`saveUser`，在当前事务中
3. 调用`logUserUpdate`，加入当前事务
4. 所有操作在同一个事务中提交

**异常情况**：
- 如果直接调用`logUserUpdate`（没有事务），会抛出异常

### 4. REQUIRES_NEW

**定义**：创建一个新的事务，如果当前存在事务，则暂停当前事务。

**特点**：
- 总是创建新事务
- 与外部事务完全隔离
- 外部事务的回滚不影响内部事务
- 内部事务的异常可以选择是否影响外部事务

**适用场景**：
- 日志记录
- 消息发送
- 独立的业务操作
- 需要确保即使外部事务失败也能执行的操作

**代码示例**：

```java
@Service
public class OrderService {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LogService logService;
    
    @Transactional
    public void processOrder(Order order) {
        try {
            // 处理订单
            saveOrder(order);
            // 处理支付
            paymentService.processPayment(order);
        } catch (Exception e) {
            // 记录错误日志（即使订单处理失败也要记录）
            logService.logError("Order processing failed: " + e.getMessage());
            throw e;
        }
    }
    
    private void saveOrder(Order order) {
        // 保存订单逻辑
    }
}

@Service
public class LogService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(String message) {
        // 记录错误日志逻辑
    }
}
```

**执行流程**：
1. 调用`processOrder`，创建新事务（事务A）
2. 执行`saveOrder`，在事务A中
3. 调用`processPayment`，在事务A中
4. 发生异常，进入catch块
5. 调用`logError`，暂停事务A，创建新事务（事务B）
6. 执行日志记录，提交事务B
7. 恢复事务A，抛出异常，回滚事务A

### 5. NOT_SUPPORTED

**定义**：以非事务方式执行操作，如果当前存在事务，则暂停当前事务。

**特点**：
- 强制以非事务方式执行
- 暂停当前事务（如果存在）
- 适合不需要事务的操作

**适用场景**：
- 耗时操作
- 外部系统调用
- 缓存更新
- 不需要事务的操作

**代码示例**：

```java
@Service
public class UserService {
    @Autowired
    private ExternalService externalService;
    
    @Transactional
    public void updateUserAndCallExternalSystem(User user) {
        // 更新用户（在事务中）
        saveUser(user);
        // 调用外部系统（以非事务方式执行）
        externalService.notifyExternalSystem(user.getId());
    }
    
    private void saveUser(User user) {
        // 保存用户逻辑
    }
}

@Service
public class ExternalService {
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void notifyExternalSystem(Long userId) {
        // 调用外部系统逻辑（可能耗时较长）
    }
}
```

**执行流程**：
1. 调用`updateUserAndCallExternalSystem`，创建新事务（事务A）
2. 执行`saveUser`，在事务A中
3. 调用`notifyExternalSystem`，暂停事务A
4. 执行外部系统调用（非事务）
5. 恢复事务A，提交事务A

### 6. NEVER

**定义**：以非事务方式执行操作，如果当前存在事务，则抛出异常。

**特点**：
- 强制以非事务方式执行
- 不允许在事务中执行
- 适合绝对不能在事务中执行的操作

**适用场景**：
- 性能敏感的操作
- 不允许事务影响的操作
- 不需要事务的操作
- 测试代码

**代码示例**：

```java
@Service
public class PerformanceService {
    @Transactional(propagation = Propagation.NEVER)
    public void performHighSpeedOperation() {
        // 高性能操作逻辑
    }
}

// 错误用法示例
@Service
public class TestService {
    @Autowired
    private PerformanceService performanceService;
    
    @Transactional
    public void testOperation() {
        // 这会抛出异常，因为performanceService.performHighSpeedOperation()不允许在事务中执行
        performanceService.performHighSpeedOperation();
    }
}
```

**执行流程**：
- 如果在事务中调用`performHighSpeedOperation`，会抛出异常
- 如果以非事务方式调用，正常执行

### 7. NESTED

**定义**：如果当前存在事务，则在嵌套事务内执行；如果当前没有事务，则创建一个新的事务。

**特点**：
- 创建嵌套事务
- 外部事务是内部事务的父事务
- 内部事务的提交依赖于外部事务
- 内部事务可以独立回滚
- 支持保存点（savepoint）

**适用场景**：
- 复杂业务流程
- 部分操作需要独立回滚的场景
- 分层业务逻辑
- 需要保存点的场景

**代码示例**：

```java
@Service
public class OrderService {
    @Autowired
    private InventoryService inventoryService;
    
    @Transactional
    public void processOrderWithInventoryCheck(Order order) {
        // 创建订单
        createOrder(order);
        try {
            // 检查库存（在嵌套事务中）
            inventoryService.checkAndReserveInventory(order);
        } catch (InventoryException e) {
            // 库存检查失败，只回滚嵌套事务
            // 订单仍然保留
            logger.warn("Inventory check failed: " + e.getMessage());
        }
        // 继续处理其他逻辑
    }
    
    private void createOrder(Order order) {
        // 创建订单逻辑
    }
}

@Service
public class InventoryService {
    @Transactional(propagation = Propagation.NESTED)
    public void checkAndReserveInventory(Order order) {
        // 检查库存
        if (!hasSufficientInventory(order)) {
            throw new InventoryException("Insufficient inventory");
        }
        // 预留库存
        reserveInventory(order);
    }
    
    private boolean hasSufficientInventory(Order order) {
        // 检查库存逻辑
        return true;
    }
    
    private void reserveInventory(Order order) {
        // 预留库存逻辑
    }
}
```

**执行流程**：
1. 调用`processOrderWithInventoryCheck`，创建新事务（事务A）
2. 执行`createOrder`，在事务A中
3. 调用`checkAndReserveInventory`，创建嵌套事务（事务B）
4. 如果库存不足，抛出异常，回滚事务B
5. 事务A继续执行，提交订单
6. 最终事务A提交

## 事务传播行为的对比

### 1. 事务创建对比

| 传播行为 | 当前无事务 | 当前有事务 |
|---------|-----------|-----------|
| REQUIRED | 创建新事务 | 加入当前事务 |
| SUPPORTS | 非事务执行 | 加入当前事务 |
| MANDATORY | 抛出异常 | 加入当前事务 |
| REQUIRES_NEW | 创建新事务 | 暂停当前事务，创建新事务 |
| NOT_SUPPORTED | 非事务执行 | 暂停当前事务，非事务执行 |
| NEVER | 非事务执行 | 抛出异常 |
| NESTED | 创建新事务 | 创建嵌套事务 |

### 2. 事务隔离对比

| 传播行为 | 事务隔离 | 异常处理 |
|---------|---------|----------|
| REQUIRED | 与外部事务同隔离 | 外部事务回滚，内部操作也回滚 |
| SUPPORTS | 依赖外部事务 | 外部事务回滚，内部操作也回滚（如果在事务中） |
| MANDATORY | 与外部事务同隔离 | 外部事务回滚，内部操作也回滚 |
| REQUIRES_NEW | 独立事务隔离 | 内部事务独立提交/回滚，不影响外部事务 |
| NOT_SUPPORTED | 无事务 | 不受外部事务影响 |
| NEVER | 无事务 | 不受外部事务影响 |
| NESTED | 外部事务的子事务 | 内部事务可独立回滚，不影响外部事务 |

### 3. 适用场景对比

| 传播行为 | 适用场景 | 推荐使用 |
|---------|---------|----------|
| REQUIRED | 大多数业务操作 | 是（默认） |
| SUPPORTS | 只读操作 | 是（查询方法） |
| MANDATORY | 必须在事务中执行的操作 | 是（关键操作） |
| REQUIRES_NEW | 需要独立事务的操作 | 是（日志、消息） |
| NOT_SUPPORTED | 不需要事务的耗时操作 | 是（外部调用） |
| NEVER | 绝对不能在事务中执行的操作 | 是（性能敏感操作） |
| NESTED | 复杂业务流程，需要部分回滚 | 是（复杂业务） |

## 如何在Spring中配置事务传播行为

### 1. 使用@Transactional注解

**方法级别**：

```java
@Service
public class UserService {
    @Transactional(propagation = Propagation.REQUIRED)
    public void createUser(User user) {
        // 业务逻辑
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUserCreation(User user) {
        // 日志记录
    }
}
```

**类级别**：

```java
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserService {
    public void createUser(User user) {
        // 业务逻辑（使用类级别的传播行为）
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logUserCreation(User user) {
        // 日志记录（覆盖类级别的传播行为）
    }
}
```

### 2. 使用XML配置

**配置示例**：

```xml
<!-- 配置事务通知 -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <tx:attributes>
        <!-- 常规业务操作 -->
        <tx:method name="create*" propagation="REQUIRED"/>
        <tx:method name="update*" propagation="REQUIRED"/>
        <tx:method name="delete*" propagation="REQUIRED"/>
        
        <!-- 只读操作 -->
        <tx:method name="get*" propagation="SUPPORTS" read-only="true"/>
        <tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
        
        <!-- 日志记录 -->
        <tx:method name="log*" propagation="REQUIRES_NEW"/>
        
        <!-- 外部系统调用 -->
        <tx:method name="call*" propagation="NOT_SUPPORTED"/>
        
        <!-- 其他方法 -->
        <tx:method name="*" propagation="REQUIRED"/>
    </tx:attributes>
</tx:advice>

<!-- 配置AOP -->
<aop:config>
    <aop:pointcut id="serviceMethod" expression="execution(* com.example.service.*.*(..))"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="serviceMethod"/>
</aop:config>
```

### 3. 使用编程式事务管理

**使用TransactionTemplate**：

```java
@Service
public class UserService {
    @Autowired
    private TransactionTemplate transactionTemplate;
    
    public void createUserWithCustomPropagation(User user) {
        // 设置传播行为
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                // 业务逻辑
                saveUser(user);
            }
        });
    }
}
```

**使用PlatformTransactionManager**：

```java
@Service
public class UserService {
    @Autowired
    private PlatformTransactionManager transactionManager;
    
    public void createUserWithCustomPropagation(User user) {
        // 定义事务属性
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        
        // 开始事务
        TransactionStatus status = transactionManager.getTransaction(def);
        
        try {
            // 业务逻辑
            saveUser(user);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw e;
        }
    }
}
```

## 事务传播行为的最佳实践

### 1. 选择合适的传播行为

- **REQUIRED**：默认选择，适合大多数业务操作
- **SUPPORTS**：适合只读操作，如查询、报表
- **MANDATORY**：适合必须在事务中执行的操作
- **REQUIRES_NEW**：适合需要独立事务的操作，如日志、消息
- **NOT_SUPPORTED**：适合不需要事务的耗时操作
- **NEVER**：适合绝对不能在事务中执行的操作
- **NESTED**：适合复杂业务流程，需要部分回滚的场景

### 2. 避免常见错误

1. **滥用REQUIRES_NEW**：
   - 错误：所有方法都使用REQUIRES_NEW
   - 后果：创建过多事务，影响性能
   - 解决：只在需要独立事务时使用

2. **嵌套事务处理不当**：
   - 错误：不了解NESTED的特性
   - 后果：事务回滚不符合预期
   - 解决：理解NESTED的保存点机制

3. **传播行为与隔离级别冲突**：
   - 错误：传播行为与隔离级别配置不当
   - 后果：事务行为异常
   - 解决：合理配置传播行为和隔离级别

4. **事务方法调用陷阱**：
   - 错误：在同一个类中调用事务方法
   - 后果：传播行为不生效（因为Spring AOP的限制）
   - 解决：使用self-injection或重构代码

### 3. 性能优化

1. **减少事务范围**：
   - 只在必要的方法上添加事务
   - 缩短事务持有时间

2. **使用合适的传播行为**：
   - 查询操作使用SUPPORTS
   - 耗时操作使用NOT_SUPPORTED

3. **避免长事务**：
   - 将耗时操作移出事务
   - 使用异步处理

4. **合理使用REQUIRES_NEW**：
   - 只在需要独立事务时使用
   - 避免嵌套过深的REQUIRES_NEW

### 4. 异常处理

1. **REQUIRED传播行为**：
   - 内部异常会导致整个事务回滚
   - 需要合理处理异常

2. **REQUIRES_NEW传播行为**：
   - 内部异常只影响新事务
   - 外部事务可以选择是否处理

3. **NESTED传播行为**：
   - 内部异常可以独立回滚
   - 外部事务可以继续执行

## 代码示例：事务传播行为的综合使用

### 1. 完整业务流程示例

```java
@Service
public class OrderProcessingService {
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private LogService logService;
    
    @Transactional
    public void processOrder(OrderRequest request) {
        try {
            // 1. 创建订单（REQUIRED）
            Order order = orderService.createOrder(request);
            
            try {
                // 2. 检查库存（NESTED）
                inventoryService.checkAndReserveInventory(order);
            } catch (InventoryException e) {
                // 库存不足，只回滚库存检查
                logService.logWarning("Inventory check failed: " + e.getMessage());
                throw new OrderProcessingException("Insufficient inventory");
            }
            
            try {
                // 3. 处理支付（REQUIRED）
                paymentService.processPayment(order);
            } catch (PaymentException e) {
                // 支付失败，回滚整个事务
                logService.logError("Payment failed: " + e.getMessage());
                throw new OrderProcessingException("Payment failed");
            }
            
            // 4. 确认订单（REQUIRED）
            orderService.confirmOrder(order.getId());
            
            // 5. 发送通知（REQUIRES_NEW）
            notificationService.sendOrderConfirmation(order);
            
            // 6. 记录成功日志（REQUIRES_NEW）
            logService.logSuccess("Order processed successfully: " + order.getId());
            
        } catch (Exception e) {
            // 7. 记录错误日志（REQUIRES_NEW）
            logService.logError("Order processing failed: " + e.getMessage());
            throw e;
        }
    }
}

@Service
public class OrderService {
    @Transactional(propagation = Propagation.REQUIRED)
    public Order createOrder(OrderRequest request) {
        // 创建订单逻辑
        return order;
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void confirmOrder(Long orderId) {
        // 确认订单逻辑
    }
}

@Service
public class InventoryService {
    @Transactional(propagation = Propagation.NESTED)
    public void checkAndReserveInventory(Order order) {
        // 检查库存
        if (!hasSufficientInventory(order)) {
            throw new InventoryException("Insufficient inventory");
        }
        // 预留库存
        reserveInventory(order);
    }
    
    private boolean hasSufficientInventory(Order order) {
        // 检查库存逻辑
        return true;
    }
    
    private void reserveInventory(Order order) {
        // 预留库存逻辑
    }
}

@Service
public class PaymentService {
    @Transactional(propagation = Propagation.REQUIRED)
    public void processPayment(Order order) {
        // 处理支付逻辑
    }
}

@Service
public class NotificationService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendOrderConfirmation(Order order) {
        // 发送通知逻辑
    }
}

@Service
public class LogService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(String message) {
        // 记录成功日志
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logError(String message) {
        // 记录错误日志
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logWarning(String message) {
        // 记录警告日志
    }
}
```

### 2. 事务传播行为测试

```java
@Service
public class PropagationTestService {
    @Autowired
    private TestServiceA serviceA;
    @Autowired
    private TestServiceB serviceB;
    
    public void testPropagation() {
        // 测试1: 非事务调用REQUIRED
        System.out.println("=== Test 1: Non-transactional call to REQUIRED ===");
        serviceA.methodWithRequired();
        
        // 测试2: 事务调用REQUIRED
        System.out.println("\n=== Test 2: Transactional call to REQUIRED ===");
        serviceA.methodWithRequiredCallingRequired();
        
        // 测试3: 事务调用REQUIRES_NEW
        System.out.println("\n=== Test 3: Transactional call to REQUIRES_NEW ===");
        serviceA.methodWithRequiredCallingRequiresNew();
        
        // 测试4: 测试NESTED
        System.out.println("\n=== Test 4: Testing NESTED propagation ===");
        serviceA.methodWithRequiredCallingNested();
    }
}

@Service
public class TestServiceA {
    @Autowired
    private TestServiceB serviceB;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodWithRequired() {
        System.out.println("ServiceA.methodWithRequired() called");
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodWithRequiredCallingRequired() {
        System.out.println("ServiceA.methodWithRequiredCallingRequired() called");
        serviceB.methodWithRequired();
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodWithRequiredCallingRequiresNew() {
        System.out.println("ServiceA.methodWithRequiredCallingRequiresNew() called");
        serviceB.methodWithRequiresNew();
    }
    
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodWithRequiredCallingNested() {
        System.out.println("ServiceA.methodWithRequiredCallingNested() called");
        try {
            serviceB.methodWithNested();
        } catch (Exception e) {
            System.out.println("Caught exception from nested transaction: " + e.getMessage());
            System.out.println("Outer transaction continues");
        }
    }
}

@Service
public class TestServiceB {
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodWithRequired() {
        System.out.println("ServiceB.methodWithRequired() called");
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodWithRequiresNew() {
        System.out.println("ServiceB.methodWithRequiresNew() called");
    }
    
    @Transactional(propagation = Propagation.NESTED)
    public void methodWithNested() {
        System.out.println("ServiceB.methodWithNested() called");
        throw new RuntimeException("Nested transaction exception");
    }
}
```

## 总结

Spring提供了七种事务传播行为，每种都有其特定的使用场景：

1. **REQUIRED**：默认传播行为，适合大多数业务操作
2. **SUPPORTS**：支持事务，适合只读操作
3. **MANDATORY**：强制要求事务，适合关键操作
4. **REQUIRES_NEW**：创建新事务，适合需要独立事务的操作
5. **NOT_SUPPORTED**：非事务执行，适合耗时操作
6. **NEVER**：禁止事务，适合绝对不能在事务中执行的操作
7. **NESTED**：嵌套事务，适合复杂业务流程

### 选择传播行为的建议

1. **默认选择REQUIRED**：
   - 大多数情况下使用默认的REQUIRED传播行为
   - 它能满足大多数业务场景的需求

2. **根据业务需求选择**：
   - **独立事务**：使用REQUIRES_NEW
   - **只读操作**：使用SUPPORTS
   - **复杂流程**：使用NESTED
   - **耗时操作**：使用NOT_SUPPORTED

3. **考虑性能因素**：
   - 减少事务创建和提交的开销
   - 避免长事务
   - 合理使用异步处理

4. **理解事务边界**：
   - 明确事务的开始和结束点
   - 处理好异常情况下的事务行为
   - 确保数据一致性

通过合理选择和使用事务传播行为，可以有效地管理事务，保证业务逻辑的正确性，同时优化系统性能。