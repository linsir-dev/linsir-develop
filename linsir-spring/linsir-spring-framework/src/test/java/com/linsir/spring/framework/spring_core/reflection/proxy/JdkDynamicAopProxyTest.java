package com.linsir.spring.framework.spring_core.reflection.proxy;

import com.linsir.spring.framework.spring_core.reflection.service.IUserService;
import com.linsir.spring.framework.spring_core.reflection.service.UserRepository;
import com.linsir.spring.framework.spring_core.reflection.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JdkDynamicAopProxy 测试类
 * 测试 JDK 动态代理的功能
 */
@DisplayName("JdkDynamicAopProxy JDK动态代理测试")
class JdkDynamicAopProxyTest {

    private UserService userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = new UserRepository();
        userService = new UserService();
        userService.setUserRepository(userRepository);
    }

    @Test
    @DisplayName("测试创建代理对象")
    void testGetProxy() {
        // 创建代理
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        Object proxyObject = proxy.getProxy();

        // 验证代理对象
        assertNotNull(proxyObject, "应该创建代理对象");
        assertTrue(proxyObject instanceof IUserService, "代理对象应该实现 IUserService 接口");
    }

    @Test
    @DisplayName("测试代理方法调用 - 公共方法")
    void testProxyMethodInvocation_Public() {
        // 创建代理
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        IUserService proxyService = (IUserService) proxy.getProxy();

        // 调用代理方法
        String result = proxyService.getServiceInfo();

        // 验证结果
        assertEquals("UserService - User Management Service", result, "应该返回正确的服务信息");
    }

    @Test
    @DisplayName("测试代理方法调用 - 带事务注解的方法")
    void testProxyMethodInvocation_Transactional() {
        // 创建代理
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        IUserService proxyService = (IUserService) proxy.getProxy();

        // 调用带事务注解的方法
        assertDoesNotThrow(() -> {
            proxyService.findById(1L);
        }, "应该成功调用带事务注解的方法");
    }

    @Test
    @DisplayName("测试代理 - 不实现接口的类应该抛出异常")
    void testGetProxy_NoInterface() {
        // 创建一个不实现接口的类
        class NoInterfaceClass {
            public void doSomething() {}
        }

        NoInterfaceClass target = new NoInterfaceClass();
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(target);

        // 应该抛出异常
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            proxy::getProxy,
            "不实现接口的类应该抛出异常"
        );

        assertTrue(exception.getMessage().contains("does not implement any interfaces"), 
                   "异常信息应该提示没有实现接口");
    }

    @Test
    @DisplayName("测试代理对象类型")
    void testProxyObjectType() {
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        Object proxyObject = proxy.getProxy();

        // 验证代理对象的类型特征
        assertTrue(java.lang.reflect.Proxy.isProxyClass(proxyObject.getClass()), 
                   "应该是 JDK 动态代理类");
    }

    @Test
    @DisplayName("测试代理方法调用 - 使用指定类加载器")
    void testGetProxyWithClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        JdkDynamicAopProxy proxy = new JdkDynamicAopProxy(userService);
        Object proxyObject = proxy.getProxy(classLoader);

        assertNotNull(proxyObject, "应该使用指定类加载器创建代理");
    }
}
