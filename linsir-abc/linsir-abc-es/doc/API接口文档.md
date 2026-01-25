# API接口文档

## 1. 创建文章
- **HTTP方法**: POST
- **路径**: /api/articles/create
- **功能描述**: 创建新的文章
- **请求体**:
  ```json
  {
    "title": "文章标题",
    "content": "文章内容",
    "author": "作者",
    "publishDate": "2026-01-25T12:00:00",
    "views": 100
  }
  ```
- **响应**:
  - 成功: 201 Created，返回创建的文章对象
  - 失败: 400 Bad Request

## 2. 根据ID查询文章
- **HTTP方法**: GET
- **路径**: /api/articles/{id}
- **功能描述**: 根据文章ID查询文章详情
- **路径参数**:
  - id: 文章ID
- **响应**:
  - 成功: 200 OK，返回文章对象
  - 失败: 404 Not Found

## 3. 查询所有文章
- **HTTP方法**: GET
- **路径**: /api/articles/all
- **功能描述**: 查询所有文章
- **响应**:
  - 成功: 200 OK，返回文章列表

## 4. 根据标题查询文章
- **HTTP方法**: GET
- **路径**: /api/articles/title/{title}
- **功能描述**: 根据标题查询文章
- **路径参数**:
  - title: 文章标题
- **响应**:
  - 成功: 200 OK，返回文章列表

## 5. 根据作者查询文章
- **HTTP方法**: GET
- **路径**: /api/articles/author/{author}
- **功能描述**: 根据作者查询文章
- **路径参数**:
  - author: 作者名称
- **响应**:
  - 成功: 200 OK，返回文章列表

## 6. 根据浏览量范围查询文章
- **HTTP方法**: GET
- **路径**: /api/articles/views-range
- **功能描述**: 根据浏览量范围查询文章
- **查询参数**:
  - minViews: 最小浏览量
  - maxViews: 最大浏览量
- **响应**:
  - 成功: 200 OK，返回文章列表

## 7. 全文搜索文章
- **HTTP方法**: GET
- **路径**: /api/articles/search
- **功能描述**: 根据关键词全文搜索文章
- **查询参数**:
  - keyword: 搜索关键词
- **响应**:
  - 成功: 200 OK，返回文章列表

## 8. 更新文章
- **HTTP方法**: PUT
- **路径**: /api/articles/{id}
- **功能描述**: 更新指定ID的文章
- **路径参数**:
  - id: 文章ID
- **请求体**:
  ```json
  {
    "title": "更新后的文章标题",
    "content": "更新后的文章内容",
    "author": "作者",
    "publishDate": "2026-01-25T12:00:00",
    "views": 200
  }
  ```
- **响应**:
  - 成功: 200 OK，返回更新后的文章对象
  - 失败: 404 Not Found

## 9. 删除文章
- **HTTP方法**: DELETE
- **路径**: /api/articles/{id}
- **功能描述**: 删除指定ID的文章
- **路径参数**:
  - id: 文章ID
- **响应**:
  - 成功: 204 No Content
  - 失败: 404 Not Found
