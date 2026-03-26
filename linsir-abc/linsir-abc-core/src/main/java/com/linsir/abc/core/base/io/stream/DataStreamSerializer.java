package com.linsir.abc.core.base.io.stream;

import java.io.*;

/**
 * 数据流序列化器
 * 演示DataInputStream/DataOutputStream的使用，支持基本类型的读写
 *
 * 设计要点：
 * 1. DataInputStream/DataOutputStream提供基本类型的便捷读写
 * 2. 注意字节顺序（Java使用大端序）
 * 3. 字符串的UTF-8编码读写
 *
 * @author linsir
 * @version 1.0.0
 * @since 2026-03-26
 */
public class DataStreamSerializer {

    /**
     * 写入基本类型数据
     *
     * @param filePath 文件路径
     * @param data 数据对象
     * @throws IOException 当IO操作失败时
     */
    public void writeData(String filePath, DataObject data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(bos)) {

            // 写入基本类型
            dos.writeBoolean(data.isFlag());
            dos.writeByte(data.getByteValue());
            dos.writeShort(data.getShortValue());
            dos.writeInt(data.getIntValue());
            dos.writeLong(data.getLongValue());
            dos.writeFloat(data.getFloatValue());
            dos.writeDouble(data.getDoubleValue());
            dos.writeChar(data.getCharValue());

            // 写入字符串（UTF-8编码）
            dos.writeUTF(data.getStringValue());

            // 写入字节数组
            byte[] bytes = data.getByteArray();
            dos.writeInt(bytes.length);
            dos.write(bytes);

            dos.flush();
        }
    }

    /**
     * 读取基本类型数据
     *
     * @param filePath 文件路径
     * @return 数据对象
     * @throws IOException 当IO操作失败时
     */
    public DataObject readData(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             DataInputStream dis = new DataInputStream(bis)) {

            DataObject data = new DataObject();

            // 按写入顺序读取
            data.setFlag(dis.readBoolean());
            data.setByteValue(dis.readByte());
            data.setShortValue(dis.readShort());
            data.setIntValue(dis.readInt());
            data.setLongValue(dis.readLong());
            data.setFloatValue(dis.readFloat());
            data.setDoubleValue(dis.readDouble());
            data.setCharValue(dis.readChar());
            data.setStringValue(dis.readUTF());

            // 读取字节数组
            int arrayLength = dis.readInt();
            byte[] bytes = new byte[arrayLength];
            dis.readFully(bytes);
            data.setByteArray(bytes);

            return data;
        }
    }

    /**
     * 写入整数数组
     *
     * @param filePath 文件路径
     * @param array 整数数组
     * @throws IOException 当IO操作失败时
     */
    public void writeIntArray(String filePath, int[] array) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {

            dos.writeInt(array.length);
            for (int value : array) {
                dos.writeInt(value);
            }
            dos.flush();
        }
    }

    /**
     * 读取整数数组
     *
     * @param filePath 文件路径
     * @return 整数数组
     * @throws IOException 当IO操作失败时
     */
    public int[] readIntArray(String filePath) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {

            int length = dis.readInt();
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = dis.readInt();
            }
            return array;
        }
    }

    /**
     * 写入字符串列表
     *
     * @param filePath 文件路径
     * @param strings 字符串数组
     * @throws IOException 当IO操作失败时
     */
    public void writeStringArray(String filePath, String[] strings) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {

            dos.writeInt(strings.length);
            for (String str : strings) {
                dos.writeUTF(str);
            }
            dos.flush();
        }
    }

    /**
     * 读取字符串列表
     *
     * @param filePath 文件路径
     * @return 字符串数组
     * @throws IOException 当IO操作失败时
     */
    public String[] readStringArray(String filePath) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {

            int length = dis.readInt();
            String[] array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = dis.readUTF();
            }
            return array;
        }
    }

    /**
     * 数据对象类
     */
    public static class DataObject {
        private boolean flag;
        private byte byteValue;
        private short shortValue;
        private int intValue;
        private long longValue;
        private float floatValue;
        private double doubleValue;
        private char charValue;
        private String stringValue;
        private byte[] byteArray;

        public DataObject() {
        }

        public DataObject(boolean flag, byte byteValue, short shortValue, int intValue,
                         long longValue, float floatValue, double doubleValue, char charValue,
                         String stringValue, byte[] byteArray) {
            this.flag = flag;
            this.byteValue = byteValue;
            this.shortValue = shortValue;
            this.intValue = intValue;
            this.longValue = longValue;
            this.floatValue = floatValue;
            this.doubleValue = doubleValue;
            this.charValue = charValue;
            this.stringValue = stringValue;
            this.byteArray = byteArray;
        }

        // Getters and Setters
        public boolean isFlag() { return flag; }
        public void setFlag(boolean flag) { this.flag = flag; }
        public byte getByteValue() { return byteValue; }
        public void setByteValue(byte byteValue) { this.byteValue = byteValue; }
        public short getShortValue() { return shortValue; }
        public void setShortValue(short shortValue) { this.shortValue = shortValue; }
        public int getIntValue() { return intValue; }
        public void setIntValue(int intValue) { this.intValue = intValue; }
        public long getLongValue() { return longValue; }
        public void setLongValue(long longValue) { this.longValue = longValue; }
        public float getFloatValue() { return floatValue; }
        public void setFloatValue(float floatValue) { this.floatValue = floatValue; }
        public double getDoubleValue() { return doubleValue; }
        public void setDoubleValue(double doubleValue) { this.doubleValue = doubleValue; }
        public char getCharValue() { return charValue; }
        public void setCharValue(char charValue) { this.charValue = charValue; }
        public String getStringValue() { return stringValue; }
        public void setStringValue(String stringValue) { this.stringValue = stringValue; }
        public byte[] getByteArray() { return byteArray; }
        public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }

        @Override
        public String toString() {
            return "DataObject{" +
                    "flag=" + flag +
                    ", byteValue=" + byteValue +
                    ", shortValue=" + shortValue +
                    ", intValue=" + intValue +
                    ", longValue=" + longValue +
                    ", floatValue=" + floatValue +
                    ", doubleValue=" + doubleValue +
                    ", charValue=" + charValue +
                    ", stringValue='" + stringValue + '\'' +
                    ", byteArrayLength=" + (byteArray != null ? byteArray.length : 0) +
                    '}';
        }
    }

    /**
     * 演示数据流的使用
     */
    public static void demonstrate() {
        DataStreamSerializer serializer = new DataStreamSerializer();

        try {
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "data_stream_demo");
            tempDir.mkdirs();

            // 测试基本类型数据
            String dataFile = new File(tempDir, "data.bin").getAbsolutePath();

            DataObject original = new DataObject(
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

            System.out.println("原始数据: " + original);

            // 写入数据
            serializer.writeData(dataFile, original);
            System.out.println("数据已写入: " + dataFile);

            // 读取数据
            DataObject restored = serializer.readData(dataFile);
            System.out.println("恢复数据: " + restored);

            // 验证数据一致性
            System.out.println("数据一致: " + original.toString().equals(restored.toString()));

            // 测试整数数组
            String intArrayFile = new File(tempDir, "int_array.bin").getAbsolutePath();
            int[] intArray = {1, 2, 3, 4, 5, 10, 20, 30, 40, 50};
            serializer.writeIntArray(intArrayFile, intArray);
            int[] restoredIntArray = serializer.readIntArray(intArrayFile);
            System.out.println("\n整数数组: " + java.util.Arrays.toString(restoredIntArray));

            // 测试字符串数组
            String stringArrayFile = new File(tempDir, "string_array.bin").getAbsolutePath();
            String[] stringArray = {"Java", "Python", "C++", "JavaScript", "Go"};
            serializer.writeStringArray(stringArrayFile, stringArray);
            String[] restoredStringArray = serializer.readStringArray(stringArrayFile);
            System.out.println("字符串数组: " + java.util.Arrays.toString(restoredStringArray));

            // 清理
            new File(dataFile).delete();
            new File(intArrayFile).delete();
            new File(stringArrayFile).delete();
            tempDir.delete();

        } catch (IOException e) {
            System.err.println("IO错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        demonstrate();
    }
}
