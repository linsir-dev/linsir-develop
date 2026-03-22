# spring-beans

IoC 容器核心模块，负责 Bean 的定义、创建和生命周期管理。

## 主要职责

- Bean 定义管理
- Bean 实例化
- 依赖注入
- 生命周期管理

## 关键组件

| 组件 | 说明 |
|------|------|
| `BeanFactory` | Bean 工厂接口 |
| `BeanDefinition` | Bean 定义 |
| `DefaultListableBeanFactory` | 默认实现 |
