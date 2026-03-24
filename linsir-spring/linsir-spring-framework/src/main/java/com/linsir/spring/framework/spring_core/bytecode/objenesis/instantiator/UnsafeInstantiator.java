package com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator;

import com.linsir.spring.framework.spring_core.bytecode.objenesis.ObjenesisException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 基于Unsafe的对象实例化器
 *
 * <p>使用sun.misc.Unsafe.allocateInstance()方法创建对象实例。
 * 这是最高效、最通用的实例化方式，不需要调用构造函数。
 *
 * <p>优点：
 * <ul>
 *   <li>性能最好</li>
 *   <li>适用于所有类（包括没有默认构造函数的类）</li>
 *   <li>不依赖特定JVM实现</li>
 * </ul>
 *
 * <p>缺点：
 * <ul>
 *   <li>需要访问sun.misc.Unsafe（在某些受限环境可能不可用）</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @param <T> 要实例化的类型
 * @see ObjectInstantiator
 * @see ObjenesisException
 */
public class UnsafeInstantiator<T> implements ObjectInstantiator<T> {

    /**
     * Unsafe实例
     */
    private static Object unsafe;

    /**
     * allocateInstance方法
     */
    private static Method allocateInstanceMethod;

    /**
     * 是否可用
     */
    private static boolean available = false;

    /**
     * 静态初始化块
     *
     * <p>尝试获取Unsafe实例和allocateInstance方法。
     */
    static {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            unsafe = theUnsafeField.get(null);

            allocateInstanceMethod = unsafeClass.getMethod("allocateInstance", Class.class);
            available = true;
        } catch (Exception e) {
            // Unsafe不可用
            available = false;
        }
    }

    /**
     * 要实例化的类型
     */
    private final Class<T> type;

    /**
     * 构造函数
     *
     * @param type 要实例化的类型
     * @throws ObjenesisException 如果Unsafe不可用
     */
    public UnsafeInstantiator(Class<T> type) {
        if (!available) {
            throw new ObjenesisException("Unsafe不可用");
        }
        this.type = type;
    }

    /**
     * 检查Unsafe实例化是否可用
     *
     * @return true如果可用
     */
    public static boolean isAvailable() {
        return available;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newInstance() {
        try {
            return (T) allocateInstanceMethod.invoke(unsafe, type);
        } catch (Exception e) {
            throw new ObjenesisException("使用Unsafe实例化失败: " + type.getName(), e);
        }
    }
}
