package com.linsir.spring.framework.spring_core.bytecode.loader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * BytecodeClassLoader测试类
 *
 * <p>测试字节码类加载器的核心功能，包括：
 * <ul>
 *   <li>从字节码定义类</li>
 *   <li>类缓存</li>
 *   <li>类查找</li>
 *   <li>缓存管理</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class BytecodeClassLoaderTest {

    /**
     * 测试类名
     */
    private static final String TEST_CLASS_NAME = "com.linsir.test.GeneratedClass";

    /**
     * 测试创建类加载器
     */
    @Test
    public void testCreateClassLoader() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        assertNotNull(loader);
        assertNotNull(loader.getParent());
    }

    /**
     * 测试创建带父加载器的类加载器
     */
    @Test
    public void testCreateClassLoaderWithParent() {
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        BytecodeClassLoader loader = new BytecodeClassLoader(parent);

        assertNotNull(loader);
        assertEquals(parent, loader.getParent());
    }

    /**
     * 测试定义类（简化版，使用模拟字节码）
     */
    @Test
    public void testDefineClass() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        // 创建模拟字节码（实际应该使用ASM生成）
        byte[] bytecode = createMockBytecode();

        // 注意：这里使用一个不存在的类名，因为模拟字节码无法真正加载
        // 实际测试中应该使用ASM生成有效的字节码
        // 模拟字节码会导致ClassFormatError
        assertThrows(ClassFormatError.class, () -> {
            loader.defineClass("com.test.InvalidClass", bytecode);
        });
    }

    /**
     * 测试定义类参数验证
     */
    @Test
    public void testDefineClassValidation() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        // 测试null类名
        assertThrows(IllegalArgumentException.class, () -> {
            loader.defineClass(null, new byte[10]);
        });

        // 测试空类名
        assertThrows(IllegalArgumentException.class, () -> {
            loader.defineClass("", new byte[10]);
        });

        // 测试null字节码
        assertThrows(IllegalArgumentException.class, () -> {
            loader.defineClass("com.test.Test", null);
        });

        // 测试空字节码
        assertThrows(IllegalArgumentException.class, () -> {
            loader.defineClass("com.test.Test", new byte[0]);
        });
    }

    /**
     * 测试类缓存
     */
    @Test
    public void testClassCache() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        // 初始缓存为空
        assertEquals(0, loader.getCacheSize());
        assertFalse(loader.isDefined(TEST_CLASS_NAME));
        assertNull(loader.getDefinedClass(TEST_CLASS_NAME));
    }

    /**
     * 测试清除缓存
     */
    @Test
    public void testClearCache() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        // 清除空缓存
        loader.clearCache();
        assertEquals(0, loader.getCacheSize());
    }

    /**
     * 测试获取已定义类名称
     */
    @Test
    public void testGetDefinedClassNames() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        String[] names = loader.getDefinedClassNames();

        assertNotNull(names);
        assertEquals(0, names.length);
    }

    /**
     * 测试定义类不缓存
     */
    @Test
    public void testDefineClassWithoutCache() {
        BytecodeClassLoader loader = new BytecodeClassLoader();

        // 测试参数验证
        assertThrows(IllegalArgumentException.class, () -> {
            loader.defineClassWithoutCache(null, new byte[10]);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            loader.defineClassWithoutCache("com.test.Test", null);
        });
    }

    /**
     * 创建模拟字节码
     *
     * @return 模拟字节码数组
     */
    private byte[] createMockBytecode() {
        // 创建一个简单的类文件结构
        // 实际应该使用ASM生成有效的字节码
        byte[] bytecode = new byte[100];

        // 魔数
        bytecode[0] = (byte) 0xCA;
        bytecode[1] = (byte) 0xFE;
        bytecode[2] = (byte) 0xBA;
        bytecode[3] = (byte) 0xBE;

        // 版本号 (Java 8)
        bytecode[4] = 0x00;
        bytecode[5] = 0x00;
        bytecode[6] = 0x00;
        bytecode[7] = 0x34;

        return bytecode;
    }
}
