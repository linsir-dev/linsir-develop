# MyBatis的Mapper映射文件？

## 1. Mapper映射文件概述

Mapper映射文件是MyBatis的核心配置文件，用于定义SQL语句、参数映射和结果集映射等信息。它是MyBatis与数据库交互的桥梁，通过XML格式的配置，实现了SQL语句与Java方法的映射。

## 2. Mapper映射文件的结构

### 2.1 基本结构

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mapper.UserMapper">
    <!-- SQL语句定义 -->
</mapper>
```

**核心元素：**
| 元素 | 描述 |
|------|------|
| `mapper` | 根元素，定义Mapper的命名空间 |
| `select` | 定义查询语句 |
| `insert` | 定义插入语句 |
| `update` | 定义更新语句 |
| `delete` | 定义删除语句 |
| `resultMap` | 定义结果集映射 |
| `parameterMap` | 定义参数映射（已过时，推荐使用内联参数映射） |
| `sql` | 定义可重用的SQL片段 |
| `cache` | 定义缓存配置 |
| `cache-ref` | 引用其他Mapper的缓存配置 |

## 3. Mapper映射文件的配置详解

### 3.1 mapper元素

**作用：**
定义Mapper的命名空间，指定Mapper接口的全限定名。

**属性：**
| 属性 | 描述 |
|------|------|
| `namespace` | Mapper接口的全限定名，必须与接口名一致 |

**示例：**
```xml
<mapper namespace="com.example.mapper.UserMapper">
    <!-- SQL语句定义 -->
</mapper>
```

### 3.2 select元素

**作用：**
定义查询语句。

**属性：**
| 属性 | 描述 |
|------|------|
| `id` | SQL语句的唯一标识，必须与Mapper接口的方法名一致 |
| `parameterType` | 参数类型，可以是基本类型、POJO、Map等 |
| `resultType` | 结果类型，可以是基本类型、POJO、Map等 |
| `resultMap` | 结果集映射的ID，与resultMap元素的id属性对应 |
| `flushCache` | 是否刷新缓存，默认false |
| `useCache` | 是否使用缓存，默认true |
| `timeout` | 超时时间，单位秒 |
| `fetchSize` | 每次获取的记录数 |
| `statementType` | Statement类型：STATEMENT、PREPARED、CALLABLE，默认PREPARED |
| `resultSetType` | 结果集类型：FORWARD_ONLY、SCROLL_SENSITIVE、SCROLL_INSENSITIVE，默认不设置 |
| `databaseId` | 数据库ID，用于多数据库支持 |
| `resultOrdered` | 结果是否有序，默认false |
| `resultSets` | 结果集名称，用于存储过程 |

**示例：**
```xml
<select id="findById" parameterType="int" resultType="User">
    SELECT * FROM users WHERE id = #{id}
</select>

<select id="findAll" resultType="User">
    SELECT * FROM users
</select>

<select id="findByCondition" parameterType="User" resultMap="userResultMap">
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

### 3.3 insert元素

**作用：**
定义插入语句。

**属性：**
| 属性 | 描述 |
|------|------|
| `id` | SQL语句的唯一标识，必须与Mapper接口的方法名一致 |
| `parameterType` | 参数类型，可以是基本类型、POJO、Map等 |
| `flushCache` | 是否刷新缓存，默认true |
| `timeout` | 超时时间，单位秒 |
| `statementType` | Statement类型：STATEMENT、PREPARED、CALLABLE，默认PREPARED |
| `useGeneratedKeys` | 是否使用生成的主键，默认false |
| `keyProperty` | 主键属性名，与useGeneratedKeys配合使用 |
| `keyColumn` | 主键列名，与useGeneratedKeys配合使用 |
| `databaseId` | 数据库ID，用于多数据库支持 |

**示例：**
```xml
<insert id="insert" parameterType="User" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO users(name, age) VALUES(#{name}, #{age})
</insert>

<insert id="insertWithSelectKey" parameterType="User">
    <selectKey keyProperty="id" order="BEFORE" resultType="int">
        SELECT MAX(id) + 1 FROM users
    </selectKey>
    INSERT INTO users(id, name, age) VALUES(#{id}, #{name}, #{age})
</insert>
```

### 3.4 update元素

**作用：**
定义更新语句。

**属性：**
| 属性 | 描述 |
|------|------|
| `id` | SQL语句的唯一标识，必须与Mapper接口的方法名一致 |
| `parameterType` | 参数类型，可以是基本类型、POJO、Map等 |
| `flushCache` | 是否刷新缓存，默认true |
| `timeout` | 超时时间，单位秒 |
| `statementType` | Statement类型：STATEMENT、PREPARED、CALLABLE，默认PREPARED |
| `databaseId` | 数据库ID，用于多数据库支持 |

**示例：**
```xml
<update id="update" parameterType="User">
    UPDATE users SET name = #{name}, age = #{age} WHERE id = #{id}
</update>

<update id="updateSelective" parameterType="User">
    UPDATE users
    <set>
        <if test="name != null">
            name = #{name},
        </if>
        <if test="age != null">
            age = #{age},
        </if>
    </set>
    WHERE id = #{id}
</update>
```

### 3.5 delete元素

**作用：**
定义删除语句。

**属性：**
| 属性 | 描述 |
|------|------|
| `id` | SQL语句的唯一标识，必须与Mapper接口的方法名一致 |
| `parameterType` | 参数类型，可以是基本类型、POJO、Map等 |
| `flushCache` | 是否刷新缓存，默认true |
| `timeout` | 超时时间，单位秒 |
| `statementType` | Statement类型：STATEMENT、PREPARED、CALLABLE，默认PREPARED |
| `databaseId` | 数据库ID，用于多数据库支持 |

**示例：**
```xml
<delete id="delete" parameterType="int">
    DELETE FROM users WHERE id = #{id}
</delete>

<delete id="deleteByCondition" parameterType="User">
    DELETE FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
    </where>
</delete>
```

### 3.6 resultMap元素

**作用：**
定义结果集映射，用于处理复杂的结果集映射，如一对一、一对多、多对多关系。

**属性：**
| 属性 | 描述 |
|------|------|
| `id` | 结果集映射的唯一标识 |
| `type` | 结果集映射的目标类型，可以是POJO、Map等 |
| `autoMapping` | 是否自动映射，默认true |

**子元素：**
| 元素 | 描述 |
|------|------|
| `id` | 定义主键映射 |
| `result` | 定义普通字段映射 |
| `association` | 定义一对一关联映射 |
| `collection` | 定义一对多关联映射 |
| `discriminator` | 定义鉴别器映射，根据不同的值映射到不同的类型 |

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
        <result property="zipCode" column="zip_code"/>
    </association>
    <collection property="orders" ofType="Order">
        <id property="id" column="order_id"/>
        <result property="orderNo" column="order_no"/>
        <result property="amount" column="amount"/>
        <result property="createTime" column="create_time"/>
    </collection>
</resultMap>
```

### 3.7 sql元素

**作用：**
定义可重用的SQL片段，减少重复代码。

**属性：**
| 属性 | 描述 |
|------|------|
| `id` | SQL片段的唯一标识 |

**使用：**
使用`<include>`元素引用SQL片段。

**示例：**
```xml
<sql id="userColumns">
    id, name, age
</sql>

<select id="findById" parameterType="int" resultType="User">
    SELECT <include refid="userColumns"/> FROM users WHERE id = #{id}
</select>

<select id="findAll" resultType="User">
    SELECT <include refid="userColumns"/> FROM users
</select>
```

### 3.8 cache元素

**作用：**
定义缓存配置，用于启用二级缓存。

**属性：**
| 属性 | 描述 |
|------|------|
| `eviction` | 缓存淘汰策略：LRU、FIFO、SOFT、WEAK，默认LRU |
| `flushInterval` | 缓存刷新间隔，单位毫秒，默认不设置 |
| `size` | 缓存大小，默认1024 |
| `readOnly` | 是否只读，默认false |
| `type` | 缓存实现类的全限定名，默认PerpetualCache |
| `blocking` | 是否阻塞，默认false |

**示例：**
```xml
<cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/>
```

### 3.9 cache-ref元素

**作用：**
引用其他Mapper的缓存配置，实现缓存共享。

**属性：**
| 属性 | 描述 |
|------|------|
| `namespace` | 引用的Mapper的命名空间 |

**示例：**
```xml
<cache-ref namespace="com.example.mapper.UserMapper"/>
```

## 4. Mapper映射文件的参数映射

### 4.1 基本参数映射

**单个参数：**
```xml
<select id="findById" parameterType="int" resultType="User">
    SELECT * FROM users WHERE id = #{id}
</select>
```

**多个参数：**
使用`@Param`注解命名参数：
```java
User findByUsernameAndAge(@Param("username") String username, @Param("age") int age);
```

```xml
<select id="findByUsernameAndAge" resultType="User">
    SELECT * FROM users WHERE username = #{username} AND age = #{age}
</select>
```

**POJO参数：**
```xml
<insert id="insert" parameterType="User" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO users(name, age) VALUES(#{name}, #{age})
</insert>
```

**Map参数：**
```xml
<select id="findByMap" parameterType="map" resultType="User">
    SELECT * FROM users WHERE name = #{name} AND age = #{age}
</select>
```

### 4.2 参数类型处理

**基本类型：**
- `int`、`long`、`float`、`double`、`boolean`等
- 对应的JDBC类型会自动转换

**字符串类型：**
- `String`
- 对应的JDBC类型为`VARCHAR`

**日期类型：**
- `java.util.Date`、`java.sql.Date`、`java.sql.Time`、`java.sql.Timestamp`
- 可以使用`jdbcType`指定JDBC类型

**集合类型：**
- `List`、`Set`、`Array`
- 通常与`<foreach>`标签配合使用

**示例：**
```xml
<select id="findByIds" parameterType="list" resultType="User">
    SELECT * FROM users WHERE id IN
    <foreach collection="list" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</select>
```

### 4.3 参数占位符

**#{}占位符：**
- 预编译SQL，防止SQL注入
- 自动处理参数类型转换

**${}占位符：**
- 字符串替换，不防止SQL注入
- 适用于动态表名、排序字段等

**示例：**
```xml
<!-- 使用#{}占位符 -->
<select id="findById" parameterType="int" resultType="User">
    SELECT * FROM users WHERE id = #{id}
</select>

<!-- 使用${}占位符 -->
<select id="findByOrder" parameterType="map" resultType="User">
    SELECT * FROM users ORDER BY ${orderBy}
</select>
```

## 5. Mapper映射文件的结果集映射

### 5.1 基本结果集映射

**使用resultType：**
```xml
<select id="findById" parameterType="int" resultType="User">
    SELECT * FROM users WHERE id = #{id}
</select>
```

**使用resultMap：**
```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
</resultMap>

<select id="findById" parameterType="int" resultMap="userResultMap">
    SELECT * FROM users WHERE id = #{id}
</select>
```

### 5.2 复杂结果集映射

**一对一关联映射：**
```xml
<resultMap id="userWithAddressResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
    <association property="address" javaType="Address">
        <id property="id" column="address_id"/>
        <result property="street" column="street"/>
        <result property="city" column="city"/>
    </association>
</resultMap>

<select id="findUserWithAddress" parameterType="int" resultMap="userWithAddressResultMap">
    SELECT u.*, a.id as address_id, a.street, a.city
    FROM users u
    LEFT JOIN addresses a ON u.address_id = a.id
    WHERE u.id = #{id}
</select>
```

**一对多关联映射：**
```xml
<resultMap id="userWithOrdersResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
    <collection property="orders" ofType="Order">
        <id property="id" column="order_id"/>
        <result property="orderNo" column="order_no"/>
        <result property="amount" column="amount"/>
    </collection>
</resultMap>

<select id="findUserWithOrders" parameterType="int" resultMap="userWithOrdersResultMap">
    SELECT u.*, o.id as order_id, o.order_no, o.amount
    FROM users u
    LEFT JOIN orders o ON u.id = o.user_id
    WHERE u.id = #{id}
</select>
```

**多对多关联映射：**
```xml
<resultMap id="userWithRolesResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
    <collection property="roles" ofType="Role">
        <id property="id" column="role_id"/>
        <result property="name" column="role_name"/>
        <result property="description" column="role_description"/>
    </collection>
</resultMap>

<select id="findUserWithRoles" parameterType="int" resultMap="userWithRolesResultMap">
    SELECT u.*, r.id as role_id, r.name as role_name, r.description as role_description
    FROM users u
    LEFT JOIN user_roles ur ON u.id = ur.user_id
    LEFT JOIN roles r ON ur.role_id = r.id
    WHERE u.id = #{id}
</select>
```

### 5.3 结果集类型转换

**使用typeHandler：**
```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
    <result property="createTime" column="create_time" typeHandler="org.apache.ibatis.type.LocalDateTimeTypeHandler"/>
</resultMap>
```

**自定义typeHandler：**
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

```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="myType" column="my_type" typeHandler="com.example.handler.MyTypeHandler"/>
</resultMap>
```

## 5. Mapper映射文件的动态SQL

### 5.1 if标签

**作用：**
根据条件判断是否包含SQL片段。

**示例：**
```xml
<select id="findByCondition" parameterType="User" resultType="User">
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

### 5.2 choose、when、otherwise标签

**作用：**
类似Java的switch语句，根据条件选择SQL片段。

**示例：**
```xml
<select id="findByChoose" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <choose>
            <when test="id != null">
                AND id = #{id}
            </when>
            <when test="name != null">
                AND name LIKE #{name}
            </when>
            <otherwise>
                AND age = 18
            </otherwise>
        </choose>
    </where>
</select>
```

### 5.3 trim、where、set标签

**作用：**
- `trim`：自定义SQL片段的前后缀
- `where`：自动处理WHERE子句，去除多余的AND/OR
- `set`：自动处理SET子句，去除多余的逗号

**示例：**
```xml
<!-- where标签 -->
<select id="findByWhere" parameterType="User" resultType="User">
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

<!-- set标签 -->
<update id="updateSelective" parameterType="User">
    UPDATE users
    <set>
        <if test="name != null">
            name = #{name},
        </if>
        <if test="age != null">
            age = #{age},
        </if>
    </set>
    WHERE id = #{id}
</update>

<!-- trim标签 -->
<select id="findByTrim" parameterType="User" resultType="User">
    SELECT * FROM users
    <trim prefix="WHERE" prefixOverrides="AND | OR">
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
    </trim>
</select>
```

### 5.4 foreach标签

**作用：**
遍历集合，生成IN语句或批量操作。

**属性：**
| 属性 | 描述 |
|------|------|
| `collection` | 集合参数名，可以是list、array、map等 |
| `item` | 集合元素的变量名 |
| `index` | 集合元素的索引变量名 |
| `open` | 循环开始的字符串 |
| `separator` | 元素之间的分隔符 |
| `close` | 循环结束的字符串 |

**示例：**
```xml
<!-- IN查询 -->
<select id="findByIds" parameterType="list" resultType="User">
    SELECT * FROM users WHERE id IN
    <foreach collection="list" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
</select>

<!-- 批量插入 -->
<insert id="batchInsert" parameterType="list">
    INSERT INTO users(name, age) VALUES
    <foreach collection="list" item="user" separator=",">
        (#{user.name}, #{user.age})
    </foreach>
</insert>
```

### 5.5 bind标签

**作用：**
创建一个变量并绑定到上下文中，通常用于模糊查询。

**示例：**
```xml
<select id="findByName" parameterType="string" resultType="User">
    <bind name="namePattern" value="'%' + name + '%'"/>
    SELECT * FROM users WHERE name LIKE #{namePattern}
</select>
```

## 6. Mapper映射文件的最佳实践

### 6.1 命名规范

**文件命名：**
- 与Mapper接口同名，后缀为`.xml`
- 放在与Mapper接口相同的包下

**元素命名：**
- `id`属性与Mapper接口的方法名一致
- 使用驼峰命名法

**示例：**
```
com/example/mapper/UserMapper.java
com/example/mapper/UserMapper.xml
```

### 6.2 配置优化

**使用resultMap：**
- 对于复杂的结果集映射，使用`resultMap`而不是`resultType`
- 可以复用`resultMap`，减少重复代码

**使用sql片段：**
- 对于重复的SQL片段，使用`<sql>`标签定义，提高代码复用性

**合理使用缓存：**
- 对于频繁查询且变化较少的数据，启用二级缓存
- 对于经常变化的数据，禁用缓存或设置合理的缓存刷新间隔

**示例：**
```xml
<!-- 使用sql片段 -->
<sql id="userColumns">
    id, name, age, create_time
</sql>

<!-- 复用sql片段 -->
<select id="findById" parameterType="int" resultMap="userResultMap">
    SELECT <include refid="userColumns"/> FROM users WHERE id = #{id}
</select>

<!-- 启用缓存 -->
<cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/>
```

### 6.3 性能优化

**使用预编译SQL：**
- 优先使用`#{}`占位符，生成预编译SQL，提高性能和安全性
- 避免使用`${}`占位符，防止SQL注入

**合理使用索引：**
- 在SQL语句中使用索引列作为查询条件
- 避免在索引列上使用函数或表达式

**优化查询语句：**
- 只查询必要的字段，避免`SELECT *`
- 使用`LIMIT`限制返回的记录数
- 合理使用连接查询，避免笛卡尔积

**示例：**
```xml
<!-- 只查询必要的字段 -->
<select id="findById" parameterType="int" resultMap="userResultMap">
    SELECT id, name, age FROM users WHERE id = #{id}
</select>

<!-- 使用LIMIT限制记录数 -->
<select id="findTop10" resultType="User">
    SELECT * FROM users ORDER BY create_time DESC LIMIT 10
</select>
```

### 6.4 安全最佳实践

**防止SQL注入：**
- 使用`#{}`占位符，避免使用`${}`占位符
- 对于必须使用`${}`的场景，进行参数验证

**参数验证：**
- 在Java代码中对参数进行验证
- 使用数据库的约束条件

**权限控制：**
- 使用数据库用户的最小权限原则
- 避免在SQL语句中硬编码敏感信息

**示例：**
```xml
<!-- 安全的参数传递 -->
<select id="findById" parameterType="int" resultType="User">
    SELECT * FROM users WHERE id = #{id}
</select>

<!-- 不安全的参数传递（尽量避免） -->
<select id="findByOrder" parameterType="map" resultType="User">
    <!-- 对orderBy参数进行验证，只允许指定的字段 -->
    SELECT * FROM users ORDER BY ${orderBy}
</select>
```

### 6.5 代码组织

**模块化：**
- 按功能模块组织Mapper映射文件
- 每个模块对应一个Mapper接口和映射文件

**分层：**
- Mapper层：负责数据库操作
- Service层：负责业务逻辑
- Controller层：负责请求处理

**注释：**
- 为SQL语句添加注释，提高可读性
- 为复杂的映射关系添加注释

**示例：**
```xml
<!-- 查询用户信息 -->
<select id="findById" parameterType="int" resultMap="userResultMap">
    SELECT * FROM users WHERE id = #{id}
</select>

<!-- 批量插入用户 -->
<insert id="batchInsert" parameterType="list">
    INSERT INTO users(name, age) VALUES
    <foreach collection="list" item="user" separator=",">
        (#{user.name}, #{user.age})
    </foreach>
</insert>
```

## 7. Mapper映射文件与注解的对比

### 7.1 优缺点对比

| 特性 | XML映射文件 | 注解 |
|------|-------------|------|
| 可读性 | 好，结构清晰 | 差，复杂SQL难以阅读 |
| 维护性 | 好，易于修改 | 差，修改需要重新编译 |
| 功能完整性 | 完整，支持所有MyBatis特性 | 有限，复杂功能支持不足 |
| 动态SQL | 支持，功能强大 | 支持有限，复杂动态SQL难以实现 |
| 代码复用 | 好，支持SQL片段 | 差，难以复用SQL代码 |
| 适用场景 | 复杂SQL，大型项目 | 简单SQL，小型项目 |

### 7.2 选择建议

**使用XML映射文件的场景：**
- 复杂的SQL语句
- 需要使用动态SQL
- 大型项目，需要更好的维护性
- 团队协作，需要统一的SQL管理

**使用注解的场景：**
- 简单的CRUD操作
- 小型项目，快速开发
- 个人项目，灵活性要求高

**混合使用：**
- 简单的SQL使用注解
- 复杂的SQL使用XML映射文件

**示例：**
```java
// 使用注解
@Select("SELECT * FROM users WHERE id = #{id}")
User findById(int id);

// 使用XML映射文件
List<User> findByCondition(User user);
```

## 8. 常见问题与解决方案

### 8.1 问题：参数映射失败

**原因：**
- 参数类型与SQL语句中的参数名不匹配
- 多个参数未使用`@Param`注解

**解决方案：**
- 检查参数类型是否正确
- 使用`@Param`注解命名参数
- 使用POJO或Map传递多个参数

**示例：**
```java
// 错误示例
User findByUsernameAndAge(String username, int age);

// 正确示例
User findByUsernameAndAge(@Param("username") String username, @Param("age") int age);
```

### 8.2 问题：结果集映射失败

**原因：**
- 数据库字段名与POJO属性名不匹配
- 复杂的关联映射配置错误

**解决方案：**
- 使用`resultMap`定义映射关系
- 使用`mapUnderscoreToCamelCase`配置自动转换下划线命名
- 检查关联映射的配置是否正确

**示例：**
```xml
<!-- 使用resultMap -->
<resultMap id="userResultMap" type="User">
    <id property="id" column="id"/>
    <result property="userName" column="user_name"/>
</resultMap>

<!-- 或在配置文件中设置 -->
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

### 8.3 问题：动态SQL语法错误

**原因：**
- 动态SQL标签使用错误
- OGNL表达式语法错误

**解决方案：**
- 检查动态SQL标签的嵌套是否正确
- 检查OGNL表达式的语法是否正确
- 使用`<where>`、`<set>`等标签自动处理SQL片段

**示例：**
```xml
<!-- 错误示例 -->
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users WHERE
    <if test="name != null">
        name LIKE #{name}
    </if>
    <if test="age != null">
        AND age = #{age}
    </if>
</select>

<!-- 正确示例 -->
<select id="findByCondition" parameterType="User" resultType="User">
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

### 8.4 问题：缓存问题

**原因：**
- 缓存配置错误
- 缓存未正确刷新

**解决方案：**
- 检查缓存配置是否正确
- 对于经常变化的数据，禁用缓存或设置合理的缓存刷新间隔
- 在执行插入、更新、删除操作后，确保缓存被正确刷新

**示例：**
```xml
<!-- 启用缓存 -->
<cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/>

<!-- 禁用缓存 -->
<select id="findById" parameterType="int" resultType="User" useCache="false">
    SELECT * FROM users WHERE id = #{id}
</select>
```

## 9. 总结

Mapper映射文件是MyBatis的核心配置文件，它定义了SQL语句、参数映射和结果集映射等信息，是MyBatis与数据库交互的桥梁。通过合理配置Mapper映射文件，可以实现高效、安全、可维护的数据库操作。

**核心要点：**

1. **基本结构：** 包含`mapper`、`select`、`insert`、`update`、`delete`、`resultMap`等元素
2. **参数映射：** 支持基本类型、POJO、Map等多种参数类型
3. **结果集映射：** 支持简单类型、POJO、集合等多种结果类型，以及一对一、一对多、多对多等复杂关联映射
4. **动态SQL：** 支持`if`、`choose`、`when`、`otherwise`、`trim`、`where`、`set`、`foreach`等标签
5. **最佳实践：** 遵循命名规范、配置优化、性能优化、安全最佳实践和代码组织原则
6. **常见问题：** 参数映射失败、结果集映射失败、动态SQL语法错误、缓存问题等

通过掌握Mapper映射文件的配置和使用，可以更好地使用MyBatis进行数据库操作，提高开发效率和代码质量。