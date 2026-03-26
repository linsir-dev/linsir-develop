# java.time 包代码说明文档

## 一、代码结构

### 1.1 包结构概览

```
com.linsir.abc.core.base.time/
├── local/                     # 本地时间
│   ├── LocalDateTimeCalculator.java   # 本地日期时间计算
│   └── InstantConverter.java          # 时间戳转换
├── format/                    # 格式化
│   ├── DateTimeFormatterBuilder.java  # 格式化器构建
│   └── IsoDateTimeParser.java         # ISO日期时间解析
└── temporal/                  # 时间计算
    ├── TemporalAdjusterImplementation.java  # 时间调整器
    ├── DurationCalculator.java          # 持续时间计算
    └── PeriodCalculator.java            # 日期间隔计算
```

### 1.2 各类详细结构

#### 1.2.1 LocalDateTimeCalculator.java

**对应JDK类**: `java.time.LocalDateTime`

**JDK源码位置**: `java.base/java/time/LocalDateTime.java`

**类结构**:
```java
public class LocalDateTimeCalculator {
    // 获取当前时间
    public LocalDateTime now()
    
    // 日期时间加减
    public LocalDateTime plusDays(LocalDateTime, long)         // 加天数
    public LocalDateTime minusMonths(LocalDateTime, long)      // 减月数
    public LocalDateTime plusYears(LocalDateTime, long)        // 加年数
    
    // 计算差值
    public Period betweenDates(LocalDate, LocalDate)           // 日期间隔
    public long daysBetween(LocalDate, LocalDate)              // 天数间隔
    public long hoursBetween(LocalDateTime, LocalDateTime)     // 小时间隔
    
    // 比较
    public boolean isBefore(LocalDateTime, LocalDateTime)      // 是否在前
    public boolean isAfter(LocalDateTime, LocalDateTime)       // 是否在后
    
    // 获取特定日期
    public LocalDate getFirstDayOfMonth(LocalDate)             // 月首日
    public LocalDate getLastDayOfMonth(LocalDate)              // 月末日
    public LocalDate getFirstDayOfYear(LocalDate)              // 年首日
}
```

**设计要点**:
- `LocalDateTime` 不包含时区信息，仅表示日期和时间
- 所有操作返回新的对象（不可变性）
- 线程安全，可在多线程环境中使用

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `now()` | `LocalDateTime.now()` | 获取当前日期时间 |
| `plusDays()` | `LocalDateTime.plusDays()` | 添加天数 |
| `minusMonths()` | `LocalDateTime.minusMonths()` | 减去月数 |
| `betweenDates()` | `Period.between()` | 计算日期间隔 |
| `daysBetween()` | `ChronoUnit.DAYS.between()` | 计算天数差 |
| `isBefore()` | `LocalDateTime.isBefore()` | 比较先后 |

#### 1.2.2 InstantConverter.java

**对应JDK类**: `java.time.Instant`

**JDK源码位置**: `java.base/java/time/Instant.java`

**类结构**:
```java
public class InstantConverter {
    // 获取当前时间戳
    public Instant now()
    
    // 与毫秒转换
    public long toEpochMilli(Instant)                          // 转毫秒
    public Instant ofEpochMilli(long)                          // 从毫秒创建
    
    // 与LocalDateTime转换
    public LocalDateTime toLocalDateTime(Instant, ZoneId)      // 转本地时间
    public Instant toInstant(LocalDateTime, ZoneId)            // 从本地时间创建
    
    // 计算
    public Instant plusSeconds(Instant, long)                  // 加秒
    public Instant minusMillis(Instant, long)                  // 减毫秒
    public Duration between(Instant, Instant)                  // 计算间隔
}
```

**设计要点**:
- `Instant` 表示时间轴上的一个点（时间戳）
- 基于UTC，与时区无关
- 适合记录事件时间、计算时间间隔

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `now()` | `Instant.now()` | 获取当前时间戳 |
| `toEpochMilli()` | `Instant.toEpochMilli()` | 转毫秒时间戳 |
| `ofEpochMilli()` | `Instant.ofEpochMilli()` | 从毫秒创建 |
| `toLocalDateTime()` | `LocalDateTime.ofInstant()` | 转本地时间 |
| `toInstant()` | `LocalDateTime.toInstant()` | 转时间戳 |
| `between()` | `Duration.between()` | 计算时间间隔 |

#### 1.2.3 DateTimeFormatterBuilder.java

**对应JDK类**: `java.time.format.DateTimeFormatter`

**JDK源码位置**: `java.base/java/time/format/DateTimeFormatter.java`

**类结构**:
```java
public class DateTimeFormatterBuilder {
    // 预定义格式化器
    public static final DateTimeFormatter ISO_FORMATTER        // ISO格式
    public static final DateTimeFormatter BASIC_FORMATTER      // 基础格式
    public static final DateTimeFormatter CHINESE_FORMATTER    // 中文格式
    
    // 格式化与解析
    public String format(LocalDateTime, DateTimeFormatter)     // 格式化
    public LocalDateTime parse(String, DateTimeFormatter)      // 解析
    
    // 创建格式化器
    public DateTimeFormatter createFormatter(String)           // 自定义模式
    public DateTimeFormatter createLocalizedFormatter(FormatStyle, FormatStyle, Locale)  // 本地化
}
```

**设计要点**:
- `DateTimeFormatter` 是线程安全的，可重复使用
- 支持自定义格式化模式（如 `yyyy-MM-dd HH:mm:ss`）
- 支持本地化格式

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `ISO_FORMATTER` | `DateTimeFormatter.ISO_LOCAL_DATE_TIME` | ISO标准格式 |
| `format()` | `DateTimeFormatter.format()` | 格式化日期时间 |
| `parse()` | `DateTimeFormatter.parse()` | 解析字符串 |
| `createFormatter()` | `DateTimeFormatter.ofPattern()` | 自定义模式 |
| `createLocalizedFormatter()` | `DateTimeFormatter.ofLocalizedDateTime()` | 本地化格式 |

**常用格式化模式**:
| 模式 | 说明 | 示例 |
|------|------|------|
| yyyy | 年 | 2024 |
| MM | 月 | 03 |
| dd | 日 | 26 |
| HH | 时（24小时制） | 14 |
| mm | 分 | 30 |
| ss | 秒 | 00 |
| SSS | 毫秒 | 000 |

#### 1.2.4 IsoDateTimeParser.java

**对应JDK类**: `java.time.LocalDateTime`, `java.time.ZonedDateTime`, `java.time.OffsetDateTime`

**JDK源码位置**: 
- `java.base/java/time/LocalDateTime.java`
- `java.base/java/time/ZonedDateTime.java`
- `java.base/java/time/OffsetDateTime.java`

**类结构**:
```java
public class IsoDateTimeParser {
    // ISO格式解析
    public LocalDateTime parseIsoDateTime(String)              // 解析日期时间
    public LocalDate parseIsoDate(String)                      // 解析日期
    public LocalTime parseIsoTime(String)                      // 解析时间
    
    // ISO格式格式化
    public String formatIsoDateTime(LocalDateTime)             // 格式化日期时间
    public String formatIsoDate(LocalDate)                     // 格式化日期
    public String formatIsoTime(LocalTime)                     // 格式化时间
    
    // 带时区
    public ZonedDateTime parseIsoZonedDateTime(String)         // 解析带时区
    public OffsetDateTime parseIsoOffsetDateTime(String)       // 解析带偏移
}
```

**设计要点**:
- ISO-8601 是国际标准化组织制定的日期时间表示标准
- 标准格式：`2024-03-26T14:30:00`
- 带时区格式：`2024-03-26T14:30:00+08:00`

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `parseIsoDateTime()` | `LocalDateTime.parse()` | 解析ISO日期时间 |
| `parseIsoDate()` | `LocalDate.parse()` | 解析ISO日期 |
| `parseIsoTime()` | `LocalTime.parse()` | 解析ISO时间 |
| `parseIsoZonedDateTime()` | `ZonedDateTime.parse()` | 解析带时区 |
| `parseIsoOffsetDateTime()` | `OffsetDateTime.parse()` | 解析带偏移 |
| `formatIsoDateTime()` | `DateTimeFormatter.ISO_LOCAL_DATE_TIME.format()` | ISO格式化 |

#### 1.2.5 TemporalAdjusterImplementation.java

**对应JDK类**: `java.time.temporal.TemporalAdjusters`

**JDK源码位置**: `java.base/java/time/temporal/TemporalAdjusters.java`

**类结构**:
```java
public class TemporalAdjusterImplementation {
    // 常用调整器
    public TemporalAdjuster firstDayOfMonth()                  // 月首日
    public TemporalAdjuster lastDayOfMonth()                   // 月末日
    public TemporalAdjuster firstDayOfNextMonth()              // 下月首日
    public TemporalAdjuster firstDayOfYear()                   // 年首日
    public TemporalAdjuster lastDayOfYear()                    // 年末日
    public TemporalAdjuster next(DayOfWeek)                    // 下一个星期几
    public TemporalAdjuster nextOrSame(DayOfWeek)              // 下一个或当天
    
    // 自定义调整器
    public TemporalAdjuster nextWorkingDay()                   // 下一个工作日
    public TemporalAdjuster nthWeekdayOfMonth(int, DayOfWeek)  // 第N个星期几
}
```

**设计要点**:
- `TemporalAdjuster` 用于调整日期时间到特定值
- 内置常用调整器（如月末、年初等）
- 支持自定义调整逻辑

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `firstDayOfMonth()` | `TemporalAdjusters.firstDayOfMonth()` | 月首日调整器 |
| `lastDayOfMonth()` | `TemporalAdjusters.lastDayOfMonth()` | 月末日调整器 |
| `firstDayOfYear()` | `TemporalAdjusters.firstDayOfYear()` | 年首日调整器 |
| `lastDayOfYear()` | `TemporalAdjusters.lastDayOfYear()` | 年末日调整器 |
| `next()` | `TemporalAdjusters.next()` | 下一个星期几 |
| `nextOrSame()` | `TemporalAdjusters.nextOrSame()` | 下一个或当天 |
| `nextWorkingDay()` | 自定义实现 | 下一个工作日 |

#### 1.2.6 DurationCalculator.java

**对应JDK类**: `java.time.Duration`

**JDK源码位置**: `java.base/java/time/Duration.java`

**类结构**:
```java
public class DurationCalculator {
    // 计算间隔
    public Duration between(LocalTime, LocalTime)              // 时间间隔
    public Duration betweenInstant(Instant, Instant)           // 时间戳间隔
    
    // 创建Duration
    public Duration ofHours(long)                              // 小时
    public Duration ofMinutes(long)                            // 分钟
    public Duration ofSeconds(long)                          // 秒
    public Duration ofMillis(long)                             // 毫秒
    
    // 加减
    public Duration plus(Duration, Duration)                   // 加
    public Duration minus(Duration, Duration)                  // 减
    
    // 转换
    public long toHours(Duration)                              // 转小时
    public long toMinutes(Duration)                            // 转分钟
    public long toSeconds(Duration)                            // 转秒
    public long toMillis(Duration)                             // 转毫秒
    
    // 工具方法
    public static <T> T measureExecutionTime(Task<T>)          // 测量执行时间
}
```

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `between()` | `Duration.between()` | 计算两个时间的间隔 |
| `ofHours()` | `Duration.ofHours()` | 创建小时Duration |
| `ofMinutes()` | `Duration.ofMinutes()` | 创建分钟Duration |
| `ofSeconds()` | `Duration.ofSeconds()` | 创建秒Duration |
| `ofMillis()` | `Duration.ofMillis()` | 创建毫秒Duration |
| `toHours()` | `Duration.toHours()` | 转换为小时 |
| `toMinutes()` | `Duration.toMinutes()` | 转换为分钟 |
| `toMillis()` | `Duration.toMillis()` | 转换为毫秒 |
| `measureExecutionTime()` | 自定义实现 | 测量执行时间 |

**设计要点**:
- `Duration` 表示以秒和纳秒为单位的时间间隔
- 适合表示短时间间隔（如任务执行时间）
- 不包含日期信息

#### 1.2.7 PeriodCalculator.java

**对应JDK类**: `java.time.Period`

**JDK源码位置**: `java.base/java/time/Period.java`

**类结构**:
```java
public class PeriodCalculator {
    // 计算间隔
    public Period between(LocalDate, LocalDate)                // 日期间隔
    
    // 创建Period
    public Period ofYears(int)                                 // 年
    public Period ofMonths(int)                                // 月
    public Period ofWeeks(int)                                 // 周
    public Period ofDays(int)                                  // 天
    public Period of(int, int, int)                            // 年月日
    
    // 加减
    public Period plus(Period, Period)                         // 加
    public Period minus(Period, Period)                        // 减
    
    // 获取
    public int getYears(Period)                                // 年数
    public int getMonths(Period)                               // 月数
    public int getDays(Period)                                 // 天数
    
    // 标准化
    public Period normalized(Period)                           // 标准化（如13月转1年1月）
}
```

**JDK对应方法**:
| 本类方法 | JDK方法 | 说明 |
|----------|---------|------|
| `between()` | `Period.between()` | 计算两个日期的间隔 |
| `ofYears()` | `Period.ofYears()` | 创建年Period |
| `ofMonths()` | `Period.ofMonths()` | 创建月Period |
| `ofWeeks()` | `Period.ofWeeks()` | 创建周Period |
| `ofDays()` | `Period.ofDays()` | 创建日Period |
| `of()` | `Period.of()` | 创建年月日Period |
| `getYears()` | `Period.getYears()` | 获取年数 |
| `getMonths()` | `Period.getMonths()` | 获取月数 |
| `getDays()` | `Period.getDays()` | 获取天数 |
| `normalized()` | `Period.normalized()` | 标准化Period |

**Duration vs Period**:

| 特性 | Duration | Period |
|------|----------|--------|
| 单位 | 秒、纳秒 | 年、月、日 |
| 适用场景 | 时间间隔 | 日期间隔 |
| 示例 | 任务执行2小时30分 | 项目持续3个月15天 |
| 计算方式 | 固定时间长度 | 日历计算（考虑闰年等） |

---

## 二、代码使用场景

### 2.1 LocalDateTimeCalculator - 本地日期时间计算

**适用场景**:
- 业务系统中的日期时间计算
- 日历功能开发
- 日期时间比较和判断

**使用示例**:
```java
LocalDateTimeCalculator calculator = new LocalDateTimeCalculator();

// 获取当前时间
LocalDateTime now = calculator.now();

// 日期加减
LocalDateTime future = calculator.plusDays(now, 7);     // 7天后
LocalDateTime past = calculator.minusMonths(now, 3);    // 3个月前

// 计算间隔
LocalDate start = LocalDate.of(2024, 1, 1);
LocalDate end = LocalDate.of(2024, 3, 26);
Period period = calculator.betweenDates(start, end);    // P2M25D
long days = calculator.daysBetween(start, end);         // 85天

// 比较
boolean isBefore = calculator.isBefore(start, end);     // true

// 获取特定日期
LocalDate firstDay = calculator.getFirstDayOfMonth(now);  // 本月1日
LocalDate lastDay = calculator.getLastDayOfMonth(now);    // 本月最后一日
```

### 2.2 InstantConverter - 时间戳转换

**适用场景**:
- 记录事件发生时间
- 计算时间间隔
- 与数据库时间戳交互
- 系统日志时间记录

**使用示例**:
```java
InstantConverter converter = new InstantConverter();

// 获取当前时间戳
Instant now = converter.now();

// 与毫秒转换
long millis = converter.toEpochMilli(now);
Instant fromMillis = converter.ofEpochMilli(millis);

// 与LocalDateTime转换
ZoneId zone = ZoneId.of("Asia/Shanghai");
LocalDateTime local = converter.toLocalDateTime(now, zone);
Instant instant = converter.toInstant(local, zone);

// 计算间隔
Instant start = Instant.now();
// ... 执行某些操作
Instant end = Instant.now();
Duration duration = converter.between(start, end);
```

### 2.3 DateTimeFormatterBuilder - 日期时间格式化

**适用场景**:
- 日期时间显示格式化
- 用户输入解析
- 不同地区本地化显示
- 日志时间格式化

**使用示例**:
```java
DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
LocalDateTime now = LocalDateTime.now();

// 使用预定义格式化器
String iso = builder.format(now, DateTimeFormatterBuilder.ISO_FORMATTER);
String basic = builder.format(now, DateTimeFormatterBuilder.BASIC_FORMATTER);
String chinese = builder.format(now, DateTimeFormatterBuilder.CHINESE_FORMATTER);

// 自定义格式化
DateTimeFormatter custom = builder.createFormatter("yyyy/MM/dd HH:mm");
String formatted = builder.format(now, custom);

// 本地化格式化
DateTimeFormatter us = builder.createLocalizedFormatter(
    FormatStyle.MEDIUM, FormatStyle.SHORT, Locale.US);

// 解析
LocalDateTime parsed = builder.parse("2024-03-26 14:30:00", 
    DateTimeFormatterBuilder.BASIC_FORMATTER);
```

### 2.4 IsoDateTimeParser - ISO日期时间解析

**适用场景**:
- 处理ISO标准格式的时间字符串
- 与外部系统交互（JSON、XML等）
- 日志解析
- API接口时间处理

**使用示例**:
```java
IsoDateTimeParser parser = new IsoDateTimeParser();

// 解析ISO格式
LocalDateTime dt = parser.parseIsoDateTime("2024-03-26T14:30:00");
LocalDate date = parser.parseIsoDate("2024-03-26");
LocalTime time = parser.parseIsoTime("14:30:00");

// 解析带时区
ZonedDateTime zdt = parser.parseIsoZonedDateTime("2024-03-26T14:30:00+08:00");

// 格式化为ISO格式
String iso = parser.formatIsoDateTime(LocalDateTime.now());
```

### 2.5 TemporalAdjusterImplementation - 时间调整器

**适用场景**:
- 获取特定日期（如月末、年初）
- 计算下一个工作日
- 日历应用开发
- 报表统计日期范围

**使用示例**:
```java
TemporalAdjusterImplementation adjuster = new TemporalAdjusterImplementation();

LocalDate date = LocalDate.of(2024, 3, 26);

// 使用内置调整器
LocalDate firstDayOfMonth = date.with(adjuster.firstDayOfMonth());
LocalDate lastDayOfMonth = date.with(adjuster.lastDayOfMonth());
LocalDate nextMonday = date.with(adjuster.next(DayOfWeek.MONDAY));

// 自定义调整器
LocalDate nextWorkingDay = date.with(adjuster.nextWorkingDay());
LocalDate secondFriday = date.with(adjuster.nthWeekdayOfMonth(2, DayOfWeek.FRIDAY));
```

### 2.6 DurationCalculator - 持续时间计算

**适用场景**:
- 测量任务执行时间
- 超时控制
- 性能测试
- 定时任务

**使用示例**:
```java
DurationCalculator calculator = new DurationCalculator();

// 计算时间间隔
LocalTime start = LocalTime.of(9, 0);
LocalTime end = LocalTime.of(17, 30);
Duration workHours = calculator.between(start, end);

// 创建Duration
Duration twoHours = calculator.ofHours(2);
Duration thirtyMinutes = calculator.ofMinutes(30);

// 转换
long minutes = calculator.toMinutes(workHours);  // 510分钟

// 测量任务执行时间
String result = DurationCalculator.measureExecutionTime(() -> {
    // 执行耗时操作
    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    return "完成";
});
```

### 2.7 PeriodCalculator - 日期间隔计算

**适用场景**:
- 计算年龄
- 计算项目周期
- 会员有效期计算
- 合同期限计算

**使用示例**:
```java
PeriodCalculator calculator = new PeriodCalculator();

// 计算日期间隔
LocalDate birthDate = LocalDate.of(1990, 5, 15);
LocalDate today = LocalDate.now();
Period age = calculator.between(birthDate, today);
System.out.println("年龄: " + age.getYears() + "岁");

// 创建Period
Period threeMonths = calculator.ofMonths(3);
Period oneYearSixMonths = calculator.of(1, 6, 0);

// 日期加减
LocalDate future = today.plus(oneYearSixMonths);
```

---

## 三、测试代码结构

### 3.1 测试包结构

```
src/test/java/com/linsir/abc/core/base/time/
├── local/
│   ├── LocalDateTimeCalculatorTest.java   # 本地日期时间计算测试
│   └── InstantConverterTest.java          # 时间戳转换测试
├── format/
│   ├── DateTimeFormatterBuilderTest.java  # 格式化器构建测试
│   └── IsoDateTimeParserTest.java         # ISO日期时间解析测试
└── temporal/
    ├── TemporalAdjusterImplementationTest.java  # 时间调整器测试
    ├── DurationCalculatorTest.java          # 持续时间计算测试
    └── PeriodCalculatorTest.java            # 日期间隔计算测试
```

### 3.2 测试类结构示例

#### LocalDateTimeCalculatorTest.java

```java
public class LocalDateTimeCalculatorTest {
    // 基础测试
    @Test void testNow()                       // 获取当前时间
    @Test void testPlusOperations()            // 加法操作
    @Test void testMinusOperations()           // 减法操作
    
    // 间隔计算测试
    @Test void testBetweenDates()              // 日期间隔
    @Test void testDaysBetween()               // 天数间隔
    @Test void testHoursBetween()              // 小时间隔
    
    // 比较测试
    @Test void testCompareDateTimes()          // 日期时间比较
    
    // 特定日期测试
    @Test void testGetFirstDayOfMonth()        // 月首日
    @Test void testGetLastDayOfMonth()         // 月末日
}
```

#### DateTimeFormatterBuilderTest.java

```java
public class DateTimeFormatterBuilderTest {
    // 格式化测试
    @Test void testFormatWithIsoFormatter()    // ISO格式化
    @Test void testFormatWithBasicFormatter()  // 基础格式化
    @Test void testFormatWithChineseFormatter() // 中文格式化
    
    // 解析测试
    @Test void testParseDateTime()             // 解析日期时间
    
    // 自定义测试
    @Test void testCreateCustomFormatter()     // 自定义格式化器
    @Test void testCreateLocalizedFormatter()  // 本地化格式化器
}
```

#### DurationCalculatorTest.java

```java
public class DurationCalculatorTest {
    // 计算测试
    @Test void testBetween()                   // 计算间隔
    @Test void testBetweenInstant()            // 时间戳间隔
    
    // 创建测试
    @Test void testOfHours()                   // 小时
    @Test void testOfMinutes()                 // 分钟
    @Test void testOfSeconds()                 // 秒
    
    // 转换测试
    @Test void testToHours()                   // 转小时
    @Test void testToMinutes()                 // 转分钟
    @Test void testToMillis()                  // 转毫秒
    
    // 工具测试
    @Test void testMeasureExecutionTime()      // 测量执行时间
}
```

---

## 四、单元测试预期结果

### 4.1 LocalDateTimeCalculatorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testNow` | ✅ PASS | 当前时间不为null，在合理范围内 |
| `testPlusOperations` | ✅ PASS | 加法计算正确 |
| `testMinusOperations` | ✅ PASS | 减法计算正确 |
| `testBetweenDates` | ✅ PASS | 日期间隔计算正确 |
| `testDaysBetween` | ✅ PASS | 天数间隔计算正确 |
| `testHoursBetween` | ✅ PASS | 小时间隔计算正确 |
| `testCompareDateTimes` | ✅ PASS | 比较结果正确 |
| `testGetFirstDayOfMonth` | ✅ PASS | 月首日正确 |
| `testGetLastDayOfMonth` | ✅ PASS | 月末日正确 |

### 4.2 InstantConverterTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testNow` | ✅ PASS | 当前时间戳不为null |
| `testToEpochMilli` | ✅ PASS | 毫秒转换正确 |
| `testOfEpochMilli` | ✅ PASS | 从毫秒创建正确 |
| `testToLocalDateTime` | ✅ PASS | 时区转换正确 |
| `testToInstant` | ✅ PASS | 本地时间转时间戳正确 |
| `testBetween` | ✅ PASS | 间隔计算正确 |

### 4.3 DateTimeFormatterBuilderTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testFormatWithIsoFormatter` | ✅ PASS | ISO格式化正确 |
| `testFormatWithBasicFormatter` | ✅ PASS | 基础格式化正确 |
| `testFormatWithChineseFormatter` | ✅ PASS | 中文格式化正确 |
| `testParseDateTime` | ✅ PASS | 解析正确 |
| `testCreateCustomFormatter` | ✅ PASS | 自定义格式化器工作正常 |
| `testCreateLocalizedFormatter` | ✅ PASS | 本地化格式化正确 |

### 4.4 IsoDateTimeParserTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testParseIsoDateTime` | ✅ PASS | 日期时间解析正确 |
| `testParseIsoDate` | ✅ PASS | 日期解析正确 |
| `testParseIsoTime` | ✅ PASS | 时间解析正确 |
| `testParseIsoZonedDateTime` | ✅ PASS | 带时区解析正确 |
| `testFormatIsoDateTime` | ✅ PASS | ISO格式化正确 |

### 4.5 TemporalAdjusterImplementationTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testFirstDayOfMonth` | ✅ PASS | 月首日调整正确 |
| `testLastDayOfMonth` | ✅ PASS | 月末日调整正确 |
| `testNext` | ✅ PASS | 下一个星期几正确 |
| `testNextWorkingDay` | ✅ PASS | 下一个工作日正确 |
| `testNthWeekdayOfMonth` | ✅ PASS | 第N个星期几正确 |

### 4.6 DurationCalculatorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testBetween` | ✅ PASS | 时间间隔计算正确 |
| `testBetweenInstant` | ✅ PASS | 时间戳间隔计算正确 |
| `testOfHours` | ✅ PASS | 小时Duration创建正确 |
| `testOfMinutes` | ✅ PASS | 分钟Duration创建正确 |
| `testToHours` | ✅ PASS | 转小时正确 |
| `testToMillis` | ✅ PASS | 转毫秒正确 |
| `testMeasureExecutionTime` | ✅ PASS | 执行时间测量正确 |

### 4.7 PeriodCalculatorTest 预期结果

| 测试方法 | 预期结果 | 说明 |
|----------|----------|------|
| `testBetween` | ✅ PASS | 日期间隔计算正确 |
| `testOfYears` | ✅ PASS | 年Period创建正确 |
| `testOfMonths` | ✅ PASS | 月Period创建正确 |
| `testOfDays` | ✅ PASS | 日Period创建正确 |
| `testPlus` | ✅ PASS | Period加法正确 |
| `testNormalized` | ✅ PASS | 标准化正确 |

### 4.8 所有测试汇总

| 测试类 | 测试数 | 预期通过率 |
|--------|--------|------------|
| LocalDateTimeCalculatorTest | 9 | 100% |
| InstantConverterTest | 6 | 100% |
| DateTimeFormatterBuilderTest | 6 | 100% |
| IsoDateTimeParserTest | 5 | 100% |
| TemporalAdjusterImplementationTest | 5 | 100% |
| DurationCalculatorTest | 7 | 100% |
| PeriodCalculatorTest | 6 | 100% |
| **总计** | **44** | **100%** |

---

## 五、运行测试

### 5.1 运行所有time包测试

```bash
mvn test -Dtest="com.linsir.abc.core.base.time.**"
```

### 5.2 运行单个测试类

```bash
mvn test -Dtest=LocalDateTimeCalculatorTest
```

### 5.3 预期输出

```
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.linsir.abc.core.base.time.local.LocalDateTimeCalculatorTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
...
[INFO] BUILD SUCCESS
```

---

## 六、时间API选择指南

### 6.1 按场景选择

| 场景 | 推荐类 | 说明 |
|------|--------|------|
| 业务日期计算 | LocalDateTimeCalculator | 不含时区，纯日期时间计算 |
| 时间戳记录 | InstantConverter | 精确到纳秒的时间点 |
| 格式化显示 | DateTimeFormatterBuilder | 灵活的格式化选项 |
| ISO标准交互 | IsoDateTimeParser | 标准格式解析 |
| 日历调整 | TemporalAdjusterImplementation | 特定日期获取 |
| 执行时间测量 | DurationCalculator | 精确时间间隔 |
| 日期间隔 | PeriodCalculator | 年月日计算 |

### 6.2 类对比

| 类 | 表示内容 | 是否含时区 | 主要用途 |
|----|----------|------------|----------|
| LocalDate | 日期 | 否 | 生日、纪念日 |
| LocalTime | 时间 | 否 | 营业时间、闹钟 |
| LocalDateTime | 日期+时间 | 否 | 会议时间、日程 |
| Instant | 时间戳 | 是（UTC） | 日志、事件记录 |
| ZonedDateTime | 日期时间+时区 | 是 | 跨国会议、航班 |
| Duration | 时间间隔 | - | 任务耗时、超时 |
| Period | 日期间隔 | - | 年龄、有效期 |

### 6.3 注意事项

1. **时区处理**: `LocalDateTime` 不含时区，需要时区时使用 `ZonedDateTime`
2. **不可变性**: 所有时间类都是不可变的，操作返回新对象
3. **线程安全**: `DateTimeFormatter` 是线程安全的，可重复使用
4. **null处理**: 时间API方法通常不接受null，需要提前检查
5. **闰年处理**: Period会自动处理闰年和月份天数差异

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26
