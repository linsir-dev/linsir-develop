# MongoDB聚合的管道方式

## 基本概念

MongoDB的聚合管道（Aggregation Pipeline）是一种数据处理框架，用于对集合中的文档进行转换、处理和分析。聚合管道由一系列数据处理阶段（Stage）组成，每个阶段接收前一个阶段的输出并产生输出，形成一个数据处理管道。

## 工作原理

1. **数据流动**：文档从集合中被读取，然后通过管道的每个阶段进行处理，最终产生结果。

2. **阶段执行**：管道中的每个阶段按顺序执行，每个阶段可以：
   - 过滤文档（如$match）
   - 转换文档（如$project）
   - 分组文档（如$group）
   - 排序文档（如$sort）
   - 限制文档数量（如$limit）
   - 跳过文档（如$skip）
   - 展开数组（如$unwind）
   - 关联其他集合（如$lookup）

3. **内存处理**：聚合管道在内存中处理数据，对于大型数据集，可以使用磁盘临时存储。

4. **并行处理**：从MongoDB 3.2开始，聚合管道支持并行处理，提高处理效率。

## 常用聚合管道操作符

### 1. $match

**功能**：过滤文档，只将满足条件的文档传递到下一个阶段。

**语法**：
```javascript
{ $match: { <query> } }
```

**示例**：
```javascript
// 过滤出age大于18的文档
{ $match: { age: { $gt: 18 } } }
```

### 2. $group

**功能**：根据指定的字段对文档进行分组，并计算聚合值。

**语法**：
```javascript
{ $group: { _id: <expression>, <field1>: { <accumulator1>: <expression1> }, ... } }
```

**常用累加器**：
- $sum：计算总和
- $avg：计算平均值
- $min：获取最小值
- $max：获取最大值
- $first：获取第一个文档的值
- $last：获取最后一个文档的值
- $push：将值添加到数组中
- $addToSet：将值添加到有序集合中（去重）

**示例**：
```javascript
// 按gender分组，计算每组的平均年龄和人数
{ $group: { _id: "$gender", avgAge: { $avg: "$age" }, count: { $sum: 1 } } }
```

### 3. $project

**功能**：重塑文档结构，可以添加、删除或重命名字段。

**语法**：
```javascript
{ $project: { <field1>: <expression>, <field2>: <expression>, ... } }
```

**示例**：
```javascript
// 只保留name和age字段，并重命名age为userAge
{ $project: { _id: 0, name: 1, userAge: "$age" } }
```

### 4. $sort

**功能**：对文档进行排序。

**语法**：
```javascript
{ $sort: { <field1>: <sort order>, <field2>: <sort order>, ... } }
```

**排序顺序**：
- 1：升序
- -1：降序

**示例**：
```javascript
// 按age降序排序
{ $sort: { age: -1 } }
```

### 5. $limit

**功能**：限制传递到下一个阶段的文档数量。

**语法**：
```javascript
{ $limit: <positive integer> }
```

**示例**：
```javascript
// 只传递前5个文档
{ $limit: 5 }
```

### 6. $skip

**功能**：跳过指定数量的文档，只传递剩余的文档。

**语法**：
```javascript
{ $skip: <positive integer> }
```

**示例**：
```javascript
// 跳过前5个文档
{ $skip: 5 }
```

### 7. $unwind

**功能**：展开数组字段，将每个数组元素转换为单独的文档。

**语法**：
```javascript
{ $unwind: { path: <field path>, preserveNullAndEmptyArrays: <boolean> } }
```

**参数**：
- path：要展开的数组字段路径
- preserveNullAndEmptyArrays：是否保留空数组或null值的文档，默认为false

**示例**：
```javascript
// 展开hobbies数组
{ $unwind: "$hobbies" }

// 展开hobbies数组，并保留空数组的文档
{ $unwind: { path: "$hobbies", preserveNullAndEmptyArrays: true } }
```

### 8. $lookup

**功能**：关联其他集合，类似于SQL中的JOIN操作。

**语法**：
```javascript
{
  $lookup: {
    from: <collection to join>,
    localField: <field from the input documents>,
    foreignField: <field from the documents of the "from" collection>,
    as: <output array field>
  }
}
```

**示例**：
```javascript
// 关联orders集合，将customerId匹配的订单添加到orders字段
{
  $lookup: {
    from: "orders",
    localField: "_id",
    foreignField: "customerId",
    as: "orders"
  }
}
```

### 9. $addFields

**功能**：向文档添加新字段，而不影响现有字段。

**语法**：
```javascript
{ $addFields: { <newField>: <expression>, ... } }
```

**示例**：
```javascript
// 添加fullName字段，值为firstName和lastName的组合
{ $addFields: { fullName: { $concat: ["$firstName", " ", "$lastName"] } } }
```

### 10. $out

**功能**：将聚合结果写入指定的集合。

**语法**：
```javascript
{ $out: <collection name> }
```

**示例**：
```javascript
// 将聚合结果写入result集合
{ $out: "result" }
```

### 11. $merge

**功能**：将聚合结果合并到指定的集合，可以插入新文档或更新现有文档。

**语法**：
```javascript
{
  $merge: {
    into: <collection name>,
    on: <field>,
    whenMatched: <action>,
    whenNotMatched: <action>
  }
}
```

**示例**：
```javascript
// 将聚合结果合并到result集合，根据_id字段匹配
{
  $merge: {
    into: "result",
    on: "_id",
    whenMatched: "replace",
    whenNotMatched: "insert"
  }
}
```

### 12. $facet

**功能**：在单个聚合管道中执行多个独立的聚合操作。

**语法**：
```javascript
{
  $facet: {
    <pipeline1>: [<stage1>, <stage2>, ...],
    <pipeline2>: [<stage1>, <stage2>, ...],
    ...
  }
}
```

**示例**：
```javascript
// 同时计算按gender分组的统计和按age范围分组的统计
{
  $facet: {
    byGender: [
      { $group: { _id: "$gender", count: { $sum: 1 } } }
    ],
    byAgeRange: [
      {
        $bucket: {
          groupBy: "$age",
          boundaries: [0, 18, 30, 50, 100],
          default: "Other",
          output: { count: { $sum: 1 } }
        }
      }
    ]
  }
}
```

### 13. $bucket

**功能**：将文档分组到指定的桶中，根据字段值的范围。

**语法**：
```javascript
{
  $bucket: {
    groupBy: <expression>,
    boundaries: [<lowerbound1>, <lowerbound2>, ...],
    default: <defaultValue>,
    output: {
      <outputField1>: { <accumulator1>: <expression1> },
      ...
    }
  }
}
```

**示例**：
```javascript
// 将age分组到不同的桶中
{
  $bucket: {
    groupBy: "$age",
    boundaries: [0, 18, 30, 50, 100],
    default: "Other",
    output: { count: { $sum: 1 } }
  }
}
```

### 14. $graphLookup

**功能**：执行递归关联，用于处理层次结构或图结构数据。

**语法**：
```javascript
{
  $graphLookup: {
    from: <collection>,
    startWith: <expression>,
    connectFromField: <field>,
    connectToField: <field>,
    as: <output array field>,
    maxDepth: <number>,
    depthField: <field>,
    restrictSearchWithMatch: <query>
  }
}
```

**示例**：
```javascript
// 递归查找所有下属
{
  $graphLookup: {
    from: "employees",
    startWith: "$ _id",
    connectFromField: "_id",
    connectToField: "managerId",
    as: "subordinates"
  }
}
```

## 聚合管道示例

### 示例1：计算每个部门的平均工资和员工数量

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

**聚合管道**：
```javascript
db.employees.aggregate([
  {
    $group: {
      _id: "$department",
      avgSalary: { $avg: "$salary" },
      count: { $sum: 1 }
    }
  },
  {
    $sort: { avgSalary: -1 }
  }
])
```

**结果**：
```javascript
[
  { _id: "技术部", avgSalary: 11000, count: 2 },
  { _id: "财务部", avgSalary: 9500, count: 1 },
  { _id: "市场部", avgSalary: 8500, count: 2 }
]
```

### 示例2：获取每个用户的最新订单

**数据**：
```javascript
// users集合
[
  { _id: 1, name: "张三" },
  { _id: 2, name: "李四" }
]

// orders集合
[
  { _id: 1, userId: 1, amount: 100, date: ISODate("2023-01-01") },
  { _id: 2, userId: 1, amount: 200, date: ISODate("2023-01-02") },
  { _id: 3, userId: 2, amount: 150, date: ISODate("2023-01-01") }
]
```

**聚合管道**：
```javascript
db.users.aggregate([
  {
    $lookup: {
      from: "orders",
      localField: "_id",
      foreignField: "userId",
      as: "orders"
    }
  },
  {
    $unwind: "$orders"
  },
  {
    $sort: { "orders.date": -1 }
  },
  {
    $group: {
      _id: "$_id",
      name: { $first: "$name" },
      latestOrder: { $first: "$orders" }
    }
  }
])
```

**结果**：
```javascript
[
  {
    _id: 1,
    name: "张三",
    latestOrder: { _id: 2, userId: 1, amount: 200, date: ISODate("2023-01-02") }
  },
  {
    _id: 2,
    name: "李四",
    latestOrder: { _id: 3, userId: 2, amount: 150, date: ISODate("2023-01-01") }
  }
]
```

### 示例3：分析用户行为数据

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

**聚合管道**：
```javascript
db.userActions.aggregate([
  {
    $group: {
      _id: "$action",
      count: { $sum: 1 },
      uniqueUsers: { $addToSet: "$userId" }
    }
  },
  {
    $addFields: {
      uniqueUserCount: { $size: "$uniqueUsers" }
    }
  },
  {
    $project: {
      _id: 1,
      count: 1,
      uniqueUserCount: 1
    }
  }
])
```

**结果**：
```javascript
[
  { _id: "view", count: 4, uniqueUserCount: 2 },
  { _id: "purchase", count: 1, uniqueUserCount: 1 }
]
```

## 聚合管道最佳实践

1. **使用$match作为第一个阶段**：减少后续阶段处理的文档数量，提高性能。

2. **合理使用索引**：为$match和$sort阶段的字段创建索引，加速查询和排序。

3. **限制文档大小**：使用$project阶段只包含必要的字段，减少数据传输和处理开销。

4. **避免大型$group操作**：如果$group操作的结果集很大，可能会导致内存不足，考虑使用分片或其他方式优化。

5. **使用$out或$merge**：对于频繁执行的聚合操作，可以将结果存储到集合中，避免重复计算。

6. **监控聚合操作**：使用explain()方法分析聚合操作的执行计划，识别性能瓶颈。

7. **设置适当的批处理大小**：对于大型聚合操作，可以设置适当的批处理大小，避免内存溢出。

## 聚合管道的限制

1. **内存限制**：默认情况下，单个聚合管道阶段的内存使用限制为100MB，可以通过设置allowDiskUse选项使用磁盘临时存储。

2. **结果大小限制**：聚合操作的结果限制为16MB，可以使用$out或$merge将结果写入集合。

3. **执行时间限制**：长时间运行的聚合操作可能会被MongoDB终止，需要合理设计管道。

4. **复杂度限制**：过于复杂的聚合管道可能会导致性能下降，需要根据实际情况进行优化。

## 总结

MongoDB的聚合管道是一种强大的数据处理工具，通过组合不同的阶段，可以实现复杂的数据转换、分析和计算。它提供了丰富的操作符，支持各种数据处理场景，从简单的过滤和排序到复杂的分组、关联和统计分析。

聚合管道的设计使得数据处理逻辑清晰易懂，同时具有良好的性能和可扩展性。在实际应用中，合理使用聚合管道可以大大简化数据处理代码，提高应用程序的性能和可维护性。