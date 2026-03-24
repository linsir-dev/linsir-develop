package com.linsir.spring.framework.spring_core.bytecode.objenesis;

import com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator.ObjectInstantiator;

/**
 * Objenesis核心接口
 *
 * <p>Objenesis是一个小型Java库，用于实例化特定类的对象。
 * 它的主要用途是绕过构造函数创建对象，这在某些场景下非常有用，如：
 * <ul>
 *   <li>序列化/反序列化</li>
 *   <li>代理对象创建</li>
 *   <li>克隆</li>
 *   <li>Mock对象创建</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * Objenesis objenesis = new ObjenesisStd();
 *
 * // 直接创建实例（不调用构造函数）
 * User user = objenesis.newInstance(User.class);
 *
 * // 获取实例化器（可重复使用）
 * ObjectInstantiator<User> instantiator = objenesis.getInstantiatorOf(User.class);
 * User user1 = instantiator.newInstance();
 * User user2 = instantiator.newInstance();
 * }</pre>
 *
 * <p>实现策略：
 * <ul>
 *   <li>SunReflectionFactoryInstantiator - 使用Sun的反射工厂</li>
 *   <li>UnsafeFactoryInstantiator - 使用sun.misc.Unsafe</li>
 *   <li>ConstructorInstantiator - 使用私有构造函数</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see ObjectInstantiator
 * @see ObjenesisStd
 */
public interface Objenesis {

    /**
     * 创建指定类的新实例
     *
     * <p>不调用类的构造函数，直接分配内存并返回实例。
     * 实例的字段值为默认值（null、0、false等）。
     *
     * @param clazz 要实例化的类
     * @param <T> 类型参数
     * @return 新实例
     * @throws ObjenesisException 如果实例化失败
     */
    <T> T newInstance(Class<T> clazz);

    /**
     * 获取指定类的实例化器
     *
     * <p>返回的实例化器可以重复使用，用于多次创建同一类型的实例。
     * 这比每次调用newInstance更高效。
     *
     * @param clazz 要实例化的类
     * @param <T> 类型参数
     * @return 实例化器
     * @throws ObjenesisException 如果创建实例化器失败
     */
    <T> ObjectInstantiator<T> getInstantiatorOf(Class<T> clazz);
}
