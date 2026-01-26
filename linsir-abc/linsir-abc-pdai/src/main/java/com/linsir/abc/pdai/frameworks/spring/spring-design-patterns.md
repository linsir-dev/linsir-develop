# Spring框架中用到了哪些设计模式？

## 设计模式概述

设计模式是在软件开发中反复出现的问题的解决方案，是经过验证的最佳实践。Spring框架作为一个成熟的企业级应用框架，广泛采用了多种设计模式，这些设计模式使得Spring框架更加灵活、可扩展和易于维护。

### 设计模式的分类

根据GoF（Gang of Four）的分类，设计模式可以分为三大类：

1. **创建型模式**：处理对象的创建过程
   - 单例模式、工厂方法模式、抽象工厂模式、建造者模式、原型模式

2. **结构型模式**：处理对象的组合和结构
   - 适配器模式、装饰器模式、代理模式、外观模式、桥接模式、组合模式、享元模式

3. **行为型模式**：处理对象之间的通信和职责分配
   - 策略模式、模板方法模式、观察者模式、迭代器模式、责任链模式、命令模式、备忘录模式、状态模式、访问者模式、中介者模式、解释器模式

## Spring框架中使用的设计模式

### 1. 单例模式 (Singleton)

**概念**：确保一个类只有一个实例，并提供一个全局访问点。

**在Spring中的应用**：
- Spring容器中的Bean默认是单例的
- 通过`singleton`作用域实现
- 使用双重检查锁定确保线程安全

**核心实现**：

```java
// Spring中的单例实现（简化版）
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {
    // 存储单例Bean的缓存
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
    private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);
    
    @Override
    public Object getSingleton(String beanName) {
        // 1. 从一级缓存获取
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null) {
            synchronized (this.singletonObjects) {
                // 2. 从二级缓存获取
                singletonObject = earlySingletonObjects.get(beanName);
                if (singletonObject == null) {
                    // 3. 从三级缓存获取
                    ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                    if (singletonFactory != null) {
                        singletonObject = singletonFactory.getObject();
                        // 移到二级缓存
                        earlySingletonObjects.put(beanName, singletonObject);
                        // 从三级缓存移除
                        singletonFactories.remove(beanName);
                    }
                }
            }
        }
        return singletonObject;
    }
}
```

**使用场景**：
- 无状态的Bean，如Service、Repository等
- 工具类和配置类

### 2. 工厂方法模式 (Factory Method)

**概念**：定义一个创建对象的接口，但让子类决定要实例化的类是哪一个。

**在Spring中的应用**：
- `BeanFactory`接口：Spring容器的核心接口，定义了获取Bean的方法
- `FactoryBean`接口：允许自定义Bean的创建过程

**核心实现**：

```java
// BeanFactory接口
public interface BeanFactory {
    Object getBean(String name) throws BeansException;
    <T> T getBean(String name, Class<T> requiredType) throws BeansException;
    <T> T getBean(Class<T> requiredType) throws BeansException;
    Object getBean(String name, Object... args) throws BeansException;
    boolean containsBean(String name);
    boolean isSingleton(String name) throws NoSuchBeanDefinitionException;
    // 其他方法...
}

// FactoryBean接口
public interface FactoryBean<T> {
    T getObject() throws Exception;
    Class<?> getObjectType();
    boolean isSingleton();
}
```

**使用场景**：
- 创建复杂的Bean，如数据源、事务管理器等
- 集成第三方库

### 3. 抽象工厂模式 (Abstract Factory)

**概念**：提供一个创建一系列相关或相互依赖对象的接口，而无需指定它们具体的类。

**在Spring中的应用**：
- `ApplicationContext`接口：扩展了`BeanFactory`，提供了更多企业级功能
- `WebApplicationContext`：为Web应用提供特定的上下文

**核心实现**：

```java
// ApplicationContext接口
public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
        MessageSource, ApplicationEventPublisher, ResourcePatternResolver {
    // 继承了多个接口，提供了丰富的功能
}

// WebApplicationContext接口
public interface WebApplicationContext extends ApplicationContext {
    ServletContext getServletContext();
}
```

**使用场景**：
- 不同环境下的Bean配置
- 企业级应用的上下文管理

### 4. 原型模式 (Prototype)

**概念**：用原型实例指定创建对象的种类，并且通过拷贝这个原型来创建新的对象。

**在Spring中的应用**：
- `prototype`作用域的Bean
- 通过`BeanDefinition`的`scope`属性控制

**核心实现**：

```java
// BeanDefinition中的作用域设置
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {
    String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
    String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;
    // 其他方法...
}

// 创建原型Bean的逻辑
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
    // ...
    Object beanInstance = doCreateBean(beanName, mbdToUse, args);
    // ...
    return beanInstance;
}

protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
    // ...
    if (instanceWrapper == null) {
        instanceWrapper = createBeanInstance(beanName, mbd, args);
    }
    // ...
    return exposedObject;
}
```

**使用场景**：
- 有状态的Bean，如Request、Session作用域的Bean
- 需要每次获取新实例的场景

### 5. 适配器模式 (Adapter)

**概念**：将一个类的接口转换成客户希望的另一个接口，使得原本由于接口不兼容而不能一起工作的类可以一起工作。

**在Spring中的应用**：
- `HandlerAdapter`：适配不同类型的处理器
- `ViewResolver`：适配不同类型的视图
- `WebMvcConfigurerAdapter`：适配Web MVC配置

**核心实现**：

```java
// HandlerAdapter接口
public interface HandlerAdapter {
    boolean supports(Object handler);
    ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception;
    long getLastModified(HttpServletRequest request, Object handler);
}

// 不同的HandlerAdapter实现
public class SimpleControllerHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof Controller);
    }
    // 其他方法...
}

public class RequestMappingHandlerAdapter implements HandlerAdapter {
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof HandlerMethod);
    }
    // 其他方法...
}
```

**使用场景**：
- 集成不同类型的处理器
- 适配不同的视图技术
- 提供向后兼容的接口

### 6. 装饰器模式 (Decorator)

**概念**：动态地给一个对象添加一些额外的职责，就增加功能来说，装饰器模式比生成子类更为灵活。

**在Spring中的应用**：
- `BeanWrapper`：对Bean进行包装，提供额外的功能
- `HttpServletRequestWrapper`：对HTTP请求进行包装
- `TransactionAwareDataSourceProxy`：对数据源进行事务管理装饰

**核心实现**：

```java
// BeanWrapper接口
public interface BeanWrapper extends ConfigurablePropertyAccessor {
    Object getWrappedInstance();
    Class<?> getWrappedClass();
    PropertyDescriptor[] getPropertyDescriptors();
    PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException;
}

// BeanWrapperImpl实现
public class BeanWrapperImpl extends AbstractPropertyAccessor implements BeanWrapper {
    private Object wrappedObject;
    private Class<?> wrappedClass;
    // 其他方法...
}
```

**使用场景**：
- 为Bean添加额外的属性访问功能
- 为数据源添加事务支持
- 为HTTP请求添加额外的信息

### 7. 代理模式 (Proxy)

**概念**：为其他对象提供一种代理以控制对这个对象的访问。

**在Spring中的应用**：
- AOP代理：实现面向切面编程
- 事务代理：实现声明式事务
- 远程代理：实现远程服务调用

**核心实现**：

```java
// AOP代理工厂
public class ProxyFactory extends ProxyCreatorSupport {
    public ProxyFactory() {
    }
    
    public ProxyFactory(Class<?>... interfaces) {
        addInterfaces(interfaces);
    }
    
    public ProxyFactory(Object target) {
        setTarget(target);
        setInterfaces(ClassUtils.getAllInterfaces(target));
    }
    
    public Object getProxy() {
        return createAopProxy().getProxy();
    }
    
    public Object getProxy(ClassLoader classLoader) {
        return createAopProxy().getProxy(classLoader);
    }
}

// AOP代理接口
public interface AopProxy {
    Object getProxy();
    Object getProxy(ClassLoader classLoader);
}
```

**使用场景**：
- 实现横切关注点，如日志、事务、安全等
- 延迟加载重量级对象
- 控制对对象的访问权限

### 8. 外观模式 (Facade)

**概念**：提供一个统一的接口，用来访问子系统中的一群接口，从而使得子系统更容易使用。

**在Spring中的应用**：
- `ApplicationContext`：作为Spring容器的外观，提供了统一的接口
- `JdbcTemplate`：作为JDBC操作的外观，简化了JDBC的使用
- `TransactionTemplate`：作为事务操作的外观，简化了事务的使用

**核心实现**：

```java
// JdbcTemplate
public class JdbcTemplate extends JdbcAccessor implements JdbcOperations {
    @Override
    public <T> T execute(StatementCallback<T> action) throws DataAccessException {
        // 实现
    }
    
    @Override
    public <T> T query(String sql, ResultSetExtractor<T> rse) throws DataAccessException {
        // 实现
    }
    
    @Override
    public int update(String sql) throws DataAccessException {
        // 实现
    }
    // 其他方法...
}

// TransactionTemplate
public class TransactionTemplate extends DefaultTransactionDefinition implements TransactionOperations, InitializingBean {
    @Override
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        // 实现
    }
}
```

**使用场景**：
- 简化复杂子系统的使用
- 提供统一的接口，隐藏实现细节
- 减少客户端与子系统之间的依赖

### 9. 观察者模式 (Observer)

**概念**：定义对象间的一种一对多依赖关系，使得每当一个对象状态发生变化时，其相关依赖对象都得到通知并被自动更新。

**在Spring中的应用**：
- `ApplicationEventPublisher`：发布应用事件
- `ApplicationListener`：监听应用事件
- `ContextRefreshedEvent`、`ContextClosedEvent`等：Spring容器的生命周期事件

**核心实现**：

```java
// ApplicationEventPublisher接口
public interface ApplicationEventPublisher {
    void publishEvent(ApplicationEvent event);
    void publishEvent(Object event);
}

// ApplicationListener接口
@FunctionalInterface
public interface ApplicationListener<E extends ApplicationEvent> extends EventListener {
    void onApplicationEvent(E event);
}

// 事件发布实现
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {
    @Override
    public void publishEvent(ApplicationEvent event) {
        publishEvent(event, null);
    }
    
    protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
        // 实现事件发布
    }
}
```

**使用场景**：
- 应用事件的处理，如容器启动、关闭等
- 业务事件的发布和订阅
- 解耦事件发布者和订阅者

### 10. 策略模式 (Strategy)

**概念**：定义一系列的算法，把它们一个个封装起来，并且使它们可相互替换，使得算法可独立于使用它的客户而变化。

**在Spring中的应用**：
- `ResourceLoader`：资源加载策略
- `PropertyResolver`：属性解析策略
- `TransactionManager`：事务管理策略
- `ViewResolver`：视图解析策略

**核心实现**：

```java
// ResourceLoader接口
public interface ResourceLoader {
    Resource getResource(String location);
    ClassLoader getClassLoader();
}

// 不同的ResourceLoader实现
public class DefaultResourceLoader implements ResourceLoader {
    @Override
    public Resource getResource(String location) {
        // 实现
    }
}

public class FileSystemResourceLoader extends DefaultResourceLoader {
    @Override
    protected Resource getResourceByPath(String path) {
        // 实现
    }
}
```

**使用场景**：
- 多种资源加载方式
- 多种事务管理策略
- 多种视图解析策略

### 11. 模板方法模式 (Template Method)

**概念**：定义一个算法的骨架，而将一些步骤延迟到子类中，使得子类可以不改变算法的结构即可重定义该算法的某些特定步骤。

**在Spring中的应用**：
- `JdbcTemplate`：JDBC操作的模板方法
- `HibernateTemplate`：Hibernate操作的模板方法
- `TransactionTemplate`：事务操作的模板方法
- `AbstractPlatformTransactionManager`：事务管理器的模板方法

**核心实现**：

```java
// JdbcTemplate中的模板方法
public <T> T execute(StatementCallback<T> action) throws DataAccessException {
    Connection con = DataSourceUtils.getConnection(getDataSource());
    Statement stmt = null;
    try {
        stmt = con.createStatement();
        applyStatementSettings(stmt);
        T result = action.doInStatement(stmt); // 回调方法
        handleWarnings(stmt);
        return result;
    } catch (SQLException ex) {
        String sql = getSql(action);
        JdbcUtils.closeStatement(stmt);
        stmt = null;
        DataSourceUtils.releaseConnection(con, getDataSource());
        con = null;
        throw translateException("StatementCallback", sql, ex);
    } finally {
        JdbcUtils.closeStatement(stmt);
        DataSourceUtils.releaseConnection(con, getDataSource());
    }
}

// 回调接口
@FunctionalInterface
public interface StatementCallback<T> {
    T doInStatement(Statement stmt) throws SQLException, DataAccessException;
}
```

**使用场景**：
- 数据库操作
- 事务管理
- 资源获取和释放

### 12. 责任链模式 (Chain of Responsibility)

**概念**：为请求创建一个接收者对象的链，使得多个对象都有机会处理请求，从而避免请求的发送者和接收者之间的耦合关系。

**在Spring中的应用**：
- `HandlerInterceptor`：拦截器链
- `Filter`：过滤器链
- `ExceptionHandler`：异常处理器链
- `BeanPostProcessor`：Bean处理器链

**核心实现**：

```java
// HandlerInterceptor接口
public interface HandlerInterceptor {
    default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return true;
    }
    
    default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
    }
    
    default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
    }
}

// 拦截器链的执行
public class HandlerExecutionChain {
    private final Object handler;
    private final List<HandlerInterceptor> interceptors;
    
    public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HandlerInterceptor[] interceptors = getInterceptors();
        if (!ObjectUtils.isEmpty(interceptors)) {
            for (int i = 0; i < interceptors.length; i++) {
                HandlerInterceptor interceptor = interceptors[i];
                if (!interceptor.preHandle(request, response, this.handler)) {
                    triggerAfterCompletion(request, response, null);
                    return false;
                }
                this.interceptorIndex = i;
            }
        }
        return true;
    }
    
    // 其他方法...
}
```

**使用场景**：
- 请求拦截和处理
- 异常处理
- Bean的生命周期处理

### 13. 命令模式 (Command)

**概念**：将一个请求封装为一个对象，从而使你可用不同的请求对客户进行参数化，对请求排队或记录请求日志，以及支持可撤销的操作。

**在Spring中的应用**：
- `JmsTemplate`：JMS消息发送
- `RedisTemplate`：Redis操作
- `TransactionCallback`：事务操作回调

**核心实现**：

```java
// TransactionCallback接口
@FunctionalInterface
public interface TransactionCallback<T> {
    T doInTransaction(TransactionStatus status);
}

// TransactionOperations接口
public interface TransactionOperations {
    <T> T execute(TransactionCallback<T> action) throws TransactionException;
}

// TransactionTemplate实现
public class TransactionTemplate extends DefaultTransactionDefinition implements TransactionOperations, InitializingBean {
    @Override
    public <T> T execute(TransactionCallback<T> action) throws TransactionException {
        // 实现
    }
}
```

**使用场景**：
- 事务操作
- 消息发送
- 远程调用

### 14. 迭代器模式 (Iterator)

**概念**：提供一种方法来访问一个容器对象中的各个元素，而又不暴露该对象的内部表示。

**在Spring中的应用**：
- `BeanFactoryUtils`：遍历BeanFactory中的Bean
- `PropertySources`：遍历属性源
- `ResourcePatternResolver`：遍历资源

**核心实现**：

```java
// PropertySources接口
public interface PropertySources extends Iterable<PropertySource<?>> {
    boolean contains(String name);
    @Nullable
    PropertySource<?> get(String name);
}

// MutablePropertySources实现
public class MutablePropertySources implements PropertySources {
    private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();
    
    @Override
    public Iterator<PropertySource<?>> iterator() {
        return this.propertySourceList.iterator();
    }
    
    // 其他方法...
}
```

**使用场景**：
- 遍历容器中的元素
- 访问集合对象的元素，而不暴露其内部结构

### 15. 组合模式 (Composite)

**概念**：将对象组合成树形结构以表示"部分-整体"的层次结构，使得用户对单个对象和组合对象的使用具有一致性。

**在Spring中的应用**：
- `ApplicationContext`的层次结构
- `BeanDefinition`的层次结构
- `Resource`的层次结构

**核心实现**：

```java
// ApplicationContext的层次结构
public interface HierarchicalBeanFactory extends BeanFactory {
    @Nullable
    BeanFactory getParentBeanFactory();
    boolean containsLocalBean(String name);
}

// AbstractApplicationContext实现
public abstract class AbstractApplicationContext extends DefaultResourceLoader
        implements ConfigurableApplicationContext {
    @Override
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return getParent();
    }
    
    @Override
    public boolean containsLocalBean(String name) {
        return getBeanFactory().containsLocalBean(name);
    }
    
    // 其他方法...
}
```

**使用场景**：
- 容器的层次结构
- 配置的层次结构
- 资源的层次结构

### 16. 桥接模式 (Bridge)

**概念**：将抽象部分与它的实现部分分离，使它们都可以独立地变化。

**在Spring中的应用**：
- `DataSource`与`Connection`：数据源与连接的桥接
- `Resource`与`ResourceLoader`：资源与资源加载器的桥接
- `MessageSource`与`MessageSourceResolvable`：消息源与消息解析的桥接

**核心实现**：

```java
// Resource接口
public interface Resource extends InputStreamSource {
    boolean exists();
    boolean isReadable();
    boolean isOpen();
    URL getURL() throws IOException;
    URI getURI() throws IOException;
    File getFile() throws IOException;
    long contentLength() throws IOException;
    long lastModified() throws IOException;
    Resource createRelative(String relativePath) throws IOException;
    String getFilename();
    String getDescription();
}

// ResourceLoader接口
public interface ResourceLoader {
    Resource getResource(String location);
    ClassLoader getClassLoader();
}
```

**使用场景**：
- 抽象与实现的分离
- 多维度的变化
- 避免类爆炸

### 17. 享元模式 (Flyweight)

**概念**：运用共享技术有效地支持大量细粒度的对象。

**在Spring中的应用**：
- `BeanDefinition`：Bean定义的共享
- `Class`对象：类对象的共享
- `MessageSource`：消息源的共享

**核心实现**：

```java
// BeanDefinition的缓存
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
        implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {
    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private final List<String> beanDefinitionNames = new ArrayList<>(256);
    
    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        // 注册BeanDefinition
    }
    
    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        // 获取BeanDefinition
    }
    
    // 其他方法...
}
```

**使用场景**：
- 大量相似对象的共享
- 减少内存占用
- 提高性能

### 18. 状态模式 (State)

**概念**：允许对象在内部状态改变时改变它的行为，对象看起来似乎修改了它的类。

**在Spring中的应用**：
- `TransactionStatus`：事务的不同状态
- `Lifecycle`：Bean的生命周期状态
- `ApplicationContext`的状态：启动、运行、关闭等状态

**核心实现**：

```java
// TransactionStatus接口
public interface TransactionStatus extends SavepointManager, Flushable {
    boolean isNewTransaction();
    boolean hasSavepoint();
    void setRollbackOnly();
    boolean isRollbackOnly();
    void flush();
    boolean isCompleted();
}

// DefaultTransactionStatus实现
public class DefaultTransactionStatus extends AbstractTransactionStatus {
    private final Object transaction;
    private final boolean newTransaction;
    private final boolean newSynchronization;
    private final boolean readOnly;
    private final boolean debug;
    private final Object suspendedResources;
    
    // 实现方法...
}
```

**使用场景**：
- 对象状态的管理
- 状态转换的处理
- 避免复杂的条件判断

### 19. 访问者模式 (Visitor)

**概念**：表示一个作用于某对象结构中的各元素的操作，它使你可以在不改变各元素的类的前提下定义作用于这些元素的新操作。

**在Spring中的应用**：
- `BeanDefinitionVisitor`：访问和修改Bean定义
- `PropertyAccessor`：访问对象的属性
- `BeanWrapper`：包装Bean，提供属性访问功能

**核心实现**：

```java
// BeanDefinitionVisitor
public class BeanDefinitionVisitor {
    private final StringValueResolver valueResolver;
    
    public BeanDefinitionVisitor(StringValueResolver valueResolver) {
        this.valueResolver = valueResolver;
    }
    
    public void visitBeanDefinition(BeanDefinition beanDefinition) {
        visitParentName(beanDefinition);
        visitBeanClassName(beanDefinition);
        visitFactoryBeanName(beanDefinition);
        visitFactoryMethodName(beanDefinition);
        visitScope(beanDefinition);
        visitPropertyValues(beanDefinition.getPropertyValues());
        ConstructorArgumentValues cas = beanDefinition.getConstructorArgumentValues();
        visitIndexedArgumentValues(cas.getIndexedArgumentValues());
        visitGenericArgumentValues(cas.getGenericArgumentValues());
    }
    
    // 其他方法...
}
```

**使用场景**：
- 对对象结构的操作，而不修改其类
- 为对象结构添加新的操作
- 分离对象结构和操作

### 20. 解释器模式 (Interpreter)

**概念**：给定一个语言，定义它的文法的一种表示，并定义一个解释器，使用该表示来解释语言中的句子。

**在Spring中的应用**：
- `SpEL` (Spring Expression Language)：Spring表达式语言
- `PropertyPlaceholderConfigurer`：属性占位符的解析
- `AntPathMatcher`：路径模式的匹配

**核心实现**：

```java
// SpEL表达式解析
public interface ExpressionParser {
    Expression parseExpression(String expressionString) throws ParseException;
    Expression parseExpression(String expressionString, ParserContext context) throws ParseException;
}

// SpelExpressionParser实现
public class SpelExpressionParser implements ExpressionParser {
    private final SpelParserConfiguration configuration;
    
    public SpelExpressionParser() {
        this.configuration = new SpelParserConfiguration();
    }
    
    @Override
    public Expression parseExpression(String expressionString) throws ParseException {
        return parseExpression(expressionString, null);
    }
    
    @Override
    public Expression parseExpression(String expressionString, ParserContext context) throws ParseException {
        // 实现
    }
}
```

**使用场景**：
- 表达式语言的解析
- 配置文件中的占位符解析
- 路径模式的匹配

## 设计模式在Spring中的应用总结

### 创建型模式的应用

| 设计模式 | 应用场景 | 核心实现 |
|---------|---------|----------|
| 单例模式 | Spring容器中的Bean默认作用域 | `DefaultSingletonBeanRegistry` |
| 工厂方法模式 | `BeanFactory`、`FactoryBean` | `BeanFactory`接口 |
| 抽象工厂模式 | `ApplicationContext`、`WebApplicationContext` | `ApplicationContext`接口 |
| 原型模式 | `prototype`作用域的Bean | `BeanDefinition`的`scope`属性 |

### 结构型模式的应用

| 设计模式 | 应用场景 | 核心实现 |
|---------|---------|----------|
| 适配器模式 | `HandlerAdapter`、`ViewResolver` | `HandlerAdapter`接口 |
| 装饰器模式 | `BeanWrapper`、`TransactionAwareDataSourceProxy` | `BeanWrapper`接口 |
| 代理模式 | AOP代理、事务代理 | `AopProxy`接口 |
| 外观模式 | `JdbcTemplate`、`TransactionTemplate` | `JdbcTemplate`类 |
| 桥接模式 | `DataSource`与`Connection` | `DataSource`接口 |
| 组合模式 | `ApplicationContext`的层次结构 | `HierarchicalBeanFactory`接口 |
| 享元模式 | `BeanDefinition`的缓存 | `DefaultListableBeanFactory` |

### 行为型模式的应用

| 设计模式 | 应用场景 | 核心实现 |
|---------|---------|----------|
| 观察者模式 | 应用事件的发布和订阅 | `ApplicationEventPublisher`接口 |
| 策略模式 | 资源加载、事务管理 | `ResourceLoader`接口 |
| 模板方法模式 | `JdbcTemplate`、`TransactionTemplate` | `JdbcTemplate`类 |
| 责任链模式 | 拦截器链、异常处理器链 | `HandlerInterceptor`接口 |
| 命令模式 | 事务操作、消息发送 | `TransactionCallback`接口 |
| 迭代器模式 | 遍历容器中的元素 | `PropertySources`接口 |
| 状态模式 | 事务状态、Bean生命周期状态 | `TransactionStatus`接口 |
| 访问者模式 | 访问和修改Bean定义 | `BeanDefinitionVisitor`类 |
| 解释器模式 | SpEL表达式解析、属性占位符解析 | `ExpressionParser`接口 |

## 设计模式的优点和使用建议

### 优点

1. **提高代码复用性**：设计模式是经过验证的最佳实践，可以在不同的场景中复用

2. **提高代码可维护性**：设计模式使得代码结构更加清晰，易于理解和维护

3. **提高代码可扩展性**：设计模式为系统的扩展提供了灵活的方式

4. **促进团队协作**：设计模式是一种通用的语言，便于团队成员之间的沟通

5. **降低系统复杂度**：设计模式通过合理的抽象和封装，降低了系统的复杂度

### 使用建议

1. **理解模式的本质**：不仅要知道如何使用设计模式，还要理解其设计意图和适用场景

2. **不要过度设计**：只有当确实需要时才使用设计模式，避免为了使用模式而使用模式

3. **结合实际场景**：根据具体的业务场景选择合适的设计模式

4. **关注代码质量**：设计模式是手段，不是目的，最终的目标是编写高质量的代码

5. **持续学习和实践**：设计模式需要在实践中不断学习和掌握

## 总结

Spring框架广泛采用了多种设计模式，这些设计模式使得Spring框架更加灵活、可扩展和易于维护。通过学习Spring框架中使用的设计模式，我们可以更好地理解Spring框架的内部工作原理，同时也可以将这些设计模式应用到自己的项目中，提高代码质量和开发效率。

### 关键要点

1. **创建型模式**：单例模式、工厂方法模式、抽象工厂模式、原型模式在Spring中广泛应用于Bean的创建和管理

2. **结构型模式**：适配器模式、装饰器模式、代理模式、外观模式等在Spring中用于处理对象的组合和结构

3. **行为型模式**：观察者模式、策略模式、模板方法模式、责任链模式等在Spring中用于处理对象之间的通信和职责分配

4. **设计模式的组合使用**：Spring框架中常常组合使用多种设计模式，以实现复杂的功能

5. **最佳实践**：理解设计模式的本质，根据实际场景选择合适的设计模式，避免过度设计

通过掌握这些设计模式，我们可以更加深入地理解Spring框架的设计理念，同时也可以在自己的项目中灵活运用这些设计模式，提高代码的质量和可维护性。