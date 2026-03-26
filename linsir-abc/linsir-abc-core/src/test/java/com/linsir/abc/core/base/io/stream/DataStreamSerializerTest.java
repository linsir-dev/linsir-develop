package com.linsir.abc.core.base.io.stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * DataStreamSerializer测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataStreamSerializerTest {

    private DataStreamSerializer serializer;
    private Path tempDir;

    @BeforeEach
    public void setUp() throws IOException {
        serializer = new DataStreamSerializer();
        tempDir = Files.createTempDirectory("data_stream_test");
    }

    @AfterEach
    public void tearDown() throws IOException {
        // 清理临时目录
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
    public void testWriteAndReadData() throws IOException {
        String filePath = tempDir.resolve("data.bin").toString();

        DataStreamSerializer.DataObject original = new DataStreamSerializer.DataObject(
            true,
            (byte) 42,
            (short) 1000,
            123456,
            9876543210L,
            3.14f,
            2.718281828,
            'A',
            "Hello, DataStream!",
            new byte[]{0x01, 0x02, 0x03, 0x04, 0x05}
        );

        // 写入数据
        serializer.writeData(filePath, original);

        // 验证文件存在
        assertTrue(new File(filePath).exists());

        // 读取数据
        DataStreamSerializer.DataObject restored = serializer.readData(filePath);

        // 验证数据一致性
        assertEquals(original.isFlag(), restored.isFlag());
        assertEquals(original.getByteValue(), restored.getByteValue());
        assertEquals(original.getShortValue(), restored.getShortValue());
        assertEquals(original.getIntValue(), restored.getIntValue());
        assertEquals(original.getLongValue(), restored.getLongValue());
        assertEquals(original.getFloatValue(), restored.getFloatValue(), 0.0001);
        assertEquals(original.getDoubleValue(), restored.getDoubleValue(), 0.0001);
        assertEquals(original.getCharValue(), restored.getCharValue());
        assertEquals(original.getStringValue(), restored.getStringValue());
        assertArrayEquals(original.getByteArray(), restored.getByteArray());
    }

    @Test
    public void testWriteAndReadIntArray() throws IOException {
        String filePath = tempDir.resolve("int_array.bin").toString();

        int[] original = {1, 2, 3, 4, 5, 10, 20, 30, 40, 50};

        // 写入数组
        serializer.writeIntArray(filePath, original);

        // 读取数组
        int[] restored = serializer.readIntArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testWriteAndReadEmptyIntArray() throws IOException {
        String filePath = tempDir.resolve("empty_int_array.bin").toString();

        int[] original = {};

        // 写入空数组
        serializer.writeIntArray(filePath, original);

        // 读取数组
        int[] restored = serializer.readIntArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testWriteAndReadStringArray() throws IOException {
        String filePath = tempDir.resolve("string_array.bin").toString();

        String[] original = {"Java", "Python", "C++", "JavaScript", "Go"};

        // 写入数组
        serializer.writeStringArray(filePath, original);

        // 读取数组
        String[] restored = serializer.readStringArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testWriteAndReadEmptyStringArray() throws IOException {
        String filePath = tempDir.resolve("empty_string_array.bin").toString();

        String[] original = {};

        // 写入空数组
        serializer.writeStringArray(filePath, original);

        // 读取数组
        String[] restored = serializer.readStringArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testWriteAndReadStringArrayWithSpecialChars() throws IOException {
        String filePath = tempDir.resolve("special_string_array.bin").toString();

        String[] original = {
            "Hello, World!",
            "你好，世界！",
            "Special chars: @#$%^&*()",
            "Unicode: 🎉🎊🎁",
            ""
        };

        // 写入数组
        serializer.writeStringArray(filePath, original);

        // 读取数组
        String[] restored = serializer.readStringArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testDataObjectWithNullString() throws IOException {
        String filePath = tempDir.resolve("null_string.bin").toString();

        DataStreamSerializer.DataObject original = new DataStreamSerializer.DataObject();
        original.setFlag(false);
        original.setByteValue((byte) 0);
        original.setShortValue((short) 0);
        original.setIntValue(0);
        original.setLongValue(0L);
        original.setFloatValue(0.0f);
        original.setDoubleValue(0.0);
        original.setCharValue('\0');
        original.setStringValue("");
        original.setByteArray(new byte[0]);

        // 写入数据
        serializer.writeData(filePath, original);

        // 读取数据
        DataStreamSerializer.DataObject restored = serializer.readData(filePath);

        // 验证
        assertEquals(original.isFlag(), restored.isFlag());
        assertEquals(original.getByteValue(), restored.getByteValue());
    }

    @Test
    public void testDataObjectToString() {
        DataStreamSerializer.DataObject data = new DataStreamSerializer.DataObject(
            true,
            (byte) 42,
            (short) 1000,
            123456,
            9876543210L,
            3.14f,
            2.718281828,
            'A',
            "Test",
            new byte[]{0x01, 0x02, 0x03}
        );

        String str = data.toString();

        assertTrue(str.contains("flag=true"));
        assertTrue(str.contains("byteValue=42"));
        assertTrue(str.contains("stringValue='Test'"));
        assertTrue(str.contains("byteArrayLength=3"));
    }

    @Test
    public void testDataObjectEmptyToString() {
        DataStreamSerializer.DataObject data = new DataStreamSerializer.DataObject();

        String str = data.toString();

        assertTrue(str.contains("flag=false"));
        assertTrue(str.contains("byteArrayLength=0"));
    }

    @Test
    public void testLargeIntArray() throws IOException {
        String filePath = tempDir.resolve("large_int_array.bin").toString();

        int[] original = new int[10000];
        for (int i = 0; i < original.length; i++) {
            original[i] = i * i;
        }

        // 写入数组
        serializer.writeIntArray(filePath, original);

        // 读取数组
        int[] restored = serializer.readIntArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testLargeStringArray() throws IOException {
        String filePath = tempDir.resolve("large_string_array.bin").toString();

        String[] original = new String[1000];
        for (int i = 0; i < original.length; i++) {
            original[i] = "String_" + i;
        }

        // 写入数组
        serializer.writeStringArray(filePath, original);

        // 读取数组
        String[] restored = serializer.readStringArray(filePath);

        // 验证
        assertArrayEquals(original, restored);
    }

    @Test
    public void testNegativeValues() throws IOException {
        String filePath = tempDir.resolve("negative_values.bin").toString();

        DataStreamSerializer.DataObject original = new DataStreamSerializer.DataObject(
            false,
            (byte) -128,
            (short) -32768,
            -2147483648,
            -9223372036854775808L,
            -3.14f,
            -2.718281828,
            'Z',
            "Negative",
            new byte[]{(byte) 0xFF, (byte) 0xFE}
        );

        // 写入数据
        serializer.writeData(filePath, original);

        // 读取数据
        DataStreamSerializer.DataObject restored = serializer.readData(filePath);

        // 验证
        assertEquals(original.getByteValue(), restored.getByteValue());
        assertEquals(original.getShortValue(), restored.getShortValue());
        assertEquals(original.getIntValue(), restored.getIntValue());
        assertEquals(original.getLongValue(), restored.getLongValue());
        assertEquals(original.getFloatValue(), restored.getFloatValue(), 0.0001);
        assertEquals(original.getDoubleValue(), restored.getDoubleValue(), 0.0001);
    }

    @Test
    public void testDemonstrate() {
        // 测试演示方法不抛出异常
        assertDoesNotThrow(() -> DataStreamSerializer.demonstrate());
    }
}