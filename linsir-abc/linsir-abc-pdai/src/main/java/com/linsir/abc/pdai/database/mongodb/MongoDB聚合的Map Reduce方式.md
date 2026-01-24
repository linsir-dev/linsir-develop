# MongoDB聚合的Map Reduce方式

## 基本概念

Map Reduce是MongoDB中一种数据处理模式，用于处理和分析大量数据。它基于Google的MapReduce论文，通过将数据处理分为映射（Map）、 shuffle 和 归约（Reduce）三个阶段，实现并行处理和分布式计算。

## 工作原理

1. **Map阶段**：对集合中的每个文档应用Map函数，将文档转换为键值对（key-value pairs）。

2. **Shuffle阶段**：MongoDB自动将Map阶段输出的键值对按键分组，将相同键的值收集到一起。

3. **Reduce阶段**：对每个键对应的所有值应用Reduce函数，将这些值合并为一个结果。

4. **Finalize阶段**（可选）：对Reduce阶段的结果应用Finalize函数，进行最终处理和转换。

## Map Reduce的语法

### 基本语法

```javascript
db.collection.mapReduce(
  function() { /* Map函数 */ },
  function(key, values) { /* Reduce函数 */ },
  {
    out: <output>,
    query: <query>,
    sort: <sort>,
    limit: <number>,
    finalize: function(key, reducedValue) { /* Finalize函数 */ },
    scope: <scope>,
    jsMode: <boolean>,
    verbose: <boolean>,
    bypassDocumentValidation: <boolean>
  }
)
```

### 参数说明

- **map函数**：接收文档作为输入，使用`emit(key, value)`函数输出键值对。
- **reduce函数**：接收键和对应的值数组，返回合并后的结果。
- **out**：指定结果的输出方式，可选值包括：
  - `{ inline: 1 }`：将结果返回给客户端（限制16MB）
  - `"collection"`：将结果写入指定集合，覆盖现有数据
  - `{ replace: "collection" }`：将结果写入指定集合，覆盖现有数据
  - `{ merge: "collection" }`：将结果合并到指定集合
  - `{ reduce: "collection" }`：将结果与指定集合中的现有数据进行reduce操作
- **query**：可选，指定过滤条件，只处理满足条件的文档。
- **sort**：可选，指定文档的排序顺序。
- **limit**：可选，限制处理的文档数量。
- **finalize**：可选，对reduce结果进行最终处理的函数。
- **scope**：可选，指定传递给map、reduce和finalize函数的变量。
- **jsMode**：可选，是否在JavaScript模式下执行Map Reduce，默认为false。
- **verbose**：可选，是否包含详细的执行统计信息，默认为false。
- **bypassDocumentValidation**：可选，是否跳过文档验证，默认为false。

## Map Reduce函数的编写

### Map函数

Map函数接收一个文档作为输入，使用`emit(key, value)`函数输出键值对。Map函数应该是无状态的，只依赖于输入文档。

**示例**：
```javascript
function() {
  // 按department分组，输出每个员工的salary
  emit(this.department, this.salary);
}
```

### Reduce函数

Reduce函数接收一个键和对应的值数组，返回合并后的结果。Reduce函数必须是幂等的，因为对于大型数据集，MongoDB可能会多次调用reduce函数处理部分结果。

**示例**：
```javascript
function(key, values) {
  // 计算每个部门的工资总和
  return Array.sum(values);
}
```

### Finalize函数

Finalize函数接收键和reduce函数的结果，返回最终处理后的结果。

**示例**：
```javascript
function(key, reducedValue) {
  // 对结果进行格式化
  return { totalSalary: reducedValue, department: key };
}
```

## Map Reduce示例

### 示例1：计算每个部门的平均工资

**数据**：
```javascript
// employees集合
[
  { _id: 1, name: "张三", department: "技术部", salary: 10000 },
  { _id: 2, name: "李四", department: "技术部", salary: 12000 },
  { _id: 3, name: "王五", department: "市场部", salary: 8000 },
  { _id: 4, name: "赵六", department: "市场部", salary: 9000 },
  { _id: 5, name: "钱七", department: "财务部", salary: 9500 }
]
```

**Map Reduce操作**：
```javascript
db.employees.mapReduce(
  function() {
    emit(this.department, { total: this.salary, count: 1 });
  },
  function(key, values) {
    var result = { total: 0, count: 0 };
    values.forEach(function(value) {
      result.total += value.total;
      result.count += value.count;
    });
    return result;
  },
  {
    finalize: function(key, reducedValue) {
      reducedValue.average = reducedValue.total / reducedValue.count;
      return reducedValue;
    },
    out: { inline: 1 }
  }
)
```

**结果**：
```javascript
{
  "results": [
    {
      "_id": "技术部",
      "value": {
        "total": 22000,
        "count": 2,
        "average": 11000
      }
    },
    {
      "_id": "市场部",
      "value": {
        "total": 17000,
        "count": 2,
        "average": 8500
      }
    },
    {
      "_id": "财务部",
      "value": {
        "total": 9500,
        "count": 1,
        "average": 9500
      }
    }
  ],
  "timeMillis": 10,
  "counts": {
    "input": 5,
    "emit": 5,
    "reduce": 1,
    "output": 3
  },
  "ok": 1
}
```

### 示例2：分析用户行为数据

**数据**：
```javascript
// userActions集合
[
  { _id: 1, userId: 1, action: "view", itemId: 101, timestamp: ISODate("2023-01-01T10:00:00") },
  { _id: 2, userId: 1, action: "view", itemId: 102, timestamp: ISODate("2023-01-01T10:05:00") },
  { _id: 3, userId: 1, action: "purchase", itemId: 101, timestamp: ISODate("2023-01-01T10:10:00") },
  { _id: 4, userId: 2, action: "view", itemId: 101, timestamp: ISODate("2023-01-01T11:00:00") },
  { _id: 5, userId: 2, action: "view", itemId: 103, timestamp: ISODate("2023-01-01T11:05:00") }
]
```

**Map Reduce操作**：
```javascript
db.userActions.mapReduce(
  function() {
    emit(this.action, { count: 1, users: [this.userId] });
  },
  function(key, values) {
    var result = { count: 0, users: [] };
    values.forEach(function(value) {
      result.count += value.count;
      result.users = result.users.concat(value.users);
    });
    // 去重
    result.users = result.users.filter(function(user, index, self) {
      return self.indexOf(user) === index;
    });
    return result;
  },
  {
    finalize: function(key, reducedValue) {
      reducedValue.uniqueUserCount = reducedValue.users.length;
      return reducedValue;
    },
    out: { inline: 1 }
  }
)
```

**结果**：
```javascript
{
  "results": [
    {
      "_id": "view",
      "value": {
        "count": 4,
        "users": [1, 2],
        "uniqueUserCount": 2
      }
    },
    {
      "_id": "purchase",
      "value": {
        "count": 1,
        "users": [1],
        "uniqueUserCount": 1
      }
    }
  ],
  "timeMillis": 15,
  "counts": {
    "input": 5,
    "emit": 5,
    "reduce": 0,
    "output": 2
  },
  "ok": 1
}
```

### 示例3：统计每个月份的订单数量

**数据**：
```javascript
// orders集合
[
  { _id: 1, amount: 100, date: ISODate("2023-01-01") },
  { _id: 2, amount: 200, date: ISODate("2023-01-15") },
  { _id: 3, amount: 150, date: ISODate("2023-02-01") },
  { _id: 4, amount: 300, date: ISODate("2023-02-15") },
  { _id: 5, amount: 250, date: ISODate("2023-03-01") }
]
```

**Map Reduce操作**：
```javascript
db.orders.mapReduce(
  function() {
    // 提取月份作为键
    var month = this.date.getMonth() + 1;
    emit(month, { count: 1, totalAmount: this.amount });
  },
  function(key, values) {
    var result = { count: 0, totalAmount: 0 };
    values.forEach(function(value) {
      result.count += value.count;
      result.totalAmount += value.totalAmount;
    });
    return result;
  },
  {
    finalize: function(key, reducedValue) {
      reducedValue.averageAmount = reducedValue.totalAmount / reducedValue.count;
      return reducedValue;
    },
    out: { inline: 1 }
  }
)
```

**结果**：
```javascript
{
  "results": [
    {
      "_id": 1,
      "value": {
        "count": 2,
        "totalAmount": 300,
        "averageAmount": 150
      }
    },
    {
      "_id": 2,
      "value": {
        "count": 2,
        "totalAmount": 450,
        "averageAmount": 225
      }
    },
    {
      "_id": 3,
      "value": {
        "count": 1,
        "totalAmount": 250,
        "averageAmount": 250
      }
    }
  ],
  "timeMillis": 12,
  "counts": {
    "input": 5,
    "emit": 5,
    "reduce": 0,
    "output": 3
  },
  "ok": 1
}
```

## Map Reduce的优缺点

### 优点

1. **灵活性**：Map Reduce使用JavaScript函数，可以处理复杂的数据转换和计算逻辑。

2. **分布式处理**：Map Reduce可以并行处理数据，适合处理大规模数据集。

3. **可扩展性**：可以处理超出内存大小的数据集，通过磁盘临时存储。

4. **结果存储**：可以将结果存储到集合中，方便后续查询和分析。

### 缺点

1. **性能较慢**：相比聚合管道，Map Reduce的性能较差，因为它使用JavaScript执行，而不是原生操作。

2. **内存限制**：虽然可以使用磁盘存储，但处理大型数据集时仍然可能遇到内存限制。

3. **复杂度高**：编写Map、Reduce和Finalize函数需要更多的代码和逻辑。

4. **调试困难**：Map Reduce的执行过程难以调试，错误信息不够详细。

5. **结果大小限制**：当使用`{ inline: 1 }`输出时，结果大小限制为16MB。

## Map Reduce与聚合管道的比较

| 特性 | Map Reduce | 聚合管道 |
|------|------------|----------|
| 执行引擎 | JavaScript | 原生C++ |
| 性能 | 较慢 | 较快 |
| 灵活性 | 高（可以编写任意JavaScript逻辑） | 中等（使用预定义操作符） |
| 复杂度 | 高（需要编写多个函数） | 低（使用链式操作） |
| 结果大小限制 | 内联输出限制16MB | 内联输出限制16MB |
| 并行处理 | 支持 | 支持（从3.2开始） |
| 适用场景 | 复杂的数据处理和转换 | 一般的聚合和分析任务 |

## Map Reduce的最佳实践

1. **使用query参数过滤数据**：减少Map函数处理的文档数量，提高性能。

2. **使用sort和limit参数**：对于大型集合，使用sort和limit限制处理的文档数量。

3. **保持Map和Reduce函数简单**：复杂的函数会降低性能，增加调试难度。

4. **使用out参数存储结果**：对于频繁执行的Map Reduce操作，将结果存储到集合中，避免重复计算。

5. **使用scope参数传递变量**：避免在Map和Reduce函数中硬编码常量和配置。

6. **设置jsMode为true**：对于小型数据集，设置jsMode为true可以提高性能，因为它避免了在JavaScript和C++之间的转换。

7. **监控Map Reduce操作**：使用MongoDB的监控工具跟踪Map Reduce操作的执行情况，识别性能瓶颈。

## Map Reduce的限制

1. **JavaScript执行**：Map Reduce使用JavaScript执行，性能不如原生操作。

2. **内存限制**：默认情况下，Map Reduce的内存使用限制为100MB，可以通过设置`maxJavaScriptHeapSizeMB`参数增加。

3. **结果大小限制**：内联输出的结果大小限制为16MB。

4. **并发限制**：MongoDB限制了Map Reduce操作的并发执行数量，避免过度消耗资源。

5. **不支持事务**：Map Reduce操作不支持事务，可能会受到并发写操作的影响。

## 何时使用Map Reduce

虽然聚合管道在大多数情况下是更好的选择，但Map Reduce仍然适用于以下场景：

1. **复杂的数据转换**：当需要执行复杂的JavaScript逻辑进行数据转换时。

2. **自定义聚合逻辑**：当预定义的聚合管道操作符无法满足需求时。

3. **处理大型数据集**：当需要处理超出聚合管道内存限制的大型数据集时。

4. **与现有代码集成**：当已经有基于Map Reduce的代码，并且迁移到聚合管道的成本较高时。

## 总结

Map Reduce是MongoDB中一种强大的数据处理工具，通过将数据处理分为映射、 shuffle 和 归约三个阶段，实现并行处理和分布式计算。虽然它的性能不如聚合管道，但在处理复杂的数据转换和计算任务时仍然具有优势。

在实际应用中，应该根据具体的需求和数据规模选择合适的聚合方式。对于一般的聚合和分析任务，推荐使用聚合管道；对于复杂的数据转换和计算任务，可以考虑使用Map Reduce。