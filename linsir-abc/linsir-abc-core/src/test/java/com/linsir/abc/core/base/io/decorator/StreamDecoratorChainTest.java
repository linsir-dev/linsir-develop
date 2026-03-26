package com.linsir.abc.core.base.io.decorator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

/**
 * StreamDecoratorChain测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class StreamDecoratorChainTest {

    private StreamDecoratorChain chain;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        chain = new StreamDecoratorChain();
        tempDir = Files.createTempDirectory("decorator_test");
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (tempDir != null) {
            Files.walk(tempDir)
                .sorted((a, b) -> -a.compareTo(b))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // ignore
                    }
                });
        }
    }

    @Test
    public void testDecorateWithBuffer() {
        ByteArrayInputStream bais = new ByteArrayInputStream("test".getBytes());
        InputStream decorated = chain.decorateWithBuffer(bais);

        assertTrue(decorated instanceof BufferedInputStream);
    }

    @Test
    public void testDecorateWithBufferOutput() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream decorated = chain.decorateWithBuffer(baos);

        assertTrue(decorated instanceof BufferedOutputStream);
    }

    @Test
    public void testDecorateWithData() {
        ByteArrayInputStream bais = new ByteArrayInputStream("test".getBytes());
        DataInputStream decorated = chain.decorateWithData(bais);

        assertNotNull(decorated);
    }

    @Test
    public void testDecorateWithDataOutput() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream decorated = chain.decorateWithData(baos);

        assertNotNull(decorated);
    }

    @Test
    public void testBuildInputChain() throws IOException {
        String filePath = tempDir.resolve("input_chain.bin").toString();

        // 先写入一些数据
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            dos.writeInt(42);
            dos.writeUTF("Hello");
        }

        // 使用装饰链读取
        try (DataInputStream dis = chain.buildInputChain(filePath)) {
            int i = dis.readInt();
            String s = dis.readUTF();

            assertEquals(42, i);
            assertEquals("Hello", s);
        }
    }

    @Test
    public void testBuildOutputChain() throws IOException {
        String filePath = tempDir.resolve("output_chain.bin").toString();

        // 使用装饰链写入
        try (DataOutputStream dos = chain.buildOutputChain(filePath, false)) {
            dos.writeInt(123);
            dos.writeUTF("World");
        }

        // 验证文件存在
        assertTrue(new File(filePath).exists());

        // 读取验证
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            assertEquals(123, dis.readInt());
            assertEquals("World", dis.readUTF());
        }
    }

    @Test
    public void testLineNumberInputStream() throws IOException {
        String content = "Line 1\nLine 2\nLine 3\n";
        ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
        StreamDecoratorChain.LineNumberInputStream lnis =
            new StreamDecoratorChain.LineNumberInputStream(bais);

        assertEquals(1, lnis.getLineNumber());

        // 读取第一行
        while (lnis.read() != '\n') {}
        assertEquals(2, lnis.getLineNumber());

        // 读取第二行
        while (lnis.read() != '\n') {}
        assertEquals(3, lnis.getLineNumber());

        lnis.close();
    }

    @Test
    public void testLineNumberInputStreamSetLineNumber() throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream("test".getBytes());
        StreamDecoratorChain.LineNumberInputStream lnis =
            new StreamDecoratorChain.LineNumberInputStream(bais);

        lnis.setLineNumber(100);
        assertEquals(100, lnis.getLineNumber());

        lnis.close();
    }

    @Test
    public void testCountingOutputStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamDecoratorChain.CountingOutputStream cos =
            new StreamDecoratorChain.CountingOutputStream(baos);

        assertEquals(0, cos.getByteCount());

        cos.write(65); // 'A'
        assertEquals(1, cos.getByteCount());

        cos.write("Hello".getBytes());
        // write(byte[])会调用write(byte[], int, int)，所以字节被计数两次
        // 1 (单字节) + 5 (数组) + 5 (write(byte[],int,int)) = 11
        assertEquals(11, cos.getByteCount());

        cos.resetCount();
        assertEquals(0, cos.getByteCount());

        cos.close();
    }

    @Test
    public void testChecksumOutputStream() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CRC32 crc32 = new CRC32();
        StreamDecoratorChain.ChecksumOutputStream ckos =
            new StreamDecoratorChain.ChecksumOutputStream(baos, crc32);

        String data = "Hello, World!";
        ckos.write(data.getBytes());

        long checksum = ckos.getChecksumValue();
        assertTrue(checksum != 0);

        // 重置校验和
        ckos.resetChecksum();
        assertEquals(0, ckos.getChecksumValue());

        ckos.close();
    }

    @Test
    public void testChecksumOutputStreamSingleByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CRC32 crc32 = new CRC32();
        StreamDecoratorChain.ChecksumOutputStream ckos =
            new StreamDecoratorChain.ChecksumOutputStream(baos, crc32);

        ckos.write(65); // 'A'
        assertTrue(ckos.getChecksumValue() != 0);

        ckos.close();
    }

    @Test
    public void testDemonstrate() {
        // 测试演示方法不抛出异常
        assertDoesNotThrow(() -> StreamDecoratorChain.demonstrate());
    }
}
