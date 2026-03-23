package com.linsir.spring.framework.spring_core.env.resolver;

import com.linsir.spring.framework.spring_core.env.source.MapPropertySource;
import com.linsir.spring.framework.spring_core.env.support.MutablePropertySources;
import com.linsir.spring.framework.spring_core.env.support.PropertySourcesPropertyResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PropertyResolver 测试类
 *
 * 测试 PropertyResolver 的各种功能
 *
 * @author linsir
 * @since 1.0.0
 */
class PropertyResolverTest {

    private MutablePropertySources propertySources;
    private PropertyResolver resolver;

    @BeforeEach
    void setUp() {
        propertySources = new MutablePropertySources();

        // 准备测试数据
        Map<String, Object> map1 = new HashMap<>();
        map1.put("app.name", "TestApp");
        map1.put("app.port", "8080");
        map1.put("app.debug", "true");
        map1.put("app.version", "1.0.0");
        map1.put("db.url", "jdbc:mysql://localhost/test");

        MapPropertySource source1 = new MapPropertySource("source1", map1);
        propertySources.addLast(source1);

        resolver = new PropertySourcesPropertyResolver(propertySources);
    }

    @Test
    void testContainsProperty() {
        assertTrue(resolver.containsProperty("app.name"));
        assertTrue(resolver.containsProperty("app.port"));
        assertFalse(resolver.containsProperty("app.nonexistent"));
    }

    @Test
    void testGetProperty() {
        assertEquals("TestApp", resolver.getProperty("app.name"));
        assertEquals("8080", resolver.getProperty("app.port"));
        assertNull(resolver.getProperty("app.nonexistent"));
    }

    @Test
    void testGetPropertyWithDefault() {
        assertEquals("TestApp", resolver.getProperty("app.name", "DefaultApp"));
        assertEquals("DefaultValue", resolver.getProperty("app.nonexistent", "DefaultValue"));
    }

    @Test
    void testGetPropertyWithType() {
        // String 类型
        String name = resolver.getProperty("app.name", String.class);
        assertEquals("TestApp", name);

        // Integer 类型
        Integer port = resolver.getProperty("app.port", Integer.class);
        assertEquals(8080, port);

        // Boolean 类型
        Boolean debug = resolver.getProperty("app.debug", Boolean.class);
        assertTrue(debug);
    }

    @Test
    void testGetPropertyWithTypeAndDefault() {
        Integer port = resolver.getProperty("app.port", Integer.class, 9090);
        assertEquals(8080, port);

        Integer nonexistent = resolver.getProperty("app.nonexistent", Integer.class, 9090);
        assertEquals(9090, nonexistent);
    }

    @Test
    void testGetRequiredProperty() {
        String name = resolver.getRequiredProperty("app.name");
        assertEquals("TestApp", name);
    }

    @Test
    void testGetRequiredPropertyNotFound() {
        assertThrows(IllegalStateException.class, () -> {
            resolver.getRequiredProperty("app.nonexistent");
        });
    }

    @Test
    void testGetRequiredPropertyWithType() {
        Integer port = resolver.getRequiredProperty("app.port", Integer.class);
        assertEquals(8080, port);
    }

    @Test
    void testGetRequiredPropertyWithTypeNotFound() {
        assertThrows(IllegalStateException.class, () -> {
            resolver.getRequiredProperty("app.nonexistent", Integer.class);
        });
    }

    @Test
    void testResolvePlaceholders() {
        // 简单占位符
        String result = resolver.resolvePlaceholders("App: ${app.name}");
        assertEquals("App: TestApp", result);

        // 多个占位符
        result = resolver.resolvePlaceholders("${app.name}:${app.port}");
        assertEquals("TestApp:8080", result);
    }

    @Test
    void testResolvePlaceholdersWithDefault() {
        // 带默认值的占位符
        String result = resolver.resolvePlaceholders("${app.nonexistent:defaultValue}");
        assertEquals("defaultValue", result);

        // 存在的属性不使用默认值
        result = resolver.resolvePlaceholders("${app.name:DefaultApp}");
        assertEquals("TestApp", result);
    }

    @Test
    void testResolvePlaceholdersNested() {
        // 添加嵌套测试数据
        Map<String, Object> map2 = new HashMap<>();
        map2.put("app.fullname", "${app.name}-v${app.version}");
        map2.put("server.address", "${app.name}:${app.port}");

        MapPropertySource source2 = new MapPropertySource("source2", map2);
        propertySources.addLast(source2);

        // 嵌套占位符
        String result = resolver.resolvePlaceholders("${app.fullname}");
        assertEquals("TestApp-v1.0.0", result);

        // 多个占位符
        result = resolver.resolvePlaceholders("${server.address}");
        assertEquals("TestApp:8080", result);
    }

    @Test
    void testResolvePlaceholdersRecursive() {
        // 添加递归测试数据
        Map<String, Object> map2 = new HashMap<>();
        map2.put("a", "${b}");
        map2.put("b", "${c}");
        map2.put("c", "final");

        MapPropertySource source2 = new MapPropertySource("source2", map2);
        propertySources.addLast(source2);

        String result = resolver.resolvePlaceholders("${a}");
        assertEquals("final", result);
    }

    @Test
    void testResolveRequiredPlaceholders() {
        String result = resolver.resolveRequiredPlaceholders("App: ${app.name}");
        assertEquals("App: TestApp", result);
    }

    @Test
    void testResolveRequiredPlaceholdersNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolveRequiredPlaceholders("${app.nonexistent}");
        });
    }

    @Test
    void testPropertySourcePriority() {
        // 添加高优先级的属性源
        Map<String, Object> map2 = new HashMap<>();
        map2.put("app.name", "HighPriorityApp");
        map2.put("app.newkey", "newvalue");

        MapPropertySource source2 = new MapPropertySource("source2", map2);
        propertySources.addFirst(source2); // 高优先级

        // 高优先级的值应该被返回
        assertEquals("HighPriorityApp", resolver.getProperty("app.name"));

        // 低优先级的属性也应该可以访问
        assertEquals("8080", resolver.getProperty("app.port"));

        // 只有高优先级有的属性
        assertEquals("newvalue", resolver.getProperty("app.newkey"));
    }

    @Test
    void testNullInput() {
        assertNull(resolver.getProperty(null));
        assertEquals("default", resolver.getProperty(null, "default"));
        assertNull(resolver.resolvePlaceholders(null));
    }

    @Test
    void testEmptyPropertySources() {
        MutablePropertySources emptySources = new MutablePropertySources();
        PropertyResolver emptyResolver = new PropertySourcesPropertyResolver(emptySources);

        assertFalse(emptyResolver.containsProperty("any.key"));
        assertNull(emptyResolver.getProperty("any.key"));
    }

    @Test
    void testTypeConversion() {
        // Integer
        assertEquals(8080, resolver.getProperty("app.port", Integer.class));

        // Long
        assertEquals(8080L, resolver.getProperty("app.port", Long.class));

        // Boolean
        assertTrue(resolver.getProperty("app.debug", Boolean.class));

        // Double - 使用合适的数字字符串
        assertEquals(8080.0, resolver.getProperty("app.port", Double.class), 0.001);
    }

    @Test
    void testTypeConversionFailure() {
        // 尝试将非数字字符串转换为 Integer
        assertThrows(IllegalArgumentException.class, () -> {
            resolver.getProperty("app.name", Integer.class);
        });
    }

    @Test
    void testCircularPlaceholderReference() {
        // 添加循环引用测试数据
        Map<String, Object> map2 = new HashMap<>();
        map2.put("x", "${y}");
        map2.put("y", "${x}");

        MapPropertySource source2 = new MapPropertySource("source2", map2);
        propertySources.addLast(source2);

        assertThrows(IllegalArgumentException.class, () -> {
            resolver.resolvePlaceholders("${x}");
        });
    }
}
