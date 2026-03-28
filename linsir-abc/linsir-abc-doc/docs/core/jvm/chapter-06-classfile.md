# 第6章 类文件结构

## 6.1 概述

Java虚拟机不和包括Java在内的任何语言绑定，它只与"Class文件"这种特定的二进制文件格式所关联。Class文件中包含了Java虚拟机指令集、符号表以及若干其他辅助信息。基于安全方面的考虑，Java虚拟机规范要求在Class文件中使用许多强制性的语法和结构化约束，但任一门功能性语言都可以表示为一个能被Java虚拟机所接受的有效的Class文件。

本章将详细讲解Class文件的结构、各个组成部分的格式以及含义。

---

## 6.2 无关性的基石

Java技术能够保持良好的向后兼容性，Class文件结构的设计是一个关键因素。Java虚拟机具有语言无关性的特性，它不关心Class的来源是何种语言，只要符合Class文件格式规范即可。

**平台无关性**：Java虚拟机可以运行在各种硬件平台和操作系统上，只要这些平台上有对应的Java虚拟机实现。

**语言无关性**：Java虚拟机不与任何程序语言绑定，其他语言（如Kotlin、Scala、Groovy等）编译后也可以生成符合规范的Class文件，在Java虚拟机上运行。

```
源代码（Java/Kotlin/Scala）
       ↓
   编译器
       ↓
  Class文件（平台无关、语言无关）
       ↓
  Java虚拟机（Windows/Linux/Mac）
       ↓
   机器码（x86/ARM）
```

---

## 6.3 Class类文件的结构

Class文件是一组以8位字节为基础单位的二进制流，各个数据项目严格按照顺序紧凑地排列在Class文件之中，中间没有添加任何分隔符。当遇到需要占用8位字节以上空间的数据项时，则会按照高位在前的方式分割成若干个8位字节进行存储。

Class文件格式采用一种类似于C语言结构体的伪结构来存储数据，这种伪结构中只有两种数据类型：
- **无符号数**：基本的数据类型，以u1、u2、u4、u8来分别代表1个字节、2个字节、4个字节和8个字节的无符号数
- **表**：由多个无符号数或者其他表作为数据项构成的复合数据类型，所有表都习惯性地以"_info"结尾

Class文件的整体结构：

| 类型 | 名称 | 数量 | 说明 |
|------|------|------|------|
| u4 | magic | 1 | 魔数 |
| u2 | minor_version | 1 | 次版本号 |
| u2 | major_version | 1 | 主版本号 |
| u2 | constant_pool_count | 1 | 常量池容量计数 |
| cp_info | constant_pool | constant_pool_count-1 | 常量池 |
| u2 | access_flags | 1 | 访问标志 |
| u2 | this_class | 1 | 类索引 |
| u2 | super_class | 1 | 父类索引 |
| u2 | interfaces_count | 1 | 接口计数 |
| u2 | interfaces | interfaces_count | 接口索引集合 |
| u2 | fields_count | 1 | 字段计数 |
| field_info | fields | fields_count | 字段表集合 |
| u2 | methods_count | 1 | 方法计数 |
| method_info | methods | methods_count | 方法表集合 |
| u2 | attributes_count | 1 | 属性计数 |
| attribute_info | attributes | attributes_count | 属性表集合 |

---

### 6.3.1 魔数与Class文件的版本

**魔数（Magic Number）**

每个Class文件的头4个字节称为魔数，它的唯一作用是确定这个文件是否为一个能被虚拟机接受的Class文件。

Class文件的魔数值为：`0xCAFEBABE`（咖啡宝贝），这个名称的由来很有趣味性，它象征着Java这个商标名称。

```
Class文件前8个字节示例：
CA FE BA BE 00 00 00 34
|_______| |___| |___|
   魔数    次版本 主版本
```

**版本号**

紧接着魔数的4个字节存储的是Class文件的版本号：
- 第5和第6个字节是**次版本号（Minor Version）**
- 第7和第8个字节是**主版本号（Major Version）**

Java的版本号是从45开始的，JDK 1.1之后的每个JDK大版本发布主版本号向上加1（JDK 1.0~1.1使用了45.0~45.3的版本号）。

| JDK版本 | 主版本号（十进制） | 主版本号（十六进制） |
|---------|------------------|---------------------|
| JDK 1.1 | 45 | 0x002D |
| JDK 1.2 | 46 | 0x002E |
| JDK 1.3 | 47 | 0x002F |
| JDK 1.4 | 48 | 0x0030 |
| JDK 5 | 49 | 0x0031 |
| JDK 6 | 50 | 0x0032 |
| JDK 7 | 51 | 0x0033 |
| JDK 8 | 52 | 0x0034 |
| JDK 9 | 53 | 0x0035 |
| JDK 10 | 54 | 0x0036 |
| JDK 11 | 55 | 0x0037 |
| JDK 17 | 61 | 0x003D |
| JDK 21 | 65 | 0x0041 |

**查看Class文件版本**

```bash
# 使用javap命令
javap -verbose HelloWorld.class | grep "major version"

# 使用十六进制查看器查看前8个字节
hexdump -C HelloWorld.class | head -1
```

**版本兼容性**

高版本的JDK能向下兼容以前版本的Class文件，但不能运行以后版本的Class文件。即使文件格式并未发生任何变化，虚拟机也必须拒绝执行超过其版本号的Class文件。

---

### 6.3.2 常量池

常量池是Class文件中与其他项目关联最多的数据类型，也是占用Class文件空间最大的数据项目之一，同时它还是在Class文件中第一个出现的表类型数据项目。

**常量池容量计数**

由于常量池中常量的数量是不固定的，所以在常量池的入口需要放置一项u2类型的数据，代表常量池容量计数值（constant_pool_count）。

注意：这个容量计数是从1而不是0开始的。设计者将第0项常量空出来是有特殊考虑的，这样做的目的在于满足后面某些指向常量池的索引值的数据在特定情况下需要表达"不引用任何一个常量池项目"的含义。

**常量池中的常量类型**

常量池中主要存放两大类常量：
1. **字面量（Literal）**：如文本字符串、声明为final的常量值等
2. **符号引用（Symbolic References）**：
   - 类和接口的全限定名
   - 字段的名称和描述符
   - 方法的名称和描述符
   - 方法句柄和方法类型
   - 动态调用点和动态常量

**常量池项目类型表**

| 类型 | 标志 | 描述 |
|------|------|------|
| CONSTANT_Utf8_info | 1 | UTF-8编码的字符串 |
| CONSTANT_Integer_info | 3 | 整型字面量 |
| CONSTANT_Float_info | 4 | 浮点型字面量 |
| CONSTANT_Long_info | 5 | 长整型字面量 |
| CONSTANT_Double_info | 6 | 双精度浮点型字面量 |
| CONSTANT_Class_info | 7 | 类或接口的符号引用 |
| CONSTANT_String_info | 8 | 字符串类型字面量 |
| CONSTANT_Fieldref_info | 9 | 字段的符号引用 |
| CONSTANT_Methodref_info | 10 | 类中方法的符号引用 |
| CONSTANT_InterfaceMethodref_info | 11 | 接口中方法的符号引用 |
| CONSTANT_NameAndType_info | 12 | 字段或方法的部分符号引用 |
| CONSTANT_MethodHandle_info | 15 | 方法句柄 |
| CONSTANT_MethodType_info | 16 | 方法类型 |
| CONSTANT_Dynamic_info | 17 | 动态计算常量 |
| CONSTANT_InvokeDynamic_info | 18 | 动态调用点 |
| CONSTANT_Module_info | 19 | 模块（JDK 9+） |
| CONSTANT_Package_info | 20 | 包（JDK 9+） |

**常量池项目结构示例**

1. **CONSTANT_Class_info**：类或接口的符号引用
```
CONSTANT_Class_info {
    u1 tag = 7;           // 标志位
    u2 name_index;        // 指向全限定名常量项的索引
}
```

2. **CONSTANT_Utf8_info**：UTF-8编码的字符串
```
CONSTANT_Utf8_info {
    u1 tag = 1;           // 标志位
    u2 length;            // 字节长度
    u1 bytes[length];     // 字节数组
}
```

3. **CONSTANT_Fieldref_info**：字段的符号引用
```
CONSTANT_Fieldref_info {
    u1 tag = 9;           // 标志位
    u2 class_index;       // 指向声明字段的类或接口
    u2 name_and_type_index; // 指向字段描述符
}
```

**查看常量池**

```bash
javap -verbose HelloWorld.class
```

输出示例：
```
Constant pool:
   #1 = Methodref          #6.#20         // java/lang/Object."<init>":()V
   #2 = Fieldref           #21.#22        // java/lang/System.out:Ljava/io/PrintStream;
   #3 = String             #23            // Hello, World!
   #4 = Methodref          #24.#25        // java/io/PrintStream.println:(Ljava/lang/String;)V
   #5 = Class              #26            // HelloWorld
   #6 = Class              #27            // java/lang/Object
   ...
```

---

### 6.3.3 访问标志

在常量池结束之后，紧接着的两个字节代表访问标志（access_flags），这个标志用于识别一些类或者接口层次的访问信息。

**访问标志表**

| 标志名称 | 标志值 | 含义 |
|----------|--------|------|
| ACC_PUBLIC | 0x0001 | 是否为public类型 |
| ACC_FINAL | 0x0010 | 是否被声明为final |
| ACC_SUPER | 0x0020 | 是否允许使用invokespecial字节码指令的新语义 |
| ACC_INTERFACE | 0x0200 | 标识这是一个接口 |
| ACC_ABSTRACT | 0x0400 | 是否为abstract类型 |
| ACC_SYNTHETIC | 0x1000 | 标识这个类并非由用户代码产生 |
| ACC_ANNOTATION | 0x2000 | 标识这是一个注解 |
| ACC_ENUM | 0x4000 | 标识这是一个枚举 |
| ACC_MODULE | 0x8000 | 标识这是一个模块（JDK 9+） |

access_flags中一共有16个标志位可以使用，当前只定义了其中9个，没有使用到的标志位要求一律为0。

**示例**

一个普通类（public class HelloWorld）：
- ACC_PUBLIC（0x0001）
- ACC_SUPER（0x0020）

access_flags = 0x0001 | 0x0020 = 0x0021

---

### 6.3.4 类索引、父类索引与接口索引集合

类索引（this_class）和父类索引（super_class）都是一个u2类型的数据，而接口索引集合（interfaces）是一组u2类型的数据的集合。

**类索引**

类索引用于确定这个类的全限定名，它指向常量池中一个类型为CONSTANT_Class_info的类描述符常量。

**父类索引**

父类索引用于确定这个类的父类的全限定名。由于Java语言不允许多重继承，所以父类索引只有一个，除了java.lang.Object之外，所有的Java类都有父类，因此除了java.lang.Object外，所有Java类的父类索引都不为0。

**接口索引集合**

接口索引集合用来描述这个类实现了哪些接口，这些被实现的接口将按implements关键字（如果这个Class文件表示的是一个接口，则应当是extends关键字）后的接口顺序从左到右排列在接口索引集合中。

**结构**

```
ClassFile {
    ...
    u2 this_class;          // 指向常量池中的类描述符
    u2 super_class;         // 指向常量池中的父类描述符（Object为0）
    u2 interfaces_count;    // 接口数量
    u2 interfaces[interfaces_count]; // 接口索引数组
    ...
}
```

---

### 6.3.5 字段表集合

字段表（field_info）用于描述接口或者类中声明的变量。字段包括类级变量以及实例级变量，但不包括在方法内部声明的局部变量。

**字段表结构**

```
field_info {
    u2 access_flags;        // 访问标志
    u2 name_index;          // 字段名索引（指向常量池）
    u2 descriptor_index;    // 描述符索引（指向常量池）
    u2 attributes_count;    // 属性数量
    attribute_info attributes[attributes_count]; // 属性表
}
```

**字段访问标志**

| 标志名称 | 标志值 | 含义 |
|----------|--------|------|
| ACC_PUBLIC | 0x0001 | 字段是否为public |
| ACC_PRIVATE | 0x0002 | 字段是否为private |
| ACC_PROTECTED | 0x0004 | 字段是否为protected |
| ACC_STATIC | 0x0008 | 字段是否为static |
| ACC_FINAL | 0x0010 | 字段是否为final |
| ACC_VOLATILE | 0x0040 | 字段是否为volatile |
| ACC_TRANSIENT | 0x0080 | 字段是否为transient |
| ACC_SYNTHETIC | 0x1000 | 字段是否由编译器自动产生 |
| ACC_ENUM | 0x4000 | 字段是否为enum类型 |

**描述符**

描述符的作用是用来描述字段的数据类型、方法的参数列表（包括数量、类型以及顺序）和返回值。

**基本类型描述符**

| 字符 | 含义 |
|------|------|
| B | byte |
| C | char |
| D | double |
| F | float |
| I | int |
| J | long |
| S | short |
| Z | boolean |
| V | void |
| L | 对象类型，如Ljava/lang/Object; |
| [ | 数组类型，如[I表示int[] |

**示例**

```java
private static final int MAX_SIZE = 100;
```

对应的字段表：
- access_flags: ACC_PRIVATE | ACC_STATIC | ACC_FINAL = 0x001A
- name_index: 指向"MAX_SIZE"的常量池索引
- descriptor_index: 指向"I"（int类型）的常量池索引

---

### 6.3.6 方法表集合

方法表（method_info）的结构与字段表一样，依次包括访问标志（access_flags）、名称索引（name_index）、描述符索引（descriptor_index）、属性表集合（attributes）几项。

**方法表结构**

```
method_info {
    u2 access_flags;        // 访问标志
    u2 name_index;          // 方法名索引
    u2 descriptor_index;    // 方法描述符索引
    u2 attributes_count;    // 属性数量
    attribute_info attributes[attributes_count]; // 属性表
}
```

**方法访问标志**

| 标志名称 | 标志值 | 含义 |
|----------|--------|------|
| ACC_PUBLIC | 0x0001 | 方法是否为public |
| ACC_PRIVATE | 0x0002 | 方法是否为private |
| ACC_PROTECTED | 0x0004 | 方法是否为protected |
| ACC_STATIC | 0x0008 | 方法是否为static |
| ACC_FINAL | 0x0010 | 方法是否为final |
| ACC_SYNCHRONIZED | 0x0020 | 方法是否为synchronized |
| ACC_BRIDGE | 0x0040 | 方法是否由编译器产生的桥接方法 |
| ACC_VARARGS | 0x0080 | 方法是否接受不定参数 |
| ACC_NATIVE | 0x0100 | 方法是否为native |
| ACC_ABSTRACT | 0x0400 | 方法是否为abstract |
| ACC_STRICTFP | 0x0800 | 方法是否为strictfp |
| ACC_SYNTHETIC | 0x1000 | 方法是否由编译器自动产生 |

**方法描述符**

方法描述符的格式：
```
(参数列表)返回值
```

示例：
```java
public int add(int a, int b)
```
描述符：`(II)I`

```java
public String concat(String a, String b)
```
描述符：`(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;`

```java
public static void main(String[] args)
```
描述符：`([Ljava/lang/String;)V`

**编译器自动生成的方法**

在Java类中，如果用户没有显式定义构造函数，编译器会自动添加一个无参构造函数（`<init>`）。

类构造器`<clinit>`方法是由编译器自动收集类中的所有类变量的赋值动作和静态语句块中的语句合并产生的。

---

### 6.3.7 属性表集合

属性表（attribute_info）在Class文件、字段表、方法表都可以携带自己的属性表集合，以用于描述某些场景专有的信息。

**属性表通用结构**

```
attribute_info {
    u2 attribute_name_index;    // 属性名索引
    u4 attribute_length;        // 属性长度
    u1 info[attribute_length];  // 属性具体内容
}

```

**常见属性**

| 属性名称 | 使用位置 | 含义 |
|----------|----------|------|
| Code | 方法表 | Java代码编译成的字节码指令 |
| ConstantValue | 字段表 | final关键字定义的常量值 |
| Deprecated | 类、方法、字段表 | 被声明为deprecated的方法和字段 |
| Exceptions | 方法表 | 方法抛出的异常 |
| LineNumberTable | Code属性 | Java源码的行号与字节码指令的对应关系 |
| LocalVariableTable | Code属性 | 方法的局部变量描述 |
| SourceFile | 类文件 | 源文件名称 |
| Synthetic | 类、方法、字段表 | 标识方法或字段为编译器自动生成 |
| StackMapTable | Code属性 | JDK 6新增，用于类型检查验证 |
| Signature | 类、方法、字段表 | JDK 5新增，支持泛型签名 |
| RuntimeVisibleAnnotations | 类、方法、字段表 | JDK 5新增，运行时可见注解 |
| RuntimeInvisibleAnnotations | 类、方法、字段表 | JDK 5新增，运行时不可见注解 |
| MethodParameters | 方法表 | JDK 8新增，方法参数信息 |
| Module | 类文件 | JDK 9新增，模块信息 |

**Code属性**

Code属性是Class文件中最重要的一个属性，它存储了方法编译后的字节码指令。

```
Code_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;           // 操作数栈深度最大值
    u2 max_locals;          // 局部变量表所需存储空间（Slot数）
    u4 code_length;         // 字节码长度
    u1 code[code_length];   // 字节码指令
    u2 exception_table_length;
    {   u2 start_pc;        // 异常处理器起始PC
        u2 end_pc;          // 异常处理器结束PC
        u2 handler_pc;      // 异常处理代码起始PC
        u2 catch_type;      // 捕获的异常类型
    } exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count]; // 可以包含LineNumberTable、LocalVariableTable等
}
```

**LineNumberTable属性**

用于描述Java源码行号与字节码行号（字节码的偏移量）之间的对应关系。

```
LineNumberTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 line_number_table_length;
    {   u2 start_pc;        // 字节码行号
        u2 line_number;     // Java源码行号
    } line_number_table[line_number_table_length];
}
```

**LocalVariableTable属性**

用于描述栈帧中局部变量表中的变量与Java源码中定义的变量之间的关系。

```
LocalVariableTable_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 local_variable_table_length;
    {   u2 start_pc;        // 生命周期开始的字节码偏移量
        u2 length;          // 作用范围覆盖的长度
        u2 name_index;      // 局部变量名称
        u2 descriptor_index; // 局部变量描述符
        u2 index;           // 在局部变量表中的Slot位置
    } local_variable_table[local_variable_table_length];
}
```

**ConstantValue属性**

通知虚拟机自动为静态变量赋值。只有被static关键字修饰的变量（类变量）才可以使用这项属性。

```
ConstantValue_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 constantvalue_index; // 常量池中字面量的引用
}
```

**Exceptions属性**

列举出方法中可能抛出的受查异常（Checked Exceptions），也就是方法描述时在throws关键字后面列举的异常。

```
Exceptions_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_exceptions;
    u2 exception_index_table[number_of_exceptions]; // 指向常量池中的异常类型
}
```

---

## 6.4 字节码指令简介

Java虚拟机的指令由一个字节长度的、代表着某种特定操作含义的操作码（opcode）以及跟随其后的零至多个代表此操作所需参数的操作数（operand）所构成。

由于Java虚拟机采用面向操作数栈而不是寄存器的架构，所以大多数指令都不包含操作数，只有一个操作码。

### 6.4.1 字节码与数据类型

在Java虚拟机的指令集中，大多数的指令都包含了其操作所对应的数据类型信息。

**指令助记符与数据类型**

| 数据类型 | 操作码助记符 |
|----------|-------------|
| byte | b |
| short | s |
| int | i |
| long | l |
| float | f |
| double | d |
| char | c |
| reference | a |

例如：
- `iload`：从局部变量表加载一个int类型数据到操作数栈
- `fload`：加载float类型
- `aload`：加载引用类型

### 6.4.2 加载和存储指令

加载和存储指令用于将数据在栈帧中的局部变量表和操作数栈之间来回传输。

**加载指令（Load）**

| 指令 | 描述 |
|------|------|
| iload | 将指定的int类型局部变量推送至栈顶 |
| lload | 将指定的long类型局部变量推送至栈顶 |
| fload | 将指定的float类型局部变量推送至栈顶 |
| dload | 将指定的double类型局部变量推送至栈顶 |
| aload | 将指定的引用类型局部变量推送至栈顶 |
| iload_n | 将第n个int类型局部变量推送至栈顶（n为0-3） |
| ldc | 将int、float或String型常量值从常量池中推送至栈顶 |

**存储指令（Store）**

| 指令 | 描述 |
|------|------|
| istore | 将栈顶int类型数值存入指定局部变量 |
| lstore | 将栈顶long类型数值存入指定局部变量 |
| fstore | 将栈顶float类型数值存入指定局部变量 |
| dstore | 将栈顶double类型数值存入指定局部变量 |
| astore | 将栈顶引用类型数值存入指定局部变量 |

### 6.4.3 运算指令

运算指令用于对两个操作数栈上的值进行某种特定运算，并把结果重新存入到操作栈顶。

**算术指令**

| 指令 | 描述 |
|------|------|
| iadd | 将栈顶两int型数值相加并将结果压入栈顶 |
| isub | 将栈顶两int型数值相减并将结果压入栈顶 |
| imul | 将栈顶两int型数值相乘并将结果压入栈顶 |
| idiv | 将栈顶两int型数值相除并将结果压入栈顶 |
| irem | 将栈顶两int型数值取模并将结果压入栈顶 |
| ineg | 将栈顶int型数值取负并将结果压入栈顶 |
| iinc | 将指定int型变量增加指定值（如i++、i--、i+=2） |

### 6.4.4 类型转换指令

类型转换指令可以将两种不同的数值类型进行相互转换。

**宽化类型转换（Widening）**

| 指令 | 描述 |
|------|------|
| i2l | 将栈顶int型数值强制转换为long型 |
| i2f | 将栈顶int型数值强制转换为float型 |
| i2d | 将栈顶int型数值强制转换为double型 |
| l2f | 将栈顶long型数值强制转换为float型 |
| l2d | 将栈顶long型数值强制转换为double型 |
| f2d | 将栈顶float型数值强制转换为double型 |

**窄化类型转换（Narrowing）**

| 指令 | 描述 |
|------|------|
| i2b | 将栈顶int型数值强制转换为byte型 |
| i2c | 将栈顶int型数值强制转换为char型 |
| i2s | 将栈顶int型数值强制转换为short型 |
| l2i | 将栈顶long型数值强制转换为int型 |
| f2i | 将栈顶float型数值强制转换为int型 |
| d2i | 将栈顶double型数值强制转换为int型 |

### 6.4.5 对象创建与访问指令

**创建指令**

| 指令 | 描述 |
|------|------|
| new | 创建一个对象，并将其引用值压入栈顶 |
| newarray | 创建一个指定原始类型的数组，并将其引用值压入栈顶 |
| anewarray | 创建一个引用类型的数组，并将其引用值压入栈顶 |
| multianewarray | 创建多维数组 |

**访问指令**

| 指令 | 描述 |
|------|------|
| getfield | 获取指定类的实例字段，并将值压入栈顶 |
| putfield | 为指定类的实例字段赋值 |
| getstatic | 获取指定类的静态字段，并将值压入栈顶 |
| putstatic | 为指定类的静态字段赋值 |
| arraylength | 获取数组的长度值 |

### 6.4.6 操作数栈管理指令

| 指令 | 描述 |
|------|------|
| pop | 将栈顶数值弹出（非long/double） |
| pop2 | 将栈顶的一个（long/double类型）或两个数值弹出 |
| dup | 复制栈顶数值并将复制值压入栈顶 |
| dup2 | 复制栈顶一个（long/double）或两个数值 |
| swap | 将栈最顶端的两个数值互换（不能是long/double） |

### 6.4.7 控制转移指令

控制转移指令可以让Java虚拟机有条件或无条件地从指定的位置指令而不是控制转移指令的下一条指令继续执行程序。

**条件分支**

| 指令 | 描述 |
|------|------|
| ifeq | 当栈顶int型数值等于0时跳转 |
| ifne | 当栈顶int型数值不等于0时跳转 |
| iflt | 当栈顶int型数值小于0时跳转 |
| ifge | 当栈顶int型数值大于等于0时跳转 |
| ifgt | 当栈顶int型数值大于0时跳转 |
| ifle | 当栈顶int型数值小于等于0时跳转 |
| if_icmpeq | 比较栈顶两int型数值大小，当结果等于0时跳转 |
| if_icmpne | 比较栈顶两int型数值大小，当结果不等于0时跳转 |
| if_icmplt | 比较栈顶两int型数值大小，当结果小于0时跳转 |
| ifnull | 为null时跳转 |
| ifnonnull | 不为null时跳转 |

**无条件分支**

| 指令 | 描述 |
|------|------|
| goto | 无条件跳转 |
| goto_w | 无条件跳转（宽索引） |
| jsr | 跳转至指定16位offset位置，并将jsr下一条指令地址压入栈顶 |
| ret | 返回至本地变量指定的index的指令位置 |
| tableswitch | 用于switch条件跳转，case值连续 |
| lookupswitch | 用于switch条件跳转，case值不连续 |

### 6.4.8 方法调用和返回指令

**方法调用指令**

| 指令 | 描述 |
|------|------|
| invokevirtual | 调用实例方法（虚方法） |
| invokeinterface | 调用接口方法 |
| invokespecial | 调用需要特殊处理的实例方法（构造方法、私有方法、父类方法） |
| invokestatic | 调用静态方法 |
| invokedynamic | 调用动态方法（JDK 7新增，用于支持动态类型语言） |

**方法返回指令**

| 指令 | 描述 |
|------|------|
| ireturn | 从当前方法返回int |
| lreturn | 从当前方法返回long |
| freturn | 从当前方法返回float |
| dreturn | 从当前方法返回double |
| areturn | 从当前方法返回对象引用 |
| return | 从当前方法返回void |

### 6.4.9 异常处理指令

| 指令 | 描述 |
|------|------|
| athrow | 将栈顶的异常对象抛出 |

异常处理不是由字节码指令来实现的，而是采用异常表（Exception Table）来完成的。

### 6.4.10 同步指令

Java虚拟机可以支持方法级的同步和方法内部一段指令序列的同步，这两种同步结构都是使用管程（Monitor）来支持的。

**方法级同步**

是隐式的，即无须通过字节码指令来控制，它实现在方法调用和返回操作之中。虚拟机可以从方法常量池的方法表结构中的ACC_SYNCHRONIZED访问标志得知一个方法是否声明为同步方法。

**指令序列级同步**

| 指令 | 描述 |
|------|------|
| monitorenter | 获取对象的monitor |
| monitorexit | 释放对象的monitor |

示例：
```java
synchronized (obj) {
    // 同步代码块
}
```

对应的字节码：
```
aload_1          // 加载obj到栈顶
monitorenter     // 获取monitor
...              // 同步代码块
aload_1          // 加载obj到栈顶
monitorexit      // 释放monitor
```

---

## 6.5 公有设计，私有实现

Java虚拟机规范描绘了Java虚拟机应有的共同程序存储格式：Class文件格式以及字节码指令集。这些内容与硬件、操作系统及具体的Java虚拟机实现之间是完全独立的。

理解公有设计与私有实现之间的分界线，对于学习虚拟机来说有着重要的意义：

1. **公有设计**：Java虚拟机规范定义的Class文件格式和字节码指令集是所有Java虚拟机实现必须遵守的标准
2. **私有实现**：虚拟机厂商可以在遵循规范的前提下，采用不同的实现技术（解释执行、JIT编译、AOT编译等）

这种设计使得：
- 相同的Class文件可以在不同的虚拟机上运行
- 虚拟机厂商可以竞争更好的性能实现
- 新的优化技术可以不断被引入

---

## 6.6 Class文件结构的发展

Class文件结构自Java诞生以来，已经经历了多个版本的演进：

**JDK 1.0 - JDK 1.4**
- 基础Class文件格式确立
- 基本的字节码指令集

**JDK 5（Java 1.5）**
- 新增Signature属性（支持泛型）
- 新增注解相关属性（RuntimeVisibleAnnotations等）
- 新增枚举类型支持（ACC_ENUM）

**JDK 6**
- 新增StackMapTable属性（用于类型检查验证）
- 改进字节码验证器

**JDK 7**
- 新增invokedynamic指令（支持动态类型语言）
- 新增MethodHandle、MethodType等常量池类型

**JDK 8**
- 新增MethodParameters属性
- Lambda表达式支持（通过invokedynamic实现）
- 默认方法支持

**JDK 9**
- 模块化系统（Module、Package等常量池类型）
- 新增ACC_MODULE访问标志

**JDK 11+**
- 常量池动态支持（CONSTANT_Dynamic）
- Nestmates（嵌套类访问控制）

---

## 6.7 本章小结

Class文件是Java虚拟机执行引擎的数据入口，也是Java技术体系的基础构成之一。本章详细讲解了Class文件结构中的各个组成部分：

1. **魔数和版本号**：用于标识Class文件格式和版本兼容性
2. **常量池**：存放字面量和符号引用，是Class文件的核心数据区
3. **访问标志、类索引、父类索引、接口索引集合**：描述类的访问权限和继承关系
4. **字段表、方法表**：描述类的成员变量和方法
5. **属性表**：存储各种元数据信息，其中Code属性存储字节码指令
6. **字节码指令**：Java虚拟机的执行指令集

了解Class文件的结构对于以下方面很有帮助：
- 理解Java虚拟机的工作原理
- 进行字节码增强和AOP编程
- 排查一些深层次的Bug
- 开发代码检查工具和静态分析工具
- 实现Java Agent和Instrumentation

---

## 参考工具

**查看Class文件**

```bash
# 使用javap反编译
javap -verbose HelloWorld.class

# 使用javap查看字节码
javap -c HelloWorld.class

# 使用十六进制查看器
hexdump -C HelloWorld.class

# 使用IDE插件（IntelliJ IDEA的Bytecode Viewer）
```

**字节码操作库**

- **ASM**：高性能的字节码操作和分析框架
- **Javassist**：简单易用的字节码编辑库
- **Byte Buddy**：基于ASM的现代字节码操作库
- **BCEL**：Apache的字节码工程库
