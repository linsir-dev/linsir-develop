package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * MethodInterceptor测试类
 *
 * <p>测试CGLIB方法拦截器的核心功能，包括：
 * <ul>
 *   <li>方法拦截</li>
 *   <li>前置/后置处理</li>
 *   <li>异常处理</li>
 *   <li>返回值修改</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 */
public class MethodInterceptorTest {

    /**
     * 用户服务接口
     */
    public interface IUserService {
        String getUserName();
        void setUserName(String userName);
        String sayHello(String name);
        int add(int a, int b);
        void throwException();
    }

    /**
     * 测试目标类
     */
    public static class UserService implements IUserService {
        private String userName;

        public UserService() {
        }

        public UserService(String userName) {
            this.userName = userName;
        }

        @Override
        public String getUserName() {
            return userName;
        }

        @Override
        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public String sayHello(String name) {
            return "Hello, " + name;
        }

        @Override
        public int add(int a, int b) {
            return a + b;
        }

        @Override
        public void throwException() {
            throw new RuntimeException("测试异常");
        }
    }

    /**
     * 测试简单的拦截器
     */
    @Test
    public void testSimpleInterceptor() {
        // 记录拦截的方法
        List<String> interceptedMethods = new ArrayList<>();

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                interceptedMethods.add(method.getName());
                // 调用父类方法
                return proxy.invokeSuper(obj, args);
            }
        };

        // 创建代理
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();

        // 调用方法
        proxy.setUserName("张三");
        String result = proxy.getUserName();

        // 验证
        assertEquals("张三", result);
        assertTrue(interceptedMethods.contains("setUserName"));
        assertTrue(interceptedMethods.contains("getUserName"));
    }

    /**
     * 测试前置处理
     */
    @Test
    public void testBeforeAdvice() {
        List<String> logs = new ArrayList<>();

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                logs.add("Before: " + method.getName());
                Object result = proxy.invokeSuper(obj, args);
                return result;
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();
        proxy.sayHello("李四");

        assertTrue(logs.contains("Before: sayHello"));
    }

    /**
     * 测试后置处理
     */
    @Test
    public void testAfterAdvice() {
        List<String> logs = new ArrayList<>();

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                Object result = proxy.invokeSuper(obj, args);
                logs.add("After: " + method.getName() + ", result=" + result);
                return result;
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();
        String result = proxy.sayHello("王五");

        assertEquals("Hello, 王五", result);
        assertTrue(logs.stream().anyMatch(log -> log.contains("After: sayHello")));
    }

    /**
     * 测试返回值修改
     */
    @Test
    public void testReturnValueModification() {
        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                Object result = proxy.invokeSuper(obj, args);
                if (result instanceof String) {
                    return "[Modified] " + result;
                }
                return result;
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();
        String result = proxy.sayHello("赵六");

        assertEquals("[Modified] Hello, 赵六", result);
    }

    /**
     * 测试参数修改
     */
    @Test
    public void testArgumentModification() {
        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                if (args != null && args.length > 0 && args[0] instanceof String) {
                    args[0] = "Mr. " + args[0];
                }
                return proxy.invokeSuper(obj, args);
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();
        String result = proxy.sayHello("钱七");

        assertEquals("Hello, Mr. 钱七", result);
    }

    /**
     * 测试异常处理
     */
    @Test
    public void testExceptionHandling() {
        List<String> logs = new ArrayList<>();

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                try {
                    return proxy.invokeSuper(obj, args);
                } catch (Exception e) {
                    logs.add("Exception caught: " + e.getMessage());
                    throw new RuntimeException("包装异常: " + e.getMessage(), e);
                }
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();

        Exception exception = assertThrows(RuntimeException.class, () -> {
            proxy.throwException();
        });

        assertTrue(exception.getMessage().contains("包装异常"));
        assertTrue(logs.stream().anyMatch(log -> log.contains("Exception caught")));
    }

    /**
     * 测试环绕通知
     */
    @Test
    public void testAroundAdvice() {
        List<String> logs = new ArrayList<>();

        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                logs.add("Before: " + method.getName());
                long startTime = System.currentTimeMillis();

                Object result = proxy.invokeSuper(obj, args);

                long endTime = System.currentTimeMillis();
                logs.add("After: " + method.getName() + ", time=" + (endTime - startTime) + "ms");

                return result;
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        IUserService proxy = (IUserService) enhancer.create();
        int result = proxy.add(10, 20);

        assertEquals(30, result);
        assertTrue(logs.stream().anyMatch(log -> log.contains("Before: add")));
        assertTrue(logs.stream().anyMatch(log -> log.contains("After: add")));
    }

    /**
     * 测试方法代理的基本功能
     */
    @Test
    public void testMethodProxy() throws Throwable {
        UserService target = new UserService();
        target.setUserName("测试用户");

        Method method = UserService.class.getMethod("getUserName");
        MethodProxy proxy = new MethodProxy(
            "getUserName()",
            method,
            0,
            1,
            UserService.class,
            null
        );

        Object result = proxy.invokeSuper(target, null);

        assertEquals("测试用户", result);
        assertEquals("getUserName()", proxy.getSignature());
        assertEquals(UserService.class, proxy.getTargetClass());
        assertEquals(0, proxy.getMethodIndex());
        assertEquals(1, proxy.getSuperMethodIndex());
    }

    /**
     * 测试带参数的方法代理
     */
    @Test
    public void testMethodProxyWithArgs() throws Throwable {
        UserService target = new UserService();

        Method method = UserService.class.getMethod("sayHello", String.class);
        MethodProxy proxy = new MethodProxy(
            "sayHello(java.lang.String)",
            method,
            0,
            1,
            UserService.class,
            null
        );

        Object result = proxy.invokeSuper(target, new Object[]{"世界"});

        assertEquals("Hello, 世界", result);
        assertEquals("sayHello(java.lang.String)", proxy.getSignature());
    }

    /**
     * 测试方法代理的invoke方法
     */
    @Test
    public void testMethodProxyInvoke() throws Throwable {
        UserService target = new UserService();
        target.setUserName("测试");

        Method method = UserService.class.getMethod("getUserName");
        MethodProxy proxy = new MethodProxy(
            "getUserName()",
            method,
            0,
            1,
            UserService.class,
            null
        );

        // invoke方法应该直接调用目标方法
        Object result = proxy.invoke(target, null);

        assertEquals("测试", result);
    }

    /**
     * 测试拦截器链
     */
    @Test
    public void testInterceptorChain() {
        List<String> logs = new ArrayList<>();

        // 第一个拦截器
        MethodInterceptor interceptor1 = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                logs.add("Interceptor1 - Before");
                Object result = proxy.invokeSuper(obj, args);
                logs.add("Interceptor1 - After");
                return result;
            }
        };

        // 创建代理
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor1);

        IUserService proxy = (IUserService) enhancer.create();
        String result = proxy.sayHello("链式测试");

        assertEquals("Hello, 链式测试", result);
        assertTrue(logs.contains("Interceptor1 - Before"));
        assertTrue(logs.contains("Interceptor1 - After"));
    }

    /**
     * 测试带构造参数的代理创建
     */
    @Test
    public void testCreateWithConstructorArgs() {
        MethodInterceptor interceptor = new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        };

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(UserService.class);
        enhancer.setCallback(interceptor);

        // 使用带参数的构造函数
        IUserService proxy = (IUserService) enhancer.create(
            new Class<?>[]{String.class},
            new Object[]{"构造参数用户"}
        );

        assertEquals("构造参数用户", proxy.getUserName());
    }
}
