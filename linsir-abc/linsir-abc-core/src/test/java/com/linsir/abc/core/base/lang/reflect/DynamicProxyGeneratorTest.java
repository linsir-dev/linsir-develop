package com.linsir.abc.core.base.lang.reflect;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DynamicProxyGenerator测试类
 */
public class DynamicProxyGeneratorTest {

    /**
     * 测试创建日志代理
     */
    @Test
    public void testCreateLoggingProxy() {
        DynamicProxyGenerator generator = new DynamicProxyGenerator();

        DynamicProxyGenerator.UserService userService = new DynamicProxyGenerator.UserServiceImpl();
        DynamicProxyGenerator.UserService proxy = generator.createProxy(userService);

        assertNotNull(proxy);

        String result = proxy.getUserById(1L);
        assertEquals("User-1", result);
    }

    /**
     * 测试创建性能代理
     */
    @Test
    public void testCreatePerformanceProxy() {
        DynamicProxyGenerator generator = new DynamicProxyGenerator();

        DynamicProxyGenerator.Calculator calculator = new DynamicProxyGenerator.CalculatorImpl();
        DynamicProxyGenerator.Calculator proxy = generator.createPerformanceProxy(calculator);

        assertNotNull(proxy);

        int result = proxy.add(10, 20);
        assertEquals(30, result);
    }

    /**
     * 测试创建事务代理
     */
    @Test
    public void testCreateTransactionProxy() {
        DynamicProxyGenerator generator = new DynamicProxyGenerator();

        DynamicProxyGenerator.UserService userService = new DynamicProxyGenerator.UserServiceImpl();
        DynamicProxyGenerator.UserService proxy = generator.createTransactionProxy(userService);

        assertNotNull(proxy);

        // 没有@Transactional注解的方法
        proxy.getUserById(1L);

        // 有@Transactional注解的方法
        proxy.saveUser("Test");
    }

    /**
     * 测试代理对象类型
     */
    @Test
    public void testProxyType() {
        DynamicProxyGenerator generator = new DynamicProxyGenerator();

        DynamicProxyGenerator.UserService userService = new DynamicProxyGenerator.UserServiceImpl();
        DynamicProxyGenerator.UserService proxy = generator.createProxy(userService);

        assertTrue(java.lang.reflect.Proxy.isProxyClass(proxy.getClass()));

        Class<?>[] interfaces = proxy.getClass().getInterfaces();
        assertTrue(interfaces.length > 0);
    }

    /**
     * 测试计算器代理操作
     */
    @Test
    public void testCalculatorProxy() {
        DynamicProxyGenerator generator = new DynamicProxyGenerator();

        DynamicProxyGenerator.Calculator calculator = new DynamicProxyGenerator.CalculatorImpl();
        DynamicProxyGenerator.Calculator proxy = generator.createProxy(calculator);

        assertEquals(30, proxy.add(10, 20));
        assertEquals(10, proxy.subtract(20, 10));
        assertEquals(200, proxy.multiply(20, 10));
        assertEquals(2.0, proxy.divide(20, 10), 0.001);
    }

    /**
     * 测试代理异常处理
     */
    @Test(expected = ArithmeticException.class)
    public void testProxyException() {
        DynamicProxyGenerator generator = new DynamicProxyGenerator();

        DynamicProxyGenerator.Calculator calculator = new DynamicProxyGenerator.CalculatorImpl();
        DynamicProxyGenerator.Calculator proxy = generator.createProxy(calculator);

        proxy.divide(10, 0);
    }
}
