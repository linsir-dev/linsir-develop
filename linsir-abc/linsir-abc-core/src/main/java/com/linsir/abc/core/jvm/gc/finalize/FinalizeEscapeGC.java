package com.linsir.abc.core.jvm.gc.finalize;

/**
 * Finalize方法逃逸示例
 * 
 * 演示对象如何在finalize()方法中自我拯救，逃脱垃圾回收。
 * 同时也演示了finalize()方法只能被执行一次的特性。
 * 
 * 注意：finalize()方法在JDK 9中已被标记为废弃，
 * 建议使用try-finally或其他方式替代。
 * 
 * @author linsir
 * @version 1.0.0
 * @since 2026/3/28
 * @deprecated JDK 9+ 中finalize()已被废弃，建议使用try-with-resources或Cleaner
 */
public class FinalizeEscapeGC {

    /**
     * 用于保存对象的静态引用
     * 在finalize()方法中将this赋值给这个变量实现自我拯救
     */
    public static FinalizeEscapeGC SAVE_HOOK = null;

    /**
     * 对象标识，用于区分不同实例
     */
    private final String name;

    /**
     * 构造函数
     * 
     * @param name 对象名称
     */
    public FinalizeEscapeGC(String name) {
        this.name = name;
    }

    /**
     * 验证对象是否存活
     */
    public void isAlive() {
        System.out.println("[" + name + "] yes, I am still alive :)");
    }

    /**
     * 获取对象名称
     * 
     * @return 对象名称
     */
    public String getName() {
        return name;
    }

    /**
     * 重写finalize方法
     * 在对象被回收前执行，可以在此方法中实现自我拯救
     * 
     * @throws Throwable 可能抛出的异常
     */
    @Override
    @SuppressWarnings("deprecation")
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("[" + name + "] finalize method executed!");

        // 自我拯救：将this赋值给静态变量，重新建立引用链
        FinalizeEscapeGC.SAVE_HOOK = this;
        System.out.println("[" + name + "] Self-rescue: SAVE_HOOK = this");
    }

    /**
     * 演示对象自我拯救
     * 
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static void demonstrateEscape() throws InterruptedException {
        System.out.println("=== Finalize方法自我拯救演示 ===\n");

        // 创建对象并建立强引用
        SAVE_HOOK = new FinalizeEscapeGC("Object-1");
        System.out.println("创建对象: " + SAVE_HOOK.getName());

        // ========== 第一次拯救 ==========
        System.out.println("\n--- 第一次拯救 ---");
        System.out.println("断开强引用: SAVE_HOOK = null");
        SAVE_HOOK = null;

        System.out.println("调用System.gc()...");
        System.gc();

        // 因为Finalizer方法优先级很低，暂停等待它执行
        System.out.println("等待Finalizer线程执行（500ms）...");
        Thread.sleep(500);

        // 检查对象是否存活
        if (SAVE_HOOK != null) {
            System.out.println("结果: 对象成功拯救自己！");
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("结果: 对象已死亡 :( ");
        }

        // ========== 第二次尝试（相同的代码）==========
        System.out.println("\n--- 第二次尝试（相同代码）---");
        System.out.println("再次断开强引用: SAVE_HOOK = null");
        SAVE_HOOK = null;

        System.out.println("调用System.gc()...");
        System.gc();

        System.out.println("等待Finalizer线程执行（500ms）...");
        Thread.sleep(500);

        // 检查对象是否存活
        if (SAVE_HOOK != null) {
            System.out.println("结果: 对象成功拯救自己！");
            SAVE_HOOK.isAlive();
        } else {
            System.out.println("结果: 对象已死亡 :( ");
        }

        System.out.println("\n结论：finalize()方法只会被执行一次！");
    }

    /**
     * 演示finalize()方法的不可靠性
     * 
     * @throws InterruptedException 当线程被中断时抛出
     */
    public static void demonstrateUnreliability() throws InterruptedException {
        System.out.println("\n=== Finalize方法不可靠性演示 ===\n");

        // 创建多个对象
        FinalizeEscapeGC[] objects = new FinalizeEscapeGC[3];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new FinalizeEscapeGC("Unreliable-" + (i + 1));
        }

        System.out.println("创建3个对象");

        // 断开所有引用
        for (int i = 0; i < objects.length; i++) {
            objects[i] = null;
        }
        System.out.println("断开所有引用");

        // 调用GC
        System.out.println("调用System.gc()...");
        System.gc();

        // 短暂等待
        System.out.println("短暂等待（100ms）...");
        Thread.sleep(100);

        System.out.println("\n注意：");
        System.out.println("1. finalize()执行时间不确定");
        System.out.println("2. finalize()执行顺序不确定");
        System.out.println("3. finalize()中的异常会被忽略");
        System.out.println("4. finalize()可能导致对象复活，影响GC效率");
    }

    /**
     * 演示finalize()的替代方案
     */
    public static void demonstrateAlternative() {
        System.out.println("\n=== Finalize方法的替代方案 ===\n");

        System.out.println("1. try-finally 语句");
        System.out.println("   try {");
        System.out.println("       // 使用资源");
        System.out.println("   } finally {");
        System.out.println("       // 清理资源");
        System.out.println("   }");

        System.out.println("\n2. try-with-resources (Java 7+)");
        System.out.println("   try (Resource res = new Resource()) {");
        System.out.println("       // 使用资源");
        System.out.println("   } // 自动关闭");

        System.out.println("\n3. Cleaner (Java 9+)");
        System.out.println("   private static final Cleaner cleaner = Cleaner.create();");
        System.out.println("   private final Cleaner.Cleanable cleanable;");
        System.out.println("   cleanable = cleaner.register(this, new CleanupTask());");

        System.out.println("\n推荐使用try-with-resources或Cleaner替代finalize()");
    }

    /**
     * 主方法
     * 
     * @param args 命令行参数
     * @throws InterruptedException 当线程被中断时抛出
     */
    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws InterruptedException {
        // 演示自我拯救
        demonstrateEscape();

        // 演示不可靠性
        demonstrateUnreliability();

        // 演示替代方案
        demonstrateAlternative();
    }
}
