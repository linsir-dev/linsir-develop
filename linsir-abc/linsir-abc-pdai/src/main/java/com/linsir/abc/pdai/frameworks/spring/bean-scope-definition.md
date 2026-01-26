# 如何定义bean的范围？

## Bean作用域概述

**Bean作用域**（Bean Scope）定义了Spring容器中Bean实例的生命周期和可见范围。不同的作用域决定了Bean何时创建、何时销毁，以及如何在不同的请求或会话中共享。

### 作用域的重要性

1. **资源管理**：合理的作用域可以优化资源使用
2. **性能优化**：减少不必要的Bean创建和销毁
3. **数据隔离**：确保不同请求/会话的数据隔离
4. **状态管理**：控制Bean的状态共享方式
5. **内存占用**：影响应用的内存使用情况

### Spring支持的作用域

Spring框架支持以下几种作用域：

| 作用域 | 描述 | 适用场景 |
|-------|------|----------|
| singleton | 单例，整个应用只创建一个实例 | 无状态Bean，如工具类、服务层Bean |
| prototype | 原型，每次请求创建新实例 | 有状态Bean，如命令对象、表单对象 |
| request | 请求作用域，每个HTTP请求创建新实例 | Web应用中与请求相关的Bean |
| session | 会话作用域，每个HTTP会话创建新实例 | Web应用中与会话相关的Bean |
| application | 应用作用域，整个Web应用创建一个实例 | Web应用中全局共享的Bean |
| websocket | WebSocket作用域，每个WebSocket会话创建新实例 | WebSocket应用中使用 |

## 定义Bean作用域的方式

Spring提供了多种方式来定义Bean的作用域，包括：

1. **XML配置方式**
2. **注解方式**
3. **Java配置方式**
4. **编程方式**

### 1. XML配置方式

#### 基本语法

```xml
<bean id="beanName" class="com.example.BeanClass" scope="作用域名称">
    <!-- 属性配置 -->
</bean>
```

#### 示例

```xml
<!-- 单例作用域（默认） -->
<bean id="userService" class="com.example.service.UserService" scope="singleton">
    <property name="userRepository" ref="userRepository"/>
</bean>

<!-- 原型作用域 -->
<bean id="userCommand" class="com.example.command.UserCommand" scope="prototype">
    <property name="userId" value="0"/>
</bean>

<!-- Web相关作用域 -->
<bean id="userSession" class="com.example.model.UserSession" scope="session">
    <aop:scoped-proxy/>
</bean>

<bean id="userRequest" class="com.example.model.UserRequest" scope="request">
    <aop:scoped-proxy/>
</bean>
```

#### 注意事项

- **默认作用域**：如果不指定scope属性，默认是singleton
- **Web作用域**：对于request、session、application、websocket作用域，需要在Web环境中使用
- **作用域代理**：对于Web作用域，通常需要使用`<aop:scoped-proxy/>`来创建代理，以便在单例Bean中引用

### 2. 注解方式

#### 基本语法

使用`@Scope`注解来指定Bean的作用域：

```java
@Scope("作用域名称")
@Component
public class BeanClass {
    // 类定义
}
```

#### 示例

```java
// 单例作用域（默认）
@Scope("singleton")
@Component
public class UserService {
    // 服务类定义
}

// 原型作用域
@Scope("prototype")
@Component
public class UserCommand {
    // 命令类定义
}

// Web相关作用域
@Scope("session")
@Component
public class UserSession {
    // 会话类定义
}

@Scope("request")
@Component
public class UserRequest {
    // 请求类定义
}

// 使用作用域代理
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class UserSession {
    // 会话类定义
}
```

#### 作用域代理模式

`@Scope`注解的`proxyMode`属性用于指定代理模式：

- **ScopedProxyMode.NO**：不使用代理（默认）
- **ScopedProxyMode.INTERFACES**：使用JDK动态代理
- **ScopedProxyMode.TARGET_CLASS**：使用CGLIB动态代理
- **ScopedProxyMode.DEFAULT**：使用默认代理模式

#### 注意事项

- **默认作用域**：如果不使用`@Scope`注解，默认是singleton
- **Web作用域**：需要在Web环境中使用
- **作用域代理**：对于Web作用域，需要指定`proxyMode`以在单例Bean中引用

### 3. Java配置方式

#### 基本语法

在Java配置类中，使用`@Bean`注解的`scope`属性或`@Scope`注解：

```java
@Configuration
public class AppConfig {
    @Bean
    @Scope("作用域名称")
    public BeanClass beanName() {
        return new BeanClass();
    }
}
```

#### 示例

```java
@Configuration
public class AppConfig {
    // 单例作用域（默认）
    @Bean
    @Scope("singleton")
    public UserService userService() {
        return new UserService(userRepository());
    }
    
    // 原型作用域
    @Bean
    @Scope("prototype")
    public UserCommand userCommand() {
        return new UserCommand();
    }
    
    // Web相关作用域
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserSession userSession() {
        return new UserSession();
    }
    
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserRequest userRequest() {
        return new UserRequest();
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
}
```

#### 注意事项

- **默认作用域**：如果不指定，默认是singleton
- **作用域代理**：对于Web作用域，需要指定`proxyMode`
- **依赖注入**：Java配置方式可以更灵活地处理依赖关系

### 4. 编程方式

通过`BeanDefinition`来编程式地设置Bean的作用域：

#### 示例

```java
// 使用DefaultListableBeanFactory
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

// 创建Bean定义
BeanDefinition beanDefinition = new RootBeanDefinition(UserService.class);
// 设置作用域
beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON); // 或其他作用域

// 注册Bean定义
beanFactory.registerBeanDefinition("userService", beanDefinition);

// 获取Bean
UserService userService = (UserService) beanFactory.getBean("userService");
```

#### 作用域常量

Spring提供了作用域的常量定义：

- `BeanDefinition.SCOPE_SINGLETON`：单例
- `BeanDefinition.SCOPE_PROTOTYPE`：原型
- `WebApplicationContext.SCOPE_REQUEST`：请求
- `WebApplicationContext.SCOPE_SESSION`：会话
- `WebApplicationContext.SCOPE_APPLICATION`：应用

## 不同作用域的使用场景

### 1. singleton作用域

#### 适用场景

- **无状态Bean**：如服务层、DAO层、工具类
- **线程安全的Bean**：不存储状态的Bean
- **频繁使用的Bean**：减少创建开销
- **资源密集型Bean**：避免重复创建

#### 示例

```java
@Service
@Scope("singleton") // 默认，可以省略
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id);
    }
    
    public void save(User user) {
        userRepository.save(user);
    }
}
```

### 2. prototype作用域

#### 适用场景

- **有状态Bean**：需要存储状态的Bean
- **命令对象**：如Struts2的Action
- **表单对象**：如Spring MVC的表单绑定对象
- **线程不安全的Bean**：避免线程间状态共享
- **每次使用需要新实例的Bean**：如计算对象

#### 示例

```java
@Component
@Scope("prototype")
public class CalculatorCommand {
    private int result;
    
    public void add(int value) {
        result += value;
    }
    
    public void subtract(int value) {
        result -= value;
    }
    
    public int getResult() {
        return result;
    }
}
```

### 3. request作用域

#### 适用场景

- **Web请求相关的Bean**：如请求参数封装
- **请求级别的缓存**：存储请求期间的数据
- **请求级别的上下文**：如用户认证信息
- **需要请求隔离的Bean**：不同请求使用不同实例

#### 示例

```java
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {
    private Map<String, Object> attributes = new HashMap<>();
    
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    public void removeAttribute(String name) {
        attributes.remove(name);
    }
}
```

### 4. session作用域

#### 适用场景

- **用户会话相关的Bean**：如用户登录信息
- **会话级别的缓存**：存储会话期间的数据
- **用户偏好设置**：如主题、语言设置
- **购物车**：电商应用中的购物车

#### 示例

```java
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    private User currentUser;
    private List<Product> shoppingCart = new ArrayList<>();
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void addToCart(Product product) {
        shoppingCart.add(product);
    }
    
    public List<Product> getShoppingCart() {
        return shoppingCart;
    }
}
```

### 5. application作用域

#### 适用场景

- **Web应用全局共享的Bean**：如应用配置
- **应用级别的缓存**：存储全局缓存数据
- **计数器**：应用级别的统计信息
- **连接池**：全局共享的连接池

#### 示例

```java
@Component
@Scope(value = "application", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ApplicationContext {
    private int requestCount = 0;
    private Map<String, Object> globalCache = new ConcurrentHashMap<>();
    
    public synchronized void incrementRequestCount() {
        requestCount++;
    }
    
    public int getRequestCount() {
        return requestCount;
    }
    
    public void putCache(String key, Object value) {
        globalCache.put(key, value);
    }
    
    public Object getCache(String key) {
        return globalCache.get(key);
    }
}
```

### 6. websocket作用域

#### 适用场景

- **WebSocket会话相关的Bean**：如WebSocket连接信息
- **实时通信数据**：WebSocket会话期间的数据
- **聊天室信息**：每个WebSocket连接的聊天室状态

#### 示例

```java
@Component
@Scope(value = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class WebSocketSessionData {
    private String sessionId;
    private String userName;
    private List<String> messages = new ArrayList<>();
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void addMessage(String message) {
        messages.add(message);
    }
    
    public List<String> getMessages() {
        return messages;
    }
}
```

## 作用域代理的使用

### 为什么需要作用域代理？

当一个单例Bean引用一个非单例Bean（如prototype、request、session等）时，默认情况下，单例Bean只会在初始化时获取一次非单例Bean的实例，之后一直使用同一个实例。这可能导致：

1. **原型Bean不生效**：单例Bean始终使用同一个原型Bean实例
2. **Web作用域Bean错误**：在非Web环境中无法创建Web作用域Bean
3. **数据共享问题**：不同请求/会话共享同一个Bean实例

### 作用域代理的工作原理

作用域代理通过创建一个代理对象来包装目标Bean，当调用代理对象的方法时，代理会根据当前的作用域上下文获取或创建真实的Bean实例。

### 配置方式

#### XML配置

```xml
<bean id="sessionBean" class="com.example.SessionBean" scope="session">
    <aop:scoped-proxy/>
</bean>
```

#### 注解配置

```java
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
public class SessionBean {
    // 类定义
}
```

#### Java配置

```java
@Bean
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public SessionBean sessionBean() {
    return new SessionBean();
}
```

### 代理模式选择

1. **ScopedProxyMode.INTERFACES**：
   - 使用JDK动态代理
   - 要求目标Bean实现接口
   - 性能较好

2. **ScopedProxyMode.TARGET_CLASS**：
   - 使用CGLIB动态代理
   - 不需要目标Bean实现接口
   - 适用范围更广

3. **ScopedProxyMode.NO**：
   - 不使用代理
   - 适用于直接注入的场景

### 示例

```java
// 单例Bean引用会话作用域Bean
@Service
public class UserService {
    // 使用代理，每次调用都会获取当前会话的UserSession实例
    @Autowired
    private UserSession userSession;
    
    public User getCurrentUser() {
        // 每次调用都会通过代理获取当前会话的UserSession
        return userSession.getCurrentUser();
    }
}

// 会话作用域Bean
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserSession {
    private User currentUser;
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
}
```

## 自定义Bean作用域

除了使用Spring提供的默认作用域，你还可以自定义Bean作用域。

### 自定义作用域的步骤

1. **实现Scope接口**
2. **注册自定义作用域**
3. **使用自定义作用域**

### 示例：自定义线程作用域

#### 1. 实现Scope接口

```java
public class ThreadScope implements Scope {
    private final ThreadLocal<Map<String, Object>> threadLocal = 
        ThreadLocal.withInitial(HashMap::new);
    
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> scope = threadLocal.get();
        Object object = scope.get(name);
        if (object == null) {
            object = objectFactory.getObject();
            scope.put(name, object);
        }
        return object;
    }
    
    @Override
    public Object remove(String name) {
        Map<String, Object> scope = threadLocal.get();
        return scope.remove(name);
    }
    
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // 可选实现
    }
    
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
    
    @Override
    public String getConversationId() {
        return Thread.currentThread().getName();
    }
    
    // 清理方法
    public void clear() {
        threadLocal.remove();
    }
}
```

#### 2. 注册自定义作用域

**XML配置方式**：

```xml
<bean class="org.springframework.beans.factory.config.CustomScopeConfigurer">
    <property name="scopes">
        <map>
            <entry key="thread">
                <bean class="com.example.ThreadScope"/>
            </entry>
        </map>
    </property>
</bean>
```

**Java配置方式**：

```java
@Configuration
public class AppConfig {
    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer();
        Map<String, Object> scopes = new HashMap<>();
        scopes.put("thread", new ThreadScope());
        configurer.setScopes(scopes);
        return configurer;
    }
}
```

**编程方式**：

```java
DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
// 注册自定义作用域
beanFactory.registerScope("thread", new ThreadScope());
// 注册Bean定义
BeanDefinition beanDefinition = new RootBeanDefinition(ThreadScopedBean.class);
beanDefinition.setScope("thread");
beanFactory.registerBeanDefinition("threadScopedBean", beanDefinition);
```

#### 3. 使用自定义作用域

**XML配置**：

```xml
<bean id="threadScopedBean" class="com.example.ThreadScopedBean" scope="thread">
    <aop:scoped-proxy/>
</bean>
```

**注解配置**：

```java
@Scope("thread")
@Component
public class ThreadScopedBean {
    private String value;
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
```

**Java配置**：

```java
@Bean
@Scope("thread")
public ThreadScopedBean threadScopedBean() {
    return new ThreadScopedBean();
}
```

## 作用域的高级特性

### 1. 作用域的生命周期管理

#### 单例Bean
- **创建**：容器启动时（默认）
- **初始化**：创建后执行初始化方法
- **销毁**：容器关闭时执行销毁方法

#### 原型Bean
- **创建**：每次请求时
- **初始化**：创建后执行初始化方法
- **销毁**：容器不管理原型Bean的销毁，需要手动处理

#### Web作用域Bean
- **创建**：相应作用域开始时
- **初始化**：创建后执行初始化方法
- **销毁**：相应作用域结束时执行销毁方法

### 2. 作用域的依赖注入

#### 单例注入原型
- **问题**：单例Bean只在初始化时获取一次原型Bean
- **解决方案**：使用作用域代理或`ObjectFactory`

#### 原型注入单例
- **直接注入**：原型Bean可以直接注入单例Bean
- **注意事项**：原型Bean会持有单例Bean的引用

#### Web作用域注入
- **必须使用代理**：否则会在容器启动时出错
- **代理模式**：根据Bean类型选择合适的代理模式

### 3. 作用域的性能优化

1. **单例Bean**：
   - 优点：减少创建开销，共享实例
   - 缺点：需要确保线程安全

2. **原型Bean**：
   - 优点：线程安全，状态隔离
   - 缺点：增加创建开销，内存占用

3. **Web作用域Bean**：
   - 优点：请求/会话隔离
   - 缺点：需要额外的代理开销

4. **优化策略**：
   - 优先使用单例Bean（无状态）
   - 合理使用原型Bean（有状态）
   - 谨慎使用Web作用域Bean
   - 使用缓存减少Bean创建

### 4. 作用域的线程安全

#### 单例Bean
- **默认不安全**：多线程共享同一个实例
- **解决方案**：
  - 无状态设计
  - 使用局部变量
  - 线程安全集合
  - 同步机制
  - ThreadLocal（谨慎使用）

#### 原型Bean
- **默认安全**：每个线程获取新实例
- **注意事项**：如果原型Bean持有共享资源，仍需考虑线程安全

#### Web作用域Bean
- **请求作用域**：线程安全（每个请求一个线程）
- **会话作用域**：可能不安全（多个请求共享会话）
- **解决方案**：会话作用域Bean需要考虑线程安全

## 常见问题和解决方案

### 1. 原型作用域不生效

**问题**：使用@Autowired注入原型Bean，但每次获取的都是同一个实例

**原因**：单例Bean在初始化时只注入一次原型Bean

**解决方案**：
- 使用`ObjectFactory`或`Provider`
- 使用作用域代理
- 实现`ApplicationContextAware`，每次从容器获取

```java
// 解决方案1：使用ObjectFactory
@Service
public class UserService {
    @Autowired
    private ObjectFactory<UserCommand> userCommandFactory;
    
    public void processUser() {
        // 每次获取新实例
        UserCommand command = userCommandFactory.getObject();
        // 使用command
    }
}

// 解决方案2：使用Provider（JSR-330）
@Service
public class UserService {
    @Autowired
    private Provider<UserCommand> userCommandProvider;
    
    public void processUser() {
        // 每次获取新实例
        UserCommand command = userCommandProvider.get();
        // 使用command
    }
}
```

### 2. Web作用域Bean在非Web环境中报错

**问题**：在非Web环境中使用request/session作用域Bean报错

**原因**：非Web环境中没有相应的作用域上下文

**解决方案**：
- 在非Web环境中使用不同的配置
- 使用条件注解@Profile
- 提供默认实现

```java
// 解决方案：使用条件配置
@Configuration
@Profile("web")
public class WebConfig {
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserSession userSession() {
        return new UserSession();
    }
}

@Configuration
@Profile({"default", "test"})
public class DefaultConfig {
    @Bean
    @Scope("singleton")
    public UserSession userSession() {
        return new DefaultUserSession();
    }
}
```

### 3. 作用域代理导致的性能问题

**问题**：使用作用域代理后性能下降

**原因**：每次方法调用都需要通过代理

**解决方案**：
- 减少代理Bean的方法调用
- 合理设计Bean结构
- 考虑使用`ObjectFactory`代替代理
- 优化代理模式选择

### 4. 自定义作用域不生效

**问题**：自定义作用域注册后不生效

**原因**：
- 注册方式不正确
- 作用域实现有问题
- 容器类型不支持

**解决方案**：
- 确保正确注册作用域
- 检查作用域实现
- 使用`CustomScopeConfigurer`注册
- 测试作用域功能

### 5. 作用域与延迟加载冲突

**问题**：使用@Lazy注解与作用域冲突

**原因**：延迟加载和作用域的初始化时机不同

**解决方案**：
- 理解两者的初始化时机
- 合理组合使用
- 测试不同组合的效果

```java
// 正确的组合方式
@Scope("prototype")
@Lazy
@Component
public class LazyPrototypeBean {
    // 类定义
}
```

## 代码示例：综合使用

### 1. 多作用域Bean的配置

```java
@Configuration
public class ScopeConfig {
    // 单例作用域（默认）
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
    
    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }
    
    // 原型作用域
    @Bean
    @Scope("prototype")
    public UserForm userForm() {
        return new UserForm();
    }
    
    // 请求作用域
    @Bean
    @Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public RequestContext requestContext() {
        return new RequestContext();
    }
    
    // 会话作用域
    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserSession userSession() {
        return new UserSession();
    }
    
    // 应用作用域
    @Bean
    @Scope(value = "application", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AppContext appContext() {
        return new AppContext();
    }
}
```

### 2. 作用域代理的使用

```java
@Service
public class UserService {
    // 注入会话作用域Bean（使用代理）
    @Autowired
    private UserSession userSession;
    
    // 注入原型Bean（使用ObjectFactory）
    @Autowired
    private ObjectFactory<UserForm> userFormFactory;
    
    public User getCurrentUser() {
        // 每次调用都会通过代理获取当前会话的UserSession
        return userSession.getCurrentUser();
    }
    
    public void processUserRequest() {
        // 每次获取新的UserForm实例
        UserForm userForm = userFormFactory.getObject();
        // 使用userForm处理请求
    }
}
```

### 3. 自定义作用域的测试

```java
public class CustomScopeTest {
    public static void main(String[] args) {
        // 创建容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 测试线程作用域
        Runnable task1 = () -> {
            ThreadScopedBean bean = context.getBean(ThreadScopedBean.class);
            bean.setValue("Task 1 Value");
            System.out.println(Thread.currentThread().getName() + ": " + bean.getValue());
        };
        
        Runnable task2 = () -> {
            ThreadScopedBean bean = context.getBean(ThreadScopedBean.class);
            bean.setValue("Task 2 Value");
            System.out.println(Thread.currentThread().getName() + ": " + bean.getValue());
        };
        
        // 启动两个线程
        new Thread(task1, "Thread-1").start();
        new Thread(task2, "Thread-2").start();
        
        // 关闭容器
        context.close();
    }
}

// 输出结果：
// Thread-1: Task 1 Value
// Thread-2: Task 2 Value
```

## 总结

定义Bean作用域是Spring框架中的重要概念，它决定了Bean的生命周期和可见范围。Spring提供了多种作用域和定义方式：

### 作用域类型

1. **singleton**：单例，默认作用域，整个应用一个实例
2. **prototype**：原型，每次请求一个新实例
3. **request**：请求作用域，每个HTTP请求一个实例
4. **session**：会话作用域，每个HTTP会话一个实例
5. **application**：应用作用域，整个Web应用一个实例
6. **websocket**：WebSocket作用域，每个WebSocket会话一个实例
7. **自定义作用域**：根据需要创建

### 定义方式

1. **XML配置**：传统方式，适用于所有Spring版本
2. **注解配置**：现代方式，使用@Scope注解
3. **Java配置**：类型安全，使用@Bean注解
4. **编程方式**：灵活，使用BeanDefinition

### 最佳实践

1. **优先使用单例作用域**：
   - 无状态Bean使用singleton
   - 确保线程安全

2. **合理使用原型作用域**：
   - 有状态Bean使用prototype
   - 注意性能影响

3. **谨慎使用Web作用域**：
   - 必要时使用request/session作用域
   - 必须使用作用域代理

4. **作用域代理**：
   - 单例Bean引用非单例Bean时使用
   - 根据需要选择代理模式

5. **性能考虑**：
   - 平衡Bean创建开销和内存占用
   - 使用缓存优化性能

6. **线程安全**：
   - 单例Bean必须考虑线程安全
   - 会话作用域Bean也需要考虑线程安全

7. **测试**：
   - 测试不同作用域的行为
   - 验证作用域代理的效果

通过合理定义和使用Bean作用域，你可以构建更加高效、安全、可维护的Spring应用。