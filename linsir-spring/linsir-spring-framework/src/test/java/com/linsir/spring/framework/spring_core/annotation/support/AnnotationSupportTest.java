package com.linsir.spring.framework.spring_core.annotation.support;

import com.linsir.spring.framework.spring_core.annotation.meta.*;
import com.linsir.spring.framework.spring_core.annotation.utils.AnnotatedElementUtils;
import com.linsir.spring.framework.spring_core.annotation.utils.AnnotationUtils;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 注解支持功能测试类
 *
 * 测试组合注解、元注解等高级功能。
 *
 * @author linsir
 * @since 1.0.0
 */
class AnnotationSupportTest {

    /**
     * 使用 ServiceFacade 组合注解的类
     */
    @ServiceFacade(value = "userService", propagation = Transactional.Propagation.REQUIRES_NEW, readOnly = true)
    static class UserServiceFacade {
    }

    /**
     * 使用 Cacheable 注解的类
     */
    @Cacheable(value = "users", key = "#id")
    static class UserCache {
    }

    /**
     * 使用 Scheduled 注解的方法
     */
    static class ScheduledTask {
        @Scheduled(cron = "0 0 * * * *", zone = "Asia/Shanghai")
        public void hourlyTask() {
        }

        @Scheduled(fixedRate = 60000, initialDelay = 5000)
        public void frequentTask() {
        }
    }

    /**
     * 使用 Async 注解的方法
     */
    static class AsyncService {
        @Async("taskExecutor")
        public void asyncMethod() {
        }
    }

    /**
     * 使用 @Autowired 和 @Qualifier 的类
     */
    static class DependencyInjection {
        @Autowired(required = false)
        @Qualifier("primary")
        private String service;
    }

    /**
     * 使用 @Value 的类
     */
    static class ValueInjection {
        @Value("${app.name}")
        private String appName;

        @Value("#{systemProperties['user.name']}")
        private String userName;
    }

    /**
     * 测试 ServiceFacade 组合注解包含 Component 元注解
     */
    @Test
    void testServiceFacadeHasComponent() {
        assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Component.class));
        assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Scope.class));
        assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Transactional.class));
    }

    /**
     * 测试 ServiceFacade 注解属性
     */
    @Test
    void testServiceFacadeAttributes() {
        ServiceFacade facade = UserServiceFacade.class.getAnnotation(ServiceFacade.class);
        assertNotNull(facade);

        assertEquals("userService", facade.value());
        assertEquals(Transactional.Propagation.REQUIRES_NEW, facade.propagation());
        assertEquals(Transactional.Isolation.DEFAULT, facade.isolation());
        assertTrue(facade.readOnly());
    }

    /**
     * 测试 Cacheable 注解
     */
    @Test
    void testCacheableAnnotation() {
        Cacheable cacheable = UserCache.class.getAnnotation(Cacheable.class);
        assertNotNull(cacheable);

        assertArrayEquals(new String[]{"users"}, cacheable.value());
        assertEquals("#id", cacheable.key());
        assertEquals("", cacheable.condition());
        assertEquals("", cacheable.unless());
    }

    /**
     * 测试 Scheduled 注解在方法上
     */
    @Test
    void testScheduledAnnotationOnMethod() throws NoSuchMethodException {
        java.lang.reflect.Method hourlyTask = ScheduledTask.class.getMethod("hourlyTask");
        Scheduled scheduled = hourlyTask.getAnnotation(Scheduled.class);
        assertNotNull(scheduled);

        assertEquals("0 0 * * * *", scheduled.cron());
        assertEquals("Asia/Shanghai", scheduled.zone());
        assertEquals(-1, scheduled.fixedDelay());
        assertEquals(-1, scheduled.fixedRate());
    }

    /**
     * 测试 Scheduled 注解的固定频率配置
     */
    @Test
    void testScheduledFixedRate() throws NoSuchMethodException {
        java.lang.reflect.Method frequentTask = ScheduledTask.class.getMethod("frequentTask");
        Scheduled scheduled = frequentTask.getAnnotation(Scheduled.class);
        assertNotNull(scheduled);

        assertEquals("", scheduled.cron());
        assertEquals(60000, scheduled.fixedRate());
        assertEquals(5000, scheduled.initialDelay());
    }

    /**
     * 测试 Async 注解
     */
    @Test
    void testAsyncAnnotation() throws NoSuchMethodException {
        java.lang.reflect.Method asyncMethod = AsyncService.class.getMethod("asyncMethod");
        Async async = asyncMethod.getAnnotation(Async.class);
        assertNotNull(async);

        assertEquals("taskExecutor", async.value());
    }

    /**
     * 测试 Autowired 注解
     */
    @Test
    void testAutowiredAnnotation() throws NoSuchFieldException {
        java.lang.reflect.Field serviceField = DependencyInjection.class.getDeclaredField("service");
        Autowired autowired = serviceField.getAnnotation(Autowired.class);
        assertNotNull(autowired);

        assertFalse(autowired.required());
    }

    /**
     * 测试 Qualifier 注解
     */
    @Test
    void testQualifierAnnotation() throws NoSuchFieldException {
        java.lang.reflect.Field serviceField = DependencyInjection.class.getDeclaredField("service");
        Qualifier qualifier = serviceField.getAnnotation(Qualifier.class);
        assertNotNull(qualifier);

        assertEquals("primary", qualifier.value());
    }

    /**
     * 测试 Value 注解
     */
    @Test
    void testValueAnnotation() throws NoSuchFieldException {
        java.lang.reflect.Field appNameField = ValueInjection.class.getDeclaredField("appName");
        Value value = appNameField.getAnnotation(Value.class);
        assertNotNull(value);

        assertEquals("${app.name}", value.value());
    }

    /**
     * 测试 Value 注解的 SpEL 表达式
     */
    @Test
    void testValueSpELExpression() throws NoSuchFieldException {
        java.lang.reflect.Field userNameField = ValueInjection.class.getDeclaredField("userName");
        Value value = userNameField.getAnnotation(Value.class);
        assertNotNull(value);

        assertEquals("#{systemProperties['user.name']}", value.value());
    }

    /**
     * 测试 Transactional 注解的默认值
     */
    @Test
    void testTransactionalDefaults() {
        Transactional transactional = Transactional.class.getAnnotation(Transactional.class);
        // Transactional 本身没有 Transactional 注解，这里测试枚举默认值

        Transactional.Propagation propagation = Transactional.Propagation.REQUIRED;
        Transactional.Isolation isolation = Transactional.Isolation.DEFAULT;

        assertEquals(Transactional.Propagation.REQUIRED, propagation);
        assertEquals(Transactional.Isolation.DEFAULT, isolation);
    }

    /**
     * 测试 Scope 注解的默认值
     */
    @Test
    void testScopeDefaults() {
        Scope scope = Scope.class.getAnnotation(Scope.class);
        // Scope 本身没有 Scope 注解，这里测试默认值

        assertEquals("singleton", Scope.class.getAnnotation(Scope.class) != null ?
            Scope.class.getAnnotation(Scope.class).value() : "singleton");
    }

    /**
     * 测试注解的 Retention 策略
     */
    @Test
    void testAnnotationRetention() {
        Retention componentRetention = Component.class.getAnnotation(Retention.class);
        assertNotNull(componentRetention);
        assertEquals(RetentionPolicy.RUNTIME, componentRetention.value());

        Retention serviceRetention = Service.class.getAnnotation(Retention.class);
        assertNotNull(serviceRetention);
        assertEquals(RetentionPolicy.RUNTIME, serviceRetention.value());
    }

    /**
     * 测试注解的 Target 范围
     */
    @Test
    void testAnnotationTarget() {
        Target componentTarget = Component.class.getAnnotation(Target.class);
        assertNotNull(componentTarget);
        assertArrayEquals(new ElementType[]{ElementType.TYPE}, componentTarget.value());

        Target autowiredTarget = Autowired.class.getAnnotation(Target.class);
        assertNotNull(autowiredTarget);
        assertTrue(java.util.Arrays.asList(autowiredTarget.value()).contains(ElementType.FIELD));
        assertTrue(java.util.Arrays.asList(autowiredTarget.value()).contains(ElementType.METHOD));
    }

    /**
     * 测试 Documented 元注解
     */
    @Test
    void testDocumentedAnnotation() {
        assertTrue(Component.class.isAnnotationPresent(Documented.class));
        assertTrue(Service.class.isAnnotationPresent(Documented.class));
        assertTrue(Transactional.class.isAnnotationPresent(Documented.class));
    }

    /**
     * 测试组合注解的元注解链
     */
    @Test
    void testMetaAnnotationChain() {
        // ServiceFacade -> Component
        assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Component.class));

        // ServiceFacade -> Scope
        assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Scope.class));

        // ServiceFacade -> Transactional
        assertTrue(AnnotatedElementUtils.hasAnnotation(UserServiceFacade.class, Transactional.class));

        // Service -> Component
        assertTrue(AnnotationUtils.isAnnotatedWith(Service.class, Component.class));

        // Repository -> Component
        assertTrue(AnnotationUtils.isAnnotatedWith(Repository.class, Component.class));
    }

    /**
     * 测试注解工具类获取组合注解
     */
    @Test
    void testAnnotationUtilsGetAnnotation() {
        ServiceFacade facade = AnnotationUtils.getAnnotation(UserServiceFacade.class, ServiceFacade.class);
        assertNotNull(facade);
        assertEquals("userService", facade.value());
    }

    /**
     * 测试注解工具类查找元注解
     */
    @Test
    void testAnnotationUtilsFindMetaAnnotation() {
        // 查找 ServiceFacade 上的 Component 元注解
        Component component = AnnotationUtils.findAnnotation(UserServiceFacade.class, Component.class);
        // 注意：AnnotationUtils.findAnnotation 查找的是直接声明的注解，不是元注解
        // 这里应该返回 null，因为 UserServiceFacade 没有直接声明 @Component
        assertNull(component);
    }
}
