# 什么是MyBatis？核心功能？

## 1. MyBatis概述

MyBatis是一个优秀的持久层框架，它支持定制化SQL、存储过程以及高级映射。MyBatis避免了几乎所有的JDBC代码和手动设置参数以及获取结果集。MyBatis可以使用简单的XML或注解来配置和映射原生类型、接口和Java的POJO（Plain Old Java Objects，普通老式Java对象）为数据库中的记录。

## 2. MyBatis的历史

### 2.1 发展历程
| 时间 | 事件 |
|------|------|
| 2001年 | Clinton Begin创建了iBATIS项目 |
| 2004年 | iBATIS成为Apache软件基金会的项目 |
| 2010年 | iBATIS迁移到Google Code，更名为MyBatis |
| 2013年 | MyBatis迁移到GitHub |
| 2014年 | 发布MyBatis 3.2.0版本 |
| 2019年 | 发布MyBatis 3.5.0版本 |
| 2023年 | 发布MyBatis 3.5.13版本 |

### 2.2 为什么从iBATIS更名为MyBatis？
- 更好的反映框架的核心功能
- 消除与Apache iBATIS的混淆
- 更好的品牌识别度

## 3. MyBatis的核心功能

### 3.1 1. SQL映射
MyBatis最核心的功能是SQL映射，它允许开发者将SQL语句与Java方法进行映射，实现面向对象的数据库操作。

**示例：**
```xml
<mapper namespace="com.example.mapper.UserMapper">
    <select id="findById" parameterType="int" resultType="User">
        SELECT * FROM users WHERE id = #{id}
    </select>
</mapper>
```

```java
public interface UserMapper {
    User findById(int id);
}
```

### 3.2 2. 动态SQL
MyBatis提供了强大的动态SQL功能，可以根据不同的条件生成不同的SQL语句，避免了字符串拼接的麻烦和风险。

**示例：**
```xml
<select id="findUsers" resultType="User">
    SELECT * FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
    </where>
</select>
```

### 3.3 3. 结果映射
MyBatis支持将查询结果映射到Java对象，包括简单类型、POJO、集合等，还支持高级映射如一对一、一对多、多对多关系。

**示例：**
```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
    <association property="address" javaType="Address">
        <id property="id" column="address_id"/>
        <result property="street" column="street"/>
        <result property="city" column="city"/>
    </association>
    <collection property="orders" ofType="Order">
        <id property="id" column="order_id"/>
        <result property="orderNo" column="order_no"/>
        <result property="amount" column="amount"/>
    </collection>
</resultMap>
```

### 3.4 4. 缓存机制
MyBatis提供了两级缓存机制，提高查询性能：

- **一级缓存**：SqlSession级别的缓存，默认开启
- **二级缓存**：Mapper级别的缓存，需要手动开启

**示例：**
```xml
<cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/>
```

### 3.5 5. 插件机制
MyBatis提供了插件机制，可以在执行SQL的过程中插入自定义逻辑，如分页、日志、性能监控等。

**示例：**
```java
@Intercepts({
    @Signature(
        type = StatementHandler.class,
        method = "prepare",
        args = {Connection.class, Integer.class}
    )
})
public class PageInterceptor implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 分页逻辑
        return invocation.proceed();
    }
}
```

### 3.6 6. 注解支持
MyBatis支持使用注解来配置SQL映射，简化开发：

**示例：**
```java
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{id}")
    User findById(int id);
    
    @Insert("INSERT INTO users(name, age) VALUES(#{name}, #{age})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
    
    @Update("UPDATE users SET name = #{name}, age = #{age} WHERE id = #{id}")
    int update(User user);
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    int delete(int id);
}
```

### 3.7 7. 多数据源支持
MyBatis支持多数据源配置，可以在一个应用中操作多个数据库：

**示例：**
```java
@Configuration
public class DataSourceConfig {
    @Bean("primaryDataSource")
    @Primary
    public DataSource primaryDataSource() {
        // 主数据源配置
    }
    
    @Bean("secondaryDataSource")
    public DataSource secondaryDataSource() {
        // 从数据源配置
    }
    
    @Bean("primarySqlSessionFactory")
    @Primary
    public SqlSessionFactory primarySqlSessionFactory(@Qualifier("primaryDataSource") DataSource dataSource) throws Exception {
        // 主SqlSessionFactory配置
    }
    
    @Bean("secondarySqlSessionFactory")
    public SqlSessionFactory secondarySqlSessionFactory(@Qualifier("secondaryDataSource") DataSource dataSource) throws Exception {
        // 从SqlSessionFactory配置
    }
}
```

### 3.8 8. 批处理
MyBatis支持批处理操作，提高批量插入、更新、删除的性能：

**示例：**
```java
try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    for (User user : userList) {
        userMapper.insert(user);
    }
    sqlSession.commit();
}
```

## 4. MyBatis的优点

### 4.1 1. 灵活性高
MyBatis允许开发者直接编写SQL语句，而不是通过ORM框架自动生成，因此可以根据具体的业务需求优化SQL语句，提高查询性能。

### 4.2 2. 易于学习和使用
MyBatis的配置和使用相对简单，开发者只需要了解基本的XML配置和SQL语法即可上手。

### 4.3 3. 与Spring集成良好
MyBatis提供了与Spring框架的无缝集成，可以利用Spring的依赖注入、事务管理等特性。

**示例：**
```java
@Configuration
@MapperScan(basePackages = "com.example.mapper")
public class MyBatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
```

### 4.4 4. 支持复杂的SQL操作
MyBatis支持存储过程、高级映射、动态SQL等复杂的SQL操作，满足各种业务场景的需求。

### 4.5 5. 性能优异
MyBatis的性能优于许多其他ORM框架，因为它允许开发者直接控制SQL语句，避免了ORM框架自动生成SQL的开销。

## 5. MyBatis的应用场景

### 5.1 1. 复杂的SQL场景
当需要编写复杂的SQL语句，如多表关联查询、嵌套查询、动态SQL等时，MyBatis是一个很好的选择。

### 5.2 2. 性能要求高的场景
当对数据库操作的性能要求较高时，MyBatis允许开发者优化SQL语句，提高查询性能。

### 5.3 3. 遗留系统迁移
当需要将遗留系统迁移到新的架构时，MyBatis可以很好地适配现有的数据库结构。

### 5.4 4. 多数据库支持
当应用需要支持多种数据库时，MyBatis的SQL映射可以根据不同的数据库生成不同的SQL语句。

## 6. MyBatis与其他ORM框架的对比

### 6.1 MyBatis vs Hibernate
| 特性 | MyBatis | Hibernate |
|------|---------|-----------|
| SQL控制 | 开发者完全控制SQL | 自动生成SQL，可定制性差 |
| 学习曲线 | 简单 | 复杂 |
| 性能 | 优秀（直接控制SQL） | 一般（自动生成SQL） |
| 适用场景 | 复杂SQL、性能要求高 | 简单CRUD、快速开发 |
| 配置复杂度 | 中等 | 复杂 |

### 6.2 MyBatis vs JPA
| 特性 | MyBatis | JPA |
|------|---------|-----|
| 标准性 | 非标准 | 标准规范 |
| SQL控制 | 完全控制 | 有限控制 |
| 灵活性 | 高 | 中等 |
| 学习曲线 | 简单 | 中等 |
| 生态系统 | 丰富 | 更丰富 |

## 7. MyBatis的基本配置

### 7.1 1. 依赖配置
**Maven依赖：**
```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.13</version>
</dependency>

<!-- 与Spring集成 -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>3.0.3</version>
</dependency>
```

### 7.2 2. 核心配置文件
**mybatis-config.xml：**
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 全局配置 -->
    <settings>
        <setting name="cacheEnabled" value="true"/>
        <setting name="lazyLoadingEnabled" value="true"/>
        <setting name="multipleResultSetsEnabled" value="true"/>
        <setting name="useColumnLabel" value="true"/>
        <setting name="useGeneratedKeys" value="false"/>
        <setting name="autoMappingBehavior" value="PARTIAL"/>
        <setting name="autoMappingUnknownColumnBehavior" value="WARNING"/>
        <setting name="defaultExecutorType" value="SIMPLE"/>
        <setting name="defaultStatementTimeout" value="25"/>
        <setting name="defaultFetchSize" value="100"/>
        <setting name="safeRowBoundsEnabled" value="false"/>
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <setting name="localCacheScope" value="SESSION"/>
        <setting name="jdbcTypeForNull" value="OTHER"/>
        <setting name="lazyLoadTriggerMethods" value="equals,clone,hashCode,toString"/>
    </settings>
    
    <!-- 类型别名 -->
    <typeAliases>
        <package name="com.example.entity"/>
    </typeAliases>
    
    <!-- 插件 -->
    <plugins>
        <plugin interceptor="com.example.plugin.PageInterceptor"/>
    </plugins>
    
    <!-- 环境配置 -->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
                <property name="username" value="root"/>
                <property name="password" value="root"/>
            </dataSource>
        </environment>
    </environments>
    
    <!-- 映射文件 -->
    <mappers>
        <package name="com.example.mapper"/>
    </mappers>
</configuration>
```

## 8. MyBatis的使用示例

### 8.1 1. 基本使用
**步骤1：创建实体类**
```java
public class User {
    private int id;
    private String name;
    private int age;
    // getter和setter方法
}
```

**步骤2：创建Mapper接口**
```java
public interface UserMapper {
    User findById(int id);
    List<User> findAll();
    int insert(User user);
    int update(User user);
    int delete(int id);
}
```

**步骤3：创建Mapper映射文件**
```xml
<mapper namespace="com.example.mapper.UserMapper">
    <select id="findById" parameterType="int" resultType="User">
        SELECT * FROM users WHERE id = #{id}
    </select>
    
    <select id="findAll" resultType="User">
        SELECT * FROM users
    </select>
    
    <insert id="insert" parameterType="User">
        INSERT INTO users(name, age) VALUES(#{name}, #{age})
    </insert>
    
    <update id="update" parameterType="User">
        UPDATE users SET name = #{name}, age = #{age} WHERE id = #{id}
    </update>
    
    <delete id="delete" parameterType="int">
        DELETE FROM users WHERE id = #{id}
    </delete>
</mapper>
```

**步骤4：使用MyBatis**
```java
// 加载配置文件
InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");

// 创建SqlSessionFactory
SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

// 创建SqlSession
try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
    // 获取Mapper
    UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
    
    // 查询用户
    User user = userMapper.findById(1);
    System.out.println(user);
    
    // 插入用户
    User newUser = new User();
    newUser.setName("张三");
    newUser.setAge(25);
    userMapper.insert(newUser);
    sqlSession.commit();
    
    // 更新用户
    user.setName("李四");
    user.setAge(30);
    userMapper.update(user);
    sqlSession.commit();
    
    // 删除用户
    userMapper.delete(1);
    sqlSession.commit();
}
```

### 8.2 2. 与Spring集成使用
**步骤1：配置数据源**
```xml
<bean id="dataSource" class="org.apache.commons.dbcp2.BasicDataSource">
    <property name="driverClassName" value="com.mysql.cj.jdbc.Driver"/>
    <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
    <property name="username" value="root"/>
    <property name="password" value="root"/>
</bean>
```

**步骤2：配置SqlSessionFactory**
```xml
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="mapperLocations" value="classpath:mapper/*.xml"/>
    <property name="typeAliasesPackage" value="com.example.entity"/>
</bean>
```

**步骤3：配置Mapper扫描**
```xml
<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.example.mapper"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
</bean>
```

**步骤4：使用Mapper**
```java
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    
    public User findById(int id) {
        return userMapper.findById(id);
    }
    
    public List<User> findAll() {
        return userMapper.findAll();
    }
    
    public int insert(User user) {
        return userMapper.insert(user);
    }
    
    public int update(User user) {
        return userMapper.update(user);
    }
    
    public int delete(int id) {
        return userMapper.delete(id);
    }
}
```

## 9. 总结

MyBatis是一个功能强大、灵活易用的持久层框架，它的核心功能包括：

1. **SQL映射**：将SQL语句与Java方法进行映射
2. **动态SQL**：根据不同条件生成不同的SQL语句
3. **结果映射**：将查询结果映射到Java对象
4. **缓存机制**：提高查询性能
5. **插件机制**：扩展MyBatis功能
6. **注解支持**：简化开发
7. **多数据源支持**：操作多个数据库
8. **批处理**：提高批量操作性能

MyBatis的优点在于它的灵活性和高性能，它允许开发者直接控制SQL语句，而不是通过ORM框架自动生成，因此可以根据具体的业务需求优化SQL语句，提高查询性能。

MyBatis适用于复杂的SQL场景、性能要求高的场景、遗留系统迁移以及多数据库支持等场景。它与Spring框架集成良好，可以利用Spring的依赖注入、事务管理等特性。

与其他ORM框架相比，MyBatis的学习曲线简单，配置相对灵活，性能优异，是Java持久层框架的优秀选择。