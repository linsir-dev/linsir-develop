# java.time 包面试题汇总

## 目录

1. [Java 8 新时间 API 概述](#一java-8-新时间-api-概述)
2. [本地日期时间](#二本地日期时间)
3. [时区处理](#三时区处理)
4. [时间戳与 Instant](#四时间戳与-instant)
5. [格式化与解析](#五格式化与解析)
6. [时间计算](#六时间计算)
7. [新旧 API 对比](#七新旧-api-对比)

---

## 一、Java 8 新时间 API 概述

### 1.1 为什么需要新的时间 API？

**问题**: Java 8 为什么要引入新的时间 API？旧的 Date/Calendar 有什么问题？

**答案**:

**旧 API（Date/Calendar）的问题**:

| 问题 | 说明 |
|------|------|
| **设计缺陷** | `Date` 类命名不准确，实际上包含时间信息 |
| **线程不安全** | `SimpleDateFormat`、`Calendar` 非线程安全 |
| **可变对象** | 日期对象可修改，容易产生 Bug |
| **时区混乱** | 时区处理复杂，容易出错 |
| **月份从 0 开始** | `Calendar.JANUARY = 0`，不符合直觉 |
| **API 设计差** | 方法命名不规范，使用困难 |

**新 API（java.time）的优势**:

| 优势 | 说明 |
|------|------|
| **不可变性** | 所有对象都是不可变的，线程安全 |
| **清晰的分离** | 日期、时间、时区明确分离 |
| **符合 ISO 标准** | 遵循 ISO-8601 国际标准 |
| **易于使用** | 方法命名直观，链式调用 |
| **向后兼容** | 提供与旧 API 的转换方法 |

**核心包结构**:
```
java.time
├── LocalDate        # 本地日期（年月日）
├── LocalTime        # 本地时间（时分秒）
├── LocalDateTime    # 本地日期时间
├── Instant          # 时间戳（UTC）
├── ZonedDateTime    # 带时区的日期时间
├── OffsetDateTime   # 带偏移量的日期时间
└── Duration/Period  # 时间间隔

java.time.format
└── DateTimeFormatter # 日期时间格式化

java.time.zone
└── ZoneId/ZoneOffset # 时区信息
```

---

### 1.2 java.time 核心类概览

**问题**: java.time 包中有哪些核心类？它们之间的关系是什么？

**答案**:

**核心类**:

| 类 | 说明 | 示例 |
|-----|------|------|
| `LocalDate` | 日期（年月日） | 2024-03-15 |
| `LocalTime` | 时间（时分秒纳秒） | 14:30:00 |
| `LocalDateTime` | 日期时间 | 2024-03-15T14:30:00 |
| `Instant` | 时间戳（UTC） | 2024-03-15T06:30:00Z |
| `ZonedDateTime` | 带时区的日期时间 | 2024-03-15T14:30+08:00[Asia/Shanghai] |
| `OffsetDateTime` | 带偏移量的日期时间 | 2024-03-15T14:30+08:00 |
| `Duration` | 时间间隔（秒、纳秒） | PT2H30M |
| `Period` | 日期间隔（年月日） | P1Y2M3D |

**关系图**:
```
LocalDate + LocalTime = LocalDateTime
                                │
                                ↓
                    + ZoneId = ZonedDateTime
                    + ZoneOffset = OffsetDateTime
                                │
                                ↓
                        toInstant() = Instant (UTC)
```

**转换关系**:
```java
// LocalDate + LocalTime → LocalDateTime
LocalDate date = LocalDate.now();
LocalTime time = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.of(date, time);

// LocalDateTime + ZoneId → ZonedDateTime
ZonedDateTime zdt = dateTime.atZone(ZoneId.of("Asia/Shanghai"));

// ZonedDateTime → Instant
Instant instant = zdt.toInstant();

// Instant → LocalDateTime
LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
```

---

## 二、本地日期时间

### 2.1 LocalDate、LocalTime、LocalDateTime

**问题**: `LocalDate`、`LocalTime`、`LocalDateTime` 有什么区别？如何使用？

**答案**:

**区别**:

| 类 | 包含信息 | 使用场景 |
|-----|----------|----------|
| `LocalDate` | 年、月、日 | 生日、纪念日、只关心日期 |
| `LocalTime` | 时、分、秒、纳秒 | 营业时间、闹钟、只关心时间 |
| `LocalDateTime` | 年、月、日、时、分、秒 | 会议时间、日志时间戳 |

**创建方式**:
```java
// 当前日期时间
LocalDate date = LocalDate.now();
LocalTime time = LocalTime.now();
LocalDateTime dateTime = LocalDateTime.now();

// 指定值创建
LocalDate date = LocalDate.of(2024, 3, 15);
LocalTime time = LocalTime.of(14, 30, 0);
LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 14, 30);

// 从字符串解析
LocalDate date = LocalDate.parse("2024-03-15");
LocalTime time = LocalTime.parse("14:30:00");
LocalDateTime dateTime = LocalDateTime.parse("2024-03-15T14:30:00");
```

**常用方法**:
```java
LocalDateTime now = LocalDateTime.now();

// 获取字段
int year = now.getYear();        // 2024
int month = now.getMonthValue(); // 3
int day = now.getDayOfMonth();   // 15
DayOfWeek week = now.getDayOfWeek(); // FRIDAY

// 日期计算（返回新对象，原对象不变）
LocalDateTime tomorrow = now.plusDays(1);
LocalDateTime lastMonth = now.minusMonths(1);
LocalDateTime nextYear = now.plusYears(1);

// 修改字段
LocalDateTime withHour = now.withHour(10);
LocalDateTime withDay = now.withDayOfMonth(1);

// 比较
boolean isBefore = now.isBefore(tomorrow);
boolean isAfter = now.isAfter(yesterday);
```

---

### 2.2 日期时间的加减计算

**问题**: 如何对日期时间进行加减计算？

**答案**:

**加减方法**:

| 方法 | 说明 |
|------|------|
| `plusYears(n)` / `minusYears(n)` | 加减年 |
| `plusMonths(n)` / `minusMonths(n)` | 加减月 |
| `plusDays(n)` / `minusDays(n)` | 加减日 |
| `plusHours(n)` / `minusHours(n)` | 加减时 |
| `plusMinutes(n)` / `minusMinutes(n)` | 加减分 |
| `plusSeconds(n)` / `minusSeconds(n)` | 加减秒 |
| `plusWeeks(n)` / `minusWeeks(n)` | 加减周 |

**代码示例**:
```java
LocalDateTime now = LocalDateTime.now();

// 常用计算
LocalDateTime tomorrow = now.plusDays(1);
LocalDateTime lastWeek = now.minusWeeks(1);
LocalDateTime nextMonth = now.plusMonths(1);
LocalDateTime threeHoursLater = now.plusHours(3);

// 链式调用
LocalDateTime complex = now
    .plusYears(1)
    .minusMonths(2)
    .plusDays(3);

// 注意：月份计算会自动调整日期
LocalDate jan31 = LocalDate.of(2024, 1, 31);
LocalDate feb29 = jan31.plusMonths(1); // 2024-02-29（闰年）
```

---

### 2.3 日期时间的比较

**问题**: 如何比较两个日期时间？

**答案**:

**比较方法**:

```java
LocalDate date1 = LocalDate.of(2024, 3, 15);
LocalDate date2 = LocalDate.of(2024, 3, 20);

// isBefore / isAfter / isEqual
boolean before = date1.isBefore(date2);  // true
boolean after = date1.isAfter(date2);    // false
boolean equal = date1.isEqual(date2);    // false

// compareTo（返回负数、0、正数）
int result = date1.compareTo(date2); // -1

// equals
boolean same = date1.equals(date2); // false

// 计算日期间隔
long daysBetween = ChronoUnit.DAYS.between(date1, date2); // 5
long monthsBetween = ChronoUnit.MONTHS.between(date1, date2); // 0
```

**排序示例**:
```java
List<LocalDate> dates = Arrays.asList(
    LocalDate.of(2024, 3, 15),
    LocalDate.of(2024, 1, 20),
    LocalDate.of(2024, 5, 10)
);

// 自然排序（升序）
dates.sort(Comparator.naturalOrder());

// 降序排序
dates.sort(Comparator.reverseOrder());
```

---

## 三、时区处理

### 3.1 ZoneId 和 ZoneOffset

**问题**: `ZoneId` 和 `ZoneOffset` 有什么区别？

**答案**:

**ZoneId**:
- 表示**时区 ID**（如 "Asia/Shanghai"）
- 包含时区规则和夏令时信息
- 推荐使用

**ZoneOffset**:
- 表示**固定偏移量**（如 "+08:00"）
- 不包含夏令时规则
- 简单但不够精确

**代码示例**:
```java
// ZoneId - 推荐
ZoneId shanghai = ZoneId.of("Asia/Shanghai");
ZoneId newYork = ZoneId.of("America/New_York");
ZoneId systemDefault = ZoneId.systemDefault();

// 获取所有可用时区
Set<String> availableZoneIds = ZoneId.getAvailableZoneIds();

// ZoneOffset
ZoneOffset offset8 = ZoneOffset.of("+08:00");
ZoneOffset offset5 = ZoneOffset.ofHours(-5);

// 从 ZoneId 获取偏移量
ZoneOffset offset = LocalDateTime.now()
    .atZone(shanghai)
    .getOffset(); // +08:00
```

**常用时区**:

| 时区 ID | 说明 |
|---------|------|
| `Asia/Shanghai` | 中国标准时间 |
| `Asia/Hong_Kong` | 香港时间 |
| `Asia/Tokyo` | 日本时间 |
| `Asia/Seoul` | 韩国时间 |
| `America/New_York` | 美国东部时间 |
| `America/Los_Angeles` | 美国西部时间 |
| `Europe/London` | 伦敦时间 |
| `Europe/Paris` | 巴黎时间 |
| `UTC` | 协调世界时 |
| `GMT` | 格林尼治时间 |

---

### 3.2 ZonedDateTime

**问题**: `ZonedDateTime` 的作用是什么？如何使用？

**答案**:

**ZonedDateTime**:
- 包含**日期、时间、时区**的完整信息
- 可处理夏令时转换
- 用于跨时区应用

**创建方式**:
```java
// 当前时区时间
ZonedDateTime now = ZonedDateTime.now();

// 指定时区
ZonedDateTime shanghai = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));
ZonedDateTime newYork = ZonedDateTime.now(ZoneId.of("America/New_York"));

// 从 LocalDateTime 转换
LocalDateTime ldt = LocalDateTime.of(2024, 3, 15, 14, 30);
ZonedDateTime zdt = ldt.atZone(ZoneId.of("Asia/Shanghai"));

// 指定完整信息
ZonedDateTime zdt = ZonedDateTime.of(
    2024, 3, 15, 14, 30, 0, 0,
    ZoneId.of("Asia/Shanghai")
);
```

**时区转换**:
```java
ZonedDateTime shanghai = ZonedDateTime.now(ZoneId.of("Asia/Shanghai"));

// 转换为其他时区
ZonedDateTime newYork = shanghai.withZoneSameInstant(
    ZoneId.of("America/New_York")
);
ZonedDateTime london = shanghai.withZoneSameInstant(
    ZoneId.of("Europe/London")
);

// 只改变时区，不改变时间（不推荐）
ZonedDateTime sameTime = shanghai.withZoneSameLocal(
    ZoneId.of("America/New_York")
);
```

**代码示例 - 会议时间转换**:
```java
// 上海时间 14:00 的会议
ZonedDateTime shanghaiMeeting = ZonedDateTime.of(
    2024, 3, 15, 14, 0, 0, 0,
    ZoneId.of("Asia/Shanghai")
);

// 转换为纽约时间
ZonedDateTime newYorkMeeting = shanghaiMeeting.withZoneSameInstant(
    ZoneId.of("America/New_York")
);
// 结果: 2024-03-15T02:00-04:00[America/New_York]

// 转换为伦敦时间
ZonedDateTime londonMeeting = shanghaiMeeting.withZoneSameInstant(
    ZoneId.of("Europe/London")
);
// 结果: 2024-03-15T06:00Z[Europe/London]
```

---

## 四、时间戳与 Instant

### 4.1 Instant

**问题**: `Instant` 是什么？与 `Date` 有什么区别？

**答案**:

**Instant**:
- 表示**时间戳**（UTC 时间线上的某个点）
- 精确到**纳秒**
- 与时区无关

**Instant vs Date**:

| 特性 | Instant | Date |
|------|---------|------|
| **精度** | 纳秒 | 毫秒 |
| **时区** | 始终 UTC | 依赖系统时区 |
| **设计** | 清晰、不可变 | 混乱、可变 |
| **线程安全** | 安全 | 不安全 |

**代码示例**:
```java
// 当前时间戳
Instant now = Instant.now();

// 从 epoch 毫秒创建
Instant instant = Instant.ofEpochMilli(1710505800000L);

// 从 epoch 秒 + 纳秒创建
Instant instant = Instant.ofEpochSecond(1710505800, 500000000);

// 获取 epoch 值
long epochMilli = now.toEpochMilli();
long epochSecond = now.getEpochSecond();
int nano = now.getNano();

// 计算
Instant later = now.plusSeconds(3600);
Instant earlier = now.minus(Duration.ofHours(2));
```

**与 Date 互转**:
```java
// Date → Instant
Date date = new Date();
Instant instant = date.toInstant();

// Instant → Date
Instant instant = Instant.now();
Date date = Date.from(instant);
```

---

### 4.2 时间戳的应用场景

**问题**: 时间戳在系统中有什么应用场景？

**答案**:

**应用场景**:

1. **数据库时间存储**
```java
// 存储到数据库（统一使用 UTC）
Instant now = Instant.now();
// 存入数据库: 1710505800000

// 读取并转换
Instant fromDb = Instant.ofEpochMilli(dbTimestamp);
ZonedDateTime localTime = fromDb.atZone(ZoneId.systemDefault());
```

2. **日志时间戳**
```java
// 统一使用 UTC 时间戳记录日志
Instant eventTime = Instant.now();
logger.info("Event occurred at {}", eventTime);
```

3. **API 接口传输**
```java
// REST API 返回时间戳
@JsonFormat(shape = JsonFormat.Shape.NUMBER)
private Instant createdAt;
```

4. **缓存过期时间**
```java
// 使用 Instant 计算过期时间
Instant expireTime = Instant.now().plus(Duration.ofMinutes(30));
if (Instant.now().isAfter(expireTime)) {
    // 缓存过期
}
```

---

## 五、格式化与解析

### 5.1 DateTimeFormatter

**问题**: `DateTimeFormatter` 的作用是什么？与 `SimpleDateFormat` 有什么区别？

**答案**:

**DateTimeFormatter**:
- 用于**格式化**和**解析**日期时间
- **线程安全**（可共享使用）
- 支持自定义模式

**DateTimeFormatter vs SimpleDateFormat**:

| 特性 | DateTimeFormatter | SimpleDateFormat |
|------|-------------------|------------------|
| **线程安全** | ✅ 安全 | ❌ 不安全 |
| **不可变性** | ✅ 不可变 | ❌ 可变 |
| **API 设计** | 清晰 | 混乱 |
| **本地化** | 强大 | 一般 |

**预定义格式器**:

| 格式器 | 模式 | 示例 |
|--------|------|------|
| `ISO_LOCAL_DATE` | yyyy-MM-dd | 2024-03-15 |
| `ISO_LOCAL_TIME` | HH:mm:ss | 14:30:00 |
| `ISO_LOCAL_DATE_TIME` | yyyy-MM-ddTHH:mm:ss | 2024-03-15T14:30:00 |
| `ISO_OFFSET_DATE_TIME` | yyyy-MM-ddTHH:mm:ss+08:00 | 2024-03-15T14:30:00+08:00 |
| `ISO_ZONED_DATE_TIME` | yyyy-MM-ddTHH:mm:ss+08:00[Asia/Shanghai] | 完整时区格式 |
| `BASIC_ISO_DATE` | yyyyMMdd | 20240315 |
| `ISO_INSTANT` | UTC 时间戳 | 2024-03-15T06:30:00Z |

**代码示例**:
```java
LocalDateTime now = LocalDateTime.now();

// 使用预定义格式器
String iso = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

// 自定义格式
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    "yyyy年MM月dd日 HH:mm:ss"
);
String formatted = now.format(formatter);
// 结果: 2024年03月15日 14:30:00

// 带本地化
DateTimeFormatter chinese = DateTimeFormatter.ofPattern(
    "yyyy年MM月dd日 EEEE",
    Locale.CHINESE
);
String withLocale = now.format(chinese);
// 结果: 2024年03月15日 星期五
```

---

### 5.2 自定义格式化模式

**问题**: 如何自定义日期时间格式？常用的模式字符有哪些？

**答案**:

**常用模式字符**:

| 字符 | 说明 | 示例 |
|------|------|------|
| `y` | 年 | yyyy → 2024 |
| `M` | 月 | MM → 03, MMM → 三月 |
| `d` | 日 | dd → 15 |
| `E` | 星期 | EEEE → 星期五, E → 五 |
| `H` | 时（0-23） | HH → 14 |
| `h` | 时（1-12） | hh → 02 |
| `m` | 分 | mm → 30 |
| `s` | 秒 | ss → 00 |
| `S` | 毫秒 | SSS → 123 |
| `a` | 上午/下午 | a → 下午 |
| `z` | 时区名称 | z → CST |
| `Z` | 时区偏移 | Z → +0800, ZZZZ → +08:00 |
| `X` | ISO 时区 | XXX → +08:00 |

**代码示例**:
```java
LocalDateTime now = LocalDateTime.now();

// 常见格式
DateTimeFormatter[] formatters = {
    DateTimeFormatter.ofPattern("yyyy-MM-dd"),
    DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
    DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分"),
    DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH),
    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
};

for (DateTimeFormatter f : formatters) {
    System.out.println(now.format(f));
}
```

**解析字符串**:
```java
// 解析日期时间
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd HH:mm:ss"
);
LocalDateTime parsed = LocalDateTime.parse(
    "2024-03-15 14:30:00",
    formatter
);

// 解析带时区的时间
DateTimeFormatter zonedFormatter = DateTimeFormatter.ofPattern(
    "yyyy-MM-dd HH:mm:ss Z"
);
ZonedDateTime zoned = ZonedDateTime.parse(
    "2024-03-15 14:30:00 +0800",
    zonedFormatter
);
```

---

### 5.3 线程安全的格式器

**问题**: 如何在多线程环境中使用 DateTimeFormatter？

**答案**:

**DateTimeFormatter 是线程安全的**，可以直接共享使用：

```java
public class DateTimeUtil {
    // 线程安全的静态格式器
    public static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static final DateTimeFormatter DATETIME_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // 线程安全的方法
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
    
    public static LocalDateTime parse(String str) {
        return LocalDateTime.parse(str, DATETIME_FORMATTER);
    }
}

// 多线程使用
ExecutorService executor = Executors.newFixedThreadPool(10);
for (int i = 0; i < 100; i++) {
    executor.submit(() -> {
        String formatted = DateTimeUtil.format(LocalDateTime.now());
        System.out.println(formatted);
    });
}
```

**对比 SimpleDateFormat（线程不安全）**:
```java
// ❌ 错误：共享 SimpleDateFormat
public static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

// ✅ 正确：使用 ThreadLocal
private static final ThreadLocal<SimpleDateFormat> SDF_HOLDER = 
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
```

---

## 六、时间计算

### 6.1 Duration 和 Period

**问题**: `Duration` 和 `Period` 有什么区别？

**答案**:

**Duration**:
- 表示**时间间隔**（时、分、秒、纳秒）
- 基于时间线，精确到纳秒
- 适合计算两个时间点之间的间隔

**Period**:
- 表示**日期间隔**（年、月、日）
- 基于日历系统
- 适合计算日期之间的间隔

**对比**:

| 特性 | Duration | Period |
|------|----------|--------|
| **单位** | 时、分、秒、纳秒 | 年、月、日 |
| **计算方式** | 精确时间差 | 日历差 |
| **适用场景** | 时间计算 | 日期计算 |
| **ISO 格式** | PT2H30M | P1Y2M3D |

**代码示例**:
```java
// Duration - 时间间隔
LocalTime start = LocalTime.of(9, 0);
LocalTime end = LocalTime.of(17, 30);
Duration workHours = Duration.between(start, end);

System.out.println(workHours.toHours());    // 8
System.out.println(workHours.toMinutes());  // 510
System.out.println(workHours.toString());   // PT8H30M

// 创建 Duration
Duration twoHours = Duration.ofHours(2);
Duration thirtyMinutes = Duration.ofMinutes(30);
Duration custom = Duration.of(90, ChronoUnit.MINUTES);

// Period - 日期间隔
LocalDate startDate = LocalDate.of(2024, 1, 1);
LocalDate endDate = LocalDate.of(2024, 3, 15);
Period period = Period.between(startDate, endDate);

System.out.println(period.getYears());  // 0
System.out.println(period.getMonths()); // 2
System.out.println(period.getDays());   // 14
System.out.println(period.toString());  // P2M14D

// 创建 Period
Period oneYearTwoMonths = Period.of(1, 2, 0);
Period tenDays = Period.ofDays(10);
```

---

### 6.2 ChronoUnit 计算时间差

**问题**: 如何使用 `ChronoUnit` 计算时间差？

**答案**:

**ChronoUnit**:
- 提供各种**时间单位**
- 可以计算两个时间点之间的差值

**代码示例**:
```java
LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
LocalDateTime end = LocalDateTime.of(2024, 3, 15, 12, 0);

// 计算各种单位的时间差
long days = ChronoUnit.DAYS.between(start, end);       // 74
long hours = ChronoUnit.HOURS.between(start, end);     // 1788
long minutes = ChronoUnit.MINUTES.between(start, end); // 107280

// 计算月份差（注意：不是简单的天数除以30）
long months = ChronoUnit.MONTHS.between(start, end);   // 2

// 计算年份差
long years = ChronoUnit.YEARS.between(start, end);     // 0

// 使用 until 方法
long daysUntil = start.until(end, ChronoUnit.DAYS);
```

**常用 ChronoUnit**:

| 单位 | 说明 |
|------|------|
| `NANOS` | 纳秒 |
| `MICROS` | 微秒 |
| `MILLIS` | 毫秒 |
| `SECONDS` | 秒 |
| `MINUTES` | 分钟 |
| `HOURS` | 小时 |
| `DAYS` | 天 |
| `WEEKS` | 周 |
| `MONTHS` | 月 |
| `YEARS` | 年 |
| `DECADES` | 十年 |
| `CENTURIES` | 世纪 |

---

### 6.3 TemporalAdjuster

**问题**: `TemporalAdjuster` 是什么？有什么用途？

**答案**:

**TemporalAdjuster**:
- 用于**调整日期时间**的策略接口
- 提供了许多预定义的调整器
- 可自定义调整逻辑

**预定义调整器**（`TemporalAdjusters` 类）:

| 调整器 | 说明 |
|--------|------|
| `firstDayOfMonth()` | 本月第一天 |
| `lastDayOfMonth()` | 本月最后一天 |
| `firstDayOfNextMonth()` | 下月第一天 |
| `firstDayOfYear()` | 本年第一天 |
| `lastDayOfYear()` | 本年最后一天 |
| `firstInMonth(DayOfWeek)` | 本月第一个星期几 |
| `lastInMonth(DayOfWeek)` | 本月最后一个星期几 |
| `next(DayOfWeek)` | 下一个星期几 |
| `nextOrSame(DayOfWeek)` | 下一个星期几（包含今天） |
| `previous(DayOfWeek)` | 上一个星期几 |
| `dayOfWeekInMonth(n, DayOfWeek)` | 本月第 n 个星期几 |

**代码示例**:
```java
LocalDate date = LocalDate.of(2024, 3, 15);

// 本月第一天
LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
// 2024-03-01

// 本月最后一天
LocalDate lastDay = date.with(TemporalAdjusters.lastDayOfMonth());
// 2024-03-31

// 下一个星期五
LocalDate nextFriday = date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
// 2024-03-22

// 本月第二个星期二
LocalDate secondTuesday = date.with(
    TemporalAdjusters.dayOfWeekInMonth(2, DayOfWeek.TUESDAY)
);
// 2024-03-12
```

**自定义调整器**:
```java
// 自定义：下一个工作日
TemporalAdjuster nextWorkingDay = temporal -> {
    DayOfWeek dayOfWeek = DayOfWeek.from(temporal);
    int daysToAdd;
    switch (dayOfWeek) {
        case FRIDAY: daysToAdd = 3; break;
        case SATURDAY: daysToAdd = 2; break;
        default: daysToAdd = 1; break;
    }
    return temporal.plus(daysToAdd, ChronoUnit.DAYS);
};

LocalDate friday = LocalDate.of(2024, 3, 15); // 周五
LocalDate nextWorkDay = friday.with(nextWorkingDay);
// 2024-03-18 (下周一)
```

---

## 七、新旧 API 对比

### 7.1 Date/Calendar 与 java.time 的对比

**问题**: 新旧时间 API 的主要区别是什么？

**答案**:

**核心区别对比**:

| 特性 | 旧 API (Date/Calendar) | 新 API (java.time) |
|------|------------------------|-------------------|
| **设计理念** | 混乱、设计缺陷 | 清晰、符合 ISO 标准 |
| **不可变性** | ❌ 可变 | ✅ 不可变 |
| **线程安全** | ❌ 不安全 | ✅ 安全 |
| **时区处理** | 混乱 | 清晰 |
| **月份** | 0-11 | 1-12 |
| **精度** | 毫秒 | 纳秒 |
| **API 复杂度** | 复杂 | 简洁 |
| **可读性** | 差 | 好 |

**代码对比**:
```java
// ❌ 旧 API - 获取当前日期
Calendar cal = Calendar.getInstance();
int year = cal.get(Calendar.YEAR);
int month = cal.get(Calendar.MONTH) + 1; // 注意 +1
int day = cal.get(Calendar.DAY_OF_MONTH);

// ✅ 新 API - 获取当前日期
LocalDate now = LocalDate.now();
int year = now.getYear();
int month = now.getMonthValue(); // 1-12
int day = now.getDayOfMonth();

// ❌ 旧 API - 日期计算
Calendar cal = Calendar.getInstance();
cal.add(Calendar.DAY_OF_MONTH, 7); // 7天后

// ✅ 新 API - 日期计算
LocalDate nextWeek = LocalDate.now().plusDays(7);

// ❌ 旧 API - 格式化（线程不安全）
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
String str = sdf.format(new Date());

// ✅ 新 API - 格式化（线程安全）
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
String str = LocalDate.now().format(formatter);
```

---

### 7.2 新旧 API 的转换

**问题**: 如何在旧 API 和新 API 之间进行转换？

**答案**:

**Date ↔ Instant**:
```java
// Date → Instant
Date date = new Date();
Instant instant = date.toInstant();

// Instant → Date
Instant instant = Instant.now();
Date date = Date.from(instant);
```

**Calendar ↔ ZonedDateTime**:
```java
// Calendar → ZonedDateTime
Calendar cal = Calendar.getInstance();
ZonedDateTime zdt = cal.toInstant().atZone(cal.getTimeZone().toZoneId());

// ZonedDateTime → Calendar
ZonedDateTime zdt = ZonedDateTime.now();
Calendar cal = GregorianCalendar.from(zdt);
```

**Date ↔ LocalDateTime**:
```java
// Date → LocalDateTime
Date date = new Date();
LocalDateTime ldt = date.toInstant()
    .atZone(ZoneId.systemDefault())
    .toLocalDateTime();

// LocalDateTime → Date
LocalDateTime ldt = LocalDateTime.now();
Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
```

**SimpleDateFormat ↔ DateTimeFormatter**:
```java
// 两者模式语法基本一致
String pattern = "yyyy-MM-dd HH:mm:ss";

// 旧 API
SimpleDateFormat sdf = new SimpleDateFormat(pattern);

// 新 API
DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
```

**Timestamp ↔ Instant**:
```java
// Timestamp → Instant
Timestamp ts = new Timestamp(System.currentTimeMillis());
Instant instant = ts.toInstant();

// Instant → Timestamp
Instant instant = Instant.now();
Timestamp ts = Timestamp.from(instant);
```

---

### 7.3 面试题速查表

| 问题 | 核心要点 |
|------|----------|
| 为什么需要新 API | 旧 API 设计缺陷、线程不安全、可变 |
| 新 API 核心类 | LocalDate、LocalTime、LocalDateTime、Instant、ZonedDateTime |
| 不可变性 | 所有对象不可变，线程安全 |
| 时区处理 | ZoneId、ZonedDateTime、带时区转换 |
| 格式化 | DateTimeFormatter，线程安全 |
| Duration vs Period | Duration 时间间隔，Period 日期间隔 |
| 时间调整 | TemporalAdjusters，如 firstDayOfMonth |
| 新旧转换 | Date.toInstant()、Date.from(Instant) |

---

**文档版本**: 1.0.0  
**最后更新**: 2026-03-26  
**适用 JDK**: Java 8+
