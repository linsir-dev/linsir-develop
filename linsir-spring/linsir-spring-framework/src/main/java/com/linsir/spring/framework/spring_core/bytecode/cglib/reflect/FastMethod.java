package com.linsir.spring.framework.spring_core.bytecode.cglib.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 快速方法
 *
 * <p>基于FastClass的方法调用包装器，提供更友好的方法调用API。
 * 封装了方法索引查找和调用逻辑，使代码更清晰易用。
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建FastClass
 * FastClass fastClass = FastClass.create(UserService.class);
 *
 * // 获取FastMethod
 * FastMethod fastMethod = fastClass.getMethod("getUserName", new Class[]{int.class});
 *
 * // 调用方法
 * Object result = fastMethod.invoke(userService, new Object[]{1});
 * }</pre>
 *
 * <p>与直接使用FastClass相比，FastMethod：
 * <ul>
 *   <li>自动处理方法索引查找</li>
 *   <li>提供更直观的方法签名信息</li>
 *   <li>支持方法元数据访问</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see FastClass
 * @see MethodProxy
 */
public class FastMethod {

    /**
     * 所属的FastClass
     */
    private final FastClass fastClass;

    /**
     * 目标方法
     */
    private final Method method;

    /**
     * 方法索引
     */
    private final int methodIndex;

    /**
     * 方法签名
     */
    private final String signature;

    /**
     * 构造函数
     *
     * @param fastClass 所属的FastClass
     * @param method 目标方法
     * @param methodIndex 方法索引
     */
    public FastMethod(FastClass fastClass, Method method, int methodIndex) {
        this.fastClass = fastClass;
        this.method = method;
        this.methodIndex = methodIndex;
        this.signature = buildSignature(method);
    }

    /**
     * 构建方法签名
     *
     * @param method 方法
     * @return 方法签名
     */
    private String buildSignature(Method method) {
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
     * 调用方法
     *
     * @param target 目标对象
     * @param args 方法参数
     * @return 方法返回值
     * @throws InvocationTargetException 如果目标方法抛出异常
     */
    public Object invoke(Object target, Object[] args) throws InvocationTargetException {
        return fastClass.invoke(methodIndex, target, args);
    }

    /**
     * 获取方法名
     *
     * @return 方法名
     */
    public String getName() {
        return method.getName();
    }

    /**
     * 获取返回类型
     *
     * @return 返回类型
     */
    public Class<?> getReturnType() {
        return method.getReturnType();
    }

    /**
     * 获取参数类型数组
     *
     * @return 参数类型数组
     */
    public Class<?>[] getParameterTypes() {
        return method.getParameterTypes();
    }

    /**
     * 获取方法索引
     *
     * @return 方法索引
     */
    public int getMethodIndex() {
        return methodIndex;
    }

    /**
     * 获取方法签名
     *
     * @return 方法签名
     */
    public String getSignature() {
        return signature;
    }

    /**
     * 获取原始Method对象
     *
     * @return Method对象
     */
    public Method getJavaMethod() {
        return method;
    }

    /**
     * 获取声明类
     *
     * @return 声明类
     */
    public Class<?> getDeclaringClass() {
        return method.getDeclaringClass();
    }

    @Override
    public String toString() {
        return "FastMethod{" +
                "signature='" + signature + '\'' +
                ", returnType=" + method.getReturnType().getName() +
                '}';
    }
}
