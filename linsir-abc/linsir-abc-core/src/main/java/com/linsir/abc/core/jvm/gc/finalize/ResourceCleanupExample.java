package com.linsir.abc.core.jvm.gc.finalize;

import java.lang.ref.Cleaner;

/**
 * 资源清理示例
 * 
 * 演示finalize()的替代方案：
 * 1. try-with-resources
 * 2. Cleaner (Java 9+)
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 */
public class ResourceCleanupExample {

    /**
     * 使用try-with-resources的资源类
     */
    public static class AutoCloseableResource implements AutoCloseable {

        private final String name;
        private boolean closed = false;

        /**
         * 构造函数
         * 
         * @param name 资源名称
         */
        public AutoCloseableResource(String name) {
            this.name = name;
            System.out.println("[" + name + "] 资源创建");
        }

        /**
         * 使用资源
         */
        public void doSomething() {
            if (closed) {
                throw new IllegalStateException("资源已关闭");
            }
            System.out.println("[" + name + "] 使用资源");
        }

        /**
         * 关闭资源
         */
        @Override
        public void close() {
            if (!closed) {
                closed = true;
                System.out.println("[" + name + "] 资源关闭（try-with-resources）");
            }
        }
    }

    /**
     * 使用Cleaner的资源类
     */
    public static class CleanerResource {

        /**
         * 共享的Cleaner实例
         */
        private static final Cleaner cleaner = Cleaner.create();

        private final String name;
        private final Cleaner.Cleanable cleanable;
        private final State state;

        /**
         * 清理任务类
         */
        private static class State implements Runnable {
            private final String name;

            State(String name) {
                this.name = name;
            }

            @Override
            public void run() {
                System.out.println("[" + name + "] 资源清理（Cleaner）");
            }
        }

        /**
         * 构造函数
         * 
         * @param name 资源名称
         */
        public CleanerResource(String name) {
            this.name = name;
            this.state = new State(name);
            this.cleanable = cleaner.register(this, state);
            System.out.println("[" + name + "] 资源创建（使用Cleaner）");
        }

        /**
         * 使用资源
         */
        public void doSomething() {
            System.out.println("[" + name + "] 使用资源");
        }

        /**
         * 显式关闭资源
         */
        public void close() {
            System.out.println("[" + name + "] 显式关闭资源");
            cleanable.clean();
        }
    }

    /**
     * 演示try-with-resources用法
     */
    public static void demonstrateTryWithResources() {
        System.out.println("=== try-with-resources 演示 ===\n");

        // 基本用法
        System.out.println("1. 基本用法:");
        try (AutoCloseableResource resource = new AutoCloseableResource("Resource-1")) {
            resource.doSomething();
        } // 自动调用close()

        System.out.println();

        // 多个资源
        System.out.println("2. 多个资源:");
        try (AutoCloseableResource res1 = new AutoCloseableResource("Resource-A");
             AutoCloseableResource res2 = new AutoCloseableResource("Resource-B")) {
            res1.doSomething();
            res2.doSomething();
        } // 按相反顺序自动关闭

        System.out.println();

        // 异常处理
        System.out.println("3. 异常处理:");
        try (AutoCloseableResource resource = new AutoCloseableResource("Resource-Exception")) {
            resource.doSomething();
            throw new RuntimeException("模拟异常");
        } catch (Exception e) {
            System.out.println("捕获异常: " + e.getMessage());
            // close()仍会被调用
        }
    }

    /**
     * 演示Cleaner用法
     */
    public static void demonstrateCleaner() throws InterruptedException {
        System.out.println("\n=== Cleaner 演示 ===\n");

        // 创建资源
        System.out.println("1. 创建资源:");
        CleanerResource resource = new CleanerResource("CleanerResource-1");
        resource.doSomething();

        // 显式关闭
        System.out.println("\n2. 显式关闭:");
        resource.close();

        // 创建另一个资源，让其被GC回收
        System.out.println("\n3. GC自动清理:");
        CleanerResource resource2 = new CleanerResource("CleanerResource-2");
        resource2.doSomething();

        System.out.println("断开引用...");
        resource2 = null;

        System.out.println("调用System.gc()...");
        System.gc();

        System.out.println("等待Cleaner执行...");
        Thread.sleep(1000);
    }

    /**
     * 对比finalize()和替代方案
     */
    public static void compareApproaches() {
        System.out.println("\n=== 方案对比 ===\n");

        System.out.println("┌─────────────────┬───────────────┬───────────────┬───────────────┐");
        System.out.println("│     特性        │  finalize()   │try-with-resources│   Cleaner    │");
        System.out.println("├─────────────────┼───────────────┼───────────────┼───────────────┤");
        System.out.println("│ 执行时机        │   不确定      │    确定       │   不确定      │");
        System.out.println("│ 执行次数        │   仅一次      │    每次       │   每次        │");
        System.out.println("│ 异常处理        │   被忽略      │   正常处理    │   被捕获      │");
        System.out.println("│ 性能影响        │   较大        │    无         │    较小       │");
        System.out.println("│ 推荐使用        │    否         │     是        │     是        │");
        System.out.println("└─────────────────┴───────────────┴───────────────┴───────────────┘");

        System.out.println("\n建议：");
        System.out.println("• 优先使用try-with-resources管理资源");
        System.out.println("• 需要后台清理时使用Cleaner");
        System.out.println("• 避免使用finalize()");
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static void main(String[] args) throws InterruptedException {
        // 演示try-with-resources
        demonstrateTryWithResources();

        // 演示Cleaner
        demonstrateCleaner();

        // 方案对比
        compareApproaches();
    }
}
