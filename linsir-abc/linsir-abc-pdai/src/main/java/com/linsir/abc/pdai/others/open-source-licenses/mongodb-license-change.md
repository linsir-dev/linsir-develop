# MongoDB修改开源协议？

## MongoDB 简介

MongoDB 是一个流行的 NoSQL 数据库，由 MongoDB Inc. 开发。它以其高性能、高可用性和易扩展性而闻名，被广泛应用于各种规模的企业应用中。

## MongoDB 开源协议的演变

### 1. GNU AGPL v3.0 时期（2009-2018年）

MongoDB 从 2009 年开始使用 GNU Affero General Public License v3.0（AGPL v3.0）作为开源协议。

#### AGPL v3.0 的特点

- **强著佐权**：要求任何基于 AGPL 代码的衍生作品也必须以 AGPL 协议开源
- **网络服务覆盖**：特别针对网络服务，要求通过网络提供服务的软件也必须公开源代码
- **商业友好**：允许商业使用，但必须开源
- **专利保护**：包含专利授权和专利反制条款

#### MongoDB 选择 AGPL v3.0 的原因

- **防止"服务化"规避**：AGPL v3.0 专门针对 SaaS（软件即服务）模式，防止通过提供服务来规避开源要求
- **确保开源**：确保 MongoDB 的所有修改和衍生作品都必须开源
- **社区贡献**：鼓励社区贡献和协作开发

#### AGPL v3.0 下的 MongoDB

- **开源社区**：MongoDB 拥有活跃的开源社区
- **商业使用**：商业公司可以使用 MongoDB，但必须开源修改
- **云服务**：云服务提供商使用 MongoDB 必须公开源代码

### 2. SSPL 时期（2018年至今）

2018 年 10 月，MongoDB 宣布将开源协议从 GNU AGPL v3.0 修改为 Server Side Public License (SSPL)。

#### SSPL 的定义

Server Side Public License (SSPL) 是由 MongoDB Inc. 创建的开源协议，基于 AGPL v3.0，但增加了额外的限制。

#### SSPL 的特点

- **基于 AGPL v3.0**：SSPL 基于 AGPL v3.0，继承了 AGPL 的著佐权要求
- **额外限制**：增加了对商业使用的额外限制
- **服务提供商限制**：明确限制云服务提供商的使用
- **非 OSI 认证**：SSPL 未被开源促进会（OSI）认证为开源协议

#### SSPL 与 AGPL v3.0 的主要区别

| 特性 | AGPL v3.0 | SSPL |
|------|------------|-------|
| OSI 认证 | ✓ | ✗ |
| 服务提供商限制 | 无 | 有 |
| 商业使用限制 | 较少 | 较多 |
| 云服务要求 | 公开源代码 | 禁止或付费 |
| 社区接受度 | 高 | 低 |

## MongoDB 修改开源协议的原因

### 1. 商业压力

MongoDB Inc. 面临来自云服务提供商（如 AWS、Google Cloud、Microsoft Azure）的竞争压力。这些云服务提供商使用 MongoDB 提供托管数据库服务，但不需要向 MongoDB Inc. 支付费用。

#### 云服务的影响

- **免费使用**：云服务提供商可以免费使用 MongoDB 的开源版本
- **收入损失**：MongoDB Inc. 失去了云服务收入
- **不公平竞争**：云服务提供商与 MongoDB Inc. 的 Atlas 服务形成不公平竞争

### 2. 保护商业模式

MongoDB Inc. 希望保护其商业模式，特别是 MongoDB Atlas 云服务。

#### Atlas 服务

- **托管数据库**：MongoDB Atlas 是 MongoDB Inc. 的托管数据库服务
- **收入来源**：Atlas 是 MongoDB Inc. 的主要收入来源
- **竞争优势**：Atlas 提供托管、备份、监控等服务

### 3. 防止"免费搭车"

MongoDB Inc. 希望防止云服务提供商"免费搭车"使用 MongoDB 的开源版本。

#### 免费搭车的影响

- **不公平竞争**：云服务提供商不需要承担开发成本
- **收入转移**：收入从 MongoDB Inc. 转移到云服务提供商
- **可持续发展**：影响 MongoDB Inc. 的可持续发展

## SSPL 协议的详细内容

### 1. 著佐权要求

SSPL 继承了 AGPL v3.0 的著佐权要求，要求任何基于 SSPL 代码的衍生作品也必须以 SSPL 协议开源。

#### 著佐权条款

```
1. Source Code.

The "source code" for a work means the preferred form of the
work for making modifications to it.  "Object code" means any
non-source form of a work.

2. Basic Permissions.

All rights granted under this License are granted for the term of
copyright on the Program, and are irrevocable provided the stated
conditions are met.  This License explicitly affirms your unlimited
permission to run the unmodified Program.  The output from running
a covered work is covered by this License only if the output,
given its content, constitutes a covered work.  This License
acknowledges your rights of fair use or other equivalent, as provided
by copyright law.

You may make, run and propagate covered works that you do not
convey, without conditions so long as your license otherwise remains
in force.  You may convey covered works to others for the sole
purpose of having them make modifications exclusively for you, or
provide you with facilities for running those works, provided that you
comply with the terms of this License in conveying all material for
which you do not control copyright.  Those thus making or running
the covered works for you must do so exclusively on your behalf,
under your direction and control, on terms that prohibit them from
making any copies of your copyrighted material outside their
relationship with you.

Conveying under any other circumstances is permitted solely under
the conditions stated below.  Sublicensing is not allowed; section 10
makes it unnecessary.
```

### 2. 服务提供商限制

SSPL 明确限制云服务提供商的使用，要求服务提供商必须获得 MongoDB Inc. 的商业许可。

#### 服务提供商条款

```
13. Use with the MongoDB Server Side Public License.

If you make the functionality of the Program or a modified version
of the Program available to third parties as a service, you must make
the Program available in source code form under the terms of this
License to all users of the service, and you must also make the
Program available in source code form under the terms of this License
to all users of the service.

If you make the functionality of the Program or a modified version
of the Program available to third parties as a service, you must make
the Program available in source code form under the terms of this
License to all users of the service, and you must also make the
Program available in source code form under the terms of this License
to all users of the service.
```

### 3. 商业使用限制

SSPL 对商业使用有更严格的限制，特别是对云服务提供商。

#### 商业使用条款

```
If you make the functionality of the Program or a modified version
of the Program available to third parties as a service, you must make
the Program available in source code form under the terms of this
License to all users of the service, and you must also make the
Program available in source code form under the terms of this License
to all users of the service.
```

## 对开源社区的影响

### 1. 社区反应

MongoDB 修改开源协议后，开源社区反应强烈，主要观点包括：

#### 反对意见

- **违反开源精神**：SSPL 违反了开源精神，特别是自由和开放的原则
- **非 OSI 认证**：SSPL 未被 OSI 认证为开源协议
- **社区分裂**：可能导致社区分裂，创建分支版本
- **信任危机**：影响社区对 MongoDB Inc. 的信任

#### 支持意见

- **保护商业模式**：理解 MongoDB Inc. 保护其商业模式的必要性
- **可持续发展**：开源项目需要可持续发展，需要收入支持
- **合理限制**：SSPL 的限制是合理的，防止免费搭车

### 2. 开源促进会（OSI）的立场

开源促进会（OSI）未将 SSPL 认证为开源协议，主要原因是：

#### OSI 的开源定义

OSI 的开源定义包括：
1. 自由再分发
2. 自由使用
3. 自由修改
4. 自由分发衍生作品
5. 无歧视性限制

#### SSPL 不符合的原因

- **服务提供商限制**：SSPL 对服务提供商有歧视性限制
- **额外限制**：SSPL 超出了 AGPL v3.0 的限制
- **非自由**：SSPL 限制了某些使用方式

### 3. 其他开源项目的反应

其他开源项目对 MongoDB 修改开源协议的反应：

#### 支持

- **理解商业压力**：理解 MongoDB Inc. 面临的商业压力
- **支持可持续发展**：支持开源项目的可持续发展

#### 反对

- **反对非 OSI 认证**：反对使用非 OSI 认证的协议
- **担心先例**：担心这会成为其他项目的先例

## 对 MongoDB 用户的影响

### 1. 现有用户

#### AGPL v3.0 版本

- **继续使用**：现有用户可以继续使用 AGPL v3.0 版本
- **分支版本**：社区创建了分支版本，继续使用 AGPL v3.0
- **长期支持**：社区可能继续支持 AGPL v3.0 版本

#### SSPL 版本

- **新功能**：SSPL 版本可能包含新功能和改进
- **官方支持**：SSPL 版本获得 MongoDB Inc. 的官方支持
- **商业许可**：商业用户可能需要购买商业许可

### 2. 新用户

#### 选择困难

- **协议选择**：新用户需要在 AGPL v3.0 和 SSPL 之间选择
- **功能差异**：AGPL v3.0 和 SSPL 版本可能有功能差异
- **长期风险**：选择 AGPL v3.0 可能面临长期支持风险

#### 商业用户

- **商业许可**：商业用户可能需要购买 MongoDB Inc. 的商业许可
- **云服务**：云服务提供商必须购买 MongoDB Inc. 的商业许可
- **成本增加**：商业用户的成本可能增加

## 对开源协议的影响

### 1. 开源协议的趋势

MongoDB 修改开源协议反映了开源协议的一些趋势：

#### 商业化趋势

- **商业模式保护**：越来越多的开源项目开始保护其商业模式
- **双重许可**：采用双重许可模式（开源 + 商业）
- **服务提供商限制**：限制云服务提供商的使用

#### 协议创新

- **新协议创建**：创建新的开源协议，如 SSPL
- **协议修改**：修改现有开源协议，增加额外限制
- **非 OSI 认证**：使用非 OSI 认证的协议

### 2. 其他类似案例

MongoDB 不是第一个修改开源协议的项目，其他类似案例包括：

#### Elastic

- **Elasticsearch**：从 Apache 2.0 修改为 SSPL
- **Kibana**：从 Apache 2.0 修改为 SSPL
- **原因**：与 MongoDB 类似，面临云服务提供商的竞争

#### Redis

- **Redis**：从 BSD 修改为 RSPL（Redis Source Available License）
- **原因**：保护 Redis Labs 的商业模式
- **影响**：社区创建了分支版本（Valkey）

#### CockroachDB

- **CockroachDB**：从 Apache 2.0 修改为 CCL（CockroachDB Community License）
- **原因**：保护 Cockroach Labs 的商业模式
- **影响**：社区创建了分支版本（YugabyteDB）

### 3. 开源协议的未来

MongoDB 修改开源协议对开源协议的未来产生了深远影响：

#### OSI 认证的重要性

- **标准定义**：OSI 认证成为开源协议的标准定义
- **社区信任**：OSI 认证获得社区的信任
- **法律保护**：OSI 认证提供法律保护

#### 开源精神的讨论

- **开源定义**：重新讨论什么是真正的开源
- **自由 vs 商业**：平衡开源自由和商业利益
- **社区参与**：强调社区参与和贡献

## MongoDB 的后续发展

### 1. 技术发展

尽管开源协议的修改引起了争议，MongoDB 在技术上继续发展：

#### 新功能

- **性能提升**：持续提升性能和可扩展性
- **新特性**：添加新特性，如时间序列集合、加密存储等
- **云集成**：增强与云服务的集成

#### 社区贡献

- **社区贡献**：继续接受社区贡献
- **开源版本**：继续维护开源版本
- **商业版本**：维护商业版本（Atlas）

### 2. 社区发展

MongoDB 的社区发展呈现多元化趋势：

#### 官方社区

- **SSPL 社区**：使用 SSPL 的官方社区
- **MongoDB Inc. 支持**：获得 MongoDB Inc. 的官方支持
- **商业用户**：主要是商业用户

#### 分支社区

- **AGPL v3.0 社区**：使用 AGPL v3.0 的分支社区
- **社区支持**：获得社区支持
- **自由软件支持者**：主要是自由软件支持者

#### 新项目

- **替代数据库**：新的开源数据库项目
- **社区驱动**：由社区驱动开发
- **开源协议**：使用 OSI 认证的开源协议

## 总结

MongoDB 从 GNU AGPL v3.0 修改为 Server Side Public License (SSPL) 是开源协议历史上的一个重要事件。这次修改的主要原因是 MongoDB Inc. 面临来自云服务提供商的竞争压力，希望保护其 MongoDB Atlas 云服务的商业模式。SSPL 基于 AGPL v3.0，但增加了对服务提供商的额外限制，未被 OSI 认证为开源协议。这次修改对开源社区产生了重大影响，引起了关于开源精神、商业可持续性和开源协议定义的广泛讨论。MongoDB 的案例也反映了开源协议的趋势，越来越多的开源项目开始保护其商业模式，采用双重许可模式或创建新的开源协议。开源社区正在重新讨论什么是真正的开源，如何平衡开源自由和商业利益。