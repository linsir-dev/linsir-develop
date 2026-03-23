package com.linsir.spring.framework.spring_core.reflection.proxy;

/**
 * AOP 代理接口
 * 定义获取代理对象的标准方法
 */
public interface AopProxy {

    /**
     * 获取代理对象
     *
     * @return 代理对象
     */
    Object getProxy();

    /**
     * 使用指定类加载器获取代理对象
     *
     * @param classLoader 类加载器
     * @return 代理对象
     */
    Object getProxy(ClassLoader classLoader);
}
