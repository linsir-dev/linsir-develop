package com.linsir.spring.framework.spring_core.reflection.proxy;

import com.linsir.spring.framework.spring_core.reflection.model.Transactional;
import com.linsir.spring.framework.spring_core.reflection.utils.ClassUtils;
import com.linsir.spring.framework.spring_core.reflection.utils.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK 动态代理实现
 * 模拟 Spring AOP 的 JDK 动态代理机制
 *
 * 核心功能：
 * 1. 为目标对象创建 JDK 动态代理
 * 2. 拦截方法调用，添加前置/后置处理
 * 3. 支持事务注解处理
 */
public class JdkDynamicAopProxy implements AopProxy, InvocationHandler {

    /**
     * 目标对象
     */
    private final Object target;

    /**
     * 目标类
     */
    private final Class<?> targetClass;

    /**
     * 构造方法
     *
     * @param target 目标对象
     */
    public JdkDynamicAopProxy(Object target) {
        this.target = target;
        this.targetClass = target.getClass();
    }

    @Override
    public Object getProxy() {
        return getProxy(ClassUtils.getDefaultClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        // 获取目标类实现的所有接口
        Class<?>[] interfaces = ClassUtils.getAllInterfacesAsArray(targetClass);
        if (interfaces.length == 0) {
            throw new IllegalArgumentException(
                "Target class [" + targetClass.getName() + "] does not implement any interfaces"
            );
        }
        // 创建 JDK 动态代理
        return Proxy.newProxyInstance(classLoader, interfaces, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 获取目标方法
        Method targetMethod = ReflectionUtils.findMethod(targetClass, method.getName(), method.getParameterTypes());

        if (targetMethod == null) {
            // 可能是 Object 类的方法（如 toString, hashCode）
            return method.invoke(target, args);
        }

        // 检查事务注解
        boolean hasTransactional = targetMethod.isAnnotationPresent(Transactional.class) ||
                                   targetClass.isAnnotationPresent(Transactional.class);

        Object result;
        try {
            // 前置处理
            beforeAdvice(method, args);

            if (hasTransactional) {
                // 开启事务
                beginTransaction();
            }

            // 调用目标方法
            result = ReflectionUtils.invokeMethod(targetMethod, target, args);

            if (hasTransactional) {
                // 提交事务
                commitTransaction();
            }

            // 返回后处理
            afterReturningAdvice(method, args, result);

        } catch (Exception ex) {
            // 异常处理
            afterThrowingAdvice(method, args, ex);

            if (hasTransactional) {
                // 回滚事务
                rollbackTransaction();
            }

            throw ex;
        } finally {
            // 最终处理
            afterFinallyAdvice(method, args);
        }

        return result;
    }

    /**
     * 前置通知
     *
     * @param method 方法
     * @param args   参数
     */
    private void beforeAdvice(Method method, Object[] args) {
        System.out.println("[AOP] Before: " + method.getName());
    }

    /**
     * 返回后通知
     *
     * @param method 方法
     * @param args   参数
     * @param result 返回值
     */
    private void afterReturningAdvice(Method method, Object[] args, Object result) {
        System.out.println("[AOP] AfterReturning: " + method.getName() + ", result=" + result);
    }

    /**
     * 异常通知
     *
     * @param method 方法
     * @param args   参数
     * @param ex     异常
     */
    private void afterThrowingAdvice(Method method, Object[] args, Exception ex) {
        System.out.println("[AOP] AfterThrowing: " + method.getName() + ", exception=" + ex.getClass().getSimpleName());
    }

    /**
     * 最终通知
     *
     * @param method 方法
     * @param args   参数
     */
    private void afterFinallyAdvice(Method method, Object[] args) {
        System.out.println("[AOP] AfterFinally: " + method.getName());
    }

    /**
     * 开启事务
     */
    private void beginTransaction() {
        System.out.println("[Transaction] Begin transaction");
    }

    /**
     * 提交事务
     */
    private void commitTransaction() {
        System.out.println("[Transaction] Commit transaction");
    }

    /**
     * 回滚事务
     */
    private void rollbackTransaction() {
        System.out.println("[Transaction] Rollback transaction");
    }
}
