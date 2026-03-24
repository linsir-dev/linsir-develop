package com.linsir.spring.framework.spring_core.bytecode.objenesis.instantiator;

/**
 * 对象实例化器接口
 *
 * <p>定义了创建对象实例的接口。实现类负责使用特定策略
 * 绕过构造函数创建对象实例。
 *
 * <p>实现策略：
 * <ul>
 *   <li>UnsafeInstantiator - 使用sun.misc.Unsafe</li>
 *   <li>ReflectionFactoryInstantiator - 使用Sun的反射工厂</li>
 *   <li>ConstructorInstantiator - 使用私有构造函数</li>
 * </ul>
 *
 * <p>使用示例：
 * <pre>{@code
 * ObjectInstantiator<User> instantiator = new UnsafeInstantiator<>(User.class);
 * User user1 = instantiator.newInstance();
 * User user2 = instantiator.newInstance();
 * }</pre>
 *
 * @author linsir
 * @since 1.0
 * @param <T> 要实例化的类型
 * @see com.linsir.spring.framework.spring_core.bytecode.objenesis.Objenesis
 */
public interface ObjectInstantiator<T> {

    /**
     * 创建新实例
     *
     * <p>不调用类的构造函数，直接分配内存并返回实例。
     * 实例的字段值为默认值。
     *
     * @return 新实例
     */
    T newInstance();
}
