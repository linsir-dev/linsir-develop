package com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator;

import com.linsir.spring.framework.spring_core.bytecode.objenesis.ObjenesisException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 基于ReflectionFactory的对象实例化器
 *
 * <p>使用Sun的ReflectionFactory创建对象实例。
 * ReflectionFactory可以创建不调用构造函数的实例。
 *
 * <p>优点：
 * <ul>
 *   <li>不需要调用构造函数</li>
 *   <li>适用于大多数类</li>
 * </ul>
 *
 * <p>缺点：
 * <ul>
 *   <li>依赖Sun的私有API</li>
 *   <li>在某些JVM实现上可能不可用</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @param <T> 要实例化的类型
 * @see ObjectInstantiator
 * @see ObjenesisException
 */
public class ReflectionFactoryInstantiator<T> implements ObjectInstantiator<T> {

    /**
     * ReflectionFactory类
     */
    private static Class<?> reflectionFactoryClass;

    /**
     * getReflectionFactory方法
     */
    private static Method getReflectionFactoryMethod;

    /**
     * newConstructorForSerialization方法
     */
    private static Method newConstructorForSerializationMethod;

    /**
     * 是否可用
     */
    private static boolean available = false;

    /**
     * 静态初始化块
     */
    static {
        try {
            reflectionFactoryClass = Class.forName("sun.reflect.ReflectionFactory");
            getReflectionFactoryMethod = reflectionFactoryClass.getMethod("getReflectionFactory");
            newConstructorForSerializationMethod = reflectionFactoryClass.getMethod(
                    "newConstructorForSerialization", Class.class, Constructor.class);
            available = true;
        } catch (Exception e) {
            // ReflectionFactory不可用
            available = false;
        }
    }

    /**
     * 要实例化的类型
     */
    private final Class<T> type;

    /**
     * 序列化构造函数
     */
    private final Constructor<T> constructor;

    /**
     * 构造函数
     *
     * @param type 要实例化的类型
     * @throws ObjenesisException 如果ReflectionFactory不可用
     */
    @SuppressWarnings("unchecked")
    public ReflectionFactoryInstantiator(Class<T> type) {
        if (!available) {
            throw new ObjenesisException("ReflectionFactory不可用");
        }
        this.type = type;
        try {
            Object factory = getReflectionFactoryMethod.invoke(null);
            this.constructor = (Constructor<T>) newConstructorForSerializationMethod.invoke(
                    factory, type, Object.class.getDeclaredConstructor());
            this.constructor.setAccessible(true);
        } catch (Exception e) {
            throw new ObjenesisException("创建序列化构造函数失败: " + type.getName(), e);
        }
    }

    /**
     * 检查ReflectionFactory实例化是否可用
     *
     * @return true如果可用
     */
    public static boolean isAvailable() {
        return available;
    }

    @Override
    public T newInstance() {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            throw new ObjenesisException("使用ReflectionFactory实例化失败: " + type.getName(), e);
        }
    }
}
