# 01 - Java 语法基础示例代码详细设计

## 文档信息

- **文档编号**: 01-grammar-detailed-design
- **所属模块**: core/grammar
- **对应文档**: [Java 语法基础深入总结](./index.md)
- **创建日期**: 2026-03-27
- **版本**: 1.0.0

---

## 1. 设计目标

本文档为 Java 语法基础提供完整的示例代码设计，涵盖：
- 数据类型与变量操作
- 运算符与表达式
- 流程控制结构
- 数组操作
- 方法定义与调用
- 面向对象编程
- 异常处理机制
- 泛型编程
- 注解应用

---

## 2. 包结构设计

```
com.linsir.abc.grammar
├── datatype/           # 数据类型示例
│   ├── PrimitiveTypes.java
│   ├── ReferenceTypes.java
│   └── TypeConversion.java
├── variable/           # 变量与常量
│   ├── VariableScopes.java
│   └── Constants.java
├── operator/           # 运算符
│   ├── ArithmeticOperators.java
│   ├── BitwiseOperators.java
│   └── LogicalOperators.java
├── controlflow/        # 流程控制
│   ├── Conditionals.java
│   ├── Loops.java
│   └── ControlKeywords.java
├── array/              # 数组操作
│   ├── ArrayBasics.java
│   └── ArrayOperations.java
├── method/             # 方法
│   ├── MethodBasics.java
│   ├── ParameterPassing.java
│   └── VarargsDemo.java
├── oop/                # 面向对象
│   ├── ClassAndObject.java
│   ├── Inheritance.java
│   ├── Polymorphism.java
│   ├── AbstractClass.java
│   └── InterfaceDemo.java
├── exception/          # 异常处理
│   ├── ExceptionHandling.java
│   └── CustomExceptions.java
├── generic/            # 泛型
│   ├── GenericClass.java
│   ├── GenericMethod.java
│   └── Wildcards.java
└── annotation/         # 注解
    ├── BuiltInAnnotations.java
    └── CustomAnnotations.java
```

---

## 3. 详细设计

### 3.1 数据类型 (datatype)

#### 3.1.1 PrimitiveTypes.java

**功能**: 演示基本数据类型的定义、默认值和取值范围

**核心代码**:

```java
package com.linsir.abc.grammar.datatype;

/**
 * 基本数据类型示例
 * 对应 JDK: java.lang 包下的基本类型包装类
 */
public class PrimitiveTypes {
    
    // 成员变量默认值演示
    private byte byteVar;
    private short shortVar;
    private int intVar;
    private long longVar;
    private float floatVar;
    private double doubleVar;
    private char charVar;
    private boolean booleanVar;
    
    /**
     * 展示各类型取值范围
     */
    public void demonstrateRanges() {
        System.out.println("=== 基本数据类型取值范围 ===");
        
        // byte: 8位有符号整数
        System.out.println("byte: " + Byte.MIN_VALUE + " ~ " + Byte.MAX_VALUE);
        
        // short: 16位有符号整数
        System.out.println("short: " + Short.MIN_VALUE + " ~ " + Short.MAX_VALUE);
        
        // int: 32位有符号整数
        System.out.println("int: " + Integer.MIN_VALUE + " ~ " + Integer.MAX_VALUE);
        
        // long: 64位有符号整数
        System.out.println("long: " + Long.MIN_VALUE + " ~ " + Long.MAX_VALUE);
        
        // float: 32位IEEE 754单精度浮点数
        System.out.println("float: " + Float.MIN_VALUE + " ~ " + Float.MAX_VALUE);
        
        // double: 64位IEEE 754双精度浮点数
        System.out.println("double: " + Double.MIN_VALUE + " ~ " + Double.MAX_VALUE);
        
        // char: 16位Unicode字符
        System.out.println("char: " + (int)Character.MIN_VALUE + " ~ " + (int)Character.MAX_VALUE);
    }
    
    /**
     * 展示默认值
     */
    public void demonstrateDefaults() {
        System.out.println("\n=== 成员变量默认值 ===");
        System.out.println("byte: " + byteVar);
        System.out.println("short: " + shortVar);
        System.out.println("int: " + intVar);
        System.out.println("long: " + longVar);
        System.out.println("float: " + floatVar);
        System.out.println("double: " + doubleVar);
        System.out.println("char: [" + charVar + "] (值为 " + (int)charVar + ")");
        System.out.println("boolean: " + booleanVar);
    }
    
    /**
     * 字面量表示
     */
    public void demonstrateLiterals() {
        System.out.println("\n=== 字面量表示 ===");
        
        // 整数字面量
        int decimal = 100;      // 十进制
        int octal = 0144;       // 八进制 (0开头)
        int hex = 0x64;         // 十六进制 (0x开头)
        int binary = 0b1100100; // 二进制 (0b开头，Java 7+)
        
        System.out.println("十进制 100 = " + decimal);
        System.out.println("八进制 0144 = " + octal);
        System.out.println("十六进制 0x64 = " + hex);
        System.out.println("二进制 0b1100100 = " + binary);
        
        // 长整型
        long longVar = 100L;    // L后缀
        
        // 浮点数字面量
        float floatVar = 3.14f;     // f后缀
        double doubleVar1 = 3.14;    // 默认double
        double doubleVar2 = 3.14d;   // d后缀
        double doubleVar3 = 3.14e2;  // 科学计数法
        
        // 字符字面量
        char char1 = 'A';
        char char2 = '\u0041';  // Unicode
        char char3 = '\n';      // 转义字符
        
        // 布尔字面量
        boolean flag = true;  // 或 false
    }
    
    public static void main(String[] args) {
        PrimitiveTypes demo = new PrimitiveTypes();
        demo.demonstrateRanges();
        demo.demonstrateDefaults();
        demo.demonstrateLiterals();
    }
}
```

**测试场景**:
1. 验证各类型取值范围
2. 验证成员变量默认值
3. 验证不同进制字面量

---

#### 3.1.2 ReferenceTypes.java

**功能**: 演示引用类型的内存结构和特点

**核心代码**:

```java
package com.linsir.abc.grammar.datatype;

/**
 * 引用类型示例
 * 对应 JDK: java.lang.Object 及其子类
 */
public class ReferenceTypes {
    
    /**
     * 演示引用类型的内存特性
     */
    public void demonstrateReferenceBehavior() {
        System.out.println("=== 引用类型内存特性 ===");
        
        // 创建对象
        Person person1 = new Person("张三", 25);
        Person person2 = person1;  // 引用赋值（同一对象）
        
        System.out.println("person1: " + person1);
        System.out.println("person2: " + person2);
        System.out.println("同一对象? " + (person1 == person2));
        
        // 修改通过person2
        person2.setAge(30);
        System.out.println("\n修改后 person1.age: " + person1.getAge());
        
        // 创建新对象
        Person person3 = new Person("张三", 30);
        System.out.println("\nperson1 equals person3? " + person1.equals(person3));
        System.out.println("person1 == person3? " + (person1 == person3));
    }
    
    /**
     * 演示null引用
     */
    public void demonstrateNullReference() {
        System.out.println("\n=== Null 引用 ===");
        
        Person person = null;
        System.out.println("person = " + person);
        
        // 以下代码会抛出 NullPointerException
        // person.getName();
    }
    
    /**
     * 演示字符串常量池
     */
    public void demonstrateStringPool() {
        System.out.println("\n=== 字符串常量池 ===");
        
        String s1 = "Hello";           // 常量池
        String s2 = "Hello";           // 常量池（同一对象）
        String s3 = new String("Hello"); // 堆内存（新对象）
        
        System.out.println("s1 == s2: " + (s1 == s2));  // true
        System.out.println("s1 == s3: " + (s1 == s3));  // false
        System.out.println("s1.equals(s3): " + s1.equals(s3));  // true
        
        // intern() 方法
        String s4 = s3.intern();
        System.out.println("s1 == s4 (intern): " + (s1 == s4));  // true
    }
    
    // 内部类
    static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        // getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + "}";
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Person person = (Person) obj;
            return age == person.age && name.equals(person.name);
        }
    }
    
    public static void main(String[] args) {
        ReferenceTypes demo = new ReferenceTypes();
        demo.demonstrateReferenceBehavior();
        demo.demonstrateNullReference();
        demo.demonstrateStringPool();
    }
}
```

---

#### 3.1.3 TypeConversion.java

**功能**: 演示类型转换规则和注意事项

**核心代码**:

```java
package com.linsir.abc.grammar.datatype;

/**
 * 类型转换示例
 * 对应 JDK: 基本类型包装类的转换方法
 */
public class TypeConversion {
    
    /**
     * 自动类型转换（隐式）
     */
    public void demonstrateImplicitConversion() {
        System.out.println("=== 自动类型转换 ===");
        
        byte b = 10;
        short s = b;      // byte -> short
        int i = s;        // short -> int
        long l = i;       // int -> long
        float f = l;      // long -> float
        double d = f;     // float -> double
        
        System.out.println("byte " + b + " -> double " + d);
        
        // char 可以转为 int
        char c = 'A';
        int charCode = c;
        System.out.println("char '" + c + "' -> int " + charCode);
        
        // 表达式类型提升
        byte b1 = 10, b2 = 20;
        int sum = b1 + b2;  // b1, b2 先提升为 int
        System.out.println("byte + byte = int: " + sum);
    }
    
    /**
     * 强制类型转换（显式）
     */
    public void demonstrateExplicitConversion() {
        System.out.println("\n=== 强制类型转换 ===");
        
        // 大类型转小类型（可能丢失精度）
        double d = 3.99;
        int i = (int) d;  // 截断小数部分
        System.out.println("(int) 3.99 = " + i);
        
        // 溢出情况
        int big = 128;
        byte small = (byte) big;  // 溢出
        System.out.println("(byte) 128 = " + small);  // -128
        
        // 大数转小数
        long large = 10000000000L;
        int truncated = (int) large;
        System.out.println("(int) 10000000000L = " + truncated);  // 溢出
    }
    
    /**
     * 包装类转换
     */
    public void demonstrateWrapperConversion() {
        System.out.println("\n=== 包装类转换 ===");
        
        // 自动装箱
        Integer obj = 100;  // 等价于 Integer.valueOf(100)
        
        // 自动拆箱
        int primitive = obj;  // 等价于 obj.intValue()
        
        // 字符串转数字
        String numStr = "12345";
        int num = Integer.parseInt(numStr);
        double d = Double.parseDouble("3.14159");
        
        // 数字转字符串
        String str1 = String.valueOf(123);
        String str2 = Integer.toString(456);
        String str3 = 789 + "";  // 简单方式
        
        System.out.println("字符串转int: " + num);
        System.out.println("int转字符串: " + str1);
    }
    
    /**
     * 引用类型转换
     */
    public void demonstrateReferenceConversion() {
        System.out.println("\n=== 引用类型转换 ===");
        
        // 向上转型（自动）
        Object obj = "Hello";  // String -> Object
        
        // 向下转型（需检查）
        if (obj instanceof String) {
            String str = (String) obj;
            System.out.println("向下转型成功: " + str);
        }
        
        // Java 16+ 模式匹配
        // if (obj instanceof String s) {
        //     System.out.println("模式匹配转型: " + s);
        // }
    }
    
    public static void main(String[] args) {
        TypeConversion demo = new TypeConversion();
        demo.demonstrateImplicitConversion();
        demo.demonstrateExplicitConversion();
        demo.demonstrateWrapperConversion();
        demo.demonstrateReferenceConversion();
    }
}
```

---

### 3.2 变量与常量 (variable)

#### 3.2.1 VariableScopes.java

**功能**: 演示不同作用域的变量

**核心代码**:

```java
package com.linsir.abc.grammar.variable;

/**
 * 变量作用域示例
 * 对应 JDK: 变量声明和作用域规则
 */
public class VariableScopes {
    
    // 成员变量（实例变量）- 对象生命周期
    private int instanceVar = 10;
    
    // 类变量（静态变量）- 类生命周期
    private static int staticVar = 20;
    
    /**
     * 演示各种变量作用域
     */
    public void demonstrateScopes() {
        System.out.println("=== 变量作用域演示 ===");
        
        // 局部变量 - 方法执行期间
        int localVar = 30;
        
        System.out.println("成员变量: " + instanceVar);
        System.out.println("类变量: " + staticVar);
        System.out.println("局部变量: " + localVar);
        
        // 代码块作用域
        {
            int blockVar = 40;
            System.out.println("代码块变量: " + blockVar);
            
            // 可以访问外部变量
            System.out.println("在代码块中访问局部变量: " + localVar);
        }
        // blockVar 在这里不可见
        
        // 循环变量作用域
        for (int i = 0; i < 3; i++) {
            System.out.println("循环变量 i: " + i);
        }
        // i 在这里不可见
        
        // 条件语句中的变量
        if (localVar > 0) {
            int ifVar = 50;
            System.out.println("if语句变量: " + ifVar);
        }
        // ifVar 在这里不可见
    }
    
    /**
     * 演示变量隐藏
     */
    public void demonstrateShadowing(int instanceVar) {
        System.out.println("\n=== 变量隐藏 ===");
        
        // 参数隐藏了成员变量
        System.out.println("参数 instanceVar: " + instanceVar);
        System.out.println("成员变量 instanceVar: " + this.instanceVar);
    }
    
    /**
     * 演示静态变量共享
     */
    public void demonstrateStaticSharing() {
        System.out.println("\n=== 静态变量共享 ===");
        
        staticVar++;
        System.out.println("staticVar 增加到: " + staticVar);
    }
    
    public static void main(String[] args) {
        VariableScopes obj1 = new VariableScopes();
        VariableScopes obj2 = new VariableScopes();
        
        obj1.demonstrateScopes();
        obj1.demonstrateShadowing(100);
        
        System.out.println("\n对象1修改静态变量:");
        obj1.demonstrateStaticSharing();
        
        System.out.println("对象2看到相同的静态变量:");
        obj2.demonstrateStaticSharing();
    }
}
```

---

#### 3.2.2 Constants.java

**功能**: 演示常量的定义和使用

**核心代码**:

```java
package com.linsir.abc.grammar.variable;

/**
 * 常量示例
 * 对应 JDK: final 关键字的使用
 */
public class Constants {
    
    // 编译期常量（基本类型或String）
    public static final int MAX_SIZE = 100;
    public static final double PI = 3.14159;
    public static final String APP_NAME = "LinsirABC";
    
    // 运行期常量（对象）
    public static final java.util.Date CREATE_TIME = new java.util.Date();
    
    // 实例常量
    private final int instanceConstant;
    
    public Constants(int value) {
        this.instanceConstant = value;  // 只能在构造方法中初始化
    }
    
    /**
     * 演示常量使用
     */
    public void demonstrateConstants() {
        System.out.println("=== 常量使用 ===");
        System.out.println("MAX_SIZE: " + MAX_SIZE);
        System.out.println("PI: " + PI);
        System.out.println("APP_NAME: " + APP_NAME);
        System.out.println("CREATE_TIME: " + CREATE_TIME);
        System.out.println("instanceConstant: " + instanceConstant);
        
        // 局部常量
        final int localConst = 50;
        System.out.println("localConst: " + localConst);
        
        // 引用类型的常量
        final StringBuilder sb = new StringBuilder("Hello");
        sb.append(" World");  // 可以修改对象内容
        System.out.println("StringBuilder: " + sb);
        // sb = new StringBuilder();  // 错误：不能改变引用
    }
    
    /**
     * 枚举常量
     */
    public enum Status {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        DELETED("已删除");
        
        private final String description;
        
        Status(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    /**
     * 演示枚举使用
     */
    public void demonstrateEnum() {
        System.out.println("\n=== 枚举常量 ===");
        
        Status status = Status.ACTIVE;
        System.out.println("状态: " + status);
        System.out.println("描述: " + status.getDescription());
        
        // 遍历枚举
        System.out.println("\n所有状态:");
        for (Status s : Status.values()) {
            System.out.println("  " + s + " = " + s.getDescription());
        }
    }
    
    public static void main(String[] args) {
        Constants demo = new Constants(42);
        demo.demonstrateConstants();
        demo.demonstrateEnum();
    }
}
```

---

### 3.3 运算符 (operator)

#### 3.3.1 ArithmeticOperators.java

**功能**: 演示算术运算符的使用

**核心代码**:

```java
package com.linsir.abc.grammar.operator;

/**
 * 算术运算符示例
 * 对应 JDK: 基本算术运算
 */
public class ArithmeticOperators {
    
    /**
     * 基本算术运算
     */
    public void demonstrateBasicArithmetic() {
        System.out.println("=== 基本算术运算 ===");
        
        int a = 17, b = 5;
        
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a + b = " + (a + b));  // 加法
        System.out.println("a - b = " + (a - b));  // 减法
        System.out.println("a * b = " + (a * b));  // 乘法
        System.out.println("a / b = " + (a / b));  // 整数除法
        System.out.println("a % b = " + (a % b));  // 取模（余数）
        
        // 浮点数除法
        double result = (double) a / b;
        System.out.println("(double)a / b = " + result);
    }
    
    /**
     * 自增自减运算符
     */
    public void demonstrateIncrementDecrement() {
        System.out.println("\n=== 自增自减运算符 ===");
        
        int x = 5;
        System.out.println("初始 x = " + x);
        
        // 前缀自增：先增后用
        int y = ++x;
        System.out.println("y = ++x: x = " + x + ", y = " + y);
        
        // 重置
        x = 5;
        
        // 后缀自增：先用后增
        int z = x++;
        System.out.println("z = x++: x = " + x + ", z = " + z);
        
        // 在表达式中使用
        x = 5;
        int result = x++ + ++x;  // 5 + 7 = 12
        System.out.println("x++ + ++x (x=5): " + result);
    }
    
    /**
     * 复合赋值运算符
     */
    public void demonstrateCompoundAssignment() {
        System.out.println("\n=== 复合赋值运算符 ===");
        
        int a = 10;
        
        a += 5;   // a = a + 5
        System.out.println("a += 5: " + a);
        
        a -= 3;   // a = a - 3
        System.out.println("a -= 3: " + a);
        
        a *= 2;   // a = a * 2
        System.out.println("a *= 2: " + a);
        
        a /= 4;   // a = a / 4
        System.out.println("a /= 4: " + a);
        
        a %= 3;   // a = a % 3
        System.out.println("a %= 3: " + a);
    }
    
    /**
     * 整数溢出
     */
    public void demonstrateOverflow() {
        System.out.println("\n=== 整数溢出 ===");
        
        int max = Integer.MAX_VALUE;
        System.out.println("Integer.MAX_VALUE = " + max);
        System.out.println("MAX_VALUE + 1 = " + (max + 1));  // 溢出为负数
        
        // 使用 Math.addExact 检测溢出（Java 8+）
        try {
            int result = Math.addExact(max, 1);
        } catch (ArithmeticException e) {
            System.out.println("溢出检测: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        ArithmeticOperators demo = new ArithmeticOperators();
        demo.demonstrateBasicArithmetic();
        demo.demonstrateIncrementDecrement();
        demo.demonstrateCompoundAssignment();
        demo.demonstrateOverflow();
    }
}
```

---

#### 3.3.2 BitwiseOperators.java

**功能**: 演示位运算符的使用

**核心代码**:

```java
package com.linsir.abc.grammar.operator;

/**
 * 位运算符示例
 * 对应 JDK: 位运算操作
 */
public class BitwiseOperators {
    
    /**
     * 基本位运算
     */
    public void demonstrateBasicBitwise() {
        System.out.println("=== 基本位运算 ===");
        
        int a = 5;   // 二进制: 0101
        int b = 3;   // 二进制: 0011
        
        System.out.println("a = " + a + " (二进制: " + toBinary(a) + ")");
        System.out.println("b = " + b + " (二进制: " + toBinary(b) + ")");
        System.out.println("a & b = " + (a & b) + " (二进制: " + toBinary(a & b) + ")");  // 0001 = 1
        System.out.println("a | b = " + (a | b) + " (二进制: " + toBinary(a | b) + ")");  // 0111 = 7
        System.out.println("a ^ b = " + (a ^ b) + " (二进制: " + toBinary(a ^ b) + ")");  // 0110 = 6
        System.out.println("~a = " + (~a) + " (二进制: " + toBinary(~a) + ")");           // 取反
    }
    
    /**
     * 移位运算
     */
    public void demonstrateShift() {
        System.out.println("\n=== 移位运算 ===");
        
        int a = 8;   // 二进制: 1000
        
        System.out.println("a = " + a + " (二进制: " + toBinary(a) + ")");
        System.out.println("a << 2 = " + (a << 2) + " (二进制: " + toBinary(a << 2) + ")");   // 左移
        System.out.println("a >> 2 = " + (a >> 2) + " (二进制: " + toBinary(a >> 2) + ")");   // 右移（算术）
        System.out.println("a >>> 2 = " + (a >>> 2) + " (二进制: " + toBinary(a >>> 2) + ")"); // 右移（逻辑）
        
        // 负数移位
        int negative = -8;
        System.out.println("\n负数 -8:");
        System.out.println("-8 >> 2 = " + (-8 >> 2));    // 算术右移，保持符号
        System.out.println("-8 >>> 2 = " + (-8 >>> 2));  // 逻辑右移，补0
    }
    
    /**
     * 位运算应用：权限控制
     */
    public void demonstratePermissionControl() {
        System.out.println("\n=== 位运算应用：权限控制 ===");
        
        // 定义权限（2的幂次）
        final int READ = 1 << 0;    // 0001 = 1
        final int WRITE = 1 << 1;   // 0010 = 2
        final int EXECUTE = 1 << 2; // 0100 = 4
        final int DELETE = 1 << 3;  // 1000 = 8
        
        // 组合权限
        int userPermission = READ | WRITE;  // 0011 = 3
        int adminPermission = READ | WRITE | EXECUTE | DELETE;  // 1111 = 15
        
        System.out.println("READ = " + READ + ", WRITE = " + WRITE);
        System.out.println("EXECUTE = " + EXECUTE + ", DELETE = " + DELETE);
        System.out.println("用户权限: " + userPermission);
        System.out.println("管理员权限: " + adminPermission);
        
        // 检查权限
        System.out.println("\n权限检查:");
        System.out.println("用户是否有 READ 权限? " + hasPermission(userPermission, READ));
        System.out.println("用户是否有 EXECUTE 权限? " + hasPermission(userPermission, EXECUTE));
        System.out.println("管理员是否有所有权限? " + hasPermission(adminPermission, READ | WRITE | EXECUTE | DELETE));
        
        // 添加权限
        userPermission = addPermission(userPermission, EXECUTE);
        System.out.println("\n添加 EXECUTE 后，用户权限: " + userPermission);
        
        // 移除权限
        userPermission = removePermission(userPermission, WRITE);
        System.out.println("移除 WRITE 后，用户权限: " + userPermission);
    }
    
    /**
     * 位运算应用：高效乘除法
     */
    public void demonstrateFastMath() {
        System.out.println("\n=== 位运算应用：高效乘除法 ===");
        
        int num = 16;
        
        // 乘以2的幂次
        System.out.println(num + " << 1 = " + (num << 1) + " (相当于 *2)");
        System.out.println(num + " << 2 = " + (num << 2) + " (相当于 *4)");
        System.out.println(num + " << 3 = " + (num << 3) + " (相当于 *8)");
        
        // 除以2的幂次
        System.out.println(num + " >> 1 = " + (num >> 1) + " (相当于 /2)");
        System.out.println(num + " >> 2 = " + (num >> 2) + " (相当于 /4)");
        System.out.println(num + " >> 3 = " + (num >> 3) + " (相当于 /8)");
    }
    
    // 辅助方法
    private String toBinary(int num) {
        return String.format("%32s", Integer.toBinaryString(num)).replace(' ', '0');
    }
    
    private boolean hasPermission(int permissions, int permission) {
        return (permissions & permission) == permission;
    }
    
    private int addPermission(int permissions, int permission) {
        return permissions | permission;
    }
    
    private int removePermission(int permissions, int permission) {
        return permissions & ~permission;
    }
    
    public static void main(String[] args) {
        BitwiseOperators demo = new BitwiseOperators();
        demo.demonstrateBasicBitwise();
        demo.demonstrateShift();
        demo.demonstratePermissionControl();
        demo.demonstrateFastMath();
    }
}
```

---

由于篇幅限制，我将继续创建其他模块的设计文档。是否需要我：
1. 继续完成剩余模块（controlflow, array, method, oop, exception, generic, annotation）的详细设计？
2. 还是先提交当前已完成的部分？

当前已完成：
- ✅ 3.1 数据类型 (datatype) - 3个类
- ✅ 3.2 变量与常量 (variable) - 2个类
- ✅ 3.3 运算符 (operator) - 2个类（算术、位运算）

待完成：
- ⏳ 3.3 运算符 - 逻辑运算符
- ⏳ 3.4 流程控制 (controlflow)
- ⏳ 3.5 数组 (array)
- ⏳ 3.6 方法 (method)
- ⏳ 3.7 面向对象 (oop)
- ⏳ 3.8 异常处理 (exception)
- ⏳ 3.9 泛型 (generic)
- ⏳ 3.10 注解 (annotation)