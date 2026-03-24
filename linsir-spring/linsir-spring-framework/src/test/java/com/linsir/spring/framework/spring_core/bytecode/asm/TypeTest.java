package com.linsir.spring.framework.spring_core.bytecode.asm;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

/**
 * ASM Type测试类
 *
 * <p>测试ASM类型描述符的核心功能，包括：
 * <ul>
 *   <li>基本类型描述符</li>
 *   <li>对象类型描述符</li>
 *   <li>数组类型描述符</li>
 *   <li>方法描述符</li>
 *   <li>内部名称</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class TypeTest {

    /**
     * 测试基本类型描述符
     */
    @Test
    public void testPrimitiveTypeDescriptors() {
        // void
        assertEquals("V", Type.VOID_TYPE.getDescriptor());
        assertEquals(Type.VOID, Type.VOID_TYPE.getSort());

        // boolean
        assertEquals("Z", Type.BOOLEAN_TYPE.getDescriptor());
        assertEquals(Type.BOOLEAN, Type.BOOLEAN_TYPE.getSort());

        // char
        assertEquals("C", Type.CHAR_TYPE.getDescriptor());
        assertEquals(Type.CHAR, Type.CHAR_TYPE.getSort());

        // byte
        assertEquals("B", Type.BYTE_TYPE.getDescriptor());
        assertEquals(Type.BYTE, Type.BYTE_TYPE.getSort());

        // short
        assertEquals("S", Type.SHORT_TYPE.getDescriptor());
        assertEquals(Type.SHORT, Type.SHORT_TYPE.getSort());

        // int
        assertEquals("I", Type.INT_TYPE.getDescriptor());
        assertEquals(Type.INT, Type.INT_TYPE.getSort());

        // float
        assertEquals("F", Type.FLOAT_TYPE.getDescriptor());
        assertEquals(Type.FLOAT, Type.FLOAT_TYPE.getSort());

        // long
        assertEquals("J", Type.LONG_TYPE.getDescriptor());
        assertEquals(Type.LONG, Type.LONG_TYPE.getSort());
        assertEquals(2, Type.LONG_TYPE.getSlots()); // long占2个槽位

        // double
        assertEquals("D", Type.DOUBLE_TYPE.getDescriptor());
        assertEquals(Type.DOUBLE, Type.DOUBLE_TYPE.getSort());
        assertEquals(2, Type.DOUBLE_TYPE.getSlots()); // double占2个槽位
    }

    /**
     * 测试对象类型描述符
     */
    @Test
    public void testObjectTypeDescriptor() {
        Type stringType = Type.getType(String.class);
        assertEquals("Ljava/lang/String;", stringType.getDescriptor());
        assertEquals(Type.OBJECT, stringType.getSort());
        assertTrue(stringType.isObject());
        assertFalse(stringType.isPrimitive());
        assertFalse(stringType.isArray());

        Type objectType = Type.getType(Object.class);
        assertEquals("Ljava/lang/Object;", objectType.getDescriptor());
    }

    /**
     * 测试数组类型描述符
     */
    @Test
    public void testArrayTypeDescriptor() {
        // int数组
        Type intArrayType = Type.getType(int[].class);
        assertEquals("[I", intArrayType.getDescriptor());
        assertEquals(Type.ARRAY, intArrayType.getSort());
        assertTrue(intArrayType.isArray());

        // String数组
        Type stringArrayType = Type.getType(String[].class);
        assertEquals("[Ljava/lang/String;", stringArrayType.getDescriptor());

        // 二维数组
        Type int2DArrayType = Type.getType(int[][].class);
        assertEquals("[[I", int2DArrayType.getDescriptor());
    }

    /**
     * 测试从描述符创建Type
     */
    @Test
    public void testGetTypeFromDescriptor() {
        Type intType = Type.getType("I");
        assertEquals(Type.INT, intType.getSort());

        Type stringType = Type.getType("Ljava/lang/String;");
        assertEquals(Type.OBJECT, stringType.getSort());
        assertEquals("Ljava/lang/String;", stringType.getDescriptor());

        Type intArrayType = Type.getType("[I");
        assertEquals(Type.ARRAY, intArrayType.getSort());
    }

    /**
     * 测试内部名称
     */
    @Test
    public void testInternalName() {
        assertEquals("java/lang/String", Type.getInternalName(String.class));
        assertEquals("java/lang/Object", Type.getInternalName(Object.class));
        assertEquals("java/util/List", Type.getInternalName(List.class));
        assertEquals("java/util/Map", Type.getInternalName(Map.class));
    }

    /**
     * 测试方法描述符
     */
    @Test
    public void testMethodDescriptor() {
        // void method()
        String desc1 = Type.getMethodDescriptor(Type.VOID_TYPE);
        assertEquals("()V", desc1);

        // int method(int, int)
        String desc2 = Type.getMethodDescriptor(Type.INT_TYPE, Type.INT_TYPE, Type.INT_TYPE);
        assertEquals("(II)I", desc2);

        // String method(String)
        String desc3 = Type.getMethodDescriptor(
            Type.getType(String.class),
            Type.getType(String.class)
        );
        assertEquals("(Ljava/lang/String;)Ljava/lang/String;", desc3);

        // void method(int, String, Object)
        String desc4 = Type.getMethodDescriptor(
            Type.VOID_TYPE,
            Type.INT_TYPE,
            Type.getType(String.class),
            Type.getType(Object.class)
        );
        assertEquals("(ILjava/lang/String;Ljava/lang/Object;)V", desc4);

        // boolean method(int[])
        String desc5 = Type.getMethodDescriptor(
            Type.BOOLEAN_TYPE,
            Type.getType(int[].class)
        );
        assertEquals("([I)Z", desc5);
    }

    /**
     * 测试类型大小
     */
    @Test
    public void testTypeSize() {
        // 基本类型大小为1
        assertEquals(1, Type.INT_TYPE.getSize());
        assertEquals(1, Type.FLOAT_TYPE.getSize());

        // long和double大小为1（但占2个槽位）
        assertEquals(1, Type.LONG_TYPE.getSize());
        assertEquals(1, Type.DOUBLE_TYPE.getSize());

        // 对象类型大小为1
        assertEquals(1, Type.getType(String.class).getSize());

        // 数组类型大小为1
        assertEquals(1, Type.getType(int[].class).getSize());
    }

    /**
     * 测试类型槽位数
     */
    @Test
    public void testTypeSlots() {
        // int和float占1个槽位
        assertEquals(1, Type.INT_TYPE.getSlots());
        assertEquals(1, Type.FLOAT_TYPE.getSlots());

        // long和double占2个槽位
        assertEquals(2, Type.LONG_TYPE.getSlots());
        assertEquals(2, Type.DOUBLE_TYPE.getSlots());

        // 对象类型占1个槽位
        assertEquals(1, Type.getType(String.class).getSlots());
    }

    /**
     * 测试类型检查方法
     */
    @Test
    public void testTypeChecks() {
        // 基本类型检查
        assertTrue(Type.INT_TYPE.isPrimitive());
        assertFalse(Type.INT_TYPE.isArray());
        assertFalse(Type.INT_TYPE.isObject());

        // 对象类型检查
        Type stringType = Type.getType(String.class);
        assertFalse(stringType.isPrimitive());
        assertFalse(stringType.isArray());
        assertTrue(stringType.isObject());

        // 数组类型检查 - 数组是特殊的对象类型
        Type intArrayType = Type.getType(int[].class);
        assertFalse(intArrayType.isPrimitive());
        assertTrue(intArrayType.isArray());
        // 注意：在ASM中，数组类型有独立的ARRAY sort，不是OBJECT
        assertFalse(intArrayType.isObject());
    }

    /**
     * 测试toString
     */
    @Test
    public void testToString() {
        assertEquals("I", Type.INT_TYPE.toString());
        assertEquals("Ljava/lang/String;", Type.getType(String.class).toString());
        assertEquals("[I", Type.getType(int[].class).toString());
    }
}
