# Java 语法基础代码说明文档

## 文档信息

- **文档编号**: 02-grammar-code-guide
- **所属模块**: core/grammar
- **对应设计文档**: [01-grammar-detailed-design.md](./01-grammar-detailed-design.md)
- **创建日期**: 2026-03-27
- **版本**: 1.0.0

---

## 1. 数据类型 (datatype)

### 1.1 PrimitiveTypes - 基本数据类型

**代码结构**

```
com.linsir.abc.core.grammar.datatype.PrimitiveTypes
├── 成员变量（8种基本类型的默认值演示）
│   ├── byteVar, shortVar, intVar, longVar
│   ├── floatVar, doubleVar
│   ├── charVar, booleanVar
├── demonstrateRanges() - 展示各类型取值范围
├── demonstrateDefaults() - 展示成员变量默认值
├── demonstrateLiterals() - 展示字面量表示方式
└── main() - 测试入口
```

**代码使用场景**

- 学习 Java 基本数据类型的取值范围
- 理解成员变量自动初始化机制
- 掌握不同进制字面量的表示方法（十进制、八进制、十六进制、二进制）
- 了解浮点数科学计数法表示

**对应 JDK 模块**: `java.lang` 包下的基本类型包装类（Byte, Short, Integer, Long, Float, Double, Character, Boolean）

**测试代码结构**

```java
public static void main(String[] args) {
    PrimitiveTypes demo = new PrimitiveTypes();
    demo.demonstrateRanges();    // 测试取值范围
    demo.demonstrateDefaults();  // 测试默认值
    demo.demonstrateLiterals();  // 测试字面量
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 基本数据类型演示                      ║
╚════════════════════════════════════════════════╝

=== 基本数据类型取值范围 ===
byte: -128 ~ 127
short: -32768 ~ 32767
int: -2147483648 ~ 2147483647
long: -9223372036854775808 ~ 9223372036854775807
float: 1.4E-45 ~ 3.4028235E38
double: 4.9E-324 ~ 1.7976931348623157E308
char: 0 ~ 65535

=== 成员变量默认值 ===
byte: 0
short: 0
int: 0
long: 0
float: 0.0
double: 0.0
char: [] (值为 0)
boolean: false

=== 字面量表示 ===
十进制 100 = 100
八进制 0144 = 100
十六进制 0x64 = 100
二进制 0b1100100 = 100
long 字面量 100L = 100
float 3.14f = 3.14
double 3.14 = 3.14
double 3.14d = 3.14
double 3.14e2 = 314.0
字符 'A' = A
Unicode '\u0041' = A
转义字符 '\n' 显示为换行
boolean true = true
```

---

### 1.2 ReferenceTypes - 引用类型

**代码结构**

```
com.linsir.abc.core.grammar.datatype.ReferenceTypes
├── demonstrateReferenceBehavior() - 引用内存特性
├── demonstrateNullReference() - null 引用
├── demonstrateStringPool() - 字符串常量池
└── 内部类 Person - 用于演示引用特性
```

**代码使用场景**

- 理解 Java 引用类型的内存模型
- 掌握对象引用赋值与对象复制的区别
- 理解字符串常量池机制
- 学习 `==` 与 `equals()` 的区别

**对应 JDK 模块**: `java.lang.Object` 及其子类，字符串常量池机制

**测试代码结构**

```java
public static void main(String[] args) {
    ReferenceTypes demo = new ReferenceTypes();
    demo.demonstrateReferenceBehavior();
    demo.demonstrateNullReference();
    demo.demonstrateStringPool();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 引用类型演示                          ║
╚════════════════════════════════════════════════╝

=== 引用类型内存特性 ===
person1: Person{name='张三', age=25}
person2: Person{name='张三', age=25}
person1 == person2 ? true

通过 person2 修改 age 为 30 后:
person1.getAge() = 30
person2.getAge() = 30

创建 person3 (内容相同的新对象):
person3: Person{name='张三', age=30}
person1.equals(person3) ? true
person1 == person3 ? false

=== Null 引用 ===
person = null
person 是否为 null ? true
注意: 访问 null 引用的成员会导致 NullPointerException
person 为 null，安全跳过访问

=== 字符串常量池 ===
s1 = "Hello"
s2 = "Hello"
s3 = new String("Hello")

使用 == 比较引用:
s1 == s2 ? true (都指向常量池同一对象)
s1 == s3 ? false (s3 是堆中新对象)

使用 equals 比较内容:
s1.equals(s2) ? true
s1.equals(s3) ? true

使用 intern() 方法:
s4 = s3.intern()
s1 == s4 ? true (intern 返回常量池引用)
```

---

### 1.3 TypeConversion - 类型转换

**代码结构**

```
com.linsir.abc.core.grammar.datatype.TypeConversion
├── demonstrateImplicitConversion() - 自动类型转换
├── demonstrateExplicitConversion() - 强制类型转换
├── demonstrateWrapperConversion() - 包装类转换
└── demonstrateReferenceConversion() - 引用类型转换
```

**代码使用场景**

- 理解自动类型转换（隐式）的规则和范围
- 掌握强制类型转换（显式）的使用和注意事项
- 学习基本类型与包装类之间的自动装箱/拆箱
- 理解字符串与数字之间的转换
- 掌握引用类型的向上转型和向下转型

**对应 JDK 模块**: 基本类型包装类（Integer, Double 等）的转换方法

**测试代码结构**

```java
public static void main(String[] args) {
    TypeConversion demo = new TypeConversion();
    demo.demonstrateImplicitConversion();
    demo.demonstrateExplicitConversion();
    demo.demonstrateWrapperConversion();
    demo.demonstrateReferenceConversion();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 类型转换演示                          ║
╚════════════════════════════════════════════════╝

=== 自动类型转换（隐式）===
byte 10 -> short 10
short 10 -> int 10
int 10 -> long 10
long 10 -> float 10.0
float 10.0 -> double 10.0

char 'A' -> int 65

byte + byte = int: 10 + 20 = 30
int + double = double: 100 + 3.14 = 103.14

=== 强制类型转换（显式）===
(int) 3.99 = 3 （截断小数部分）
(int) -3.99 = -3 （向零取整）

int 128 -> byte -128 （溢出，只保留低8位）
(int) 10000000000L = 1410065408 （高位截断）

(int) 1234.567 = 1234

=== 包装类转换 ===
自动装箱: Integer = 100
valueOf: Integer = 100

自动拆箱: int = 100
intValue: int = 100

字符串转数字:
Integer.parseInt("12345") = 12345
Double.parseDouble("3.14159") = 3.14159

数字转字符串:
String.valueOf(123) = "123"
Integer.toString(456) = "456"

255 的不同进制表示:
二进制: 11111111
八进制: 377
十六进制: ff

=== 引用类型转换 ===
向上转型: String -> Object
obj = Hello

向下转型: Object -> String
str = "Hello"
长度: 5

num 不是 String 类型，跳过转型
```

---

## 2. 变量与常量 (variable)

### 2.1 VariableScopes - 变量作用域

**代码结构**

```
com.linsir.abc.core.grammar.variable.VariableScopes
├── instanceVar - 成员变量（实例变量）
├── staticVar - 类变量（静态变量）
├── demonstrateScopes() - 各种变量作用域演示
├── demonstrateShadowing() - 变量隐藏（Shadowing）
└── demonstrateStaticSharing() - 静态变量共享
```

**代码使用场景**

- 理解局部变量、成员变量、类变量的区别
- 掌握变量作用域规则（方法作用域、代码块作用域、循环作用域）
- 理解变量隐藏（Shadowing）现象
- 学习静态变量的共享特性

**对应 JDK 模块**: Java 变量声明和作用域规则

**测试代码结构**

```java
public static void main(String[] args) {
    VariableScopes obj1 = new VariableScopes();
    VariableScopes obj2 = new VariableScopes();
    
    obj1.demonstrateScopes();
    obj1.demonstrateShadowing(100);
    obj1.demonstrateStaticSharing();
    obj2.demonstrateStaticSharing();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 变量作用域演示                        ║
╚════════════════════════════════════════════════╝

【对象 1 的演示】
=== 变量作用域演示 ===
成员变量 (instanceVar): 10
类变量 (staticVar): 20
局部变量 (localVar): 30

代码块内:
  代码块变量 (blockVar): 40
  访问局部变量: 30

循环变量作用域:
  循环内 i = 0
  循环内 i = 1
  循环内 i = 2

if 语句块内:
  if变量 (ifVar): 50

新代码块内的 blockVar: 60

=== 变量隐藏（Shadowing）===
参数 instanceVar: 100
成员变量 this.instanceVar: 10
局部变量 localInstanceVar: 100

──────────────────────────────────────────────────

【对象 2 的演示 - 静态变量共享】
对象 1 修改静态变量:

=== 静态变量共享 ===
修改前 staticVar = 20
修改后 staticVar = 21

对象 2 看到相同的静态变量值:

=== 静态变量共享 ===
修改前 staticVar = 21
修改后 staticVar = 22

通过类名访问静态变量: VariableScopes.getStaticVar() = 22

══════════════════════════════════════════════════
演示完成！

总结:
• 局部变量：方法/代码块内，栈内存，必须初始化
• 成员变量：对象级别，堆内存，自动初始化
• 类变量：类级别，方法区，被所有实例共享
```

---

### 2.2 Constants - 常量

**代码结构**

```
com.linsir.abc.core.grammar.variable.Constants
├── MAX_SIZE, PI, APP_NAME - 编译期常量
├── CREATE_TIME - 运行期常量
├── instanceConstant - 实例常量
├── demonstrateConstants() - 常量使用演示
├── demonstrateEnum() - 枚举常量演示
└── Status - 枚举类型定义
```

**代码使用场景**

- 学习 `final` 关键字定义常量
- 理解编译期常量与运行期常量的区别
- 掌握枚举类型的定义和使用
- 理解引用类型常量的特性（引用不可变，内容可变）

**对应 JDK 模块**: `final` 关键字，枚举类型 `java.lang.Enum`

**测试代码结构**

```java
public static void main(String[] args) {
    Constants demo = new Constants(42);
    demo.demonstrateConstants();
    demo.demonstrateEnum();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 常量演示                              ║
╚════════════════════════════════════════════════╝

=== 常量使用 ===
MAX_SIZE: 100
PI: 3.141592653589793
APP_NAME: LinsirABC
VERSION: 1.0.0
CREATE_TIME: Fri Mar 27 01:49:03 CST 2026
instanceConstant: 42
localConst: 50

引用类型常量 StringBuilder:
  初始值: Hello
  修改后: Hello World
  数组常量修改元素后: [100, 2, 3]

=== 枚举常量 ===
当前状态: ACTIVE
状态描述: 激活
状态编码: 1

所有状态:
  ACTIVE (code=1): 激活
  INACTIVE (code=2): 未激活
  DELETED (code=3): 已删除

从字符串解析: Status.valueOf("ACTIVE") = ACTIVE

使用 switch 处理枚举:
  处理未激活状态
  处理已删除状态

══════════════════════════════════════════════════
演示完成！

总结:
• 编译期常量：基本类型/String，编译时确定，性能最优
• 运行期常量：对象类型，运行时确定
• 引用常量：引用不可变，对象内容可变
• 枚举：类型安全的常量集合，可包含方法和字段
```

---

## 3. 运算符 (operator)

### 3.1 ArithmeticOperators - 算术运算符

**代码结构**

```
com.linsir.abc.core.grammar.operator.ArithmeticOperators
├── demonstrateBasicArithmetic() - 基本算术运算
├── demonstrateIncrementDecrement() - 自增自减
├── demonstrateCompoundAssignment() - 复合赋值
└── demonstrateOverflow() - 整数溢出
```

**代码使用场景**

- 掌握基本算术运算（加减乘除、取模）
- 理解自增自减运算符的前缀和后缀区别
- 学习复合赋值运算符的使用
- 了解整数溢出的检测和处理

**对应 JDK 模块**: Java 基本算术运算，Math 类

**测试代码结构**

```java
public static void main(String[] args) {
    ArithmeticOperators demo = new ArithmeticOperators();
    demo.demonstrateBasicArithmetic();
    demo.demonstrateIncrementDecrement();
    demo.demonstrateCompoundAssignment();
    demo.demonstrateOverflow();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 算术运算符演示                        ║
╚════════════════════════════════════════════════╝

=== 基本算术运算 ===
a = 17, b = 5

a + b = 22
a - b = 12
a * b = 85
a / b (整数除法) = 3
a % b (取模) = 2
(double)a / b = 3.4

负数取模:
17 % 5 = 2
-17 % 5 = -2
17 % -5 = 2
-17 % -5 = -2

=== 自增自减运算符 ===
初始 x = 5

前缀自增 (++x):
  y = ++x: x = 6, y = 6

后缀自增 (x++):
  z = x++: x = 6, z = 5

自减运算:
  --x = 4, x = 4
  x-- = 5, x = 4

在表达式中使用:
  x = 5, x++ + ++x = 12 (x 最终 = 7)

=== 复合赋值运算符 ===
初始 a = 10

a += 5  -> a = 15
a -= 3  -> a = 12
a *= 2  -> a = 24
a /= 4  -> a = 6
a %= 3  -> a = 0

复合赋值的类型转换:
byte b += 5 -> b = 15

=== 整数溢出 ===
Integer.MAX_VALUE = 2147483647
MAX_VALUE + 1 = -2147483648
Integer.MIN_VALUE = -2147483648

MIN_VALUE - 1 = 2147483647

使用 Math.addExact 检测溢出:
溢出检测: integer overflow

乘法溢出:
100000 * 100000 = 1410065408 (溢出)
(long)100000 * 100000 = 10000000000

══════════════════════════════════════════════════
演示完成！
```

---

### 3.2 BitwiseOperators - 位运算符

**代码结构**

```
com.linsir.abc.core.grammar.operator.BitwiseOperators
├── demonstrateBasicBitwise() - 基本位运算
├── demonstrateShift() - 移位运算
├── demonstratePermissionControl() - 权限控制应用
└── demonstrateEfficientOperations() - 高效运算
```

**代码使用场景**

- 理解位运算符（与、或、异或、非）
- 掌握移位运算（左移、右移、无符号右移）
- 学习位运算在权限控制中的应用
- 了解位运算实现高效乘除法

**对应 JDK 模块**: Java 位运算操作

**测试代码结构**

```java
public static void main(String[] args) {
    BitwiseOperators demo = new BitwiseOperators();
    demo.demonstrateBasicBitwise();
    demo.demonstrateShift();
    demo.demonstratePermissionControl();
    demo.demonstrateEfficientOperations();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 位运算符演示                          ║
╚════════════════════════════════════════════════╝

=== 基本位运算 ===
a = 5 (二进制: 00000000000000000000000000000101)
b = 3 (二进制: 00000000000000000000000000000011)

a & b = 1 (二进制: 00000000000000000000000000000001)
  解释: 0101 & 0011 = 0001

a | b = 7 (二进制: 00000000000000000000000000000111)
  解释: 0101 | 0011 = 0111

a ^ b = 6 (二进制: 00000000000000000000000000000110)
  解释: 0101 ^ 0011 = 0110

~a = -6 (二进制: 11111111111111111111111111111010)
  解释: ~0101 = 1111...1010 (补码表示的负数)

=== 移位运算 ===
a = 8 (二进制: 00000000000000000000000000001000)

a << 2 = 32 (二进制: 00000000000000000000000000100000)
  相当于 a * 4 = 32

a >> 2 = 2 (二进制: 00000000000000000000000000000010)
  相当于 a / 4 = 2

负数移位:
-8 = -8 (二进制: 11111111111111111111111111111000)
-8 >> 2 = -2 (算术右移，保持符号)
-8 >>> 2 = 1073741822 (逻辑右移，高位补0)

=== 位运算应用：权限控制 ===
权限定义:
  READ = 1 (二进制: 0001)
  WRITE = 2 (二进制: 0010)
  EXECUTE = 4 (二进制: 0100)
  DELETE = 8 (二进制: 1000)

用户权限: 3 (二进制: 0011)
管理员权限: 15 (二进制: 1111)

权限检查:
  用户是否有 READ 权限? true
  用户是否有 EXECUTE 权限? false
  管理员是否有所有权限? true

添加 EXECUTE 后，用户权限: 7
  现在有 EXECUTE 权限? true

移除 WRITE 后，用户权限: 5
  还有 WRITE 权限? false

=== 位运算应用：高效乘除法 ===
num = 16

乘法（左移）:
  16 << 1 = 32 (相当于 *2)
  16 << 2 = 64 (相当于 *4)
  16 << 3 = 128 (相当于 *8)

除法（右移）:
  16 >> 1 = 8 (相当于 /2)
  16 >> 2 = 4 (相当于 /4)
  16 >> 3 = 2 (相当于 /8)

取模（与运算）:
  16 & 7 = 0 (相当于 %8)
  16 & 15 = 0 (相当于 %16)

判断奇偶:
  16 & 1 = 0 (0为偶数，1为奇数)
  17 & 1 = 1

══════════════════════════════════════════════════
演示完成！
```

---

### 3.3 LogicalOperators - 逻辑运算符

**代码结构**

```
com.linsir.abc.core.grammar.operator.LogicalOperators
├── demonstrateBasicLogical() - 基本逻辑运算
├── demonstrateShortCircuit() - 短路运算
├── demonstratePracticalUsage() - 实际应用
├── demonstrateComplexExpressions() - 复杂表达式
└── demonstrateBooleanAlgebra() - 布尔代数定律
```

**代码使用场景**

- 掌握逻辑运算符（&&、||、!、^）
- 理解短路运算的原理和应用
- 学习复杂逻辑表达式的构建
- 验证布尔代数基本定律

**对应 JDK 模块**: Java 逻辑运算

**测试代码结构**

```java
public static void main(String[] args) {
    LogicalOperators demo = new LogicalOperators();
    demo.demonstrateBasicLogical();
    demo.demonstrateShortCircuit();
    demo.demonstratePracticalUsage();
    demo.demonstrateComplexExpressions();
    demo.demonstrateBooleanAlgebra();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 逻辑运算符演示                        ║
╚════════════════════════════════════════════════╝

=== 基本逻辑运算 ===
a = true, b = false

a && b = false (true AND false = false)
a || b = true (true OR false = true)
!a = false (NOT true = false)
!b = true (NOT false = true)
a ^ b = true (true XOR false = true)
a ^ a = false (true XOR true = false)

=== 短路运算 ===
短路逻辑与 (&&):
  false && willNotExecute() = false
  右边方法没有被执行（短路）

短路逻辑或 (||):
  true || willNotExecute() = true
  右边方法没有被执行（短路）

非短路运算符 (& 和 |):
  即使左边已经能确定结果，右边仍会执行
  [这个方法被执行了]
  false & willExecute() = false
  [这个方法被执行了]
  true | willExecute() = true

=== 短路运算的实际应用 ===
安全调用示例:
  字符串为空或null，安全跳过

数组边界检查:
  索引越界或条件不满足，安全跳过

默认值设置:
  显示名称: 未知

=== 复杂逻辑表达式 ===
条件: age=25, hasId=true, isVip=false

入场条件 (hasId && (age>=18 || isVip)): true
优惠条件 (isVip || age<18 || age>60): false

德摩根定律验证:
  !(age>=18 && hasId) = false
  age<18 || !hasId = false
  两者相等: true

=== 布尔代数定律 ===
交换律:
  a && b = b && a: false = false
  a || b = b || a: true = true

结合律:
  (a && b) && c = a && (b && c): false = false
  (a || b) || c = a || (b || c): true = true

分配律:
  a && (b || c) = (a && b) || (a && c): true = true
  a || (b && c) = (a || b) && (a || c): true = true

恒等律:
  a && true = a: true = true
  a || false = a: true = true

零律:
  a && false = false: false
  a || true = true: true

双重否定:
  !!a = a: true = true

══════════════════════════════════════════════════
演示完成！
```

---

## 4. 流程控制 (controlflow)

### 4.1 Conditionals - 条件语句

**代码结构**

```
com.linsir.abc.core.grammar.controlflow.Conditionals
├── demonstrateIfElse() - if-else 语句
├── demonstrateTernary() - 三元运算符
├── demonstrateSwitchTraditional() - 传统 switch
├── demonstrateSwitchEnhanced() - 增强 switch (Java 14+)
└── demonstrateSwitchWithEnum() - switch 与枚举
```

**代码使用场景**

- 掌握 if-else 条件判断的各种形式
- 学习三元运算符的使用
- 理解传统 switch 语句的语法
- 掌握 Java 14+ 增强 switch 表达式
- 学习 switch 与枚举类型的结合使用

**对应 JDK 模块**: Java 条件语句，switch 表达式（Java 14+）

**测试代码结构**

```java
public static void main(String[] args) {
    Conditionals demo = new Conditionals();
    demo.demonstrateIfElse();
    demo.demonstrateTernary();
    demo.demonstrateSwitchTraditional();
    demo.demonstrateSwitchEnhanced();
    demo.demonstrateSwitchWithEnum();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 条件语句演示                          ║
╚════════════════════════════════════════════════╝

=== if-else 条件语句 ===
分数: 85
及格

双分支判断:
未达到优秀

成绩等级判断:
等级: B

嵌套条件判断:
可以入场（成人票）

=== 三元运算符 ===
a=10, b=20, max=20
分数 75 -> 及格
|-5| = 5

=== 传统 switch 语句 ===
day=3 -> 星期三

多个 case 共享代码:
等级 B -> 通过

=== 增强 switch 表达式 (Java 14+) ===
day=5 -> 星期五

多个 case:
月份 5 -> 春季

使用 yield 返回值:
  良好成绩
分数 85 -> 等级 B

=== switch 与枚举 ===
传统 switch:
  今天是工作日

增强 switch:
  今天活动: 工作中

══════════════════════════════════════════════════
演示完成！
```

---

### 4.2 Loops - 循环语句

**代码结构**

```
com.linsir.abc.core.grammar.controlflow.Loops
├── demonstrateForLoop() - for 循环
├── demonstrateEnhancedFor() - 增强 for 循环
├── demonstrateWhileLoop() - while 循环
├── demonstrateDoWhileLoop() - do-while 循环
├── demonstrateLoopControl() - 循环控制语句
└── demonstrateNestedLoops() - 嵌套循环
```

**代码使用场景**

- 掌握各种循环结构（for、while、do-while）
- 学习增强 for 循环遍历数组和集合
- 理解 break、continue 的使用
- 学习带标签的 break 语句
- 掌握嵌套循环的应用

**对应 JDK 模块**: Java 循环控制结构

**测试代码结构**

```java
public static void main(String[] args) {
    Loops demo = new Loops();
    demo.demonstrateForLoop();
    demo.demonstrateEnhancedFor();
    demo.demonstrateWhileLoop();
    demo.demonstrateDoWhileLoop();
    demo.demonstrateLoopControl();
    demo.demonstrateNestedLoops();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 循环语句演示                          ║
╚════════════════════════════════════════════════╝

=== for 循环 ===
基本 for 循环:
0 1 2 3 4 

多个初始化:
  i=0, j=10
  i=1, j=9
  i=2, j=8
  i=3, j=7
  i=4, j=6

省略初始化（外部定义）:
0 1 2
循环后 k=3

有限循环（避免无限）:
0 1 2

=== 增强 for 循环 ===
遍历数组:
1 2 3 4 5

遍历字符串数组:
  Apple
  Banana
  Cherry

增强 for 是迭代器的语法糖:
A B C

=== while 循环 ===
基本 while:
0 1 2 3 4

条件不满足的情况:
  循环未执行（条件一开始为false）

=== do-while 循环 ===
基本 do-while:
0 1 2 3 4

至少执行一次:
  执行了一次，即使条件不满足

=== 循环控制语句 ===
break 语句:
0 1 2 3 4 (在 5 处跳出)

continue 语句:
1 3 5 7 9 (跳过偶数)

带标签的 break:
  i=0, j=0
  i=0, j=1
  i=0, j=2
  i=1, j=0

=== 嵌套循环 ===
乘法表:
1*1=1  
1*2=2  2*2=4
1*3=3  2*3=6  3*3=9
1*4=4  2*4=8  3*4=12 4*4=16
1*5=5  2*5=10 3*5=15 4*5=20 5*5=25
1*6=6  2*6=12 3*6=18 4*6=24 5*6=30 6*6=36
1*7=7  2*7=14 3*7=21 4*7=28 5*7=35 6*7=42 7*7=49
1*8=8  2*8=16 3*8=24 4*8=32 5*8=40 6*8=48 7*8=56 8*8=64
1*9=9  2*9=18 3*9=27 4*9=36 5*9=45 6*9=54 7*9=63 8*9=72 9*9=81

三角形:
    *
   ***
  *****
 *******
*********

══════════════════════════════════════════════════
演示完成！
```

---

## 5. 数组 (array)

### 5.1 ArrayBasics - 数组基础

**代码结构**

```
com.linsir.abc.core.grammar.array.ArrayBasics
├── demonstrateDeclaration() - 数组声明和初始化
├── demonstrateMultiDimensional() - 多维数组
├── demonstrateBasicOperations() - 数组基本操作
└── demonstrateMemory() - 数组内存特性
```

**代码使用场景**

- 学习数组的多种声明和初始化方式
- 理解多维数组和不规则数组
- 掌握数组的基本操作（遍历、查找）
- 理解数组的内存特性和复制

**对应 JDK 模块**: Java 数组类型，`java.util.Arrays`

**测试代码结构**

```java
public static void main(String[] args) {
    ArrayBasics demo = new ArrayBasics();
    demo.demonstrateDeclaration();
    demo.demonstrateMultiDimensional();
    demo.demonstrateBasicOperations();
    demo.demonstrateMemory();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 数组基础演示                          ║
╚════════════════════════════════════════════════╝

=== 数组声明和初始化 ===
方式1 - 声明后分配空间:
  arr1 = [0, 0, 0, 0, 0]

方式2 - 声明同时分配空间:
  arr2 = [0, 0, 0, 0, 0]

方式3 - 直接初始化:
  arr3 = [1, 2, 3, 4, 5]

方式4 - 匿名数组:
  arr4 = [10, 20, 30]

不同数据类型的数组:
  double[]: [1.1, 2.2, 3.3]
  String[]: [Hello, World]
  boolean[]: [true, false, true]

=== 多维数组 ===
二维数组 [3][4]:
  第0行: [0, 0, 0, 0]
  第1行: [0, 0, 0, 0]
  第2行: [0, 0, 0, 0]

不规则数组:
  第0行: [1]
  第1行: [2, 3]
  第2行: [4, 5, 6]

直接初始化的二维数组:
  第0行: [1, 2, 3]
  第1行: [4, 5, 6]
  第2行: [7, 8, 9]

三维数组维度信息:
  第一维长度: 2
  第二维长度: 2
  第三维长度: 2

=== 数组基本操作 ===
原始数组: [5, 2, 8, 1, 9, 3]

数组长度: 6
第一个元素: arr[0] = 5
最后一个元素: arr[5] = 3

修改 arr[0] = 100 后: [100, 2, 8, 1, 9, 3]

遍历数组:
  for循环: 100 2 8 1 9 3
  增强for: 100 2 8 1 9 3

查找 8: 找到，索引=2

=== 数组内存特性 ===
arr1: [1, 2, 3]
arr2: [1, 2, 3]
arr1 == arr2 ? true

修改 arr2[0] = 100 后:
arr1: [100, 2, 3]
arr2: [100, 2, 3]

创建副本后:
arr1 == arr3 ? false
arr1 == arr4 ? false
Arrays.equals(arr1, arr3) ? true

══════════════════════════════════════════════════
演示完成！
```

---

### 5.2 ArrayOperations - 数组操作

**代码结构**

```
com.linsir.abc.core.grammar.array.ArrayOperations
├── demonstrateSorting() - 数组排序
├── demonstrateSearching() - 数组查找
├── demonstrateFillingAndCopying() - 填充和复制
├── demonstrateComparison() - 数组比较
└── demonstrateConversion() - 数组转换
```

**代码使用场景**

- 学习数组排序（快速排序、并行排序）
- 掌握二分查找算法
- 理解数组填充和复制操作
- 学习数组比较方法
- 掌握数组与集合的转换

**对应 JDK 模块**: `java.util.Arrays` 工具类

**测试代码结构**

```java
public static void main(String[] args) {
    ArrayOperations demo = new ArrayOperations();
    demo.demonstrateSorting();
    demo.demonstrateSearching();
    demo.demonstrateFillingAndCopying();
    demo.demonstrateComparison();
    demo.demonstrateConversion();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 数组操作演示                          ║
╚════════════════════════════════════════════════╝

=== 数组排序 ===
原始数组: [5, 2, 8, 1, 9, 3]
升序排序: [1, 2, 3, 5, 8, 9]
部分排序 [1,4): [5, 1, 2, 8, 9, 3]

字符串数组: [banana, apple, cherry, date]
自然排序: [apple, banana, cherry, date]

降序排序: [9, 8, 5, 2, 1]

并行排序: [1, 2, 3, 4, 5, 6, 7, 8, 9]

=== 数组查找 ===
有序数组: [1, 3, 5, 7, 9, 11, 13]
查找 7: 索引=3
查找 6（不存在）: 索引=-4 (插入点=3)
在[1,4)范围内查找 5: 索引=2

=== 数组填充和复制 ===
填充 100: [100, 100, 100, 100, 100]
部分填充 [1,4) 为 50: [100, 50, 50, 50, 100]

原始数组: [1, 2, 3, 4, 5]
完整复制: [1, 2, 3, 4, 5]
复制前3个: [1, 2, 3]
扩展到7个: [1, 2, 3, 4, 5, 0, 0]
复制范围[1,4): [2, 3, 4]

=== 数组比较 ===
arr1: [1, 2, 3]
arr2: [1, 2, 3]
arr3: [1, 2, 4]

引用比较:
arr1 == arr2 ? false

内容比较:
Arrays.equals(arr1, arr2) ? true
Arrays.equals(arr1, arr3) ? false

多维数组比较:
Arrays.equals(m1, m2) ? false
Arrays.deepEquals(m1, m2) ? true

=== 数组转换 ===
数组转字符串: [1, 2, 3, 4, 5]
多维数组: [[1, 2], [3, 4]]

数组转List: [a, b, c]

流操作 - 和: 15, 平均值: 3.0

══════════════════════════════════════════════════
演示完成！
```

---

## 6. 方法 (method)

### 6.1 MethodBasics - 方法基础

**代码结构**

```
com.linsir.abc.core.grammar.method.MethodBasics
├── sayHello() - 无参无返回值
├── add() 重载 - 多版本加法
├── calculateCircleArea() - 静态方法
├── getSquare() - 返回值方法
├── factorial() - 递归方法
├── fibonacci() - 斐波那契数列
└── 私有辅助方法
```

**代码使用场景**

- 学习方法的定义和调用
- 理解方法重载（Overload）
- 掌握静态方法与实例方法的区别
- 学习递归算法实现
- 理解方法返回值

**对应 JDK 模块**: Java 方法定义和调用机制

**测试代码结构**

```java
public static void main(String[] args) {
    MethodBasics demo = new MethodBasics();
    demo.demonstrateMethodCalls();
    demo.demonstrateRecursion();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 方法基础演示                          ║
╚════════════════════════════════════════════════╝

=== 方法调用演示 ===
调用 sayHello():
Hello, World!

调用 add(5, 3): 8
调用 add(2.5, 3.5): 6.0
调用 add(1, 2, 3): 6

调用静态方法 calculateCircleArea(5): 78.53981633974483
通过类名调用: MethodBasics.calculateCircleArea(3): 28.274333882308138

调用 getSquare(4): 16

=== 递归演示 ===
阶乘计算:
  0! = 1
  1! = 1
  2! = 2
  3! = 6
  4! = 24
  5! = 120
  6! = 720
  7! = 5040
  8! = 40320
  9! = 362880
  10! = 3628800

斐波那契数列:
0 1 1 2 3 5 8 13 21 34

当前值: 100
设置后值: 200

══════════════════════════════════════════════════
演示完成！
```

---

### 6.2 ParameterPassing - 参数传递

**代码结构**

```
com.linsir.abc.core.grammar.method.ParameterPassing
├── demonstratePrimitivePassing() - 基本类型传递
├── demonstrateArrayContentModification() - 数组内容修改
├── demonstrateArrayReferenceReassignment() - 数组引用重新赋值
├── demonstrateObjectContentModification() - 对象内容修改
├── demonstrateObjectReferenceReassignment() - 对象引用重新赋值
├── demonstrateStringPassing() - String 传递
└── demonstrateWrapperPassing() - 包装类传递
```

**代码使用场景**

- 深入理解 Java 的参数传递机制（值传递）
- 区分基本类型和引用类型的传递差异
- 理解为什么数组和对象内容可以被修改
- 理解为什么 String 和包装类表现得像值传递

**对应 JDK 模块**: Java 参数传递机制

**测试代码结构**

```java
public static void main(String[] args) {
    ParameterPassing demo = new ParameterPassing();
    demo.demonstratePrimitivePassing();
    demo.demonstrateArrayContentModification();
    demo.demonstrateArrayReferenceReassignment();
    demo.demonstrateObjectContentModification();
    demo.demonstrateObjectReferenceReassignment();
    demo.demonstrateStringPassing();
    demo.demonstrateWrapperPassing();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 参数传递演示                          ║
╚════════════════════════════════════════════════╝

=== 值传递演示 ===

1. 基本类型:
调用前: num = 10
  方法内修改前: num = 10
  方法内修改后: num = 100
调用后: num = 10 (未改变)

2. 数组类型（修改内容）:
调用前: arr = [1, 2, 3]
  方法内修改前: arr[0] = 1
  方法内修改后: arr[0] = 999
调用后: arr = [999, 2, 3] (内容已改变)

3. 数组类型（重新赋值引用）:
调用前: arr2 = [1, 2, 3]
  方法内重新赋值前: arr = [1, 2, 3]
  方法内重新赋值后: arr = [100, 200, 300]
调用后: arr2 = [1, 2, 3] (未改变)

4. 对象类型（修改内容）:
调用前: Person{name='张三', age=25}
  方法内修改前: Person{name='张三', age=25}
  方法内修改后: Person{name='李四', age=30}
调用后: Person{name='李四', age=30} (内容已改变)

5. 对象类型（重新赋值引用）:
调用前: Person{name='张三', age=25}
  方法内重新赋值前: Person{name='张三', age=25}
  方法内重新赋值后: Person{name='王五', age=40}
调用后: Person{name='张三', age=25} (未改变)

6. String类型:
调用前: str = Hello
  方法内修改前: str = Hello
  方法内修改后: str = Hello World
调用后: str = Hello (未改变，因为String不可变)

7. 包装类:
调用前: num = 10
  方法内修改前: num = 10
  方法内修改后: num = 999
调用后: num = 10 (未改变)

══════════════════════════════════════════════════
总结:
• Java 只有值传递，没有引用传递
• 基本类型：传递值的副本
• 引用类型：传递引用的副本（地址的副本）
• 修改对象内容会影响原对象
• 重新赋值引用不会影响原引用
```

---

### 6.3 VarargsDemo - 可变参数

**代码结构**

```
com.linsir.abc.core.grammar.method.VarargsDemo
├── sum(int... numbers) - 求和
├── average(double... values) - 平均值
├── max(int... numbers) - 最大值
├── format(String template, Object... args) - 格式化输出
└── 重载方法优先级演示
```

**代码使用场景**

- 学习可变参数的定义和使用
- 理解可变参数与数组的关系
- 掌握可变参数在格式化输出中的应用
- 理解可变参数方法重载的优先级

**对应 JDK 模块**: Java 可变参数（varargs）

**测试代码结构**

```java
public static void main(String[] args) {
    VarargsDemo demo = new VarargsDemo();
    demo.demonstrateVarargs();
    demo.demonstratePracticalUsage();
    demo.demonstrateVarargsAndArray();
    demo.demonstrateOverloading();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 可变参数演示                          ║
╚════════════════════════════════════════════════╝

=== 可变参数基本用法 ===
sum() = 0
sum(5) = 5
sum(1, 2, 3, 4, 5) = 15
sum(arr) = 60

=== 实际应用 ===
average(10, 20, 30) = 20.0
average(1, 2, 3, 4, 5) = 3.0

max(3, 1, 4, 1, 5, 9, 2, 6) = 9

格式化输出:
姓名: 张三, 年龄: 20, 成绩: 85.50
当前时间: 2026-03-27T01:51:00.718994700

=== 可变参数与数组的关系 ===
传递数组给可变参数方法:
  sum(arr) = 6

两种声明方式的对比:
数组参数: [1, 2, 3]
可变参数: [1, 2, 3]
可变参数: [1, 2, 3]

=== 注意事项 ===
1. 可变参数必须是最后一个参数
   void method(String str, int... nums) // 正确
   void method(int... nums, String str) // 错误!

2. 一个方法只能有一个可变参数
   void method(int... nums, String... strs) // 错误!

3. 重载时的优先级
  调用: testOverloading(int, int)
  调用: testOverloading(int...)

══════════════════════════════════════════════════
演示完成！
```

---

## 7. 面向对象 (oop)

### 7.1 ClassAndObject - 类与对象

**代码结构**

```
com.linsir.abc.core.grammar.oop.ClassAndObject
├── demonstrateClassDefinition() - 类的定义
├── demonstrateEncapsulation() - 封装
├── demonstrateThisKeyword() - this 关键字
├── demonstrateStaticMembers() - 静态成员
├── demonstrateCodeBlocks() - 代码块
└── 内部类 Student, DemoClass
```

**代码使用场景**

- 学习类的定义和对象的创建
- 理解构造方法和构造链
- 掌握封装原则（private + getter/setter）
- 理解 this 关键字的作用
- 学习静态成员和代码块的执行顺序

**对应 JDK 模块**: Java 类和对象机制

**测试代码结构**

```java
public static void main(String[] args) {
    ClassAndObject demo = new ClassAndObject();
    demo.demonstrateClassDefinition();
    demo.demonstrateEncapsulation();
    demo.demonstrateThisKeyword();
    demo.demonstrateStaticMembers();
    demo.demonstrateCodeBlocks();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 类与对象演示                          ║
╚════════════════════════════════════════════════╝

=== 类的定义 ===
默认构造: Student{name='未知', age=18, major='未分配'}
带参构造: Student{name='张三', age=20, major='计算机科学'}
构造链: Student{name='李四', age=18, major='未分配'}

=== 封装演示 ===
姓名: 王五
年龄: 25
专业: 软件工程

数据验证:
  错误: 年龄 -5 无效
  错误: 年龄 150 无效

=== this 关键字 ===
s1 信息: Student{name='张三', age=20, major='计算机'}
s2 信息: Student{name='李四', age=22, major='软件工程'}

链式调用:
s3 信息: Student{name='王五', age=25, major='网络工程'}

=== 静态成员 ===
学校名称: 理工大学
修改后: 科技大学

静态方法:
学生总数: 7
创建2个学生后总数: 9

=== 代码块 ===
创建第一个对象:
  静态代码块执行（只执行一次）
  实例代码块执行（每次创建对象都执行）
  构造方法执行

创建第二个对象:
  实例代码块执行（每次创建对象都执行）
  构造方法执行

══════════════════════════════════════════════════
演示完成！
```

---

### 7.2 Inheritance - 继承

**代码结构**

```
com.linsir.abc.core.grammar.oop.Inheritance
├── demonstrateBasicInheritance() - 基本继承
├── demonstrateMethodOverride() - 方法重写
├── demonstrateConstructorChaining() - 构造方法链
└── demonstratePolymorphism() - 多态演示
```

**代码使用场景**

- 学习继承的基本语法和特点
- 理解方法重写（Override）
- 掌握 super 关键字的使用
- 理解构造方法链的执行顺序
- 初步了解多态的概念

**对应 JDK 模块**: Java 继承机制

**测试代码结构**

```java
public static void main(String[] args) {
    Inheritance demo = new Inheritance();
    demo.demonstrateBasicInheritance();
    demo.demonstrateMethodOverride();
    demo.demonstrateConstructorChaining();
    demo.demonstratePolymorphism();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 继承演示                              ║
╚════════════════════════════════════════════════╝

=== 基本继承 ===
  Animal 构造方法被调用
父类对象:
动物 在吃东西
动物 在睡觉
  Animal 构造方法被调用
  Dog 构造方法被调用

子类对象:
旺财 (狗) 在吃骨头
旺财 在睡觉
旺财 汪汪叫

狗的信息:
  名字: 旺财
  年龄: 3
  品种: 金毛

=== 方法重写 ===
  Animal 构造方法被调用
  Animal 构造方法被调用
  Dog 构造方法被调用
  Animal 构造方法被调用
  Cat 构造方法被调用
Animal 的 eat():
动物 在吃东西

Dog 的 eat() (重写):
旺财 (狗) 在吃骨头

Cat 的 eat() (重写):
咪咪 (猫) 在吃鱼

使用 super 调用父类方法:
狗的 eatWithSuper():
旺财 在吃东西

=== 构造方法链 ===
创建 Dog 对象:
  Animal 构造方法被调用
  Dog 构造方法被调用

创建 Cat 对象:
  Animal 构造方法被调用
  Cat 构造方法被调用

=== 多态演示 ===
  Animal 构造方法被调用
  Dog 构造方法被调用
  Animal 构造方法被调用
  Cat 构造方法被调用
animal1 (实际是 Dog):
旺财 (狗) 在吃骨头

animal2 (实际是 Cat):
咪咪 (猫) 在吃鱼

类型检查:
animal1 是 Dog 类型
旺财 汪汪叫

══════════════════════════════════════════════════
演示完成！
```

---

### 7.3 Polymorphism - 多态

**代码结构**

```
com.linsir.abc.core.grammar.oop.Polymorphism
├── demonstrateRuntimePolymorphism() - 运行时多态
├── demonstrateDynamicBinding() - 动态绑定
├── demonstrateTypeCasting() - 类型转换
└── demonstratePolymorphicArray() - 多态数组
```

**代码使用场景**

- 深入理解运行时多态（动态绑定）
- 掌握向上转型和向下转型
- 学习 instanceof 运算符的使用
- 理解多态数组的应用

**对应 JDK 模块**: Java 多态机制

**测试代码结构**

```java
public static void main(String[] args) {
    Polymorphism demo = new Polymorphism();
    demo.demonstrateRuntimePolymorphism();
    demo.demonstrateDynamicBinding();
    demo.demonstrateTypeCasting();
    demo.demonstratePolymorphicArray();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 多态演示                              ║
╚════════════════════════════════════════════════╝

=== 运行时多态 ===
遍历形状数组:
  圆形 面积: 78.53981633974483
  矩形 面积: 20.0
  三角形 面积: 6.0

=== 动态绑定 ===
编译时类型: Shape
运行时类型: Circle
调用 calculateArea(): 28.274333882308138

=== 类型转换 ===
向上转型:
  Circle -> Shape (自动)

向下转型:
  Shape -> Circle (显式转换成功)
  半径: 3.0

错误的类型转换:
  rectangle 不是 Circle 类型，不能转换

模式匹配 instanceof (Java 16+):
  是 Circle，半径: 5.0

=== 多态数组 ===
总面积: 54.27433388230814

══════════════════════════════════════════════════
演示完成！
```

---

### 7.4 AbstractClass - 抽象类

**代码结构**

```
com.linsir.abc.core.grammar.oop.AbstractClass
├── demonstrateAbstractClass() - 抽象类演示
└── demonstrateTemplateMethod() - 模板方法模式
```

**代码使用场景**

- 学习抽象类和抽象方法的定义
- 理解抽象类的特点（不能实例化、可有成员变量）
- 掌握模板方法设计模式
- 理解抽象类在代码复用中的作用

**对应 JDK 模块**: Java 抽象类机制

**测试代码结构**

```java
public static void main(String[] args) {
    AbstractClass demo = new AbstractClass();
    demo.demonstrateAbstractClass();
    demo.demonstrateTemplateMethod();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 抽象类演示                            ║
╚════════════════════════════════════════════════╝

=== 抽象类演示 ===
Dog:
旺财 在吃东西
旺财 在睡觉
旺财 汪汪叫

Cat:
咪咪 在吃东西
咪咪 在睡觉
咪咪 喵喵叫

=== 模板方法模式 ===
处理 CSV 文件:
  读取文件: data.csv
  解析 CSV 格式数据
  验证数据
  保存数据到数据库

处理 XML 文件:
  读取文件: data.xml
  解析 XML 格式数据
  验证数据
  保存数据到数据库

══════════════════════════════════════════════════
演示完成！
```

---

### 7.5 InterfaceDemo - 接口

**代码结构**

```
com.linsir.abc.core.grammar.oop.InterfaceDemo
├── demonstrateBasicInterface() - 基本接口
├── demonstrateDefaultMethod() - 默认方法
├── demonstrateMultipleInterfaces() - 多接口实现
└── demonstrateStaticMethod() - 静态方法
```

**代码使用场景**

- 学习接口的定义和实现
- 理解 Java 8+ 默认方法的作用
- 掌握多接口实现
- 理解接口静态方法的使用

**对应 JDK 模块**: Java 接口机制

**测试代码结构**

```java
public static void main(String[] args) {
    InterfaceDemo demo = new InterfaceDemo();
    demo.demonstrateBasicInterface();
    demo.demonstrateDefaultMethod();
    demo.demonstrateMultipleInterfaces();
    demo.demonstrateStaticMethod();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 接口演示                              ║
╚════════════════════════════════════════════════╝

=== 基本接口 ===
Dog 实现 Animal:
旺财 在吃东西
旺财 在睡觉

Cat 实现 Animal:
咪咪 在吃东西
咪咪 在睡觉

使用接口引用:
旺财 在吃东西

=== 默认方法 ===
Car:
汽车启动
汽车停止
汽车鸣笛: 嘀嘀!

Bike:
自行车启动
自行车停止
发出声音

=== 多接口实现 ===
作为 Phone:
拨打电话: 123456789

作为 Camera:
拍照

作为 MusicPlayer:
播放音乐

=== 接口静态方法 ===
Animal 是动物接口
Vehicle 是交通工具接口

══════════════════════════════════════════════════
演示完成！
```

---

## 8. 异常处理 (exception)

### 8.1 ExceptionHandling - 异常处理

**代码结构**

```
com.linsir.abc.core.grammar.exception.ExceptionHandling
├── demonstrateTryCatch() - try-catch 基本用法
├── demonstrateMultipleCatch() - 多重 catch
├── demonstrateFinally() - finally 块
├── demonstrateTryWithResources() - try-with-resources
├── demonstrateThrowing() - 抛出异常
└── demonstrateExceptionChaining() - 异常链
```

**代码使用场景**

- 学习 try-catch 异常捕获
- 掌握多重 catch 和异常类型匹配
- 理解 finally 块的执行时机
- 学习 try-with-resources 自动资源管理
- 掌握异常抛出和异常链

**对应 JDK 模块**: Java 异常处理机制

**测试代码结构**

```java
public static void main(String[] args) {
    ExceptionHandling demo = new ExceptionHandling();
    demo.demonstrateTryCatch();
    demo.demonstrateMultipleCatch();
    demo.demonstrateFinally();
    demo.demonstrateTryWithResources();
    demo.demonstrateThrowing();
    demo.demonstrateExceptionChaining();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 异常处理演示                          ║
╚════════════════════════════════════════════════╝

=== try-catch 基本用法 ===
捕获到算术异常: / by zero
捕获到数组越界异常: Index 5 out of bounds for length 3
程序继续执行...

=== 多重 catch ===
输入: 123, 结果: 0
数字格式错误: abc
数字格式错误: null

=== finally 块 ===
情况1: 正常执行
  try 块开始
  try 块正常结束
  finally 块: 总是执行

情况2: 发生异常
  try 块开始
  catch 块: 模拟异常
  finally 块: 总是执行

=== try-with-resources ===
  打开资源: 资源1
  使用资源: 资源1
  关闭资源: 资源1

多个资源:
  打开资源: 资源A
  打开资源: 资源B
  使用资源: 资源A
  使用资源: 资源B
  关闭资源: 资源B
  关闭资源: 资源A

=== 抛出异常 ===
年龄有效: 25
主方法捕获: 年龄必须在 0-150 之间: -5

=== 异常链 ===
捕获异常: method1 出错
原因: java.lang.IllegalArgumentException: method2 参数错误

══════════════════════════════════════════════════
演示完成！
```

---

### 8.2 CustomExceptions - 自定义异常

**代码结构**

```
com.linsir.abc.core.grammar.exception.CustomExceptions
├── demonstrateCheckedException() - 自定义检查异常
├── demonstrateRuntimeException() - 自定义运行时异常
├── InsufficientFundsException - 余额不足异常
├── InvalidAmountException - 金额无效异常
└── ValidationException - 验证异常
```

**代码使用场景**

- 学习自定义检查异常（extends Exception）
- 学习自定义运行时异常（extends RuntimeException）
- 理解两种异常的使用场景
- 掌握异常信息的封装

**对应 JDK 模块**: Java 异常类层次结构

**测试代码结构**

```java
public static void main(String[] args) {
    CustomExceptions demo = new CustomExceptions();
    demo.demonstrateCheckedException();
    demo.demonstrateRuntimeException();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 自定义异常演示                        ║
╚════════════════════════════════════════════════╝

=== 自定义检查异常 ===
存入 500.0，当前余额: 1500.0
取出 200.0，当前余额: 1300.0
余额不足: 余额不足
当前余额: 1300.0
取款金额: 2000.0

=== 自定义运行时异常 ===
注册成功
注册失败: 邮箱格式无效
错误字段: email
注册失败: 密码长度至少6位

══════════════════════════════════════════════════
演示完成！
```

---

## 9. 泛型 (generic)

### 9.1 GenericClass - 泛型类

**代码结构**

```
com.linsir.abc.core.grammar.generic.GenericClass
├── Box<T> - 单类型参数泛型类
├── Pair<K, V> - 多类型参数泛型类
├── Triple<A, B, C> - 三类型参数泛型类
├── NumberBox<T extends Number> - 上界泛型
└── 各种泛型类的使用演示
```

**代码使用场景**

- 学习泛型类的定义和使用
- 理解类型参数（Type Parameter）
- 掌握多类型参数泛型类
- 学习泛型边界（extends）

**对应 JDK 模块**: Java 泛型类，`java.util` 集合框架

**测试代码结构**

```java
public static void main(String[] args) {
    GenericClass demo = new GenericClass();
    demo.demonstrateGenericClass();
    demo.demonstrateGenericCollections();
    demo.demonstrateMultipleTypeParameters();
    demo.demonstrateGenericBounds();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 泛型类演示                            ║
╚════════════════════════════════════════════════╝

=== 泛型类基本用法 ===
Integer 类型: 100
String 类型: Hello
Person 类型: Person{name='张三', age=25}

=== 泛型集合 ===
String 列表:
  张三
  李四

Person 列表:
  Person{name='王五', age=30}
  Person{name='赵六', age=35}

=== 多个类型参数 ===
Key: 年龄, Value: 25
姓名: 张三, 年龄: 25, 成绩: 85.5

=== 泛型边界 ===
Number Box: 100
Integer Box: 200
Double Box: 3.14
Box value: 100
Box value: 200
Box value: 3.14

══════════════════════════════════════════════════
演示完成！
```

---

### 9.2 GenericMethod - 泛型方法

**代码结构**

```
com.linsir.abc.core.grammar.generic.GenericMethod
├── printArray(T[] array) - 泛型方法打印数组
├── swap(T[] array, int i, int j) - 泛型方法交换元素
├── max(T a, T b) - 带边界的泛型方法
└── findMax(T[] array) - 静态泛型方法
```

**代码使用场景**

- 学习泛型方法的定义
- 理解类型推断机制
- 掌握带边界的泛型方法
- 学习静态泛型方法

**对应 JDK 模块**: Java 泛型方法，`java.util.Collections`

**测试代码结构**

```java
public static void main(String[] args) {
    GenericMethod demo = new GenericMethod();
    demo.demonstrateGenericMethod();
    demo.demonstrateGenericSwap();
    demo.demonstrateBoundedGenericMethod();
    demo.demonstrateStaticGenericMethod();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 泛型方法演示                          ║
╚════════════════════════════════════════════════╝

=== 泛型方法基本用法 ===
Integer 数组: [1, 2, 3, 4, 5]
String 数组: [Hello, World, Java]
Double 数组: [1.1, 2.2, 3.3]

=== 泛型方法交换元素 ===
交换前:
[Apple, Banana, Cherry]
交换后 (0 和 2):
[Cherry, Banana, Apple]

=== 带边界的泛型方法 ===
max(10, 20) = 20
max("Apple", "Banana") = Banana
max(3.14, 2.71) = 3.14

=== 静态泛型方法 ===
数组最大值: 5
字符串数组最大值: cherry

══════════════════════════════════════════════════
演示完成！
```

---

### 9.3 Wildcards - 泛型通配符

**代码结构**

```
com.linsir.abc.core.grammar.generic.Wildcards
├── demonstrateUnboundedWildcard() - 无界通配符 <?>
├── demonstrateUpperBoundedWildcard() - 上界通配符 <? extends T>
├── demonstrateLowerBoundedWildcard() - 下界通配符 <? super T>
└── demonstratePECS() - PECS 原则
```

**代码使用场景**

- 理解无界通配符的使用场景
- 掌握上界通配符（Producer Extends）
- 掌握下界通配符（Consumer Super）
- 理解 PECS 原则（Producer Extends Consumer Super）

**对应 JDK 模块**: Java 泛型通配符

**测试代码结构**

```java
public static void main(String[] args) {
    Wildcards demo = new Wildcards();
    demo.demonstrateUnboundedWildcard();
    demo.demonstrateUpperBoundedWildcard();
    demo.demonstrateLowerBoundedWildcard();
    demo.demonstratePECS();
    demo.demonstrateWildcardRestrictions();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 泛型通配符演示                        ║
╚════════════════════════════════════════════════╝

=== 无界通配符 ===
读取: Hello
列表大小: 3

=== 上界通配符 (<? extends T>) ===
Integer 列表总和: 6.0
Double 列表总和: 3.3000000000000003

=== 下界通配符 (<? super T>) ===
Number 列表: [1, 2, 3]
Object 列表: [1, 2, 3]

=== PECS 原则 ===
Producer Extends, Consumer Super
从列表读取（Producer）:
  1
  2
  3

向列表写入（Consumer）:
复制后的列表: [1, 2, 3]

=== 通配符限制 ===
上界通配符列表: [1, 2]
下界通配符列表: [1, 2]

══════════════════════════════════════════════════
演示完成！

总结:
• <?> - 无界通配符：只读，可写入 null
• <? extends T> - 上界：只读（Producer）
• <? super T> - 下界：可写（Consumer）
• PECS 原则：Producer Extends, Consumer Super
```

---

## 10. 注解 (annotation)

### 10.1 BuiltInAnnotations - 内置注解

**代码结构**

```
com.linsir.abc.core.grammar.annotation.BuiltInAnnotations
├── demonstrateOverride() - @Override 注解
├── demonstrateDeprecated() - @Deprecated 注解
├── demonstrateSuppressWarnings() - @SuppressWarnings 注解
├── demonstrateFunctionalInterface() - @FunctionalInterface 注解
├── demonstrateSafeVarargs() - @SafeVarargs 注解
└── demonstrateReflection() - 通过反射读取注解
```

**代码使用场景**

- 学习常用内置注解的使用
- 理解 @Override 的编译时检查作用
- 掌握 @Deprecated 标记过时 API
- 了解 @FunctionalInterface 函数式接口
- 学习通过反射获取注解信息

**对应 JDK 模块**: `java.lang` 注解类型，`java.lang.reflect` 反射

**测试代码结构**

```java
public static void main(String[] args) {
    BuiltInAnnotations demo = new BuiltInAnnotations();
    demo.demonstrateOverride();
    demo.demonstrateDeprecated();
    demo.demonstrateSuppressWarnings();
    demo.demonstrateFunctionalInterface();
    demo.demonstrateSafeVarargs();
    demo.demonstrateReflection();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 内置注解演示                          ║
╚════════════════════════════════════════════════╝

=== @Override 注解 ===
汪汪叫

@Override 作用:
  - 表明方法重写了父类方法
  - 编译器会检查方法签名是否正确
  - 提高代码可读性

=== @Deprecated 注解 ===
这是已过时的方法

@Deprecated 作用:
  - 标记已过时的类、方法或字段
  - 编译器会产生警告
  - 建议用户使用替代方案

=== @SuppressWarnings 注解 ===
@SuppressWarnings 作用:
  - 抑制编译器警告
  - 常用值: unchecked, deprecation, rawtypes
  - 应谨慎使用，避免隐藏真正的问题

=== @FunctionalInterface 注解 ===
10 + 5 = 15
10 * 5 = 50

@FunctionalInterface 作用:
  - 标记函数式接口
  - 编译器检查是否只有一个抽象方法
  - 可以使用 Lambda 表达式

=== @SafeVarargs 注解 ===
  参数: A
  参数: B
  参数: C

@SafeVarargs 作用:
  - 标记可变参数方法不会对其参数执行不安全的操作
  - 只能用于 static 或 final 方法

=== 通过反射读取注解 ===
类注解 @Service:
  name: MyService
  version: 2.0

方法 doSomething() 被标记为 @Deprecated

══════════════════════════════════════════════════
演示完成！
```

---

### 10.2 CustomAnnotations - 自定义注解

**代码结构**

```
com.linsir.abc.core.grammar.annotation.CustomAnnotations
├── @Service - 服务注解（类级别）
├── @Transactional - 事务注解（方法级别）
├── @Permission - 权限注解（可重复）
├── @Permissions - 权限容器注解
├── AnnotationProcessor - 注解处理器
└── 注解使用示例类 UserService
```

**代码使用场景**

- 学习自定义注解的定义（使用元注解）
- 理解元注解的作用（@Retention, @Target, @Documented, @Inherited, @Repeatable）
- 掌握注解处理器的编写
- 学习可重复注解的使用

**对应 JDK 模块**: Java 注解定义和处理

**测试代码结构**

```java
public static void main(String[] args) {
    CustomAnnotations demo = new CustomAnnotations();
    demo.demonstrateCustomAnnotations();
    demo.demonstrateAnnotationProcessing();
    demo.demonstrateRepeatableAnnotations();
}
```

**测试预期结果**

```
╔════════════════════════════════════════════════╗
║     Java 自定义注解演示                        ║
╚════════════════════════════════════════════════╝

=== 自定义注解使用 ===
调用方法:
  创建用户: 张三
  删除用户 ID: 123
  查询用户 ID: 456

=== 注解处理器 ===
服务信息:
  名称: UserService
  描述: 用户管理服务
  是否单例: true

方法信息:
    已过时: 是
  方法: createUser
    需要事务: 是
    只读: false
    超时: 30秒
  方法: getUser
    需要事务: 是
    只读: true
    超时: -1秒
  方法: deleteUser
    需要事务: 是
    只读: false
    超时: -1秒

=== 重复注解 ===
权限列表:
  - user:read (读取用户)
  - user:write (写入用户)
  - user:delete (删除用户)

通过容器获取:
  - user:read
  - user:write
  - user:delete

══════════════════════════════════════════════════
演示完成！

元注解说明:
• @Retention - 注解保留策略
• @Target - 注解应用目标
• @Documented - 包含在 Javadoc 中
• @Inherited - 子类继承注解
• @Repeatable - 可重复注解
```

---

## 11. 总结

### 11.1 代码结构总览

本文档涵盖了 Java 语法基础的 10 大模块，共 27 个示例类：

| 模块 | 类数量 | 主要内容 |
|------|--------|----------|
| 数据类型 | 3 | 基本类型、引用类型、类型转换 |
| 变量与常量 | 2 | 作用域、final、枚举 |
| 运算符 | 3 | 算术、位运算、逻辑运算 |
| 流程控制 | 2 | 条件语句、循环语句 |
| 数组 | 2 | 数组基础、数组操作 |
| 方法 | 3 | 方法基础、参数传递、可变参数 |
| 面向对象 | 5 | 类、继承、多态、抽象类、接口 |
| 异常处理 | 2 | 异常处理、自定义异常 |
| 泛型 | 3 | 泛型类、泛型方法、通配符 |
| 注解 | 2 | 内置注解、自定义注解 |

### 11.2 对应 JDK 模块

所有示例代码均对应 JDK 标准库的相关模块：

- **java.lang**: 基本类型、包装类、String、Object、异常类、注解
- **java.util**: Arrays、集合框架、日期时间
- **java.lang.reflect**: 反射机制

### 11.3 测试执行方式

```bash
# 编译所有类
cd linsir-abc-core/src/main/java
javac -encoding UTF-8 -d . com/linsir/abc/core/grammar/**/*.java

# 运行单个类测试
java com.linsir.abc.core.grammar.datatype.PrimitiveTypes
java com.linsir.abc.core.grammar.oop.ClassAndObject
# ... 其他类
```

### 11.4 学习建议

1. **按顺序学习**: 建议按照文档顺序从基础到高级逐步学习
2. **动手实践**: 每个类都有 main 方法，可以直接运行观察结果
3. **修改实验**: 修改代码参数，观察不同输入的输出结果
4. **对比理解**: 对比相似概念（如抽象类 vs 接口、上界 vs 下界）
5. **实际应用**: 思考每个语法特性在实际开发中的应用场景