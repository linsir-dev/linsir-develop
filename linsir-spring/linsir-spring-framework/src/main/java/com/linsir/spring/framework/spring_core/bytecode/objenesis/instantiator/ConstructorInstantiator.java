package com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator;

import com.linsir.spring.framework.spring_core.bytecode.objenesis.ObjenesisException;

import java.lang.reflect.Constructor;

/**
 * 基于构造函数的对象实例化器
 *
 * <p>使用类的无参构造函数创建对象实例。
 * 这是最基础的实例化方式，需要类有可访问的无参构造函数。
 *
 * <p>优点：
 * <ul>
 *   <li>不依赖任何特殊API</li>
 *   <li>在所有JVM上都可用</li>
 *   <li>最安全的方式</li>
 * </ul>
 *
 * <p>缺点：
 * <ul>
 *   <li>需要类有无参构造函数</li>
 *   <li>会执行构造函数中的代码</li>
 *   <li>对于某些类可能无法使用</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @param <T> 要实例化的类型
 * @see ObjectInstantiator
 * @see ObjenesisException
 */
public class ConstructorInstantiator<T> implements ObjectInstantiator<T> {

    /**
     * 要实例化的类型
     */
    private final Class<T> type;

    /**
     * 构造函数
     */
    private final Constructor<T> constructor;

    /**
     * 构造函数
     *
     * @param type 要实例化的类型
     * @throws ObjenesisException 如果找不到合适的构造函数
     */
    public ConstructorInstantiator(Class<T> type) {
        this.type = type;
        try {
            this.constructor = type.getDeclaredConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ObjenesisException("找不到无参构造函数: " + type.getName(), e);
        }
    }

    @Override
    public T newInstance() {
        try {
            return constructor.newInstance();
        } catch (Exception e) {
            throw new ObjenesisException("使用构造函数实例化失败: " + type.getName(), e);
        }
    }
}
