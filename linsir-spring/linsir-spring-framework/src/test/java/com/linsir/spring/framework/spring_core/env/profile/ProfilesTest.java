package com.linsir.spring.framework.spring_core.env.profile;

import com.linsir.spring.framework.spring_core.env.core.ConfigurableEnvironment;
import com.linsir.spring.framework.spring_core.env.support.StandardEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Profiles 测试类
 *
 * 测试 Profiles 接口的各种功能
 *
 * @author linsir
 * @since 1.0.0
 */
class ProfilesTest {

    private ConfigurableEnvironment environment;

    @BeforeEach
    void setUp() {
        environment = new StandardEnvironment();
        environment.setActiveProfiles("dev", "test");
    }

    @Test
    void testParseSimpleProfile() {
        Profiles profiles = Profiles.parse("dev");
        assertTrue(profiles.matches(environment));

        profiles = Profiles.parse("prod");
        assertFalse(profiles.matches(environment));
    }

    @Test
    void testParseNegatedProfile() {
        Profiles profiles = Profiles.parse("!prod");
        assertTrue(profiles.matches(environment)); // prod 未激活，所以 !prod 匹配

        profiles = Profiles.parse("!dev");
        assertFalse(profiles.matches(environment)); // dev 已激活，所以 !dev 不匹配
    }

    @Test
    void testParseOrExpression() {
        Profiles profiles = Profiles.parse("dev | prod");
        assertTrue(profiles.matches(environment)); // dev 激活

        profiles = Profiles.parse("prod | staging");
        assertFalse(profiles.matches(environment)); // prod 和 staging 都未激活

        profiles = Profiles.parse("prod | dev");
        assertTrue(profiles.matches(environment)); // dev 激活
    }

    @Test
    void testParseAndExpression() {
        environment.setActiveProfiles("dev", "test", "local");

        Profiles profiles = Profiles.parse("dev & test");
        assertTrue(profiles.matches(environment)); // dev 和 test 都激活

        profiles = Profiles.parse("dev & prod");
        assertFalse(profiles.matches(environment)); // prod 未激活

        profiles = Profiles.parse("dev & test & local");
        assertTrue(profiles.matches(environment)); // 三个都激活
    }

    @Test
    void testParseComplexExpression() {
        environment.setActiveProfiles("dev", "test");

        // 复杂表达式：(dev & test) | prod
        Profiles profiles = Profiles.parse("dev & test | prod");
        assertTrue(profiles.matches(environment)); // dev & test 匹配

        environment.setActiveProfiles("prod");
        assertTrue(profiles.matches(environment)); // prod 匹配

        environment.setActiveProfiles("staging");
        assertFalse(profiles.matches(environment)); // 都不匹配
    }

    @Test
    void testParseNullOrEmpty() {
        Profiles profiles = Profiles.parse(null);
        assertFalse(profiles.matches(environment));

        profiles = Profiles.parse("");
        assertFalse(profiles.matches(environment));

        profiles = Profiles.parse("   ");
        assertFalse(profiles.matches(environment));
    }

    @Test
    void testOf() {
        Profiles profiles = Profiles.of("dev", "prod");
        assertTrue(profiles.matches(environment)); // dev 激活

        profiles = Profiles.of("prod", "staging");
        assertFalse(profiles.matches(environment)); // 都未激活

        profiles = Profiles.of();
        assertFalse(profiles.matches(environment));

        profiles = Profiles.of((String[]) null);
        assertFalse(profiles.matches(environment));
    }

    @Test
    void testAllOf() {
        environment.setActiveProfiles("dev", "test", "local");

        Profiles profiles = Profiles.allOf("dev", "test");
        assertTrue(profiles.matches(environment)); // dev 和 test 都激活

        profiles = Profiles.allOf("dev", "prod");
        assertFalse(profiles.matches(environment)); // prod 未激活

        profiles = Profiles.allOf();
        assertTrue(profiles.matches(environment)); // 空数组返回 true

        profiles = Profiles.allOf((String[]) null);
        assertTrue(profiles.matches(environment)); // null 返回 true
    }

    @Test
    void testProfileConditionIsActive() {
        assertTrue(ProfileCondition.isActive(environment, "dev"));
        assertTrue(ProfileCondition.isActive(environment, "test"));
        assertFalse(ProfileCondition.isActive(environment, "prod"));
    }

    @Test
    void testProfileConditionIsAnyActive() {
        assertTrue(ProfileCondition.isAnyActive(environment, "dev", "prod"));
        assertTrue(ProfileCondition.isAnyActive(environment, "prod", "dev"));
        assertFalse(ProfileCondition.isAnyActive(environment, "prod", "staging"));
    }

    @Test
    void testProfileConditionIsNoProfileActive() {
        ConfigurableEnvironment emptyEnv = new StandardEnvironment();
        assertTrue(ProfileCondition.isNoProfileActive(emptyEnv));

        assertFalse(ProfileCondition.isNoProfileActive(environment));
    }

    @Test
    void testProfileConditionEnvironmentChecks() {
        // 测试开发环境
        environment.setActiveProfiles("dev");
        assertTrue(ProfileCondition.isDev(environment));
        assertFalse(ProfileCondition.isProd(environment));
        assertFalse(ProfileCondition.isTest(environment));

        // 测试测试环境
        environment.setActiveProfiles("test");
        assertFalse(ProfileCondition.isDev(environment));
        assertFalse(ProfileCondition.isProd(environment));
        assertTrue(ProfileCondition.isTest(environment));

        // 测试生产环境
        environment.setActiveProfiles("prod");
        assertFalse(ProfileCondition.isDev(environment));
        assertTrue(ProfileCondition.isProd(environment));
        assertFalse(ProfileCondition.isTest(environment));

        // 测试预发布环境
        environment.setActiveProfiles("staging");
        assertFalse(ProfileCondition.isDev(environment));
        assertFalse(ProfileCondition.isProd(environment));
        assertFalse(ProfileCondition.isTest(environment));
        assertTrue(ProfileCondition.isStaging(environment));

        // 测试别名
        environment.setActiveProfiles("development");
        assertTrue(ProfileCondition.isDev(environment));

        environment.setActiveProfiles("production");
        assertTrue(ProfileCondition.isProd(environment));

        environment.setActiveProfiles("testing");
        assertTrue(ProfileCondition.isTest(environment));

        environment.setActiveProfiles("preprod");
        assertTrue(ProfileCondition.isStaging(environment));
    }
}
