package com.linsir.spring.framework.spring_core.env.core;

import com.linsir.spring.framework.spring_core.env.source.MapPropertySource;
import com.linsir.spring.framework.spring_core.env.support.StandardEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Environment 测试类
 *
 * 测试 Environment 的各种功能
 *
 * @author linsir
 * @since 1.0.0
 */
class EnvironmentTest {

    private StandardEnvironment environment;

    @BeforeEach
    void setUp() {
        environment = new StandardEnvironment();

        // 添加测试属性源
        Map<String, Object> map = new HashMap<>();
        map.put("app.name", "TestApp");
        map.put("app.port", "8080");

        MapPropertySource source = new MapPropertySource("testSource", map);
        environment.getPropertySources().addFirst(source);
    }

    @Test
    void testGetActiveProfiles() {
        // 默认情况下没有激活的 Profile
        String[] activeProfiles = environment.getActiveProfiles();
        assertNotNull(activeProfiles);

        // 设置激活的 Profile
        environment.setActiveProfiles("dev", "test");
        activeProfiles = environment.getActiveProfiles();
        assertEquals(2, activeProfiles.length);
        assertTrue(contains(activeProfiles, "dev"));
        assertTrue(contains(activeProfiles, "test"));
    }

    @Test
    void testGetDefaultProfiles() {
        // 默认的 Profile
        String[] defaultProfiles = environment.getDefaultProfiles();
        assertNotNull(defaultProfiles);
        assertTrue(defaultProfiles.length > 0);
        assertTrue(contains(defaultProfiles, "default"));

        // 设置默认的 Profile
        environment.setDefaultProfiles("production");
        defaultProfiles = environment.getDefaultProfiles();
        assertEquals(1, defaultProfiles.length);
        assertEquals("production", defaultProfiles[0]);
    }

    @Test
    void testAcceptsProfiles() {
        // 设置激活的 Profile
        environment.setActiveProfiles("dev");

        // 验证激活的 Profile
        assertTrue(environment.acceptsProfiles("dev"));
        assertFalse(environment.acceptsProfiles("prod"));

        // 验证否定表达式
        assertTrue(environment.acceptsProfiles("!prod"));
        assertFalse(environment.acceptsProfiles("!dev"));
    }

    @Test
    void testAcceptsProfilesMultiple() {
        // 设置激活的 Profile
        environment.setActiveProfiles("dev", "test");

        // 验证多个 Profile（或关系）
        assertTrue(environment.acceptsProfiles("dev", "prod"));
        assertTrue(environment.acceptsProfiles("prod", "dev"));
        assertFalse(environment.acceptsProfiles("prod", "staging"));
    }

    @Test
    void testAcceptsProfilesWithNoActiveProfiles() {
        // 没有激活的 Profile 时，使用默认的
        String[] defaultProfiles = environment.getDefaultProfiles();
        for (String profile : defaultProfiles) {
            assertTrue(environment.acceptsProfiles(profile));
        }
    }

    @Test
    void testAddActiveProfile() {
        environment.addActiveProfile("dev");
        assertTrue(environment.acceptsProfiles("dev"));

        environment.addActiveProfile("test");
        assertTrue(environment.acceptsProfiles("dev"));
        assertTrue(environment.acceptsProfiles("test"));
    }

    @Test
    void testPropertySources() {
        // 验证属性源集合不为空（包含系统属性和环境变量）
        assertNotNull(environment.getPropertySources());
        assertTrue(environment.getPropertySources().size() >= 2);
    }

    @Test
    void testAddPropertySource() {
        Map<String, Object> map = new HashMap<>();
        map.put("new.key", "newvalue");

        MapPropertySource source = new MapPropertySource("newSource", map);
        environment.addPropertySource(source);

        assertEquals("newvalue", environment.getProperty("new.key"));
    }

    @Test
    void testMerge() {
        // 创建另一个环境
        StandardEnvironment other = new StandardEnvironment();
        other.setActiveProfiles("staging");

        Map<String, Object> map = new HashMap<>();
        map.put("other.key", "othervalue");
        MapPropertySource source = new MapPropertySource("otherSource", map);
        other.addPropertySource(source);

        // 合并环境
        environment.merge(other);

        // 验证属性源被合并
        assertEquals("othervalue", environment.getProperty("other.key"));

        // 验证 Profile 被合并
        assertTrue(environment.acceptsProfiles("staging"));
    }

    @Test
    void testPropertyResolution() {
        // 测试属性解析
        assertEquals("TestApp", environment.getProperty("app.name"));
        assertEquals("8080", environment.getProperty("app.port"));
    }

    @Test
    void testResolvePlaceholders() {
        String result = environment.resolvePlaceholders("App: ${app.name}");
        assertEquals("App: TestApp", result);
    }

    @Test
    void testActiveProfilesFromProperty() {
        // 通过属性设置激活的 Profile
        Map<String, Object> map = new HashMap<>();
        map.put("spring.profiles.active", "prod,test");

        MapPropertySource source = new MapPropertySource("profileSource", map);
        environment.getPropertySources().addFirst(source);

        String[] activeProfiles = environment.getActiveProfiles();
        assertTrue(contains(activeProfiles, "prod"));
        assertTrue(contains(activeProfiles, "test"));
    }

    @Test
    void testNullProfile() {
        assertFalse(environment.acceptsProfiles((String) null));
        assertFalse(environment.acceptsProfiles((String[]) null));
        assertFalse(environment.acceptsProfiles(""));
    }

    @Test
    void testToString() {
        String str = environment.toString();
        assertNotNull(str);
        assertTrue(str.contains("StandardEnvironment"));
    }

    /**
     * 辅助方法：检查数组是否包含指定元素
     */
    private boolean contains(String[] array, String element) {
        for (String s : array) {
            if (s.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
