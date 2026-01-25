# Spring Boot + Elasticsearch 文章管理系统

## 项目介绍

这是一个基于 Spring Boot 和 Elasticsearch 构建的文章管理系统，提供了完整的文章 CRUD 操作和搜索功能。系统采用 RESTful API 设计风格，支持文章的创建、查询、更新以及多种条件的搜索功能。

### 核心功能

- ✅ 创建文章
- ✅ 根据 ID 查询文章
- ✅ 查询所有文章
- ✅ 根据标题查询文章
- ✅ 根据作者查询文章
- ✅ 根据浏览量范围查询文章
- ✅ 全文搜索文章
- ✅ 更新文章

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.2.0 | 应用框架 |
| Spring Data Elasticsearch | 5.2.0 | Elasticsearch 数据访问 |
| Elasticsearch | 8.11.1 | 搜索引擎 |
| Lombok | 1.18.30 | 代码简化工具 |
| Java | 17+ | 开发语言 |
| Maven | 3.8+ | 项目构建工具 |

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven 3.8 或更高版本
- Elasticsearch 8.11.1 或兼容版本

### 配置说明

1. **Elasticsearch 配置**

   在 `src/main/resources/application-dev.yaml` 文件中配置 Elasticsearch 连接信息：

   ```yaml
   spring:
     elasticsearch:
       uris: http://localhost:9200
       # 如果需要认证，添加以下配置
       # username: your-username
       # password: your-password
   ```

### 启动步骤

1. **编译项目**

   ```bash
   # Windows PowerShell
   mvn clean compile

   # Linux/Mac
   ./mvnw clean compile
   ```

2. **运行应用**

   ```bash
   # Windows PowerShell
   mvn spring-boot:run

   # Linux/Mac
   ./mvnw spring-boot:run
   ```

   应用将在 `http://localhost:6084` 启动。

## API 接口文档

### 1. 创建文章

- **URL**: `/api/articles/create`
- **方法**: `POST`
- **请求体**:
  ```json
  {
    "title": "测试文章",
    "content": "测试内容",
    "author": "linsir",
    "publishDate": "2026-01-25",
    "views": 1000
  }
  ```
- **响应**:
  ```json
  {
    "id": "文章ID",
    "title": "测试文章",
    "content": "测试内容",
    "author": "linsir",
    "publishDate": "2026-01-25",
    "views": 1000,
    "createdAt": "2026-01-25T23:39:17.3894289",
    "updatedAt": "2026-01-25T23:39:17.3894289"
  }
  ```

### 2. 根据 ID 查询文章

- **URL**: `/api/articles/{id}`
- **方法**: `GET`
- **响应**:
  ```json
  {
    "id": "文章ID",
    "title": "测试文章",
    "content": "测试内容",
    "author": "linsir",
    "publishDate": "2026-01-25",
    "views": 1000,
    "createdAt": "2026-01-25T23:39:17.3894289",
    "updatedAt": "2026-01-25T23:39:17.3894289"
  }
  ```

### 3. 查询所有文章

- **URL**: `/api/articles/all`
- **方法**: `GET`
- **响应**:
  ```json
  [
    {
      "id": "文章ID",
      "title": "测试文章",
      "content": "测试内容",
      "author": "linsir",
      "publishDate": "2026-01-25",
      "views": 1000,
      "createdAt": "2026-01-25T23:39:17.3894289",
      "updatedAt": "2026-01-25T23:39:17.3894289"
    }
  ]
  ```

### 4. 根据标题查询文章

- **URL**: `/api/articles/title/{title}`
- **方法**: `GET`
- **响应**:
  ```json
  [
    {
      "id": "文章ID",
      "title": "测试文章",
      "content": "测试内容",
      "author": "linsir",
      "publishDate": "2026-01-25",
      "views": 1000,
      "createdAt": "2026-01-25T23:39:17.3894289",
      "updatedAt": "2026-01-25T23:39:17.3894289"
    }
  ]
  ```

### 5. 根据作者查询文章

- **URL**: `/api/articles/author/{author}`
- **方法**: `GET`
- **响应**:
  ```json
  [
    {
      "id": "文章ID",
      "title": "测试文章",
      "content": "测试内容",
      "author": "linsir",
      "publishDate": "2026-01-25",
      "views": 1000,
      "createdAt": "2026-01-25T23:39:17.3894289",
      "updatedAt": "2026-01-25T23:39:17.3894289"
    }
  ]
  ```

### 6. 根据浏览量范围查询文章

- **URL**: `/api/articles/views-range?minViews=500&maxViews=1500`
- **方法**: `GET`
- **响应**:
  ```json
  [
    {
      "id": "文章ID",
      "title": "测试文章",
      "content": "测试内容",
      "author": "linsir",
      "publishDate": "2026-01-25",
      "views": 1000,
      "createdAt": "2026-01-25T23:39:17.3894289",
      "updatedAt": "2026-01-25T23:39:17.3894289"
    }
  ]
  ```

### 7. 全文搜索文章

- **URL**: `/api/articles/search?keyword=测试`
- **方法**: `GET`
- **响应**:
  ```json
  [
    {
      "id": "文章ID",
      "title": "测试文章",
      "content": "测试内容",
      "author": "linsir",
      "publishDate": "2026-01-25",
      "views": 1000,
      "createdAt": "2026-01-25T23:39:17.3894289",
      "updatedAt": "2026-01-25T23:39:17.3894289"
    }
  ]
  ```

### 8. 更新文章

- **URL**: `/api/articles/{id}`
- **方法**: `PUT`
- **请求体**:
  ```json
  {
    "title": "更新后的文章标题",
    "content": "更新后的文章内容",
    "views": 1500
  }
  ```
- **响应**:
  ```json
  {
    "id": "文章ID",
    "title": "更新后的文章标题",
    "content": "更新后的文章内容",
    "author": "linsir",
    "publishDate": "2026-01-25",
    "views": 1500,
    "createdAt": "2026-01-25T23:39:17.3894289",
    "updatedAt": "2026-01-25T23:45:00.0000000"
  }
  ```

## 项目结构

```
linsir-abc-es/
├── doc/                   # 文档目录
│   ├── API接口文档.md       # API接口详细文档
│   └── postman-import.json # Postman测试集合
├── src/
│   ├── main/
│   │   ├── java/com/linsir/es/
│   │   │   ├── controller/   # 控制器层
│   │   │   │   └── ArticleController.java
│   │   │   ├── entity/       # 实体层
│   │   │   │   └── Article.java
│   │   │   ├── repository/   # 数据访问层
│   │   │   │   └── ArticleRepository.java
│   │   │   ├── service/      # 服务层
│   │   │   │   ├── impl/     # 服务实现
│   │   │   │   │   └── ArticleServiceImpl.java
│   │   │   │   └── ArticleService.java
│   │   │   └── ElasticSearchApplication.java # 应用启动类
│   │   └── resources/        # 资源文件
│   │       ├── application-dev.yaml # 开发环境配置
│   │       └── application.yaml     # 主配置文件
│   └── test/                # 测试目录
└── pom.xml                  # Maven配置文件
```

## 核心类说明

### 1. Article 实体类

```java
@Data
@Document(indexName = "articles")
public class Article {
    @Id
    private String id;
    
    @Field(type = FieldType.Text)
    private String title;
    
    @Field(type = FieldType.Text)
    private String content;
    
    @Field(type = FieldType.Keyword)
    private String author;
    
    @Field(type = FieldType.Keyword)
    private String publishDate;
    
    @Field(type = FieldType.Integer)
    private Integer views;
    
    @Field(type = FieldType.Keyword)
    private String createdAt;
    
    @Field(type = FieldType.Keyword)
    private String updatedAt;
}
```

### 2. ArticleController 控制器类

提供 RESTful API 接口，处理 HTTP 请求，调用服务层方法完成业务逻辑。

### 3. ArticleServiceImpl 服务实现类

实现业务逻辑，包括文章的创建、查询、更新等操作，处理日期字段的设置。

### 4. ArticleRepository 数据访问接口

继承自 Spring Data Elasticsearch 的 Repository 接口，提供 Elasticsearch 数据访问方法。

## 日期处理说明

系统使用 ISO 标准格式的日期字符串处理日期字段：

- **publishDate**: 发布日期，格式为 `yyyy-MM-dd`
- **createdAt**: 创建时间，格式为 `yyyy-MM-dd'T'HH:mm:ss.SSSSSSS`
- **updatedAt**: 更新时间，格式为 `yyyy-MM-dd'T'HH:mm:ss.SSSSSSS`

## 常见问题

### 1. Elasticsearch 连接失败

- 检查 Elasticsearch 服务是否启动
- 检查 `application-dev.yaml` 中的连接配置是否正确
- 检查网络连接和防火墙设置

### 2. 索引创建失败

- 确保 Elasticsearch 有足够的权限创建索引
- 检查索引名称是否合法
- 尝试删除旧索引后重新创建

### 3. 日期格式错误

- 确保日期字符串符合 ISO 标准格式
- 检查服务层日期处理逻辑

## 测试

### 使用 Postman 测试

1. 导入 `doc/postman-import.json` 文件到 Postman
2. 运行集合中的测试用例

### 使用 PowerShell 测试

```powershell
# 创建文章
Invoke-WebRequest -Uri "http://localhost:6084/api/articles/create" -Method POST -ContentType "application/json" -Body '{"title": "测试文章", "content": "测试内容", "author": "linsir", "publishDate": "2026-01-25", "views": 1000}'

# 获取文章
Invoke-WebRequest -Uri "http://localhost:6084/api/articles/{id}" -Method GET

# 查询所有文章
Invoke-WebRequest -Uri "http://localhost:6084/api/articles/all" -Method GET
```

## 部署

### 构建可执行 JAR

```bash
mvn clean package
```

构建完成后，可执行 JAR 文件将生成在 `target` 目录中。

### 运行可执行 JAR

```bash
java -jar target/linsir-abc-es-1.0.0.jar
```

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进这个项目！

## 联系方式

- 作者：linsir
- 邮箱：linsir@example.com
- 项目地址：https://github.com/linsir/linsir-abc-es
