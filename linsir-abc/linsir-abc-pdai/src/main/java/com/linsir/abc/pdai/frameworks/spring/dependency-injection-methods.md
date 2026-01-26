# 可以通过多少种方式完成依赖注入？

## 依赖注入概述

**依赖注入**（Dependency Injection，简称DI）是一种设计模式，也是Spring框架的核心特性之一。它通过将对象的依赖关系从对象内部移出，由外部容器来管理，从而实现了对象之间的解耦。

### 依赖注入的优势

1. **解耦**：减少对象之间的直接依赖
2. **可测试性**：便于单元测试，可轻松替换依赖
3. **可维护性**：代码更加清晰，职责更加明确
4. **灵活性**：运行时动态切换实现
5. **可扩展性**：便于添加新功能

### 依赖注入的核心概念

- **依赖**：一个对象需要另一个对象来完成其功能
- **注入**：将依赖对象提供给需要它的对象
- **容器**：负责创建和管理对象及其依赖关系

## Spring中的依赖注入方式

Spring框架提供了多种依赖注入方式，主要包括：

1. **基于注解的注入**
   - 字段注入（@Autowired）
   - 构造函数注入（@Autowired）
   - Setter方法注入（@Autowired）
   - 方法注入（@Autowired）

2. **基于XML的注入**
   - 构造函数注入
   - Setter方法注入
   - 静态工厂方法注入
   - 实例工厂方法注入

3. **基于Java配置的注入**
   - @Bean方法参数注入
   - 构造函数注入
   - Setter方法注入

4. **特殊注入方式**
   - 自动装配（Autowiring）
   - 条件注入（@Conditional）
   - 延迟注入（@Lazy）
   - 集合类型注入
   - 泛型注入
   - 方法注入（Lookup Method Injection）

## 基于注解的依赖注入

### 1. 字段注入（Field Injection）

#### 定义

直接在字段上使用@Autowired注解进行注入，不需要setter方法或构造函数。

#### 实现机制

Spring通过反射直接设置字段值，即使字段是private的。

#### 代码示例

```java
@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
}
```

#### 优缺点

**优点**：
- 代码简洁，减少样板代码
- 易于阅读

**缺点**：
- 违反封装原则，直接访问private字段
- 难以进行单元测试，无法通过构造函数或setter方法注入mock对象
- 可能导致循环依赖
- 无法保证依赖的不可变性

#### 适用场景

- 快速开发原型
- 小型应用
- 测试代码

### 2. 构造函数注入（Constructor Injection）

#### 定义

在构造函数上使用@Autowired注解进行注入，Spring会在创建Bean时通过构造函数传入依赖。

#### 实现机制

Spring在实例化Bean时，会解析构造函数参数，并注入相应的依赖。

#### 代码示例

```java
@Component
public class UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
    
    @Autowired
    public UserService(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
    
    // 从Spring 4.3开始，如果只有一个构造函数，可以省略@Autowired
    /*
    public UserService(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
    */
}
```

#### 优缺点

**优点**：
- 保证依赖的不可变性（使用final字段）
- 确保对象在创建时就处于有效状态
- 避免循环依赖
- 便于单元测试，可通过构造函数传入mock对象
- 符合依赖倒置原则

**缺点**：
- 代码相对冗长，特别是当依赖较多时

#### 适用场景

- 核心服务类
- 需要确保依赖不可变的场景
- 大型应用
- 对可测试性要求高的场景

### 3. Setter方法注入（Setter Injection）

#### 定义

在setter方法上使用@Autowired注解进行注入，Spring会在创建Bean后调用setter方法注入依赖。

#### 实现机制

Spring在实例化Bean后，会调用标记了@Autowired的setter方法注入依赖。

#### 代码示例

```java
@Component
public class UserService {
    private UserRepository userRepository;
    private OrderService orderService;
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
```

#### 优缺点

**优点**：
- 允许在运行时修改依赖
- 可选依赖（可设置默认值）
- 代码相对清晰

**缺点**：
- 无法保证依赖的不可变性
- 对象可能在依赖注入完成前被使用
- 代码相对冗长

#### 适用场景

- 可选依赖
- 需要在运行时修改依赖的场景
- 遗留代码的改造

### 4. 方法注入（Method Injection）

#### 定义

在任意方法上使用@Autowired注解进行注入，Spring会调用该方法并传入依赖。

#### 实现机制

Spring会解析方法参数，并注入相应的依赖。

#### 代码示例

```java
@Component
public class UserService {
    private UserRepository userRepository;
    private OrderService orderService;
    private EmailService emailService;
    
    @Autowired
    public void initializeDependencies(UserRepository userRepository, 
                                     OrderService orderService, 
                                     EmailService emailService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
        this.emailService = emailService;
    }
}
```

#### 优缺点

**优点**：
- 可以在一个方法中注入多个依赖
- 方法名可以更具描述性
- 可以在注入时执行额外的逻辑

**缺点**：
- 代码相对复杂
- 无法保证依赖的不可变性

#### 适用场景

- 需要在注入依赖时执行初始化逻辑
- 多个依赖需要一起处理的场景

## 基于XML的依赖注入

### 1. 构造函数注入

#### 基本语法

```xml
<bean id="userService" class="com.example.service.UserService">
    <constructor-arg ref="userRepository"/>
    <constructor-arg ref="orderService"/>
</bean>

<bean id="userRepository" class="com.example.repository.UserRepository"/>
<bean id="orderService" class="com.example.service.OrderService"/>
```

#### 高级配置

```xml
<!-- 通过索引指定参数位置 -->
<bean id="userService" class="com.example.service.UserService">
    <constructor-arg index="0" ref="userRepository"/>
    <constructor-arg index="1" ref="orderService"/>
</bean>

<!-- 通过类型指定参数 -->
<bean id="userService" class="com.example.service.UserService">
    <constructor-arg type="com.example.repository.UserRepository" ref="userRepository"/>
    <constructor-arg type="com.example.service.OrderService" ref="orderService"/>
</bean>

<!-- 通过名称指定参数 -->
<bean id="userService" class="com.example.service.UserService">
    <constructor-arg name="userRepository" ref="userRepository"/>
    <constructor-arg name="orderService" ref="orderService"/>
</bean>

<!-- 注入基本类型 -->
<bean id="userService" class="com.example.service.UserService">
    <constructor-arg name="userId" value="100"/>
    <constructor-arg name="userName" value="admin"/>
</bean>
```

### 2. Setter方法注入

#### 基本语法

```xml
<bean id="userService" class="com.example.service.UserService">
    <property name="userRepository" ref="userRepository"/>
    <property name="orderService" ref="orderService"/>
</bean>

<bean id="userRepository" class="com.example.repository.UserRepository"/>
<bean id="orderService" class="com.example.service.OrderService"/>
```

#### 高级配置

```xml
<!-- 注入基本类型 -->
<bean id="userService" class="com.example.service.UserService">
    <property name="userId" value="100"/>
    <property name="userName" value="admin"/>
</bean>

<!-- 注入集合类型 -->
<bean id="userService" class="com.example.service.UserService">
    <property name="userRoles">
        <list>
            <value>ADMIN</value>
            <value>USER</value>
            <value>MANAGER</value>
        </list>
    </property>
    <property name="userMap">
        <map>
            <entry key="name" value="admin"/>
            <entry key="email" value="admin@example.com"/>
        </map>
    </property>
</bean>
```

### 3. 静态工厂方法注入

#### 基本语法

```xml
<bean id="userService" class="com.example.service.UserServiceFactory" factory-method="createUserService">
    <constructor-arg ref="userRepository"/>
</bean>

<bean id="userRepository" class="com.example.repository.UserRepository"/>
```

#### 代码示例

```java
public class UserServiceFactory {
    public static UserService createUserService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}
```

### 4. 实例工厂方法注入

#### 基本语法

```xml
<bean id="userServiceFactory" class="com.example.service.UserServiceFactory"/>

<bean id="userService" factory-bean="userServiceFactory" factory-method="createUserService">
    <constructor-arg ref="userRepository"/>
</bean>

<bean id="userRepository" class="com.example.repository.UserRepository"/>
```

#### 代码示例

```java
public class UserServiceFactory {
    public UserService createUserService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}
```

## 基于Java配置的依赖注入

### 1. @Bean方法参数注入

#### 基本语法

```java
@Configuration
public class AppConfig {
    @Bean
    public UserService userService(UserRepository userRepository, OrderService orderService) {
        return new UserService(userRepository, orderService);
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
    
    @Bean
    public OrderService orderService() {
        return new OrderService();
    }
}
```

#### 工作原理

Spring会自动解析@Bean方法的参数，并从容器中查找相应类型的Bean注入。

### 2. 构造函数注入

#### 代码示例

```java
@Configuration
public class AppConfig {
    @Bean
    public UserService userService() {
        return new UserService(userRepository(), orderService());
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
    
    @Bean
    public OrderService orderService() {
        return new OrderService();
    }
}
```

### 3. Setter方法注入

#### 代码示例

```java
@Configuration
public class AppConfig {
    @Bean
    public UserService userService() {
        UserService userService = new UserService();
        userService.setUserRepository(userRepository());
        userService.setOrderService(orderService());
        return userService;
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
    
    @Bean
    public OrderService orderService() {
        return new OrderService();
    }
}
```

## 特殊注入方式

### 1. 自动装配（Autowiring）

#### 定义

Spring容器自动为Bean注入依赖，无需显式配置。

#### 自动装配模式

- **no**：默认值，不自动装配
- **byName**：按名称自动装配
- **byType**：按类型自动装配
- **constructor**：按构造函数自动装配
- **autodetect**：自动检测（已废弃）

#### XML配置示例

```xml
<!-- 按类型自动装配 -->
<bean id="userService" class="com.example.service.UserService" autowire="byType"/>

<!-- 按名称自动装配 -->
<bean id="userService" class="com.example.service.UserService" autowire="byName"/>

<!-- 按构造函数自动装配 -->
<bean id="userService" class="com.example.service.UserService" autowire="constructor"/>
```

#### 注解配置示例

```java
@Component
@Autowired
public class UserService {
    private UserRepository userRepository;
    
    // 构造函数自动装配
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### 2. 条件注入（@Conditional）

#### 定义

根据条件决定是否注入某个Bean。

#### 代码示例

```java
@Configuration
public class AppConfig {
    @Bean
    @Conditional(DevCondition.class)
    public DataSource devDataSource() {
        return new EmbeddedDataSource();
    }
    
    @Bean
    @Conditional(ProdCondition.class)
    public DataSource prodDataSource() {
        return new JndiDataSource();
    }
}

public class DevCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("spring.profiles.active").equals("dev");
    }
}

public class ProdCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("spring.profiles.active").equals("prod");
    }
}
```

### 3. 延迟注入（@Lazy）

#### 定义

延迟加载Bean，直到首次使用时才创建。

#### 代码示例

```java
@Component
@Lazy
public class HeavyService {
    // 重量级服务
}

@Component
public class UserService {
    @Autowired
    @Lazy
    private HeavyService heavyService;
    
    public void doSomething() {
        // 首次调用时才创建heavyService
        heavyService.process();
    }
}
```

### 4. 集合类型注入

#### 代码示例

```java
@Component
public class UserService {
    @Autowired
    private List<UserRepository> userRepositories;
    
    @Autowired
    private Map<String, UserRepository> userRepositoryMap;
    
    @Autowired
    private Set<UserRepository> userRepositorySet;
}

// 多个实现类
@Component("jdbcUserRepository")
public class JdbcUserRepository implements UserRepository {
    // 实现
}

@Component("jpaUserRepository")
public class JpaUserRepository implements UserRepository {
    // 实现
}
```

### 5. 泛型注入

#### 代码示例

```java
// 泛型接口
public interface Repository<T> {
    void save(T entity);
    T findById(Long id);
}

// 实现类
@Repository
public class UserRepository implements Repository<User> {
    @Override
    public void save(User entity) {
        // 实现
    }
    
    @Override
    public User findById(Long id) {
        // 实现
        return null;
    }
}

@Repository
public class OrderRepository implements Repository<Order> {
    @Override
    public void save(Order entity) {
        // 实现
    }
    
    @Override
    public Order findById(Long id) {
        // 实现
        return null;
    }
}

// 使用泛型注入
@Component
public class UserService {
    @Autowired
    private Repository<User> userRepository; // 自动注入UserRepository
}

@Component
public class OrderService {
    @Autowired
    private Repository<Order> orderRepository; // 自动注入OrderRepository
}
```

### 6. 方法注入（Lookup Method Injection）

#### 定义

解决单例Bean依赖原型Bean的问题，每次调用方法都会返回新的原型Bean实例。

#### 代码示例

```java
// 抽象类中的抽象方法
@Component
public abstract class UserService {
    public void processUser() {
        // 每次调用都会获取新的UserCommand实例
        UserCommand command = createUserCommand();
        command.execute();
    }
    
    // 抽象方法，由Spring实现
    @Lookup
    protected abstract UserCommand createUserCommand();
}

// 原型Bean
@Component
@Scope("prototype")
public class UserCommand {
    public void execute() {
        // 实现
    }
}
```

## 依赖注入方式的对比

### 1. 注入方式对比

| 注入方式 | 优点 | 缺点 | 适用场景 |
|---------|------|------|----------|
| 字段注入 | 代码简洁 | 违反封装，难以测试 | 快速开发，小型应用 |
| 构造函数注入 | 保证不可变性，易于测试 | 代码冗长 | 核心服务，大型应用 |
| Setter方法注入 | 可选依赖，运行时修改 | 无法保证不可变性 | 可选依赖，遗留代码 |
| 方法注入 | 灵活，可执行额外逻辑 | 代码复杂 | 特殊场景 |

### 2. 配置方式对比

| 配置方式 | 优点 | 缺点 | 适用场景 |
|---------|------|------|----------|
| 注解配置 | 简洁，类型安全 | 侵入性强 | 现代Spring应用 |
| XML配置 | 非侵入性，灵活 | 冗长，类型不安全 | 遗留系统，复杂配置 |
| Java配置 | 类型安全，灵活 | 代码量较大 | 复杂配置，需要编程逻辑 |

### 3. 特殊注入方式对比

| 注入方式 | 优点 | 缺点 | 适用场景 |
|---------|------|------|----------|
| 自动装配 | 减少配置 | 可能注入错误的Bean | 简单应用 |
| 条件注入 | 环境感知，灵活 | 配置复杂 | 多环境部署 |
| 延迟注入 | 提高启动速度 | 可能隐藏初始化问题 | 重量级Bean |
| 集合注入 | 批量注入同类型Bean | 需要额外处理 | 多个实现的场景 |
| 泛型注入 | 类型安全，简洁 | 复杂泛型可能出错 | 泛型接口的场景 |
| Lookup方法注入 | 解决单例依赖原型问题 | 只能用于抽象类 | 单例依赖原型的场景 |

## 依赖注入的最佳实践

### 1. 优先使用构造函数注入

- **理由**：
  - 保证依赖的不可变性
  - 确保对象在创建时就处于有效状态
  - 便于单元测试
  - 避免循环依赖
  - 符合依赖倒置原则

- **示例**：

```java
@Component
public class UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
    
    public UserService(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
    
    // 方法实现
}
```

### 2. 合理使用Setter方法注入

- **理由**：
  - 适用于可选依赖
  - 允许在运行时修改依赖
  - 代码相对清晰

- **示例**：

```java
@Component
public class UserService {
    private UserRepository userRepository;
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    // 可选依赖，有默认值
    @Autowired(required = false)
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
```

### 3. 谨慎使用字段注入

- **理由**：
  - 违反封装原则
  - 难以进行单元测试
  - 可能导致循环依赖
  - 无法保证依赖的不可变性

- **替代方案**：
  - 使用构造函数注入
  - 使用@RequiredArgsConstructor注解（Lombok）

### 4. 避免循环依赖

- **常见原因**：
  - A依赖B，B依赖C，C依赖A
  - 双向依赖

- **解决方案**：
  - 重构代码，打破循环依赖
  - 使用@Lazy注解延迟加载
  - 使用Setter方法注入替代构造函数注入
  - 引入接口，使用依赖倒置原则

### 5. 合理使用@Qualifier

- **理由**：
  - 当有多个同类型Bean时，明确指定要注入的Bean
  - 避免自动装配的歧义

- **示例**：

```java
@Component
public class UserService {
    @Autowired
    @Qualifier("jdbcUserRepository")
    private UserRepository userRepository;
}

@Component("jdbcUserRepository")
public class JdbcUserRepository implements UserRepository {
    // 实现
}

@Component("jpaUserRepository")
public class JpaUserRepository implements UserRepository {
    // 实现
}
```

### 6. 使用@Value注入配置属性

- **理由**：
  - 从配置文件中注入属性值
  - 支持SpEL表达式
  - 便于配置管理

- **示例**：

```java
@Component
public class DatabaseConfig {
    @Value("${database.url}")
    private String url;
    
    @Value("${database.username}")
    private String username;
    
    @Value("${database.password}")
    private String password;
    
    @Value("${database.maxConnections:10}")
    private int maxConnections;
}
```

### 7. 使用@Profile进行环境隔离

- **理由**：
  - 根据不同环境加载不同的配置
  - 避免环境配置混乱

- **示例**：

```java
@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDataSource();
    }
}

@Configuration
@Profile("prod")
public class ProdConfig {
    @Bean
    public DataSource dataSource() {
        return new JndiDataSource();
    }
}
```

## 依赖注入的实现原理

### 1. Spring依赖注入的核心流程

1. **扫描**：扫描指定包路径，发现Bean定义
2. **解析**：解析Bean定义，识别依赖关系
3. **实例化**：创建Bean实例
4. **注入**：注入依赖
5. **初始化**：执行初始化方法
6. **注册**：将Bean注册到容器中

### 2. 依赖注入的实现机制

#### 构造函数注入

- **实现**：通过反射调用构造函数
- **流程**：
  1. 解析构造函数参数
  2. 递归创建依赖Bean
  3. 调用构造函数创建实例

#### Setter方法注入

- **实现**：通过反射调用setter方法
- **流程**：
  1. 创建Bean实例
  2. 解析setter方法
  3. 递归创建依赖Bean
  4. 调用setter方法注入依赖

#### 字段注入

- **实现**：通过反射直接设置字段值
- **流程**：
  1. 创建Bean实例
  2. 解析字段注解
  3. 递归创建依赖Bean
  4. 通过反射设置字段值

### 3. 依赖注入的核心组件

- **BeanDefinition**：Bean的定义信息
- **BeanFactory**：Bean的创建和管理
- **AutowiredAnnotationBeanPostProcessor**：处理@Autowired注解
- **DependencyDescriptor**：依赖的描述信息
- **BeanResolver**：解析Bean的依赖
- **ConstructorResolver**：解析构造函数依赖
- **MethodResolver**：解析方法依赖

## 代码示例：综合使用

### 1. 多种注入方式的综合应用

```java
@Configuration
public class AppConfig {
    // Java配置方式
    @Bean
    public UserService userService(UserRepository userRepository, OrderService orderService) {
        return new UserService(userRepository, orderService);
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
    
    @Bean
    public OrderService orderService() {
        OrderService orderService = new OrderService();
        // Setter方法注入
        orderService.setNotificationService(notificationService());
        return orderService;
    }
    
    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }
}

// 构造函数注入
@Component
public class UserService {
    private final UserRepository userRepository;
    private final OrderService orderService;
    
    // 构造函数注入
    public UserService(UserRepository userRepository, OrderService orderService) {
        this.userRepository = userRepository;
        this.orderService = orderService;
    }
    
    public void processUser(Long userId) {
        User user = userRepository.findById(userId);
        orderService.createOrder(user);
    }
}

// Setter方法注入
@Component
public class OrderService {
    private NotificationService notificationService;
    
    // Setter方法注入
    @Autowired
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    public void createOrder(User user) {
        // 创建订单逻辑
        notificationService.notifyUser(user);
    }
}

// 字段注入
@Component
public class NotificationService {
    // 字段注入
    @Autowired
    private EmailSender emailSender;
    
    public void notifyUser(User user) {
        emailSender.sendEmail(user.getEmail(), "Order Created", "Your order has been created successfully");
    }
}

@Component
public class EmailSender {
    public void sendEmail(String to, String subject, String content) {
        // 发送邮件逻辑
        System.out.println("Sending email to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
    }
}
```

### 2. 复杂依赖注入场景

```java
@Configuration
public class ComplexConfig {
    // 条件注入
    @Bean
    @ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
    public FeatureService featureService() {
        return new FeatureService();
    }
    
    // 延迟注入
    @Bean
    @Lazy
    public HeavyService heavyService() {
        return new HeavyService();
    }
    
    // 集合注入
    @Bean
    public CompositeService compositeService(List<Strategy> strategies, Map<String, Strategy> strategyMap) {
        return new CompositeService(strategies, strategyMap);
    }
}

// 策略接口
public interface Strategy {
    void execute();
}

// 策略实现1
@Component("strategyA")
public class StrategyA implements Strategy {
    @Override
    public void execute() {
        System.out.println("Executing Strategy A");
    }
}

// 策略实现2
@Component("strategyB")
public class StrategyB implements Strategy {
    @Override
    public void execute() {
        System.out.println("Executing Strategy B");
    }
}

// 组合服务
@Component
public class CompositeService {
    private final List<Strategy> strategies;
    private final Map<String, Strategy> strategyMap;
    
    // 构造函数注入
    public CompositeService(List<Strategy> strategies, Map<String, Strategy> strategyMap) {
        this.strategies = strategies;
        this.strategyMap = strategyMap;
    }
    
    public void executeAll() {
        strategies.forEach(Strategy::execute);
    }
    
    public void executeByName(String name) {
        Strategy strategy = strategyMap.get(name);
        if (strategy != null) {
            strategy.execute();
        }
    }
}
```

## 总结

Spring框架提供了丰富的依赖注入方式，每种方式都有其适用场景：

### 主要注入方式

1. **基于注解的注入**：
   - 字段注入：简洁但不推荐
   - 构造函数注入：推荐的主要方式
   - Setter方法注入：适用于可选依赖
   - 方法注入：灵活但较少使用

2. **基于配置的注入**：
   - XML配置：传统方式，适用于遗留系统
   - Java配置：现代方式，类型安全

3. **特殊注入方式**：
   - 自动装配：减少配置
   - 条件注入：环境感知
   - 延迟注入：提高性能
   - 集合注入：批量处理
   - 泛型注入：类型安全
   - Lookup方法注入：解决单例依赖原型问题

### 最佳实践建议

1. **优先使用构造函数注入**：
   - 保证依赖的不可变性
   - 便于单元测试
   - 避免循环依赖

2. **合理使用其他注入方式**：
   - Setter方法注入：适用于可选依赖
   - 字段注入：仅在简单场景使用
   - 特殊注入：根据需要使用

3. **注意事项**：
   - 避免循环依赖
   - 合理使用@Qualifier
   - 注意注入的顺序
   - 考虑Bean的作用域

4. **配置选择**：
   - 现代应用：注解+Java配置
   - 遗留系统：XML配置
   - 复杂场景：混合配置

通过合理选择和使用依赖注入方式，你可以构建更加灵活、可测试、可维护的Spring应用。依赖注入不仅是一种技术，更是一种设计思想，它体现了控制反转和依赖倒置的原则，是构建高质量软件系统的重要手段。