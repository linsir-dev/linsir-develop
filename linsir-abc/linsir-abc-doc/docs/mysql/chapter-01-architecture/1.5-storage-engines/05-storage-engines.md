# 1.5 MySQL 的存储引擎

> 本文档基于《高性能MySQL》（第3版）第1章1.5节内容整理总结

MySQL 的存储引擎是数据库的核心组件，负责数据的存储、检索、索引管理和并发控制。MySQL 采用**可插拔存储引擎架构**，允许用户根据具体应用场景选择最合适的存储引擎。不同的存储引擎在事务支持、锁机制、索引实现、崩溃恢复等方面有着显著差异。

***

## 什么是存储引擎

存储引擎是 MySQL 中用于管理数据存储和检索的底层软件组件。它定义了：
- 数据如何在磁盘上存储
- 如何建立和维护索引
- 如何执行查询和更新操作
- 如何管理并发访问和事务

```mermaid
flowchart TB
    subgraph MySQL["MySQL 服务器架构"]
        SQL["SQL 接口层"]
        Parser["解析器"]
        Optimizer["优化器"]
        Cache["查询缓存"]
    end
    
    subgraph Engines["存储引擎层"]
        InnoDB["InnoDB<br/>默认引擎"]
        MyISAM["MyISAM"]
        Memory["Memory"]
        Archive["Archive"]
        Others["其他引擎..."]
    end
    
    SQL --> Parser --> Optimizer --> Cache
    Cache --> InnoDB
    Cache --> MyISAM
    Cache --> Memory
    Cache --> Archive
    Cache --> Others
    
    style InnoDB fill:#e1f5fe
    style MyISAM fill:#fff3e0
    style Memory fill:#f3e5f5
```

### 查看支持的存储引擎

```sql
-- 查看所有支持的存储引擎
SHOW ENGINES;

-- 查看默认存储引擎
SHOW VARIABLES LIKE 'default_storage_engine';

-- 查看特定表的存储引擎
SHOW TABLE STATUS LIKE 'table_name';

-- 查看表的创建语句（包含引擎信息）
SHOW CREATE TABLE table_name;
```

***

## 1.5.1 InnoDB 存储引擎

InnoDB 是 MySQL 5.5 及以后版本的**默认存储引擎**，也是目前最广泛使用的存储引擎。它专为高并发、事务密集型应用设计。

### 核心特性

```mermaid
mindmap
  root((InnoDB<br/>核心特性))
    事务支持
      完全支持ACID
      提交/回滚
      崩溃恢复
    锁机制
      行级锁
      无锁升级
      高并发性能
    数据完整性
      外键约束
      级联操作
      参照完整性
    存储结构
      聚簇索引
      表空间管理
      缓冲池
    并发控制
      MVCC实现
      一致性非锁定读
      四种隔离级别
```

### 事务支持（ACID）

| 特性 | 说明 | 实现机制 |
|------|------|----------|
| **原子性** | 事务中的操作要么全部成功，要么全部失败回滚 | Undo Log |
| **一致性** | 事务执行前后，数据库始终处于一致状态 | 约束检查、触发器 |
| **隔离性** | 多个并发事务之间互不干扰 | MVCC、锁机制 |
| **持久性** | 事务提交后，数据永久保存 | Redo Log、Binlog |

### 存储结构

```mermaid
flowchart LR
    subgraph Table["InnoDB 表结构"]
        direction TB
        Clustered["聚簇索引<br/>Clustered Index"]
        Secondary["二级索引<br/>Secondary Index"]
    end
    
    Clustered -->|"叶子节点存储<br/>完整行数据"| Data["数据页"]
    Secondary -->|"叶子节点存储<br/>主键值"| Clustered
    
    style Clustered fill:#e3f2fd
    style Secondary fill:#f3e5f5
```

**聚簇索引特点：**
- 主键索引即聚簇索引，数据按主键顺序物理存储
- 主键查询性能极高，无需回表
- 二级索引叶子节点存储主键值，查询可能需要回表

### 适用场景

- ✅ 需要事务支持的应用（银行、电商订单）
- ✅ 高并发读写操作
- ✅ 需要外键约束的复杂关系数据库
- ✅ 对数据一致性和完整性要求高的场景

***

## 1.5.2 MyISAM 存储引擎

MyISAM 是 MySQL 5.5 之前的默认存储引擎，以其简单高效的结构在特定场景下仍有应用价值。

### 核心特性

```mermaid
mindmap
  root((MyISAM<br/>核心特性))
    存储结构
      数据和索引分离
      .frm .MYD .MYI文件
      非聚簇索引
    锁机制
      表级锁
      并发写入受限
      读操作不互斥
    索引特性
      支持全文索引
      B+树索引结构
      快速COUNT(*)
    性能特点
      读取速度快
      存储空间小
      无事务开销
```

### 存储文件结构

```mermaid
flowchart TB
    subgraph Files["MyISAM 表文件"]
        FRM[".frm 文件<br/>表结构定义"]
        MYD[".MYD 文件<br/>数据文件<br/>MyISAM Data"]
        MYI[".MYI 文件<br/>索引文件<br/>MyISAM Index"]
    end
    
    Query["查询请求"] --> MYI
    MYI -->|"获取行指针"| MYD
    MYD -->|"读取数据"| Result["返回结果"]
    
    style FRM fill:#e8f5e9
    style MYD fill:#fff3e0
    style MYI fill:#f3e5f5
```

### 与 InnoDB 对比

| 特性 | InnoDB | MyISAM |
|------|--------|--------|
| **事务支持** | ✅ 完整 ACID | ❌ 不支持 |
| **锁粒度** | 行级锁 | 表级锁 |
| **外键约束** | ✅ 支持 | ❌ 不支持 |
| **崩溃恢复** | ✅ 自动恢复 | ❌ 需手动修复 |
| **存储结构** | 聚簇索引 | 非聚簇索引 |
| **全文索引** | 5.6+ 支持 | ✅ 原生支持 |
| **COUNT(*)** | 需扫描 | ✅ 快速统计 |
| **写性能** | 高并发优秀 | 并发写受限 |
| **读性能** | 优秀 | 简单查询更快 |

### 适用场景

- ✅ 只读或读多写少的应用
- ✅ 数据仓库、报表系统
- ✅ 日志记录、统计分析
- ✅ 不需要事务的小型应用

***

## 1.5.3 MySQL 内建的其他存储引擎

除 InnoDB 和 MyISAM 外，MySQL 还内置了多种专用存储引擎：

### Memory（HEAP）引擎

```mermaid
flowchart LR
    subgraph Memory["Memory 引擎特点"]
        direction TB
        RAM["数据存储在内存中<br/>速度极快"]
        Volatile["服务器重启<br/>数据丢失"]
        Hash["支持哈希索引<br/>等值查询快"]
        TableLock["表级锁<br/>并发有限"]
    end
```

**适用场景：**
- 临时数据处理（会话存储、计算中间结果）
- 高速缓存层（热点数据缓存）
- 查找表、配置表

**配置优化：**
```sql
-- 设置最大内存表大小
SET max_heap_table_size = 64 * 1024 * 1024;  -- 64MB

-- 临时表优先使用 Memory 引擎
SET default_tmp_storage_engine = MEMORY;
```

### Archive 引擎

```mermaid
flowchart TB
    subgraph Archive["Archive 引擎特点"]
        Compress["高压缩比<br/>可达10:1"]
        AppendOnly["仅支持 INSERT/SELECT<br/>不支持 UPDATE/DELETE"]
        NoIndex["无索引<br/>全表扫描"]
        Log["日志归档<br/>审计数据"]
    end
```

**适用场景：**
- 日志归档（应用日志、审计日志）
- 历史数据存储（长期保存、很少查询）
- 合规性存储（法律要求的只读数据存档）

### CSV 引擎

```mermaid
flowchart LR
    subgraph CSV["CSV 引擎特点"]
        Text["纯文本存储<br/>CSV格式"]
        Edit["可直接编辑<br/>文本编辑器"]
        Exchange["数据交换<br/>Excel兼容"]
        NoIndex["无索引<br/>性能受限"]
    end
```

**适用场景：**
- 数据交换中转站
- 与其他系统共享数据
- 简单的导入导出

### Blackhole 引擎

```mermaid
flowchart TB
    subgraph Blackhole["Blackhole 引擎特点"]
        DevNull["类似 /dev/null<br/>接收但不存储"]
        WriteOnly["支持写入<br/>无法读取"]
        Replication["复制中继<br/>日志过滤"]
        Test["语法测试<br/>性能测试"]
    end
```

**适用场景：**
- 主从复制中的中间节点
- 测试 SQL 语句语法
- 安全审计或日志丢弃

### Federated 引擎

```mermaid
flowchart TB
    subgraph Federated["Federated 引擎特点"]
        Remote["远程表映射<br/>本地不存储"]
        CrossServer["跨服务器查询<br/>分布式访问"]
        NoData["无数据冗余<br/>实时访问"]
        Network["依赖网络<br/>延迟敏感"]
    end
```

**适用场景：**
- 跨数据库查询整合
- 分布式环境下的数据访问
- 微服务间少量数据关联

### Merge（MRG_MyISAM）引擎

将多个结构相同的 MyISAM 表逻辑合并为一个表，适用于：
- 日志分表查询
- 大数据量分表管理
- 历史数据分区访问

***

## 1.5.4 第三方存储引擎

除 MySQL 官方提供的存储引擎外，还有一些优秀的第三方存储引擎：

### TokuDB

```mermaid
flowchart TB
    subgraph TokuDB["TokuDB 特点"]
        Fractal["分形树索引<br/>高压缩率"]
        HighWrite["高写入性能<br/>优于InnoDB"]
        BigData["适合大数据<br/>TB级表"]
        Note["已停止维护<br/>谨慎使用"]
    end
```

**特点：**
- 使用分形树（Fractal Tree）索引，非 B+树
- 极高的数据压缩率（通常 5-10 倍）
- 高写入性能，适合写入密集型应用
- **注意：Percona 已停止维护 TokuDB**

### MyRocks

Facebook 开发的存储引擎，基于 RocksDB：
- 使用 LSM-Tree 结构，写放大低
- 高压缩率，节省存储空间
- 适合写入密集型、大容量数据场景

### ColumnStore

面向列式存储的引擎（MariaDB）：
- 列式存储，适合 OLAP 分析查询
- 大规模并行处理（MPP）架构
- 适合数据仓库、BI 分析场景

### NDB Cluster

MySQL Cluster 使用的存储引擎：
- 分布式内存数据库
- 高可用、高可扩展
- 实时数据访问

***

## 1.5.5 选择合适的引擎

### 存储引擎选择决策树

```mermaid
flowchart TD
    Start(["开始选择存储引擎"]) --> NeedTrans{"需要事务支持？"}
    
    NeedTrans -->|"是"| HighCon{"高并发写入？"}
    NeedTrans -->|"否"| ReadOnly{"主要是读操作？"}
    
    HighCon -->|"是"| InnoDB1["✅ InnoDB<br/>最佳选择"]
    HighCon -->|"否"| InnoDB2["✅ InnoDB<br/>依然推荐"]
    
    ReadOnly -->|"是"| NeedFullText{"需要全文索引？"}
    ReadOnly -->|"否"| TempData{"临时/缓存数据？"}
    
    NeedFullText -->|"是<br/>且MySQL<5.6"| MyISAM1["✅ MyISAM"]
    NeedFullText -->|"否"| DataSize{"数据量很大？"}
    
    TempData -->|"是"| Memory["✅ Memory<br/>或 InnoDB"]
    TempData -->|"否"| ArchiveQ{"日志归档？"}
    
    ArchiveQ -->|"是"| Archive["✅ Archive"]
    ArchiveQ -->|"否"| CSVQ{"数据交换？"}
    
    CSVQ -->|"是"| CSV["✅ CSV"]
    CSVQ -->|"否"| InnoDB3["✅ InnoDB<br/>默认选择"]
    
    DataSize -->|"是"| Archive2["✅ Archive<br/>或 InnoDB"]
    DataSize -->|"否"| MyISAM2["✅ MyISAM<br/>或 InnoDB"]
    
    style InnoDB1 fill:#e3f2fd
    style InnoDB2 fill:#e3f2fd
    style InnoDB3 fill:#e3f2fd
    style MyISAM1 fill:#fff3e0
    style MyISAM2 fill:#fff3e0
    style Memory fill:#f3e5f5
    style Archive fill:#e8f5e9
    style Archive2 fill:#e8f5e9
    style CSV fill:#fce4ec
```

### 各引擎适用场景总结

| 引擎 | 最佳适用场景 | 不推荐场景 |
|------|-------------|-----------|
| **InnoDB** | 高并发事务、OLTP系统 | 纯只读且数据量极小的场景 |
| **MyISAM** | 只读报表、数据仓库 | 高并发写入、需要事务 |
| **Memory** | 临时表、会话缓存 | 需要持久化的数据 |
| **Archive** | 日志归档、历史数据 | 频繁查询、需要索引 |
| **CSV** | 数据交换、导入导出 | 性能要求高的生产环境 |
| **Blackhole** | 复制中继、日志过滤 | 需要存储数据的场景 |

### 现代应用的最佳实践

```mermaid
mindmap
  root((存储引擎<br/>选择建议))
    默认选择
      InnoDB满足90%需求
      5.5+版本的默认引擎
    特殊场景
      大数据归档用Archive
      临时缓存用Memory
      数据交换用CSV
    避免使用
      MyISAM新项目不推荐
      TokuDB已停止维护
    混合使用
      不同表用不同引擎
      根据访问模式选择
```

**建议：**
1. **默认选择 InnoDB**：除非有特殊需求，否则优先使用 InnoDB
2. **避免 MyISAM**：新项目不建议使用，旧项目考虑迁移
3. **混合使用**：可以在一个数据库中使用多种存储引擎
4. **定期评估**：根据业务变化重新评估存储引擎选择

***

## 1.5.6 转换表的引擎

### 使用 ALTER TABLE 转换

```sql
-- 将表转换为 InnoDB
ALTER TABLE table_name ENGINE=InnoDB;

-- 将表转换为 MyISAM
ALTER TABLE table_name ENGINE=MyISAM;

-- 查看转换进度（MySQL 8.0）
SHOW PROCESSLIST;
```

### 转换过程说明

```mermaid
sequenceDiagram
    participant User as 用户
    participant MySQL as MySQL服务器
    participant Old as 原表
    participant Temp as 临时表
    participant New as 新表
    
    User->>MySQL: ALTER TABLE ... ENGINE=InnoDB
    MySQL->>Old: 获取表结构
    MySQL->>Temp: 创建新结构临时表
    loop 逐行复制数据
        MySQL->>Old: 读取数据行
        MySQL->>Temp: 插入到新表
    end
    MySQL->>Old: 重命名为备份
    MySQL->>Temp: 重命名为正式表名
    MySQL->>Old: 删除旧表
    MySQL->>User: 返回成功
```

### 转换注意事项

| 注意事项 | 说明 | 解决方案 |
|----------|------|----------|
| **锁表时间** | 转换期间会锁定表 | 选择低峰期执行 |
| **磁盘空间** | 需要额外空间存储临时表 | 确保磁盘空间充足 |
| **全文索引** | MyISAM 和 InnoDB 全文索引不兼容 | 5.6+ 需重建索引 |
| **外键约束** | MyISAM 不支持外键 | 转换前检查外键 |
| **性能影响** | 大表转换耗时长 | 使用 pt-online-schema-change |

### 批量转换脚本

```sql
-- 生成所有 MyISAM 表的转换语句
SELECT 
    CONCAT('ALTER TABLE `', table_schema, '`.`', table_name, '` ENGINE=InnoDB;') AS alter_statement
FROM information_schema.tables 
WHERE engine = 'MyISAM' 
AND table_schema NOT IN ('mysql', 'information_schema', 'performance_schema', 'sys');
```

### 在线 DDL 工具

对于大表的存储引擎转换，推荐使用在线 DDL 工具减少停机时间：

```bash
# 使用 pt-online-schema-change（Percona Toolkit）
pt-online-schema-change \
    --alter "ENGINE=InnoDB" \
    --execute \
    D=database_name,t=table_name
```

### 转换后的验证

```sql
-- 验证存储引擎已更改
SHOW TABLE STATUS LIKE 'table_name';

-- 验证表结构完整性
CHECK TABLE table_name;

-- 验证数据完整性（行数对比）
SELECT COUNT(*) FROM table_name;

-- 测试基本操作
SELECT * FROM table_name LIMIT 10;
INSERT INTO table_name ...;
UPDATE table_name SET ...;
```

***

## 总结

```mermaid
mindmap
  root((MySQL<br/>存储引擎))
    核心引擎
      InnoDB
        默认引擎
        事务支持
        行级锁
        高并发
      MyISAM
        简单高效
        表级锁
        读多写少
    专用引擎
      Memory
        内存存储
        临时数据
      Archive
        高压缩
        日志归档
      CSV
        数据交换
      Blackhole
        复制中继
    选择原则
      默认InnoDB
      按需选择
      避免MyISAM
```

### 关键要点

1. **InnoDB 是现代首选**：MySQL 5.5+ 的默认引擎，支持事务、行级锁、MVCC，适合绝大多数场景

2. **MyISAM 逐渐淘汰**：仅适用于特定只读场景，新项目不建议使用

3. **专用引擎按需使用**：Memory、Archive、CSV 等在特定场景下能发挥独特价值

4. **存储引擎可混合使用**：一个数据库中不同表可以使用不同存储引擎

5. **引擎转换需谨慎**：大表转换会锁表，需要选择合适时机或使用在线工具

### 参考资源

- [MySQL 官方文档 - 存储引擎](https://dev.mysql.com/doc/refman/8.0/en/storage-engines.html)
- [高性能MySQL（第3版）第1章](https://book.douban.com/subject/23008813/)
- [InnoDB 存储引擎详解](https://dev.mysql.com/doc/refman/8.0/en/innodb-storage-engine.html)
