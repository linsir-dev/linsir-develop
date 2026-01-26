# Hutool 工具库示例

## 目录介绍

本目录包含了 Hutool 工具库的示例代码，旨在演示 Hutool 提供的各种实用工具类的使用方法。Hutool 是一个 Java 工具包，提供了丰富的工具类，简化了 Java 开发中的各种常见操作。

## 依赖说明

本示例代码基于 Hutool 5.8.26 版本，需要在 Maven 项目的 `pom.xml` 文件中添加以下依赖：

```xml
<dependency>
    <groupId>cn.hutool</groupId>
    <artifactId>hutool-all</artifactId>
    <version>5.8.26</version>
</dependency>
```

## 示例代码结构

本目录包含以下示例代码文件：

1. **HutoolCoreDemo.java** - 核心工具类示例
2. **HutoolIODemo.java** - IO 工具类示例
3. **HutoolHttpDemo.java** - HTTP 工具类示例
4. **HutoolCryptoDemo.java** - 加密工具类示例
5. **HutoolDemoMain.java** - 主类，用于运行所有示例

## 功能说明

### 1. HutoolCoreDemo.java

演示 Hutool 核心工具类的使用，包括：

- **字符串工具** (`StrUtil`)：字符串判空、操作、格式化、分割等
- **日期工具** (`DateUtil`)：日期格式化、解析、计算、计时器等
- **数字工具** (`RandomUtil`)：随机数生成、ID 生成等
- **验证工具** (`Validator`)：数字、邮箱、手机号验证等
- **其他工具**：Console 工具、系统工具等

### 2. HutoolIODemo.java

演示 Hutool IO 工具类的使用，包括：

- **文件工具** (`FileUtil`)：文件创建、读取、写入、复制、删除等
- **流工具** (`IoUtil`)：流的转换、读取、写入、关闭等
- **资源工具** (`ResourceUtil`)：ClassPath 资源的加载和读取
- **文件监视器** (`WatchMonitor`)：文件变化的监听和处理

### 3. HutoolHttpDemo.java

演示 Hutool HTTP 工具类的使用，包括：

- **HTTP 请求** (`HttpUtil`)：发送 GET、POST 等 HTTP 请求
- **高级 HTTP** (`HttpRequest`)：自定义 HTTP 请求头、超时等
- **文件下载**：从 URL 下载文件到本地
- **JSON 工具** (`JSONUtil`)：JSON 字符串的解析、生成和美化
- **HTTP 会话**：模拟 HTTP 会话管理

### 4. HutoolCryptoDemo.java

演示 Hutool 加密工具类的使用，包括：

- **摘要加密** (`DigestUtil`)：MD5、SHA-1、SHA-256、SHA-512 等
- **对称加密** (`SymmetricCrypto`)：AES、DES 等
- **非对称加密** (`RSA`)：RSA 密钥对生成、加密、解密、签名等
- **安全工具** (`SecureUtil`)：简化的加密工具方法
- **Base64 编码**：Base64 编码和解码

### 5. HutoolDemoMain.java

主类，用于运行和验证所有 Hutool 示例代码。它会依次运行上述四个示例类中的所有演示方法，并输出运行结果。

## 运行说明

### 方法一：使用 Maven 运行

在项目根目录下执行以下命令：

```bash
mvn exec:java -Dexec.mainClass=com.linsir.abc.pdai.tools.hutool.HutoolDemoMain
```

### 方法二：在 IDE 中运行

1. 打开项目在 IDE 中
2. 找到 `HutoolDemoMain.java` 文件
3. 右键点击该文件，选择 "Run" 或 "运行" 选项

## 示例代码说明

### 1. 核心工具类示例

#### 字符串工具

```java
// 字符串判空
StrUtil.isEmpty(str);
StrUtil.isBlank(str);

// 字符串操作
StrUtil.sub(str, 0, 5);
StrUtil.removePrefix(str, "Hello");
StrUtil.removeSuffix(str, "!");

// 字符串格式化
StrUtil.format("Hello, {}! Today is {}.", "World", "Monday");

// 字符串分割
List<String> splitList = StrUtil.split("a,b,c,d", ",");
```

#### 日期工具

```java
// 获取当前时间
Date now = DateUtil.date();

// 日期格式化
DateUtil.formatDateTime(now);
DateUtil.formatDate(now);
DateUtil.formatTime(now);

// 字符串转日期
Date date = DateUtil.parse("2024-01-01 12:00:00");

// 日期计算
Date tomorrow = DateUtil.tomorrow();
Date nextWeek = DateUtil.offsetWeek(now, 1);
Date lastMonth = DateUtil.offsetMonth(now, -1);

// 日期差值
long betweenDay = DateUtil.between(date, now, DateUnit.DAY);

// 计时器
TimeInterval timer = DateUtil.timer();
timer.interval(); // 毫秒
timer.intervalSecond(); // 秒
```

#### 数字工具

```java
// 随机数
int randomInt = RandomUtil.randomInt(1, 100);
double randomDouble = RandomUtil.randomDouble(0, 1);
String randomStr = RandomUtil.randomString(10);
String randomNumbers = RandomUtil.randomNumbers(6);

// ID 生成
String uuid = IdUtil.randomUUID();
String simpleUUID = IdUtil.simpleUUID();
long snowflakeId = IdUtil.getSnowflakeNextId();
```

#### 验证工具

```java
// 验证是否为数字
Validator.isNumber("123");

// 验证是否为邮箱
Validator.isEmail("test@example.com");

// 验证是否为手机号
Validator.isMobile("13800138000");
```

### 2. IO 工具类示例

#### 文件工具

```java
// 创建目录
FileUtil.mkdir("/path/to/dir");

// 写入文件
FileUtil.writeString("Hello, Hutool IO!", "test.txt", StandardCharsets.UTF_8);

// 读取文件
String content = FileUtil.readString("test.txt", StandardCharsets.UTF_8);

// 复制文件
FileUtil.copy("source.txt", "target.txt", true);

// 文件信息
FileUtil.exist("test.txt");
FileUtil.size(new File("test.txt"));
FileUtil.getAbsolutePath("test.txt");
FileUtil.getName("test.txt");
FileUtil.getSuffix("test.txt");

// 列出目录下的文件
File[] files = FileUtil.ls("/path/to/dir");

// 删除文件
FileUtil.del("test.txt");
```

#### 流工具

```java
// 字节数组转输入流
InputStream inputStream = IoUtil.toStream("Hello".getBytes());

// 输入流转字节数组
byte[] bytes = IoUtil.readBytes(inputStream);

// 输入流转字符串
String str = IoUtil.read(inputStream, StandardCharsets.UTF_8);

// 关闭流
IoUtil.close(inputStream);
```

#### 资源工具

```java
// 读取 ClassPath 资源
String content = ResourceUtil.readUtf8Str("logback.xml");

// 检查资源是否存在
ResourceUtil.getResource("logback.xml") != null;
```

#### 文件监视器

```java
// 创建文件监视器
WatchMonitor watchMonitor = WatchMonitor.createAll("/path/to/dir", new Watcher() {
    @Override
    public void onCreate(WatchEvent<?> event, Path currentPath) {
        // 处理文件创建事件
    }
    
    @Override
    public void onModify(WatchEvent<?> event, Path currentPath) {
        // 处理文件修改事件
    }
    
    @Override
    public void onDelete(WatchEvent<?> event, Path currentPath) {
        // 处理文件删除事件
    }
    
    @Override
    public void onOverflow(WatchEvent<?> event, Path currentPath) {
        // 处理事件溢出
    }
});

// 启动监视器
watchMonitor.setDaemon(true);
watchMonitor.start();

// 停止监视器
watchMonitor.close();
```

### 3. HTTP 工具类示例

#### HTTP 请求

```java
// 发送 GET 请求
String result = HttpUtil.get("https://httpbin.org/get");

// 带参数的 GET 请求
Map<String, Object> params = new HashMap<>();
params.put("name", "Hutool");
params.put("version", "5.8.26");
String result = HttpUtil.get("https://httpbin.org/get", params);

// 发送 POST 请求
Map<String, Object> formParams = new HashMap<>();
formParams.put("username", "test");
formParams.put("password", "123456");
String result = HttpUtil.post("https://httpbin.org/post", formParams);

// 发送 JSON 参数的 POST 请求
JSONObject jsonParams = new JSONObject();
jsonParams.put("name", "Hutool");
jsonParams.put("type", "HTTP Client");
String result = HttpUtil.post("https://httpbin.org/post", jsonParams.toString());
```

#### 高级 HTTP

```java
// 自定义 HTTP 请求
HttpResponse response = HttpRequest.put("https://httpbin.org/put")
        .header("Content-Type", "application/json")
        .header("User-Agent", "Hutool HTTP Client")
        .body("{\"method\": \"PUT\", \"data\": \"test\"}")
        .timeout(5000) // 超时时间
        .execute();

// 获取响应信息
int status = response.getStatus();
Map<String, List<String>> headers = response.headers();
String body = response.body();

// 关闭响应
response.close();
```

#### 文件下载

```java
// 下载文件
String fileUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt";
String destFile = "/path/to/save/LICENSE-2.0.txt";
long size = HttpUtil.downloadFile(fileUrl, destFile);
```

#### JSON 工具

```java
// JSON 字符串转对象
String jsonStr = "{\"name\": \"Hutool\", \"version\": \"5.8.26\"}";
JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
String name = jsonObject.getStr("name");

// 对象转 JSON
Map<String, Object> map = new HashMap<>();
map.put("name", "Hutool");
map.put("version", "5.8.26");
String json = JSONUtil.toJsonStr(map);

// 美化 JSON
String prettyJson = JSONUtil.toJsonPrettyStr(map);
```

### 4. 加密工具类示例

#### 摘要加密

```java
// MD5 加密
String md5Hex = DigestUtil.md5Hex("Hello");

// SHA-1 加密
String sha1Hex = DigestUtil.sha1Hex("Hello");

// SHA-256 加密
String sha256Hex = DigestUtil.sha256Hex("Hello");

// SHA-512 加密
String sha512Hex = DigestUtil.sha512Hex("Hello");

// 带盐值的 MD5
String salt = "hutool";
String md5WithSalt = DigestUtil.md5Hex("Hello" + salt);
```

#### 对称加密

```java
// AES 加密
SymmetricCrypto aes = new AES();
String encrypted = aes.encryptHex("Hello");
String decrypted = aes.decryptStr(encrypted, StandardCharsets.UTF_8);

// 使用自定义密钥的 AES 加密
String key = "1234567890123456";
SymmetricCrypto aesWithKey = new AES(key.getBytes());
String encrypted = aesWithKey.encryptHex("Hello");
String decrypted = aesWithKey.decryptStr(encrypted, StandardCharsets.UTF_8);

// DES 加密
SymmetricCrypto des = new DES();
String encrypted = des.encryptHex("Hello");
String decrypted = des.decryptStr(encrypted, StandardCharsets.UTF_8);
```

#### 非对称加密

```java
// 生成 RSA 密钥对
RSA rsa = new RSA();
String publicKeyBase64 = rsa.getPublicKeyBase64();
String privateKeyBase64 = rsa.getPrivateKeyBase64();

// 使用公钥加密
String encrypted = rsa.encryptHex("Hello", KeyType.PublicKey);

// 使用私钥解密
String decrypted = rsa.decryptStr(encrypted, KeyType.PrivateKey);

// 使用私钥加密，公钥解密（签名和验证）
String signed = rsa.encryptHex("Hello", KeyType.PrivateKey);
String verified = rsa.decryptStr(signed, KeyType.PublicKey);
```

#### Base64 编码

```java
// Base64 编码
String base64Encoded = Base64.encode("Hello");

// Base64 解码
String base64Decoded = Base64.decodeStr(base64Encoded);
```

## 注意事项

1. **依赖版本**：示例代码基于 Hutool 5.8.26 版本，其他版本可能存在 API 差异
2. **网络连接**：HTTP 示例需要网络连接才能正常运行
3. **文件系统**：IO 示例会在本地文件系统创建临时文件和目录
4. **性能考虑**：加密示例中的 RSA 密钥对生成可能较为耗时
5. **错误处理**：示例代码中的错误处理较为简单，实际应用中应根据需要进行完善

## 总结

Hutool 是一个功能强大的 Java 工具包，提供了丰富的工具类，简化了 Java 开发中的各种常见操作。本示例代码涵盖了 Hutool 的主要功能模块，包括核心工具、IO 操作、HTTP 客户端和加密工具等。通过学习和使用这些示例代码，开发者可以更高效地利用 Hutool 工具库，提高开发效率，减少重复代码。