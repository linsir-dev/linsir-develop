package com.linsir.abc.pdai.base.oopdemo.str;/*
 * StringVsBuffersDemo.java
 *
 * 演示：
 *  - String 的不可变性（immutable）
 *  - StringBuffer 的可变性与线程安全（synchronized）
 *  - StringBuilder 的可变性与非线程安全（更快但不安全于并发）
 *
 * 编译：
 *   javac StringVsBuffersDemo.java
 * 运行：
 *   java StringVsBuffersDemo
 *
 * 说明：线程安全示例的表现是非确定性的（StringBuilder 在并发情况下可能出现长度不正确或数据混乱）。
 */

public class StringVsBuffersDemo {

    public static void main(String[] args) throws Exception {
        System.out.println("== String 不可变性示例 ==");
        demoStringImmutability();

        System.out.println("\n== StringBuffer vs StringBuilder 可变性示例 ==");
        demoMutability();

        System.out.println("\n== 线程安全性示例（并发追加） ==");
        demoThreadSafety();

        System.out.println("\n== 性能基准（多次 append） ==");
        demoPerformance();
    }

    // 演示 String 是不可变的
    static void demoStringImmutability() {
        String s1 = "Hello";
        String s2 = s1;
        s1 = s1.concat(", world!"); // 返回新对象，s1 指向新对象
        System.out.println("原来引用 s2: " + s2); // 仍然是 "Hello"
        System.out.println("s1: " + s1);          // "Hello, world!"
        // 说明：对 String 的拼接会产生新字符串（若使用 +，编译器在循环中也会变成 StringBuilder 导致效率问题）
    }

    // 演示可变对象的行为
    static void demoMutability() {
        StringBuffer sbuf = new StringBuffer("A");
        StringBuilder sbuild = new StringBuilder("A");

        sbuf.append("B");
        sbuild.append("B");

        System.out.println("StringBuffer 内容: " + sbuf.toString());
        System.out.println("StringBuilder 内容: " + sbuild.toString());

        // 可以继续修改（不会产生新的对象引用）
        sbuf.append("C").append("D");
        sbuild.append("C").append("D");

        System.out.println("StringBuffer 修改后: " + sbuf);
        System.out.println("StringBuilder 修改后: " + sbuild);
    }

    // 线程安全性示例：两个或多个线程并发向同一个对象追加大量字符
    static void demoThreadSafety() throws InterruptedException {
        final int THREADS = 4;
        final int APPENDS_PER_THREAD = 50000; // 每个线程追加次数
        final String APPEND_STR = "x";

        // 1) 使用 StringBuffer（线程安全）
        StringBuffer sharedBuffer = new StringBuffer();
        Thread[] tbuf = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            tbuf[i] = new Thread(() -> {
                for (int j = 0; j < APPENDS_PER_THREAD; j++) {
                    sharedBuffer.append(APPEND_STR);
                }
            }, "SBuf-" + i);
            tbuf[i].start();
        }
        for (Thread t : tbuf) t.join();

        int expectedLength = THREADS * APPENDS_PER_THREAD * APPEND_STR.length();
        System.out.println("StringBuffer 预计长度: " + expectedLength + ", 实际长度: " + sharedBuffer.length());

        // 2) 使用 StringBuilder（非线程安全）
        StringBuilder sharedBuilder = new StringBuilder();
        Thread[] tbuild = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            tbuild[i] = new Thread(() -> {
                for (int j = 0; j < APPENDS_PER_THREAD; j++) {
                    sharedBuilder.append(APPEND_STR);
                }
            }, "SBuild-" + i);
            tbuild[i].start();
        }
        for (Thread t : tbuild) t.join();

        System.out.println("StringBuilder 预计长度: " + expectedLength + ", 实际长度: " + sharedBuilder.length());
        // 注意：在很多 JVM 与很多次运行中，StringBuilder 在并发写入可能出现长度不同于预计长度，或出现数据错乱。
        // 但是这是非确定性的问题：某些运行可能“碰巧”没有出错，因此长期的压力测试更能暴露问题。
    }

    // 简单性能比较（仅作示例，真实基准应使用 JMH）
    static void demoPerformance() {
        final int N = 200_000;
        final String piece = "a";

        // 1) String 通过 + 在循环中拼接（最慢，因每次都会创建新对象）
        long t0 = System.nanoTime();
        String s = "";
        for (int i = 0; i < N; i++) {
            s = s + piece;
        }
        long t1 = System.nanoTime();
        long timeStringMs = (t1 - t0) / 1_000_000;
        System.out.println("String concat (s = s + piece) 耗时: " + timeStringMs + " ms (长度=" + s.length() + ")");

        // 2) StringBuffer
        t0 = System.nanoTime();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < N; i++) {
            sb.append(piece);
        }
        t1 = System.nanoTime();
        long timeBufferMs = (t1 - t0) / 1_000_000;
        System.out.println("StringBuffer append 耗时: " + timeBufferMs + " ms (长度=" + sb.length() + ")");

        // 3) StringBuilder
        t0 = System.nanoTime();
        StringBuilder sbld = new StringBuilder();
        for (int i = 0; i < N; i++) {
            sbld.append(piece);
        }
        t1 = System.nanoTime();
        long timeBuilderMs = (t1 - t0) / 1_000_000;
        System.out.println("StringBuilder append 耗时: " + timeBuilderMs + " ms (长度=" + sbld.length() + ")");

        System.out.println("\n说明（一般情况）:");
        System.out.println("- String 在循环中反复用 + 拼接最慢，因为每次都会分配新对象。");
        System.out.println("- StringBuffer 比 StringBuilder 慢一点，因为它的方法是同��的（线程安全）。");
        System.out.println("- StringBuilder 在单线程下通常最快，建议在非并发场景下使用。");
        System.out.println("- 若对并发写入有需求，请使用 StringBuffer 或在外部加锁；否则优先使用 StringBuilder。");
        System.out.println("- 对精确性能测试请使用 JMH（Java Microbenchmark Harness）。");
    }
}