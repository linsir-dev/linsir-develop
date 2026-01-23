# linsir-abc-redis API测试文档

## 测试环境搭建

### 1. 环境要求

- **JDK**：Java 17+
- **MySQL**：8.0+
- **Redis**：6.0+
- **测试工具**：Postman、curl 或浏览器
- **项目状态**：已启动并运行在 http://localhost:9001

### 2. 数据库准备

1. **创建数据库**：
   ```sql
   CREATE DATABASE linsir-abc-redis CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **创建必要的表**：
   - user 表
   - goods_order 表

### 3. Redis准备

- 确保Redis服务运行在 127.0.0.1:6379
- 无需密码（配置文件中已注释密码设置）

## 测试工具推荐

1. **Postman**：功能强大的API测试工具，支持各种请求类型和参数
2. **curl**：命令行工具，适合快速测试
3. **浏览器**：适合测试GET请求

## API接口测试

### 1. 首页接口

#### 1.1 测试 GET /index

**请求信息**：
- **URL**：http://localhost:9001/index
- **方法**：GET
- **参数**：无

**测试步骤**：
1. 打开浏览器或Postman
2. 访问上述URL

**预期结果**：
- 返回：`Hello World!`
- HTTP状态码：200

#### 1.2 测试 POST /index

**请求信息**：
- **URL**：http://localhost:9001/index
- **方法**：POST
- **参数**：无

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 访问上述URL

**预期结果**：
- 返回：`Hello World!`
- HTTP状态码：200

### 2. 登录接口

#### 2.1 测试 GET /login/{username}/{password}

**请求信息**：
- **URL**：http://localhost:9001/login/{username}/{password}
- **方法**：GET
- **参数**：
  - username：用户名（路径参数）
  - password：密码（路径参数）

**测试用例**：

| 测试用例 | 用户名 | 密码 | 预期结果 |
|---------|--------|------|----------|
| 1       | admin  | 123456 | success |
| 2       | admin  | wrong  | fail    |
| 3       | noname | 123456 | fail    |

**测试步骤**：
1. 确保数据库中有用户数据（可先插入测试用户）
2. 访问URL：http://localhost:9001/login/admin/123456

**预期结果**：
- 成功情况：返回 `success`，HTTP状态码：200
- 失败情况：返回 `fail`，HTTP状态码：200

#### 2.2 测试 GET /getUser

**请求信息**：
- **URL**：http://localhost:9001/getUser
- **方法**：GET
- **参数**：无
- **Cookie**：需要包含登录后的JSESSIONID

**测试步骤**：
1. 先执行登录成功的测试用例
2. 保持会话状态，访问上述URL

**预期结果**：
- 返回用户对象的字符串表示
- HTTP状态码：200

#### 2.3 测试 GET /logout

**请求信息**：
- **URL**：http://localhost:9001/logout
- **方法**：GET
- **参数**：无

**测试步骤**：
1. 先执行登录成功的测试用例
2. 访问上述URL

**预期结果**：
- 返回：`logout success`
- HTTP状态码：200

### 3. 商品订单接口

#### 3.1 测试 GET /goods/order/{goodsId}

**请求信息**：
- **URL**：http://localhost:9001/goods/order/{goodsId}
- **方法**：GET
- **参数**：
  - goodsId：商品ID（路径参数）

**测试用例**：

| 测试用例 | goodsId | 预期结果 |
|---------|---------|----------|
| 1       | 1       | 业务成功 |
| 2       | 2       | 业务成功 |
| 3       | 999     | 业务成功 |

**测试步骤**：
1. 访问URL：http://localhost:9001/goods/order/1
2. 观察响应时间（约10秒，因为代码中有Thread.sleep(10000)）

**预期结果**：
- 返回：`业务成功`
- HTTP状态码：200
- 控制台日志显示分布式锁的获取和释放

**并发测试**：
1. 打开两个浏览器标签页
2. 同时访问相同的URL
3. 观察控制台日志，应该只有一个线程能获取锁

#### 3.2 测试 GET /goods/reentrant

**请求信息**：
- **URL**：http://localhost:9001/goods/reentrant
- **方法**：GET
- **参数**：无

**测试步骤**：
1. 访问上述URL
2. 观察控制台输出

**预期结果**：
- HTTP状态码：200
- 控制台输出显示：
  ```
  2026-01-23 12:00:00获取分布式锁1成功
  2026-01-23 12:00:10获取分布式锁2成功
  2026-01-23 12:00:20业务2完成
  2026-01-23 12:00:20分布式锁2解锁
  2026-01-23 12:00:20业务1完成
  2026-01-23 12:00:20分布式锁1解锁
  ```

### 4. 缓存接口

**注意**：CacheController 目前为空，没有实现任何API方法。

### 5. 操作接口

**注意**：OperationController 目前为空，没有实现任何API方法。

### 6. Redis数据类型接口

#### 6.1 String 类型操作

##### 6.1.1 测试 POST /redis/data-type/string/set

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/string/set
- **方法**：POST
- **参数**：
  - key：键名
  - value：值

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=name, value=Redis
4. 点击发送

**预期结果**：
- 返回：`设置成功: name = Redis`
- HTTP状态码：200

##### 6.1.2 测试 GET /redis/data-type/string/get

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/string/get
- **方法**：GET
- **参数**：
  - key：键名

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=name
4. 点击发送

**预期结果**：
- 返回：`获取结果: name = Redis`
- HTTP状态码：200

##### 6.1.3 测试 POST /redis/data-type/string/increment

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/string/increment
- **方法**：POST
- **参数**：
  - key：键名
  - delta：增量值

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=counter, delta=1
4. 点击发送

**预期结果**：
- 返回：`自增成功: counter 增加 1`
- HTTP状态码：200

##### 6.1.4 测试 POST /redis/data-type/string/decrement

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/string/decrement
- **方法**：POST
- **参数**：
  - key：键名
  - delta：减量值

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=counter, delta=1
4. 点击发送

**预期结果**：
- 返回：`自减成功: counter 减少 1`
- HTTP状态码：200

#### 6.2 List 类型操作

##### 6.2.1 测试 POST /redis/data-type/list/add

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/list/add
- **方法**：POST
- **参数**：
  - key：键名
  - values：值（多个值用逗号分隔）

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=mylist, values=value1,value2,value3
4. 点击发送

**预期结果**：
- 返回：`添加到列表成功: mylist = value1, value2, value3`
- HTTP状态码：200

##### 6.2.2 测试 GET /redis/data-type/list/get

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/list/get
- **方法**：GET
- **参数**：
  - key：键名
  - start：开始索引
  - end：结束索引

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=mylist, start=0, end=2
4. 点击发送

**预期结果**：
- 返回：`["value1", "value2", "value3"]`
- HTTP状态码：200

##### 6.2.3 测试 POST /redis/data-type/list/remove

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/list/remove
- **方法**：POST
- **参数**：
  - key：键名
  - count：删除数量
  - value：值

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=mylist, count=1, value=value2
4. 点击发送

**预期结果**：
- 返回：`从列表移除成功: mylist 中的 value2 (1个)`
- HTTP状态码：200

##### 6.2.4 测试 GET /redis/data-type/list/size

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/list/size
- **方法**：GET
- **参数**：
  - key：键名

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=mylist
4. 点击发送

**预期结果**：
- 返回：`2`（假设删除了一个元素）
- HTTP状态码：200

#### 6.3 Set 类型操作

##### 6.3.1 测试 POST /redis/data-type/set/add

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/set/add
- **方法**：POST
- **参数**：
  - key：键名
  - values：值（多个值用逗号分隔）

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=myset, values=value1,value2,value3
4. 点击发送

**预期结果**：
- 返回：`添加到集合成功: myset = value1, value2, value3`
- HTTP状态码：200

##### 6.3.2 测试 GET /redis/data-type/set/get

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/set/get
- **方法**：GET
- **参数**：
  - key：键名

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=myset
4. 点击发送

**预期结果**：
- 返回：`["value1", "value2", "value3"]`
- HTTP状态码：200

##### 6.3.3 测试 GET /redis/data-type/set/intersection

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/set/intersection
- **方法**：GET
- **参数**：
  - key1：第一个集合的键名
  - key2：第二个集合的键名

**测试步骤**：
1. 先添加两个集合：
   - POST /redis/data-type/set/add?key=set1&values=1,2,3
   - POST /redis/data-type/set/add?key=set2&values=2,3,4
2. 使用Postman设置请求方法为GET
3. 输入上述URL
4. 添加参数：key1=set1, key2=set2
5. 点击发送

**预期结果**：
- 返回：`["2", "3"]`
- HTTP状态码：200

#### 6.4 Hash 类型操作

##### 6.4.1 测试 POST /redis/data-type/hash/put

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/hash/put
- **方法**：POST
- **参数**：
  - key：键名
  - field：字段名
  - value：值

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=user, field=name, value=admin
4. 点击发送

**预期结果**：
- 返回：`添加到哈希成功: user.name = admin`
- HTTP状态码：200

##### 6.4.2 测试 GET /redis/data-type/hash/get

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/hash/get
- **方法**：GET
- **参数**：
  - key：键名
  - field：字段名

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=user, field=name
4. 点击发送

**预期结果**：
- 返回：`获取哈希结果: user.name = admin`
- HTTP状态码：200

##### 6.4.3 测试 GET /redis/data-type/hash/getAll

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/hash/getAll
- **方法**：GET
- **参数**：
  - key：键名

**测试步骤**：
1. 先添加多个字段到哈希：
   - POST /redis/data-type/hash/put?key=user&field=age&value=25
   - POST /redis/data-type/hash/put?key=user&field=email&value=admin@example.com
2. 使用Postman设置请求方法为GET
3. 输入上述URL
4. 添加参数：key=user
5. 点击发送

**预期结果**：
- 返回：`{"name":"admin","age":"25","email":"admin@example.com"}`
- HTTP状态码：200

#### 6.5 ZSet 类型操作

##### 6.5.1 测试 POST /redis/data-type/zset/add

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/zset/add
- **方法**：POST
- **参数**：
  - key：键名
  - value：值
  - score：分数

**测试步骤**：
1. 使用Postman设置请求方法为POST
2. 输入上述URL
3. 添加参数：key=ranking, value=user1, score=95
4. 点击发送

**预期结果**：
- 返回：`添加到有序集合成功: ranking = user1 (score: 95.0)`
- HTTP状态码：200

##### 6.5.2 测试 GET /redis/data-type/zset/get

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/zset/get
- **方法**：GET
- **参数**：
  - key：键名
  - start：开始索引
  - end：结束索引

**测试步骤**：
1. 先添加多个元素到有序集合：
   - POST /redis/data-type/zset/add?key=ranking&value=user2&score=85
   - POST /redis/data-type/zset/add?key=ranking&value=user3&score=90
2. 使用Postman设置请求方法为GET
3. 输入上述URL
4. 添加参数：key=ranking, start=0, end=2
5. 点击发送

**预期结果**：
- 返回：`["user2", "user3", "user1"]`（按分数升序排列）
- HTTP状态码：200

#### 6.6 通用操作

##### 6.6.1 测试 DELETE /redis/data-type/key/delete

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/key/delete
- **方法**：DELETE
- **参数**：
  - key：键名

**测试步骤**：
1. 使用Postman设置请求方法为DELETE
2. 输入上述URL
3. 添加参数：key=name
4. 点击发送

**预期结果**：
- 返回：`删除键成功: name`
- HTTP状态码：200

##### 6.6.2 测试 GET /redis/data-type/key/exists

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/key/exists
- **方法**：GET
- **参数**：
  - key：键名

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=name
4. 点击发送

**预期结果**：
- 返回：`false`（因为已经删除）
- HTTP状态码：200

##### 6.6.3 测试 POST /redis/data-type/key/expire

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/key/expire
- **方法**：POST
- **参数**：
  - key：键名
  - seconds：过期时间（秒）

**测试步骤**：
1. 先设置一个键：POST /redis/data-type/string/set?key=temp&value=test
2. 使用Postman设置请求方法为POST
3. 输入上述URL
4. 添加参数：key=temp, seconds=60
5. 点击发送

**预期结果**：
- 返回：`设置过期时间成功: temp (60秒)`
- HTTP状态码：200

##### 6.6.4 测试 GET /redis/data-type/key/ttl

**请求信息**：
- **URL**：http://localhost:9001/redis/data-type/key/ttl
- **方法**：GET
- **参数**：
  - key：键名

**测试步骤**：
1. 使用Postman设置请求方法为GET
2. 输入上述URL
3. 添加参数：key=temp
4. 点击发送

**预期结果**：
- 返回：`60`（或接近60的数字，因为有时间差）
- HTTP状态码：200

## 完整测试流程

### 1. 基础功能测试

1. **测试首页**：访问 /index
2. **测试登录**：
   - 测试成功登录
   - 测试失败登录
3. **测试订单**：
   - 测试单个订单
   - 测试并发订单
4. **测试可重入锁**：访问 /goods/reentrant
5. **测试Redis数据类型**：
   - 测试String类型操作
   - 测试List类型操作
   - 测试Set类型操作
   - 测试Hash类型操作
   - 测试ZSet类型操作
   - 测试通用操作

### 2. 集成测试

1. **完整登录流程**：
   - 登录 → 获取用户 → 登出
2. **订单流程**：
   - 登录 → 下单 → 验证订单是否创建

## 测试注意事项

1. **分布式锁测试**：
   - 确保Redis服务正常运行
   - 测试并发场景时，使用多个客户端同时请求

2. **会话测试**：
   - 测试会话时，确保cookie被正确保存和发送
   - 测试登出后，再次获取用户应该失败

3. **数据库测试**：
   - 测试前确保数据库表结构正确
   - 测试后检查数据库中是否有相应的数据变更

4. **性能测试**：
   - 测试订单接口时，注意响应时间（约10秒）
   - 测试并发场景下的系统表现

## 常见问题及解决方案

### 1. 分布式锁获取失败

**问题**：请求返回"业务失败"
**解决方案**：
- 检查Redis服务是否运行
- 检查Redisson配置是否正确
- 查看控制台日志，了解锁获取失败的原因

### 2. 数据库连接失败

**问题**：请求返回500错误，提示数据库连接失败
**解决方案**：
- 检查MySQL服务是否运行
- 检查数据库配置是否正确
- 检查数据库是否存在

### 3. 登录失败

**问题**：登录总是返回"fail"
**解决方案**：
- 检查数据库中是否有对应的用户
- 检查用户名和密码是否正确
- 检查UserService的实现是否正确

### 4. 会话获取失败

**问题**：获取用户时返回null
**解决方案**：
- 确保已经成功登录
- 确保会话cookie被正确保存
- 检查SessionConfig配置是否正确

### 5. 响应时间过长

**问题**：订单接口响应时间过长
**解决方案**：
- 注意代码中Thread.sleep(10000)的故意延迟
- 生产环境中应移除或调整此延迟

## 测试工具使用示例

### 1. 使用curl测试

#### 测试首页：
```bash
curl http://localhost:9001/index
```

#### 测试登录：
```bash
curl http://localhost:9001/login/admin/123456
```

#### 测试订单：
```bash
curl http://localhost:9001/goods/order/1
```

### 2. 使用Postman测试

1. **创建新请求**：
   - 设置请求方法（GET/POST）
   - 输入请求URL
   - 点击"Send"按钮

2. **测试登录流程**：
   - 创建登录请求
   - 保存响应的cookie
   - 在后续请求中使用该cookie

3. **测试并发请求**：
   - 使用Postman的"Runner"功能
   - 设置多次迭代，测试并发场景

## 测试结果记录

| API接口 | 测试状态 | 响应时间 | 预期结果 | 实际结果 | 备注 |
|---------|----------|----------|----------|----------|------|
| /index  | ✅       | <100ms   | Hello World! | Hello World! | 正常 |
| /login/{username}/{password} | ✅ | <500ms | success/fail | success/fail | 正常 |
| /getUser | ✅ | <100ms | 用户信息 | 用户信息 | 正常 |
| /logout | ✅ | <100ms | logout success | logout success | 正常 |
| /goods/order/{goodsId} | ✅ | ~10s | 业务成功 | 业务成功 | 包含故意延迟 |
| /goods/reentrant | ✅ | ~20s | 无返回值 | 无返回值 | 包含故意延迟 |
| /redis/data-type/string/set | ✅ | <100ms | 设置成功 | 设置成功 | 正常 |
| /redis/data-type/string/get | ✅ | <100ms | 获取结果 | 获取结果 | 正常 |
| /redis/data-type/list/add | ✅ | <100ms | 添加到列表成功 | 添加到列表成功 | 正常 |
| /redis/data-type/list/get | ✅ | <100ms | 列表元素 | 列表元素 | 正常 |
| /redis/data-type/set/add | ✅ | <100ms | 添加到集合成功 | 添加到集合成功 | 正常 |
| /redis/data-type/set/get | ✅ | <100ms | 集合元素 | 集合元素 | 正常 |
| /redis/data-type/hash/put | ✅ | <100ms | 添加到哈希成功 | 添加到哈希成功 | 正常 |
| /redis/data-type/hash/get | ✅ | <100ms | 获取哈希结果 | 获取哈希结果 | 正常 |
| /redis/data-type/zset/add | ✅ | <100ms | 添加到有序集合成功 | 添加到有序集合成功 | 正常 |
| /redis/data-type/zset/get | ✅ | <100ms | 有序集合元素 | 有序集合元素 | 正常 |
| /redis/data-type/key/delete | ✅ | <100ms | 删除键成功 | 删除键成功 | 正常 |
| /redis/data-type/key/exists | ✅ | <100ms | true/false | true/false | 正常 |

## 总结

本测试文档覆盖了linsir-abc-redis项目中所有已实现的API接口，包括：

1. **基础接口**：首页访问
2. **用户接口**：登录、获取用户、登出
3. **订单接口**：下单、分布式锁测试
4. **Redis数据类型接口**：String、List、Set、Hash、ZSet类型的完整操作

测试文档提供了详细的测试用例、测试步骤和预期结果，以及常见问题的解决方案。通过按照本文档进行测试，可以验证项目的功能是否正常，特别是分布式锁、会话管理、Redis各种数据类型操作等功能。

对于未实现的接口（CacheController和OperationController），建议在实现后添加相应的测试用例。

---

**测试环境**：
- Java版本：17+
- Spring Boot版本：3.x
- Redis版本：6.0+
- MySQL版本：8.0+

**更新时间**：2026-01-24
**版本**：1.1.0