package com.linsir.abc.core.jvm.tuning.offheap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 堆外内存泄漏演示类
 * 演示错误的堆外内存使用方式导致的内存泄漏问题
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class DirectBufferLeakDemo {

    private static final Logger LOGGER = Logger.getLogger(DirectBufferLeakDemo.class.getName());

    /**
     * 错误示例：未释放ByteBuffer导致的内存泄漏
     * 每次调用都会分配新的直接内存，但从不释放
     *
     * @param data 数据
     */
    public void wrongWay(byte[] data) {
        // 错误：分配直接缓冲区但不释放
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        buffer.put(data);
        buffer.flip();

        // 处理数据...
        processBuffer(buffer);

        // 错误：没有调用cleaner().clean()释放堆外内存
        // 只有buffer被GC回收时，堆外内存才会被释放
        // 但如果buffer被长时间引用，堆外内存就会泄漏
    }

    /**
     * 正确示例：使用try-finally确保释放
     *
     * @param data 数据
     */
    public void correctWayWithFinally(byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(data.length);
        try {
            buffer.put(data);
            buffer.flip();
            processBuffer(buffer);
        } finally {
            // 正确：显式释放堆外内存
            cleanDirectBuffer(buffer);
        }
    }

    /**
     * 正确示例：使用DirectBufferManager管理
     *
     * @param data    数据
     * @param manager 缓冲区管理器
     */
    public void correctWayWithManager(byte[] data, DirectBufferManager manager) {
        ByteBuffer buffer = manager.allocate(data.length);
        try {
            buffer.put(data);
            buffer.flip();
            processBuffer(buffer);
        } finally {
            // 正确：使用管理器释放
            manager.release(buffer);
        }
    }

    /**
     * 模拟处理缓冲区
     *
     * @param buffer ByteBuffer
     */
    private void processBuffer(ByteBuffer buffer) {
        // 模拟数据处理
        LOGGER.fine("Processing buffer with capacity: " + buffer.capacity());
    }

    /**
     * 清理直接缓冲区
     * 兼容JDK 8和JDK 9+
     *
     * @param buffer ByteBuffer
     */
    private void cleanDirectBuffer(ByteBuffer buffer) {
        if (buffer == null || !buffer.isDirect()) {
            return;
        }

        try {
            // 方式1：尝试使用JDK 9+的Unsafe.invokeCleaner
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Object unsafe = unsafeField.get(null);

            try {
                Method invokeCleaner = unsafeClass.getMethod("invokeCleaner", ByteBuffer.class);
                invokeCleaner.invoke(unsafe, buffer);
                LOGGER.fine("Direct buffer cleaned successfully using Unsafe");
            } catch (NoSuchMethodException e) {
                // JDK 8使用cleaner
                cleanWithCleaner(buffer);
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to clean direct buffer: " + e.getMessage());
        }
    }

    /**
     * 使用Cleaner清理（JDK 8兼容）
     *
     * @param buffer ByteBuffer实例
     */
    private void cleanWithCleaner(ByteBuffer buffer) {
        try {
            Field cleanerField = buffer.getClass().getDeclaredField("cleaner");
            cleanerField.setAccessible(true);
            Object cleaner = cleanerField.get(buffer);

            if (cleaner != null) {
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.invoke(cleaner);
                LOGGER.fine("Direct buffer cleaned successfully using Cleaner");
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to clean with Cleaner: " + e.getMessage());
        }
    }

    /**
     * 演示内存泄漏场景
     * 注意：运行此方法可能导致OOM
     *
     * @param iterations 迭代次数
     */
    public void demonstrateLeak(int iterations) {
        LOGGER.warning("Starting memory leak demonstration...");
        LOGGER.warning("This may cause OutOfMemoryError!");

        List<ByteBuffer> buffers = new ArrayList<>();

        for (int i = 0; i < iterations; i++) {
            try {
                // 分配1MB的直接内存
                ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
                buffers.add(buffer);

                if (i % 100 == 0) {
                    LOGGER.info("Allocated " + (i + 1) + " buffers");
                }
            } catch (OutOfMemoryError e) {
                LOGGER.severe("OutOfMemoryError at iteration " + i + ": " + e.getMessage());
                break;
            }
        }

        LOGGER.info("Leak demonstration completed. Total buffers allocated: " + buffers.size());
    }

    /**
     * 演示正确的内存管理方式
     *
     * @param iterations 迭代次数
     */
    public void demonstrateCorrectUsage(int iterations) {
        LOGGER.info("Starting correct memory usage demonstration...");

        DirectBufferManager manager = new DirectBufferManager(512 * 1024 * 1024, true); // 512MB限制

        for (int i = 0; i < iterations; i++) {
            ByteBuffer buffer = null;
            try {
                // 分配1MB的直接内存
                buffer = manager.allocate(1024 * 1024);

                // 使用缓冲区...
                buffer.put(new byte[1024 * 1024]);

                // 立即释放
                manager.release(buffer);

                if (i % 100 == 0) {
                    LOGGER.info("Iteration " + (i + 1) + ": " + manager.getStatistics());
                }
            } catch (OutOfMemoryError e) {
                LOGGER.severe("OutOfMemoryError at iteration " + i + ": " + e.getMessage());
                if (buffer != null) {
                    manager.release(buffer);
                }
            }
        }

        manager.shutdown();
        LOGGER.info("Correct usage demonstration completed");
    }

    /**
     * 生成JVM参数建议
     *
     * @return JVM参数建议
     */
    public static String getJvmOptionsRecommendation() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 堆外内存JVM参数建议 ===\n\n");
        sb.append("1. 限制堆外内存大小:\n");
        sb.append("   -XX:MaxDirectMemorySize=2g\n\n");
        sb.append("2. 启用GC日志:\n");
        sb.append("   -XX:+PrintGCDetails\n");
        sb.append("   -XX:+PrintGCTimeStamps\n");
        sb.append("   -Xloggc:/logs/gc.log\n\n");
        sb.append("3. 发生OOM时生成堆转储:\n");
        sb.append("   -XX:+HeapDumpOnOutOfMemoryError\n");
        sb.append("   -XX:HeapDumpPath=/logs/heapdump.hprof\n\n");
        sb.append("4. 启用NMT（Native Memory Tracking）:\n");
        sb.append("   -XX:NativeMemoryTracking=summary\n\n");
        return sb.toString();
    }
}
