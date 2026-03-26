package com.linsir.abc.core.base.io.decorator;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;

/**
 * DataStreamDecorator测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class DataStreamDecoratorTest {

    @Test
    public void testSimpleDataOutputStreamWriteBoolean() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeBoolean(true);
        sdos.writeBoolean(false);
        sdos.flush();

        byte[] result = baos.toByteArray();
        assertEquals(1, result[0]);
        assertEquals(0, result[1]);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteByte() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeByte(42);
        sdos.flush();

        assertEquals(42, baos.toByteArray()[0]);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteShort() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeShort(0x1234);
        sdos.flush();

        byte[] result = baos.toByteArray();
        assertEquals(0x12, result[0] & 0xFF);
        assertEquals(0x34, result[1] & 0xFF);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteInt() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeInt(0x12345678);
        sdos.flush();

        byte[] result = baos.toByteArray();
        assertEquals(0x12, result[0] & 0xFF);
        assertEquals(0x34, result[1] & 0xFF);
        assertEquals(0x56, result[2] & 0xFF);
        assertEquals(0x78, result[3] & 0xFF);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteLong() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeLong(0x123456789ABCDEF0L);
        sdos.flush();

        byte[] result = baos.toByteArray();
        assertEquals(8, result.length);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteFloat() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeFloat(3.14f);
        sdos.flush();

        assertEquals(4, baos.toByteArray().length);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteDouble() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeDouble(3.14159);
        sdos.flush();

        assertEquals(8, baos.toByteArray().length);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteChar() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeChar('A');
        sdos.flush();

        byte[] result = baos.toByteArray();
        assertEquals(0, result[0]);
        assertEquals('A', result[1]);

        sdos.close();
    }

    @Test
    public void testSimpleDataOutputStreamWriteUTF() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataStreamDecorator.SimpleDataOutputStream sdos =
            new DataStreamDecorator.SimpleDataOutputStream(baos);

        sdos.writeUTF("Hello");
        sdos.flush();

        byte[] result = baos.toByteArray();
        // 前2字节是长度
        assertEquals(0, result[0]);
        assertEquals(5, result[1]);

        sdos.close();
    }

    @Test
    public void testSimpleDataInputStreamReadBoolean() throws IOException {
        byte[] data = {1, 0};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertTrue(sdis.readBoolean());
        assertFalse(sdis.readBoolean());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadByte() throws IOException {
        byte[] data = {42};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(42, sdis.readByte());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadShort() throws IOException {
        byte[] data = {0x12, 0x34};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals((short) 0x1234, sdis.readShort());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadInt() throws IOException {
        byte[] data = {0x12, 0x34, 0x56, 0x78};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(0x12345678, sdis.readInt());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadLong() throws IOException {
        byte[] data = {0x12, 0x34, 0x56, 0x78, (byte) 0x9A, (byte) 0xBC, (byte) 0xDE, (byte) 0xF0};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(0x123456789ABCDEF0L, sdis.readLong());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadFloat() throws IOException {
        float original = 3.14f;
        int bits = Float.floatToIntBits(original);
        byte[] data = {
            (byte) (bits >> 24),
            (byte) (bits >> 16),
            (byte) (bits >> 8),
            (byte) bits
        };

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(original, sdis.readFloat(), 0.0001);

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadDouble() throws IOException {
        double original = 3.14159;
        long bits = Double.doubleToLongBits(original);
        byte[] data = new byte[8];
        for (int i = 0; i < 8; i++) {
            data[i] = (byte) (bits >> (56 - i * 8));
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(original, sdis.readDouble(), 0.0001);

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadChar() throws IOException {
        byte[] data = {0, 'A'};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals('A', sdis.readChar());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadUTF() throws IOException {
        String original = "Hello";
        byte[] utfBytes = original.getBytes("UTF-8");
        byte[] data = new byte[2 + utfBytes.length];
        data[0] = 0;
        data[1] = (byte) utfBytes.length;
        System.arraycopy(utfBytes, 0, data, 2, utfBytes.length);

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(original, sdis.readUTF());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadFully() throws IOException {
        byte[] data = {1, 2, 3, 4, 5};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        byte[] buffer = new byte[5];
        sdis.readFully(buffer);

        assertArrayEquals(data, buffer);

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadFullyPartial() throws IOException {
        byte[] data = {1, 2, 3, 4, 5};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        byte[] buffer = new byte[3];
        sdis.readFully(buffer, 0, 3);

        assertEquals(1, buffer[0]);
        assertEquals(2, buffer[1]);
        assertEquals(3, buffer[2]);

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadFullyEOF() {
        byte[] data = {1, 2};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        byte[] buffer = new byte[5];
        assertThrows(EOFException.class, () -> sdis.readFully(buffer));
    }

    @Test
    public void testSimpleDataInputStreamSkipBytes() throws IOException {
        byte[] data = {1, 2, 3, 4, 5};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        int skipped = sdis.skipBytes(3);
        assertEquals(3, skipped);
        assertEquals(4, sdis.readByte());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadUnsignedByte() throws IOException {
        byte[] data = {(byte) 0xFF};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(255, sdis.readUnsignedByte());

        sdis.close();
    }

    @Test
    public void testSimpleDataInputStreamReadUnsignedShort() throws IOException {
        byte[] data = {(byte) 0xFF, (byte) 0xFF};
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataStreamDecorator.SimpleDataInputStream sdis =
            new DataStreamDecorator.SimpleDataInputStream(bais);

        assertEquals(65535, sdis.readUnsignedShort());

        sdis.close();
    }

    @Test
    public void testByteOrderConverterShort() {
        short original = (short) 0x1234;
        short converted = DataStreamDecorator.ByteOrderConverter.bigToLittleEndian(original);
        short back = DataStreamDecorator.ByteOrderConverter.bigToLittleEndian(converted);

        assertEquals(original, back);
        assertEquals((short) 0x3412, converted);
    }

    @Test
    public void testByteOrderConverterInt() {
        int original = 0x12345678;
        int converted = DataStreamDecorator.ByteOrderConverter.bigToLittleEndian(original);
        int back = DataStreamDecorator.ByteOrderConverter.bigToLittleEndian(converted);

        assertEquals(original, back);
        assertEquals(0x78563412, converted);
    }

    @Test
    public void testByteOrderConverterLong() {
        long original = 0x123456789ABCDEF0L;
        long converted = DataStreamDecorator.ByteOrderConverter.bigToLittleEndian(original);
        long back = DataStreamDecorator.ByteOrderConverter.bigToLittleEndian(converted);

        assertEquals(original, back);
        assertEquals(0xF0DEBC9A78563412L, converted);
    }

    @Test
    public void testDemonstrate() {
        // 测试演示方法不抛出异常
        assertDoesNotThrow(() -> DataStreamDecorator.demonstrate());
    }

    @Test
    public void testMain() {
        // 测试main方法不抛出异常
        assertDoesNotThrow(() -> DataStreamDecorator.main(new String[]{}));
    }
}
