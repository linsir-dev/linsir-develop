package com.linsir.abc.core.jvm.classloading.process;

import java.util.logging.Logger;

/**
 * <clinit>()方法演示类
 * 演示<clinit>()方法的执行顺序、特点以及线程安全性
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class ClinitDemo {

    private static final Logger LOGGER = Logger.getLogger(ClinitDemo.class.getName());

    /**
     * 静态变量赋值顺序演示
     */
    public static class StaticOrderDemo {
        static {
            // 可以给后面的变量赋值
            value = 0;
            // System.out.println(value); // 编译错误：非法前向引用
        }

        public static int value = 1;

        static {
            System.out.println("StaticOrderDemo static block, value = " + value);
            value = 2;
        }
    }

    /**
     * 父类静态初始化
     */
    public static class Parent {
        public static int A = 1;

        static {
            System.out.println("Parent static block");
            A = 2;
        }
    }

    /**
     * 子类静态初始化，演示父类优先
     */
    public static class Child extends Parent {
        public static int B = A;

        static {
            System.out.println("Child static block, B = " + B);
        }
    }

    /**
     * 接口静态初始化
     */
    public interface InterfaceA {
        int A = 1;
    }

    /**
     * 子接口
     */
    public interface InterfaceB extends InterfaceA {
        int B = 2;
    }

    /**
     * 实现接口的类
     */
    public static class InterfaceImpl implements InterfaceB {
        public static int C = A + B;

        static {
            System.out.println("InterfaceImpl static block");
        }
    }

    /**
     * 演示静态变量赋值顺序
     */
    public void demonstrateStaticOrder() {
        LOGGER.info("=== 演示静态变量赋值顺序 ===");
        System.out.println("\n--- 开始测试 ---");

        System.out.println("StaticOrderDemo.value = " + StaticOrderDemo.value);
        // 预期输出2，因为静态代码块最后将value赋值为2

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：静态代码块按源文件中出现的顺序执行");
    }

    /**
     * 演示父类静态初始化优先
     */
    public void demonstrateParentPriority() {
        LOGGER.info("=== 演示父类静态初始化优先 ===");
        System.out.println("\n--- 开始测试 ---");

        System.out.println("Child.B = " + Child.B);
        // 预期输出2，因为父类静态代码块将A赋值为2

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：父类的<clinit>()方法优先于子类的<clinit>()方法执行");
    }

    /**
     * 演示接口初始化
     */
    public void demonstrateInterfaceInit() {
        LOGGER.info("=== 演示接口初始化 ===");
        System.out.println("\n--- 开始测试 ---");

        System.out.println("InterfaceImpl.C = " + InterfaceImpl.C);

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：实现类初始化时，接口也会初始化");
    }

    /**
     * 演示<clinit>()方法的线程安全性
     */
    public void demonstrateThreadSafety() {
        LOGGER.info("=== 演示<clinit>()方法的线程安全性 ===");
        System.out.println("\n--- 开始测试 ---");

        Runnable script = () -> {
            System.out.println(Thread.currentThread() + " start");
            DeadLoopClass dlc = new DeadLoopClass();
            System.out.println(Thread.currentThread() + " run over");
        };

        Thread thread1 = new Thread(script, "Thread-1");
        Thread thread2 = new Thread(script, "Thread-2");

        thread1.start();
        thread2.start();

        // 等待一段时间观察输出
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 中断线程（因为DeadLoopClass会进入死循环）
        thread1.interrupt();
        thread2.interrupt();

        System.out.println("--- 测试结束 ---\n");
        LOGGER.info("结论：<clinit>()方法在多线程环境中会被正确地加锁同步");
    }

    /**
     * 运行所有演示
     */
    public void runAllDemos() {
        LOGGER.info("\n========== <clinit>()方法演示 ==========\n");

        demonstrateStaticOrder();
        demonstrateParentPriority();
        demonstrateInterfaceInit();
        // demonstrateThreadSafety(); // 会进入死循环，默认不执行

        LOGGER.info("\n========== 演示结束 ==========\n");
    }

    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        ClinitDemo demo = new ClinitDemo();
        demo.runAllDemos();
    }
}

/**
 * 死循环类，用于演示<clinit>()的线程安全性
 * 注意：这个类会导致死循环，谨慎使用
 */
class DeadLoopClass {
    static {
        System.out.println(Thread.currentThread() + " init DeadLoopClass");
        // 模拟长时间初始化
        // 实际测试中可以使用短时间的sleep
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread() + " interrupted");
        }
    }
}
