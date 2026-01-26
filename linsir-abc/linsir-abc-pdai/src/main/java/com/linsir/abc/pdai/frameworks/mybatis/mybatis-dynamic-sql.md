# MyBatis的动态SQL？

## 1. 动态SQL概述

动态SQL是MyBatis的核心特性之一，它允许开发者根据不同的条件生成不同的SQL语句，而不需要手动拼接SQL字符串。MyBatis提供了强大的动态SQL标签，可以灵活地构建SQL语句，提高代码的可读性和可维护性。

## 2. 动态SQL的优势

### 2.1 灵活性
- 根据不同的条件生成不同的SQL语句
- 避免了手动拼接SQL字符串的麻烦和风险

### 2.2 可读性
- 使用XML标签和OGNL表达式，结构清晰
- 便于理解和维护

### 2.3 安全性
- 自动处理参数绑定，防止SQL注入
- 不需要担心引号、逗号等语法问题

### 2.4 可维护性
- 集中管理SQL逻辑
- 便于修改和调试

## 3. 动态SQL标签详解

### 3.1 if标签

**作用：**
根据条件判断是否包含SQL片段。

**属性：**
| 属性 | 描述 |
|------|------|
| `test` | OGNL表达式，返回布尔值 |

**示例：**
```xml
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null and name != ''">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
    </where>
</select>
```

**OGNL表达式示例：**
| 表达式 | 描述 |
|--------|------|
| `name != null` | 检查name是否不为null |
| `name != null and name != ''` | 检查name是否不为null且不为空字符串 |
| `age > 18` | 检查age是否大于18 |
| `list.size() > 0` | 检查list是否不为空 |
| `map.containsKey('name')` | 检查map是否包含name键 |

### 3.2 choose、when、otherwise标签

**作用：**
类似Java的switch语句，根据条件选择SQL片段。

**属性：**
| 标签 | 属性 | 描述 |
|------|------|------|
| `choose` | 无 | 根标签，包含when和otherwise |
| `when` | `test` | OGNL表达式，返回布尔值 |
| `otherwise` | 无 | 当所有when条件都不满足时执行 |

**示例：**
```xml
<select id="findByChoose" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <choose>
            <when test="id != null">
                AND id = #{id}
            </when>
            <when test="name != null and name != ''">
                AND name LIKE #{name}
            </when>
            <otherwise>
                AND age = 18
            </otherwise>
        </choose>
    </where>
</select>
```

### 3.3 trim、where、set标签

**作用：**
- `trim`：自定义SQL片段的前后缀
- `where`：自动处理WHERE子句，去除多余的AND/OR
- `set`：自动处理SET子句，去除多余的逗号

**属性：**
| 标签 | 属性 | 描述 |
|------|------|------|
| `trim` | `prefix` | 前缀 |
| `trim` | `suffix` | 后缀 |
| `trim` | `prefixOverrides` | 去除前缀 |
| `trim` | `suffixOverrides` | 去除后缀 |
| `where` | 无 | 自动处理WHERE子句 |
| `set` | 无 | 自动处理SET子句 |

**示例：**
```xml
<!-- where标签 -->
<select id="findByWhere" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null">
            AND name LIKE #{name}
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
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
    </trim>
</select>

<!-- trim标签实现set功能 -->
<update id="updateWithTrim" parameterType="User">
    UPDATE users
    <trim prefix="SET" suffixOverrides=",">
        <if test="name != null">
            name = #{name},
        </if>
        <if test="age != null">
            age = #{age},
        </if>
    </trim>
    WHERE id = #{id}
</update>
```

### 3.4 foreach标签

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
| `nullHandingMode` | 空集合处理模式：NULL、EMPTY、FOREACH |

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

<!-- 批量更新 -->
<update id="batchUpdate" parameterType="list">
    <foreach collection="list" item="user" separator=";">
        UPDATE users SET name = #{user.name}, age = #{user.age} WHERE id = #{user.id}
    </foreach>
</update>

<!-- 遍历map -->
<select id="findByMap" parameterType="map" resultType="User">
    SELECT * FROM users
    <where>
        <foreach collection="map" item="value" index="key">
            AND ${key} = #{value}
        </foreach>
    </where>
</select>
```

### 3.5 bind标签

**作用：**
创建一个变量并绑定到上下文中，通常用于模糊查询。

**属性：**
| 属性 | 描述 |
|------|------|
| `name` | 变量名 |
| `value` | OGNL表达式，变量的值 |

**示例：**
```xml
<!-- 模糊查询 -->
<select id="findByName" parameterType="string" resultType="User">
    <bind name="namePattern" value="'%' + name + '%'"/>
    SELECT * FROM users WHERE name LIKE #{namePattern}
</select>

<!-- 多个bind标签 -->
<select id="findByCondition" parameterType="User" resultType="User">
    <bind name="namePattern" value="'%' + name + '%'"/>
    <bind name="minAge" value="age - 5"/>
    <bind name="maxAge" value="age + 5"/>
    SELECT * FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{namePattern}
        </if>
        <if test="age != null">
            AND age BETWEEN #{minAge} AND #{maxAge}
        </if>
    </where>
</select>
```

### 3.6 sql和include标签

**作用：**
- `sql`：定义可重用的SQL片段
- `include`：引用SQL片段

**属性：**
| 标签 | 属性 | 描述 |
|------|------|------|
| `sql` | `id` | SQL片段的唯一标识 |
| `include` | `refid` | 引用的SQL片段ID |

**示例：**
```xml
<!-- 定义SQL片段 -->
<sql id="userColumns">
    id, name, age, create_time
</sql>

<!-- 引用SQL片段 -->
<select id="findById" parameterType="int" resultType="User">
    SELECT <include refid="userColumns"/> FROM users WHERE id = #{id}
</select>

<!-- 带参数的SQL片段 -->
<sql id="whereClause">
    <where>
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
    </where>
</sql>

<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users
    <include refid="whereClause"/>
</select>
```

## 4. 动态SQL的高级用法

### 4.1 嵌套动态SQL

**作用：**
在动态SQL标签中嵌套其他动态SQL标签，实现更复杂的逻辑。

**示例：**
```xml
<select id="findByComplexCondition" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null or age != null">
            <choose>
                <when test="name != null">
                    AND name LIKE #{name}
                </when>
                <otherwise>
                    AND age = #{age}
                </otherwise>
            </choose>
        </if>
        <if test="ids != null and ids.size() > 0">
            AND id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
        </if>
    </where>
</select>
```

### 4.2 动态SQL与结果映射

**作用：**
结合动态SQL和结果映射，实现更灵活的查询和映射。

**示例：**
```xml
<resultMap id="userResultMap" type="User">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="age" column="age"/>
    <collection property="orders" ofType="Order" column="id" select="findOrdersByUserId"/>
</resultMap>

<select id="findOrdersByUserId" parameterType="int" resultType="Order">
    SELECT * FROM orders WHERE user_id = #{id}
    <if test="status != null">
        AND status = #{status}
    </if>
</select>

<select id="findUserWithOrders" parameterType="User" resultMap="userResultMap">
    SELECT * FROM users
    <where>
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
    </where>
</select>
```

### 4.3 动态SQL与存储过程

**作用：**
结合动态SQL和存储过程，实现更复杂的数据库操作。

**示例：**
```xml
<select id="callProcedure" statementType="CALLABLE">
    {CALL get_user_info(
        #{id, mode=IN, jdbcType=INTEGER},
        #{name, mode=OUT, jdbcType=VARCHAR},
        #{age, mode=OUT, jdbcType=INTEGER}
    )}
</select>

<select id="callDynamicProcedure" statementType="CALLABLE">
    {CALL get_user_list(
        #{startAge, mode=IN, jdbcType=INTEGER},
        #{endAge, mode=IN, jdbcType=INTEGER},
        #{name, mode=IN, jdbcType=VARCHAR, javaType=String}
    )}
</select>
```

## 5. 动态SQL的性能优化

### 5.1 减少动态SQL的复杂度
- 避免过于复杂的嵌套逻辑
- 将复杂的动态SQL拆分为多个简单的SQL片段

### 5.2 合理使用缓存
- 对于频繁执行的动态SQL，启用二级缓存
- 设置合理的缓存刷新间隔

### 5.3 优化参数传递
- 使用POJO或Map传递参数，避免传递过多的单个参数
- 合理使用`@Param`注解命名参数

### 5.4 优化SQL语句
- 只查询必要的字段，避免`SELECT *`
- 使用索引列作为查询条件
- 避免在WHERE子句中使用函数或表达式

**示例：**
```xml
<!-- 优化前 -->
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
        <if test="createTime != null">
            AND DATE(create_time) = #{createTime}
        </if>
    </where>
</select>

<!-- 优化后 -->
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT id, name, age FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
        <if test="createTime != null">
            AND create_time BETWEEN #{createTime} AND #{createTimeEnd}
        </if>
    </where>
</select>
```

## 6. 动态SQL的最佳实践

### 6.1 命名规范
- 使用清晰的标签嵌套结构
- 为动态SQL添加注释，提高可读性

### 6.2 代码组织
- 将复杂的动态SQL拆分为多个SQL片段
- 使用`sql`标签定义可重用的SQL片段

### 6.3 安全性
- 使用`#{}`占位符，避免使用`${}`占位符，防止SQL注入
- 对于必须使用`${}`的场景，进行参数验证

### 6.4 可读性
- 合理缩进，保持代码结构清晰
- 使用空格和换行，提高可读性

### 6.5 常见场景

**场景1：条件查询**
```xml
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <if test="id != null">
            AND id = #{id}
        </if>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
    </where>
</select>
```

**场景2：选择性更新**
```xml
<update id="updateSelective" parameterType="User">
    UPDATE users
    <set>
        <if test="name != null">
            name = #{name},
        </if>
        <if test="age != null">
            age = #{age},
        </if>
        <if test="email != null">
            email = #{email},
        </if>
    </set>
    WHERE id = #{id}
</update>
```

**场景3：批量操作**
```xml
<insert id="batchInsert" parameterType="list">
    INSERT INTO users(name, age) VALUES
    <foreach collection="list" item="user" separator=",">
        (#{user.name}, #{user.age})
    </foreach>
</insert>
```

**场景4：模糊查询**
```xml
<select id="findByName" parameterType="string" resultType="User">
    <bind name="namePattern" value="'%' + name + '%'"/>
    SELECT * FROM users WHERE name LIKE #{namePattern}
</select>
```

**场景5：复杂条件**
```xml
<select id="findByComplexCondition" parameterType="map" resultType="User">
    SELECT * FROM users
    <where>
        <if test="ids != null and ids.size() > 0">
            AND id IN
            <foreach collection="ids" item="id" open="(" separator="," close=")">
                #{id}
            </foreach>
        </if>
        <if test="startAge != null">
            AND age >= #{startAge}
        </if>
        <if test="endAge != null">
            AND age <= #{endAge}
        </if>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="sortBy != null">
            ORDER BY ${sortBy} ${sortOrder}
        </if>
    </where>
</select>
```

## 7. 动态SQL与注解的对比

### 7.1 XML vs 注解

| 特性 | XML | 注解 |
|------|-----|------|
| 可读性 | 好，结构清晰 | 差，复杂SQL难以阅读 |
| 维护性 | 好，易于修改 | 差，修改需要重新编译 |
| 功能完整性 | 完整，支持所有MyBatis特性 | 有限，复杂功能支持不足 |
| 动态SQL | 支持，功能强大 | 支持有限，复杂动态SQL难以实现 |
| 代码复用 | 好，支持SQL片段 | 差，难以复用SQL代码 |
| 适用场景 | 复杂SQL，大型项目 | 简单SQL，小型项目 |

### 7.2 混合使用

**策略：**
- 简单的CRUD操作使用注解
- 复杂的动态SQL使用XML

**示例：**
```java
// 使用注解
@Select("SELECT * FROM users WHERE id = #{id}")
User findById(int id);

// 使用XML
List<User> findByCondition(User user);
```

## 8. 常见问题与解决方案

### 8.1 问题：参数绑定失败

**原因：**
- 参数名与SQL语句中的占位符不匹配
- 多个参数未使用`@Param`注解

**解决方案：**
- 检查参数名是否正确
- 使用`@Param`注解命名参数
- 使用POJO或Map传递多个参数

**示例：**
```java
// 错误示例
User findByUsernameAndAge(String username, int age);

// 正确示例
User findByUsernameAndAge(@Param("username") String username, @Param("age") int age);
```

### 8.2 问题：动态SQL语法错误

**原因：**
- 标签嵌套错误
- OGNL表达式语法错误
- SQL语法错误

**解决方案：**
- 检查标签嵌套是否正确
- 检查OGNL表达式语法是否正确
- 使用`<where>`、`<set>`等标签自动处理SQL片段
- 打印生成的SQL语句，检查语法是否正确

**示例：**
```xml
<!-- 错误示例 -->
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users WHERE
    <if test="name != null">
        AND name LIKE #{name}
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

### 8.3 问题：SQL注入

**原因：**
- 使用了`${}`占位符
- 未对参数进行验证

**解决方案：**
- 优先使用`#{}`占位符
- 对于必须使用`${}`的场景，进行参数验证
- 使用白名单机制，限制参数的取值范围

**示例：**
```xml
<!-- 不安全的示例 -->
<select id="findByOrder" parameterType="map" resultType="User">
    SELECT * FROM users ORDER BY ${sortBy}
</select>

<!-- 安全的示例 -->
<select id="findByOrder" parameterType="map" resultType="User">
    SELECT * FROM users
    <if test="sortBy == 'name' or sortBy == 'age'">
        ORDER BY ${sortBy}
    </if>
    <if test="sortOrder == 'asc' or sortOrder == 'desc'">
        ${sortOrder}
    </if>
</select>
```

### 8.4 问题：性能问题

**原因：**
- 动态SQL过于复杂
- 生成的SQL语句效率低下
- 未使用缓存

**解决方案：**
- 简化动态SQL逻辑
- 优化生成的SQL语句
- 启用二级缓存
- 合理使用索引

**示例：**
```xml
<!-- 优化前 -->
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT * FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
        <if test="createTime != null">
            AND DATE(create_time) = #{createTime}
        </if>
    </where>
</select>

<!-- 优化后 -->
<select id="findByCondition" parameterType="User" resultType="User">
    SELECT id, name, age FROM users
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="age != null">
            AND age = #{age}
        </if>
        <if test="createTime != null">
            AND create_time BETWEEN #{createTime} AND #{createTimeEnd}
        </if>
    </where>
</select>
```

## 9. 动态SQL的实际应用案例

### 9.1 案例1：用户管理系统

**需求：**
- 根据不同的条件查询用户
- 支持分页和排序
- 支持模糊查询

**实现：**
```xml
<select id="findUsers" parameterType="map" resultType="User">
    SELECT * FROM users
    <where>
        <if test="username != null">
            AND username LIKE #{username}
        </if>
        <if test="email != null">
            AND email LIKE #{email}
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="startDate != null">
            AND create_time >= #{startDate}
        </if>
        <if test="endDate != null">
            AND create_time <= #{endDate}
        </if>
    </where>
    <if test="sortBy != null">
        ORDER BY ${sortBy}
        <if test="sortOrder != null">
            ${sortOrder}
        </if>
    </if>
    <if test="pageSize != null and pageNum != null">
        LIMIT #{pageSize} OFFSET #{offset}
    </if>
</select>
```

### 9.2 案例2：订单管理系统

**需求：**
- 根据订单状态、用户ID、时间范围等条件查询订单
- 支持批量操作

**实现：**
```xml
<!-- 查询订单 -->
<select id="findOrders" parameterType="map" resultType="Order">
    SELECT * FROM orders
    <where>
        <if test="userId != null">
            AND user_id = #{userId}
        </if>
        <if test="status != null">
            AND status = #{status}
        </if>
        <if test="startTime != null">
            AND create_time >= #{startTime}
        </if>
        <if test="endTime != null">
            AND create_time <= #{endTime}
        </if>
    </where>
    ORDER BY create_time DESC
</select>

<!-- 批量更新订单状态 -->
<update id="batchUpdateStatus" parameterType="map">
    UPDATE orders SET status = #{status}
    <where>
        id IN
        <foreach collection="orderIds" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </where>
</update>
```

### 9.3 案例3：商品管理系统

**需求：**
- 根据商品名称、分类、价格范围等条件查询商品
- 支持多条件组合查询

**实现：**
```xml
<select id="findProducts" parameterType="map" resultType="Product">
    SELECT * FROM products
    <where>
        <if test="name != null">
            AND name LIKE #{name}
        </if>
        <if test="categoryId != null">
            AND category_id = #{categoryId}
        </if>
        <if test="minPrice != null">
            AND price >= #{minPrice}
        </if>
        <if test="maxPrice != null">
            AND price <= #{maxPrice}
        </if>
        <if test="brand != null">
            AND brand = #{brand}
        </if>
        <if test="isHot != null">
            AND is_hot = #{isHot}
        </if>
    </where>
    ORDER BY sales DESC
    <if test="limit != null">
        LIMIT #{limit}
    </if>
</select>
```

## 10. 总结

动态SQL是MyBatis的核心特性之一，它提供了强大的标签和表达式，可以灵活地构建SQL语句，提高代码的可读性和可维护性。主要特点包括：

1. **强大的标签支持**：`if`、`choose`、`when`、`otherwise`、`trim`、`where`、`set`、`foreach`、`bind`等
2. **灵活的OGNL表达式**：支持复杂的条件判断
3. **自动处理SQL语法**：去除多余的AND/OR、逗号等
4. **防止SQL注入**：自动处理参数绑定
5. **代码复用**：支持SQL片段

动态SQL的最佳实践：
- 合理使用动态SQL标签
- 保持SQL语句的简洁性
- 优化性能
- 确保安全性
- 提高代码可读性

通过掌握动态SQL的使用，可以更好地利用MyBatis的强大功能，构建高效、安全、可维护的数据库操作代码。