package com.linsir.abc.core.base.lang.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * 动态代理生成器
 * 
 * 本类演示Java动态代理的实现：
 * 1. JDK动态代理（基于接口）
 * 2. 代理模式的应用场景
 * 3. AOP（面向切面编程）基础
 * 
 * 动态代理原理：
 * - 在运行时动态生成代理类
 * - 代理类实现目标接口
 * - 方法调用被转发到InvocationHandler
 * 
 * 应用场景：
 * - 事务管理
 * - 日志记录
 * - 权限控制
 * - 性能监控
 * - 延迟加载
 * 
 * 限制：
 * - JDK动态代理只能代理接口
 * - 如果要代理类，需要使用CGLIB等第三方库
 * 
 * @author linsir
 * @version 1.0
 * @since 1.0
 */
public class DynamicProxyGenerator {
    
    /**
     * 为目标对象创建动态代理
     * 
     * @param target 目标对象
     * @param <T> 目标类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T createProxy(T target) {
        Class<?> clazz = target.getClass();
        
        // 获取目标对象实现的所有接口
        Class<?>[] interfaces = clazz.getInterfaces();
        
        // 创建代理对象
        return (T) Proxy.newProxyInstance(
            clazz.getClassLoader(),
            interfaces,
            new LoggingInvocationHandler(target)
        );
    }
    
    /**
     * 创建带性能监控的代理
     * 
     * @param target 目标对象
     * @param <T> 目标类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T createPerformanceProxy(T target) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            interfaces,
            new PerformanceInvocationHandler(target)
        );
    }
    
    /**
     * 创建带事务管理的代理
     * 
     * @param target 目标对象
     * @param <T> 目标类型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T createTransactionProxy(T target) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            interfaces,
            new TransactionInvocationHandler(target)
        );
    }
    
    /**
     * 创建组合多个处理器的代理
     * 
     * @param target 目标对象
     * @param handlers 处理器数组
     * @param <T> 目标类型
     * @return 代理对象
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T createChainedProxy(T target, InvocationHandler... handlers) {
        Class<?>[] interfaces = target.getClass().getInterfaces();
        
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            interfaces,
            new ChainedInvocationHandler(target, handlers)
        );
    }
    
    /**
     * 日志记录调用处理器
     */
    private static class LoggingInvocationHandler implements InvocationHandler {
        private final Object target;
        
        public LoggingInvocationHandler(Object target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            System.out.println("[日志] 调用方法: " + method.getName());
            System.out.println("[日志] 参数: " + Arrays.toString(args));
            
            long startTime = System.currentTimeMillis();
            
            try {
                Object result = method.invoke(target, args);
                System.out.println("[日志] 返回值: " + result);
                return result;
            } catch (Exception e) {
                System.out.println("[日志] 异常: " + e.getCause().getMessage());
                throw e.getCause();
            } finally {
                long endTime = System.currentTimeMillis();
                System.out.println("[日志] 执行时间: " + (endTime - startTime) + "ms");
            }
        }
    }
    
    /**
     * 性能监控调用处理器
     */
    private static class PerformanceInvocationHandler implements InvocationHandler {
        private final Object target;
        
        public PerformanceInvocationHandler(Object target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            long startTime = System.nanoTime();
            
            Object result = method.invoke(target, args);
            
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            
            System.out.println("[性能] 方法 " + method.getName() + " 执行时间: " + duration + "ns (" + 
                (duration / 1_000_000.0) + "ms)");
            
            return result;
        }
    }
    
    /**
     * 事务管理调用处理器
     */
    private static class TransactionInvocationHandler implements InvocationHandler {
        private final Object target;
        
        public TransactionInvocationHandler(Object target) {
            this.target = target;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 检查方法是否有@Transactional注解（简化处理）
            boolean hasTransactional = method.isAnnotationPresent(Transactional.class);
            
            if (!hasTransactional) {
                // 没有事务注解，直接执行
                return method.invoke(target, args);
            }
            
            System.out.println("[事务] 开始事务");
            
            try {
                Object result = method.invoke(target, args);
                System.out.println("[事务] 提交事务");
                return result;
            } catch (Exception e) {
                System.out.println("[事务] 回滚事务");
                throw e.getCause();
            }
        }
    }
    
    /**
     * 链式调用处理器
     */
    private static class ChainedInvocationHandler implements InvocationHandler {
        private final Object target;
        private final InvocationHandler[] handlers;
        
        public ChainedInvocationHandler(Object target, InvocationHandler[] handlers) {
            this.target = target;
            this.handlers = handlers;
        }
        
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 创建链式调用
            return invokeChain(0, proxy, method, args);
        }
        
        private Object invokeChain(int index, Object proxy, Method method, Object[] args) throws Throwable {
            if (index < handlers.length) {
                // 继续调用链中的下一个处理器
                final int nextIndex = index + 1;
                return handlers[index].invoke(proxy, method, args);
            } else {
                // 调用实际目标方法
                return method.invoke(target, args);
            }
        }
    }
    
    /**
     * 事务注解（简化版）
     */
    public @interface Transactional {
    }
    
    /**
     * 服务接口（用于演示）
     */
    public interface UserService {
        String getUserById(Long id);
        void saveUser(String name);
        void deleteUser(Long id);
    }
    
    /**
     * 服务实现类
     */
    public static class UserServiceImpl implements UserService {
        @Override
        public String getUserById(Long id) {
            System.out.println("执行: getUserById(" + id + ")");
            return "User-" + id;
        }
        
        @Override
        @Transactional
        public void saveUser(String name) {
            System.out.println("执行: saveUser(" + name + ")");
        }
        
        @Override
        @Transactional
        public void deleteUser(Long id) {
            System.out.println("执行: deleteUser(" + id + ")");
        }
    }
    
    /**
     * 计算器接口（用于演示）
     */
    public interface Calculator {
        int add(int a, int b);
        int subtract(int a, int b);
        int multiply(int a, int b);
        double divide(int a, int b);
    }
    
    /**
     * 计算器实现类
     */
    public static class CalculatorImpl implements Calculator {
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
        public double divide(int a, int b) {
            if (b == 0) {
                throw new ArithmeticException("除数不能为零");
            }
            return (double) a / b;
        }
    }
    
    /**
     * 演示动态代理的使用
     */
    public void demonstrateProxy() {
        System.out.println("========== 日志代理演示 ==========");
        UserService userService = new UserServiceImpl();
        UserService loggingProxy = createProxy(userService);
        
        loggingProxy.getUserById(1L);
        System.out.println();
        loggingProxy.saveUser("张三");
        
        System.out.println("\n========== 性能代理演示 ==========");
        Calculator calculator = new CalculatorImpl();
        Calculator performanceProxy = createPerformanceProxy(calculator);
        
        performanceProxy.add(10, 20);
        performanceProxy.multiply(5, 6);
        
        System.out.println("\n========== 事务代理演示 ==========");
        UserService transactionProxy = createTransactionProxy(userService);
        
        transactionProxy.saveUser("李四");
        transactionProxy.getUserById(2L); // 没有@Transactional注解
    }
    
    /**
     * 演示代理对象的类型信息
     */
    public void demonstrateProxyType() {
        UserService userService = new UserServiceImpl();
        UserService proxy = createProxy(userService);
        
        System.out.println("原始对象类型: " + userService.getClass().getName());
        System.out.println("代理对象类型: " + proxy.getClass().getName());
        System.out.println("是否为代理类: " + Proxy.isProxyClass(proxy.getClass()));
        
        // 获取代理类实现的接口
        Class<?>[] interfaces = proxy.getClass().getInterfaces();
        System.out.println("代理类实现的接口:");
        for (Class<?> iface : interfaces) {
            System.out.println("  - " + iface.getName());
        }
        
        // 获取调用处理器
        InvocationHandler handler = Proxy.getInvocationHandler(proxy);
        System.out.println("调用处理器类型: " + handler.getClass().getName());
    }
}
