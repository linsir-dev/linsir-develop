# 什么是SAGA方案？

## SAGA简介

SAGA是一种长事务解决方案，将长事务拆分为多个本地事务，每个本地事务都有对应的补偿事务。SAGA通过正向操作和补偿操作保证分布式事务的最终一致性。

## SAGA的核心概念

### 1. 正向操作

执行业务操作，记录操作日志。

### 2. 补偿操作

执行补偿操作，回滚业务状态。

### 3. 事务日志

记录事务的执行状态，用于事务恢复。

## SAGA的两种模式

### 1. 编排式SAGA

由一个中心协调者负责编排所有子事务的执行顺序和补偿逻辑。

#### 1.1 原理

```
协调者                    服务A                     服务B                     服务C
   │                          │                         │                         │
   ├──────────────────────────>│                         │                         │
   │      执行操作A            │                         │                         │
   │<──────────────────────────┤                         │                         │
   │      操作A成功            │                         │                         │
   │                          ├─────────────────────────>│                         │
   │                          │      执行操作B          │                         │
   │                          │<─────────────────────────┤                         │
   │                          │      操作B成功          │                         │
   │                          │                         ├─────────────────────────>│
   │                          │                         │      执行操作C          │
   │                          │                         │<─────────────────────────┤
   │                          │                         │      操作C成功          │
   │                          │                         │<─────────────────────────┤
   │                          │                         │      操作C失败          │
   │                          │<─────────────────────────┤                         │
   │                          │      补偿操作B          │                         │
   │                          │<─────────────────────────┤                         │
   │                          │      补偿操作B成功      │                         │
   │<──────────────────────────┤                         │                         │
   │      补偿操作A           │                         │                         │
   │<──────────────────────────┤                         │                         │
   │      补偿操作A成功       │                         │                         │
```

#### 1.2 实现

```java
@Service
public class OrderSagaOrchestrator {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private SagaLogRepository sagaLogRepository;
    
    public void createOrder(Order order) {
        String sagaId = UUID.randomUUID().toString();
        
        try {
            createOrder(order, sagaId);
            decreaseInventory(order, sagaId);
            deductPayment(order, sagaId);
            
            markSagaAsCompleted(sagaId);
        } catch (Exception e) {
            compensateOrder(sagaId);
            throw e;
        }
    }
    
    private void createOrder(Order order, String sagaId) {
        orderService.createOrder(order);
        
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(sagaId);
        sagaLog.setStep("createOrder");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
    }
    
    private void decreaseInventory(Order order, String sagaId) {
        inventoryService.decreaseInventory(order.getProductId(), order.getQuantity());
        
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(sagaId);
        sagaLog.setStep("decreaseInventory");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
    }
    
    private void deductPayment(Order order, String sagaId) {
        paymentService.deductPayment(order.getUserId(), order.getAmount());
        
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(sagaId);
        sagaLog.setStep("deductPayment");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
    }
    
    private void compensateOrder(String sagaId) {
        List<SagaLog> sagaLogs = sagaLogRepository.findBySagaIdOrderByCreateTimeDesc(sagaId);
        
        for (SagaLog sagaLog : sagaLogs) {
            try {
                switch (sagaLog.getStep()) {
                    case "deductPayment":
                        paymentService.refundPayment(sagaLog.getUserId(), sagaLog.getAmount());
                        break;
                    case "decreaseInventory":
                        inventoryService.increaseInventory(sagaLog.getProductId(), sagaLog.getQuantity());
                        break;
                    case "createOrder":
                        orderService.deleteOrder(sagaLog.getOrderId());
                        break;
                }
                
                sagaLog.setStatus(SagaStatus.COMPENSATED);
                sagaLogRepository.save(sagaLog);
            } catch (Exception e) {
                log.error("补偿操作失败", e);
            }
        }
    }
    
    private void markSagaAsCompleted(String sagaId) {
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(sagaId);
        sagaLog.setStep("completed");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
    }
}
```

### 2. 协同式SAGA

每个服务都负责执行自己的本地事务和补偿事务，通过事件驱动的方式进行协调。

#### 2.1 原理

```
服务A                     服务B                     服务C                     服务D
   │                          │                         │                         │
   ├──────────────────────────>│                         │                         │
   │      执行操作A            │                         │                         │
   │<──────────────────────────┤                         │                         │
   │      操作A成功            │                         │                         │
   │                          ├─────────────────────────>│                         │
   │                          │      执行操作B          │                         │
   │                          │<─────────────────────────┤                         │
   │                          │      操作B成功          │                         │
   │                          │                         ├─────────────────────────>│
   │                          │                         │      执行操作C          │
   │                          │                         │<─────────────────────────┤
   │                          │                         │      操作C失败          │
   │                          │<─────────────────────────┤                         │
   │                          │      补偿操作B          │                         │
   │<──────────────────────────┤                         │                         │
   │      补偿操作A           │                         │                         │
```

#### 2.2 实现

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Autowired
    private SagaLogRepository sagaLogRepository;
    
    @Transactional
    public void createOrder(Order order) {
        orderRepository.save(order);
        
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(order.getSagaId());
        sagaLog.setStep("createOrder");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
        
        eventPublisher.publish(new OrderCreatedEvent(order));
    }
    
    @Transactional
    public void compensateOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            return;
        }
        
        orderRepository.delete(order);
        
        SagaLog sagaLog = sagaLogRepository.findBySagaIdAndStep(order.getSagaId(), "createOrder");
        if (sagaLog != null) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            sagaLogRepository.save(sagaLog);
        }
    }
}

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Autowired
    private SagaLogRepository sagaLogRepository;
    
    @EventListener
    @Transactional
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        Inventory inventory = inventoryRepository.findByProductId(event.getProductId());
        inventory.setQuantity(inventory.getQuantity() - event.getQuantity());
        inventoryRepository.save(inventory);
        
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(event.getSagaId());
        sagaLog.setStep("decreaseInventory");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
        
        eventPublisher.publish(new InventoryDecreasedEvent(event.getSagaId(), event.getProductId(), event.getQuantity()));
    }
    
    @EventListener
    @Transactional
    public void handlePaymentFailedEvent(PaymentFailedEvent event) {
        Inventory inventory = inventoryRepository.findByProductId(event.getProductId());
        inventory.setQuantity(inventory.getQuantity() + event.getQuantity());
        inventoryRepository.save(inventory);
        
        SagaLog sagaLog = sagaLogRepository.findBySagaIdAndStep(event.getSagaId(), "decreaseInventory");
        if (sagaLog != null) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            sagaLogRepository.save(sagaLog);
        }
    }
}

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private EventPublisher eventPublisher;
    
    @Autowired
    private SagaLogRepository sagaLogRepository;
    
    @EventListener
    @Transactional
    public void handleInventoryDecreasedEvent(InventoryDecreasedEvent event) {
        try {
            Payment payment = new Payment();
            payment.setUserId(event.getUserId());
            payment.setAmount(event.getAmount());
            paymentRepository.save(payment);
            
            SagaLog sagaLog = new SagaLog();
            sagaLog.setSagaId(event.getSagaId());
            sagaLog.setStep("deductPayment");
            sagaLog.setStatus(SagaStatus.COMPLETED);
            sagaLogRepository.save(sagaLog);
            
            eventPublisher.publish(new PaymentDeductedEvent(event.getSagaId()));
        } catch (Exception e) {
            eventPublisher.publish(new PaymentFailedEvent(event.getSagaId(), event.getProductId(), event.getQuantity()));
            throw e;
        }
    }
    
    @EventListener
    @Transactional
    public void handleOrderFailedEvent(OrderFailedEvent event) {
        Payment payment = paymentRepository.findBySagaId(event.getSagaId());
        if (payment != null) {
            paymentRepository.delete(payment);
            
            SagaLog sagaLog = sagaLogRepository.findBySagaIdAndStep(event.getSagaId(), "deductPayment");
            if (sagaLog != null) {
                sagaLog.setStatus(SagaStatus.COMPENSATED);
                sagaLogRepository.save(sagaLog);
            }
        }
    }
}
```

## SAGA的实现框架

### 1. Seata Saga模式

#### 1.1 定义SAGA流程

```json
{
  "Name": "createOrder",
  "Comment": "创建订单",
  "StartState": "CreateOrder",
  "States": {
    "CreateOrder": {
      "Type": "ServiceTask",
      "ServiceName": "orderService",
      "ServiceMethod": "createOrder",
      "CompensateState": "CompensateOrder",
      "Next": "DecreaseInventory"
    },
    "DecreaseInventory": {
      "Type": "ServiceTask",
      "ServiceName": "inventoryService",
      "ServiceMethod": "decreaseInventory",
      "CompensateState": "CompensateInventory",
      "Next": "DeductPayment"
    },
    "DeductPayment": {
      "Type": "ServiceTask",
      "ServiceName": "paymentService",
      "ServiceMethod": "deductPayment",
      "CompensateState": "CompensatePayment",
      "End": true
    },
    "CompensateOrder": {
      "Type": "ServiceTask",
      "ServiceName": "orderService",
      "ServiceMethod": "compensateOrder",
      "IsForCompensation": true
    },
    "CompensateInventory": {
      "Type": "ServiceTask",
      "ServiceName": "inventoryService",
      "ServiceMethod": "compensateInventory",
      "IsForCompensation": true
    },
    "CompensatePayment": {
      "Type": "ServiceTask",
      "ServiceName": "paymentService",
      "ServiceMethod": "compensatePayment",
      "IsForCompensation": true
    }
  }
}
```

#### 1.2 实现服务

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
    }
    
    public void compensateOrder(Order order) {
        orderRepository.delete(order);
    }
}

@Service
public class InventoryService {
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    public void decreaseInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
    
    public void compensateInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }
}

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public void deductPayment(Long userId, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(amount);
        paymentRepository.save(payment);
    }
    
    public void compensatePayment(Long userId, BigDecimal amount) {
        Payment payment = paymentRepository.findByUserIdAndAmount(userId, amount);
        if (payment != null) {
            paymentRepository.delete(payment);
        }
    }
}
```

### 2. Axon Framework

#### 2.1 定义聚合

```java
@Aggregate
public class Order {
    
    @AggregateIdentifier
    private String orderId;
    
    private String sagaId;
    
    private OrderStatus status;
    
    public Order() {
    }
    
    @CommandHandler
    public Order(CreateOrderCommand command) {
        apply(new OrderCreatedEvent(command.getOrderId(), command.getSagaId()));
    }
    
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        this.orderId = event.getOrderId();
        this.sagaId = event.getSagaId();
        this.status = OrderStatus.CREATED;
    }
    
    @CommandHandler
    public void handle(CompensateOrderCommand command) {
        apply(new OrderCompensatedEvent(command.getOrderId()));
    }
    
    @EventSourcingHandler
    public void on(OrderCompensatedEvent event) {
        this.status = OrderStatus.COMPENSATED;
    }
}
```

#### 2.2 定义Saga

```java
@Saga
public class OrderSaga {
    
    @SagaEventHandler(associationProperty = "sagaId")
    public void handle(OrderCreatedEvent event) {
        SagaLifecycle.associateWith("sagaId", event.getSagaId());
        commandGateway.send(new DecreaseInventoryCommand(event.getProductId(), event.getQuantity()));
    }
    
    @SagaEventHandler(associationProperty = "sagaId")
    public void handle(InventoryDecreasedEvent event) {
        commandGateway.send(new DeductPaymentCommand(event.getUserId(), event.getAmount()));
    }
    
    @SagaEventHandler(associationProperty = "sagaId")
    public void handle(PaymentDeductedEvent event) {
        SagaLifecycle.end();
    }
    
    @SagaEventHandler(associationProperty = "sagaId")
    public void handle(PaymentFailedEvent event) {
        commandGateway.send(new CompensateInventoryCommand(event.getProductId(), event.getQuantity()));
    }
    
    @SagaEventHandler(associationProperty = "sagaId")
    public void handle(InventoryCompensatedEvent event) {
        commandGateway.send(new CompensateOrderCommand(event.getOrderId()));
    }
    
    @SagaEventHandler(associationProperty = "sagaId")
    public void handle(OrderCompensatedEvent event) {
        SagaLifecycle.end();
    }
}
```

## SAGA的优缺点

### 优点

1. **性能高**：不需要锁住资源，并发性能好
2. **灵活**：可以根据业务需求定制
3. **最终一致性**：保证最终一致性
4. **可扩展性强**：可以支持复杂的业务场景

### 缺点

1. **开发成本高**：需要为每个业务操作实现补偿逻辑
2. **代码侵入性强**：业务代码需要感知分布式事务
3. **补偿逻辑复杂**：补偿逻辑可能比正向逻辑更复杂
4. **事务日志管理复杂**：需要管理事务日志，用于事务恢复

## SAGA的适用场景

1. **对性能要求高**：如电商订单、库存扣减等场景
2. **业务逻辑相对简单**：业务逻辑不复杂，容易实现补偿逻辑
3. **可以接受最终一致性**：对实时性要求不高，可以接受最终一致性
4. **长事务场景**：事务执行时间较长，不适合使用2PC、3PC等强一致性方案

## SAGA的最佳实践

### 1. 保证幂等性

```java
public void compensateOrder(Long orderId) {
    Order order = orderRepository.findById(orderId);
    if (order == null || order.getStatus() == OrderStatus.COMPENSATED) {
        return;
    }
    
    orderRepository.delete(order);
    
    SagaLog sagaLog = sagaLogRepository.findBySagaIdAndStep(order.getSagaId(), "createOrder");
    if (sagaLog != null) {
        sagaLog.setStatus(SagaStatus.COMPENSATED);
        sagaLogRepository.save(sagaLog);
    }
}
```

### 2. 使用事务日志

```java
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private SagaLogRepository sagaLogRepository;
    
    @Transactional
    public void createOrder(Order order) {
        orderRepository.save(order);
        
        SagaLog sagaLog = new SagaLog();
        sagaLog.setSagaId(order.getSagaId());
        sagaLog.setStep("createOrder");
        sagaLog.setStatus(SagaStatus.COMPLETED);
        sagaLogRepository.save(sagaLog);
    }
    
    @Transactional
    public void compensateOrder(Long orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) {
            return;
        }
        
        orderRepository.delete(order);
        
        SagaLog sagaLog = sagaLogRepository.findBySagaIdAndStep(order.getSagaId(), "createOrder");
        if (sagaLog != null) {
            sagaLog.setStatus(SagaStatus.COMPENSATED);
            sagaLogRepository.save(sagaLog);
        }
    }
}
```

### 3. 使用超时机制

```java
@Scheduled(fixedDelay = 5000)
public void timeoutCompensate() {
    List<SagaLog> sagaLogs = sagaLogRepository.findByStatusAndCreateTimeBefore(
        SagaStatus.COMPLETED, 
        new Date(System.currentTimeMillis() - 30000)
    );
    
    for (SagaLog sagaLog : sagaLogs) {
        try {
            compensateSaga(sagaLog.getSagaId());
        } catch (Exception e) {
            log.error("超时补偿失败", e);
        }
    }
}
```

### 4. 使用重试机制

```java
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
public void compensateOrder(Long orderId) {
    Order order = orderRepository.findById(orderId);
    if (order == null) {
        return;
    }
    
    orderRepository.delete(order);
    
    SagaLog sagaLog = sagaLogRepository.findBySagaIdAndStep(order.getSagaId(), "createOrder");
    if (sagaLog != null) {
        sagaLog.setStatus(SagaStatus.COMPENSATED);
        sagaLogRepository.save(sagaLog);
    }
}
```

### 5. 监控SAGA事务

```java
public class SagaMonitor {
    
    @Autowired
    private SagaLogRepository sagaLogRepository;
    
    public void monitorSagas() {
        List<SagaLog> completedSagas = sagaLogRepository.findByStatus(SagaStatus.COMPLETED);
        List<SagaLog> compensatedSagas = sagaLogRepository.findByStatus(SagaStatus.COMPENSATED);
        
        System.out.println("已完成SAGA数量: " + completedSagas.size());
        System.out.println("已补偿SAGA数量: " + compensatedSagas.size());
    }
}
```

## SAGA与其他方案的对比

| 方案 | 一致性 | 性能 | 实现复杂度 | 适用场景 |
|------|--------|------|------------|----------|
| 2PC | 强一致性 | 低 | 低 | 对一致性要求极高的场景 |
| 3PC | 强一致性 | 中 | 中 | 对一致性要求高，但可以接受一定性能损失的场景 |
| TCC | 最终一致性 | 高 | 高 | 对性能要求高，业务逻辑简单的场景 |
| SAGA | 最终一致性 | 高 | 高 | 对性能要求高，业务逻辑简单的场景 |
| 本地消息表 | 最终一致性 | 中 | 低 | 对实时性要求不高的场景 |
| 事务消息 | 最终一致性 | 高 | 中 | 对实时性要求不高，已有支持事务消息的消息队列 |

## 总结

SAGA是一种长事务解决方案，将长事务拆分为多个本地事务，每个本地事务都有对应的补偿事务。SAGA通过正向操作和补偿操作保证分布式事务的最终一致性。

SAGA具有性能高、灵活、可扩展性强等优点，但也存在开发成本高、代码侵入性强、补偿逻辑复杂等缺点。在实际应用中，SAGA适用于对性能要求高、业务逻辑相对简单、可以接受最终一致性、长事务场景。

无论选择哪种方案，都需要考虑幂等性、补偿机制、监控告警等问题，确保分布式事务的可靠性。
