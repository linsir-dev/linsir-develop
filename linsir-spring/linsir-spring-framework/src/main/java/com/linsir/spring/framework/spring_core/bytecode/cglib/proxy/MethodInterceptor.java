package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

import java.lang.reflect.Method;

/**
 * 方法拦截器接口
 * 
 * <p>CGLIB代理的核心回调接口，用于拦截目标类的方法调用。
 * 实现此接口可以在方法调用前后插入自定义逻辑，如事务管理、日志记录、性能监控等。
 * 
 * <p>使用示例：
 * <pre>{@code
 * public class LoggingInterceptor implements MethodInterceptor {
 *     @Override
 *     public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
 *         System.out.println("方法调用前: " + method.getName());
 *         Object result = proxy.invokeSuper(obj, args);
 *         System.out.println("方法调用后: " + method.getName());
 *         return result;
 *     }
 * }
 * }</pre>
 * 
 * @author linsir
 * @since 1.0
 * @see Callback
 * @see MethodProxy
 * @see Enhancer
 */
public interface MethodInterceptor extends Callback {
    
    /**
     * 拦截方法调用
     * 
     * <p>在代理对象的方法被调用时执行。可以通过 {@link MethodProxy#invokeSuper} 
     * 调用父类（被代理类）的原始方法实现。
     * 
     * @param obj 代理对象实例
     * @param method 被调用的方法
     * @param args 方法参数数组
     * @param proxy 方法代理对象，用于调用父类方法
     * @return 方法返回值
     * @throws Throwable 可能抛出的异常
     */
    Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable;
}
