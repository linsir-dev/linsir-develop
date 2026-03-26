package com.linsir.abc.core.base.nio.buffer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.*;

/**
 * BufferStateManager测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class BufferStateManagerTest {

    @Test
    public void testByteBufferAllocate() {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        assertEquals(10, buffer.capacity());
        assertEquals(0, buffer.position());
        assertEquals(10, buffer.limit());
        assertEquals(10, buffer.remaining());
    }

    @Test
    public void testByteBufferPut() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("Hello".getBytes());

        assertEquals(5, buffer.position());
        assertEquals(10, buffer.limit());
    }

    @Test
    public void testByteBufferFlip() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("Hello".getBytes());
        buffer.flip();

        assertEquals(0, buffer.position());
        assertEquals(5, buffer.limit());
    }

    @Test
    public void testByteBufferGet() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("Hello".getBytes());
        buffer.flip();

        byte[] readData = new byte[3];
        buffer.get(readData);

        assertEquals("Hel", new String(readData));
        assertEquals(3, buffer.position());
    }

    @Test
    public void testByteBufferRewind() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("Hello".getBytes());
        buffer.flip();

        buffer.get(); // 读取1个字节
        assertEquals(1, buffer.position());

        buffer.rewind();
        assertEquals(0, buffer.position());
        assertEquals(5, buffer.limit());
    }

    @Test
    public void testByteBufferClear() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("Hello".getBytes());
        buffer.flip();
        buffer.get(new byte[5]); // 读取所有数据

        buffer.clear();

        assertEquals(0, buffer.position());
        assertEquals(10, buffer.limit());
    }

    @Test
    public void testByteBufferCompact() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("ABCDEFGHIJ".getBytes());
        buffer.flip();

        // 读取4个字节
        byte[] read = new byte[4];
        buffer.get(read);
        assertEquals("ABCD", new String(read));

        // compact压缩
        buffer.compact();

        // position应该移动到6 (10-4)
        assertEquals(6, buffer.position());
        assertEquals(10, buffer.limit());
    }

    @Test
    public void testByteBufferMarkReset() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put("ABCDEFGHIJ".getBytes());
        buffer.flip();

        // 读取2个字节
        buffer.get();
        buffer.get();
        assertEquals(2, buffer.position());

        // 设置mark
        buffer.mark();

        // 再读取3个字节
        buffer.get();
        buffer.get();
        buffer.get();
        assertEquals(5, buffer.position());

        // reset回到mark位置
        buffer.reset();
        assertEquals(2, buffer.position());
    }

    @Test
    public void testByteBufferHasRemaining() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put("Hi".getBytes());
        buffer.flip();

        assertTrue(buffer.hasRemaining());

        buffer.get(new byte[2]);
        assertFalse(buffer.hasRemaining());
    }

    @Test
    public void testCharBuffer() {
        CharBuffer charBuffer = CharBuffer.allocate(10);
        charBuffer.put('H');
        charBuffer.put('e');
        charBuffer.put('l');
        charBuffer.put('l');
        charBuffer.put('o');
        charBuffer.flip();

        assertEquals("Hello", charBuffer.toString());
    }

    @Test
    public void testIntBuffer() {
        IntBuffer intBuffer = IntBuffer.allocate(5);
        intBuffer.put(10);
        intBuffer.put(20);
        intBuffer.put(30);
        intBuffer.flip();

        assertEquals(10, intBuffer.get());
        assertEquals(20, intBuffer.get());
        assertEquals(30, intBuffer.get());
    }

    @Test
    public void testDoubleBuffer() {
        DoubleBuffer doubleBuffer = DoubleBuffer.allocate(3);
        doubleBuffer.put(3.14);
        doubleBuffer.put(2.718);
        doubleBuffer.flip();

        assertEquals(3.14, doubleBuffer.get(), 0.001);
        assertEquals(2.718, doubleBuffer.get(), 0.001);
    }

    @Test
    public void testViewBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(16);
        IntBuffer view = bb.asIntBuffer();
        view.put(100);
        view.put(200);

        assertEquals(0, bb.position()); // ByteBuffer position不变
        assertEquals(2, view.position()); // IntBuffer position移动
    }

    @Test
    public void testDemonstrateBufferStates() {
        assertDoesNotThrow(() -> BufferStateManager.demonstrateBufferStates());
    }

    @Test
    public void testDemonstrateCompact() {
        assertDoesNotThrow(() -> BufferStateManager.demonstrateCompact());
    }

    @Test
    public void testDemonstrateMarkReset() {
        assertDoesNotThrow(() -> BufferStateManager.demonstrateMarkReset());
    }

    @Test
    public void testDemonstrateDifferentBuffers() {
        assertDoesNotThrow(() -> BufferStateManager.demonstrateDifferentBuffers());
    }

    @Test
    public void testDemonstrate() {
        assertDoesNotThrow(() -> BufferStateManager.demonstrate());
    }

    @Test
    public void testMain() {
        assertDoesNotThrow(() -> BufferStateManager.main(new String[]{}));
    }
}
