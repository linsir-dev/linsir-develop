package com.linsir.abc.core.base.lang.reflect;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * ReflectionInspector测试类
 */
public class ReflectionInspectorTest {

    /**
     * 测试获取Class对象的三种方式
     */
    @Test
    public void testGetClassMethods() throws ClassNotFoundException {
        ReflectionInspector inspector = new ReflectionInspector();

        // 通过类名获取
        Class<?> clazz1 = inspector.getClassByName("java.lang.String");
        assertEquals(String.class, clazz1);

        // 通过对象获取
        Class<?> clazz2 = inspector.getClassByObject("test");
        assertEquals(String.class, clazz2);

        // 通过类字面量获取
        Class<String> clazz3 = inspector.getClassByLiteral(String.class);
        assertEquals(String.class, clazz3);
    }

    /**
     * 测试检查类基本信息
     */
    @Test
    public void testInspectClassBasicInfo() {
        ReflectionInspector inspector = new ReflectionInspector();
        String info = inspector.inspectClassBasicInfo(String.class);

        assertNotNull(info);
        assertTrue(info.contains("类名: java.lang.String"));
        assertTrue(info.contains("简单类名: String"));
    }

    /**
     * 测试获取构造方法信息
     */
    @Test
    public void testGetConstructorsInfo() {
        ReflectionInspector inspector = new ReflectionInspector();
        java.util.List<String> constructors = inspector.getConstructorsInfo(String.class);

        assertNotNull(constructors);
        assertFalse(constructors.isEmpty());
    }

    /**
     * 测试获取字段信息
     */
    @Test
    public void testGetFieldsInfo() {
        ReflectionInspector inspector = new ReflectionInspector();
        java.util.List<String> fields = inspector.getFieldsInfo(ReflectionInspector.TestClass.class);

        assertNotNull(fields);
        assertFalse(fields.isEmpty());
    }

    /**
     * 测试获取方法信息
     */
    @Test
    public void testGetMethodsInfo() {
        ReflectionInspector inspector = new ReflectionInspector();
        java.util.List<String> methods = inspector.getMethodsInfo(ReflectionInspector.TestClass.class);

        assertNotNull(methods);
        assertFalse(methods.isEmpty());
    }

    /**
     * 测试创建实例
     */
    @Test
    public void testCreateInstance() throws Exception {
        ReflectionInspector inspector = new ReflectionInspector();

        ReflectionInspector.TestClass instance = inspector.createInstance(
            ReflectionInspector.TestClass.class, "test");

        assertNotNull(instance);
        assertEquals("test", instance.getPrivateField());
    }

    /**
     * 测试调用方法
     */
    @Test
    public void testInvokeMethod() throws Exception {
        ReflectionInspector inspector = new ReflectionInspector();

        ReflectionInspector.TestClass instance = new ReflectionInspector.TestClass();
        Object result = inspector.invokeMethod(instance, "add", 1, 2);

        assertEquals(3, result);
    }

    /**
     * 测试获取和设置字段值
     */
    @Test
    public void testFieldAccess() throws Exception {
        ReflectionInspector inspector = new ReflectionInspector();

        ReflectionInspector.TestClass instance = new ReflectionInspector.TestClass();

        // 获取字段值
        Object value = inspector.getFieldValue(instance, "publicField");
        assertEquals(0, value);

        // 设置字段值
        inspector.setFieldValue(instance, "publicField", 100);
        assertEquals(100, instance.publicField);
    }
}
