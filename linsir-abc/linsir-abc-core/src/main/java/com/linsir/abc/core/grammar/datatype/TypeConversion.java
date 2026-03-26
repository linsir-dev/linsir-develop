package com.linsir.abc.core.grammar.datatype;

/**
 * 类型转换示例
 * 
 * 本类演示 Java 中的自动类型转换、强制类型转换和包装类转换
 * 对应 JDK: 基本类型包装类的转换方法
 * 
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class TypeConversion {
    
    /**
     * 演示自动类型转换（隐式转换）
     * 小范围类型自动转换为大范围类型，不会丢失精度
     */
    public void demonstrateImplicitConversion() {
        System.out.println("=== 自动类型转换（隐式）===");
        
        // byte (8位) -> short (16位) -> int (32位) -> long (64位) -> float (32位) -> double (64位)
        byte b = 10;
        short s = b;        // byte 自动提升为 short
        int i = s;          // short 自动提升为 int
        long l = i;         // int 自动提升为 long
        float f = l;        // long 自动提升为 float
        double d = f;       // float 自动提升为 double
        
        System.out.println("byte " + b + " -> short " + s);
        System.out.println("short " + s + " -> int " + i);
        System.out.println("int " + i + " -> long " + l);
        System.out.println("long " + l + " -> float " + f);
        System.out.println("float " + f + " -> double " + d);
        
        // char 可以自动提升为 int
        char c = 'A';
        int charCode = c;   // char 自动提升为 int，获取 Unicode 码点
        System.out.println("\nchar '" + c + "' -> int " + charCode);
        
        // 表达式中的类型提升
        byte b1 = 10;
        byte b2 = 20;
        // b1 和 b2 在运算时先提升为 int，结果也是 int
        int sum = b1 + b2;
        System.out.println("\nbyte + byte = int: " + b1 + " + " + b2 + " = " + sum);
        
        // 混合类型运算，结果提升为最高级类型
        int num = 100;
        double result = num + 3.14;  // int 提升为 double
        System.out.println("int + double = double: " + num + " + 3.14 = " + result);
    }
    
    /**
     * 演示强制类型转换（显式转换）
     * 大范围类型转换为小范围类型，可能丢失精度
     */
    public void demonstrateExplicitConversion() {
        System.out.println("\n=== 强制类型转换（显式）===");
        
        // double 转 int，截断小数部分（向零取整）
        double d = 3.99;
        int i = (int) d;
        System.out.println("(int) " + d + " = " + i + " （截断小数部分）");
        
        // 负数转换
        double negative = -3.99;
        int negInt = (int) negative;
        System.out.println("(int) " + negative + " = " + negInt + " （向零取整）");
        
        // 溢出情况：int 转 byte
        int big = 128;
        byte small = (byte) big;
        System.out.println("\nint " + big + " -> byte " + small + " （溢出，只保留低8位）");
        
        // 解释：128 的二进制是 10000000
        // 作为 byte（有符号），最高位是 1，表示负数
        // 补码表示，值为 -128
        
        // 大数转小数，高位被截断
        long large = 10000000000L;
        int truncated = (int) large;
        System.out.println("(int) " + large + "L = " + truncated + " （高位截断）");
        
        // 浮点数转整数
        float f = 1234.567f;
        int fromFloat = (int) f;
        System.out.println("\n(int) " + f + " = " + fromFloat);
    }
    
    /**
     * 演示包装类转换
     * 包括自动装箱、自动拆箱和字符串转换
     */
    public void demonstrateWrapperConversion() {
        System.out.println("\n=== 包装类转换 ===");
        
        // 自动装箱：基本类型自动转换为包装类
        Integer obj1 = 100;                 // 等价于 Integer.valueOf(100)
        Integer obj2 = Integer.valueOf(100);
        System.out.println("自动装箱: Integer = " + obj1);
        System.out.println("valueOf: Integer = " + obj2);
        
        // 自动拆箱：包装类自动转换为基本类型
        int primitive = obj1;               // 等价于 obj1.intValue()
        int primitive2 = obj1.intValue();
        System.out.println("\n自动拆箱: int = " + primitive);
        System.out.println("intValue: int = " + primitive2);
        
        // 字符串转数字
        String numStr = "12345";
        int num = Integer.parseInt(numStr);
        double d = Double.parseDouble("3.14159");
        System.out.println("\n字符串转数字:");
        System.out.println("Integer.parseInt(\"12345\") = " + num);
        System.out.println("Double.parseDouble(\"3.14159\") = " + d);
        
        // 数字转字符串
        String str1 = String.valueOf(123);
        String str2 = Integer.toString(456);
        String str3 = 789 + "";             // 简单但效率较低的方式
        System.out.println("\n数字转字符串:");
        System.out.println("String.valueOf(123) = \"" + str1 + "\"");
        System.out.println("Integer.toString(456) = \"" + str2 + "\"");
        
        // 不同进制转换
        String binary = Integer.toBinaryString(255);    // 二进制
        String octal = Integer.toOctalString(255);      // 八进制
        String hex = Integer.toHexString(255);          // 十六进制
        System.out.println("\n255 的不同进制表示:");
        System.out.println("二进制: " + binary);
        System.out.println("八进制: " + octal);
        System.out.println("十六进制: " + hex);
    }
    
    /**
     * 演示引用类型转换
     * 包括向上转型和向下转型
     */
    public void demonstrateReferenceConversion() {
        System.out.println("\n=== 引用类型转换 ===");
        
        // 向上转型（Upcasting）：子类转父类，自动进行，安全
        Object obj = "Hello";           // String 向上转型为 Object
        System.out.println("向上转型: String -> Object");
        System.out.println("obj = " + obj);
        
        // 向下转型（Downcasting）：父类转子类，需要显式转换，可能不安全
        if (obj instanceof String) {
            String str = (String) obj;  // 安全向下转型
            System.out.println("\n向下转型: Object -> String");
            System.out.println("str = \"" + str + "\"");
            System.out.println("长度: " + str.length());
        }
        
        // 错误的向下转型会导致 ClassCastException
        Object num = 123;  // Integer 对象
        // String wrong = (String) num;  // 编译通过，但运行时会抛出 ClassCastException
        
        // 使用 instanceof 进行类型检查
        if (num instanceof String) {
            String s = (String) num;
        } else {
            System.out.println("\nnum 不是 String 类型，跳过转型");
        }
        
        // Java 16+ 模式匹配（如果使用的是 Java 16+）
        // if (obj instanceof String s) {
        //     System.out.println("模式匹配: " + s.toUpperCase());
        // }
    }
    
    /**
     * 主方法，运行所有演示
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        TypeConversion demo = new TypeConversion();
        
        System.out.println("╔════════════════════════════════════════════════╗");
        System.out.println("║     Java 类型转换演示                          ║");
        System.out.println("╚════════════════════════════════════════════════╝\n");
        
        demo.demonstrateImplicitConversion();
        demo.demonstrateExplicitConversion();
        demo.demonstrateWrapperConversion();
        demo.demonstrateReferenceConversion();
        
        System.out.println("\n══════════════════════════════════════════════════");
        System.out.println("演示完成！");
    }
}
