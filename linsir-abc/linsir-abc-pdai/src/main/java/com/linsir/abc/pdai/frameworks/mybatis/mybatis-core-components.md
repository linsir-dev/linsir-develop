# MyBatis的核心组件？

## 1. MyBatis核心组件概述

MyBatis由多个核心组件组成，这些组件协同工作，实现了从SQL映射到执行的完整流程。理解这些核心组件对于深入掌握MyBatis非常重要。

## 2. 核心组件详解

### 2.1 SqlSessionFactory

**作用：**
SqlSessionFactory是MyBatis的核心工厂类，负责创建SqlSession实例。它是线程安全的，通常在应用启动时创建，全局共享。

**创建方式：**
```java
// 通过XML配置文件创建
InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

// 通过Java代码创建
DataSource dataSource = new PooledDataSource(driver, url, username, password);
TransactionFactory transactionFactory = new JdbcTransactionFactory();
Environment environment = new Environment("development", transactionFactory, dataSource);
Configuration configuration = new Configuration(environment);
configuration.addMapper(UserMapper.class);
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
```

**核心方法：**
| 方法 | 描述 |
|------|------|
| `openSession()` | 创建默认的SqlSession实例 |
| `openSession(boolean autoCommit)` | 创建指定是否自动提交的SqlSession实例 |
| `openSession(ExecutorType execType)` | 创建指定执行器类型的SqlSession实例 |
| `openSession(ExecutorType execType, boolean autoCommit)` | 创建指定执行器类型和是否自动提交的SqlSession实例 |
| `getConfiguration()` | 获取Configuration对象 |

**源码解析：**
```java
public interface SqlSessionFactory {
    SqlSession openSession();
    SqlSession openSession(boolean autoCommit);
    SqlSession openSession(ExecutorType execType);
    SqlSession openSession(TransactionIsolationLevel level);
    SqlSession openSession(ExecutorType execType, TransactionIsolationLevel level);
    SqlSession openSession(ExecutorType execType, boolean autoCommit);
    Configuration getConfiguration();
}
```

### 2.2 SqlSession

**作用：**
SqlSession是MyBatis的核心接口，提供了执行SQL语句的方法。它是非线程安全的，通常在方法级别创建和关闭。

**创建方式：**
```java
SqlSession sqlSession = sqlSessionFactory.openSession();
try {
    // 执行SQL操作
} finally {
    sqlSession.close();
}
```

**核心方法：**
| 方法 | 描述 |
|------|------|
| `selectOne(String statement, Object parameter)` | 执行查询，返回单个结果 |
| `selectList(String statement, Object parameter)` | 执行查询，返回结果列表 |
| `selectMap(String statement, Object parameter, String mapKey)` | 执行查询，返回Map结果 |
| `insert(String statement, Object parameter)` | 执行插入操作 |
| `update(String statement, Object parameter)` | 执行更新操作 |
| `delete(String statement, Object parameter)` | 执行删除操作 |
| `commit()` | 提交事务 |
| `rollback()` | 回滚事务 |
| `getMapper(Class<T> type)` | 获取Mapper接口实例 |
| `close()` | 关闭SqlSession |

**源码解析：**
```java
public interface SqlSession extends Closeable {
    <T> T selectOne(String statement);
    <T> T selectOne(String statement, Object parameter);
    <E> List<E> selectList(String statement);
    <E> List<E> selectList(String statement, Object parameter);
    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);
    <K, V> Map<K, V> selectMap(String statement, String mapKey);
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey);
    <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds);
    <T> Cursor<T> selectCursor(String statement);
    <T> Cursor<T> selectCursor(String statement, Object parameter);
    <T> Cursor<T> selectCursor(String statement, Object parameter, RowBounds rowBounds);
    int insert(String statement);
    int insert(String statement, Object parameter);
    int update(String statement);
    int update(String statement, Object parameter);
    int delete(String statement);
    int delete(String statement, Object parameter);
    void commit();
    void commit(boolean force);
    void rollback();
    void rollback(boolean force);
    List<BatchResult> flushStatements();
    <T> T getMapper(Class<T> type);
    Connection getConnection();
    void clearCache();
    void close();
}
```

### 2.3 Executor

**作用：**
Executor是MyBatis的核心执行器，负责执行SQL语句。它是SqlSession的底层实现，处理SQL的执行、缓存管理和事务控制。

**实现类：**
| 实现类 | 描述 |
|--------|------|
| `SimpleExecutor` | 简单执行器，每次执行SQL都会创建新的Statement |
| `ReuseExecutor` | 重用执行器，重用Statement |
| `BatchExecutor` | 批处理执行器，批量执行SQL |
| `CachingExecutor` | 缓存执行器，处理二级缓存 |

**创建方式：**
```java
// MyBatis内部创建，用户通常不直接使用
Configuration configuration = sqlSessionFactory.getConfiguration();
Executor executor = configuration.newExecutor(tx, ExecutorType.SIMPLE);
```

**核心方法：**
| 方法 | 描述 |
|------|------|
| `update(MappedStatement ms, Object parameter)` | 执行更新操作（插入、更新、删除） |
| `query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler)` | 执行查询操作 |
| `commit(boolean required)` | 提交事务 |
| `rollback(boolean required)` | 回滚事务 |
| `createCacheKey(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql)` | 创建缓存键 |
| `isCached(MappedStatement ms, CacheKey key)` | 检查是否缓存 |
| `clearLocalCache()` | 清除本地缓存 |
| `close(boolean forceRollback)` | 关闭执行器 |

**源码解析：**
```java
public interface Executor {
    ResultHandler NO_RESULT_HANDLER = null;

    int update(MappedStatement ms, Object parameter) throws SQLException;
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, CacheKey cacheKey, BoundSql boundSql) throws SQLException;
    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;
    <E> Cursor<E> queryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds) throws SQLException;
    List<BatchResult> flushStatements() throws SQLException;
    void commit(boolean required) throws SQLException;
    void rollback(boolean required) throws SQLException;
    CacheKey createCacheKey(MappedStatement ms, Object parameter, RowBounds rowBounds, BoundSql boundSql);
    boolean isCached(MappedStatement ms, CacheKey key);
    void clearLocalCache();
    void deferLoad(MappedStatement ms, MetaObject resultObject, String property, CacheKey key, Class<?> targetType);
    Transaction getTransaction();
    void close(boolean forceRollback);
    boolean isClosed();
    void setExecutorWrapper(Executor executor);
}
```

### 2.4 StatementHandler

**作用：**
StatementHandler负责处理SQL语句的执行，包括创建Statement对象、设置参数、执行SQL和处理结果。

**实现类：**
| 实现类 | 描述 |
|--------|------|
| `SimpleStatementHandler` | 处理SimpleStatement，用于执行不带参数的SQL |
| `PreparedStatementHandler` | 处理PreparedStatement，用于执行带参数的SQL |
| `CallableStatementHandler` | 处理CallableStatement，用于执行存储过程 |

**创建方式：**
```java
// MyBatis内部创建，用户通常不直接使用
Configuration configuration = sqlSessionFactory.getConfiguration();
StatementHandler handler = configuration.newStatementHandler(executor, ms, parameter, rowBounds, resultHandler, boundSql);
```

**核心方法：**
| 方法 | 描述 |
|------|------|
| `prepare(Connection connection, Integer transactionTimeout)` | 创建Statement对象 |
| `parameterize(Statement statement)` | 设置SQL参数 |
| `batch(Statement statement)` | 执行批处理 |
| `update(Statement statement)` | 执行更新操作 |
| `query(Statement statement, ResultHandler resultHandler)` | 执行查询操作 |
| `getBoundSql()` | 获取BoundSql对象 |
| `getParameterHandler()` | 获取ParameterHandler对象 |

**源码解析：**
```java
public interface StatementHandler {
    Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;
    void parameterize(Statement statement) throws SQLException;
    void batch(Statement statement) throws SQLException;
    int update(Statement statement) throws SQLException;
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
    <E> Cursor<E> queryCursor(Statement statement) throws SQLException;
    BoundSql getBoundSql();
    ParameterHandler getParameterHandler();
}
```

### 2.5 ParameterHandler

**作用：**
ParameterHandler负责处理SQL参数的映射，将Java参数转换为JDBC参数。

**实现类：**
- `DefaultParameterHandler`：默认的参数处理器

**创建方式：**
```java
// MyBatis内部创建，用户通常不直接使用
ParameterHandler parameterHandler = configuration.newParameterHandler(ms, parameterObject, boundSql);
```

**核心方法：**
| 方法 | 描述 |
|------|------|
| `getParameterObject()` | 获取参数对象 |
| `setParameters(PreparedStatement ps)` | 设置SQL参数 |

**源码解析：**
```java
public interface ParameterHandler {
    Object getParameterObject();
    void setParameters(PreparedStatement ps) throws SQLException;
}
```

### 2.6 ResultSetHandler

**作用：**
ResultSetHandler负责处理结果集的映射，将JDBC ResultSet转换为Java对象。

**实现类：**
- `DefaultResultSetHandler`：默认的结果集处理器

**创建方式：**
```java
// MyBatis内部创建，用户通常不直接使用
ResultSetHandler resultSetHandler = configuration.newResultSetHandler(executor, ms, rowBounds, parameterHandler, resultHandler, boundSql);
```

**核心方法：**
| 方法 | 描述 |
|------|------|
| `handleResultSets(Statement stmt)` | 处理结果集，返回结果列表 |
| `handleCursorResultSets(Statement stmt)` | 处理游标结果集 |
| `handleOutputParameters(CallableStatement cs)` | 处理存储过程的输出参数 |

**源码解析：**
```java
public interface ResultSetHandler {
    <E> List<E> handleResultSets(Statement stmt) throws SQLException;
    <E> Cursor<E> handleCursorResultSets(Statement stmt) throws SQLException;
    void handleOutputParameters(CallableStatement cs) throws SQLException;
}
```

### 2.7 TypeHandler

**作用：**
TypeHandler负责Java类型和JDBC类型之间的转换。

**内置实现：**
MyBatis内置了多种TypeHandler，如：
- `IntegerTypeHandler`：处理Integer类型
- `StringTypeHandler`：处理String类型
- `DateTypeHandler`：处理Date类型
- `BlobTypeHandler`：处理Blob类型
- `ClobTypeHandler`：处理Clob类型

**自定义TypeHandler：**
```java
public class MyTypeHandler implements TypeHandler<MyType> {
    @Override
    public void setParameter(PreparedStatement ps, int i, MyType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public MyType getResult(ResultSet rs, String columnName) throws SQLException {
        return new MyType(rs.getString(columnName));
    }

    @Override
    public MyType getResult(ResultSet rs, int columnIndex) throws SQLException {
        return new MyType(rs.getString(columnIndex));
    }

    @Override
    public MyType getResult(CallableStatement cs, int columnIndex) throws SQLException {
        return new MyType(cs.getString(columnIndex));
    }
}
```

**注册TypeHandler：**
```xml
<typeHandlers>
    <typeHandler handler="com.example.handler.MyTypeHandler" javaType="com.example.type.MyType" jdbcType="VARCHAR"/>
</typeHandlers>
```

**源码解析：**
```java
public interface TypeHandler<T> {
    void setParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
    T getResult(ResultSet rs, String columnName) throws SQLException;
    T getResult(ResultSet rs, int columnIndex) throws SQLException;
    T getResult(CallableStatement cs, int columnIndex) throws SQLException;
}
```

### 2.8 MappedStatement

**作用：**
MappedStatement是MyBatis的核心配置类，存储了SQL语句、参数映射、结果集映射等信息。

**创建方式：**
```java
// MyBatis内部创建，用户通常不直接使用
// 通过解析Mapper映射文件或注解创建
```

**核心属性：**
| 属性 | 描述 |
|------|------|
| `id` | MappedStatement的唯一标识 |
| `sqlSource` | SQL语句的来源 |
| `parameterMap` | 参数映射信息 |
| `resultMaps` | 结果集映射信息 |
| `statementType` | Statement类型（STATEMENT、PREPARED、CALLABLE） |
| `sqlCommandType` | SQL命令类型（SELECT、INSERT、UPDATE、DELETE） |
| `timeout` | 超时时间 |
| `flushCacheRequired` | 是否需要刷新缓存 |
| `useCache` | 是否使用缓存 |
| `resultOrdered` | 结果是否有序 |
| `resultSets` | 结果集名称 |

**源码解析：**
```java
public final class MappedStatement {
    private String resource;
    private Configuration configuration;
    private String id;
    private Integer fetchSize;
    private Integer timeout;
    private StatementType statementType;
    private ResultSetType resultSetType;
    private SqlSource sqlSource;
    private Cache cache;
    private ParameterMap parameterMap;
    private List<ResultMap> resultMaps;
    private boolean flushCacheRequired;
    private boolean useCache;
    private boolean resultOrdered;
    private SqlCommandType sqlCommandType;
    private KeyGenerator keyGenerator;
    private String[] keyProperties;
    private String[] keyColumns;
    private boolean hasNestedResultMaps;
    private String databaseId;
    private Log statementLog;
    private LanguageDriver lang;
    private String[] resultSets;
    // 省略getter和setter方法
}
```

### 2.9 Configuration

**作用：**
Configuration是MyBatis的核心配置类，存储了所有配置信息，如数据源、映射文件、插件等。

**创建方式：**
```java
// 通过XML配置文件创建
InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
XMLConfigBuilder parser = new XMLConfigBuilder(inputStream);
Configuration configuration = parser.parse();

// 通过Java代码创建
Configuration configuration = new Configuration();
configuration.setCacheEnabled(true);
configuration.setMapUnderscoreToCamelCase(true);
```

**核心属性：**
| 属性 | 描述 |
|------|------|
| `environment` | 环境配置 |
| `settings` | 全局设置 |
| `typeAliases` | 类型别名 |
| `typeHandlers` | 类型处理器 |
| `mappers` | 映射器 |
| `plugins` | 插件 |
| `objectFactory` | 对象工厂 |
| `objectWrapperFactory` | 对象包装工厂 |
| `reflectorFactory` | 反射工厂 |
| `lazyLoadingEnabled` | 是否启用延迟加载 |
| `aggressiveLazyLoading` | 是否启用积极延迟加载 |
| `multipleResultSetsEnabled` | 是否启用多结果集 |
| `useGeneratedKeys` | 是否使用生成的键 |
| `useColumnLabel` | 是否使用列标签 |
| `cacheEnabled` | 是否启用缓存 |

**核心方法：**
| 方法 | 描述 |
|------|------|
| `addMapper(Class<T> type)` | 添加Mapper接口 |
| `getMapper(Class<T> type, SqlSession sqlSession)` | 获取Mapper接口实例 |
| `addMappedStatement(MappedStatement ms)` | 添加MappedStatement |
| `getMappedStatement(String id)` | 获取MappedStatement |
| `newExecutor(Transaction transaction, ExecutorType executorType)` | 创建Executor |
| `newStatementHandler(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql)` | 创建StatementHandler |
| `newParameterHandler(MappedStatement ms, Object parameterObject, BoundSql boundSql)` | 创建ParameterHandler |
| `newResultSetHandler(Executor executor, MappedStatement ms, RowBounds rowBounds, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql)` | 创建ResultSetHandler |

**源码解析：**
```java
public class Configuration {
    protected Environment environment;
    protected boolean safeRowBoundsEnabled;
    protected boolean safeResultHandlerEnabled = true;
    protected boolean mapUnderscoreToCamelCase;
    protected boolean aggressiveLazyLoading;
    protected boolean multipleResultSetsEnabled = true;
    protected boolean useGeneratedKeys;
    protected boolean useColumnLabel = true;
    protected boolean cacheEnabled = true;
    protected boolean callSettersOnNulls;
    protected boolean useActualParamName = true;
    protected boolean returnInstanceForEmptyRow;
    protected String logPrefix;
    protected Class<? extends Log> logImpl;
    protected Class<? extends VFS> vfsImpl;
    protected LocalCacheScope localCacheScope = LocalCacheScope.SESSION;
    protected JdbcType jdbcTypeForNull = JdbcType.OTHER;
    protected Set<String> lazyLoadTriggerMethods = new HashSet<>(Arrays.asList("equals", "clone", "hashCode", "toString"));
    protected Integer defaultStatementTimeout;
    protected Integer defaultFetchSize;
    protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;
    protected AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;
    protected AutoMappingUnknownColumnBehavior autoMappingUnknownColumnBehavior = AutoMappingUnknownColumnBehavior.NONE;
    protected Properties variables = new Properties();
    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();
    protected boolean lazyLoadingEnabled = false;
    protected ProxyFactory proxyFactory = new JavassistProxyFactory();
    protected String databaseId;
    protected Class<? extends LanguageDriver> defaultScriptingLanguage = XMLLanguageDriver.class;
    protected Map<String, LanguageDriver> languageDrivers = new HashMap<>();
    protected Boolean useConfigValidation;
    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);
    protected final InterceptorChain interceptorChain = new InterceptorChain();
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();
    protected final LanguageDriverRegistry languageDriverRegistry = new LanguageDriverRegistry();
    protected final Map<String, MappedStatement> mappedStatements = new StrictMap<MappedStatement>("Mapped Statements collection");
    protected final Map<String, Cache> caches = new StrictMap<Cache>("Caches collection");
    protected final Map<String, ResultMap> resultMaps = new StrictMap<ResultMap>("Result Maps collection");
    protected final Map<String, ParameterMap> parameterMaps = new StrictMap<ParameterMap>("Parameter Maps collection");
    protected final Map<String, KeyGenerator> keyGenerators = new StrictMap<KeyGenerator>("Key Generators collection");
    protected final Set<String> loadedResources = new HashSet<String>();
    protected final Map<String, XNode> sqlFragments = new StrictMap<XNode>("XML fragments parsed from previous mappers");
    protected final Collection<XMLStatementBuilder> incompleteStatements = new LinkedList<XMLStatementBuilder>();
    protected final Collection<CacheRefResolver> incompleteCacheRefs = new LinkedList<CacheRefResolver>();
    protected final Collection<ResultMapResolver> incompleteResultMaps = new LinkedList<ResultMapResolver>();
    protected final Collection<MethodResolver> incompleteMethods = new LinkedList<MethodResolver>();
    // 省略方法
}
```

### 2.10 SqlSource

**作用：**
SqlSource是SQL语句的来源，负责生成BoundSql对象。

**实现类：**
| 实现类 | 描述 |
|--------|------|
| `DynamicSqlSource` | 动态SQL语句的来源，处理包含动态SQL标签的语句 |
| `RawSqlSource` | 原始SQL语句的来源，处理静态SQL语句 |
| `ProviderSqlSource` | 提供者SQL语句的来源，处理通过@SelectProvider等注解提供的SQL |
| `StaticSqlSource` | 静态SQL语句的来源，处理预编译的SQL语句 |

**核心方法：**
| 方法 | 描述 |
|------|------|
| `getBoundSql(Object parameterObject)` | 根据参数对象生成BoundSql对象 |

**源码解析：**
```java
public interface SqlSource {
    BoundSql getBoundSql(Object parameterObject);
}
```

### 2.11 BoundSql

**作用：**
BoundSql是SQL语句的封装，包含了SQL语句、参数映射信息和参数值。

**核心属性：**
| 属性 | 描述 |
|------|------|
| `sql` | SQL语句 |
| `parameterMappings` | 参数映射信息列表 |
| `parameterObject` | 参数对象 |
| `additionalParameters` | 额外的参数 |
| `metaParameters` | 参数的元信息 |

**核心方法：**
| 方法 | 描述 |
|------|------|
| `getSql()` | 获取SQL语句 |
| `getParameterMappings()` | 获取参数映射信息列表 |
| `getParameterObject()` | 获取参数对象 |
| `hasAdditionalParameter(String name)` | 检查是否有额外的参数 |
| `getAdditionalParameter(String name)` | 获取额外的参数 |
| `setAdditionalParameter(String name, Object value)` | 设置额外的参数 |

**源码解析：**
```java
public class BoundSql {
    private final String sql;
    private final List<ParameterMapping> parameterMappings;
    private final Object parameterObject;
    private final Map<String, Object> additionalParameters;
    private final MetaObject metaParameters;

    public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.parameterObject = parameterObject;
        this.additionalParameters = new HashMap<>();
        this.metaParameters = configuration.newMetaObject(additionalParameters);
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public Object getParameterObject() {
        return parameterObject;
    }

    public boolean hasAdditionalParameter(String name) {
        String paramName = stripPrefix(name);
        return additionalParameters.containsKey(paramName);
    }

    public Object getAdditionalParameter(String name) {
        String paramName = stripPrefix(name);
        return additionalParameters.get(paramName);
    }

    public void setAdditionalParameter(String name, Object value) {
        String paramName = stripPrefix(name);
        additionalParameters.put(paramName, value);
    }

    private String stripPrefix(String name) {
        if (name.startsWith("_")) {
            return name.substring(1);
        }
        return name;
    }
}
```

### 2.12 Mapper接口

**作用：**
Mapper接口是MyBatis的核心接口，定义了SQL操作的方法。MyBatis会为Mapper接口创建动态代理实现。

**创建方式：**
```java
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(int id);
    
    @Select("SELECT * FROM users")
    List<User> findAll();
    
    @Insert("INSERT INTO users(name, age) VALUES(#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE users SET name = #{name}, age = #{age} WHERE id = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(int id);
}
```

**使用方式：**
```java
// 通过SqlSession获取Mapper实例
SqlSession sqlSession = sqlSessionFactory.openSession();
try {
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    User user = userMapper.findById(1);
} finally {
    sqlSession.close();
}

// 在Spring中通过@Autowired注入
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User findById(int id) {
        return userMapper.findById(id);
    }
}
```

## 3. 核心组件的关系

### 3.1 组件关系图

```
SqlSessionFactory → SqlSession → Executor → StatementHandler → ParameterHandler
                        ↓                      ↓
                        ↓                      → ResultSetHandler
                        ↓
                        → Mapper接口（动态代理）

Configuration → MappedStatement → SqlSource → BoundSql
            ↓
            → TypeHandler
            ↓
            → Plugin
            ↓
            → Cache
```

### 3.2 执行流程

1. **初始化阶段**：
   - 加载配置文件，创建Configuration对象
   - 创建SqlSessionFactory实例

2. **执行阶段**：
   - 创建SqlSession实例
   - 获取Mapper接口的动态代理实现
   - 调用Mapper方法，触发动态代理的invoke方法
   - 根据方法名和参数获取对应的MappedStatement
   - 创建Executor执行器
   - Executor创建StatementHandler
   - StatementHandler创建ParameterHandler和ResultSetHandler
   - ParameterHandler设置SQL参数
   - StatementHandler执行SQL语句
   - ResultSetHandler处理结果集
   - 将结果返回给调用者

3. **关闭阶段**：
   - 关闭SqlSession
   - 提交或回滚事务
   - 释放资源

## 4. 核心组件的使用技巧

### 4.1 SqlSessionFactory的使用
- **单例模式**：SqlSessionFactory是线程安全的，应该在应用启动时创建，全局共享
- **配置优化**：合理配置数据源、连接池、事务管理等
- **Mapper扫描**：使用包扫描或XML配置方式加载Mapper

### 4.2 SqlSession的使用
- **try-with-resources**：使用try-with-resources语句确保SqlSession正确关闭
- **事务管理**：合理控制事务的提交和回滚
- **批量操作**：使用ExecutorType.BATCH执行批量操作

### 4.3 Executor的使用
- **选择合适的Executor**：根据不同的场景选择合适的Executor类型
- **缓存管理**：合理使用一级缓存和二级缓存
- **批处理**：使用BatchExecutor执行批量操作时，注意调用flushStatements()方法

### 4.4 StatementHandler的使用
- **选择合适的Statement类型**：根据SQL语句的特点选择合适的Statement类型
- **参数设置**：合理设置SQL参数，避免SQL注入
- **结果集处理**：优化结果集的处理，提高性能

### 4.5 TypeHandler的使用
- **内置TypeHandler**：优先使用MyBatis内置的TypeHandler
- **自定义TypeHandler**：对于特殊类型，创建自定义的TypeHandler
- **注册TypeHandler**：正确注册自定义的TypeHandler

### 4.6 Mapper接口的使用
- **方法命名规范**：使用清晰的方法命名，如findById、insert、update、delete等
- **参数传递**：合理传递参数，使用@Param注解命名参数
- **返回值类型**：根据SQL语句的结果选择合适的返回值类型
- **注解使用**：合理使用@Select、@Insert、@Update、@Delete等注解

## 5. 核心组件的最佳实践

### 5.1 配置最佳实践
- **使用外部配置**：将数据库连接信息等配置放在外部文件中
- **合理设置缓存**：根据业务场景合理设置缓存策略
- **启用驼峰命名**：设置mapUnderscoreToCamelCase为true，自动处理下划线命名
- **使用连接池**：使用性能良好的连接池，如HikariCP

### 5.2 性能最佳实践
- **使用预编译SQL**：优先使用PreparedStatement，避免SQL注入
- **合理使用缓存**：根据业务场景合理使用一级缓存和二级缓存
- **批量操作**：对于大量数据的操作，使用批处理
- **延迟加载**：对于复杂对象，使用延迟加载
- **结果集映射**：优化结果集映射，避免不必要的字段查询

### 5.3 安全最佳实践
- **参数化SQL**：使用参数化SQL，避免SQL注入
- **权限控制**：合理控制数据库用户的权限
- **密码加密**：对敏感信息如密码进行加密存储
- **日志脱敏**：对日志中的敏感信息进行脱敏处理

### 5.4 代码组织最佳实践
- **Mapper接口与映射文件分离**：将Mapper接口和映射文件放在同一包下，使用相同的名称
- **合理使用注解**：对于简单的SQL，使用注解；对于复杂的SQL，使用XML映射文件
- **模块化**：按功能模块组织Mapper接口和映射文件
- **代码生成**：使用MyBatis Generator生成基础的Mapper代码

## 6. 总结

MyBatis的核心组件包括：

1. **SqlSessionFactory**：核心工厂类，创建SqlSession实例
2. **SqlSession**：核心接口，提供执行SQL的方法
3. **Executor**：执行器，负责执行SQL语句
4. **StatementHandler**：语句处理器，处理SQL语句的执行
5. **ParameterHandler**：参数处理器，处理SQL参数的映射
6. **ResultSetHandler**：结果集处理器，处理结果集的映射
7. **TypeHandler**：类型处理器，处理Java类型和JDBC类型的转换
8. **MappedStatement**：映射语句，存储SQL语句和映射信息
9. **Configuration**：配置类，存储所有配置信息
10. **SqlSource**：SQL源，生成BoundSql对象
11. **BoundSql**：SQL绑定，封装SQL语句和参数
12. **Mapper接口**：映射接口，定义SQL操作的方法

这些核心组件协同工作，实现了从SQL映射到执行的完整流程。理解这些核心组件的作用和原理，对于深入掌握MyBatis、优化MyBatis的使用以及解决MyBatis相关的问题非常重要。

通过合理使用这些核心组件，可以构建高性能、可维护的MyBatis应用。