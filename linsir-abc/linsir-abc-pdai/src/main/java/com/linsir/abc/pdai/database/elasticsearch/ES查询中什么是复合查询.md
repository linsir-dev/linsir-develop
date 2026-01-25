# ES查询中什么是复合查询？有哪些复合查询方式？

在ElasticSearch中，复合查询（Compound Queries）是一种特殊类型的查询，它可以组合多个查询条件，形成更复杂的查询逻辑。复合查询允许你将多个简单查询组合成一个更强大的查询，以满足复杂的搜索需求。本文将详细介绍复合查询的概念、类型和使用方法。

## 什么是复合查询？

复合查询是指由多个子查询组成的查询，它可以：

1. **组合多个查询条件**：将多个简单查询组合成一个复杂查询
2. **控制查询逻辑**：使用逻辑运算符（如AND、OR、NOT）控制查询条件的组合方式
3. **调整相关性得分**：通过权重、函数等方式调整文档的相关性得分
4. **提高查询灵活性**：支持更复杂的查询场景，如条件嵌套、多条件组合等

复合查询是ElasticSearch中最强大的查询类型之一，它允许你构建几乎任何复杂的查询逻辑，以满足各种搜索需求。

## 复合查询的类型

ElasticSearch提供了多种类型的复合查询，每种类型都有其特定的用途和语法。以下是常见的复合查询类型：

### 1. bool查询

`bool`查询是最常用的复合查询类型，它允许你使用逻辑运算符组合多个查询条件。

#### 语法和参数

```json
{
  "bool": {
    "must": [],        // 必须满足的条件（AND）
    "should": [],      // 可选的条件（OR）
    "must_not": [],    // 必须不满足的条件（NOT）
    "filter": [],      // 过滤条件（不影响得分）
    "minimum_should_match": 1  // 至少需要满足的should条件数量
  }
}
```

#### 子句说明

- **must**：文档必须满足所有must子句中的条件，相当于逻辑AND操作。会影响相关性得分。
- **should**：文档可以满足should子句中的条件，相当于逻辑OR操作。会影响相关性得分。
- **must_not**：文档必须不满足must_not子句中的条件，相当于逻辑NOT操作。不影响相关性得分。
- **filter**：文档必须满足filter子句中的条件，但不影响相关性得分。结果会被缓存，提高性能。
- **minimum_should_match**：指定至少需要满足的should子句数量，默认为1（当没有must子句时）。

#### 示例

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "ElasticSearch" } }
      ],
      "should": [
        { "match": { "content": "tutorial" } },
        { "match": { "content": "guide" } }
      ],
      "filter": [
        { "range": { "published_date": { "gte": "2023-01-01" } } }
      ],
      "must_not": [
        { "term": { "status": "draft" } }
      ]
    }
  }
}
```

### 2. dis_max查询

`dis_max`查询（Disjunction Max Query）返回匹配任何查询的文档，但只使用得分最高的查询的得分作为文档的总得分。

#### 语法和参数

```json
{
  "dis_max": {
    "queries": [],     // 子查询列表
    "tie_breaker": 0.7  // 平局breaker，范围0-1
  }
}
```

#### 参数说明

- **queries**：子查询列表，文档需要匹配其中任何一个查询。
- **tie_breaker**：平局breaker，用于调整多个查询都匹配时的得分计算。值越大，其他查询的得分对总得分的影响越大。

#### 示例

```json
{
  "query": {
    "dis_max": {
      "queries": [
        { "match": { "title": "ElasticSearch tutorial" } },
        { "match": { "content": "ElasticSearch tutorial" } }
      ],
      "tie_breaker": 0.7
    }
  }
}
```

### 3. function_score查询

`function_score`查询允许你使用函数修改查询的相关性得分，以实现更复杂的得分计算逻辑。

#### 语法和参数

```json
{
  "function_score": {
    "query": {},       // 基础查询
    "functions": [],   // 得分函数列表
    "score_mode": "sum",  // 得分模式：sum, avg, max, min, first, multiply
    "boost_mode": "sum",   // 增强模式：multiply, sum, avg, max, min, replace
    "max_boost": 10      // 最大增强值
  }
}
```

#### 函数类型

- **weight**：为匹配的文档分配一个固定的权重。
- **field_value_factor**：根据文档中某个字段的值调整得分。
- **random_score**：为文档分配一个随机得分。
- **decay_functions**：基于字段值的衰减函数，如gauss、linear、exp。
- **script_score**：使用脚本计算得分。

#### 示例

```json
{
  "query": {
    "function_score": {
      "query": { "match": { "title": "ElasticSearch" } },
      "functions": [
        {
          "gauss": {
            "published_date": {
              "origin": "2023-01-01",
              "scale": "30d",
              "decay": 0.5
            }
          }
        },
        {
          "field_value_factor": {
            "field": "views",
            "factor": 0.1,
            "modifier": "log1p"
          }
        }
      ],
      "score_mode": "sum",
      "boost_mode": "sum"
    }
  }
}
```

### 4. boosting查询

`boosting`查询允许你降低某些文档的得分，以减少它们在搜索结果中的排名。

#### 语法和参数

```json
{
  "boosting": {
    "positive": {},    // 正面查询，匹配的文档会被返回
    "negative": {},    // 负面查询，匹配的文档会被降低得分
    "negative_boost": 0.5  // 负面增强因子，范围0-1
  }
}
```

#### 参数说明

- **positive**：正面查询，匹配的文档会被返回。
- **negative**：负面查询，匹配的文档会被降低得分。
- **negative_boost**：负面增强因子，值越小，负面查询对得分的降低效果越明显。

#### 示例

```json
{
  "query": {
    "boosting": {
      "positive": { "match": { "title": "ElasticSearch" } },
      "negative": { "match": { "content": "deprecated" } },
      "negative_boost": 0.5
    }
  }
}
```

### 5. constant_score查询

`constant_score`查询为所有匹配的文档分配相同的得分，忽略原始的相关性得分。

#### 语法和参数

```json
{
  "constant_score": {
    "filter": {},      // 过滤条件
    "boost": 1.2       // 增强因子
  }
}
```

#### 参数说明

- **filter**：过滤条件，文档必须满足这些条件。
- **boost**：增强因子，为匹配的文档分配一个固定的得分。

#### 示例

```json
{
  "query": {
    "constant_score": {
      "filter": {
        "term": { "status": "active" }
      },
      "boost": 1.2
    }
  }
}
```

### 6. 其他复合查询类型

除了上述常见的复合查询类型外，ElasticSearch还提供了其他一些复合查询类型，如：

- **indices查询**：根据文档所在的索引选择不同的查询
- **type查询**：根据文档的类型选择不同的查询（在ElasticSearch 7.0+中已弃用）
- **wrapper查询**：包装其他查询，用于传递原始JSON查询

## 复合查询的最佳实践

### 1. 合理使用bool查询

- **将必须满足的条件放在must子句中**：如精确匹配、范围过滤等
- **将可选的条件放在should子句中**：如提高相关性的额外匹配条件
- **将不需要得分的条件放在filter子句中**：如状态、类别等过滤条件，以提高性能
- **合理设置minimum_should_match参数**：根据实际需求设置至少需要满足的should子句数量

### 2. 优化查询性能

- **使用filter子句**：对于不需要计算得分的条件，使用filter子句而不是must子句
- **避免过深的嵌套**：过深的查询嵌套可能会影响性能
- **合理使用缓存**：利用ElasticSearch的查询缓存和过滤器缓存
- **监控查询性能**：定期监控查询性能，及时发现和解决性能问题

### 3. 调整相关性得分

- **使用function_score查询**：当需要复杂的得分计算逻辑时
- **使用boost参数**：为重要的查询条件设置更高的权重
- **使用tie_breaker参数**：在dis_max查询中调整多个查询的得分影响
- **测试不同的得分策略**：根据实际需求测试不同的得分策略，选择最佳方案

### 4. 提高查询可读性

- **使用有意义的查询结构**：组织查询结构，提高可读性
- **添加注释**：对于复杂的查询，添加注释说明查询逻辑
- **分解复杂查询**：将复杂查询分解为多个简单查询，提高可维护性

### 5. 测试和验证

- **测试查询结果**：确保查询返回预期的结果
- **验证查询性能**：确保查询在大型数据集上的性能可接受
- **测试边界情况**：测试边界情况，如空结果、大量结果等

## 复合查询的使用场景

### 1. 多条件搜索

当需要根据多个条件搜索文档时，如同时搜索标题、内容、作者等字段。

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "ElasticSearch" } },
        { "match": { "content": "tutorial" } }
      ],
      "filter": [
        { "term": { "author": "John" } },
        { "range": { "published_date": { "gte": "2023-01-01" } } }
      ]
    }
  }
}
```

### 2. 条件过滤

当需要根据特定条件过滤文档时，如过滤特定状态、类别、日期范围等。

```json
{
  "query": {
    "bool": {
      "filter": [
        { "term": { "status": "active" } },
        { "range": { "age": { "gte": 18, "lte": 30 } } },
        { "terms": { "category": ["technology", "science"] } }
      ]
    }
  }
}
```

### 3. 相关性排序

当需要根据多个因素调整文档的相关性得分时，如结合内容匹配、时间衰减、用户行为等。

```json
{
  "query": {
    "function_score": {
      "query": { "match": { "content": "ElasticSearch" } },
      "functions": [
        {
          "gauss": {
            "published_date": {
              "origin": "2023-01-01",
              "scale": "30d",
              "decay": 0.5
            }
          }
        },
        {
          "field_value_factor": {
            "field": "views",
            "modifier": "log1p"
          }
        }
      ]
    }
  }
}
```

### 4. 负面过滤

当需要排除某些文档或降低它们的排名时，如排除过期内容、低质量内容等。

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "ElasticSearch" } }
      ],
      "must_not": [
        { "term": { "status": "draft" } },
        { "match": { "content": "spam" } }
      ]
    }
  }
}
```

### 5. 跨字段搜索

当需要在多个字段中搜索相同的查询文本时，如同时搜索标题和内容。

```json
{
  "query": {
    "dis_max": {
      "queries": [
        { "match": { "title": "ElasticSearch tutorial" } },
        { "match": { "content": "ElasticSearch tutorial" } }
      ],
      "tie_breaker": 0.7
    }
  }
}
```

## 总结

复合查询是ElasticSearch中最强大的查询类型之一，它允许你构建几乎任何复杂的查询逻辑，以满足各种搜索需求。通过合理使用不同类型的复合查询，你可以：

1. **组合多个查询条件**：使用bool查询组合多个简单查询
2. **调整相关性得分**：使用function_score查询实现复杂的得分计算
3. **提高查询性能**：使用filter子句和缓存机制
4. **满足复杂的搜索需求**：支持各种复杂的搜索场景

在实际应用中，你应该根据具体的搜索需求选择合适的复合查询类型，并结合最佳实践进行优化，以获得最佳的搜索结果和性能。

## 常见问题

### 1. bool查询中，当同时有must和should子句时，should子句是必需的吗？

不是。当bool查询中同时有must和should子句时，should子句是可选的，即使没有满足任何should子句，文档也会被匹配。should子句的作用是提高匹配文档的相关性得分。

### 2. 如何在复合查询中使用脚本？

你可以在function_score查询的script_score函数中使用脚本，以实现更复杂的得分计算逻辑。例如：

```json
{
  "function_score": {
    "query": { "match": { "title": "ElasticSearch" } },
    "functions": [
      {
        "script_score": {
          "script": {
            "source": "Math.log(1 + doc['views'].value)"
          }
        }
      }
    ]
  }
}
```

### 3. 复合查询的性能如何优化？

优化复合查询性能的方法包括：
- 使用filter子句处理不需要得分的条件
- 合理设置缓存策略
- 避免过深的查询嵌套
- 监控和分析查询性能
- 使用适当的查询类型和参数

### 4. 如何调试复合查询？

调试复合查询的方法包括：
- 使用explain参数查看查询的执行计划和得分计算
- 分解复杂查询，逐步测试每个部分
- 使用Profile API分析查询性能
- 检查查询结果是否符合预期

### 5. 复合查询可以嵌套使用吗？

是的，复合查询可以嵌套使用，以构建更复杂的查询逻辑。例如：

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "ElasticSearch" } },
        {
          "bool": {
            "should": [
              { "match": { "content": "tutorial" } },
              { "match": { "content": "guide" } }
            ]
          }
        }
      ]
    }
  }
}
```