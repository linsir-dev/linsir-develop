# ES聚合中的管道聚合有哪些？如何理解？

在ElasticSearch中，聚合（Aggregations）是一种强大的数据分析工具，用于对数据进行统计、分组和计算。管道聚合（Pipeline Aggregations）是聚合的一种特殊类型，它用于对其他聚合的结果进行进一步的计算和分析。本文将详细介绍管道聚合的概念、类型和使用方法。

## 什么是管道聚合？

管道聚合是一种特殊类型的聚合，它不直接对文档进行操作，而是对其他聚合（称为父聚合）的结果进行操作。管道聚合的核心思想是"链式计算"，它允许你在一个聚合结果的基础上执行另一个聚合，形成一个聚合计算链。

管道聚合可以分为两类：

1. **父级管道聚合**：在父聚合的结果基础上进行计算
2. **兄弟管道聚合**：在同级聚合的结果基础上进行计算

## 管道聚合的类型

ElasticSearch提供了多种类型的管道聚合，每种类型都有其特定的计算逻辑和使用场景。以下是常见的管道聚合类型：

### 1. avg_bucket聚合

`avg_bucket`聚合计算同级聚合桶的平均值。

#### 语法和参数

```json
{
  "avg_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "average_monthly_sales": {
      "avg_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

#### 应用场景

- **计算平均值**：如计算每月销售额的平均值
- **平滑数据**：如平滑时间序列数据的波动

### 2. max_bucket聚合

`max_bucket`聚合计算同级聚合桶的最大值。

#### 语法和参数

```json
{
  "max_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros"           // 处理缺失桶的策略
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "max_monthly_sales": {
      "max_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

#### 应用场景

- **找出最大值**：如找出销售额最高的月份
- **峰值分析**：如分析时间序列数据的峰值

### 3. min_bucket聚合

`min_bucket`聚合计算同级聚合桶的最小值。

#### 语法和参数

```json
{
  "min_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros"           // 处理缺失桶的策略
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "min_monthly_sales": {
      "min_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

#### 应用场景

- **找出最小值**：如找出销售额最低的月份
- **谷值分析**：如分析时间序列数据的谷值

### 4. sum_bucket聚合

`sum_bucket`聚合计算同级聚合桶的总和。

#### 语法和参数

```json
{
  "sum_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "total_yearly_sales": {
      "sum_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

#### 应用场景

- **计算总和**：如计算年度总销售额
- **累积计算**：如计算累积值

### 5. stats_bucket聚合

`stats_bucket`聚合计算同级聚合桶的多个统计值，包括最小值、最大值、总和、平均值和桶数量。

#### 语法和参数

```json
{
  "stats_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "sales_stats": {
      "stats_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

#### 应用场景

- **综合统计**：如计算每月销售额的综合统计信息
- **快速分析**：如快速了解时间序列数据的整体情况

### 6. extended_stats_bucket聚合

`extended_stats_bucket`聚合计算同级聚合桶的扩展统计值，除了`stats_bucket`聚合的统计值外，还包括方差、标准差、总和的平方、偏度和峰度。

#### 语法和参数

```json
{
  "extended_stats_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "sales_extended_stats": {
      "extended_stats_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

#### 应用场景

- **高级统计分析**：如分析销售额的分布情况、离散程度等
- **质量控制**：如分析数据的稳定性

### 7. percentiles_bucket聚合

`percentiles_bucket`聚合计算同级聚合桶的百分位数。

#### 语法和参数

```json
{
  "percentiles_bucket": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "percents": [1, 5, 25, 50, 75, 95, 99], // 百分位数
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        }
      }
    },
    "sales_percentiles": {
      "percentiles_bucket": {
        "buckets_path": "sales_by_month>total_sales",
        "percents": [10, 50, 90]
      }
    }
  }
}
```

#### 应用场景

- **分布分析**：如分析每月销售额的分布情况
- **异常检测**：如识别异常高或异常低的桶值

### 8. derivative聚合

`derivative`聚合计算父聚合桶值的导数（变化率）。

#### 语法和参数

```json
{
  "derivative": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "sales_change": {
          "derivative": {
            "buckets_path": "total_sales"
          }
        }
      }
    }
  }
}
```

#### 应用场景

- **趋势分析**：如分析销售额的变化趋势
- **速度计算**：如计算增长速度

### 9. cumulative_sum聚合

`cumulative_sum`聚合计算父聚合桶值的累积和。

#### 语法和参数

```json
{
  "cumulative_sum": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "cumulative_sales": {
          "cumulative_sum": {
            "buckets_path": "total_sales"
          }
        }
      }
    }
  }
}
```

#### 应用场景

- **累积分析**：如计算年度累积销售额
- **进度跟踪**：如跟踪项目进度

### 10. moving_avg聚合

`moving_avg`聚合计算父聚合桶值的移动平均值。

#### 语法和参数

```json
{
  "moving_avg": {
    "buckets_path": "parent_aggregation",  // 父聚合路径
    "window": 3,                          // 窗口大小
    "model": "simple",                    // 模型：simple, linear, ewma, holt, holt_winters
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "moving_average_sales": {
          "moving_avg": {
            "buckets_path": "total_sales",
            "window": 3
          }
        }
      }
    }
  }
}
```

#### 应用场景

- **趋势分析**：如分析销售额的移动平均趋势
- **平滑数据**：如平滑时间序列数据的短期波动

### 11. bucket_script聚合

`bucket_script`聚合使用脚本对父聚合的多个子聚合结果进行计算。

#### 语法和参数

```json
{
  "bucket_script": {
    "buckets_path": {
      "sales": "total_sales",
      "costs": "total_costs"
    },
    "script": "params.sales - params.costs",  // 脚本
    "gap_policy": "insert_zeros",          // 处理缺失桶的策略
    "format": "#.##"                      // 格式化输出
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "total_costs": {
          "sum": {
            "field": "costs"
          }
        },
        "profit": {
          "bucket_script": {
            "buckets_path": {
              "sales": "total_sales",
              "costs": "total_costs"
            },
            "script": "params.sales - params.costs"
          }
        }
      }
    }
  }
}
```

#### 应用场景

- **自定义计算**：如计算利润（销售额 - 成本）
- **比率计算**：如计算利润率（利润 / 销售额）
- **复杂指标**：如计算综合业务指标

### 12. bucket_selector聚合

`bucket_selector`聚合使用脚本过滤父聚合的桶。

#### 语法和参数

```json
{
  "bucket_selector": {
    "buckets_path": {
      "sales": "total_sales"
    },
    "script": "params.sales > 1000",  // 脚本
    "gap_policy": "insert_zeros"      // 处理缺失桶的策略
  }
}
```

#### 示例

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "high_sales": {
          "bucket_selector": {
            "buckets_path": {
              "sales": "total_sales"
            },
            "script": "params.sales > 10000"
          }
        }
      }
    }
  }
}
```

#### 应用场景

- **过滤桶**：如过滤销售额超过阈值的月份
- **条件分析**：如只分析满足特定条件的桶

### 13. bucket_sort聚合

`bucket_sort`聚合对父聚合的桶进行排序。

#### 语法和参数

```json
{
  "bucket_sort": {
    "sort": [
      { "total_sales": { "order": "desc" } }
    ],
    "from": 0,
    "size": 10,
    "gap_policy": "insert_zeros"
  }
}
```

#### 示例

```json
{
  "size": 0,
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
        "sorted_sales": {
          "bucket_sort": {
            "sort": [
              { "total_sales": { "order": "desc" } }
            ],
            "size": 5
          }
        }
      }
    }
  }
}
```

#### 应用场景

- **排序桶**：如按销售额降序排序类别
- **Top N分析**：如获取销售额最高的前5个类别

## 管道聚合的使用方法

### 1. 基本使用

```json
{
  "size": 0,
  "aggs": {
    "parent_aggregation": {
      "bucket_aggregation_type": {
        "field": "field_name"
      },
      "aggs": {
        "metric_aggregation": {
          "metric_aggregation_type": {
            "field": "numeric_field"
          }
        },
        "pipeline_aggregation": {
          "pipeline_aggregation_type": {
            "buckets_path": "metric_aggregation"
          }
        }
      }
    }
  }
}
```

### 2. 兄弟管道聚合

```json
{
  "size": 0,
  "aggs": {
    "parent_aggregation": {
      "bucket_aggregation_type": {
        "field": "field_name"
      },
      "aggs": {
        "metric_aggregation": {
          "metric_aggregation_type": {
            "field": "numeric_field"
          }
        }
      }
    },
    "sibling_pipeline_aggregation": {
      "pipeline_aggregation_type": {
        "buckets_path": "parent_aggregation>metric_aggregation"
      }
    }
  }
}
```

### 3. 多级管道聚合

管道聚合可以多级嵌套使用：

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "sales_change": {
          "derivative": {
            "buckets_path": "total_sales"
          }
        },
        "change_trend": {
          "moving_avg": {
            "buckets_path": "sales_change",
            "window": 3
          }
        }
      }
    }
  }
}
```

## 管道聚合的最佳实践

### 1. 合理选择管道聚合类型

- **计算平均值**：使用avg_bucket聚合
- **计算最大值/最小值**：使用max_bucket/min_bucket聚合
- **计算总和**：使用sum_bucket聚合
- **计算综合统计**：使用stats_bucket或extended_stats_bucket聚合
- **计算百分位数**：使用percentiles_bucket聚合
- **计算变化率**：使用derivative聚合
- **计算累积和**：使用cumulative_sum聚合
- **计算移动平均值**：使用moving_avg聚合
- **自定义计算**：使用bucket_script聚合
- **过滤桶**：使用bucket_selector聚合
- **排序桶**：使用bucket_sort聚合

### 2. 优化性能

- **限制父聚合的桶数量**：减少管道聚合需要处理的桶数量
- **合理设置gap_policy**：根据数据特点选择合适的缺失桶处理策略
- **避免过深的嵌套**：过深的聚合嵌套可能会影响性能
- **使用适当的脚本**：避免在bucket_script中使用复杂的脚本

### 3. 处理缺失数据

- **使用gap_policy参数**：选择合适的缺失桶处理策略
  - **insert_zeros**：插入零值
  - **skip**：跳过缺失的桶
  - **keep_values**：保持原始值

### 4. 提高可读性

- **使用有意义的聚合名称**：为聚合设置有意义的名称
- **组织聚合结构**：合理组织聚合的嵌套结构
- **使用format参数**：格式化聚合结果的输出

### 5. 测试和验证

- **测试聚合结果**：确保管道聚合返回预期的结果
- **验证计算逻辑**：验证管道聚合的计算逻辑是否正确
- **监控性能**：监控管道聚合的性能，及时发现和解决性能问题

## 应用场景示例

### 1. 销售趋势分析

```json
{
  "size": 0,
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month",
        "format": "yyyy-MM"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "sales_change": {
          "derivative": {
            "buckets_path": "total_sales"
          }
        },
        "moving_average": {
          "moving_avg": {
            "buckets_path": "total_sales",
            "window": 3
          }
        },
        "cumulative_sales": {
          "cumulative_sum": {
            "buckets_path": "total_sales"
          }
        }
      }
    },
    "sales_summary": {
      "stats_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    },
    "best_month": {
      "max_bucket": {
        "buckets_path": "sales_by_month>total_sales"
      }
    }
  }
}
```

### 2. 业务指标计算

```json
{
  "size": 0,
  "aggs": {
    "by_product": {
      "terms": {
        "field": "product_id"
      },
      "aggs": {
        "total_sales": {
          "sum": {
            "field": "sales"
          }
        },
        "total_costs": {
          "sum": {
            "field": "costs"
          }
        },
        "profit": {
          "bucket_script": {
            "buckets_path": {
              "sales": "total_sales",
              "costs": "total_costs"
            },
            "script": "params.sales - params.costs"
          }
        },
        "profit_margin": {
          "bucket_script": {
            "buckets_path": {
              "sales": "total_sales",
              "profit": "profit"
            },
            "script": "params.sales > 0 ? (params.profit / params.sales) * 100 : 0"
          }
        },
        "top_products": {
          "bucket_sort": {
            "sort": [
              { "profit": { "order": "desc" } }
            ],
            "size": 5
          }
        }
      }
    }
  }
}
```

### 3. 网站流量分析

```json
{
  "size": 0,
  "aggs": {
    "traffic_by_hour": {
      "histogram": {
        "field": "hour",
        "interval": 1
      },
      "aggs": {
        "total_visitors": {
          "sum": {
            "field": "visitors"
          }
        },
        "total_page_views": {
          "sum": {
            "field": "page_views"
          }
        },
        "average_page_views": {
          "bucket_script": {
            "buckets_path": {
              "views": "total_page_views",
              "visitors": "total_visitors"
            },
            "script": "params.visitors > 0 ? params.views / params.visitors : 0"
          }
        },
        "busy_hours": {
          "bucket_selector": {
            "buckets_path": {
              "visitors": "total_visitors"
            },
            "script": "params.visitors > 1000"
          }
        }
      }
    },
    "daily_average": {
      "avg_bucket": {
        "buckets_path": "traffic_by_hour>total_visitors"
      }
    }
  }
}
```

## 总结

管道聚合是ElasticSearch中一种强大的数据分析工具，它允许你对其他聚合的结果进行进一步的计算和分析。ElasticSearch提供了多种类型的管道聚合，每种类型都有其特定的计算逻辑和使用场景。

通过合理使用管道聚合，你可以：

1. **执行链式计算**：在一个聚合结果的基础上执行另一个聚合
2. **分析数据趋势**：如计算变化率、移动平均值等
3. **计算复杂指标**：如使用脚本计算自定义指标
4. **过滤和排序桶**：如过滤满足特定条件的桶、排序桶等
5. **生成综合统计**：如计算多个统计值

在实际应用中，你应该根据具体的分析需求选择合适的管道聚合类型，并结合其他聚合类型使用，以获得更全面的数据分析结果。

## 常见问题

### 1. 管道聚合与普通聚合有什么区别？

- **操作对象**：管道聚合操作其他聚合的结果，而普通聚合操作文档
- **执行顺序**：管道聚合在普通聚合之后执行
- **依赖关系**：管道聚合依赖于其他聚合的结果
- **计算逻辑**：管道聚合通常执行更复杂的计算

### 2. 如何指定管道聚合的路径？

使用`buckets_path`参数指定管道聚合的路径，格式为：
- 对于父级管道聚合：`metric_aggregation`
- 对于兄弟管道聚合：`parent_aggregation>metric_aggregation`

### 3. 如何处理管道聚合中的缺失数据？

使用`gap_policy`参数处理缺失数据，可选值包括：
- **insert_zeros**：插入零值
- **skip**：跳过缺失的桶
- **keep_values**：保持原始值

### 4. 管道聚合的性能如何优化？

- **限制父聚合的桶数量**：减少需要处理的桶数量
- **合理设置参数**：如window大小、精度等
- **避免复杂脚本**：减少bucket_script中的复杂计算
- **监控执行时间**：及时发现性能问题

### 5. 管道聚合可以嵌套使用吗？

是的，管道聚合可以嵌套使用，形成多级管道聚合：

```json
{
  "aggs": {
    "sales_by_month": {
      "date_histogram": {
        "field": "sale_date",
        "interval": "month"
      },
      "aggs": {
        "total_sales": { "sum": { "field": "sales" } },
        "sales_change": { "derivative": { "buckets_path": "total_sales" } },
        "change_trend": { "moving_avg": { "buckets_path": "sales_change" } }
      }
    }
  }
}
```