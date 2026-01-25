# ES查询和聚合的有哪些方式？

## 一、ES查询方式

ElasticSearch提供了丰富的查询方式，满足不同场景的搜索需求。这些查询方式可以分为以下几类：

### 1. 全文搜索查询

全文搜索查询用于对文本字段进行搜索，会对查询文本进行分词处理。

#### 1.1 match查询

最基本的全文搜索查询，用于匹配一个字段中的文本。

```json
{
  "query": {
    "match": {
      "message": "ElasticSearch"
    }
  }
}
```

#### 1.2 match_phrase查询

匹配短语，要求查询文本中的词按照精确的顺序出现在字段中。

```json
{
  "query": {
    "match_phrase": {
      "message": "ElasticSearch tutorial"
    }
  }
}
```

#### 1.3 match_phrase_prefix查询

匹配短语前缀，类似于match_phrase，但最后一个词可以是前缀。

```json
{
  "query": {
    "match_phrase_prefix": {
      "message": "ElasticSearch tut"
    }
  }
}
```

#### 1.4 multi_match查询

在多个字段上执行相同的match查询。

```json
{
  "query": {
    "multi_match": {
      "query": "ElasticSearch",
      "fields": ["title", "body"]
    }
  }
}
```

#### 1.5 common查询

针对常见词和罕见词进行不同的处理，优化全文搜索性能。

```json
{
  "query": {
    "common": {
      "body": {
        "query": "to be or not to be",
        "cutoff_frequency": 0.001
      }
    }
  }
}
```

### 2. 精确值查询

精确值查询用于匹配精确值，不会对查询文本进行分词处理。

#### 2.1 term查询

匹配字段中的精确值。

```json
{
  "query": {
    "term": {
      "status": "active"
    }
  }
}
```

#### 2.2 terms查询

匹配字段中的多个精确值。

```json
{
  "query": {
    "terms": {
      "status": ["active", "pending"]
    }
  }
}
```

#### 2.3 range查询

匹配字段值在指定范围内的文档。

```json
{
  "query": {
    "range": {
      "age": {
        "gte": 18,
        "lte": 30
      }
    }
  }
}
```

#### 2.4 exists查询

匹配指定字段存在的文档。

```json
{
  "query": {
    "exists": {
      "field": "email"
    }
  }
}
```

#### 2.5 prefix查询

匹配字段前缀的文档。

```json
{
  "query": {
    "prefix": {
      "name": "Jo"
    }
  }
}
```

#### 2.6 wildcard查询

使用通配符匹配字段值的文档。

```json
{
  "query": {
    "wildcard": {
      "name": "Jo*"
    }
  }
}
```

#### 2.7 regexp查询

使用正则表达式匹配字段值的文档。

```json
{
  "query": {
    "regexp": {
      "name": "Jo.*"
    }
  }
}
```

### 3. 复合查询

复合查询用于组合多个查询条件。

#### 3.1 bool查询

组合多个查询条件，使用must、should、must_not和filter子句。

```json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "ElasticSearch" } }
      ],
      "should": [
        { "match": { "body": "tutorial" } }
      ],
      "filter": [
        { "range": { "published_date": { "gte": "2023-01-01" } } }
      ]
    }
  }
}
```

#### 3.2 dis_max查询

返回匹配任何查询的文档，但只使用得分最高的查询的得分。

```json
{
  "query": {
    "dis_max": {
      "queries": [
        { "match": { "title": "ElasticSearch" } },
        { "match": { "body": "ElasticSearch" } }
      ],
      "tie_breaker": 0.7
    }
  }
}
```

#### 3.3 function_score查询

使用函数修改查询的得分。

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
        }
      ]
    }
  }
}
```

#### 3.4 boosting查询

用于降低某些文档的得分。

```json
{
  "query": {
    "boosting": {
      "positive": { "match": { "title": "ElasticSearch" } },
      "negative": { "match": { "body": "deprecated" } },
      "negative_boost": 0.5
    }
  }
}
```

### 4. 特殊查询

#### 4.1 fuzzy查询

用于模糊匹配，允许一定程度的拼写错误。

```json
{
  "query": {
    "fuzzy": {
      "name": {
        "value": "john",
        "fuzziness": 2
      }
    }
  }
}
```

#### 4.2 ids查询

根据文档ID匹配文档。

```json
{
  "query": {
    "ids": {
      "values": ["1", "2", "3"]
    }
  }
}
```

#### 4.3 constant_score查询

为所有匹配的文档分配相同的得分。

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

## 二、ES聚合方式

ElasticSearch提供了强大的聚合功能，用于数据统计和分析。聚合可以分为以下几类：

### 1. 桶聚合（Bucket Aggregations）

桶聚合将文档分组到不同的桶中，每个桶对应一个分组。

#### 1.1 terms聚合

根据字段值对文档进行分组。

```json
{
  "aggs": {
    "by_status": {
      "terms": {
        "field": "status"
      }
    }
  }
}
```

#### 1.2 range聚合

根据字段值的范围对文档进行分组。

```json
{
  "aggs": {
    "age_ranges": {
      "range": {
        "field": "age",
        "ranges": [
          { "to": 18 },
          { "from": 18, "to": 30 },
          { "from": 30 }
        ]
      }
    }
  }
}
```

#### 1.3 date_range聚合

根据日期范围对文档进行分组。

```json
{
  "aggs": {
    "date_ranges": {
      "date_range": {
        "field": "date",
        "format": "yyyy-MM-dd",
        "ranges": [
          { "to": "2023-01-01" },
          { "from": "2023-01-01", "to": "2023-06-01" },
          { "from": "2023-06-01" }
        ]
      }
    }
  }
}
```

#### 1.4 histogram聚合

根据数值字段的间隔对文档进行分组。

```json
{
  "aggs": {
    "age_histogram": {
      "histogram": {
        "field": "age",
        "interval": 5
      }
    }
  }
}
```

#### 1.5 date_histogram聚合

根据日期字段的间隔对文档进行分组。

```json
{
  "aggs": {
    "date_histogram": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      }
    }
  }
}
```

#### 1.6 geohash_grid聚合

根据地理位置对文档进行分组。

```json
{
  "aggs": {
    "geo_grid": {
      "geohash_grid": {
        "field": "location",
        "precision": 3
      }
    }
  }
}
```

#### 1.7 filter聚合

根据过滤条件对文档进行分组。

```json
{
  "aggs": {
    "filtered_agg": {
      "filter": {
        "term": { "status": "active" }
      },
      "aggs": {
        "average_age": {
          "avg": {
            "field": "age"
          }
        }
      }
    }
  }
}
```

#### 1.8 filters聚合

根据多个过滤条件对文档进行分组。

```json
{
  "aggs": {
    "multi_filter": {
      "filters": {
        "filters": {
          "active": { "term": { "status": "active" } },
          "inactive": { "term": { "status": "inactive" } }
        }
      }
    }
  }
}
```

### 2. 指标聚合（Metric Aggregations）

指标聚合计算文档的统计指标。

#### 2.1 min聚合

计算字段的最小值。

```json
{
  "aggs": {
    "min_age": {
      "min": {
        "field": "age"
      }
    }
  }
}
```

#### 2.2 max聚合

计算字段的最大值。

```json
{
  "aggs": {
    "max_age": {
      "max": {
        "field": "age"
      }
    }
  }
}
```

#### 2.3 sum聚合

计算字段的总和。

```json
{
  "aggs": {
    "total_age": {
      "sum": {
        "field": "age"
      }
    }
  }
}
```

#### 2.4 avg聚合

计算字段的平均值。

```json
{
  "aggs": {
    "average_age": {
      "avg": {
        "field": "age"
      }
    }
  }
}
```

#### 2.5 stats聚合

计算字段的多个统计指标（最小值、最大值、总和、平均值、计数）。

```json
{
  "aggs": {
    "age_stats": {
      "stats": {
        "field": "age"
      }
    }
  }
}
```

#### 2.6 extended_stats聚合

计算字段的更多统计指标，包括方差、标准差等。

```json
{
  "aggs": {
    "age_extended_stats": {
      "extended_stats": {
        "field": "age"
      }
    }
  }
}
```

#### 2.7 value_count聚合

计算字段非空值的数量。

```json
{
  "aggs": {
    "count_age": {
      "value_count": {
        "field": "age"
      }
    }
  }
}
```

#### 2.8 cardinality聚合

计算字段的基数（不同值的数量）。

```json
{
  "aggs": {
    "unique_ages": {
      "cardinality": {
        "field": "age"
      }
    }
  }
}
```

#### 2.9 percentiles聚合

计算字段的百分位数。

```json
{
  "aggs": {
    "age_percentiles": {
      "percentiles": {
        "field": "age"
      }
    }
  }
}
```

#### 2.10 percentile_ranks聚合

计算字段值的百分位排名。

```json
{
  "aggs": {
    "age_ranks": {
      "percentile_ranks": {
        "field": "age",
        "values": [25, 50, 75]
      }
    }
  }
}
```

### 3. 管道聚合（Pipeline Aggregations）

管道聚合对其他聚合的结果进行进一步计算。

#### 3.1 avg_bucket聚合

计算桶聚合结果的平均值。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "average_monthly_sales": {
      "avg_bucket": {
        "buckets_path": "sales_by_month>monthly_sales"
      }
    }
  }
}
```

#### 3.2 sum_bucket聚合

计算桶聚合结果的总和。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "total_sales": {
      "sum_bucket": {
        "buckets_path": "sales_by_month>monthly_sales"
      }
    }
  }
}
```

#### 3.3 max_bucket聚合

计算桶聚合结果的最大值。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "max_monthly_sales": {
      "max_bucket": {
        "buckets_path": "sales_by_month>monthly_sales"
      }
    }
  }
}
```

#### 3.4 min_bucket聚合

计算桶聚合结果的最小值。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "min_monthly_sales": {
      "min_bucket": {
        "buckets_path": "sales_by_month>monthly_sales"
      }
    }
  }
}
```

#### 3.5 stats_bucket聚合

计算桶聚合结果的统计指标。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "sales_stats": {
      "stats_bucket": {
        "buckets_path": "sales_by_month>monthly_sales"
      }
    }
  }
}
```

#### 3.6 extended_stats_bucket聚合

计算桶聚合结果的更多统计指标。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "sales_extended_stats": {
      "extended_stats_bucket": {
        "buckets_path": "sales_by_month>monthly_sales"
      }
    }
  }
}
```

#### 3.7 derivative聚合

计算桶聚合结果的导数。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "sales_change": {
          "derivative": {
            "buckets_path": "monthly_sales"
          }
        }
      }
    }
  }
}
```

#### 3.8 cumulative_sum聚合

计算桶聚合结果的累积和。

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "date",
        "calendar_interval": "month"
      },
      "aggs": {
        "monthly_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "cumulative_sales": {
          "cumulative_sum": {
            "buckets_path": "monthly_sales"
          }
        }
      }
    }
  }
}
```

## 三、查询和聚合的组合使用

在实际应用中，查询和聚合通常是结合使用的。例如：

```json
{
  "query": {
    "bool": {
      "filter": [
        { "range": { "date": { "gte": "2023-01-01" } } }
      ]
    }
  },
  "aggs": {
    "sales_by_category": {
      "terms": {
        "field": "category"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "avg_price": {
          "avg": {
            "field": "price"
          }
        }
      }
    },
    "total_sales": {
      "sum": {
        "field": "sales"
      }
    }
  }
}
```

## 四、最佳实践

1. **根据需求选择合适的查询类型**：全文搜索使用match等查询，精确匹配使用term等查询。

2. **使用filter子句提高性能**：对于不需要计算得分的条件，使用filter子句而不是must子句。

3. **合理使用聚合**：避免在大型数据集上使用过于复杂的聚合，可能会导致性能问题。

4. **分页处理**：对于大型聚合结果，使用分页机制避免返回过多数据。

5. **缓存优化**：利用ElasticSearch的查询缓存和字段数据缓存提高性能。

6. **监控和调优**：监控查询和聚合的性能，根据实际情况进行调优。

7. **测试不同的查询和聚合方式**：对于复杂的查询需求，测试不同的实现方式，选择性能最佳的方案。

## 总结

ElasticSearch提供了丰富的查询和聚合功能，能够满足各种复杂的搜索和分析需求。通过合理选择和组合这些功能，可以构建强大的搜索和分析系统。在实际应用中，应该根据具体的业务需求和数据特点，选择最合适的查询和聚合方式，并进行适当的性能优化。