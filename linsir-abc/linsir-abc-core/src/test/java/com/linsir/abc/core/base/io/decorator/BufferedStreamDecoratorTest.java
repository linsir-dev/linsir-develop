package com.linsir.abc.core.base.io.decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

/**
 * BufferedStreamDecorator测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class BufferedStreamDecoratorTest {

    @Test
    public void testSimpleBufferedInputStreamRead() throws IOException {
        byte[] data = "Hello, World!".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        int firstByte = sbis.read();
        assertEquals('H', firstByte);

        sbis.close();
    }

    @Test
    public void testSimpleBufferedInputStreamReadArray() throws IOException {
        byte[] data = "Hello, World!".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        byte[] buffer = new byte[5];
        int bytesRead = sbis.read(buffer, 0, 5);

        assertEquals(5, bytesRead);
        assertArrayEquals("Hello".getBytes(), buffer);

        sbis.close();
    }

    @Test
    public void testSimpleBufferedInputStreamSkip() throws IOException {
        byte[] data = "Hello, World!".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        long skipped = sbis.skip(7);
        assertEquals(7, skipped);

        int nextByte = sbis.read();
        assertEquals('W', nextByte);

        sbis.close();
    }

    @Test
    public void testSimpleBufferedInputStreamAvailable() throws IOException {
        byte[] data = "Hello".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        int available = sbis.available();
        assertTrue(available >= 5);

        sbis.close();
    }

    @Test
    public void testSimpleBufferedInputStreamMarkSupported() {
        ByteArrayInputStream bais = new ByteArrayInputStream("test".getBytes());
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        assertTrue(sbis.markSupported());
    }

    @Test
    public void testSimpleBufferedInputStreamMarkReset() throws IOException {
        byte[] data = "Hello, World! This is a test.".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais, 16);

        // 读取前10个字节
        byte[] buf1 = new byte[10];
        sbis.read(buf1);
        assertArrayEquals("Hello, Wor".getBytes(), buf1);

        // 标记当前位置
        sbis.mark(20);

        // 继续读取10个字节
        byte[] buf2 = new byte[10];
        sbis.read(buf2);
        assertArrayEquals("ld! This i".getBytes(), buf2);

        // 重置到标记位置
        sbis.reset();

        // 重新读取
        byte[] buf3 = new byte[10];
        sbis.read(buf3);
        assertArrayEquals("ld! This i".getBytes(), buf3);

        sbis.close();
    }

    @Test
    public void testSimpleBufferedInputStreamResetWithoutMark() {
        ByteArrayInputStream bais = new ByteArrayInputStream("test".getBytes());
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        assertThrows(IOException.class, () -> sbis.reset());
    }

    @Test
    public void testSimpleBufferedOutputStreamWrite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedStreamDecorator.SimpleBufferedOutputStream sbos =
            new BufferedStreamDecorator.SimpleBufferedOutputStream(baos);

        sbos.write('H');
        sbos.write('i');
        sbos.flush();

        assertArrayEquals("Hi".getBytes(), baos.toByteArray());

        sbos.close();
    }

    @Test
    public void testSimpleBufferedOutputStreamWriteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedStreamDecorator.SimpleBufferedOutputStream sbos =
            new BufferedStreamDecorator.SimpleBufferedOutputStream(baos);

        sbos.write("Hello, World!".getBytes(), 0, 13);
        sbos.flush();

        assertArrayEquals("Hello, World!".getBytes(), baos.toByteArray());

        sbos.close();
    }

    @Test
    public void testSimpleBufferedOutputStreamLargeWrite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedStreamDecorator.SimpleBufferedOutputStream sbos =
            new BufferedStreamDecorator.SimpleBufferedOutputStream(baos, 8); // 小缓冲区

        // 写入超过缓冲区大小的数据
        byte[] largeData = new byte[100];
        for (int i = 0; i < 100; i++) {
            largeData[i] = (byte) (i % 256);
        }

        sbos.write(largeData, 0, 100);
        sbos.flush();

        assertArrayEquals(largeData, baos.toByteArray());

        sbos.close();
    }

    @Test
    public void testSimpleBufferedInputStreamReadEmpty() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(new byte[0]);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        int result = sbis.read();
        assertEquals(-1, result);

        sbis.close();
    }

    @Test
    public void testSimpleBufferedInputStreamReadZeroLength() throws IOException {
        byte[] data = "Hello".getBytes();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        BufferedStreamDecorator.SimpleBufferedInputStream sbis =
            new BufferedStreamDecorator.SimpleBufferedInputStream(bais);

        byte[] buffer = new byte[5];
        int bytesRead = sbis.read(buffer, 0, 0);

        assertEquals(0, bytesRead);

        sbis.close();
    }

    @Test
    public void testPerformanceComparison() {
        // 测试性能比较方法不抛出异常
        assertDoesNotThrow(() -> BufferedStreamDecorator.performanceComparison());
    }

    @Test
    public void testDemonstrateMarkReset() {
        // 测试mark/reset演示方法不抛出异常
        assertDoesNotThrow(() -> BufferedStreamDecorator.demonstrateMarkReset());
    }

    @Test
    public void testDemonstrate() {
        // 测试演示方法不抛出异常
        assertDoesNotThrow(() -> BufferedStreamDecorator.demonstrate());
    }

    @Test
    public void testMain() {
        // 测试main方法不抛出异常
        assertDoesNotThrow(() -> BufferedStreamDecorator.main(new String[]{}));
    }
}
