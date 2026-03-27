package com.linsir.abc.core.jvm.memory.direct;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * 直接内存(堆外内存)溢出
 *
 * <p>直接内存并不是虚拟机运行时数据区的一部分，但是这部分内存也被频繁地使用，
 * 而且也可能导致OutOfMemoryError异常出现。</p>
 *
 * <p>本类提供两种触发直接内存OOM的方式：</p>
 * <ol>
 *   <li>使用Unsafe类直接分配内存</li>
 *   <li>使用NIO的ByteBuffer.allocateDirect()</li>
 * </ol>
 *
 * <p><strong>VM参数:</strong> -Xmx20m -XX:MaxDirectMemorySize=10m</p>
 *
 * <p><strong>预期异常:</strong> java.lang.OutOfMemoryError: Direct buffer memory</p>
 *
 * <p><strong>注意:</strong> 使用sun.misc.Unsafe需要添加JVM参数: --add-opens java.base/jdk.internal.misc=ALL-UNNAMED (JDK 9+)</p>
 *
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class DirectMemoryOutOfMemory {

    /**
     * 每次分配的内存大小: 1MB
     */
    private static final int ALLOCATE_SIZE = 1024 * 1024;

    /**
     * 内存分配计数器
     */
    private int allocateCount = 0;

    /**
     * 使用Unsafe类直接分配堆外内存
     *
     * <p>通过反射获取Unsafe实例，直接调用allocateMemory方法分配内存。
     * 这种方式分配的内存不受ByteBuffer管理，是真正的"裸"内存分配。</p>
     *
     * @throws Exception 反射操作可能抛出的异常
     */
    public void allocateByUnsafe() throws Exception {
        // 通过反射获取Unsafe实例
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        System.out.println("使用Unsafe分配直接内存...");

        while (true) {
            // 分配直接内存
            unsafe.allocateMemory(ALLOCATE_SIZE);
            allocateCount++;

            if (allocateCount % 100 == 0) {
                System.out.println("已分配 " + allocateCount + " MB 直接内存");
            }
        }
    }

    /**
     * 使用NIO ByteBuffer分配直接内存
     *
     * <p>通过ByteBuffer.allocateDirect()分配直接内存，
     * 这种方式分配的内存受-XX:MaxDirectMemorySize参数限制。</p>
     */
    public void allocateByByteBuffer() {
        System.out.println("使用ByteBuffer.allocateDirect()分配直接内存...");

        // 用于保持引用，防止被GC回收
        java.util.List<ByteBuffer> bufferList = new java.util.ArrayList<>();

        while (true) {
            // 分配直接内存
            ByteBuffer buffer = ByteBuffer.allocateDirect(ALLOCATE_SIZE);
            bufferList.add(buffer);

            allocateCount++;
            if (allocateCount % 10 == 0) {
                System.out.println("已分配 " + allocateCount + " MB 直接内存");
            }
        }
    }

    /**
     * 程序入口
     *
     * <p>默认使用ByteBuffer方式分配直接内存。</p>
     *
     * @param args 命令行参数，args[0]为分配方式: "unsafe" 或 "buffer"
     */
    public static void main(String[] args) {
        DirectMemoryOutOfMemory demo = new DirectMemoryOutOfMemory();

        // 默认使用ByteBuffer方式
        boolean useUnsafe = args.length > 0 && "unsafe".equals(args[0]);

        System.out.println("开始分配直接内存，准备触发OOM...");
        System.out.println("VM参数: -Xmx20m -XX:MaxDirectMemorySize=10m");
        System.out.println("分配方式: " + (useUnsafe ? "Unsafe" : "ByteBuffer"));

        try {
            if (useUnsafe) {
                demo.allocateByUnsafe();
            } else {
                demo.allocateByByteBuffer();
            }
        } catch (OutOfMemoryError e) {
            System.err.println("捕获到OutOfMemoryError: " + e.getMessage());
            System.err.println("已分配内存: " + demo.allocateCount + " MB");
            throw e;
        } catch (Exception e) {
            System.err.println("发生异常: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
