# java.time 包详细设计文档

## 一、模块概述

**包路径**: `com.linsir.abc.core.base.time`

**包含子包**:
- `local` - 本地时间
- `format` - 格式化
- `temporal` - 时间计算

**类数**: 7个

---

## 二、本地时间

**包路径**: `com.linsir.abc.core.base.time.local`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `LocalDateTimeCalculator` | 本地日期时间计算 | `plusDays()`, `minusMonths()`, `until()` |
| `InstantConverter` | 时间戳转换 | `toEpochMilli()`, `ofEpochMilli()` |

**设计要点**:
- LocalDate、LocalTime、LocalDateTime 的区别
- 日期时间的加减计算
- Instant 的时间戳表示

---

## 三、格式化

**包路径**: `com.linsir.abc.core.base.time.format`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `DateTimeFormatterBuilder` | 格式化器构建 | `appendPattern()`, `toFormatter()` |
| `IsoDateTimeParser` | ISO 日期时间解析 | `parse()`, `format()` |

**设计要点**:
- DateTimeFormatter 的线程安全
- 自定义格式化模式
- ISO-8601 标准格式

---

## 四、时间计算

**包路径**: `com.linsir.abc.core.base.time.temporal`

| 类名 | 功能描述 | 核心方法 |
|------|----------|----------|
| `TemporalAdjusterImplementation` | 时间调整器实现 | `adjustInto()` |
| `DurationCalculator` | 持续时间计算 | `between()`, `plus()`, `minus()` |
| `PeriodCalculator` | 日期间隔计算 | `between()`, `plus()`, `minus()` |

**设计要点**:
- Duration（时间）和 Period（日期）的区别
- TemporalAdjuster 的自定义调整逻辑
- ChronoUnit 的时间单位

---

## 五、完整类名列表

| 序号 | 完整类名 |
|------|----------|
| 1 | `com.linsir.abc.core.base.time.local.LocalDateTimeCalculator` |
| 2 | `com.linsir.abc.core.base.time.local.InstantConverter` |
| 3 | `com.linsir.abc.core.base.time.format.DateTimeFormatterBuilder` |
| 4 | `com.linsir.abc.core.base.time.format.IsoDateTimeParser` |
| 5 | `com.linsir.abc.core.base.time.temporal.TemporalAdjusterImplementation` |
| 6 | `com.linsir.abc.core.base.time.temporal.DurationCalculator` |
| 7 | `com.linsir.abc.core.base.time.temporal.PeriodCalculator` |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
