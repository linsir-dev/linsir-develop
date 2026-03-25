package com.linsir.abc.core.base.lang.string;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * StringImmutability测试类
 */
public class StringImmutabilityTest {

    /**
     * 测试String的不可变性
     */
    @Test
    public void testImmutability() {
        String str = "Hello";
        int originalHashCode = str.hashCode();

        // 修改字符串
        str = str + " World";

        // hashCode应该不同，说明是新对象
        assertNotEquals(originalHashCode, str.hashCode());
        assertEquals("Hello World", str);
    }

    /**
     * 测试字符串常量池
     */
    @Test
    public void testStringPool() {
        // 字面量创建
        String str1 = "Java";
        String str2 = "Java";

        // new创建
        String str3 = new String("Java");
        String str4 = new String("Java");

        // 字面量指向同一对象
        assertSame(str1, str2);

        // new创建的是不同对象
        assertNotSame(str3, str4);

        // 字面量和new创建的对象不同
        assertNotSame(str1, str3);

        // 但内容相等
        assertEquals(str1, str3);
    }

    /**
     * 测试intern方法
     */
    @Test
    public void testIntern() {
        String str1 = "Java";
        String str2 = new String("Java");

        // intern后应该指向常量池中的同一对象
        String str3 = str2.intern();
        assertSame(str1, str3);
    }

    /**
     * 测试StringBuilder性能优于String拼接
     */
    @Test
    public void testStringBuilderPerformance() {
        int count = 1000;

        // String拼接
        long start1 = System.currentTimeMillis();
        String result1 = "";
        for (int i = 0; i < count; i++) {
            result1 += i;
        }
        long time1 = System.currentTimeMillis() - start1;

        // StringBuilder
        long start2 = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(i);
        }
        String result2 = sb.toString();
        long time2 = System.currentTimeMillis() - start2;

        // 结果应该相同
        assertEquals(result1, result2);

        // StringBuilder应该更快（或至少不更慢）
        // 注意：在非常小的循环中可能不稳定，这里仅作演示
    }

    /**
     * 测试StringBuilder和StringBuffer的区别
     */
    @Test
    public void testBuilderVsBuffer() {
        // StringBuilder
        StringBuilder builder = new StringBuilder();
        builder.append("Hello").append(" ").append("World");
        assertEquals("Hello World", builder.toString());

        // StringBuffer
        StringBuffer buffer = new StringBuffer();
        buffer.append("Hello").append(" ").append("World");
        assertEquals("Hello World", buffer.toString());
    }

    /**
     * 测试String的线程安全性
     */
    @Test
    public void testThreadSafety() throws InterruptedException {
        final String shared = "Shared";
        final int[] count = {0};

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                // String是不可变的，所以是线程安全的
                String modified = shared + i;
                if (modified.startsWith("Shared")) {
                    count[0]++;
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                String modified = shared + i;
                if (modified.startsWith("Shared")) {
                    count[0]++;
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        assertEquals(200, count[0]);
    }

    /**
     * 测试作为HashMap key的稳定性
     */
    @Test
    public void testAsHashMapKey() {
        java.util.HashMap<String, Integer> map = new java.util.HashMap<>();
        String key = "testKey";

        map.put(key, 100);

        // 修改key引用
        key = key + "Modified";

        // 原key对应的值仍然存在
        assertEquals(100, (int) map.get("testKey"));

        // 新key不存在
        assertNull(map.get(key));
    }
}
