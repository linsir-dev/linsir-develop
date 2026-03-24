package com.linsir.spring.framework.spring_core.bytecode.cglib.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 快速类
 *
 * <p>CGLIB提供的反射优化方案，通过生成索引化的方法调用代码，
 * 避免Java反射带来的性能开销。FastClass的性能接近直接方法调用。
 *
 * <p>工作原理：
 * <ol>
 *   <li>为目标类生成一个FastClass子类</li>
 *   <li>为每个方法分配一个唯一索引</li>
 *   <li>通过switch语句根据索引直接调用方法</li>
 * </ol>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建FastClass
 * FastClass fastClass = FastClass.create(UserService.class);
 *
 * // 获取方法索引
 * int index = fastClass.getIndex("getUserName", new Class[]{int.class});
 *
 * // 通过索引调用方法（性能接近直接调用）
 * Object result = fastClass.invoke(index, userService, new Object[]{1});
 * }</pre>
 *
 * <p>性能对比：
 * <ul>
 *   <li>反射调用：~100ns</li>
 *   <li>FastClass调用：~10ns</li>
 *   <li>直接调用：~5ns</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see FastMethod
 * @see MethodProxy
 */
public abstract class FastClass {

    /**
     * 目标类
     */
    private final Class<?> targetClass;

    /**
     * 方法签名到索引的映射
     */
    private final Map<String, Integer> methodIndices = new HashMap<>();

    /**
     * 索引到方法的映射
     */
    private final Map<Integer, Method> indexToMethod = new HashMap<>();

    /**
     * 构造函数
     *
     * @param targetClass 目标类
     */
    protected FastClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        initMethodIndices();
    }

    /**
     * 创建FastClass
     *
     * <p>为指定的类创建FastClass实例。实际实现中应该生成一个FastClass的子类，
     * 这里简化处理，返回一个基于反射的实现。
     *
     * @param type 目标类
     * @return FastClass实例
     */
    public static FastClass create(Class<?> type) {
        return new ReflectFastClass(type);
    }

    /**
     * 初始化方法索引
     *
     * <p>为类的所有公共方法建立索引映射。
     */
    private void initMethodIndices() {
        Method[] methods = targetClass.getDeclaredMethods();
        int index = 0;

        for (Method method : methods) {
            String signature = getSignature(method);
            methodIndices.put(signature, index);
            indexToMethod.put(index, method);
            index++;
        }
    }

    /**
     * 获取方法签名
     *
     * @param method 方法
     * @return 方法签名
     */
    private String getSignature(Method method) {
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
     * 获取方法索引
     *
     * <p>根据方法名和参数类型获取方法在FastClass中的索引。
     *
     * @param name 方法名
     * @param parameterTypes 参数类型数组
     * @return 方法索引，如果找不到返回-1
     */
    public int getIndex(String name, Class<?>[] parameterTypes) {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append('(');

        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(parameterTypes[i].getName());
        }

        sb.append(')');

        Integer index = methodIndices.get(sb.toString());
        return index != null ? index : -1;
    }

    /**
     * 通过索引调用方法
     *
     * <p>这是FastClass的核心方法，通过索引直接调用目标方法，
     * 避免了反射查找方法的开销。
     *
     * @param index 方法索引
     * @param target 目标对象
     * @param args 方法参数
     * @return 方法返回值
     * @throws InvocationTargetException 如果目标方法抛出异常
     */
    public abstract Object invoke(int index, Object target, Object[] args)
            throws InvocationTargetException;

    /**
     * 获取目标类
     *
     * @return 目标类
     */
    public Class<?> getTargetClass() {
        return targetClass;
    }

    /**
     * 获取方法总数
     *
     * @return 方法数量
     */
    public int getMethodCount() {
        return methodIndices.size();
    }

    /**
     * 获取索引对应的方法
     *
     * @param index 方法索引
     * @return 方法对象
     */
    public Method getMethod(int index) {
        return indexToMethod.get(index);
    }

    /**
     * 基于反射的FastClass实现（简化版）
     *
     * <p>实际CGLIB会生成字节码来优化调用，这里使用反射作为简化实现。
     */
    private static class ReflectFastClass extends FastClass {

        public ReflectFastClass(Class<?> targetClass) {
            super(targetClass);
        }

        @Override
        public Object invoke(int index, Object target, Object[] args)
                throws InvocationTargetException {
            Method method = getMethod(index);
            if (method == null) {
                throw new IllegalArgumentException("无效的方法索引: " + index);
            }

            try {
                method.setAccessible(true);
                return method.invoke(target, args);
            } catch (IllegalAccessException e) {
                throw new InvocationTargetException(e, "方法访问失败");
            } catch (InvocationTargetException e) {
                throw e;
            }
        }
    }
}
