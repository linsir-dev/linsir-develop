# 环境抽象模块功能扩展设计文档

## 1. 设计目标

本文档提供环境抽象模块的功能扩展设计，包括：
- 配置文件加载支持
- 加密属性源
- 动态配置刷新
- 配置验证
- 配置元数据

## 2. 配置文件加载扩展

### 2.1 设计意图

支持从 YAML、Properties、JSON 等配置文件加载属性，简化配置管理。

### 2.2 核心类设计

#### 2.2.1 ConfigFilePropertySource

```java
package com.linsir.spring.framework.spring_core.env.source;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 配置文件属性源
 *
 * 支持从各种配置文件格式加载属性。
 *
 * @author linsir
 * @since 1.0.0
 */
public class ConfigFilePropertySource extends MapPropertySource {

    /**
     * 配置文件格式
     */
    public enum Format {
        PROPERTIES,
        YAML,
        JSON
    }

    /**
     * 创建配置文件属性源
     *
     * @param name 属性源名称
     * @param path 配置文件路径
     * @param format 文件格式
     */
    public ConfigFilePropertySource(String name, Path path, Format format) {
        super(name, loadProperties(path, format));
    }

    /**
     * 加载属性
     */
    private static Map<String, Object> loadProperties(Path path, Format format) {
        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Config file not found: " + path);
        }

        try (InputStream is = Files.newInputStream(path)) {
            switch (format) {
                case PROPERTIES:
                    return loadProperties(is);
                case YAML:
                    return loadYaml(is);
                case JSON:
                    return loadJson(is);
                default:
                    throw new IllegalArgumentException("Unsupported format: " + format);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + path, e);
        }
    }

    /**
     * 加载 Properties 格式
     */
    private static Map<String, Object> loadProperties(InputStream is) throws IOException {
        Properties props = new Properties();
        props.load(is);
        Map<String, Object> result = new HashMap<>();
        props.forEach((k, v) -> result.put(String.valueOf(k), v));
        return result;
    }

    /**
     * 加载 YAML 格式
     */
    private static Map<String, Object> loadYaml(InputStream is) {
        // 需要引入 YAML 解析库，如 SnakeYAML
        // 这里简化处理
        throw new UnsupportedOperationException("YAML support requires SnakeYAML library");
    }

    /**
     * 加载 JSON 格式
     */
    private static Map<String, Object> loadJson(InputStream is) {
        // 需要引入 JSON 解析库
        // 这里简化处理
        throw new UnsupportedOperationException("JSON support requires JSON library");
    }
}
```

#### 2.2.2 ConfigFileLoader

```java
package com.linsir.spring.framework.spring_core.env.support;

import com.linsir.spring.framework.spring_core.env.source.ConfigFilePropertySource;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置文件加载器
 *
 * 自动加载指定目录下的配置文件。
 *
 * @author linsir
 * @since 1.0.0
 */
public class ConfigFileLoader {

    /**
     * 默认配置文件路径
     */
    private static final String DEFAULT_CONFIG_PATH = "config/";

    /**
     * 加载配置文件到环境
     *
     * @param environment 环境对象
     * @param configPath 配置文件路径
     */
    public static void load(ConfigurableEnvironment environment, String configPath) {
        Path path = Paths.get(configPath);
        if (!path.toFile().exists()) {
            return;
        }

        // 加载 application.properties
        loadPropertiesFile(environment, path.resolve("application.properties"));

        // 加载 application-{profile}.properties
        for (String profile : environment.getActiveProfiles()) {
            loadPropertiesFile(environment, path.resolve("application-" + profile + ".properties"));
        }
    }

    /**
     * 加载 Properties 文件
     */
    private static void loadPropertiesFile(ConfigurableEnvironment environment, Path path) {
        if (path.toFile().exists()) {
            ConfigFilePropertySource source = new ConfigFilePropertySource(
                path.getFileName().toString(),
                path,
                ConfigFilePropertySource.Format.PROPERTIES
            );
            environment.addPropertySource(source);
        }
    }

    /**
     * 使用默认路径加载
     */
    public static void load(ConfigurableEnvironment environment) {
        load(environment, DEFAULT_CONFIG_PATH);
    }
}
```

### 2.3 使用示例

```java
// 创建环境
StandardEnvironment env = new StandardEnvironment();
env.setActiveProfiles("dev");

// 加载配置文件
ConfigFileLoader.load(env, "config/");

// 现在可以使用配置文件中的属性
String dbUrl = env.getProperty("database.url");
```

### 2.4 扩展建议

1. **支持 YAML**: 引入 SnakeYAML 库
2. **支持 JSON**: 引入 Jackson 或 Gson 库
3. **配置热加载**: 监听文件变化，自动重新加载
4. **配置加密**: 支持加密配置文件

## 3. 加密属性源扩展

### 3.1 设计意图

支持敏感信息（密码、密钥等）的加密存储和解密读取。

### 3.2 核心类设计

#### 3.2.1 EncryptablePropertySource

```java
package com.linsir.spring.framework.spring_core.env.source;

import java.util.function.Function;

/**
 * 可加密属性源装饰器
 *
 * 对属性值进行解密处理。
 *
 * @author linsir
 * @since 1.0.0
 */
public class EncryptablePropertySource extends PropertySource<PropertySource<?>> {

    /**
     * 加密前缀
     */
    public static final String ENCRYPTED_PREFIX = "ENC(";

    /**
     * 加密后缀
     */
    public static final String ENCRYPTED_SUFFIX = ")";

    /**
     * 解密函数
     */
    private final Function<String, String> decryptor;

    /**
     * 创建可加密属性源
     *
     * @param delegate 委托属性源
     * @param decryptor 解密函数
     */
    public EncryptablePropertySource(PropertySource<?> delegate, Function<String, String> decryptor) {
        super(delegate.getName(), delegate);
        this.decryptor = decryptor;
    }

    @Override
    public boolean containsProperty(String name) {
        return this.source.containsProperty(name);
    }

    @Override
    public Object getProperty(String name) {
        Object value = this.source.getProperty(name);
        if (value instanceof String) {
            return decryptIfNecessary((String) value);
        }
        return value;
    }

    /**
     * 解密如果需要
     */
    private String decryptIfNecessary(String value) {
        if (isEncrypted(value)) {
            String encrypted = value.substring(
                ENCRYPTED_PREFIX.length(),
                value.length() - ENCRYPTED_SUFFIX.length()
            );
            return decryptor.apply(encrypted);
        }
        return value;
    }

    /**
     * 判断是否加密
     */
    private boolean isEncrypted(String value) {
        return value != null &&
               value.startsWith(ENCRYPTED_PREFIX) &&
               value.endsWith(ENCRYPTED_SUFFIX);
    }
}
```

#### 3.2.2 EncryptionUtils

```java
package com.linsir.spring.framework.spring_core.env.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 *
 * 提供基本的加密解密功能。
 *
 * @author linsir
 * @since 1.0.0
 */
public final class EncryptionUtils {

    private EncryptionUtils() {
        // 工具类
    }

    /**
     * 使用 AES 加密
     *
     * @param plainText 明文
     * @param key 密钥
     * @return 密文
     */
    public static String encrypt(String plainText, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * 使用 AES 解密
     *
     * @param encryptedText 密文
     * @param key 密钥
     * @return 明文
     */
    public static String decrypt(String encryptedText, String key) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
```

### 3.3 使用示例

```java
// 创建原始属性源
Map<String, Object> map = new HashMap<>();
map.put("db.password", "ENC(AbCdEfGhIjKlMnOpQrStUvWxYz==)");
MapPropertySource source = new MapPropertySource("dbConfig", map);

// 包装为可加密属性源
String key = "mySecretKey12345"; // 16字节密钥
EncryptablePropertySource encryptedSource = new EncryptablePropertySource(
    source,
    encrypted -> EncryptionUtils.decrypt(encrypted, key)
);

// 获取属性时自动解密
String password = encryptedSource.getProperty("db.password");
// 结果: 原始密码（已解密）
```

### 3.4 安全建议

1. **密钥管理**: 使用密钥管理服务（KMS）存储密钥
2. **密钥轮换**: 定期更换加密密钥
3. **算法选择**: 使用强加密算法（AES-256）
4. **传输安全**: 确保密钥传输过程安全

## 4. 动态配置刷新扩展

### 4.1 设计意图

支持运行时动态刷新配置，无需重启应用。

### 4.2 核心类设计

#### 4.2.1 RefreshablePropertySource

```java
package com.linsir.spring.framework.spring_core.env.source;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 可刷新属性源
 *
 * 支持定时刷新属性值。
 *
 * @author linsir
 * @since 1.0.0
 */
public class RefreshablePropertySource extends MapPropertySource {

    /**
     * 属性加载器
     */
    private final Supplier<Map<String, Object>> loader;

    /**
     * 刷新间隔（毫秒）
     */
    private final long refreshInterval;

    /**
     * 调度器
     */
    private final ScheduledExecutorService scheduler;

    /**
     * 创建可刷新属性源
     *
     * @param name 属性源名称
     * @param loader 属性加载器
     * @param refreshInterval 刷新间隔（毫秒）
     */
    public RefreshablePropertySource(String name, Supplier<Map<String, Object>> loader, long refreshInterval) {
        super(name, loader.get());
        this.loader = loader;
        this.refreshInterval = refreshInterval;
        this.scheduler = new ScheduledThreadPoolExecutor(1);
        startRefreshTask();
    }

    /**
     * 启动刷新任务
     */
    private void startRefreshTask() {
        scheduler.scheduleAtFixedRate(
            this::refresh,
            refreshInterval,
            refreshInterval,
            TimeUnit.MILLISECONDS
        );
    }

    /**
     * 刷新属性
     */
    public synchronized void refresh() {
        Map<String, Object> newProperties = loader.get();
        if (newProperties != null) {
            // 清空并重新加载
            this.source.clear();
            this.source.putAll(newProperties);
        }
    }

    /**
     * 停止刷新
     */
    public void stop() {
        scheduler.shutdown();
    }
}
```

#### 4.2.2 EnvironmentRefreshEvent

```java
package com.linsir.spring.framework.spring_core.env.event;

import com.linsir.spring.framework.spring_core.env.core.Environment;
import java.util.Set;

/**
 * 环境刷新事件
 *
 * 当配置发生变化时触发。
 *
 * @author linsir
 * @since 1.0.0
 */
public class EnvironmentRefreshEvent {

    /**
     * 环境对象
     */
    private final Environment environment;

    /**
     * 变化的属性键
     */
    private final Set<String> changedKeys;

    public EnvironmentRefreshEvent(Environment environment, Set<String> changedKeys) {
        this.environment = environment;
        this.changedKeys = changedKeys;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Set<String> getChangedKeys() {
        return changedKeys;
    }
}
```

#### 4.2.3 EnvironmentRefreshListener

```java
package com.linsir.spring.framework.spring_core.env.event;

/**
 * 环境刷新监听器
 *
 * @author linsir
 * @since 1.0.0
 */
@FunctionalInterface
public interface EnvironmentRefreshListener {

    /**
     * 处理刷新事件
     *
     * @param event 刷新事件
     */
    void onRefresh(EnvironmentRefreshEvent event);
}
```

### 4.3 使用示例

```java
// 创建可刷新属性源
RefreshablePropertySource refreshableSource = new RefreshablePropertySource(
    "dynamicConfig",
    () -> loadConfigFromDatabase(), // 从数据库加载配置
    60000 // 每60秒刷新一次
);

// 添加到环境
environment.addPropertySource(refreshableSource);

// 注册刷新监听器
environment.addRefreshListener(event -> {
    System.out.println("配置已刷新: " + event.getChangedKeys());
    // 重新初始化相关组件
});
```

### 4.4 注意事项

1. **线程安全**: 确保刷新过程线程安全
2. **一致性**: 考虑配置刷新的一致性
3. **性能**: 避免频繁刷新影响性能
4. **回滚**: 支持配置刷新失败时的回滚

## 5. 配置验证扩展

### 5.1 设计意图

支持配置属性的校验，确保配置值符合预期。

### 5.2 核心类设计

#### 5.2.1 PropertyValidator

```java
package com.linsir.spring.framework.spring_core.env.validation;

import com.linsir.spring.framework.spring_core.env.core.Environment;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 属性验证器
 *
 * @author linsir
 * @since 1.0.0
 */
public class PropertyValidator {

    /**
     * 验证规则列表
     */
    private final List<ValidationRule> rules = new ArrayList<>();

    /**
     * 添加验证规则
     *
     * @param key 属性键
     * @param required 是否必需
     * @param validator 验证器
     * @return this
     */
    public PropertyValidator addRule(String key, boolean required, Predicate<Object> validator) {
        rules.add(new ValidationRule(key, required, validator));
        return this;
    }

    /**
     * 验证环境
     *
     * @param environment 环境对象
     * @return 验证结果
     */
    public ValidationResult validate(Environment environment) {
        List<String> errors = new ArrayList<>();

        for (ValidationRule rule : rules) {
            Object value = environment.getProperty(rule.key);

            if (value == null) {
                if (rule.required) {
                    errors.add("Required property '" + rule.key + "' is missing");
                }
                continue;
            }

            if (!rule.validator.test(value)) {
                errors.add("Property '" + rule.key + "' has invalid value: " + value);
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    /**
     * 验证规则
     */
    private static class ValidationRule {
        final String key;
        final boolean required;
        final Predicate<Object> validator;

        ValidationRule(String key, boolean required, Predicate<Object> validator) {
            this.key = key;
            this.required = required;
            this.validator = validator;
        }
    }
}
```

#### 5.2.2 ValidationResult

```java
package com.linsir.spring.framework.spring_core.env.validation;

import java.util.List;

/**
 * 验证结果
 *
 * @author linsir
 * @since 1.0.0
 */
public class ValidationResult {

    private final boolean valid;
    private final List<String> errors;

    public ValidationResult(boolean valid, List<String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void throwIfInvalid() {
        if (!valid) {
            throw new IllegalStateException("Configuration validation failed: " + String.join(", ", errors));
        }
    }
}
```

### 5.3 使用示例

```java
// 创建验证器
PropertyValidator validator = new PropertyValidator()
    .addRule("server.port", true, v -> {
        int port = Integer.parseInt(v.toString());
        return port > 0 && port < 65536;
    })
    .addRule("database.url", true, v -> v.toString().startsWith("jdbc:"))
    .addRule("app.name", true, v -> !v.toString().isEmpty())
    .addRule("app.debug", false, v -> v instanceof Boolean || v.toString().matches("true|false"));

// 验证
ValidationResult result = validator.validate(environment);
if (!result.isValid()) {
    System.err.println("配置验证失败:");
    result.getErrors().forEach(System.err::println);
    System.exit(1);
}

// 或抛出异常
result.throwIfInvalid();
```

## 6. 配置元数据扩展

### 6.1 设计意图

提供配置属性的元数据信息，用于配置提示和文档生成。

### 6.2 核心类设计

#### 6.2.1 PropertyMetadata

```java
package com.linsir.spring.framework.spring_core.env.metadata;

/**
 * 属性元数据
 *
 * @author linsir
 * @since 1.0.0
 */
public class PropertyMetadata {

    /**
     * 属性键
     */
    private final String key;

    /**
     * 属性类型
     */
    private final Class<?> type;

    /**
     * 默认值
     */
    private final Object defaultValue;

    /**
     * 描述
     */
    private final String description;

    /**
     * 是否必需
     */
    private final boolean required;

    /**
     * 示例值
     */
    private final String example;

    public PropertyMetadata(String key, Class<?> type, Object defaultValue,
                           String description, boolean required, String example) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
        this.description = description;
        this.required = required;
        this.example = example;
    }

    // Getters...
    public String getKey() { return key; }
    public Class<?> getType() { return type; }
    public Object getDefaultValue() { return defaultValue; }
    public String getDescription() { return description; }
    public boolean isRequired() { return required; }
    public String getExample() { return example; }
}
```

#### 6.2.2 PropertyMetadataRegistry

```java
package com.linsir.spring.framework.spring_core.env.metadata;

import java.util.*;

/**
 * 属性元数据注册表
 *
 * @author linsir
 * @since 1.0.0
 */
public class PropertyMetadataRegistry {

    /**
     * 元数据映射
     */
    private final Map<String, PropertyMetadata> metadataMap = new HashMap<>();

    /**
     * 注册元数据
     *
     * @param metadata 元数据
     */
    public void register(PropertyMetadata metadata) {
        metadataMap.put(metadata.getKey(), metadata);
    }

    /**
     * 获取元数据
     *
     * @param key 属性键
     * @return 元数据
     */
    public Optional<PropertyMetadata> getMetadata(String key) {
        return Optional.ofNullable(metadataMap.get(key));
    }

    /**
     * 获取所有元数据
     *
     * @return 元数据集合
     */
    public Collection<PropertyMetadata> getAllMetadata() {
        return Collections.unmodifiableCollection(metadataMap.values());
    }

    /**
     * 生成配置文档
     *
     * @return Markdown 格式的文档
     */
    public String generateDocumentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("# 配置属性文档\n\n");
        sb.append("| 属性 | 类型 | 必需 | 默认值 | 描述 |\n");
        sb.append("|------|------|------|--------|------|\n");

        metadataMap.values().stream()
            .sorted(Comparator.comparing(PropertyMetadata::getKey))
            .forEach(m -> {
                sb.append(String.format("| %s | %s | %s | %s | %s |\n",
                    m.getKey(),
                    m.getType().getSimpleName(),
                    m.isRequired() ? "是" : "否",
                    m.getDefaultValue() != null ? m.getDefaultValue() : "-",
                    m.getDescription()
                ));
            });

        return sb.toString();
    }
}
```

### 6.3 使用示例

```java
// 创建注册表
PropertyMetadataRegistry registry = new PropertyMetadataRegistry();

// 注册元数据
registry.register(new PropertyMetadata(
    "server.port",
    Integer.class,
    8080,
    "服务器端口号",
    false,
    "8080"
));

registry.register(new PropertyMetadata(
    "database.url",
    String.class,
    null,
    "数据库连接URL",
    true,
    "jdbc:mysql://localhost:3306/mydb"
));

// 生成文档
String documentation = registry.generateDocumentation();
System.out.println(documentation);
```

## 7. 总结

### 7.1 扩展功能列表

| 功能 | 核心类 | 用途 |
|------|--------|------|
| 配置文件加载 | ConfigFilePropertySource, ConfigFileLoader | 从文件加载配置 |
| 加密属性源 | EncryptablePropertySource, EncryptionUtils | 敏感信息加密 |
| 动态配置刷新 | RefreshablePropertySource, EnvironmentRefreshEvent | 运行时刷新配置 |
| 配置验证 | PropertyValidator, ValidationResult | 配置校验 |
| 配置元数据 | PropertyMetadata, PropertyMetadataRegistry | 配置文档化 |

### 7.2 集成建议

1. **配置文件 + 加密**: 配置文件中的敏感信息使用 ENC() 包裹
2. **动态刷新 + 事件**: 配置刷新时触发事件，通知相关组件
3. **验证 + 元数据**: 基于元数据进行配置验证
4. **元数据 + 文档**: 自动生成配置文档

### 7.3 注意事项

1. **性能**: 动态刷新注意性能影响
2. **安全**: 加密密钥安全管理
3. **兼容性**: 扩展功能与现有代码兼容
4. **文档**: 及时更新扩展功能文档
