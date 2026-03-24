package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhancer测试类
 *
 * <p>测试CGLIB增强器的核心功能，包括：
 * <ul>
 *   <li>代理类创建</li>
 *   <li>回调设置</li>
 *   <li>回调过滤</li>
 *   <li>方法代理映射</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class EnhancerTest {

    /**
     * 计算器接口
     */
    public interface ICalculator {
        int add(int a, int b);
        int subtract(int a, int b);
        int multiply(int a, int b);
        int divide(int a, int b);
    }

    /**
     * 测试目标类
     */
    public static class Calculator implements ICalculator {
        @Override
        public int add(int a, int b) {
            return a + b;
        }

        @Override
        public int subtract(int a, int b) {
            return a - b;
        }

        @Override
        public int multiply(int a, int b) {
            return a * b;
        }

        @Override
        public int divide(int a, int b) {
            return a / b;
        }
    }

    /**
     * 人员接口
     */
    public interface IPerson {
        String getName();
        int getAge();
    }

    /**
     * 带构造参数的测试类
     */
    public static class Person implements IPerson {
        private final String name;
        private final int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public int getAge() {
            return age;
        }
    }

    /**
     * 测试基本的代理创建
     */
    @Test
    public void testBasicProxyCreation() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Calculator.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        });

        ICalculator proxy = (ICalculator) enhancer.create();

        assertNotNull(proxy);
        assertEquals(5, proxy.add(2, 3));
        assertEquals(2, proxy.subtract(5, 3));
    }

    /**
     * 测试带构造参数的代理创建
     */
    @Test
    public void testProxyWithConstructorArgs() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Person.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        });

        IPerson proxy = (IPerson) enhancer.create(
            new Class<?>[]{String.class, int.class},
            new Object[]{"张三", 25}
        );

        assertNotNull(proxy);
        assertEquals("张三", proxy.getName());
        assertEquals(25, proxy.getAge());
    }

    /**
     * 测试回调过滤器
     */
    @Test
    public void testCallbackFilter() {
        List<String> interceptedMethods = new ArrayList<>();

        // 创建两个不同的拦截器
        MethodInterceptor loggingInterceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                interceptedMethods.add("Logging: " + method.getName());
                return proxy.invokeSuper(obj, args);
            }
        };

        MethodInterceptor timingInterceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                interceptedMethods.add("Timing: " + method.getName());
                return proxy.invokeSuper(obj, args);
            }
        };

        // 创建回调过滤器
        CallbackFilter filter = new CallbackFilter() {
            @Override
            public int accept(Method method) {
                // add和subtract使用第一个拦截器，其他使用第二个
                if (method.getName().equals("add") || method.getName().equals("subtract")) {
                    return 0;
                }
                return 1;
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Calculator.class);
        enhancer.setCallbacks(new Callback[]{loggingInterceptor, timingInterceptor});
        enhancer.setCallbackFilter(filter);

        ICalculator proxy = (ICalculator) enhancer.create();

        // 调用add - 应该使用loggingInterceptor
        proxy.add(1, 2);
        assertTrue(interceptedMethods.stream().anyMatch(m -> m.contains("Logging: add")));

        // 调用multiply - 应该使用timingInterceptor
        proxy.multiply(2, 3);
        assertTrue(interceptedMethods.stream().anyMatch(m -> m.contains("Timing: multiply")));
    }

    /**
     * 测试方法代理映射
     */
    @Test
    public void testMethodProxyMapping() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(Calculator.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        });

        // 创建代理以触发方法映射创建
        ICalculator proxy = (ICalculator) enhancer.create();

        // 验证方法代理映射不为空
        assertFalse(enhancer.getMethodProxies().isEmpty());

        // 验证add方法有对应的代理
        assertNotNull(enhancer.getMethodProxy("add(int,int)"));
    }

    /**
     * 测试设置和获取父类
     */
    @Test
    public void testSetAndGetSuperclass() {
        Enhancer enhancer = new Enhancer();
        assertNull(enhancer.getSuperclass());

        enhancer.setSuperclass(Calculator.class);
        assertEquals(Calculator.class, enhancer.getSuperclass());
    }

    /**
     * 测试设置和获取回调
     */
    @Test
    public void testSetAndGetCallback() {
        Enhancer enhancer = new Enhancer();
        assertNull(enhancer.getCallback());

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        };

        enhancer.setCallback(interceptor);
        assertNotNull(enhancer.getCallback());
        assertEquals(interceptor, enhancer.getCallback());
    }

    /**
     * 测试设置和获取回调数组
     */
    @Test
    public void testSetAndGetCallbacks() {
        Enhancer enhancer = new Enhancer();
        assertNull(enhancer.getCallbacks());

        MethodInterceptor interceptor1 = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        };

        MethodInterceptor interceptor2 = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        };

        Callback[] callbacks = new Callback[]{interceptor1, interceptor2};
        enhancer.setCallbacks(callbacks);

        assertNotNull(enhancer.getCallbacks());
        assertEquals(2, enhancer.getCallbacks().length);
        assertEquals(interceptor1, enhancer.getCallback()); // 第一个回调应该被设置为默认回调
    }

    /**
     * 测试设置和获取回调过滤器
     */
    @Test
    public void testSetAndGetCallbackFilter() {
        Enhancer enhancer = new Enhancer();
        assertNull(enhancer.getCallbackFilter());

        CallbackFilter filter = new CallbackFilter() {
            @Override
            public int accept(Method method) {
                return 0;
            }
        };

        enhancer.setCallbackFilter(filter);
        assertNotNull(enhancer.getCallbackFilter());
        assertEquals(filter, enhancer.getCallbackFilter());
    }

    /**
     * 测试设置和获取接口
     */
    @Test
    public void testSetAndGetInterfaces() {
        Enhancer enhancer = new Enhancer();
        assertNull(enhancer.getInterfaces());

        Class<?>[] interfaces = new Class<?>[]{Runnable.class};
        enhancer.setInterfaces(interfaces);

        assertNotNull(enhancer.getInterfaces());
        assertEquals(1, enhancer.getInterfaces().length);
        assertEquals(Runnable.class, enhancer.getInterfaces()[0]);
    }

    /**
     * 测试设置和获取useFactory属性
     */
    @Test
    public void testSetAndGetUseFactory() {
        Enhancer enhancer = new Enhancer();
        assertTrue(enhancer.isUseFactory()); // 默认值为true

        enhancer.setUseFactory(false);
        assertFalse(enhancer.isUseFactory());
    }

    /**
     * 测试未设置父类时创建代理抛出异常
     */
    @Test
    public void testCreateWithoutSuperclass() {
        Enhancer enhancer = new Enhancer();
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        });

        assertThrows(IllegalStateException.class, () -> {
            enhancer.create();
        });
    }

    /**
     * 测试设置接口为父类时抛出异常
     */
    @Test
    public void testSetInterfaceAsSuperclass() {
        Enhancer enhancer = new Enhancer();

        assertThrows(IllegalArgumentException.class, () -> {
            enhancer.setSuperclass(Runnable.class);
        });
    }

    /**
     * 测试设置final类为父类时抛出异常
     */
    @Test
    public void testSetFinalClassAsSuperclass() {
        Enhancer enhancer = new Enhancer();

        assertThrows(IllegalArgumentException.class, () -> {
            enhancer.setSuperclass(String.class);
        });
    }
}
