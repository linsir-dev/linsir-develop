package com.linsir.spring.framework.spring_core.bytecode.cglib.proxy;

/**
 * 代理工厂接口
 *
 * <p>所有CGLIB生成的代理类都会实现此接口，用于设置和获取回调对象。
 * 通过此接口可以在运行时动态更换代理的回调逻辑。
 *
 * <p>使用场景：
 * <ul>
 *   <li>运行时更换拦截器</li>
 *   <li>动态调整代理行为</li>
 *   <li>回调对象的延迟设置</li>
 * </ul>
 *
 * @author linsir
 * @since 1.0
 * @see Enhancer#setUseFactory(boolean)
 * @see Callback
 */
public interface Factory {

    /**
     * 设置回调对象
     *
     * <p>设置单个回调对象，相当于设置回调数组的第一个元素。
     *
     * @param callback 回调对象
     */
    void setCallback(Callback callback);

    /**
     * 获取回调对象
     *
     * @return 回调对象
     */
    Callback getCallback();

    /**
     * 设置回调数组
     *
     * <p>设置多个回调对象，配合CallbackFilter使用。
     *
     * @param callbacks 回调数组
     */
    void setCallbacks(Callback[] callbacks);

    /**
     * 获取回调数组
     *
     * @return 回调数组
     */
    Callback[] getCallbacks();
}
