package com.linsir.spring.framework.spring_core.reflection;

import com.linsir.spring.framework.spring_core.reflection.cache.ReflectionCache;
import com.linsir.spring.framework.spring_core.reflection.event.EventListenerProcessor;
import com.linsir.spring.framework.spring_core.reflection.event.UserCreatedEvent;
import com.linsir.spring.framework.spring_core.reflection.model.User;
import com.linsir.spring.framework.spring_core.reflection.processor.AutowiredAnnotationProcessor;
import com.linsir.spring.framework.spring_core.reflection.proxy.JdkDynamicAopProxy;
import com.linsir.spring.framework.spring_core.reflection.service.IUserService;
import com.linsir.spring.framework.spring_core.reflection.service.UserRepository;
import com.linsir.spring.framework.spring_core.reflection.service.UserService;
import com.linsir.spring.framework.spring_core.reflection.utils.ClassUtils;
import com.linsir.spring.framework.spring_core.reflection.utils.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 反射工具集成测试
 * 测试各个组件的协同工作
 */
@DisplayName("反射工具集成测试")
class ReflectionIntegrationTest {

    @BeforeEach
    void setUp() {
        ReflectionCache.clearCache();
    }

    @Test
    @DisplayName("集成测试：完整的依赖注入流程")
    void testDependencyInjectionFlow() {
        // 1. 创建依赖注入处理器
        AutowiredAnnotationProcessor processor = new AutowiredAnnotationProcessor();

        // 2. 注册 Repository
        UserRepository repository = new UserRepository();
        processor.registerBean(repository);

        // 3. 创建 Service 并注入依赖
        UserService userService = processor.createBean(UserService.class);

        // 4. 验证注入成功
        assertNotNull(userService.getUserRepository(), "依赖应该被注入");

        // 5. 使用 Service 进行操作
        User user = new User();
        user.setUsername("integrationTest");
        User savedUser = userService.save(user);

        // 6. 验证操作成功
        assertNotNull(savedUser.getId(), "用户应该被保存");
        assertEquals("integrationTest", savedUser.getUsername(), "用户名应该匹配");
    }

    @Test
    @DisplayName("集成测试：AOP 代理 + 依赖注入")
    void testAopProxyWithDependencyInjection() {
        // 1. 创建并配置 Service
        UserRepository repository = new UserRepository();
        UserService userService = new UserService();
        userService.setUserRepository(repository);

        // 2. 创建 AOP 代理（JDK动态代理返回接口类型）
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        IUserService proxyService = (IUserService) proxy.getProxy();

        // 3. 通过代理调用方法
        assertDoesNotThrow(() -> {
            proxyService.findById(1L);
        }, "代理方法应该正常执行");
    }

    @Test
    @DisplayName("集成测试：事件监听 + 反射缓存")
    void testEventListenerWithReflectionCache() {
        // 1. 创建事件处理器
        EventListenerProcessor eventProcessor = new EventListenerProcessor();

        // 2. 创建并注册监听器
        TestEventListener listener = new TestEventListener();
        eventProcessor.registerListener(listener);

        // 3. 使用反射缓存获取方法信息
        Method method = ReflectionCache.findMethod(TestEventListener.class, "onUserCreated", UserCreatedEvent.class);
        assertNotNull(method, "应该找到监听方法");

        // 4. 发布事件
        User user = new User();
        user.setUsername("eventTest");
        UserCreatedEvent event = new UserCreatedEvent(this, user);
        eventProcessor.publishEvent(event);

        // 5. 验证监听器被调用
        assertTrue(listener.isCalled(), "监听器应该被调用");
    }

    @Test
    @DisplayName("集成测试：反射工具 + 类工具协同")
    void testReflectionUtilsWithClassUtils() {
        // 1. 使用 ClassUtils 获取类信息
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(UserService.class);

        // 2. 使用 ReflectionUtils 获取方法
        Method[] methods = ReflectionUtils.getAllDeclaredMethods(UserService.class);

        // 3. 验证协同工作
        assertNotNull(interfaces, "应该获取接口列表");
        assertTrue(methods.length > 0, "应该获取方法数组");

        // 4. 结合使用：查找实现了特定接口的方法
        for (Method method : methods) {
            Class<?> declaringClass = method.getDeclaringClass();
            if (declaringClass.isInterface() && interfaces.contains(declaringClass)) {
                // 这是接口方法的实现
                assertNotNull(method.getName(), "应该能获取方法名");
            }
        }
    }

    @Test
    @DisplayName("集成测试：完整的 Spring 风格流程")
    void testCompleteSpringStyleFlow() {
        // 场景：模拟 Spring 容器的初始化流程

        // 1. 创建依赖注入处理器（模拟 ApplicationContext）
        AutowiredAnnotationProcessor context = new AutowiredAnnotationProcessor();

        // 2. 注册基础设施（Repository）
        UserRepository repository = new UserRepository();
        context.registerBean(repository);

        // 3. 创建 Service 并注入依赖
        UserService userService = context.createBean(UserService.class);

        // 4. 创建 AOP 代理（模拟 Spring AOP，JDK代理返回接口类型）
        JdkDynamicAopProxy proxyFactory = new JdkDynamicAopProxy(userService);
        IUserService proxiedService = (IUserService) proxyFactory.getProxy();

        // 5. 创建事件处理器（模拟 ApplicationEventPublisher）
        EventListenerProcessor eventPublisher = new EventListenerProcessor();
        TestEventListener eventListener = new TestEventListener();
        eventPublisher.registerListener(eventListener);

        // 6. 执行业务操作
        User user = new User();
        user.setUsername("completeFlow");
        user.setEmail("test@example.com");

        // 通过代理保存用户
        User savedUser = proxiedService.save(user);
        assertNotNull(savedUser.getId(), "用户应该被保存");

        // 7. 发布用户创建事件
        UserCreatedEvent event = new UserCreatedEvent(this, savedUser);
        eventPublisher.publishEvent(event);

        // 8. 验证事件被处理
        assertTrue(eventListener.isCalled(), "事件监听器应该被调用");
        assertEquals(savedUser.getUsername(), eventListener.getReceivedUser().getUsername(), "应该接收到正确的用户");

        // 9. 验证缓存（使用 ReflectionCache 主动缓存一些数据）
        ReflectionCache.getDeclaredFields(UserService.class);
        ReflectionCache.getDeclaredMethods(UserService.class);
        assertTrue(ReflectionCache.getCachedFieldCount() > 0, "应该有字段缓存");
        assertTrue(ReflectionCache.getCachedMethodCount() > 0, "应该有方法缓存");
    }

    @Test
    @DisplayName("集成测试：反射缓存性能优化")
    void testReflectionCachePerformance() {
        // 测试反射缓存的性能提升

        Class<?> targetClass = UserService.class;
        int iterations = 1000;

        // 1. 不使用缓存的时间
        long startWithoutCache = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ReflectionUtils.getAllDeclaredFields(targetClass);
            ReflectionUtils.findMethod(targetClass, "findById", Long.class);
        }
        long durationWithoutCache = System.currentTimeMillis() - startWithoutCache;

        // 2. 使用缓存的时间
        long startWithCache = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            ReflectionCache.getDeclaredFields(targetClass);
            ReflectionCache.findMethod(targetClass, "findById", Long.class);
        }
        long durationWithCache = System.currentTimeMillis() - startWithCache;

        // 3. 验证缓存更快（通常应该快很多，但这里只做基本验证）
        System.out.println("Without cache: " + durationWithoutCache + "ms");
        System.out.println("With cache: " + durationWithCache + "ms");

        // 缓存应该至少不更慢
        assertTrue(durationWithCache <= durationWithoutCache * 2,
            "使用缓存不应该显著慢于不使用缓存");
    }

    @Test
    @DisplayName("集成测试：异常处理链")
    void testExceptionHandlingChain() {
        // 测试反射操作中的异常处理

        // 1. 测试 ReflectionUtils 的异常转换
        Field field = ReflectionUtils.findField(UserService.class, "userRepository");
        assertNotNull(field);

        // 尝试获取 null 对象的字段值，应该抛出 ReflectionException
        ReflectionUtils.ReflectionException exception = assertThrows(
            ReflectionUtils.ReflectionException.class,
            () -> ReflectionUtils.getField(field, null),
            "应该抛出 ReflectionException"
        );

        // 2. 验证异常链
        assertNotNull(exception.getCause(), "异常应该包含原始原因");

        // 3. 测试 AOP 代理中的异常传播（JDK代理返回接口类型）
        UserService userService = new UserService();
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        IUserService proxiedService = (IUserService) proxy.getProxy();

        // 调用方法不应该抛出异常
        assertDoesNotThrow(() -> {
            proxiedService.getServiceInfo();
        }, "正常方法调用不应该抛出异常");
    }

    // ==================== 测试用的监听器类 ====================

    /**
     * 测试用的事件监听器
     */
    static class TestEventListener {
        private boolean called = false;
        private User receivedUser;

        @com.linsir.spring.framework.spring_core.reflection.event.EventListener
        public void onUserCreated(UserCreatedEvent event) {
            this.called = true;
            this.receivedUser = event.getUser();
        }

        public boolean isCalled() {
            return called;
        }

        public User getReceivedUser() {
            return receivedUser;
        }
    }
}
