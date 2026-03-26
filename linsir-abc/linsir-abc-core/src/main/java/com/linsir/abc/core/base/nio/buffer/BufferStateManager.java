package com.linsir.abc.core.base.nio.buffer;

import java.nio.*;

/**
 * Buffer状态管理器
 * 演示Buffer的核心操作：flip、clear、rewind、compact等
 *
 * 设计要点：
 * 1. Buffer的四个核心属性：mark、position、limit、capacity
 * 2. flip：从写模式切换到读模式
 * 3. clear：清空缓冲区，准备写入
 * 4. rewind：重置position，重新读取
 * 5. compact：压缩缓冲区，保留未读数据
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class BufferStateManager {

    /**
     * 演示Buffer的基本状态管理
     */
    public static void demonstrateBufferStates() {
        System.out.println("=== Buffer状态管理演示 ===\n");

        // 创建一个容量为10的ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(10);

        System.out.println("1. 初始状态 (allocate后):");
        printBufferState(buffer);

        // 写入数据
        System.out.println("\n2. 写入数据 'Hello':");
        buffer.put("Hello".getBytes());
        printBufferState(buffer);

        // flip切换到读模式
        System.out.println("\n3. flip() 切换到读模式:");
        buffer.flip();
        printBufferState(buffer);

        // 读取部分数据
        System.out.println("\n4. 读取3个字节:");
        byte[] readData = new byte[3];
        buffer.get(readData);
        System.out.println("   读取内容: " + new String(readData));
        printBufferState(buffer);

        // rewind重新读取
        System.out.println("\n5. rewind() 重新读取:");
        buffer.rewind();
        printBufferState(buffer);

        // 读取所有数据
        System.out.println("\n6. 读取所有数据:");
        byte[] allData = new byte[buffer.remaining()];
        buffer.get(allData);
        System.out.println("   读取内容: " + new String(allData));
        printBufferState(buffer);

        // clear清空缓冲区
        System.out.println("\n7. clear() 清空缓冲区:");
        buffer.clear();
        printBufferState(buffer);

        // 写入新数据
        System.out.println("\n8. 写入新数据 'World':");
        buffer.put("World".getBytes());
        printBufferState(buffer);
    }

    /**
     * 演示compact操作
     */
    public static void demonstrateCompact() {
        System.out.println("\n=== Compact操作演示 ===\n");

        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 写入数据
        buffer.put("ABCDEFGHIJ".getBytes());
        buffer.flip();

        System.out.println("1. 初始状态 (写入ABCDEFGHIJ后):");
        printBufferState(buffer);

        // 读取部分数据
        byte[] read = new byte[4];
        buffer.get(read);
        System.out.println("\n2. 读取4个字节 ('" + new String(read) + "'):");
        printBufferState(buffer);

        // compact压缩
        System.out.println("\n3. compact() 压缩缓冲区:");
        buffer.compact();
        printBufferState(buffer);

        System.out.println("   说明: position移动到6 (10-4), 前6个字节是'EFGHIJ'");

        // 继续写入
        System.out.println("\n4. 继续写入 '123':");
        buffer.put("123".getBytes());
        printBufferState(buffer);

        // 读取所有数据
        buffer.flip();
        System.out.println("\n5. flip后读取所有数据:");
        byte[] allData = new byte[buffer.remaining()];
        buffer.get(allData);
        System.out.println("   内容: '" + new String(allData) + "'");
    }

    /**
     * 演示mark和reset
     */
    public static void demonstrateMarkReset() {
        System.out.println("\n=== Mark/Reset操作演示 ===\n");

        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("ABCDEFGHIJ".getBytes());
        buffer.flip();

        System.out.println("1. 初始状态:");
        printBufferState(buffer);

        // 读取2个字节
        buffer.get();
        buffer.get();
        System.out.println("\n2. 读取2个字节后:");
        printBufferState(buffer);

        // 设置mark
        buffer.mark();
        System.out.println("\n3. mark() 设置标记:");
        System.out.println("   mark = " + buffer.position());

        // 再读取3个字节
        buffer.get();
        buffer.get();
        buffer.get();
        System.out.println("\n4. 再读取3个字节后:");
        printBufferState(buffer);

        // reset回到mark位置
        buffer.reset();
        System.out.println("\n5. reset() 回到标记位置:");
        printBufferState(buffer);

        // 读取剩余数据
        byte[] remaining = new byte[buffer.remaining()];
        buffer.get(remaining);
        System.out.println("\n6. 读取剩余数据: '" + new String(remaining) + "'");
    }

    /**
     * 演示不同类型Buffer的使用
     */
    public static void demonstrateDifferentBuffers() {
        System.out.println("\n=== 不同类型Buffer演示 ===\n");

        // ByteBuffer
        System.out.println("1. ByteBuffer:");
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 2);
        System.out.println("   容量: " + byteBuffer.capacity() + ", 写入2个字节");

        // CharBuffer
        System.out.println("\n2. CharBuffer:");
        CharBuffer charBuffer = CharBuffer.allocate(10);
        charBuffer.put('H');
        charBuffer.put('e');
        charBuffer.put('l');
        charBuffer.put('l');
        charBuffer.put('o');
        charBuffer.flip();
        System.out.println("   内容: " + charBuffer.toString());

        // IntBuffer
        System.out.println("\n3. IntBuffer:");
        IntBuffer intBuffer = IntBuffer.allocate(5);
        intBuffer.put(10);
        intBuffer.put(20);
        intBuffer.put(30);
        intBuffer.flip();
        System.out.print("   内容: ");
        while (intBuffer.hasRemaining()) {
            System.out.print(intBuffer.get() + " ");
        }
        System.out.println();

        // DoubleBuffer
        System.out.println("\n4. DoubleBuffer:");
        DoubleBuffer doubleBuffer = DoubleBuffer.allocate(3);
        doubleBuffer.put(3.14);
        doubleBuffer.put(2.718);
        doubleBuffer.put(1.414);
        doubleBuffer.flip();
        System.out.print("   内容: ");
        while (doubleBuffer.hasRemaining()) {
            System.out.printf("%.3f ", doubleBuffer.get());
        }
        System.out.println();

        // 视图Buffer
        System.out.println("\n5. 视图Buffer (ByteBuffer.asIntBuffer):");
        ByteBuffer bb = ByteBuffer.allocate(16);
        IntBuffer view = bb.asIntBuffer();
        view.put(100);
        view.put(200);
        System.out.println("   通过IntBuffer视图写入2个int (占用8字节)");
        System.out.println("   ByteBuffer position: " + bb.position());
        System.out.println("   IntBuffer position: " + view.position());
    }

    /**
     * 打印Buffer状态
     */
    private static void printBufferState(Buffer buffer) {
        System.out.println("   position: " + buffer.position() +
                          ", limit: " + buffer.limit() +
                          ", capacity: " + buffer.capacity() +
                          ", remaining: " + buffer.remaining());
    }

    /**
     * 主演示方法
     */
    public static void demonstrate() {
        demonstrateBufferStates();
        demonstrateCompact();
        demonstrateMarkReset();
        demonstrateDifferentBuffers();
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
