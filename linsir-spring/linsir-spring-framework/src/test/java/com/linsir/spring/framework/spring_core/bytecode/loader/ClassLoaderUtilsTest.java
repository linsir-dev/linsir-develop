package com.linsir.spring.framework.spring_core.bytecode.loader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassLoaderUtils测试类
 *
 * <p>测试类加载器工具类的核心功能，包括：
 * <ul>
 *   <li>获取默认类加载器</li>
 *   <li>获取类加载器层次结构</li>
 *   <li>获取类字节码</li>
 *   <li>类加载器名称</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class ClassLoaderUtilsTest {

    /**
     * 测试获取默认类加载器
     */
    @Test
    public void testGetDefaultClassLoader() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();

        assertNotNull(loader);
    }

    /**
     * 测试获取类加载器层次结构
     */
    @Test
    public void testGetClassLoaderHierarchy() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();
        ClassLoader[] hierarchy = ClassLoaderUtils.getClassLoaderHierarchy(loader);

        assertNotNull(hierarchy);
        assertTrue(hierarchy.length > 0);

        // 第一个应该是传入的类加载器
        assertEquals(loader, hierarchy[0]);
    }

    /**
     * 测试获取null类加载器的层次结构
     */
    @Test
    public void testGetNullClassLoaderHierarchy() {
        ClassLoader[] hierarchy = ClassLoaderUtils.getClassLoaderHierarchy(null);

        assertNotNull(hierarchy);
        assertEquals(0, hierarchy.length);
    }

    /**
     * 测试获取类字节码
     */
    @Test
    public void testGetClassBytes() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();
        byte[] bytes = ClassLoaderUtils.getClassBytes(String.class, loader);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        // 验证魔数
        assertEquals((byte) 0xCA, bytes[0]);
        assertEquals((byte) 0xFE, bytes[1]);
        assertEquals((byte) 0xBA, bytes[2]);
        assertEquals((byte) 0xBE, bytes[3]);
    }

    /**
     * 测试获取不存在的类字节码
     */
    @Test
    public void testGetClassBytesNotFound() {
        // 创建一个自定义类加载器，它不会委托给父类加载器
        ClassLoader customLoader = new ClassLoader(null) {
            @Override
            public java.io.InputStream getResourceAsStream(String name) {
                // 返回null表示找不到资源
                return null;
            }
        };

        // 使用这个类加载器尝试加载类
        byte[] bytes = ClassLoaderUtils.getClassBytes(
            ClassLoaderUtilsTest.class,
            customLoader
        );

        assertNull(bytes);
    }

    /**
     * 测试检查类是否由指定类加载器加载
     */
    @Test
    public void testIsLoadedBy() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();

        // String类通常由Bootstrap类加载器加载
        boolean loadedBy = ClassLoaderUtils.isLoadedBy(String.class, loader);

        // 结果取决于具体的类加载器层次结构
        // 这里只是验证方法不抛出异常
        // assertTrue或assertFalse都可以，取决于环境
    }

    /**
     * 测试获取类加载器名称
     */
    @Test
    public void testGetClassLoaderName() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();
        String name = ClassLoaderUtils.getClassLoaderName(loader);

        assertNotNull(name);
        assertTrue(name.contains("@")); // 包含hashCode
    }

    /**
     * 测试获取Bootstrap类加载器名称
     */
    @Test
    public void testGetBootstrapClassLoaderName() {
        String name = ClassLoaderUtils.getClassLoaderName(null);

        assertEquals("Bootstrap ClassLoader", name);
    }

    /**
     * 测试打印类加载器层次结构
     */
    @Test
    public void testPrintClassLoaderHierarchy() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();
        String output = ClassLoaderUtils.printClassLoaderHierarchy(loader);

        assertNotNull(output);
        assertTrue(output.contains("->")); // 包含层次结构标记
    }

    /**
     * 测试创建字节码类加载器
     */
    @Test
    public void testCreateBytecodeClassLoader() {
        BytecodeClassLoader loader = ClassLoaderUtils.createBytecodeClassLoader();

        assertNotNull(loader);
    }

    /**
     * 测试工具类不能实例化
     */
    @Test
    public void testCannotInstantiate() {
        // 使用反射尝试实例化，验证会抛出AssertionError
        Exception exception = assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<ClassLoaderUtils> constructor =
                ClassLoaderUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });

        // 验证异常链中包含AssertionError
        Throwable cause = exception;
        boolean foundAssertionError = false;
        while (cause != null) {
            if (cause instanceof AssertionError) {
                foundAssertionError = true;
                break;
            }
            cause = cause.getCause();
        }
        assertTrue(foundAssertionError, "应该抛出AssertionError");
    }
}
