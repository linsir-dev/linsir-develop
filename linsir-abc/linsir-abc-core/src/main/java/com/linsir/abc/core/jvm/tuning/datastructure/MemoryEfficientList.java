package com.linsir.abc.core.jvm.tuning.datastructure;

import java.util.*;

/**
 * 内存高效列表
 * 演示如何优化ArrayList的使用，减少内存占用
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024/1/1
 */
public class MemoryEfficientList {

    /**
     * 默认加载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 错误示例：未预估容量导致频繁扩容
     *
     * @param data 数据
     * @return 列表
     */
    public static <T> List<T> wrongWay(List<T> data) {
        // 错误：使用默认容量（10），频繁扩容导致内存碎片
        List<T> result = new ArrayList<>();
        for (T item : data) {
            result.add(item);
        }
        return result;
    }

    /**
     * 正确示例：预估容量避免扩容
     *
     * @param data 数据
     * @return 列表
     */
    public static <T> List<T> correctWayWithCapacity(List<T> data) {
        // 正确：预先指定容量，避免扩容
        List<T> result = new ArrayList<>(data.size());
        for (T item : data) {
            result.add(item);
        }
        return result;
    }

    /**
     * 正确示例：使用紧凑的数据结构存储原始类型
     * 使用int数组替代ArrayList<Integer>
     *
     * @param data int数据
     * @return IntArrayList
     */
    public static IntArrayList efficientIntList(int[] data) {
        IntArrayList list = new IntArrayList(data.length);
        for (int value : data) {
            list.add(value);
        }
        return list;
    }

    /**
     * 正确示例：使用紧凑的数据结构存储原始类型
     * 使用long数组替代ArrayList<Long>
     *
     * @param data long数据
     * @return LongArrayList
     */
    public static LongArrayList efficientLongList(long[] data) {
        LongArrayList list = new LongArrayList(data.length);
        for (long value : data) {
            list.add(value);
        }
        return list;
    }

    /**
     * 字符串去重
     * 使用String.intern()减少重复字符串的内存占用
     *
     * @param strings 字符串列表
     * @return 去重后的字符串列表
     */
    public static List<String> deduplicateStrings(List<String> strings) {
        // 使用Set来去重
        Set<String> unique = new HashSet<>(strings.size());
        List<String> result = new ArrayList<>(strings.size());

        for (String str : strings) {
            // intern()会将字符串放入常量池，相同内容的字符串共享同一对象
            String interned = str.intern();
            if (unique.add(interned)) {
                result.add(interned);
            }
        }

        return result;
    }

    /**
     * 流式处理大数据
     * 避免将所有数据加载到内存
     *
     * @param data  数据源
     * @param batch 批处理大小
     */
    public static void processInBatches(List<String> data, int batch, BatchProcessor processor) {
        int size = data.size();
        for (int i = 0; i < size; i += batch) {
            int end = Math.min(i + batch, size);
            List<String> batchData = data.subList(i, end);
            processor.process(batchData);
        }
    }

    /**
     * 批处理器接口
     */
    @FunctionalInterface
    public interface BatchProcessor {
        void process(List<String> batch);
    }

    /**
     * 原始类型int列表
     * 比ArrayList<Integer>更节省内存
     */
    public static class IntArrayList {
        private int[] data;
        private int size;

        public IntArrayList() {
            this(10);
        }

        public IntArrayList(int initialCapacity) {
            this.data = new int[initialCapacity];
            this.size = 0;
        }

        public void add(int value) {
            ensureCapacity(size + 1);
            data[size++] = value;
        }

        public int get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
            return data[index];
        }

        public int size() {
            return size;
        }

        private void ensureCapacity(int minCapacity) {
            if (minCapacity > data.length) {
                int newCapacity = Math.max(data.length * 2, minCapacity);
                data = Arrays.copyOf(data, newCapacity);
            }
        }

        public int[] toArray() {
            return Arrays.copyOf(data, size);
        }
    }

    /**
     * 原始类型long列表
     * 比ArrayList<Long>更节省内存
     */
    public static class LongArrayList {
        private long[] data;
        private int size;

        public LongArrayList() {
            this(10);
        }

        public LongArrayList(int initialCapacity) {
            this.data = new long[initialCapacity];
            this.size = 0;
        }

        public void add(long value) {
            ensureCapacity(size + 1);
            data[size++] = value;
        }

        public long get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }
            return data[index];
        }

        public int size() {
            return size;
        }

        private void ensureCapacity(int minCapacity) {
            if (minCapacity > data.length) {
                int newCapacity = Math.max(data.length * 2, minCapacity);
                data = Arrays.copyOf(data, newCapacity);
            }
        }

        public long[] toArray() {
            return Arrays.copyOf(data, size);
        }
    }

    /**
     * 计算内存占用对比
     *
     * @param elementCount 元素数量
     * @return 内存占用对比报告
     */
    public static String getMemoryComparisonReport(int elementCount) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== 内存占用对比（元素数量：").append(elementCount).append("）===\n\n");

        // ArrayList<Integer> 内存占用
        // 每个Integer对象：对象头(12字节) + int值(4字节) + 对齐(4字节) = 20字节
        // ArrayList本身：对象头(12字节) + 引用数组(约8字节/引用) + 其他字段
        long arrayListMemory = elementCount * (20 + 4L) + 24; // 约24字节的对象头
        sb.append("ArrayList<Integer>: ").append(arrayListMemory / 1024).append("KB\n");

        // int[] 内存占用
        // 数组对象头(12字节) + length字段(4字节) + int数据(4字节/元素)
        long intArrayMemory = 16L + elementCount * 4L;
        sb.append("int[]: ").append(intArrayMemory / 1024).append("KB\n");

        // IntArrayList 内存占用
        long intArrayListMemory = 16L + elementCount * 4L + 4; // +4 for size字段
        sb.append("IntArrayList: ").append(intArrayListMemory / 1024).append("KB\n");

        sb.append("\n节省内存: ")
                .append(String.format("%.1f%%", (1 - (double) intArrayListMemory / arrayListMemory) * 100))
                .append("\n");

        return sb.toString();
    }
}
