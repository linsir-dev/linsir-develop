# Apache Commons 工具库示例

## 项目介绍

本模块提供了 Apache Commons 系列工具库的详细使用示例，包括 `commons-lang3`、`commons-collections4` 和 `commons-io` 等常用库。通过这些示例代码，开发者可以快速了解和掌握 Apache Commons 工具库的核心功能，提高开发效率。

## 技术栈

- Java 8+
- Apache Commons Lang3 3.14.0
- Apache Commons Collections4 4.4
- Apache Commons IO 2.11.0
- Maven 3.6+

## 目录结构

```
apacheCommons/
├── ApacheCommonsDemoMain.java    # 主类，用于运行所有示例
├── CommonsLang3Demo.java         # commons-lang3 库示例
├── CommonsCollections4Demo.java   # commons-collections4 库示例
├── CommonsIODemo.java             # commons-io 库示例
└── README.md                      # 项目文档
```

## 功能说明

### 1. CommonsLang3Demo

演示 `commons-lang3` 库的核心功能：

- **StringUtils**：字符串操作工具类
  - 字符串判空（isEmpty、isBlank）
  - 字符串转换（capitalize、upperCase、lowerCase）
  - 字符串截取和填充（substring、leftPad、rightPad）
  - 字符串分割和连接（split、join）

- **NumberUtils**：数字处理工具类
  - 字符串转数字（toInt、toDouble）
  - 数字判断（isDigits、isParsable）
  - 数字比较（max、min）

- **DateUtils**：日期处理工具类
  - 日期加减（addDays、addWeeks、addMonths）
  - 日期比较（isSameDay、truncatedCompareTo）

- **RandomStringUtils**：随机字符串生成工具类
  - 生成随机字母字符串（randomAlphabetic）
  - 生成随机数字字符串（randomNumeric）
  - 生成随机字母数字混合字符串（randomAlphanumeric）
  - 生成指定字符集的随机字符串（random）

- **ObjectUtils**：对象操作工具类
  - 对象判空（isEmpty）
  - 对象默认值（defaultIfNull）
  - 对象比较（compare）

### 2. CommonsCollections4Demo

演示 `commons-collections4` 库的核心功能：

- **CollectionUtils**：集合操作工具类
  - 集合判空（isEmpty、isNotEmpty）
  - 集合交集（intersection）
  - 集合并集（union）
  - 集合差集（subtract）
  - 集合包含关系（containsAny、containsAll）

- **ListUtils**：列表操作工具类
  - 列表合并（union）
  - 列表分割（partition）
  - 空列表处理（emptyIfNull）
  - 列表比较（isEqualList）

- **MapUtils**：Map 操作工具类
  - Map 判空（isEmpty、isNotEmpty）
  - Map 值获取（getIntValue）
  - 空 Map 处理（emptyIfNull）

- **BidiMap**：双向映射实现
  - 正向查找（get）
  - 反向查找（getKey）
  - 值删除（removeValue）

- **LRUMap**：最近最少使用缓存实现
  - 缓存容量控制
  - 自动淘汰最久未使用的元素

- **HashedMap**：哈希映射实现
  - 基本 Map 操作
  - 键值查找

### 3. CommonsIODemo

演示 `commons-io` 库的核心功能：

- **FileUtils**：文件操作工具类
  - 文件读写（writeStringToFile、readFileToString）
  - 文件复制（copyFile、copyFileToDirectory）
  - 目录操作（forceMkdir、deleteDirectory）
  - 文件列表（listFiles）
  - 文件删除（deleteQuietly）

- **IOUtils**：IO 流操作工具类
  - 流转换（toInputStream）
  - 流读取（toString）
  - 流复制（copy）
  - 流关闭（closeQuietly）

- **LineIterator**：文件行迭代器
  - 逐行读取文件内容
  - 自动资源管理

- **FilenameUtils**：文件名操作工具类
  - 文件名解析（getName、getBaseName、getExtension）
  - 路径操作（getPath、getFullPath）
  - 路径规范化（normalize）
  - 路径比较（equals、equalsNormalized）

- **FileFilterUtils**：文件过滤器工具类
  - 后缀过滤器（suffixFileFilter）
  - 逻辑过滤器（or）
  - 目录过滤器（directoryFileFilter）

## 使用方法

### 1. 编译项目

在项目根目录执行以下命令：

```bash
mvn compile
```

### 2. 运行示例

使用 Maven 执行主类：

```bash
mvn exec:java -Dexec.mainClass=com.linsir.abc.pdai.tools.apacheCommons.ApacheCommonsDemoMain
```

或直接使用 Java 命令运行编译后的类：

```bash
java -cp target/classes com.linsir.abc.pdai.tools.apacheCommons.ApacheCommonsDemoMain
```

### 3. 查看输出

运行后，控制台会输出详细的示例结果，包括每个工具方法的使用方式和执行结果。

## 示例输出

运行示例后，控制台将输出类似以下内容：

```
==========================================
Apache Commons 库示例代码验证
==========================================

1. 运行 Apache Commons Lang3 示例:
------------------------------------------
=== StringUtils 示例 ===
StringUtils.isEmpty(emptyStr): true
StringUtils.isEmpty(nullStr): true
StringUtils.isEmpty(normalStr): false
StringUtils.isBlank('   '): true
StringUtils.isNotBlank(normalStr): true
...

2. 运行 Apache Commons Collections4 示例:
------------------------------------------
=== CollectionUtils 示例 ===
CollectionUtils.isEmpty(list1): false
CollectionUtils.isNotEmpty(list1): true
CollectionUtils.intersection(list1, list2): [banana]
...

3. 运行 Apache Commons IO 示例:
------------------------------------------
=== FileUtils 示例 ===
FileUtils.writeStringToFile: 文件创建成功
FileUtils.readFileToString: Hello, Apache Commons IO!
This is a test file.
...

==========================================
Apache Commons 库示例代码验证完成！
==========================================
```

## 技术要点

1. **工具类的选择**：根据具体需求选择合适的 Apache Commons 工具类，可以大大简化代码开发

2. **异常处理**：示例代码中包含了基本的异常处理，实际开发中应根据具体情况进行适当的异常捕获和处理

3. **资源管理**：使用 `IOUtils.closeQuietly()` 等方法确保资源正确关闭，避免资源泄露

4. **性能考虑**：对于大数据量操作，应注意选择合适的工具方法，避免不必要的性能开销

5. **版本兼容性**：确保使用的 Apache Commons 库版本与项目的 Java 版本兼容

## 总结

Apache Commons 工具库是 Java 开发中的得力助手，提供了丰富的工具类和方法，可以显著提高开发效率，减少重复代码。通过本模块的示例代码，开发者可以快速掌握这些工具库的使用方法，在实际项目中灵活应用。

## 参考资料

- [Apache Commons Lang3 官方文档](https://commons.apache.org/proper/commons-lang/)
- [Apache Commons Collections4 官方文档](https://commons.apache.org/proper/commons-collections/)
- [Apache Commons IO 官方文档](https://commons.apache.org/proper/commons-io/)
