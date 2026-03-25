package com.linsir.abc.core.base.lang.object;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ObjectMethodOverride 测试类
 *
 * 本测试类验证 ObjectMethodOverride 中重写方法的行为：
 * - equals() 方法的契约测试
 * - hashCode() 方法的契约测试
 * - toString() 方法的行为测试
 * - clone() 方法的浅拷贝和深拷贝测试
 *
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class ObjectMethodOverrideTest {

    /**
     * 测试 equals() 方法的自反性
     * 契约：x.equals(x) 必须返回 true
     */
    @Test
    public void testEqualsReflexivity() {
        ObjectMethodOverride obj = new ObjectMethodOverride(1L, "Test", 25);
        assertTrue("对象应该等于自身", obj.equals(obj));
    }

    /**
     * 测试 equals() 方法的对称性
     * 契约：x.equals(y) 为 true，则 y.equals(x) 也必须为 true
     */
    @Test
    public void testEqualsSymmetry() {
        ObjectMethodOverride obj1 = new ObjectMethodOverride(1L, "Test", 25);
        ObjectMethodOverride obj2 = new ObjectMethodOverride(1L, "Different", 30);

        assertTrue("obj1 应该等于 obj2", obj1.equals(obj2));
        assertTrue("obj2 应该等于 obj1", obj2.equals(obj1));
    }

    /**
     * 测试 equals() 方法的传递性
     * 契约：x.equals(y)为true且y.equals(z)为true，则x.equals(z)必须为true
     */
    @Test
    public void testEqualsTransitivity() {
        ObjectMethodOverride obj1 = new ObjectMethodOverride(1L, "Test", 25);
        ObjectMethodOverride obj2 = new ObjectMethodOverride(1L, "Different", 30);
        ObjectMethodOverride obj3 = new ObjectMethodOverride(1L, "Another", 35);

        assertTrue("obj1 应该等于 obj2", obj1.equals(obj2));
        assertTrue("obj2 应该等于 obj3", obj2.equals(obj3));
        assertTrue("obj1 应该等于 obj3", obj1.equals(obj3));
    }

    /**
     * 测试 equals() 方法的一致性
     * 契约：多次调用结果应该相同
     */
    @Test
    public void testEqualsConsistency() {
        ObjectMethodOverride obj1 = new ObjectMethodOverride(1L, "Test", 25);
        ObjectMethodOverride obj2 = new ObjectMethodOverride(1L, "Test", 25);

        boolean firstResult = obj1.equals(obj2);
        boolean secondResult = obj1.equals(obj2);
        boolean thirdResult = obj1.equals(obj2);

        assertEquals("多次调用结果应该一致", firstResult, secondResult);
        assertEquals("多次调用结果应该一致", secondResult, thirdResult);
    }

    /**
     * 测试 equals() 方法的非空性
     * 契约：x.equals(null) 必须返回 false
     */
    @Test
    public void testEqualsNonNull() {
        ObjectMethodOverride obj = new ObjectMethodOverride(1L, "Test", 25);
        assertFalse("对象不应该等于 null", obj.equals(null));
    }

    /**
     * 测试不同 ID 的对象不相等
     */
    @Test
    public void testEqualsDifferentId() {
        ObjectMethodOverride obj1 = new ObjectMethodOverride(1L, "Test", 25);
        ObjectMethodOverride obj2 = new ObjectMethodOverride(2L, "Test", 25);

        assertFalse("不同 ID 的对象不应该相等", obj1.equals(obj2));
    }

    /**
     * 测试 hashCode() 方法的一致性
     * 契约：同一对象多次调用应该返回相同值
     */
    @Test
    public void testHashCodeConsistency() {
        ObjectMethodOverride obj = new ObjectMethodOverride(1L, "Test", 25);

        int hashCode1 = obj.hashCode();
        int hashCode2 = obj.hashCode();
        int hashCode3 = obj.hashCode();

        assertEquals("同一对象的 hashCode 应该一致", hashCode1, hashCode2);
        assertEquals("同一对象的 hashCode 应该一致", hashCode2, hashCode3);
    }

    /**
     * 测试 equals 相等的对象 hashCode 必须相等
     * 契约：如果 x.equals(y) 为 true，则 x.hashCode() == y.hashCode()
     */
    @Test
    public void testHashCodeEqualsContract() {
        ObjectMethodOverride obj1 = new ObjectMethodOverride(1L, "Test", 25);
        ObjectMethodOverride obj2 = new ObjectMethodOverride(1L, "Different", 30);

        assertTrue("对象应该相等", obj1.equals(obj2));
        assertEquals("相等对象的 hashCode 应该相等", obj1.hashCode(), obj2.hashCode());
    }

    /**
     * 测试 toString() 方法包含必要信息
     */
    @Test
    public void testToStringContainsRequiredInfo() {
        ObjectMethodOverride obj = new ObjectMethodOverride(1L, "Test", 25);

        String str = obj.toString();

        assertTrue("toString 应该包含 id", str.contains("id=1"));
        assertTrue("toString 应该包含 name", str.contains("name='Test'"));
        assertTrue("toString 应该包含 age", str.contains("age=25"));
    }

    /**
     * 测试浅拷贝
     * 验证：基本类型字段被复制，引用类型字段共享引用
     */
    @Test
    public void testShallowClone() throws CloneNotSupportedException {
        ObjectMethodOverride original = new ObjectMethodOverride(1L, "Original", 25);
        ObjectMethodOverride reference = new ObjectMethodOverride(2L, "Reference", 30);
        original.setReference(reference);

        ObjectMethodOverride cloned = original.clone();

        // 验证是新对象
        assertNotSame("克隆对象应该是新对象", original, cloned);

        // 验证基本字段被复制
        assertEquals("ID 应该相同", original.getId(), cloned.getId());
        assertEquals("Name 应该相同", original.getName(), cloned.getName());
        assertEquals("Age 应该相同", original.getAge(), cloned.getAge());

        // 验证引用字段共享（浅拷贝特性）
        assertSame("引用字段应该共享", original.getReference(), cloned.getReference());
    }

    /**
     * 测试深拷贝
     * 验证：所有字段都被递归复制，引用类型字段也是新对象
     */
    @Test
    public void testDeepClone() throws CloneNotSupportedException {
        ObjectMethodOverride original = new ObjectMethodOverride(1L, "Original", 25);
        ObjectMethodOverride reference = new ObjectMethodOverride(2L, "Reference", 30);
        original.setReference(reference);

        ObjectMethodOverride cloned = original.deepClone();

        // 验证是新对象
        assertNotSame("克隆对象应该是新对象", original, cloned);

        // 验证基本字段被复制
        assertEquals("ID 应该相同", original.getId(), cloned.getId());
        assertEquals("Name 应该相同", original.getName(), cloned.getName());

        // 验证引用字段也是新对象（深拷贝特性）
        assertNotSame("引用字段应该是新对象", original.getReference(), cloned.getReference());
        assertEquals("引用对象的 ID 应该相同", original.getReference().getId(), cloned.getReference().getId());
    }

    /**
     * 测试修改克隆对象不影响原始对象
     */
    @Test
    public void testCloneIndependence() throws CloneNotSupportedException {
        ObjectMethodOverride original = new ObjectMethodOverride(1L, "Original", 25);
        ObjectMethodOverride cloned = original.deepClone();

        // 修改克隆对象
        cloned.setName("Modified");
        cloned.setAge(30);

        // 验证原始对象未被修改
        assertEquals("原始对象的 name 不应该被修改", "Original", original.getName());
        assertEquals("原始对象的 age 不应该被修改", Integer.valueOf(25), original.getAge());
    }

    /**
     * 测试浅拷贝的副作用（引用共享问题）
     */
    @Test
    public void testShallowCloneSideEffect() throws CloneNotSupportedException {
        ObjectMethodOverride original = new ObjectMethodOverride(1L, "Original", 25);
        ObjectMethodOverride reference = new ObjectMethodOverride(2L, "Reference", 30);
        original.setReference(reference);

        ObjectMethodOverride cloned = original.clone();

        // 修改克隆对象的引用对象
        cloned.getReference().setName("Modified");

        // 验证原始对象的引用对象也被修改（浅拷贝的副作用）
        assertEquals("原始对象的引用对象也被修改", "Modified", original.getReference().getName());
    }

    /**
     * 测试深拷贝避免副作用
     */
    @Test
    public void testDeepCloneAvoidSideEffect() throws CloneNotSupportedException {
        ObjectMethodOverride original = new ObjectMethodOverride(1L, "Original", 25);
        ObjectMethodOverride reference = new ObjectMethodOverride(2L, "Reference", 30);
        original.setReference(reference);

        ObjectMethodOverride cloned = original.deepClone();

        // 修改克隆对象的引用对象
        cloned.getReference().setName("Modified");

        // 验证原始对象的引用对象未被修改
        assertEquals("原始对象的引用对象不应该被修改", "Reference", original.getReference().getName());
    }
}
