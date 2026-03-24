package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

/**
 * CGLIB回调接口标记
 * 
 * <p>所有CGLIB回调接口的父接口，用于标识回调类型。
 * 具体的回调接口包括：
 * <ul>
 *   <li>{@link MethodInterceptor} - 方法拦截器</li>
 *   <li>{@link NoOp} - 无操作回调</li>
 *   <li>{@link LazyLoader} - 延迟加载器</li>
 *   <li>{@link Dispatcher} - 分发器</li>
 *   <li>{@link FixedValue} - 固定值提供者</li>
 * </ul>
 * 
 * <p>回调机制是CGLIB实现代理的核心，通过回调可以在方法调用时插入自定义逻辑。
 * 
 * @author linsir
 * @since 1.0
 * @see MethodInterceptor
 * @see Enhancer
 */
public interface Callback {
    // 标记接口，无方法定义
}
