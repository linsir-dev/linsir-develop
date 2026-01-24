# Spring Data和MongoDB集成

## 基本概念

Spring Data MongoDB是Spring Data项目的一部分，提供了与MongoDB数据库集成的简化方案。它通过提供Repository接口、模板类和对象映射等功能，使开发者能够更方便地在Spring应用中使用MongoDB。

### 核心优势

1. **简化数据访问**：提供Repository接口，自动生成CRUD操作的实现。
2. **对象映射**：自动将Java对象映射到MongoDB文档，无需手动转换。
3. **丰富的查询支持**：支持方法名查询、@Query注解、Criteria API等多种查询方式。
4. **事务支持**：集成Spring的事务管理，支持MongoDB 4.0+的多文档事务。
5. **与Spring生态集成**：与Spring Boot、Spring MVC等框架无缝集成。
6. **类型安全**：提供类型安全的查询API，减少运行时错误。

## 环境搭建

### 1. Maven依赖配置

在Spring Boot项目的`pom.xml`文件中添加以下依赖：

```xml
<!-- Spring Boot Starter Data MongoDB -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- 可选：Spring Boot Starter Web，用于构建REST API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

### 2. 连接配置

在`application.properties`或`application.yml`文件中配置MongoDB连接信息：

#### application.properties

```properties
# MongoDB连接配置
spring.data.mongodb.uri=mongodb://localhost:27017/mydatabase

# 可选配置
# spring.data.mongodb.host=localhost
# spring.data.mongodb.port=27017
# spring.data.mongodb.database=mydatabase
# spring.data.mongodb.username=admin
# spring.data.mongodb.password=password
```

#### application.yml

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mydatabase
      # 可选配置
      # host: localhost
      # port: 27017
      # database: mydatabase
      # username: admin
      # password: password
```

### 3. 连接到复制集

如果MongoDB部署为复制集，可以使用以下URI格式：

```properties
spring.data.mongodb.uri=mongodb://host1:27017,host2:27017,host3:27017/mydatabase?replicaSet=rs0
```

### 4. 连接到分片集群

如果MongoDB部署为分片集群，可以使用以下URI格式：

```properties
spring.data.mongodb.uri=mongodb://mongos1:27017,mongos2:27017/mydatabase
```

## 核心组件

### 1. MongoTemplate

`MongoTemplate`是Spring Data MongoDB的核心类，提供了与MongoDB交互的低级API，支持复杂的查询和操作。

**主要功能**：
- CRUD操作
- 批量操作
- 聚合操作
- 地理位置查询
- 索引管理

**使用示例**：

```java
@Autowired
private MongoTemplate mongoTemplate;

// 保存文档
User user = new User("张三", 25);
mongoTemplate.save(user);

// 查询文档
User foundUser = mongoTemplate.findById(1L, User.class);

// 更新文档
Query query = new Query(Criteria.where("name").is("张三"));
Update update = new Update().set("age", 26);
mongoTemplate.updateFirst(query, update, User.class);

// 删除文档
mongoTemplate.remove(user);
```

### 2. MongoRepository

`MongoRepository`是一个接口，继承自`PagingAndSortingRepository`，提供了基本的CRUD操作和分页排序功能。通过继承这个接口，开发者可以快速创建数据访问层。

**主要功能**：
- 基本CRUD操作
- 分页和排序
- 自定义查询方法
- 批量操作

**使用示例**：

```java
public interface UserRepository extends MongoRepository<User, Long> {
    // 根据名称查询用户
    User findByName(String name);
    
    // 根据年龄范围查询用户
    List<User> findByAgeBetween(int minAge, int maxAge);
    
    // 根据名称模糊查询用户
    List<User> findByNameLike(String namePattern);
    
    // 自定义查询
    @Query("{ 'name' : ?0, 'age' : { '$gt' : ?1 } }")
    List<User> findUsersByNameAndAgeGreaterThan(String name, int age);
}
```

### 3. MappingMongoConverter

`MappingMongoConverter`负责将Java对象映射到MongoDB文档，以及将MongoDB文档映射回Java对象。

**主要功能**：
- 类型转换
- 字段映射
- 自定义转换器

**使用示例**：

```java
@Configuration
public class MongoConfig {
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context) {
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
        // 配置自定义转换器
        converter.setTypeMapper(new DefaultMongoTypeMapper(null)); // 移除_class字段
        return converter;
    }
}
```

### 4. MongoClient

`MongoClient`是MongoDB Java驱动的核心类，负责与MongoDB服务器建立连接。Spring Data MongoDB内部使用它来执行操作。

**使用示例**：

```java
@Configuration
public class MongoConfig {
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
    
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "mydatabase");
    }
}
```

## 实体映射

### 1. 基本映射

Spring Data MongoDB使用注解来控制Java对象与MongoDB文档之间的映射。

**示例实体类**：

```java
@Document(collection = "users") // 指定集合名
public class User {
    @Id // 指定主键
    private Long id;
    
    @Field("user_name") // 指定字段名
    private String name;
    
    private int age;
    
    @DBRef // 引用其他文档
    private List<Address> addresses;
    
    @Transient // 不存储到MongoDB
    private String temporaryField;
    
    // 构造函数、getter和setter方法
}

@Document(collection = "addresses")
public class Address {
    @Id
    private String id;
    private String street;
    private String city;
    
    // 构造函数、getter和setter方法
}
```

### 2. 索引注解

Spring Data MongoDB提供了注解来定义索引。

**示例**：

```java
@Document(collection = "users")
@CompoundIndex(def = "{ 'name' : 1, 'age' : -1 }") // 复合索引
public class User {
    @Id
    private Long id;
    
    @Indexed(unique = true) // 唯一索引
    private String name;
    
    @Indexed // 普通索引
    private int age;
    
    // 构造函数、getter和setter方法
}
```

### 3. 自定义类型转换

对于复杂类型，可以实现`Converter`接口来自定义类型转换。

**示例**：

```java
// 日期转换器
@Component
public class DateToStringConverter implements Converter<Date, String> {
    @Override
    public String convert(Date source) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(source);
    }
}

// 字符串转日期转换器
@Component
public class StringToDateConverter implements Converter<String, Date> {
    @Override
    public Date convert(String source) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(source);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
```

## CRUD操作

### 1. 使用MongoRepository

```java
@Autowired
private UserRepository userRepository;

// 保存用户
User user = new User("张三", 25);
userRepository.save(user);

// 批量保存
List<User> users = Arrays.asList(new User("李四", 26), new User("王五", 27));
userRepository.saveAll(users);

// 根据ID查询
Optional<User> optionalUser = userRepository.findById(1L);
User foundUser = optionalUser.orElse(null);

// 查询所有
List<User> allUsers = userRepository.findAll();

// 根据条件查询
List<User> usersByName = userRepository.findByName("张三");

// 更新用户
foundUser.setAge(26);
userRepository.save(foundUser);

// 删除用户
userRepository.delete(foundUser);
userRepository.deleteById(1L);
userRepository.deleteAll();
```

### 2. 使用MongoTemplate

```java
@Autowired
private MongoTemplate mongoTemplate;

// 保存文档
User user = new User("张三", 25);
mongoTemplate.save(user);

// 根据ID查询
User foundUser = mongoTemplate.findById(1L, User.class);

// 根据条件查询
Query query = new Query(Criteria.where("name").is("张三"));
List<User> users = mongoTemplate.find(query, User.class);

// 查询单个文档
User singleUser = mongoTemplate.findOne(query, User.class);

// 更新文档
Update update = new Update().set("age", 26);
mongoTemplate.updateFirst(query, update, User.class);

// 删除文档
mongoTemplate.remove(user);
mongoTemplate.remove(query, User.class);

// 批量操作
BulkOperations bulkOps = mongoTemplate.bulkOps(BulkMode.UNORDERED, User.class);
bulkOps.insert(users);
bulkOps.execute();
```

## 查询方法

### 1. 方法名查询

Spring Data MongoDB会根据方法名自动生成查询语句，遵循一定的命名规则。

**命名规则**：
- `findBy`：查询操作
- `And`/`Or`：逻辑操作
- `Between`：范围查询
- `Like`：模糊查询
- `GreaterThan`/`LessThan`：比较查询
- `IsNull`/`IsNotNull`：空值查询
- `In`：包含查询
- `OrderBy`：排序

**示例**：

```java
public interface UserRepository extends MongoRepository<User, Long> {
    // 根据名称查询
    User findByName(String name);
    
    // 根据名称和年龄查询
    User findByNameAndAge(String name, int age);
    
    // 根据年龄范围查询
    List<User> findByAgeBetween(int minAge, int maxAge);
    
    // 根据名称模糊查询
    List<User> findByNameLike(String namePattern);
    
    // 根据年龄大于某个值查询
    List<User> findByAgeGreaterThan(int age);
    
    // 根据名称排序查询
    List<User> findByOrderByNameAsc();
    
    // 分页查询
    Page<User> findByAge(int age, Pageable pageable);
}
```

### 2. @Query注解

使用`@Query`注解可以直接编写MongoDB查询语句，提供更灵活的查询能力。

**示例**：

```java
public interface UserRepository extends MongoRepository<User, Long> {
    // 基本查询
    @Query("{ 'name' : ?0 }")
    User findByNameUsingQuery(String name);
    
    // 带条件的查询
    @Query("{ 'age' : { '$gt' : ?0, '$lt' : ?1 } }")
    List<User> findByAgeBetweenUsingQuery(int minAge, int maxAge);
    
    // 投影查询（只返回指定字段）
    @Query(value = "{ 'age' : { '$gt' : ?0 } }", fields = "{ 'name' : 1, 'age' : 1 }")
    List<User> findByAgeGreaterThanWithProjection(int age);
    
    // 排序查询
    @Query(value = "{ 'name' : { '$regex' : ?0 } }", sort = "{ 'age' : -1 }")
    List<User> findByNameLikeWithSort(String namePattern);
    
    // 使用SpEL表达式
    @Query("{ 'name' : ?#{#name} }")
    List<User> findByNameUsingSpEL(@Param("name") String name);
}
```

### 3. Criteria API

使用`Criteria`和`Query`类可以构建复杂的查询条件。

**示例**：

```java
@Autowired
private MongoTemplate mongoTemplate;

// 基本查询
Query query = new Query(Criteria.where("name").is("张三").and("age").is(25));
List<User> users = mongoTemplate.find(query, User.class);

// 范围查询
Criteria criteria = Criteria.where("age").gt(20).lt(30);
query = new Query(criteria);
users = mongoTemplate.find(query, User.class);

// 模糊查询
criteria = Criteria.where("name").regex("张.*");
query = new Query(criteria);
users = mongoTemplate.find(query, User.class);

// 排序
query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "name"));
users = mongoTemplate.find(query, User.class);

// 分页
Pageable pageable = PageRequest.of(0, 10);
query = new Query(criteria).with(pageable);
List<User> pageUsers = mongoTemplate.find(query, User.class);
long total = mongoTemplate.count(query, User.class);
Page<User> resultPage = new PageImpl<>(pageUsers, pageable, total);
```

## 聚合操作

### 1. 使用MongoTemplate

```java
@Autowired
private MongoTemplate mongoTemplate;

// 聚合操作示例：按年龄分组，计算每组人数
Aggregation aggregation = Aggregation.newAggregation(
    Aggregation.group("age").count().as("count"),
    Aggregation.sort(Sort.Direction.DESC, "count")
);

AggregationResults<AgeCount> results = mongoTemplate.aggregate(
    aggregation, "users", AgeCount.class
);

List<AgeCount> ageCounts = results.getMappedResults();

// 内部类：用于接收聚合结果
class AgeCount {
    private int age;
    private long count;
    
    // getter和setter方法
}
```

### 2. 使用Repository接口

```java
public interface UserRepository extends MongoRepository<User, Long> {
    // 使用@Aggregation注解
    @Aggregation(pipeline = {
        "{ '$group' : { '_id' : '$age', 'count' : { '$sum' : 1 } } }",
        "{ '$sort' : { 'count' : -1 } }"
    })
    List<AgeCount> countByAge();
}
```

## 事务支持

Spring Data MongoDB集成了Spring的事务管理，支持MongoDB 4.0+的多文档事务。

### 1. 配置事务管理器

在Spring Boot 2.0+中，事务管理器会自动配置，无需手动设置。

### 2. 使用@Transactional注解

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Transactional // 启用事务
    public void createUserWithOrder() {
        // 创建用户
        User user = new User("张三", 25);
        userRepository.save(user);
        
        // 创建订单
        Order order = new Order(user.getId(), "商品1", 100.0);
        orderRepository.save(order);
        
        // 如果发生异常，事务会回滚
        if (true) {
            throw new RuntimeException("测试事务回滚");
        }
    }
}
```

### 3. 使用TransactionTemplate

```java
@Autowired
private TransactionTemplate transactionTemplate;

public void createUserWithOrder() {
    transactionTemplate.execute(status -> {
        try {
            // 创建用户
            User user = new User("张三", 25);
            userRepository.save(user);
            
            // 创建订单
            Order order = new Order(user.getId(), "商品1", 100.0);
            orderRepository.save(order);
            
            return true;
        } catch (Exception e) {
            status.setRollbackOnly();
            return false;
        }
    });
}
```

## 高级特性

### 1. 地理位置查询

MongoDB支持地理位置索引和查询，Spring Data MongoDB提供了相应的支持。

**示例**：

```java
@Document(collection = "places")
public class Place {
    @Id
    private String id;
    private String name;
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private Point location;
    
    // 构造函数、getter和setter方法
}

// 查询附近的地点
@Autowired
private MongoTemplate mongoTemplate;

Point point = new Point(116.404, 39.915); // 北京坐标
Distance distance = new Distance(10, Metrics.KILOMETERS);
Query query = new Query(Criteria.where("location").near(point).maxDistance(distance));
List<Place> nearbyPlaces = mongoTemplate.find(query, Place.class);
```

### 2. 事件监听

Spring Data MongoDB提供了事件监听机制，可以在文档的生命周期中执行自定义逻辑。

**示例**：

```java
@Component
public class UserEventListener {
    @EventListener
    public void onBeforeSave(BeforeSaveEvent<User> event) {
        User user = event.getSource();
        // 在保存前执行逻辑，如设置创建时间、更新时间等
        System.out.println("保存前：" + user);
    }
    
    @EventListener
    public void onAfterSave(AfterSaveEvent<User> event) {
        User user = event.getSource();
        // 在保存后执行逻辑，如发送通知、记录日志等
        System.out.println("保存后：" + user);
    }
}
```

### 3. 自定义Repository实现

对于复杂的操作，可以自定义Repository的实现。

**示例**：

```java
// 自定义Repository接口
public interface UserRepositoryCustom {
    List<User> findUsersByCustomQuery(String namePattern);
}

// 实现类
public class UserRepositoryImpl implements UserRepositoryCustom {
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Override
    public List<User> findUsersByCustomQuery(String namePattern) {
        Query query = new Query(Criteria.where("name").regex(namePattern));
        return mongoTemplate.find(query, User.class);
    }
}

// 主Repository接口，继承MongoRepository和自定义接口
public interface UserRepository extends MongoRepository<User, Long>, UserRepositoryCustom {
    // 其他查询方法
}
```

## 最佳实践

1. **使用Repository接口**：优先使用`MongoRepository`接口，只在需要复杂操作时使用`MongoTemplate`。

2. **合理设计实体类**：
   - 使用`@Document`注解指定集合名
   - 使用`@Id`注解指定主键
   - 使用`@Field`注解指定字段名，保持与MongoDB命名一致
   - 合理使用`@DBRef`和嵌入文档

3. **使用索引**：
   - 为常用查询字段创建索引
   - 使用`@Indexed`注解定义索引
   - 为复合查询创建复合索引

4. **优化查询**：
   - 使用`@Query`注解的`fields`参数限制返回字段
   - 使用分页和排序，避免一次性查询大量数据
   - 合理使用`Criteria API`构建复杂查询

5. **事务管理**：
   - 只在需要时使用事务，因为事务会影响性能
   - 保持事务操作简短，避免长时间占用事务

6. **错误处理**：
   - 捕获并处理MongoDB相关异常
   - 实现适当的重试机制，处理网络波动等临时错误

7. **监控和日志**：
   - 记录关键操作的日志
   - 监控查询性能，优化慢查询
   - 使用MongoDB的监控工具，如MongoDB Compass、MongoDB Atlas等

## 示例项目

### 1. 项目结构

```
src/main/java
├── com/example/mongodb
│   ├── config/
│   │   └── MongoConfig.java
│   ├── controller/
│   │   └── UserController.java
│   ├── entity/
│   │   └── User.java
│   ├── repository/
│   │   └── UserRepository.java
│   ├── service/
│   │   └── UserService.java
│   └── Application.java
```

### 2. 核心代码

#### User.java

```java
@Document(collection = "users")
public class User {
    @Id
    private Long id;
    private String name;
    private int age;
    private String email;
    
    // 构造函数、getter和setter方法
}
```

#### UserRepository.java

```java
public interface UserRepository extends MongoRepository<User, Long> {
    List<User> findByName(String name);
    List<User> findByAgeBetween(int minAge, int maxAge);
    
    @Query("{ 'email' : { '$regex' : ?0 } }")
    List<User> findByEmailLike(String emailPattern);
}
```

#### UserService.java

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User getUserById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }
    
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }
    
    public List<User> getUsersByAgeRange(int minAge, int maxAge) {
        return userRepository.findByAgeBetween(minAge, maxAge);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

#### UserController.java

```java
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {
        List<User> users;
        if (name != null) {
            users = userService.getUsersByName(name);
        } else if (minAge != null && maxAge != null) {
            users = userService.getUsersByAgeRange(minAge, maxAge);
        } else {
            users = userService.getAllUsers();
        }
        return ResponseEntity.ok(users);
    }
    
    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
```

#### Application.java

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 3. 配置文件

#### application.properties

```properties
# MongoDB连接配置
spring.data.mongodb.uri=mongodb://localhost:27017/testdb

# 应用配置
spring.application.name=spring-data-mongodb-example
server.port=8080
```

## 总结

Spring Data MongoDB为Spring应用提供了与MongoDB集成的简化方案，通过Repository接口、MongoTemplate和对象映射等功能，使开发者能够更方便地使用MongoDB。它不仅简化了数据访问代码，还提供了丰富的查询功能和事务支持，与Spring生态系统无缝集成。

在实际应用中，开发者应该根据具体需求选择合适的API，合理设计实体类和索引，优化查询性能，并遵循最佳实践，以充分发挥Spring Data MongoDB的优势。