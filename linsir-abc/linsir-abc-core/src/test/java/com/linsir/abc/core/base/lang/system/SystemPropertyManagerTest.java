package com.linsir.abc.core.base.lang.system;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Properties;

/**
 * SystemPropertyManager测试类
 */
public class SystemPropertyManagerTest {

    /**
     * 测试获取系统属性
     */
    @Test
    public void testGetProperty() {
        // 获取已知的系统属性
        String javaVersion = SystemPropertyManager.getProperty("java.version");
        assertNotNull(javaVersion);

        // 获取不存在的属性
        String nonExistent = SystemPropertyManager.getProperty("non.existent.property");
        assertNull(nonExistent);
    }

    /**
     * 测试获取带默认值的系统属性
     */
    @Test
    public void testGetPropertyWithDefault() {
        // 存在的属性
        String javaVersion = SystemPropertyManager.getProperty("java.version", "default");
        assertNotNull(javaVersion);
        assertNotEquals("default", javaVersion);

        // 不存在的属性，返回默认值
        String nonExistent = SystemPropertyManager.getProperty("non.existent.property", "defaultValue");
        assertEquals("defaultValue", nonExistent);
    }

    /**
     * 测试设置系统属性
     */
    @Test
    public void testSetProperty() {
        String key = "test.property";
        String value = "testValue";

        // 设置属性
        String oldValue = SystemPropertyManager.setProperty(key, value);

        // 验证设置成功
        assertEquals(value, SystemPropertyManager.getProperty(key));

        // 清理
        SystemPropertyManager.clearProperty(key);
    }

    /**
     * 测试清除系统属性
     */
    @Test
    public void testClearProperty() {
        String key = "test.clear.property";
        String value = "testValue";

        // 先设置
        SystemPropertyManager.setProperty(key, value);
        assertEquals(value, SystemPropertyManager.getProperty(key));

        // 清除
        String clearedValue = SystemPropertyManager.clearProperty(key);
        assertEquals(value, clearedValue);

        // 验证已清除
        assertNull(SystemPropertyManager.getProperty(key));
    }

    /**
     * 测试获取所有系统属性
     */
    @Test
    public void testGetAllProperties() {
        Properties props = SystemPropertyManager.getAllProperties();
        assertNotNull(props);
        assertFalse(props.isEmpty());

        // 验证包含常见的系统属性
        assertTrue(props.containsKey("java.version"));
        assertTrue(props.containsKey("java.home"));
        assertTrue(props.containsKey("os.name"));
    }

    /**
     * 测试获取常用系统属性
     */
    @Test
    public void testGetCommonProperties() {
        String info = SystemPropertyManager.getCommonProperties();
        assertNotNull(info);

        // 验证包含关键信息
        assertTrue(info.contains("Java版本"));
        assertTrue(info.contains("Java安装目录"));
        assertTrue(info.contains("操作系统"));
        assertTrue(info.contains("用户目录"));
    }

    /**
     * 测试获取环境变量
     */
    @Test
    public void testGetEnv() {
        // 获取PATH环境变量（Windows/Linux都常见）
        String path = SystemPropertyManager.getEnv("PATH");
        if (path == null) {
            path = SystemPropertyManager.getEnv("Path");
        }

        // PATH应该存在
        assertNotNull(path);
    }

    /**
     * 测试获取所有环境变量
     */
    @Test
    public void testGetAllEnv() {
        java.util.Map<String, String> env = SystemPropertyManager.getAllEnv();
        assertNotNull(env);
        assertFalse(env.isEmpty());

        // 验证包含PATH
        assertTrue(env.containsKey("PATH") || env.containsKey("Path"));
    }

    /**
     * 测试获取当前时间戳
     */
    @Test
    public void testCurrentTimeMillis() {
        long time1 = SystemPropertyManager.currentTimeMillis();

        // 等待一小段时间
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long time2 = SystemPropertyManager.currentTimeMillis();

        // 时间应该递增
        assertTrue(time2 >= time1);
    }

    /**
     * 测试获取纳秒时间
     */
    @Test
    public void testNanoTime() {
        long time1 = SystemPropertyManager.nanoTime();

        // 等待一小段时间
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long time2 = SystemPropertyManager.nanoTime();

        // 纳秒时间应该递增
        assertTrue(time2 > time1);
    }

    /**
     * 测试数组拷贝
     */
    @Test
    public void testArrayCopy() {
        int[] source = {1, 2, 3, 4, 5};
        int[] dest = new int[5];

        SystemPropertyManager.arrayCopy(source, 0, dest, 0, 5);

        // 验证拷贝结果
        assertArrayEquals(source, dest);
    }

    /**
     * 测试工具类不能被实例化
     */
    @Test(expected = AssertionError.class)
    public void testCannotInstantiate() {
        // 尝试通过反射创建实例
        try {
            java.lang.reflect.Constructor<SystemPropertyManager> constructor =
                SystemPropertyManager.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        } catch (Exception e) {
            if (e.getCause() instanceof AssertionError) {
                throw (AssertionError) e.getCause();
            }
            fail("Expected AssertionError");
        }
    }
}
