# JPA的查询语言？

## 1. 查询语言概述

JPA提供了多种查询方式，主要包括：

1. **JPQL（JPA Query Language）**：一种面向对象的查询语言，类似于SQL，但操作的是实体而非表
2. **Criteria API**：一种类型安全的查询API，允许通过编程方式构建查询
3. **原生SQL**：直接执行原生SQL语句
4. **命名查询**：预定义的查询，通过名称引用

这些查询方式各有优缺点，开发者可以根据具体场景选择合适的查询方式。

## 2. JPQL（JPA Query Language）

### 2.1 JPQL概述

JPQL是一种面向对象的查询语言，它的语法类似于SQL，但操作的是实体而非表，属性而非列。JPQL是JPA的核心查询语言，提供了丰富的查询功能。

### 2.2 JPQL语法

**基本语法**：

```sql
SELECT [DISTINCT] select_expression
FROM from_clause
[WHERE where_clause]
[GROUP BY group_by_clause]
[HAVING having_clause]
[ORDER BY order_by_clause]
[LIMIT limit_clause]
[OFFSET offset_clause]
```

**关键字**：

| 关键字 | 描述 |
|-------|------|
| SELECT | 指定要查询的表达式 |
| FROM | 指定查询的实体 |
| WHERE | 指定查询条件 |
| GROUP BY | 按指定属性分组 |
| HAVING | 分组后的过滤条件 |
| ORDER BY | 按指定属性排序 |
| LIMIT | 限制查询结果数量（JPA 2.0+） |
| OFFSET | 指定查询结果的起始位置（JPA 2.0+） |
| DISTINCT | 去重 |

### 2.3 JPQL查询示例

**基本查询**：

```java
// 查询所有用户
Query query = em.createQuery("SELECT u FROM User u");
List<User> users = query.getResultList();

// 查询单个用户
Query query = em.createQuery("SELECT u FROM User u WHERE u.id = :id");
query.setParameter("id", 1L);
User user = (User) query.getSingleResult();
```

**条件查询**：

```java
// 等值条件
Query query = em.createQuery("SELECT u FROM User u WHERE u.username = :username");
query.setParameter("username", "admin");

// 不等值条件
Query query = em.createQuery("SELECT u FROM User u WHERE u.age > :age");
query.setParameter("age", 18);

// 范围条件
Query query = em.createQuery("SELECT u FROM User u WHERE u.age BETWEEN :minAge AND :maxAge");
query.setParameter("minAge", 18);
query.setParameter("maxAge", 30);

// 模糊查询
Query query = em.createQuery("SELECT u FROM User u WHERE u.username LIKE :pattern");
query.setParameter("pattern", "%admin%");

// 空值查询
Query query = em.createQuery("SELECT u FROM User u WHERE u.email IS NULL");

// 集合查询
Query query = em.createQuery("SELECT u FROM User u WHERE u.id IN (:ids)");
query.setParameter("ids", Arrays.asList(1L, 2L, 3L));
```

**排序查询**：

```java
// 升序排序
Query query = em.createQuery("SELECT u FROM User u ORDER BY u.username ASC");

// 降序排序
Query query = em.createQuery("SELECT u FROM User u ORDER BY u.age DESC");

// 多字段排序
Query query = em.createQuery("SELECT u FROM User u ORDER BY u.age DESC, u.username ASC");
```

**分页查询**：

```java
// 分页查询
Query query = em.createQuery("SELECT u FROM User u ORDER BY u.id ASC");
query.setFirstResult(0);  // 起始位置
query.setMaxResults(10);  // 最大结果数
List<User> users = query.getResultList();
```

**聚合查询**：

```java
// 计数
Query query = em.createQuery("SELECT COUNT(u) FROM User u");
Long count = (Long) query.getSingleResult();

// 平均值
Query query = em.createQuery("SELECT AVG(u.age) FROM User u");
Double avgAge = (Double) query.getSingleResult();

// 最大值
Query query = em.createQuery("SELECT MAX(u.age) FROM User u");
Integer maxAge = (Integer) query.getSingleResult();

// 最小值
Query query = em.createQuery("SELECT MIN(u.age) FROM User u");
Integer minAge = (Integer) query.getSingleResult();

// 求和
Query query = em.createQuery("SELECT SUM(u.salary) FROM User u");
BigDecimal totalSalary = (BigDecimal) query.getSingleResult();
```

**分组查询**：

```java
// 按性别分组计数
Query query = em.createQuery("SELECT u.gender, COUNT(u) FROM User u GROUP BY u.gender");
List<Object[]> results = query.getResultList();

// 分组后过滤
Query query = em.createQuery("SELECT u.department, AVG(u.salary) FROM User u GROUP BY u.department HAVING AVG(u.salary) > :minSalary");
query.setParameter("minSalary", new BigDecimal(5000));
```

**关联查询**：

```java
// 内连接
Query query = em.createQuery("SELECT u FROM User u JOIN u.orders o WHERE o.amount > :amount");
query.setParameter("amount", new BigDecimal(1000));

// 左外连接
Query query = em.createQuery("SELECT u FROM User u LEFT JOIN u.orders o WHERE o IS NULL");

// 右外连接
Query query = em.createQuery("SELECT o FROM User u RIGHT JOIN u.orders o");

// 全外连接（部分数据库支持）
Query query = em.createQuery("SELECT u FROM User u FULL JOIN u.orders o");

// 连接获取（解决N+1查询问题）
Query query = em.createQuery("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :id");
query.setParameter("id", 1L);
```

**子查询**：

```java
// 子查询作为条件
Query query = em.createQuery("SELECT u FROM User u WHERE u.age > (SELECT AVG(u2.age) FROM User u2)");

// 子查询作为集合
Query query = em.createQuery("SELECT u FROM User u WHERE u.id IN (SELECT o.user.id FROM Order o WHERE o.amount > :amount)");
query.setParameter("amount", new BigDecimal(1000));

//  EXISTS子查询
Query query = em.createQuery("SELECT u FROM User u WHERE EXISTS (SELECT o FROM Order o WHERE o.user = u AND o.amount > :amount)");
query.setParameter("amount", new BigDecimal(1000));

//  NOT EXISTS子查询
Query query = em.createQuery("SELECT u FROM User u WHERE NOT EXISTS (SELECT o FROM Order o WHERE o.user = u)");
```

**函数查询**：

```java
// 字符串函数
Query query = em.createQuery("SELECT CONCAT(u.firstName, ' ', u.lastName) FROM User u");
Query query = em.createQuery("SELECT LOWER(u.username) FROM User u");
Query query = em.createQuery("SELECT UPPER(u.email) FROM User u");
Query query = em.createQuery("SELECT LENGTH(u.username) FROM User u");
Query query = em.createQuery("SELECT SUBSTRING(u.username, 1, 3) FROM User u");

// 数值函数
Query query = em.createQuery("SELECT ABS(u.salary) FROM User u");
Query query = em.createQuery("SELECT CEIL(u.age) FROM User u");
Query query = em.createQuery("SELECT FLOOR(u.age) FROM User u");
Query query = em.createQuery("SELECT MOD(u.age, 2) FROM User u");

// 日期函数
Query query = em.createQuery("SELECT CURRENT_DATE FROM User u");
Query query = em.createQuery("SELECT CURRENT_TIME FROM User u");
Query query = em.createQuery("SELECT CURRENT_TIMESTAMP FROM User u");
Query query = em.createQuery("SELECT YEAR(u.birthDate) FROM User u");
Query query = em.createQuery("SELECT MONTH(u.birthDate) FROM User u");
Query query = em.createQuery("SELECT DAY(u.birthDate) FROM User u");

// 聚合函数
Query query = em.createQuery("SELECT COUNT(u), MIN(u.age), MAX(u.age), AVG(u.age), SUM(u.salary) FROM User u");
```

### 2.4 JPQL参数绑定

**命名参数**：

```java
Query query = em.createQuery("SELECT u FROM User u WHERE u.username = :username AND u.age = :age");
query.setParameter("username", "admin");
query.setParameter("age", 25);
```

**位置参数**：

```java
Query query = em.createQuery("SELECT u FROM User u WHERE u.username = ?1 AND u.age = ?2");
query.setParameter(1, "admin");
query.setParameter(2, 25);
```

**参数类型**：

```java
// 基本类型
query.setParameter("age", 25);

// 字符串
query.setParameter("username", "admin");

// 日期
query.setParameter("birthDate", LocalDate.of(1990, 1, 1));

// 枚举
query.setParameter("gender", Gender.MALE);

// 集合
query.setParameter("ids", Arrays.asList(1L, 2L, 3L));

// 实体
query.setParameter("user", user);
```

### 2.5 JPQL执行方法

**获取多个结果**：

```java
// 获取所有结果
List<User> users = query.getResultList();

// 获取单个结果（无结果时抛出NoResultException）
User user = (User) query.getSingleResult();

// 获取单个结果（无结果时返回null）
User user = (User) query.getResultStream().findFirst().orElse(null);
```

**执行更新和删除**：

```java
// 更新操作
Query query = em.createQuery("UPDATE User u SET u.status = :status WHERE u.id = :id");
query.setParameter("status", "ACTIVE");
query.setParameter("id", 1L);
int updated = query.executeUpdate();

// 删除操作
Query query = em.createQuery("DELETE FROM User u WHERE u.id = :id");
query.setParameter("id", 1L);
int deleted = query.executeUpdate();
```

## 3. Criteria API

### 3.1 Criteria API概述

Criteria API是一种类型安全的查询API，它允许开发者通过编程方式构建查询，而不是使用字符串。Criteria API的主要优点是类型安全，可以在编译时检查查询语法，避免运行时错误。

### 3.2 Criteria API核心组件

| 组件 | 描述 |
|------|------|
| CriteriaBuilder | 用于构建查询条件、表达式和谓词 |
| CriteriaQuery | 表示一个查询对象 |
| Root | 表示查询的根实体 |
| Selection | 表示查询的选择项 |
| Predicate | 表示查询条件 |
| Expression | 表示查询表达式 |
| Order | 表示排序规则 |

### 3.3 Criteria API查询示例

**基本查询**：

```java
// 查询所有用户
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
cq.select(root);
Query query = em.createQuery(cq);
List<User> users = query.getResultList();

// 查询单个用户
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
Predicate predicate = cb.equal(root.get("id"), 1L);
cq.where(predicate);
Query query = em.createQuery(cq);
User user = (User) query.getSingleResult();
```

**条件查询**：

```java
// 等值条件
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
Predicate predicate = cb.equal(root.get("username"), "admin");
cq.where(predicate);

// 不等值条件
Predicate predicate = cb.greaterThan(root.get("age"), 18);

// 范围条件
Predicate predicate = cb.between(root.get("age"), 18, 30);

// 模糊查询
Predicate predicate = cb.like(root.get("username"), "%admin%");

// 空值查询
Predicate predicate = cb.isNull(root.get("email"));

// 集合查询
List<Long> ids = Arrays.asList(1L, 2L, 3L);
Predicate predicate = root.get("id").in(ids);

// 组合条件
Predicate p1 = cb.equal(root.get("username"), "admin");
Predicate p2 = cb.greaterThan(root.get("age"), 18);
Predicate predicate = cb.and(p1, p2);

// 或条件
Predicate p1 = cb.equal(root.get("username"), "admin");
Predicate p2 = cb.equal(root.get("username"), "user");
Predicate predicate = cb.or(p1, p2);

// 非条件
Predicate p1 = cb.equal(root.get("username"), "admin");
Predicate predicate = cb.not(p1);
```

**排序查询**：

```java
// 升序排序
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
cq.select(root);
cq.orderBy(cb.asc(root.get("username")));

// 降序排序
cq.orderBy(cb.desc(root.get("age")));

// 多字段排序
cq.orderBy(
    cb.asc(root.get("age")),
    cb.desc(root.get("username"))
);
```

**分页查询**：

```java
// 分页查询
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
cq.select(root);
Query query = em.createQuery(cq);
query.setFirstResult(0);  // 起始位置
query.setMaxResults(10);  // 最大结果数
List<User> users = query.getResultList();
```

**聚合查询**：

```java
// 计数
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Long> cq = cb.createQuery(Long.class);
Root<User> root = cq.from(User.class);
cq.select(cb.count(root));
Long count = em.createQuery(cq).getSingleResult();

// 平均值
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Double> cq = cb.createQuery(Double.class);
Root<User> root = cq.from(User.class);
cq.select(cb.avg(root.get("age")));
Double avgAge = em.createQuery(cq).getSingleResult();

// 最大值
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
Root<User> root = cq.from(User.class);
cq.select(cb.max(root.get("age")));
Integer maxAge = em.createQuery(cq).getSingleResult();

// 最小值
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
Root<User> root = cq.from(User.class);
cq.select(cb.min(root.get("age")));
Integer minAge = em.createQuery(cq).getSingleResult();

// 求和
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<BigDecimal> cq = cb.createQuery(BigDecimal.class);
Root<User> root = cq.from(User.class);
cq.select(cb.sum(root.get("salary")));
BigDecimal totalSalary = em.createQuery(cq).getSingleResult();
```

**分组查询**：

```java
// 按性别分组计数
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
Root<User> root = cq.from(User.class);
cq.multiselect(
    root.get("gender"),
    cb.count(root)
);
cq.groupBy(root.get("gender"));
List<Object[]> results = em.createQuery(cq).getResultList();

// 分组后过滤
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
Root<User> root = cq.from(User.class);
cq.multiselect(
    root.get("department"),
    cb.avg(root.get("salary"))
);
cq.groupBy(root.get("department"));
cq.having(cb.greaterThan(cb.avg(root.get("salary")), new BigDecimal(5000)));
List<Object[]> results = em.createQuery(cq).getResultList();
```

**关联查询**：

```java
// 内连接
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
Join<User, Order> orderJoin = root.join("orders", JoinType.INNER);
Predicate predicate = cb.greaterThan(orderJoin.get("amount"), new BigDecimal(1000));
cq.where(predicate);

// 左外连接
Join<User, Order> orderJoin = root.join("orders", JoinType.LEFT);

// 右外连接
Join<User, Order> orderJoin = root.join("orders", JoinType.RIGHT);

// 全外连接
Join<User, Order> orderJoin = root.join("orders", JoinType.FULL);
```

**子查询**：

```java
// 子查询作为条件
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);

// 子查询
Subquery<Double> subquery = cq.subquery(Double.class);
Root<User> subRoot = subquery.from(User.class);
subquery.select(cb.avg(subRoot.get("age")));

// 主查询条件
Predicate predicate = cb.greaterThan(root.get("age"), subquery);
cq.where(predicate);

// 子查询作为集合
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);

// 子查询
Subquery<Long> subquery = cq.subquery(Long.class);
Root<Order> subRoot = subquery.from(Order.class);
subquery.select(subRoot.get("user").get("id"));
subquery.where(cb.greaterThan(subRoot.get("amount"), new BigDecimal(1000)));

// 主查询条件
Predicate predicate = root.get("id").in(subquery);
cq.where(predicate);
```

**函数查询**：

```java
// 字符串函数
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<String> cq = cb.createQuery(String.class);
Root<User> root = cq.from(User.class);
Expression<String> concatExpr = cb.concat(root.get("firstName"), " ");
concatExpr = cb.concat(concatExpr, root.get("lastName"));
cq.select(concatExpr);

// 数值函数
Expression<Double> absExpr = cb.abs(root.get("salary"));
Expression<Double> ceilExpr = cb.ceil(root.get("age"));
Expression<Double> floorExpr = cb.floor(root.get("age"));
Expression<Integer> modExpr = cb.mod(root.get("age"), 2);

// 日期函数
Expression<Date> currentDateExpr = cb.currentDate();
Expression<Time> currentTimeExpr = cb.currentTime();
Expression<Timestamp> currentTimestampExpr = cb.currentTimestamp();
```

**动态查询**：

```java
// 动态构建查询条件
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);

List<Predicate> predicates = new ArrayList<>();

// 根据条件动态添加查询条件
if (username != null) {
    predicates.add(cb.equal(root.get("username"), username));
}

if (age != null) {
    predicates.add(cb.greaterThan(root.get("age"), age));
}

if (email != null) {
    predicates.add(cb.like(root.get("email"), "%" + email + "%"));
}

// 组合所有条件
if (!predicates.isEmpty()) {
    Predicate[] predicateArray = predicates.toArray(new Predicate[0]);
    Predicate finalPredicate = cb.and(predicateArray);
    cq.where(finalPredicate);
}

// 排序
if (sortBy != null) {
    if (sortOrder.equals("asc")) {
        cq.orderBy(cb.asc(root.get(sortBy)));
    } else {
        cq.orderBy(cb.desc(root.get(sortBy)));
    }
}

// 分页
Query query = em.createQuery(cq);
query.setFirstResult(page * pageSize);
query.setMaxResults(pageSize);

List<User> users = query.getResultList();
```

## 4. 原生SQL查询

### 4.1 原生SQL概述

JPA允许执行原生SQL语句，这对于需要使用数据库特定功能或优化查询性能的场景非常有用。原生SQL查询可以返回实体对象或标量值。

### 4.2 原生SQL查询示例

**基本查询**：

```java
// 查询所有用户
Query query = em.createNativeQuery("SELECT * FROM users", User.class);
List<User> users = query.getResultList();

// 查询单个用户
Query query = em.createNativeQuery("SELECT * FROM users WHERE id = ?", User.class);
query.setParameter(1, 1L);
User user = (User) query.getSingleResult();
```

**参数绑定**：

```java
// 位置参数
Query query = em.createNativeQuery("SELECT * FROM users WHERE username = ? AND age = ?", User.class);
query.setParameter(1, "admin");
query.setParameter(2, 25);

// 命名参数
Query query = em.createNativeQuery("SELECT * FROM users WHERE username = :username AND age = :age", User.class);
query.setParameter("username", "admin");
query.setParameter("age", 25);
```

**返回标量值**：

```java
// 返回单个标量值
Query query = em.createNativeQuery("SELECT COUNT(*) FROM users");
Long count = ((Number) query.getSingleResult()).longValue();

// 返回多个标量值
Query query = em.createNativeQuery("SELECT username, email FROM users");
List<Object[]> results = query.getResultList();
for (Object[] result : results) {
    String username = (String) result[0];
    String email = (String) result[1];
    System.out.println("Username: " + username + ", Email: " + email);
}
```

**使用ResultSetMapping**：

```java
// 定义结果集映射
@SqlResultSetMapping(
    name = "UserMapping",
    entities = @EntityResult(
        entityClass = User.class,
        fields = {
            @FieldResult(name = "id", column = "id"),
            @FieldResult(name = "username", column = "username"),
            @FieldResult(name = "email", column = "email")
        }
    )
)
@Entity
public class User {
    // ...
}

// 使用结果集映射
Query query = em.createNativeQuery("SELECT id, username, email FROM users", "UserMapping");
List<User> users = query.getResultList();
```

**复杂原生SQL**：

```java
// 复杂查询
Query query = em.createNativeQuery("""
    SELECT 
        u.id, 
        u.username, 
        COUNT(o.id) as order_count,
        SUM(o.amount) as total_amount
    FROM 
        users u
    LEFT JOIN 
        orders o ON u.id = o.user_id
    WHERE 
        u.age > :age
    GROUP BY 
        u.id, u.username
    HAVING 
        COUNT(o.id) > 0
    ORDER BY 
        total_amount DESC
    LIMIT :limit
    OFFSET :offset
""", User.class);
query.setParameter("age", 18);
query.setParameter("limit", 10);
query.setParameter("offset", 0);
List<User> users = query.getResultList();
```

## 5. 命名查询

### 5.1 命名查询概述

命名查询是预定义的查询，通过名称引用。命名查询可以在实体类上使用注解定义，也可以在XML配置文件中定义。命名查询的优点是可以集中管理查询，提高代码的可维护性。

### 5.2 命名查询示例

**使用注解定义**：

```java
@Entity
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
    @NamedQuery(name = "User.findByUsername", query = "SELECT u FROM User u WHERE u.username = :username"),
    @NamedQuery(name = "User.findByAgeGreaterThan", query = "SELECT u FROM User u WHERE u.age > :age"),
    @NamedQuery(name = "User.countByGender", query = "SELECT u.gender, COUNT(u) FROM User u GROUP BY u.gender")
})
public class User {
    // ...
}

// 使用命名查询
Query query = em.createNamedQuery("User.findAll");
List<User> users = query.getResultList();

Query query = em.createNamedQuery("User.findById");
query.setParameter("id", 1L);
User user = (User) query.getSingleResult();
```

**使用XML定义**：

```xml
<!-- META-INF/orm.xml -->
<entity-mappings xmlns="http://xmlns.jcp.org/xml/ns/persistence/orm"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence/orm
                                     http://xmlns.jcp.org/xml/ns/persistence/orm_2_1.xsd"
                 version="2.1">
    <entity class="com.example.entity.User">
        <named-query name="User.findAll">
            <query>SELECT u FROM User u</query>
        </named-query>
        <named-query name="User.findById">
            <query>SELECT u FROM User u WHERE u.id = :id</query>
        </named-query>
    </entity>
</entity-mappings>
```

**命名原生查询**：

```java
@Entity
@NamedNativeQueries({
    @NamedNativeQuery(name = "User.findWithNativeSql", 
                     query = "SELECT * FROM users WHERE age > :age", 
                     resultClass = User.class),
    @NamedNativeQuery(name = "User.countWithNativeSql", 
                     query = "SELECT COUNT(*) FROM users", 
                     resultSetMapping = "countResult")
})
@SqlResultSetMapping(name = "countResult", 
                     columns = @ColumnResult(name = "count"))
public class User {
    // ...
}

// 使用命名原生查询
Query query = em.createNamedQuery("User.findWithNativeSql");
query.setParameter("age", 18);
List<User> users = query.getResultList();
```

## 6. 查询结果处理

### 6.1 结果映射

**映射到实体**：

```java
// JPQL查询映射到实体
Query query = em.createQuery("SELECT u FROM User u");
List<User> users = query.getResultList();

// 原生SQL查询映射到实体
Query query = em.createNativeQuery("SELECT * FROM users", User.class);
List<User> users = query.getResultList();
```

**映射到DTO**：

```java
// 使用构造函数映射到DTO
Query query = em.createQuery("SELECT new com.example.dto.UserDTO(u.id, u.username, u.email) FROM User u");
List<UserDTO> userDTOs = query.getResultList();

// 使用Tuple映射到多字段
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<Tuple> cq = cb.createTupleQuery();
Root<User> root = cq.from(User.class);
cq.multiselect(root.get("id"), root.get("username"), root.get("email"));
Query query = em.createQuery(cq);
List<Tuple> tuples = query.getResultList();
for (Tuple tuple : tuples) {
    Long id = tuple.get(0, Long.class);
    String username = tuple.get(1, String.class);
    String email = tuple.get(2, String.class);
    // 处理结果
}

// 使用Map映射到键值对
Query query = em.createQuery("SELECT u.id as id, u.username as username FROM User u");
List<Map<String, Object>> results = query.getResultList();
for (Map<String, Object> result : results) {
    Long id = (Long) result.get("id");
    String username = (String) result.get("username");
    // 处理结果
}
```

### 6.2 结果转换

**使用Stream API**：

```java
// 使用Stream API处理结果
List<User> users = query.getResultList();
List<String> usernames = users.stream()
    .map(User::getUsername)
    .collect(Collectors.toList());

// 使用Stream API过滤结果
List<User> activeUsers = users.stream()
    .filter(user -> user.getStatus().equals("ACTIVE"))
    .collect(Collectors.toList());

// 使用Stream API排序结果
List<User> sortedUsers = users.stream()
    .sorted(Comparator.comparing(User::getUsername))
    .collect(Collectors.toList());

// 使用Stream API聚合结果
Double avgAge = users.stream()
    .mapToInt(User::getAge)
    .average()
    .orElse(0.0);
```

**使用Optional**：

```java
// 使用Optional处理单个结果
Query query = em.createQuery("SELECT u FROM User u WHERE u.username = :username");
query.setParameter("username", "admin");
Optional<User> userOptional = query.getResultStream().findFirst();
User user = userOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));
```

## 7. 查询性能优化

### 7.1 查询优化策略

**使用索引**：
- 在查询条件中使用索引列
- 避免在WHERE子句中使用函数或表达式
- 合理设计索引，避免过多的索引

**优化查询语句**：
- 只查询必要的字段，避免SELECT *
- 使用JOIN FETCH减少N+1查询问题
- 使用分页查询，限制结果数量
- 合理使用子查询和连接查询

**使用缓存**：
- 对频繁访问的查询结果使用二级缓存
- 使用@Cacheable注解标记实体
- 合理设置缓存过期时间

**批量操作**：
- 使用批量查询，减少数据库访问次数
- 使用批量插入、更新和删除
- 合理设置批处理大小

### 7.2 N+1查询问题

**问题**：当查询包含关联关系时，会产生N+1条SQL语句，影响性能。

**解决方案**：

**使用JOIN FETCH**：

```java
// JPQL使用JOIN FETCH
Query query = em.createQuery("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :id");
query.setParameter("id", 1L);
User user = (User) query.getSingleResult();

// Criteria API使用JOIN FETCH
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
root.fetch("orders", JoinType.INNER);
cq.where(cb.equal(root.get("id"), 1L));
User user = em.createQuery(cq).getSingleResult();
```

**使用@BatchSize**：

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "user")
    @BatchSize(size = 10)
    private List<Order> orders;
    
    // ...
}
```

**使用EntityGraph**：

```java
// 定义EntityGraph
@NamedEntityGraph(name = "User.withOrders", attributeNodes = @NamedAttributeNode("orders"))
@Entity
public class User {
    // ...
}

// 使用EntityGraph
EntityGraph<?> graph = em.getEntityGraph("User.withOrders");
Map<String, Object> hints = new HashMap<>();
hints.put("jakarta.persistence.loadgraph", graph);
User user = em.find(User.class, 1L, hints);

// 动态创建EntityGraph
EntityGraph<User> graph = em.createEntityGraph(User.class);
graph.addAttributeNodes("orders");
Map<String, Object> hints = new HashMap<>();
hints.put("jakarta.persistence.loadgraph", graph);
TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
query.setHint("jakarta.persistence.loadgraph", graph);
List<User> users = query.getResultList();
```

## 8. 不同查询方式的比较

| 查询方式 | 优点 | 缺点 | 适用场景 |
|---------|------|------|----------|
| JPQL | 面向对象，语法简洁，功能丰富 | 运行时检查语法 | 大多数查询场景 |
| Criteria API | 类型安全，编译时检查，动态查询 | 代码冗长，学习曲线陡峭 | 复杂的动态查询 |
| 原生SQL | 功能强大，数据库特定功能，性能优化 | 平台相关，类型不安全 | 复杂查询，数据库特定功能 |
| 命名查询 | 集中管理，可维护性高 | 灵活性差 | 频繁使用的查询 |

## 9. 最佳实践

### 9.1 查询设计

- **使用合适的查询方式**：根据场景选择JPQL、Criteria API或原生SQL
- **保持查询简洁**：避免过于复杂的查询，拆分为多个简单查询
- **使用参数绑定**：避免SQL注入，提高性能
- **合理使用分页**：限制结果数量，避免内存溢出
- **使用DTO**：只返回必要的字段，减少数据传输

### 9.2 代码组织

- **集中管理查询**：使用命名查询或查询类集中管理查询
- **分层架构**：查询逻辑放在数据访问层，业务逻辑放在服务层
- **使用Repository模式**：使用Spring Data JPA的Repository接口
- **异常处理**：合理处理查询异常，如NoResultException

### 9.3 性能优化

- **使用连接查询**：减少N+1查询问题
- **合理使用缓存**：对频繁访问的数据使用缓存
- **批量操作**：对大量数据使用批量操作
- **监控查询性能**：使用数据库监控工具分析查询性能
- **定期优化查询**：根据业务需求和数据量变化优化查询

## 10. 常见问题与解决方案

### 10.1 JPQL语法错误

**问题**：JPQL语法错误，导致查询失败。

**解决方案**：
- 检查JPQL语法是否正确
- 检查实体名称和属性名称是否正确
- 查看错误日志，定位具体的语法错误
- 使用工具如Hibernate Validator验证JPQL语法

### 10.2 类型转换错误

**问题**：查询结果类型转换错误。

**解决方案**：
- 确保查询结果类型与转换类型匹配
- 使用正确的getResultList()或getSingleResult()方法
- 对于原生SQL查询，使用正确的结果映射

### 10.3 性能问题

**问题**：查询性能差，响应时间长。

**解决方案**：
- 优化查询语句，减少不必要的字段和表连接
- 使用索引，提高查询速度
- 合理使用缓存，减少数据库访问次数
- 使用分页查询，限制结果数量
- 分析执行计划，找出性能瓶颈

### 10.4 N+1查询问题

**问题**：关联查询产生N+1条SQL语句。

**解决方案**：
- 使用JOIN FETCH进行关联查询
- 使用@BatchSize批量加载关联数据
- 使用EntityGraph定义获取计划
- 考虑使用DTO，避免加载不必要的关联数据

### 10.5 参数绑定错误

**问题**：参数绑定错误，导致查询失败。

**解决方案**：
- 检查参数名称是否正确
- 检查参数类型是否匹配
- 确保所有参数都已绑定
- 使用命名参数，提高代码可读性

## 11. 总结

JPA提供了多种查询方式，包括JPQL、Criteria API、原生SQL和命名查询。每种查询方式都有其优缺点，开发者可以根据具体场景选择合适的查询方式。

**JPQL**是一种面向对象的查询语言，类似于SQL，但操作的是实体而非表。它语法简洁，功能丰富，是JPA的核心查询语言。

**Criteria API**是一种类型安全的查询API，允许通过编程方式构建查询。它的优点是类型安全，可以在编译时检查查询语法，适合构建复杂的动态查询。

**原生SQL**允许直接执行原生SQL语句，适合使用数据库特定功能或优化查询性能的场景。

**命名查询**是预定义的查询，通过名称引用。它的优点是可以集中管理查询，提高代码的可维护性。

在实际开发中，需要根据具体场景选择合适的查询方式，并注意查询性能优化，如使用索引、减少N+1查询问题、合理使用缓存等。

通过掌握JPA的查询语言，可以更高效地操作数据库，提高应用程序的性能和可维护性。