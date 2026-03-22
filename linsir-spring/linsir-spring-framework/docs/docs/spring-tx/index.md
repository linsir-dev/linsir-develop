# spring-tx

事务管理模块，提供声明式事务管理功能。

## 主要职责

- 事务管理抽象
- 声明式事务支持
- 事务传播控制
- 事务回滚管理

## 关键组件

| 组件 | 说明 |
|------|------|
| `PlatformTransactionManager` | 事务管理器接口 |
| `@Transactional` | 事务注解 |
