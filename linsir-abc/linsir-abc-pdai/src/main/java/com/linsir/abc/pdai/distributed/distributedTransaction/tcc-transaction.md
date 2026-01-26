# 什么是TCC？

## TCC简介

TCC（Try-Confirm-Cancel）是一种应用层的分布式事务解决方案，通过业务逻辑实现最终一致性。TCC将业务操作分为三个阶段：Try阶段、Confirm阶段和Cancel阶段，通过这三个阶段保证分布式事务的一致性。

## TCC的三个阶段

### 1. Try阶段

**目的**：资源预留和检查

**操作**：
- 检查业务规则是否满足
- 预留资源
- 锁定资源

**示例**：
```java
public boolean tryDecreaseBalance(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId);
    if (account.getBalance().compareTo(amount) < 0) {
        return false;
    }
    account.setFrozenBalance(account.getFrozenBalance().add(amount));
    accountRepository.save(account);
    return true;
}
```

### 2. Confirm阶段

**目的**：确认执行业务操作

**操作**：
- 确认执行业务操作
- 使用Try阶段预留的资源
- 释放资源锁

**示例**：
```java
public boolean confirmDecreaseBalance(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId);
    account.setBalance(account.getBalance().subtract(amount));
    account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
    accountRepository.save(account);
    return true;
}
```

### 3. Cancel阶段

**目的**：取消业务操作

**操作**：
- 取消业务操作
- 释放Try阶段预留的资源
- 回滚业务状态

**示例**：
```java
public boolean cancelDecreaseBalance(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId);
    account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
    accountRepository.save(account);
    return true;
}
```

## TCC的流程

### 正常流程

```
事务开始
  ↓
Try阶段：资源预留
  ↓
所有Try都成功
  ↓
Confirm阶段：确认执行
  ↓
事务提交成功
```

### 异常流程

```
事务开始
  ↓
Try阶段：资源预留
  ↓
有Try失败
  ↓
Cancel阶段：取消操作
  ↓
事务回滚成功
```

## TCC的实现

### 1. 基本实现

#### 1.1 定义TCC接口

```java
public interface AccountTccService {
    
    @TccTry
    boolean tryDecreaseBalance(Long accountId, BigDecimal amount);
    
    @TccConfirm
    boolean confirmDecreaseBalance(Long accountId, BigDecimal amount);
    
    @TccCancel
    boolean cancelDecreaseBalance(Long accountId, BigDecimal amount);
}
```

#### 1.2 实现TCC接口

```java
@Service
public class AccountTccServiceImpl implements AccountTccService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public boolean tryDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            return false;
        }
        account.setFrozenBalance(account.getFrozenBalance().add(amount));
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean confirmDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean cancelDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }
}
```

#### 1.3 使用TCC

```java
@Service
public class OrderService {
    
    @Autowired
    private AccountTccService accountTccService;
    
    @Autowired
    private InventoryTccService inventoryTccService;
    
    @TccTransaction
    public void createOrder(Order order) {
        boolean tryResult1 = accountTccService.tryDecreaseBalance(order.getUserId(), order.getAmount());
        boolean tryResult2 = inventoryTccService.tryDecreaseInventory(order.getProductId(), order.getQuantity());
        
        if (tryResult1 && tryResult2) {
            accountTccService.confirmDecreaseBalance(order.getUserId(), order.getAmount());
            inventoryTccService.confirmDecreaseInventory(order.getProductId(), order.getQuantity());
        } else {
            accountTccService.cancelDecreaseBalance(order.getUserId(), order.getAmount());
            inventoryTccService.cancelDecreaseInventory(order.getProductId(), order.getQuantity());
        }
    }
}
```

### 2. 使用Seata实现TCC

#### 2.1 定义TCC接口

```java
@LocalTCC
public interface AccountTccService {
    
    @TwoPhaseBusinessAction(name = "decreaseBalance", commitMethod = "confirmDecreaseBalance", rollbackMethod = "cancelDecreaseBalance")
    boolean tryDecreaseBalance(@BusinessActionContextParameter(paramName = "accountId") Long accountId, 
                               @BusinessActionContextParameter(paramName = "amount") BigDecimal amount);
    
    boolean confirmDecreaseBalance(BusinessActionContext context);
    
    boolean cancelDecreaseBalance(BusinessActionContext context);
}
```

#### 2.2 实现TCC接口

```java
@Service
public class AccountTccServiceImpl implements AccountTccService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public boolean tryDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        account.setFrozenBalance(account.getFrozenBalance().add(amount));
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean confirmDecreaseBalance(BusinessActionContext context) {
        Long accountId = Long.valueOf(context.getActionContext("accountId").toString());
        BigDecimal amount = new BigDecimal(context.getActionContext("amount").toString());
        
        Account account = accountRepository.findById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean cancelDecreaseBalance(BusinessActionContext context) {
        Long accountId = Long.valueOf(context.getActionContext("accountId").toString());
        BigDecimal amount = new BigDecimal(context.getActionContext("amount").toString());
        
        Account account = accountRepository.findById(accountId);
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }
}
```

#### 2.3 使用TCC

```java
@Service
public class OrderService {
    
    @Autowired
    private AccountTccService accountTccService;
    
    @Autowired
    private InventoryTccService inventoryTccService;
    
    @GlobalTransactional
    public void createOrder(Order order) {
        accountTccService.tryDecreaseBalance(order.getUserId(), order.getAmount());
        inventoryTccService.tryDecreaseInventory(order.getProductId(), order.getQuantity());
    }
}
```

### 3. 使用Hmily实现TCC

#### 3.1 定义TCC接口

```java
@Hmily
public interface AccountTccService {
    
    @HmilyTCC(confirmMethod = "confirmDecreaseBalance", cancelMethod = "cancelDecreaseBalance")
    boolean tryDecreaseBalance(Long accountId, BigDecimal amount);
    
    boolean confirmDecreaseBalance(Long accountId, BigDecimal amount);
    
    boolean cancelDecreaseBalance(Long accountId, BigDecimal amount);
}
```

#### 3.2 实现TCC接口

```java
@Service
public class AccountTccServiceImpl implements AccountTccService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    public boolean tryDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足");
        }
        account.setFrozenBalance(account.getFrozenBalance().add(amount));
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean confirmDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }
    
    @Override
    public boolean cancelDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        return true;
    }
}
```

#### 3.3 使用TCC

```java
@Service
public class OrderService {
    
    @Autowired
    private AccountTccService accountTccService;
    
    @Autowired
    private InventoryTccService inventoryTccService;
    
    @HmilyTCC
    public void createOrder(Order order) {
        accountTccService.tryDecreaseBalance(order.getUserId(), order.getAmount());
        inventoryTccService.tryDecreaseInventory(order.getProductId(), order.getQuantity());
    }
}
```

## TCC的设计原则

### 1. Try阶段

- **资源预留**：预留资源，但不真正执行业务操作
- **业务检查**：检查业务规则是否满足
- **幂等性**：保证Try操作的幂等性

### 2. Confirm阶段

- **确认执行**：真正执行业务操作
- **使用预留资源**：使用Try阶段预留的资源
- **幂等性**：保证Confirm操作的幂等性

### 3. Cancel阶段

- **取消操作**：取消业务操作
- **释放预留资源**：释放Try阶段预留的资源
- **幂等性**：保证Cancel操作的幂等性

## TCC的优缺点

### 优点

1. **性能高**：不需要锁住资源，并发性能好
2. **灵活**：可以根据业务需求定制
3. **最终一致性**：保证最终一致性
4. **可扩展性强**：可以支持复杂的业务场景

### 缺点

1. **开发成本高**：需要为每个业务操作实现Try、Confirm、Cancel三个方法
2. **代码侵入性强**：业务代码需要感知分布式事务
3. **幂等性要求高**：需要保证Try、Confirm、Cancel操作的幂等性
4. **补偿逻辑复杂**：Cancel逻辑可能比Try逻辑更复杂

## TCC的适用场景

1. **对性能要求高**：如电商订单、库存扣减等场景
2. **业务逻辑相对简单**：业务逻辑不复杂，容易实现Try、Confirm、Cancel三个方法
3. **可以接受最终一致性**：对实时性要求不高，可以接受最终一致性
4. **资源可以预留**：业务场景支持资源预留

## TCC的最佳实践

### 1. 保证幂等性

```java
public boolean confirmDecreaseBalance(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId);
    
    if (account.getFrozenBalance().compareTo(amount) < 0) {
        return true;
    }
    
    account.setBalance(account.getBalance().subtract(amount));
    account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
    accountRepository.save(account);
    return true;
}
```

### 2. 使用事务日志

```java
@Service
public class AccountTccServiceImpl implements AccountTccService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    
    @Override
    public boolean tryDecreaseBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId);
        if (account.getBalance().compareTo(amount) < 0) {
            return false;
        }
        account.setFrozenBalance(account.getFrozenBalance().add(amount));
        accountRepository.save(account);
        
        TransactionLog transactionLog = new TransactionLog();
        transactionLog.setTransactionId(getTransactionId());
        transactionLog.setAccountId(accountId);
        transactionLog.setAmount(amount);
        transactionLog.setStatus(TransactionStatus.TRY);
        transactionLogRepository.save(transactionLog);
        
        return true;
    }
    
    @Override
    public boolean confirmDecreaseBalance(Long accountId, BigDecimal amount) {
        TransactionLog transactionLog = transactionLogRepository.findByTransactionId(getTransactionId());
        if (transactionLog == null || transactionLog.getStatus() == TransactionStatus.CONFIRM) {
            return true;
        }
        
        Account account = accountRepository.findById(accountId);
        account.setBalance(account.getBalance().subtract(amount));
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        
        transactionLog.setStatus(TransactionStatus.CONFIRM);
        transactionLogRepository.save(transactionLog);
        
        return true;
    }
    
    @Override
    public boolean cancelDecreaseBalance(Long accountId, BigDecimal amount) {
        TransactionLog transactionLog = transactionLogRepository.findByTransactionId(getTransactionId());
        if (transactionLog == null || transactionLog.getStatus() == TransactionStatus.CANCEL) {
            return true;
        }
        
        Account account = accountRepository.findById(accountId);
        account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
        accountRepository.save(account);
        
        transactionLog.setStatus(TransactionStatus.CANCEL);
        transactionLogRepository.save(transactionLog);
        
        return true;
    }
}
```

### 3. 使用超时机制

```java
@Scheduled(fixedDelay = 5000)
public void timeoutCancel() {
    List<TransactionLog> transactionLogs = transactionLogRepository.findByStatusAndCreateTimeBefore(
        TransactionStatus.TRY, 
        new Date(System.currentTimeMillis() - 30000)
    );
    
    for (TransactionLog transactionLog : transactionLogs) {
        try {
            cancelDecreaseBalance(transactionLog.getAccountId(), transactionLog.getAmount());
        } catch (Exception e) {
            log.error("超时取消失败", e);
        }
    }
}
```

### 4. 使用重试机制

```java
@Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
public boolean confirmDecreaseBalance(Long accountId, BigDecimal amount) {
    Account account = accountRepository.findById(accountId);
    account.setBalance(account.getBalance().subtract(amount));
    account.setFrozenBalance(account.getFrozenBalance().subtract(amount));
    accountRepository.save(account);
    return true;
}
```

### 5. 监控TCC事务

```java
public class TccMonitor {
    
    @Autowired
    private TransactionLogRepository transactionLogRepository;
    
    public void monitorTccTransactions() {
        List<TransactionLog> tryTransactions = transactionLogRepository.findByStatus(TransactionStatus.TRY);
        List<TransactionLog> confirmTransactions = transactionLogRepository.findByStatus(TransactionStatus.CONFIRM);
        List<TransactionLog> cancelTransactions = transactionLogRepository.findByStatus(TransactionStatus.CANCEL);
        
        System.out.println("Try事务数量: " + tryTransactions.size());
        System.out.println("Confirm事务数量: " + confirmTransactions.size());
        System.out.println("Cancel事务数量: " + cancelTransactions.size());
    }
}
```

## TCC与其他方案的对比

| 方案 | 一致性 | 性能 | 实现复杂度 | 适用场景 |
|------|--------|------|------------|----------|
| 2PC | 强一致性 | 低 | 低 | 对一致性要求极高的场景 |
| 3PC | 强一致性 | 中 | 中 | 对一致性要求高，但可以接受一定性能损失的场景 |
| TCC | 最终一致性 | 高 | 高 | 对性能要求高，业务逻辑简单的场景 |
| SAGA | 最终一致性 | 高 | 高 | 对性能要求高，业务逻辑简单的场景 |
| 本地消息表 | 最终一致性 | 中 | 低 | 对实时性要求不高的场景 |
| 事务消息 | 最终一致性 | 高 | 中 | 对实时性要求不高，已有支持事务消息的消息队列 |

## 总结

TCC是一种应用层的分布式事务解决方案，通过业务逻辑实现最终一致性。TCC将业务操作分为三个阶段：Try阶段、Confirm阶段和Cancel阶段，通过这三个阶段保证分布式事务的一致性。

TCC具有性能高、灵活、可扩展性强等优点，但也存在开发成本高、代码侵入性强、幂等性要求高等缺点。在实际应用中，TCC适用于对性能要求高、业务逻辑相对简单、可以接受最终一致性、资源可以预留的场景。

无论选择哪种方案，都需要考虑幂等性、补偿机制、监控告警等问题，确保分布式事务的可靠性。
