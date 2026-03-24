package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

import java.lang.reflect.Method;

/**
 * 方法代理类
 *
 * <p>用于在拦截器中调用父类（被代理类）的原始方法实现。
 * 通过方法索引优化调用性能，避免反射带来的开销。
 *
 * <p>每个被代理的方法都会生成一个对应的 MethodProxy 实例，
 * 包含两个方法索引：
 * <ul>
 *   <li>sig1 - 用于调用父类方法（非代理方法）</li>
 *   <li>sig2 - 用于调用代理方法</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) {
 *     // 调用父类原始方法
 *     return proxy.invokeSuper(obj, args);
 * }
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see MethodInterceptor
 * @see Enhancer
 */
public class MethodProxy {

    /**
     * 方法签名，用于唯一标识一个方法
     */
    private final String signature;

    /**
     * 被代理的方法
     */
    private final Method method;

    /**
     * 方法在FastClass中的索引
     */
    private final int methodIndex;

    /**
     * 父类FastClass的方法索引
     */
    private final int superMethodIndex;

    /**
     * 被代理的类
     */
    private final Class<?> targetClass;

    /**
     * 代理类
     */
    private final Class<?> proxyClass;

    /**
     * 构造函数
     *
     * @param signature 方法签名
     * @param method 被代理的方法
     * @param methodIndex 方法索引
     * @param superMethodIndex 父类方法索引
     * @param targetClass 被代理的类
     * @param proxyClass 代理类
     */
    public MethodProxy(String signature, Method method, int methodIndex,
                       int superMethodIndex, Class<?> targetClass, Class<?> proxyClass) {
        this.signature = signature;
        this.method = method;
        this.methodIndex = methodIndex;
        this.superMethodIndex = superMethodIndex;
        this.targetClass = targetClass;
        this.proxyClass = proxyClass;
    }

    /**
     * 调用父类（被代理类）的原始方法
     *
     * <p>这是拦截器中最常用的方法，用于执行被代理方法的原始逻辑。
     *
     * @param obj 代理对象实例
     * @param args 方法参数
     * @return 方法返回值
     * @throws Throwable 可能抛出的异常
     */
    public Object invokeSuper(Object obj, Object[] args) throws Throwable {
        // 实际实现中会通过FastClass调用
        // 这里简化处理，直接通过反射调用
        return method.invoke(obj, args);
    }

    /**
     * 调用指定对象的方法
     *
     * <p>与invokeSuper不同，此方法直接调用指定对象的方法，不经过代理逻辑。
     *
     * @param obj 目标对象实例
     * @param args 方法参数
     * @return 方法返回值
     * @throws Throwable 可能抛出的异常
     */
    public Object invoke(Object obj, Object[] args) throws Throwable {
        // 直接通过反射调用目标对象的方法
        return method.invoke(obj, args);
    }

    /**
     * 获取方法签名
     *
     * @return 方法签名字符串
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 获取被代理的方法
     *
     * @return 方法对象
     */
    public Method getMethod() {
        return method;
    }

    /**
     * 获取方法索引
     *
     * @return 方法在FastClass中的索引
     */
    public int getMethodIndex() {
        return methodIndex;
    }

    /**
     * 获取父类方法索引
     *
     * @return 父类方法索引
     */
    public int getSuperMethodIndex() {
        return superMethodIndex;
    }

    /**
     * 获取被代理的类
     *
     * @return 目标类
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * 获取代理类
     *
     * @return 代理类
     */
    public Class<?> getProxyClass() {
        return proxyClass;
    }

    @Override
    public String toString() {
        return "MethodProxy{" +
                "signature='" + signature + '\'' +
                ", method=" + method +
                ", targetClass=" + targetClass.getName() +
                '}';
    }
}
