package com.linsir.abc.core.base.lang.object;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * HashCodeGenerator测试类
 *
 * @author linsir
 * @version 1.0.0
 * @since 1.0.0
 */
public class HashCodeGeneratorTest {

    @Test
    public void testGenerateForSingleField() {
        // 测试null值
        assertEquals(0, HashCodeGenerator.generateForSingleField(null));

        // 测试字符串
        String str = "test";
        assertEquals(str.hashCode(), HashCodeGenerator.generateForSingleField(str));

        // 测试整数
        Integer num = 123;
        assertEquals(num.hashCode(), HashCodeGenerator.generateForSingleField(num));
    }

    @Test
    public void testGenerateForMultipleFields() {
        // 测试多字段哈希
        Object[] fields1 = {"name", 25, 100.0};
        Object[] fields2 = {"name", 25, 100.0};
        Object[] fields3 = {"name", 26, 100.0};

        int hash1 = HashCodeGenerator.generateForMultipleFields(fields1);
        int hash2 = HashCodeGenerator.generateForMultipleFields(fields2);
        int hash3 = HashCodeGenerator.generateForMultipleFields(fields3);

        // 相同字段应该产生相同哈希码
        assertEquals(hash1, hash2);

        // 不同字段应该产生不同哈希码（大概率）
        assertNotEquals(hash1, hash3);
    }

    @Test
    public void testGenerateManually() {
        // 测试手动计算哈希
        Object[] fields1 = {"name", 25, 100.0};
        Object[] fields2 = {"name", 25, 100.0};

        int hash1 = HashCodeGenerator.generateManually(fields1);
        int hash2 = HashCodeGenerator.generateManually(fields2);

        // 相同字段应该产生相同哈希码
        assertEquals(hash1, hash2);
    }

    @Test
    public void testGenerateWithArrays() {
        int[] intArray = {1, 2, 3};
        Object[] objectArray = {"a", "b", "c"};
        String otherField = "test";

        int hash1 = HashCodeGenerator.generateWithArrays(intArray, objectArray, otherField);
        int hash2 = HashCodeGenerator.generateWithArrays(intArray, objectArray, otherField);

        // 相同参数应该产生相同哈希码
        assertEquals(hash1, hash2);

        // 修改数组应该产生不同哈希码
        int[] differentIntArray = {1, 2, 4};
        int hash3 = HashCodeGenerator.generateWithArrays(differentIntArray, objectArray, otherField);
        assertNotEquals(hash1, hash3);
    }

    @Test
    public void testHashCodeForBoolean() {
        // true和false应该产生不同的哈希码
        assertNotEquals(
            HashCodeGenerator.hashCodeForBoolean(true),
            HashCodeGenerator.hashCodeForBoolean(false)
        );

        // 相同的布尔值应该产生相同的哈希码
        assertEquals(
            HashCodeGenerator.hashCodeForBoolean(true),
            HashCodeGenerator.hashCodeForBoolean(true)
        );
    }

    @Test
    public void testHashCodeForByte() {
        assertEquals(0, HashCodeGenerator.hashCodeForByte((byte) 0));
        assertEquals(127, HashCodeGenerator.hashCodeForByte((byte) 127));
        assertEquals(-128, HashCodeGenerator.hashCodeForByte((byte) -128));
    }

    @Test
    public void testHashCodeForChar() {
        assertEquals(65, HashCodeGenerator.hashCodeForChar('A'));
        assertEquals(97, HashCodeGenerator.hashCodeForChar('a'));
        assertEquals(48, HashCodeGenerator.hashCodeForChar('0'));
    }

    @Test
    public void testHashCodeForShort() {
        assertEquals(100, HashCodeGenerator.hashCodeForShort((short) 100));
        assertEquals(-100, HashCodeGenerator.hashCodeForShort((short) -100));
    }

    @Test
    public void testHashCodeForInt() {
        assertEquals(0, HashCodeGenerator.hashCodeForInt(0));
        assertEquals(12345, HashCodeGenerator.hashCodeForInt(12345));
        assertEquals(-12345, HashCodeGenerator.hashCodeForInt(-12345));
    }

    @Test
    public void testHashCodeForLong() {
        // 测试长整型哈希码计算
        long value = 0x123456789ABCDEF0L;
        int hash = HashCodeGenerator.hashCodeForLong(value);

        // 高位和低位异或结果
        int expected = (int) (value ^ (value >>> 32));
        assertEquals(expected, hash);
    }

    @Test
    public void testHashCodeForFloat() {
        // 测试浮点型哈希码
        float value = 3.14f;
        int hash = HashCodeGenerator.hashCodeForFloat(value);
        assertEquals(Float.floatToIntBits(value), hash);

        // 相同值应该产生相同哈希码
        assertEquals(
            HashCodeGenerator.hashCodeForFloat(1.5f),
            HashCodeGenerator.hashCodeForFloat(1.5f)
        );
    }

    @Test
    public void testHashCodeForDouble() {
        // 测试双精度浮点型哈希码
        double value = 3.14159;
        int hash = HashCodeGenerator.hashCodeForDouble(value);

        long bits = Double.doubleToLongBits(value);
        int expected = (int) (bits ^ (bits >>> 32));
        assertEquals(expected, hash);
    }

    @Test
    public void testPersonHashCode() {
        // 测试Person类的哈希码生成
        String[] hobbies = {"reading", "swimming"};
        HashCodeGenerator.Person person1 = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies);
        HashCodeGenerator.Person person2 = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies.clone());
        HashCodeGenerator.Person person3 = new HashCodeGenerator.Person(2L, "Bob", 30, hobbies);

        // 相同属性应该产生相同哈希码
        assertEquals(person1.hashCode(), person2.hashCode());

        // 不同属性应该产生不同哈希码
        assertNotEquals(person1.hashCode(), person3.hashCode());
    }

    @Test
    public void testPersonEquals() {
        // 测试Person类的equals方法
        String[] hobbies = {"reading", "swimming"};
        HashCodeGenerator.Person person1 = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies);
        HashCodeGenerator.Person person2 = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies.clone());
        HashCodeGenerator.Person person3 = new HashCodeGenerator.Person(2L, "Bob", 30, hobbies);

        // 自反性
        assertEquals(person1, person1);

        // 对称性
        assertEquals(person1, person2);
        assertEquals(person2, person1);

        // 不同对象
        assertNotEquals(person1, person3);
        assertNotEquals(person1, null);
        assertNotEquals(person1, "not a person");
    }

    @Test
    public void testHashCodeConsistency() {
        // 测试哈希码一致性：同一对象多次调用返回相同值
        String[] hobbies = {"reading", "swimming"};
        HashCodeGenerator.Person person = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies);

        int hash1 = person.hashCode();
        int hash2 = person.hashCode();
        int hash3 = person.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    public void testEqualsContract() {
        // 测试equals和hashCode的契约关系
        String[] hobbies = {"reading", "swimming"};
        HashCodeGenerator.Person person1 = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies);
        HashCodeGenerator.Person person2 = new HashCodeGenerator.Person(1L, "Alice", 25, hobbies.clone());

        // 如果两个对象相等，它们的哈希码必须相等
        assertEquals(person1, person2);
        assertEquals(person1.hashCode(), person2.hashCode());
    }
}
