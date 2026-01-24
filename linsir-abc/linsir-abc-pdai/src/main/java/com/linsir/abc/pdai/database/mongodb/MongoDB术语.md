# MongoDB术语

## 基本术语

### 1. MongoDB

MongoDB是一个开源的、跨平台的、面向文档的NoSQL数据库，由MongoDB Inc.开发和维护。

### 2. BSON

BSON（Binary JSON）是MongoDB使用的二进制数据格式，类似于JSON，但支持更多数据类型，如日期、二进制数据、32位整数、64位整数等。

### 3. 文档（Document）

文档是MongoDB中存储数据的基本单位，类似于关系型数据库中的行。文档以BSON格式存储，结构类似于JSON对象。

### 4. 集合（Collection）

集合是文档的容器，类似于关系型数据库中的表。集合中的文档可以有不同的结构，不需要预定义模式。

### 5. 数据库（Database）

数据库是集合的容器，类似于关系型数据库中的数据库。一个MongoDB实例可以包含多个数据库。

### 6. 实例（Instance）

MongoDB实例是MongoDB服务器的一个运行实例，包含一个或多个数据库。

### 7. 客户端（Client）

客户端是连接到MongoDB实例并执行操作的应用程序，如MongoDB Shell、应用程序代码等。

## 数据模型术语

### 1. 字段（Field）

字段是文档中的键值对，类似于关系型数据库中的列。

### 2. _id

_id是文档的主键，在集合中必须唯一。如果插入文档时没有指定_id字段，MongoDB会自动生成一个ObjectId类型的_id。

### 3. ObjectId

ObjectId是MongoDB自动生成的唯一标识符，由12字节组成，包含时间戳、机器ID、进程ID和计数器。

### 4. 嵌套文档（Embedded Document）

嵌套文档是文档中包含的另一个文档，用于表示复杂的数据结构。

### 5. 数组（Array）

数组是文档中包含的一个值的列表，可以包含不同类型的值，包括其他文档和数组。

### 6. 引用（Reference）

引用是文档中指向另一个文档的链接，类似于关系型数据库中的外键。

### 7. 模式（Schema）

模式是文档结构的定义，包括字段名、数据类型和约束等。MongoDB是模式灵活的，但应用程序可以定义自己的模式。

## 索引术语

### 1. 索引（Index）

索引是一种数据结构，用于加速查询操作。MongoDB支持多种类型的索引。

### 2. 单字段索引（Single Field Index）

单字段索引是基于单个字段创建的索引。

### 3. 复合索引（Compound Index）

复合索引是基于多个字段创建的索引。

### 4. 多键索引（Multikey Index）

多键索引是基于数组字段创建的索引，会为数组中的每个元素创建索引条目。

### 5. 地理空间索引（Geospatial Index）

地理空间索引用于加速地理空间查询，如查找附近的位置。

### 6. 文本索引（Text Index）

文本索引用于加速文本搜索查询。

### 7. 哈希索引（Hashed Index）

哈希索引是基于字段值的哈希值创建的索引，用于分片键。

### 8. 唯一索引（Unique Index）

唯一索引确保索引字段的值在集合中唯一。

### 9. 稀疏索引（Sparse Index）

稀疏索引只包含具有索引字段的文档，不包含没有索引字段的文档。

### 10. 部分索引（Partial Index）

部分索引只包含满足指定条件的文档，减少索引大小和提高性能。

## 查询术语

### 1. 查询（Query）

查询是从集合中检索文档的操作。

### 2. 投影（Projection）

投影是指定查询结果中包含哪些字段的操作。

### 3. 排序（Sort）

排序是指定查询结果的排序顺序的操作。

### 4. 限制（Limit）

限制是指定查询结果返回的文档数量的操作。

### 5. 跳过（Skip）

跳过是指定查询结果跳过的文档数量的操作。

### 6. 游标（Cursor）

游标是查询结果的指针，用于遍历查询结果。

### 7. 聚合（Aggregation）

聚合是对数据进行处理和转换的操作，如分组、计算、过滤等。

### 8. 聚合管道（Aggregation Pipeline）

聚合管道是一系列数据处理阶段，每个阶段接收前一个阶段的输出并产生输出。

### 9. MapReduce

MapReduce是一种数据处理模式，用于处理大量数据，包括映射（Map）、 shuffle 和 归约（Reduce）三个阶段。

### 10. 操作符（Operator）

操作符是用于构建查询条件和执行操作的符号，如$eq、$gt、$lt等。

## 复制集术语

### 1. 复制集（Replica Set）

复制集是一组MongoDB实例，包含一个主节点和多个从节点，用于提供数据冗余和高可用性。

### 2. 主节点（Primary）

主节点是复制集中接收所有写操作的节点，并将操作记录到 oplog 中。

### 3. 从节点（Secondary）

从节点是复制集中复制主节点 oplog 并应用到自己的数据集的节点，只能执行读操作。

### 4. 仲裁者（Arbiter）

仲裁者是复制集中不存储数据，只参与选举的节点，用于增加复制集的投票数。

### 5. 选举（Election）

选举是复制集在主节点故障时选择新主节点的过程。

### 6.  oplog（Operation Log）

oplog是主节点上的操作日志，记录了所有写操作，从节点通过复制 oplog 来同步数据。

### 7. 复制延迟（Replication Lag）

复制延迟是从节点与主节点之间的数据同步延迟。

### 8. 心跳（Heartbeat）

心跳是复制集成员之间定期发送的信号，用于检测成员状态。

### 9. 故障转移（Failover）

故障转移是当主节点故障时，复制集自动选举新主节点的过程。

### 10. 优先级（Priority）

优先级是复制集成员在选举中的权重，优先级越高，成为主节点的可能性越大。

### 11. 隐藏节点（Hidden Node）

隐藏节点是复制集中不接收客户端请求的节点，用于备份、监控等。

### 12. 延迟节点（Delayed Node）

延迟节点是复制集中故意延迟复制 oplog 的节点，用于灾难恢复。

### 13. 投票（Vote）

投票是复制集成员在选举中对新主节点的选择。

### 14. 多数派（Majority）

多数派是复制集中超过半数的成员，用于确定选举结果和写入关注点。

## 分片术语

### 1. 分片（Sharding）

分片是将数据分布到多个服务器上的过程，用于水平扩展MongoDB。

### 2. 分片集群（Sharded Cluster）

分片集群是由分片、配置服务器和 mongos 路由器组成的MongoDB部署。

### 3. 分片键（Shard Key）

分片键是用于确定数据分布到哪个分片的字段。

### 4. 块（Chunk）

块是分片上的基本数据单元，包含一定范围的分片键值。

### 5. 配置服务器（Config Server）

配置服务器存储分片集群的元数据，包括分片键范围、块分布等。

### 6. mongos

mongos是分片集群的路由器，负责将客户端请求路由到正确的分片。

### 7. 平衡器（Balancer）

平衡器是MongoDB的后台进程，负责在分片之间迁移块，以确保数据分布均匀。

### 8. 块迁移（Chunk Migration）

块迁移是将块从一个分片移动到另一个分片的过程，由平衡器执行。

### 9. 分片标签（Shard Tag）

分片标签是分配给分片的标识符，用于将特定范围的数据定向到特定分片。

### 10. 分割（Split）

分割是将一个块分成两个块的过程，当块大小超过配置的阈值时发生。

## 存储术语

### 1. 存储引擎（Storage Engine）

存储引擎是MongoDB用于管理数据存储和检索的组件，如WiredTiger、MMAPv1等。

### 2. WiredTiger

WiredTiger是MongoDB 3.0及以上版本的默认存储引擎，提供文档级并发控制、压缩和缓存等功能。

### 3. MMAPv1

MMAPv1是MongoDB 2.6及以下版本的默认存储引擎，使用内存映射文件进行数据存储。

### 4. 集合文件（Collection File）

集合文件是存储集合数据的文件，在WiredTiger存储引擎中，数据存储在B树中。

### 5. 命名空间（Namespace）

命名空间是集合或索引的标识符，格式为database.collection。

### 6. 预分配（Preallocation）

预分配是MongoDB为数据文件预分配空间的过程，以减少文件系统碎片。

### 7.  journa l（日志）

journal是MongoDB的预写日志，用于崩溃恢复，确保数据持久性。

### 8. 缓存（Cache）

缓存是MongoDB用于存储热点数据的内存区域，提高读写性能。

### 9. 压缩（Compression）

压缩是存储引擎对数据进行压缩的过程，减少存储空间和I/O操作。

## 管理术语

### 1. MongoDB Shell

MongoDB Shell是MongoDB的命令行界面，用于执行命令和操作数据库。

### 2. mongod

mongod是MongoDB的主进程，负责处理客户端请求和管理数据。

### 3. mongos

mongos是MongoDB分片集群的路由器，负责将客户端请求路由到正确的分片。

### 4. mongostat

mongostat是MongoDB的状态监控工具，用于显示MongoDB实例的运行状态。

### 5. mongotop

mongotop是MongoDB的性能监控工具，用于显示集合的读写时间。

### 6. mongodump

mongodump是MongoDB的备份工具，用于创建数据库的备份。

### 7. mongorestore

mongorestore是MongoDB的恢复工具，用于从备份中恢复数据库。

### 8. mongoimport

mongoimport是MongoDB的导入工具，用于从CSV、JSON等格式的文件中导入数据。

### 9. mongoexport

mongoexport是MongoDB的导出工具，用于将数据导出到CSV、JSON等格式的文件中。

### 10. 慢查询日志（Slow Query Log）

慢查询日志是MongoDB记录执行时间超过阈值的查询的日志。

## 安全术语

### 1. 认证（Authentication）

认证是验证用户身份的过程，确保只有授权用户可以访问MongoDB。

### 2. 授权（Authorization）

授权是确定用户可以执行哪些操作的过程，基于用户的角色和权限。

### 3. 角色（Role）

角色是一组权限的集合，用于授权用户。

### 4. 权限（Privilege）

权限是用户可以执行的操作，如读取、写入、管理等。

### 5. 用户（User）

用户是MongoDB中的身份，具有用户名、密码和角色。

### 6. SCRAM

SCRAM（Salted Challenge Response Authentication Mechanism）是MongoDB 3.0及以上版本使用的默认认证机制。

### 7. X.509

X.509是MongoDB支持的一种认证机制，使用SSL/TLS证书进行身份验证。

### 8. SSL/TLS

SSL（Secure Sockets Layer）和TLS（Transport Layer Security）是用于加密网络通信的协议。

### 9. 网络绑定（Network Binding）

网络绑定是指定MongoDB监听的IP地址和端口的过程。

### 10. IP白名单（IP Whitelist）

IP白名单是允许连接到MongoDB的IP地址列表。

## 其他术语

### 1. 写入关注点（Write Concern）

写入关注点是指定写入操作必须满足的条件，如复制到多少个节点。

### 2. 读取关注点（Read Concern）

读取关注点是指定读取操作返回的数据的一致性级别。

### 3. 事务（Transaction）

事务是一组原子操作，要么全部成功，要么全部失败。MongoDB 4.0及以上版本支持多文档事务。

### 4. 原子操作（Atomic Operation）

原子操作是不可分割的操作，要么全部执行，要么不执行。MongoDB的单文档操作是原子的。

### 5. 批处理（Bulk Operation）

批处理是一组操作的集合，一次性发送到服务器执行，减少网络开销。

### 6.  capped Collection（固定集合）

capped collection是大小固定的集合，当达到大小限制时，会覆盖最早的文档。

### 7. TTL Index（生存时间索引）

TTL索引是一种特殊的索引，用于自动删除过期的文档。

### 8. GridFS

GridFS是MongoDB用于存储大型文件的机制，将文件分成多个块存储。

### 9. 聚合框架（Aggregation Framework）

聚合框架是MongoDB用于数据处理和分析的工具，支持数据的转换、分组、计算等操作。

### 10. MongoDB Atlas

MongoDB Atlas是MongoDB官方提供的云服务，提供完全托管的MongoDB数据库服务。

### 11. MongoDB Compass

MongoDB Compass是MongoDB官方提供的图形化管理工具，用于查看和操作数据。

### 12. MongoDB Enterprise Advanced

MongoDB Enterprise Advanced是MongoDB的商业版本，提供额外的功能和支持。

### 13. MongoDB Community Server

MongoDB Community Server是MongoDB的开源版本，免费使用。

### 14. 版本（Version）

版本是MongoDB的发布版本，如3.6、4.0、4.4等。

### 15. 变更流（Change Stream）

变更流是MongoDB 3.6及以上版本提供的功能，用于实时监控集合的变更。