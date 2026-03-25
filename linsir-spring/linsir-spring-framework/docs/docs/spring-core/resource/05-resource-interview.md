# Spring 资源抽象 - 面试题汇总

## 一、Resource接口

### Q1: Spring的Resource接口是什么？它解决了什么问题？

**答案：**

Resource是Spring提供的资源抽象接口，位于`org.springframework.core.io`包下。它统一了对各种底层资源（文件、类路径、URL等）的访问方式。

**解决的问题：**

| 痛点 | 原生Java | Spring Resource |
|------|----------|----------------|
| 资源类型多样 | URL、File、InputStream各自为政 | 统一Resource接口 |
| 路径解析复杂 | 不同资源类型路径格式不同 | 统一路径解析策略 |
| 资源加载繁琐 | 需要手动处理各类资源 | ResourceLoader自动加载 |
| 缺乏通配符支持 | 无法批量加载资源 | Ant风格通配符支持 |

**核心方法：**
```java
public interface Resource extends InputStreamSource {
    boolean exists();           // 资源是否存在
    boolean isReadable();       // 资源是否可读
    InputStream getInputStream() throws IOException;  // 获取输入流
    File getFile() throws IOException;                // 获取文件
    String getFilename();       // 获取文件名
    String getDescription();    // 获取资源描述
}
```

---

### Q2: ClassPathResource和FileSystemResource有什么区别？

**答案：**

| 特性 | ClassPathResource | FileSystemResource |
|------|-------------------|-------------------|
| 资源位置 | 类路径（classpath） | 文件系统 |
| 路径格式 | `config/application.properties` | `/path/to/file.txt` 或 `file:/path/to/file.txt` |
| 打包后 | 可读取（在jar包内） | 无法读取（需要解压） |
| 适用场景 | 配置文件、静态资源 | 外部配置文件、日志文件 |
| 创建方式 | `new ClassPathResource("config.xml")` | `new FileSystemResource("/path/file.txt")` |

**代码示例：**
```java
// ClassPathResource - 从类路径加载
Resource classpathResource = new ClassPathResource("application.properties");
// 查找顺序：1. 当前线程类加载器 2. 当前类类加载器 3. 系统类加载器

// FileSystemResource - 从文件系统加载
Resource fileResource = new FileSystemResource("/etc/config/app.properties");
// 支持绝对路径和相对路径
```

**面试要点：**
- ClassPathResource适合读取打包在jar内的资源
- FileSystemResource适合读取外部配置文件
- Spring Boot的application.properties默认使用ClassPathResource加载

---

### Q3: Spring中有哪些常用的Resource实现类？

**答案：**

| 实现类 | 说明 | 使用场景 |
|--------|------|---------|
| `ClassPathResource` | 类路径资源 | 加载配置文件、静态资源 |
| `FileSystemResource` | 文件系统资源 | 加载外部配置文件 |
| `UrlResource` | URL资源 | 加载HTTP/FTP资源 |
| `ServletContextResource` | Web应用资源 | 加载WEB-INF下的资源 |
| `ByteArrayResource` | 字节数组资源 | 内存中的资源 |
| `InputStreamResource` | 输入流资源 | 包装InputStream |

**代码示例：**
```java
// 1. 类路径资源
Resource classpathResource = new ClassPathResource("config.xml");

// 2. 文件系统资源
Resource fileResource = new FileSystemResource("/path/to/file.txt");

// 3. URL资源
Resource urlResource = new UrlResource("https://example.com/config.json");

// 4. Web应用资源（需要在Servlet环境中）
Resource servletResource = new ServletContextResource(servletContext, "/WEB-INF/web.xml");

// 5. 字节数组资源
byte[] data = "Hello World".getBytes();
Resource byteArrayResource = new ByteArrayResource(data);
```

---

### Q4: Resource接口的getFile()方法有什么限制？

**答案：**

**限制：**
1. **不是所有Resource都支持**：如UrlResource（HTTP资源）可能不支持
2. **打包后无法使用**：ClassPathResource在jar包内时，getFile()会抛出异常
3. **需要资源是文件**：某些资源（如ByteArrayResource）不是文件

**解决方案：**
```java
Resource resource = new ClassPathResource("application.properties");

// 方式1：使用getFile()（可能抛出异常）
try {
    File file = resource.getFile();
} catch (IOException e) {
    // 资源不在文件系统中
}

// 方式2：使用getInputStream()（通用方案）
try (InputStream is = resource.getInputStream()) {
    // 读取资源内容
    String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
}

// 方式3：检查isFile()后再使用
if (resource.isFile()) {
    File file = resource.getFile();
} else {
    // 使用InputStream
}
```

**最佳实践：**
- 优先使用`getInputStream()`，通用性更好
- 如果确定资源是文件，可以使用`getFile()`
- 对于类路径资源，打包后使用`getInputStream()`

---

## 二、ResourceLoader

### Q5: 什么是ResourceLoader？它如何根据路径加载资源？

**答案：**

ResourceLoader是Spring的资源加载器接口，定义了根据资源位置字符串加载Resource的方法。

**核心方法：**
```java
public interface ResourceLoader {
    String CLASSPATH_URL_PREFIX = "classpath:";
    
    Resource getResource(String location);
    ClassLoader getClassLoader();
}
```

**加载策略（DefaultResourceLoader）：**

| 路径前缀 | 加载方式 | 示例 |
|---------|---------|------|
| `classpath:` | ClassPathResource | `classpath:config.xml` |
| `file:` | UrlResource | `file:/path/to/file.txt` |
| `http://`/`https://` | UrlResource | `https://example.com/config.json` |
| `/` | ClassPathResource（绝对路径） | `/config.xml` |
| 无前缀 | ClassPathResource（相对路径） | `config.xml` |

**代码示例：**
```java
ResourceLoader loader = new DefaultResourceLoader();

// 1. 加载类路径资源
Resource r1 = loader.getResource("classpath:application.properties");

// 2. 加载文件系统资源
Resource r2 = loader.getResource("file:/etc/config/app.properties");

// 3. 加载URL资源
Resource r3 = loader.getResource("https://example.com/config.json");

// 4. 简写形式（默认作为类路径资源）
Resource r4 = loader.getResource("config.properties");
```

---

### Q6: ApplicationContext和ResourceLoader有什么关系？

**答案：**

ApplicationContext继承了ResourcePatternResolver接口，而ResourcePatternResolver继承了ResourceLoader接口，因此ApplicationContext具有资源加载能力。

**继承关系：**
```
ApplicationContext
    ↓ extends
ResourcePatternResolver
    ↓ extends
ResourceLoader
```

**代码示例：**
```java
@Autowired
private ApplicationContext context;

public void loadResource() {
    // ApplicationContext可以直接加载资源
    Resource resource = context.getResource("classpath:config.xml");
    
    // 也可以使用通配符批量加载
    Resource[] resources = context.getResources("classpath*:/**/*.xml");
}
```

**BeanFactory vs ApplicationContext：**
- BeanFactory：不继承ResourceLoader，无法直接加载资源
- ApplicationContext：继承ResourcePatternResolver，支持资源加载和通配符

---

## 三、资源通配符加载

### Q7: Spring支持哪些Ant风格的路径通配符？

**答案：**

Spring的PathMatchingResourcePatternResolver支持Ant风格的路径通配符：

| 通配符 | 说明 | 示例 |
|--------|------|------|
| `?` | 匹配单个字符 | `file?.txt` 匹配 `file1.txt`、`fileA.txt` |
| `*` | 匹配零个或多个字符（不含目录分隔符） | `*.xml` 匹配所有XML文件 |
| `**` | 匹配任意层级目录 | `/**/*.xml` 匹配所有目录下的XML文件 |

**代码示例：**
```java
PathMatchingResourcePatternResolver resolver = 
    new PathMatchingResourcePatternResolver();

// 1. 匹配单个字符
Resource[] resources1 = resolver.getResources("classpath:file?.txt");

// 2. 匹配所有XML文件
Resource[] resources2 = resolver.getResources("classpath:/*.xml");

// 3. 匹配任意层级目录下的XML文件
Resource[] resources3 = resolver.getResources("classpath*:/**/*.xml");

// 4. 匹配特定包下的所有类
Resource[] resources4 = resolver.getResources("classpath:com/example/**/*.class");

// 5. 匹配配置文件
Resource[] resources5 = resolver.getResources("classpath*:config-*.properties");
```

**注意事项：**
- `classpath:` 只加载当前类路径的资源
- `classpath*:` 加载所有类路径（包括jar包）的资源
- `**` 只能用于路径中，不能用于文件名

---

### Q8: `classpath:` 和 `classpath*:` 有什么区别？

**答案：**

| 前缀 | 说明 | 使用场景 |
|------|------|---------|
| `classpath:` | 只加载第一个匹配的资源 | 加载唯一配置文件 |
| `classpath*:` | 加载所有匹配的资源（包括jar包） | 批量加载资源、扫描Mapper文件 |

**代码示例：**
```java
PathMatchingResourcePatternResolver resolver = 
    new PathMatchingResourcePatternResolver();

// 1. classpath: 只加载第一个匹配的资源
Resource resource = resolver.getResource("classpath:application.properties");

// 2. classpath*: 加载所有匹配的资源
Resource[] resources = resolver.getResources("classpath*:/**/*.xml");
// 会从所有jar包的类路径中加载XML文件
```

**实际应用场景：**
```java
// MyBatis Mapper扫描（需要加载所有jar包中的Mapper文件）
@Bean
public SqlSessionFactory sqlSessionFactory() throws Exception {
    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(dataSource);
    
    // 使用classpath*:加载所有Mapper文件
    Resource[] mapperResources = resolver.getResources("classpath*:mapper/**/*.xml");
    factoryBean.setMapperLocations(mapperResources);
    
    return factoryBean.getObject();
}
```

---

## 四、实际应用

### Q9: Spring Boot是如何加载application.properties的？

**答案：**

Spring Boot通过ResourceLoader加载配置文件，优先级如下：

**加载顺序（从高到低）：**
1. 命令行参数
2. 来自`java:comp/env`的JNDI属性
3. Java系统属性（System.getProperties()）
4. 操作系统环境变量
5. `RandomValuePropertySource`（random.*）
6. **Jar包外部的application-{profile}.properties**
7. **Jar包内部的application-{profile}.properties**
8. **Jar包外部的application.properties**
9. **Jar包内部的application.properties**
10. @PropertySource注解定义的属性
11. 默认属性（SpringApplication.setDefaultProperties）

**源码分析：**
```java
// ConfigFileApplicationListener
private void load(PropertySourceLoader loader, String location, 
                  Profile profile, DocumentFilter filter) {
    // 1. 加载文件系统资源（外部配置文件）
    String filePath = location + ".properties";
    Resource resource = new FileSystemResource(filePath);
    if (resource.exists()) {
        loadIntoProperties(resource);
    }
    
    // 2. 加载类路径资源（内部配置文件）
    Resource classpathResource = new ClassPathResource(filePath);
    if (classpathResource.exists()) {
        loadIntoProperties(classpathResource);
    }
}
```

**自定义配置文件位置：**
```bash
# 命令行指定
java -jar app.jar --spring.config.location=/etc/config/

# 环境变量
export SPRING_CONFIG_LOCATION=file:/etc/config/
```

---

### Q10: 如何在Spring中实现多环境配置加载？

**答案：**

**方案1：使用Spring Profiles**
```java
@Configuration
public class MultiEnvConfig {
    
    @Bean
    @Profile("dev")
    public DataSource devDataSource() {
        // 开发环境数据源
    }
    
    @Bean
    @Profile("prod")
    public DataSource prodDataSource() {
        // 生产环境数据源
    }
}
```

**方案2：使用ResourcePatternResolver批量加载**
```java
@Configuration
public class MultiEnvConfig {
    
    @Bean
    public Properties multiEnvProperties() throws IOException {
        PathMatchingResourcePatternResolver resolver = 
            new PathMatchingResourcePatternResolver();
        
        // 加载所有环境的配置文件
        Resource[] resources = resolver.getResources(
            "classpath*:config/application-*.properties");
        
        Properties mergedProps = new Properties();
        
        for (Resource resource : resources) {
            System.out.println("加载配置: " + resource.getFilename());
            
            try (InputStream is = resource.getInputStream()) {
                Properties props = new Properties();
                props.load(is);
                mergedProps.putAll(props);
            }
        }
        
        return mergedProps;
    }
}
```

**方案3：使用@PropertySource**
```java
@Configuration
@PropertySource("classpath:application-common.properties")
@PropertySource("classpath:application-${spring.profiles.active}.properties")
public class AppConfig {
}
```

---

### Q11: 如何在Spring中加载外部配置文件？

**答案：**

**方案1：使用FileSystemResource**
```java
@Component
public class ExternalConfigLoader {
    
    public Properties loadExternalConfig(String filePath) throws IOException {
        Resource resource = new FileSystemResource(filePath);
        
        if (!resource.exists()) {
            throw new FileNotFoundException("配置文件不存在: " + filePath);
        }
        
        Properties props = new Properties();
        try (InputStream is = resource.getInputStream()) {
            props.load(is);
        }
        
        return props;
    }
}
```

**方案2：使用ResourceLoader加载**
```java
@Component
public class ConfigService {
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    public String loadConfig(String location) throws IOException {
        // 支持file:前缀
        Resource resource = resourceLoader.getResource("file:" + location);
        
        try (InputStream is = resource.getInputStream()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
```

**方案3：Spring Boot配置**
```yaml
# application.yml
spring:
  config:
    import: file:/etc/config/external.properties
```

---

## 五、高级话题

### Q12: 如何实现自定义的ResourceLoader？

**答案：**

可以通过实现ResourceLoader接口来自定义资源加载逻辑。

**示例：从数据库加载配置**
```java
public class DatabaseResourceLoader implements ResourceLoader {
    
    private final DataSource dataSource;
    
    public DatabaseResourceLoader(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    @Override
    public Resource getResource(String location) {
        // 自定义协议：db:config/name
        if (location.startsWith("db:")) {
            String configName = location.substring(3);
            return new DatabaseResource(configName, dataSource);
        }
        
        // 委托给默认加载器
        return new DefaultResourceLoader().getResource(location);
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return getClass().getClassLoader();
    }
}

// 自定义Resource实现
public class DatabaseResource extends AbstractResource {
    
    private final String configName;
    private final DataSource dataSource;
    
    @Override
    public InputStream getInputStream() throws IOException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                "SELECT content FROM config WHERE name = ?");
            ps.setString(1, configName);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String content = rs.getString("content");
                return new ByteArrayInputStream(content.getBytes());
            }
        } catch (SQLException e) {
            throw new IOException("从数据库加载配置失败", e);
        }
        
        throw new FileNotFoundException("配置不存在: " + configName);
    }
    
    @Override
    public String getDescription() {
        return "DatabaseResource[" + configName + "]";
    }
}
```

---

### Q13: Spring资源加载的性能如何优化？

**答案：**

**优化方案：**

**1. 缓存Resource对象**
```java
@Component
public class CachedResourceLoader {
    
    private final ResourceLoader delegate = new DefaultResourceLoader();
    private final Map<String, Resource> cache = new ConcurrentHashMap<>();
    
    public Resource getResource(String location) {
        return cache.computeIfAbsent(location, delegate::getResource);
    }
}
```

**2. 缓存资源内容**
```java
@Component
public class CachedResourceService {
    
    private final Map<String, String> contentCache = new ConcurrentHashMap<>();
    
    public String loadResourceContent(String location) throws IOException {
        return contentCache.computeIfAbsent(location, loc -> {
            try {
                Resource resource = resourceLoader.getResource(loc);
                try (InputStream is = resource.getInputStream()) {
                    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
```

**3. 避免频繁使用通配符**
```java
// 不好的做法：每次调用都扫描
public Resource[] findMappers() throws IOException {
    return resolver.getResources("classpath*:mapper/**/*.xml");
}

// 好的做法：启动时扫描一次，缓存结果
@PostConstruct
public void init() throws IOException {
    this.mapperResources = resolver.getResources("classpath*:mapper/**/*.xml");
}
```

---

## 六、面试技巧

### 答题模板

**问题：Spring的Resource和ResourceLoader有什么区别？**

**推荐回答结构：**

1. **先说明概念**：Resource是资源抽象接口，ResourceLoader是资源加载器接口
2. **说明关系**：ResourceLoader负责根据路径字符串创建Resource对象
3. **列举实现**：Resource有ClassPathResource、FileSystemResource等实现
4. **说明加载策略**：ResourceLoader根据路径前缀选择不同的Resource实现
5. **举例说明**：展示如何使用ResourceLoader加载不同类型的资源

**示例回答：**
> "Resource是Spring对底层资源的抽象接口，定义了访问资源的统一方式，比如获取输入流、判断资源是否存在等。ResourceLoader则是资源加载器接口，负责根据路径字符串创建对应的Resource对象。
>
> Resource有多个实现类：ClassPathResource用于加载类路径资源，FileSystemResource用于加载文件系统资源，UrlResource用于加载URL资源等。
>
> ResourceLoader的默认实现DefaultResourceLoader会根据路径前缀选择不同的Resource实现：以classpath:开头的使用ClassPathResource，以file:开头的使用UrlResource，其他默认使用ClassPathResource。
>
> 例如，使用ResourceLoader可以统一加载类路径配置文件、外部文件或远程URL资源，而不需要关心具体的资源类型。"

---

## 七、相关文档

- [资源抽象概述](./00-resource-overview.md)
- [资源抽象代码说明](./01-resource-code-guide.md)
- [资源抽象测试说明](./02-resource-test-guide.md)
- [资源抽象测试报告](./03-resource-test-report.md)
- [资源抽象扩展设计](./04-resource-extension-design.md)
