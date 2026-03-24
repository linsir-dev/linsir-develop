package com.linsir.spring.framework.spring_core.bytecode.objenesis;

import com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator.ObjectInstantiator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Objenesis标准实现
 *
 * <p>Objenesis接口的标准实现，提供了完整的对象实例化功能。
 * 使用策略模式选择合适的实例化策略，并缓存实例化器以提高性能。
 *
 * <p>特性：
 * <ul>
 *   <li>自动选择最佳实例化策略</li>
 *   <li>缓存实例化器</li>
 *   <li>线程安全</li>
 *   <li>支持多种JVM实现</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * // 创建Objenesis实例
 * Objenesis objenesis = new ObjenesisStd();
 *
 * // 创建对象实例
 * MyClass instance = objenesis.newInstance(MyClass.class);
 *
 * // 获取可重复使用的实例化器
 * ObjectInstantiator<MyClass> instantiator = objenesis.getInstantiatorOf(MyClass.class);
 * MyClass instance2 = instantiator.newInstance();
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @see Objenesis
 * @see ObjectInstantiator
 */
public class ObjenesisStd implements Objenesis {

    /**
     * 是否使用缓存
     */
    private final boolean useCache;

    /**
     * 实例化器缓存
     */
    private final ConcurrentMap<Class<?>, ObjectInstantiator<?>> cache = new ConcurrentHashMap<>();

    /**
     * 实例化策略
     */
    private final InstantiatorStrategy strategy;

    /**
     * 默认构造函数
     *
     * <p>使用默认策略并启用缓存。
     */
    public ObjenesisStd() {
        this(true);
    }

    /**
     * 构造函数
     *
     * @param useCache 是否使用缓存
     */
    public ObjenesisStd(boolean useCache) {
        this(useCache, new StdInstantiatorStrategy());
    }

    /**
     * 构造函数
     *
     * @param useCache 是否使用缓存
     * @param strategy 实例化策略
     */
    public ObjenesisStd(boolean useCache, InstantiatorStrategy strategy) {
        this.useCache = useCache;
        this.strategy = strategy;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> clazz) {
        return (T) getInstantiatorOf(clazz).newInstance();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("类不能为null");
        }
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("不能实例化基本类型: " + clazz.getName());
        }
        if (clazz.isInterface()) {
            throw new IllegalArgumentException("不能实例化接口: " + clazz.getName());
        }
        if (clazz.isArray()) {
            throw new IllegalArgumentException("不能实例化数组: " + clazz.getName());
        }

        if (useCache) {
            ObjectInstantiator<?> instantiator = cache.get(clazz);
            if (instantiator == null) {
                instantiator = strategy.newInstantiatorOf(clazz);
                ObjectInstantiator<?> existing = cache.putIfAbsent(clazz, instantiator);
                if (existing != null) {
                    instantiator = existing;
                }
            }
            return (ObjectInstantiator<T>) instantiator;
        }

        return strategy.newInstantiatorOf(clazz);
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * 获取缓存大小
     *
     * @return 缓存中的实例化器数量
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * 实例化策略接口
     */
    public interface InstantiatorStrategy {
        /**
         * 创建实例化器
         *
         * @param clazz 要实例化的类
         * @param <T> 类型参数
         * @return 实例化器
         */
        <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> clazz);
    }

    /**
     * 标准实例化策略
     *
     * <p>按优先级尝试不同的实例化策略：
     * <ol>
     *   <li>SunReflectionFactoryInstantiator（如果可用）</li>
     *   <li>UnsafeFactoryInstantiator（如果可用）</li>
     *   <li>ConstructorInstantiator（作为后备）</li>
     * </ol>
     */
    private static class StdInstantiatorStrategy implements InstantiatorStrategy {

        @Override
        public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> clazz) {
            // 按优先级尝试不同的策略

            // 1. 尝试使用Unsafe（最通用，性能最好）
            try {
                return new com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator.UnsafeInstantiator<>(clazz);
            } catch (Exception e) {
                // 继续尝试其他策略
            }

            // 2. 尝试使用反射工厂
            try {
                return new com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator.ReflectionFactoryInstantiator<>(clazz);
            } catch (Exception e) {
                // 继续尝试其他策略
            }

            // 3. 使用构造函数方式（最后的选择）
            return new com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator.ConstructorInstantiator<>(clazz);
        }
    }
}
