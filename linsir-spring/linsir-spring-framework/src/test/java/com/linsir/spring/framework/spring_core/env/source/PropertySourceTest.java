package com.linsir.spring.framework.spring_core.env.source;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * PropertySource 测试类
 *
 * 测试各种 PropertySource 实现的功能
 *
 * @author linsir
 * @since 1.0.0
 */
class PropertySourceTest {

    @Test
    void testMapPropertySource() {
        // 准备测试数据
        Map<String, Object> map = new HashMap<>();
        map.put("app.name", "TestApp");
        map.put("app.port", 8080);

        // 创建 MapPropertySource
        MapPropertySource source = new MapPropertySource("testMap", map);

        // 验证基本属性
        assertEquals("testMap", source.getName());
        assertSame(map, source.getSource());

        // 验证 containsProperty
        assertTrue(source.containsProperty("app.name"));
        assertTrue(source.containsProperty("app.port"));
        assertFalse(source.containsProperty("app.nonexistent"));

        // 验证 getProperty
        assertEquals("TestApp", source.getProperty("app.name"));
        assertEquals(8080, source.getProperty("app.port"));
        assertNull(source.getProperty("app.nonexistent"));

        // 验证 getProperty with type
        String name = source.getProperty("app.name", String.class);
        assertEquals("TestApp", name);

        Integer port = source.getProperty("app.port", Integer.class);
        assertEquals(8080, port);

        // 验证 getPropertyNames
        String[] names = source.getPropertyNames();
        assertEquals(2, names.length);
    }

    @Test
    void testMapPropertySourceModify() {
        Map<String, Object> map = new HashMap<>();
        MapPropertySource source = new MapPropertySource("testMap", map);

        // 测试 setProperty
        source.setProperty("key1", "value1");
        assertTrue(source.containsProperty("key1"));
        assertEquals("value1", source.getProperty("key1"));

        // 测试 removeProperty
        Object removed = source.removeProperty("key1");
        assertEquals("value1", removed);
        assertFalse(source.containsProperty("key1"));
    }

    @Test
    void testPropertiesPropertySource() {
        // 准备测试数据
        Properties props = new Properties();
        props.setProperty("db.url", "jdbc:mysql://localhost:3306/test");
        props.setProperty("db.username", "root");

        // 创建 PropertiesPropertySource
        PropertiesPropertySource source = new PropertiesPropertySource("testProps", props);

        // 验证基本属性
        assertEquals("testProps", source.getName());

        // 验证 containsProperty
        assertTrue(source.containsProperty("db.url"));
        assertTrue(source.containsProperty("db.username"));
        assertFalse(source.containsProperty("db.password"));

        // 验证 getProperty
        assertEquals("jdbc:mysql://localhost:3306/test", source.getProperty("db.url"));
        assertEquals("root", source.getProperty("db.username"));
        assertNull(source.getProperty("db.password"));

        // 验证 getPropertyNames
        String[] names = source.getPropertyNames();
        assertEquals(2, names.length);
    }

    @Test
    void testSystemEnvironmentPropertySource() {
        // 准备测试数据
        Map<String, Object> env = new HashMap<>();
        env.put("JAVA_HOME", "/usr/lib/java");
        env.put("PATH", "/usr/bin:/bin");
        env.put("SPRING_PROFILES_ACTIVE", "dev");

        // 创建 SystemEnvironmentPropertySource
        SystemEnvironmentPropertySource source = new SystemEnvironmentPropertySource(env);

        // 验证基本属性
        assertEquals(SystemEnvironmentPropertySource.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, source.getName());

        // 验证精确匹配
        assertTrue(source.containsProperty("JAVA_HOME"));
        assertEquals("/usr/lib/java", source.getProperty("JAVA_HOME"));

        // 验证转换后的匹配（spring.profiles.active -> SPRING_PROFILES_ACTIVE）
        assertTrue(source.containsProperty("spring.profiles.active"));
        assertEquals("dev", source.getProperty("spring.profiles.active"));

        // 验证不存在的属性
        assertFalse(source.containsProperty("NONEXISTENT_VAR"));
        assertNull(source.getProperty("NONEXISTENT_VAR"));
    }

    @Test
    void testCommandLinePropertySource() {
        // 准备测试数据
        String[] args = {
            "--server.port=8080",
            "--spring.profiles.active=dev,test",
            "--debug"
        };

        // 创建 CommandLinePropertySource
        CommandLinePropertySource source = new CommandLinePropertySource(args);

        // 验证基本属性
        assertEquals(CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME, source.getName());

        // 验证 --key=value 格式
        assertTrue(source.containsProperty("server.port"));
        assertEquals("8080", source.getProperty("server.port"));

        // 验证 --key value 格式（这里用等号格式测试）
        assertTrue(source.containsProperty("spring.profiles.active"));
        assertEquals("dev,test", source.getProperty("spring.profiles.active"));

        // 验证 --flag 格式
        assertTrue(source.containsProperty("debug"));
        assertEquals("true", source.getProperty("debug"));

        // 验证不存在的属性
        assertFalse(source.containsProperty("nonexistent"));
    }

    @Test
    void testCommandLinePropertySourceWithSpace() {
        // 准备测试数据（使用空格分隔的格式）
        String[] args = {
            "--config",
            "application.yml",
            "--verbose"
        };

        // 创建 CommandLinePropertySource
        CommandLinePropertySource source = new CommandLinePropertySource(args);

        // 验证 --key value 格式
        assertTrue(source.containsProperty("config"));
        assertEquals("application.yml", source.getProperty("config"));

        // 验证 --flag 格式
        assertTrue(source.containsProperty("verbose"));
        assertEquals("true", source.getProperty("verbose"));
    }

    @Test
    void testPropertySourceEquality() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("key", "value");

        Map<String, Object> map2 = new HashMap<>();
        map2.put("key", "different");

        MapPropertySource source1 = new MapPropertySource("sameName", map1);
        MapPropertySource source2 = new MapPropertySource("sameName", map2);
        MapPropertySource source3 = new MapPropertySource("differentName", map1);

        // 验证名称相同的 PropertySource 相等
        assertEquals(source1, source2);
        assertEquals(source1.hashCode(), source2.hashCode());

        // 验证名称不同的 PropertySource 不相等
        assertNotEquals(source1, source3);
    }

    @Test
    void testPropertySourceToString() {
        Map<String, Object> map = new HashMap<>();
        MapPropertySource source = new MapPropertySource("testSource", map);

        String str = source.toString();
        assertTrue(str.contains("testSource"));
        assertTrue(str.contains("HashMap"));
    }

    @Test
    void testPropertySourceNullValidation() {
        // 验证 name 不能为 null
        assertThrows(IllegalArgumentException.class, () -> {
            new MapPropertySource(null, new HashMap<>());
        });

        // 验证 source 不能为 null
        assertThrows(IllegalArgumentException.class, () -> {
            new MapPropertySource("test", null);
        });
    }
}
