package com.linsir.abc.core.base.lang.string;

/**
 * String不可变性演示类
 *
 * 本类演示String的不可变性特性及其影响：
 * 1. String对象一旦创建不可修改
 * 2. 字符串常量池机制
 * 3. String、StringBuilder、StringBuffer的区别
 * 4. 字符串拼接的性能影响
 *
 * 不可变性的优点：
 * - 线程安全
 * - 适合作为HashMap的key
 * - 字符串常量池节省内存
 * - 安全性（防止被篡改）
 *
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class StringImmutability {

    /**
     * 演示String的不可变性
     * 每次修改都会创建新对象
     */
    public void demonstrateImmutability() {
        String str = "Hello";
        System.out.println("原始字符串: " + str);
        System.out.println("原始hashCode: " + str.hashCode());

        // 看似修改了字符串，实际创建了新对象
        str = str + " World";
        System.out.println("修改后字符串: " + str);
        System.out.println("修改后hashCode: " + str.hashCode());

        // 证明：hashCode不同，说明是新对象
    }

    /**
     * 演示字符串常量池
     * 使用字面量创建的字符串会被放入常量池
     */
    public void demonstrateStringPool() {
        // 方式1：字面量创建（推荐）
        String str1 = "Java";
        String str2 = "Java";

        // 方式2：new创建
        String str3 = new String("Java");
        String str4 = new String("Java");

        System.out.println("str1 == str2: " + (str1 == str2)); // true，同一常量池对象
        System.out.println("str3 == str4: " + (str3 == str4)); // false，不同堆对象
        System.out.println("str1 == str3: " + (str1 == str3)); // false

        // intern()方法：将字符串放入常量池
        String str5 = str3.intern();
        System.out.println("str1 == str5 (after intern): " + (str1 == str5)); // true
    }

    /**
     * 演示字符串拼接的性能问题
     * 循环中使用+拼接会产生大量临时对象
     */
    public void demonstrateConcatenationProblem() {
        int count = 10000;

        // 方式1：使用+拼接（性能差）
        long start1 = System.currentTimeMillis();
        String result1 = "";
        for (int i = 0; i < count; i++) {
            result1 += i; // 每次循环都创建新String对象
        }
        long end1 = System.currentTimeMillis();
        System.out.println("使用+拼接耗时: " + (end1 - start1) + "ms");

        // 方式2：使用StringBuilder（性能好）
        long start2 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(i);
        }
        String result2 = sb.toString();
        long end2 = System.currentTimeMillis();
        System.out.println("使用StringBuilder耗时: " + (end2 - start2) + "ms");
    }

    /**
     * 演示StringBuilder vs StringBuffer
     * StringBuilder：非线程安全，性能更好
     * StringBuffer：线程安全，性能稍差
     */
    public void demonstrateBuilderVsBuffer() {
        // StringBuilder - 单线程环境使用
        StringBuilder builder = new StringBuilder();
        builder.append("Hello").append(" ").append("World");
        System.out.println("StringBuilder结果: " + builder.toString());

        // StringBuffer - 多线程环境使用
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hello").append(" ").append("World");
        System.out.println("StringBuffer结果: " + buffer.toString());
    }

    /**
     * 演示不可变性的线程安全
     * String对象可以安全地在多个线程间共享
     */
    public void demonstrateThreadSafety() throws InterruptedException {
        final String shared = "Shared String";

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                // 读取String是线程安全的
                System.out.println(Thread.currentThread().getName() + ": " + shared);
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("最终字符串: " + shared); // 仍然是原始值
    }

    /**
     * 演示String的常用方法
     */
    public void demonstrateCommonMethods() {
        String str = "  Hello World  ";

        // 长度
        System.out.println("长度: " + str.length());

        // 去除空格
        System.out.println("trim(): '" + str.trim() + "'");

        // 截取
        System.out.println("substring(2, 7): '" + str.substring(2, 7) + "'");

        // 替换
        System.out.println("replace('l', 'L'): '" + str.replace('l', 'L') + "'");

        // 分割
        String[] parts = str.trim().split(" ");
        System.out.println("split结果: ");
        for (String part : parts) {
            System.out.println("  " + part);
        }

        // 包含判断
        System.out.println("contains('World'): " + str.contains("World"));

        // 索引查找
        System.out.println("indexOf('o'): " + str.indexOf('o'));
        System.out.println("lastIndexOf('o'): " + str.lastIndexOf('o'));
    }
}
