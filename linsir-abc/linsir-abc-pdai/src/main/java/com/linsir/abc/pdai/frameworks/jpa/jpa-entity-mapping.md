# JPA的实体映射？

## 1. 实体映射概述

实体映射是JPA的核心功能之一，它定义了Java对象与数据库表之间的对应关系。通过实体映射，开发者可以使用面向对象的方式操作数据库，而不需要编写SQL语句。JPA支持多种类型的映射，包括基本映射、主键映射、关联映射、继承映射等。

## 2. 基本映射

### 2.1 实体类注解

**@Entity**

标记一个类为实体类，映射到数据库表。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 实体名称 | 类名 |

**示例：**

```java
@Entity
public class User {
    // ...
}
```

**@Table**

指定实体类映射的数据库表。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 表名 | 实体名称 |
| `schema` | 数据库模式 | 默认为当前连接的模式 |
| `catalog` | 数据库目录 | 默认为当前连接的目录 |
| `uniqueConstraints` | 唯一约束 | 无 |

**示例：**

```java
@Entity
@Table(name = "users", schema = "public", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"username"}),
    @UniqueConstraint(columnNames = {"email"})
})
public class User {
    // ...
}
```

### 2.2 属性映射

**@Basic**

标记一个基本类型的属性，映射到数据库列。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `optional` | 是否允许为空 | true |
| `fetch` | 加载策略 | FetchType.EAGER |

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @Basic(optional = false)
    private String username;
    
    @Basic(fetch = FetchType.LAZY)
    private String description;
    
    // ...
}
```

**@Column**

指定属性映射的数据库列。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 列名 | 属性名 |
| `unique` | 是否唯一 | false |
| `nullable` | 是否允许为空 | true |
| `insertable` | 是否可插入 | true |
| `updatable` | 是否可更新 | true |
| `columnDefinition` | 列定义 | 自动生成 |
| `length` | 列长度 | 255 |
| `precision` | 数值精度 | 0 |
| `scale` | 小数位数 | 0 |

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;
    
    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean active;
    
    // ...
}
```

**@Transient**

标记一个属性为临时属性，不映射到数据库列。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    private String password;
    
    @Transient
    private String confirmPassword; // 不映射到数据库
    
    // ...
}
```

**@Temporal**

指定日期时间类型的映射方式。

**属性：**
| 属性 | 描述 |
|------|------|
| `value` | 时间类型：TemporalType.DATE（日期）、TemporalType.TIME（时间）、TemporalType.TIMESTAMP（时间戳） |

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @Temporal(TemporalType.DATE)
    private Date birthDate;
    
    @Temporal(TemporalType.TIME)
    private Date loginTime;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    // ...
}
```

**@Enumerated**

指定枚举类型的映射方式。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `value` | 枚举类型：EnumType.ORDINAL（序数）、EnumType.STRING（字符串） | EnumType.ORDINAL |

**示例：**

```java
public enum Gender {
    MALE, FEMALE
}

@Entity
public class User {
    @Id
    private Long id;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    // ...
}
```

## 3. 主键映射

### 3.1 简单主键

**@Id**

标记一个属性为主键。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    // ...
}
```

**@GeneratedValue**

指定主键的生成策略。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `strategy` | 生成策略 | GenerationType.AUTO |
| `generator` | 生成器名称 | 无 |

**生成策略：**
| 策略 | 描述 |
|------|------|
| `GenerationType.IDENTITY` | 数据库自增 |
| `GenerationType.SEQUENCE` | 数据库序列 |
| `GenerationType.TABLE` | 表生成器 |
| `GenerationType.AUTO` | 自动选择 |
| `GenerationType.UUID` | UUID生成（JPA 3.1+） |

**示例：**

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ...
}
```

### 3.2 复合主键

**@IdClass**

使用单独的类作为复合主键。

**步骤：**
1. 创建主键类，实现Serializable接口
2. 在实体类上使用@IdClass注解
3. 在实体类中定义与主键类对应的属性

**示例：**

```java
// 主键类
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long productId;
    
    // 无参构造函数
    public OrderItemId() {
    }
    
    // 有参构造函数
    public OrderItemId(Long orderId, Long productId) {
        this.orderId = orderId;
        this.productId = productId;
    }
    
    // equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId that = (OrderItemId) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(productId, that.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}

// 实体类
@Entity
@IdClass(OrderItemId.class)
public class OrderItem {
    @Id
    private Long orderId;
    
    @Id
    private Long productId;
    
    private int quantity;
    private BigDecimal price;
    
    // ...
}
```

**@EmbeddedId**

使用嵌入式类作为复合主键。

**步骤：**
1. 创建嵌入式主键类，使用@Embeddable注解
2. 在实体类中使用@EmbeddedId注解

**示例：**

```java
// 嵌入式主键类
@Embeddable
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long productId;
    
    // 构造函数、getter、setter、equals和hashCode方法
}

// 实体类
@Entity
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;
    
    private int quantity;
    private BigDecimal price;
    
    // ...
}
```

### 3.3 主键生成器

**@SequenceGenerator**

定义序列生成器。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 生成器名称 | 无 |
| `sequenceName` | 序列名称 | 生成器名称 |
| `initialValue` | 初始值 | 1 |
| `allocationSize` | 分配大小 | 50 |

**示例：**

```java
@Entity
@SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", initialValue = 1, allocationSize = 10)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;
    
    // ...
}
```

**@TableGenerator**

定义表生成器。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 生成器名称 | 无 |
| `table` | 表名 | SEQUENCE |
| `pkColumnName` | 主键列名 | SEQUENCE_NAME |
| `valueColumnName` | 值列名 | NEXT_VAL |
| `pkColumnValue` | 主键列值 | 生成器名称 |
| `initialValue` | 初始值 | 0 |
| `allocationSize` | 分配大小 | 50 |

**示例：**

```java
@Entity
@TableGenerator(name = "user_gen", table = "id_generator", pkColumnName = "gen_name", valueColumnName = "gen_value", pkColumnValue = "user_id", initialValue = 1, allocationSize = 10)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "user_gen")
    private Long id;
    
    // ...
}
```

## 4. 关联映射

### 4.1 一对一映射

**@OneToOne**

定义一对一关联关系。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `targetEntity` | 目标实体类型 | 自动推断 |
| `cascade` | 级联操作 | 无 |
| `fetch` | 加载策略 | FetchType.EAGER |
| `optional` | 是否可选 | true |
| `mappedBy` | 关系维护方 | 无 |
| `orphanRemoval` | 孤儿删除 | false |

**单向一对一：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    
    // ...
}

@Entity
public class Address {
    @Id
    private Long id;
    
    private String street;
    private String city;
    
    // ...
}
```

**双向一对一：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Address address;
    
    // ...
}

@Entity
public class Address {
    @Id
    private Long id;
    
    private String street;
    private String city;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // ...
}
```

### 4.2 一对多映射

**@OneToMany**

定义一对多关联关系。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `targetEntity` | 目标实体类型 | 自动推断 |
| `cascade` | 级联操作 | 无 |
| `fetch` | 加载策略 | FetchType.LAZY |
| `mappedBy` | 关系维护方 | 无 |
| `orphanRemoval` | 孤儿删除 | false |

**单向一对多：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private List<Order> orders;
    
    // ...
}

@Entity
public class Order {
    @Id
    private Long id;
    
    private String orderNo;
    private BigDecimal amount;
    
    // ...
}
```

**双向一对多：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private List<Order> orders;
    
    // ...
}

@Entity
public class Order {
    @Id
    private Long id;
    
    private String orderNo;
    private BigDecimal amount;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // ...
}
```

### 4.3 多对一映射

**@ManyToOne**

定义多对一关联关系。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `targetEntity` | 目标实体类型 | 自动推断 |
| `cascade` | 级联操作 | 无 |
| `fetch` | 加载策略 | FetchType.EAGER |
| `optional` | 是否可选 | true |

**示例：**

```java
@Entity
public class Order {
    @Id
    private Long id;
    
    private String orderNo;
    private BigDecimal amount;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // ...
}

@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    private String email;
    
    // ...
}
```

### 4.4 多对多映射

**@ManyToMany**

定义多对多关联关系。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `targetEntity` | 目标实体类型 | 自动推断 |
| `cascade` | 级联操作 | 无 |
| `fetch` | 加载策略 | FetchType.LAZY |
| `mappedBy` | 关系维护方 | 无 |

**单向多对多：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;
    
    // ...
}

@Entity
public class Role {
    @Id
    private Long id;
    
    private String name;
    private String description;
    
    // ...
}
```

**双向多对多：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "users")
    private Set<Role> roles;
    
    // ...
}

@Entity
public class Role {
    @Id
    private Long id;
    
    private String name;
    private String description;
    
    @ManyToMany
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> users;
    
    // ...
}
```

### 4.5 关联映射的连接列

**@JoinColumn**

指定关联关系的连接列。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 列名 | 自动生成 |
| `referencedColumnName` | 引用的列名 | 目标实体的主键列 |
| `unique` | 是否唯一 | false |
| `nullable` | 是否允许为空 | true |
| `insertable` | 是否可插入 | true |
| `updatable` | 是否可更新 | true |

**示例：**

```java
@Entity
public class Order {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;
    
    // ...
}
```

**@JoinTable**

指定多对多关联关系的连接表。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 表名 | 自动生成 |
| `joinColumns` | 连接列 | 无 |
| `inverseJoinColumns` | 反向连接列 | 无 |
| `uniqueConstraints` | 唯一约束 | 无 |

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @ManyToMany
    @JoinTable(name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"),
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role_id"}))
    private Set<Role> roles;
    
    // ...
}
```

## 5. 继承映射

### 5.1 单表继承

**@Inheritance(strategy = InheritanceType.SINGLE_TABLE)**

所有子类映射到同一个表，使用鉴别器列区分不同的子类。

**@DiscriminatorColumn**

指定鉴别器列。

**属性：**
| 属性 | 描述 | 默认值 |
|------|------|--------|
| `name` | 列名 | DTYPE |
| `discriminatorType` | 鉴别器类型 | DiscriminatorType.STRING |
| `length` | 列长度 | 31 |

**@DiscriminatorValue**

指定鉴别器值。

**示例：**

```java
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public abstract class User {
    @Id
    private Long id;
    
    private String username;
    private String email;
    
    // ...
}

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    private String role;
    private String permissions;
    
    // ...
}

@Entity
@DiscriminatorValue("CUSTOMER")
public class Customer extends User {
    private String address;
    private String phone;
    
    // ...
}
```

### 5.2 连接表继承

**@Inheritance(strategy = InheritanceType.JOINED)**

每个类映射到单独的表，使用外键关联。

**示例：**

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    private Long id;
    
    private String username;
    private String email;
    
    // ...
}

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
public class Admin extends User {
    private String role;
    private String permissions;
    
    // ...
}

@Entity
@PrimaryKeyJoinColumn(name = "user_id")
public class Customer extends User {
    private String address;
    private String phone;
    
    // ...
}
```

### 5.3 表每类继承

**@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)**

每个具体类映射到单独的表，包含父类的所有属性。

**示例：**

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    
    private String username;
    private String email;
    
    // ...
}

@Entity
public class Admin extends User {
    private String role;
    private String permissions;
    
    // ...
}

@Entity
public class Customer extends User {
    private String address;
    private String phone;
    
    // ...
}
```

## 6. 嵌入式映射

### 6.1 嵌入式对象

**@Embeddable**

标记一个类为嵌入式类。

**@Embedded**

标记一个属性为嵌入式属性。

**示例：**

```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    
    // getter和setter方法
}

@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    private String email;
    
    @Embedded
    private Address address;
    
    // ...
}
```

### 6.2 嵌入式属性覆盖

**@AttributeOverrides**

覆盖嵌入式属性的映射。

**@AttributeOverride**

覆盖单个嵌入式属性的映射。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "home_street")),
        @AttributeOverride(name = "city", column = @Column(name = "home_city")),
        @AttributeOverride(name = "state", column = @Column(name = "home_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "home_zip_code"))
    })
    private Address homeAddress;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "street", column = @Column(name = "work_street")),
        @AttributeOverride(name = "city", column = @Column(name = "work_city")),
        @AttributeOverride(name = "state", column = @Column(name = "work_state")),
        @AttributeOverride(name = "zipCode", column = @Column(name = "work_zip_code"))
    })
    private Address workAddress;
    
    // ...
}
```

## 7. 集合映射

### 7.1 基本类型集合

**@ElementCollection**

映射基本类型或嵌入式类型的集合。

**@CollectionTable**

指定集合表。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @ElementCollection
    @CollectionTable(name = "user_phone_numbers", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "phone_number")
    private Set<String> phoneNumbers;
    
    @ElementCollection
    @CollectionTable(name = "user_addresses", joinColumns = @JoinColumn(name = "user_id"))
    private List<Address> addresses;
    
    // ...
}

@Embeddable
public class Address {
    private String street;
    private String city;
    
    // getter和setter方法
}
```

### 7.2 映射到Map

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @ElementCollection
    @CollectionTable(name = "user_preferences", joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyColumn(name = "preference_key")
    @Column(name = "preference_value")
    private Map<String, String> preferences;
    
    // ...
}
```

## 8. 高级映射特性

### 8.1 懒加载

**FetchType.LAZY**

指定属性或关联关系为懒加载。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @Basic(fetch = FetchType.LAZY)
    private String biography;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    
    // ...
}
```

### 8.2 级联操作

**CascadeType**

指定级联操作类型。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
    
    // ...
}
```

### 8.3 乐观锁

**@Version**

标记一个属性为版本号，用于乐观锁。

**示例：**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    private String email;
    
    @Version
    private int version;
    
    // ...
}
```

### 8.4 自定义类型转换器

**@Converter**

标记一个类为类型转换器。

**示例：**

```java
@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {
    @Override
    public Date convertToDatabaseColumn(LocalDate attribute) {
        return attribute == null ? null : Date.valueOf(attribute);
    }
    
    @Override
    public LocalDate convertToEntityAttribute(Date dbData) {
        return dbData == null ? null : dbData.toLocalDate();
    }
}

@Entity
public class User {
    @Id
    private Long id;
    
    private String username;
    private LocalDate birthDate; // 使用LocalDate类型
    
    // ...
}
```

## 9. 实体映射的最佳实践

### 9.1 命名规范

- **实体类名**：使用 PascalCase，与表名对应
- **属性名**：使用 camelCase，与列名对应
- **表名**：使用 snake_case，复数形式
- **列名**：使用 snake_case
- **主键名**：使用 id 或 实体名_id

### 9.2 映射策略

- **简单实体**：使用基本映射
- **复杂实体**：合理使用关联映射和嵌入式映射
- **继承关系**：根据实际情况选择合适的继承策略
  - 单表继承：性能最好，适合简单的继承关系
  - 连接表继承：结构清晰，适合复杂的继承关系
  - 表每类继承：最灵活，适合独立的子类

### 9.3 性能优化

- **使用懒加载**：对于关联关系，默认使用FetchType.LAZY
- **合理使用级联**：只对必要的操作使用级联
- **避免循环引用**：注意关联关系的双向引用
- **使用@BatchSize**：对集合属性使用批量加载
- **使用@Cacheable**：对频繁访问的实体使用缓存

### 9.4 代码组织

- **实体类**：放在单独的包中
- **嵌入式类**：放在实体类同一包或子包中
- **映射配置**：优先使用注解，复杂配置可使用XML
- **业务逻辑**：与实体类分离，放在服务层

## 10. 常见问题与解决方案

### 10.1 映射错误

**问题：** 实体映射错误，导致应用程序启动失败。

**解决方案：**
- 检查实体类注解是否正确
- 检查关联关系是否正确配置
- 检查数据库表结构是否与映射一致
- 查看错误日志，定位具体的映射错误

### 10.2 性能问题

**问题：** 实体映射导致的性能问题，如N+1查询。

**解决方案：**
- 使用FetchType.LAZY延迟加载
- 使用JOIN FETCH或@EntityGraph优化查询
- 使用@BatchSize批量加载集合
- 合理使用二级缓存

### 10.3 关联关系问题

**问题：** 关联关系配置错误，导致数据不一致。

**解决方案：**
- 明确关系维护方（mappedBy属性）
- 合理设置级联操作
- 注意双向关联的同步
- 使用orphanRemoval处理孤儿记录

### 10.4 主键生成问题

**问题：** 主键生成策略选择不当，导致性能问题或主键冲突。

**解决方案：**
- 根据数据库类型选择合适的生成策略
- 对于MySQL，使用GenerationType.IDENTITY
- 对于Oracle，使用GenerationType.SEQUENCE
- 对于需要跨数据库的应用，使用GenerationType.TABLE或UUID

### 10.5 继承映射问题

**问题：** 继承映射策略选择不当，导致性能问题或查询困难。

**解决方案：**
- 根据实际情况选择合适的继承策略
- 对于简单的继承关系，使用单表继承
- 对于复杂的继承关系，使用连接表继承
- 避免过深的继承层次

## 11. 总结

JPA的实体映射是其核心功能之一，它提供了丰富的映射选项，使得开发者可以灵活地定义Java对象与数据库表之间的对应关系。通过合理使用实体映射，可以：

1. **简化开发**：使用面向对象的方式操作数据库，减少SQL语句的编写
2. **提高可维护性**：集中管理映射关系，便于修改和维护
3. **优化性能**：通过合理的映射策略和加载策略，提高应用程序性能
4. **支持复杂场景**：支持继承、关联、集合等复杂的数据结构

在实际开发中，需要根据具体的业务需求和数据库设计，选择合适的映射策略，并遵循最佳实践，以确保实体映射的正确性和高效性。