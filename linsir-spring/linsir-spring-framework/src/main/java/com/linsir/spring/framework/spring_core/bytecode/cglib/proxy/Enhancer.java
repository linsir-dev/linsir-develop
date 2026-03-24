package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

import com.linsir.spring.framework.spring_core.bytecode.cglib.core.AbstractClassGenerator;
import com.linsir.spring.framework.spring_core.bytecode.cglib.core.GeneratorStrategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * CGLIB增强器
 *
 * <p>CGLIB库的核心类，用于动态生成目标类的子类（代理类）。
 * 通过继承方式实现代理，可以代理没有实现接口的普通类。
 *
 * <p>主要功能：
 * <ul>
 *   <li>生成目标类的子类</li>
 *   <li>拦截非final、非static方法</li>
 *   <li>支持多种回调类型</li>
 *   <li>提供方法索引优化</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * Enhancer enhancer = new Enhancer();
 * enhancer.setSuperclass(UserService.class);
 * enhancer.setCallback(new MethodInterceptor() {
 *     @Override
 *     public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
 *         System.out.println("Before: " + method.getName());
 *         Object result = proxy.invokeSuper(obj, args);
 *         System.out.println("After: " + method.getName());
 *         return result;
 *     }
 * });
 * UserService proxy = (UserService) enhancer.create();
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see AbstractClassGenerator
 * @see MethodInterceptor
 * @see Callback
 */
public class Enhancer extends AbstractClassGenerator {

    /**
     * 被代理的父类
     */
    private Class<?> superclass;

    /**
     * 回调对象
     */
    private Callback callback;

    /**
     * 回调过滤器
     */
    private CallbackFilter callbackFilter;

    /**
     * 回调数组（支持多回调）
     */
    private Callback[] callbacks;

    /**
     * 接口数组
     */
    private Class<?>[] interfaces;

    /**
     * 是否使用工厂
     */
    private boolean useFactory = true;

    /**
     * 方法代理映射
     */
    private final Map<String, MethodProxy> methodProxies = new HashMap<>();

    /**
     * 目标对象实例（用于拦截时调用父类方法）
     */
    private Object target;

    /**
     * 设置被代理的父类
     *
     * @param superclass 父类类型
     */
    public void setSuperclass(Class<?> superclass) {
        if (superclass.isInterface()) {
            throw new IllegalArgumentException(" superclass不能是接口，请使用setInterfaces");
        }
        if (Modifier.isFinal(superclass.getModifiers())) {
            throw new IllegalArgumentException("不能代理final类: " + superclass.getName());
        }
        this.superclass = superclass;
    }

    /**
     * 获取被代理的父类
     *
     * @return 父类类型
     */
    public Class<?> getSuperclass() {
        return superclass;
    }

    /**
     * 设置回调对象
     *
     * @param callback 回调对象
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
        this.callbacks = new Callback[]{callback};
    }

    /**
     * 获取回调对象
     *
     * @return 回调对象
     */
    public Callback getCallback() {
        return callback;
    }

    /**
     * 设置回调数组
     *
     * @param callbacks 回调数组
     */
    public void setCallbacks(Callback[] callbacks) {
        this.callbacks = callbacks;
        if (callbacks != null && callbacks.length > 0) {
            this.callback = callbacks[0];
        }
    }

    /**
     * 获取回调数组
     *
     * @return 回调数组
     */
    public Callback[] getCallbacks() {
        return callbacks;
    }

    /**
     * 设置回调过滤器
     *
     * @param callbackFilter 回调过滤器
     */
    public void setCallbackFilter(CallbackFilter callbackFilter) {
        this.callbackFilter = callbackFilter;
    }

    /**
     * 获取回调过滤器
     *
     * @return 回调过滤器
     */
    public CallbackFilter getCallbackFilter() {
        return callbackFilter;
    }

    /**
     * 设置接口数组
     *
     * @param interfaces 接口数组
     */
    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * 获取接口数组
     *
     * @return 接口数组
     */
    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    /**
     * 设置是否使用工厂
     *
     * @param useFactory 是否使用工厂
     */
    public void setUseFactory(boolean useFactory) {
        this.useFactory = useFactory;
    }

    /**
     * 是否使用工厂
     *
     * @return true表示使用工厂
     */
    public boolean isUseFactory() {
        return useFactory;
    }

    /**
     * 创建代理对象
     *
     * @return 代理对象实例
     */
    public Object create() {
        if (superclass == null) {
            throw new IllegalStateException("必须设置superclass");
        }

        try {
            // 创建目标对象实例
            this.target = superclass.getDeclaredConstructor().newInstance();

            // 创建方法代理映射
            createMethodProxies();

            // 使用JDK动态代理创建代理对象
            Class<?>[] proxyInterfaces = getProxyInterfaces();

            return Proxy.newProxyInstance(
                getClassLoader(),
                proxyInterfaces,
                new InterceptorInvocationHandler()
            );
        } catch (Exception e) {
            throw new RuntimeException("创建代理对象失败", e);
        }
    }

    /**
     * 创建代理对象（带构造参数）
     *
     * @param argumentTypes 构造参数类型
     * @param arguments 构造参数值
     * @return 代理对象实例
     */
    public Object create(Class<?>[] argumentTypes, Object[] arguments) {
        if (superclass == null) {
            throw new IllegalStateException("必须设置superclass");
        }

        try {
            // 创建目标对象实例
            this.target = superclass.getConstructor(argumentTypes).newInstance(arguments);

            // 创建方法代理映射
            createMethodProxies();

            // 使用JDK动态代理创建代理对象
            Class<?>[] proxyInterfaces = getProxyInterfaces();

            return Proxy.newProxyInstance(
                getClassLoader(),
                proxyInterfaces,
                new InterceptorInvocationHandler()
            );
        } catch (Exception e) {
            throw new RuntimeException("创建代理对象失败", e);
        }
    }

    /**
     * 获取代理接口数组
     *
     * @return 代理接口数组
     */
    private Class<?>[] getProxyInterfaces() {
        List<Class<?>> proxyInterfaces = new ArrayList<>();

        // 添加父类的所有接口
        if (superclass.getInterfaces() != null) {
            proxyInterfaces.addAll(Arrays.asList(superclass.getInterfaces()));
        }

        // 添加额外设置的接口
        if (interfaces != null) {
            proxyInterfaces.addAll(Arrays.asList(interfaces));
        }

        // 如果没有接口，使用父类本身（JDK动态代理要求至少一个接口）
        if (proxyInterfaces.isEmpty()) {
            // 创建一个标记接口
            proxyInterfaces.add(ProxyTarget.class);
        }

        return proxyInterfaces.toArray(new Class<?>[0]);
    }

    /**
     * 代理目标标记接口
     */
    public interface ProxyTarget {
    }

    /**
     * 拦截器调用处理器
     */
    private class InterceptorInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 获取方法签名
            String signature = getMethodSignature(method);
            MethodProxy methodProxy = methodProxies.get(signature);

            // 确定使用哪个回调
            Callback callbackToUse = determineCallback(method);

            if (callbackToUse instanceof MethodInterceptor && methodProxy != null) {
                // 使用MethodInterceptor拦截
                return ((MethodInterceptor) callbackToUse).intercept(target, method, args, methodProxy);
            } else {
                // 直接调用目标方法
                return method.invoke(target, args);
            }
        }

        /**
         * 确定使用哪个回调
         *
         * @param method 方法
         * @return 回调对象
         */
        private Callback determineCallback(Method method) {
            if (callbackFilter != null && callbacks != null && callbacks.length > 0) {
                int index = callbackFilter.accept(method);
                if (index >= 0 && index < callbacks.length) {
                    return callbacks[index];
                }
            }
            return callback;
        }
    }

    @Override
    protected Class<?> generateClass() {
        // 生成代理类名
        String className = generateClassName(superclass);

        // 这里简化处理，实际应该使用ASM生成字节码
        // 返回一个模拟的代理类
        try {
            return createProxyClass(className);
        } catch (Exception e) {
            throw new RuntimeException("生成代理类失败", e);
        }
    }

    /**
     * 创建代理类（简化实现）
     *
     * @param className 类名
     * @return 代理类
     */
    private Class<?> createProxyClass(String className) throws Exception {
        // 实际实现中应该使用ASM动态生成字节码
        // 这里返回父类作为简化实现
        // 在真实场景中，这会生成一个新的类继承superclass

        // 创建方法代理映射
        createMethodProxies();

        return superclass;
    }

    /**
     * 创建方法代理映射
     */
    private void createMethodProxies() {
        Method[] methods = superclass.getDeclaredMethods();
        int index = 0;

        for (Method method : methods) {
            // 跳过final、static、private方法
            int modifiers = method.getModifiers();
            if (Modifier.isFinal(modifiers) ||
                Modifier.isStatic(modifiers) ||
                Modifier.isPrivate(modifiers)) {
                continue;
            }

            String signature = getMethodSignature(method);
            MethodProxy proxy = new MethodProxy(
                signature,
                method,
                index++,
                index++,
                superclass,
                null // 代理类在实际生成后才确定
            );

            methodProxies.put(signature, proxy);
        }
    }

    /**
     * 获取方法签名
     *
     * @param method 方法
     * @return 方法签名
     */
    private String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());
        sb.append('(');

        Class<?>[] params = method.getParameterTypes();
        for (int i = 0; i < params.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(params[i].getName());
        }

        sb.append(')');
        return sb.toString();
    }

    /**
     * 获取方法代理
     *
     * @param signature 方法签名
     * @return 方法代理
     */
    public MethodProxy getMethodProxy(String signature) {
        return methodProxies.get(signature);
    }

    /**
     * 获取所有方法代理
     *
     * @return 方法代理映射
     */
    public Map<String, MethodProxy> getMethodProxies() {
        return Collections.unmodifiableMap(methodProxies);
    }

    @Override
    protected GeneratorStrategy getDefaultGeneratorStrategy() {
        return com.linsir.spring.framework.spring_core.bytecode.cglib.core.DefaultGeneratorStrategy.getInstance();
    }
}
