# spring-aop

面向切面编程模块，提供 AOP 功能的实现。

## 主要职责

- 切面定义与管理
- 代理创建
- 通知执行
- 切点匹配

## 关键组件

| 组件 | 说明 |
|------|------|
| `AopProxy` | AOP 代理 |
| `Advisor` | 通知器 |
| `Pointcut` | 切点 |
| `MethodInterceptor` | 方法拦截器 |
