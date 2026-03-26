package com.linsir.abc.core.grammar.datatype;

/**
 * 基本数据类型示例
 * 
 * 本类演示 Java 基本数据类型的定义、默认值、取值范围及字面量表示
 * 对应 JDK: java.lang 包下的基本类型包装类
 * 
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class PrimitiveTypes {
    
    /** byte 类型成员变量，演示默认值 */
    private byte byteVar;
    /** short 类型成员变量，演示默认值 */
    private short shortVar;
    /** int 类型成员变量，演示默认值 */
    private int intVar;
    /** long 类型成员变量，演示默认值 */
    private long longVar;
    /** float 类型成员变量，演示默认值 */
    private float floatVar;
    /** double 类型成员变量，演示默认值 */
    private double doubleVar;
    /** char 类型成员变量，演示默认值 */
    private char charVar;
    /** boolean 类型成员变量，演示默认值 */
    private boolean booleanVar;
    
    /**
     * 展示各基本数据类型的取值范围
     * 通过访问包装类的 MIN_VALUE 和 MAX_VALUE 常量获取
     */
    public void demonstrateRanges() {
        System.out.println("=== 基本数据类型取值范围 ===");
        
        // byte: 8位有符号整数，范围 -128 ~ 127
        System.out.println("byte: " + Byte.MIN_VALUE + " ~ " + Byte.MAX_VALUE);
        
        // short: 16位有符号整数，范围 -32768 ~ 32767
        System.out.println("short: " + Short.MIN_VALUE + " ~ " + Short.MAX_VALUE);
        
        // int: 32位有符号整数，范围 -2^31 ~ 2^31-1
        System.out.println("int: " + Integer.MIN_VALUE + " ~ " + Integer.MAX_VALUE);
        
        // long: 64位有符号整数，范围 -2^63 ~ 2^63-1
        System.out.println("long: " + Long.MIN_VALUE + " ~ " + Long.MAX_VALUE);
        
        // float: 32位IEEE 754单精度浮点数
        System.out.println("float: " + Float.MIN_VALUE + " ~ " + Float.MAX_VALUE);
        
        // double: 64位IEEE 754双精度浮点数
        System.out.println("double: " + Double.MIN_VALUE + " ~ " + Double.MAX_VALUE);
        
        // char: 16位Unicode字符，范围 0 ~ 65535
        System.out.println("char: " + (int) Character.MIN_VALUE + " ~ " + (int) Character.MAX_VALUE);
    }
    
    /**
     * 展示成员变量的默认值
     * 成员变量会自动初始化，局部变量不会
     */
    public void demonstrateDefaults() {
        System.out.println("\n=== 成员变量默认值 ===");
        System.out.println("byte: " + byteVar);
        System.out.println("short: " + shortVar);
        System.out.println("int: " + intVar);
        System.out.println("long: " + longVar);
        System.out.println("float: " + floatVar);
        System.out.println("double: " + doubleVar);
        System.out.println("char: [" + charVar + "] (值为 " + (int) charVar + ")");
        System.out.println("boolean: " + booleanVar);
    }
    
    /**
     * 展示各种字面量表示方式
     * 包括不同进制的整数表示和浮点数表示
     */
    public void demonstrateLiterals() {
        System.out.println("\n=== 字面量表示 ===");
        
        // 整数字面量的不同进制表示
        int decimal = 100;          // 十进制（默认）
        int octal = 0144;           // 八进制，以 0 开头
        int hex = 0x64;             // 十六进制，以 0x 开头
        int binary = 0b1100100;     // 二进制，以 0b 开头（Java 7+）
        
        System.out.println("十进制 100 = " + decimal);
        System.out.println("八进制 0144 = " + octal);
        System.out.println("十六进制 0x64 = " + hex);
        System.out.println("二进制 0b1100100 = " + binary);
        
        // 长整型字面量，需要 L 或 l 后缀（推荐使用大写 L）
        long longVar = 100L;
        System.out.println("long 字面量 100L = " + longVar);
        
        // 浮点数字面量
        float floatVar = 3.14f;         // float 类型，需要 f 或 F 后缀
        double doubleVar1 = 3.14;        // double 类型（默认）
        double doubleVar2 = 3.14d;       // double 类型，d 或 D 后缀（可选）
        double doubleVar3 = 3.14e2;      // 科学计数法，表示 3.14 × 10^2 = 314.0
        
        System.out.println("float 3.14f = " + floatVar);
        System.out.println("double 3.14 = " + doubleVar1);
        System.out.println("double 3.14d = " + doubleVar2);
        System.out.println("double 3.14e2 = " + doubleVar3);
        
        // 字符字面量
        char char1 = 'A';           // 普通字符
        char char2 = '\u0041';      // Unicode 转义，0041 是 'A' 的 Unicode 码点
        char char3 = '\n';          // 转义字符：换行
        char char4 = '\t';          // 转义字符：制表符
        char char5 = '\\';          // 转义字符：反斜杠
        char char6 = '\'';          // 转义字符：单引号
        
        System.out.println("字符 'A' = " + char1);
        System.out.println("Unicode '\\u0041' = " + char2);
        System.out.println("转义字符 '\\n' 显示为换行");
        
        // 布尔字面量
        boolean flag = true;        // 或 false
        System.out.println("boolean true = " + flag);
    }
    
    /**
     * 主方法，运行所有演示
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        PrimitiveTypes demo = new PrimitiveTypes();
        
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 基本数据类型演示                      ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        demo.demonstrateRanges();
        demo.demonstrateDefaults();
        demo.demonstrateLiterals();
        
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
