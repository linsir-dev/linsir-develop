package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 作用域注解
 *
 * 标识一个组件的作用域，定义了 Bean 的生命周期和可见性。
 * 常见的作用域包括单例（singleton）和原型（prototype）。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {

    /**
     * 作用域名称
     * 常用值：
     * - singleton：单例，整个应用只有一个实例（默认）
     * - prototype：原型，每次请求都创建新实例
     * - request：每个 HTTP 请求创建一个实例
     * - session：每个 HTTP 会话创建一个实例
     *
     * @return 作用域名称
     */
    String value() default "singleton";

    /**
     * 作用域代理模式
     * 用于解决作用域 Bean 的依赖注入问题
     *
     * @return 代理模式
     */
    ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;

    /**
     * 作用域代理模式枚举
     */
    enum ScopedProxyMode {
        /**
         * 默认行为，通常等同于 NO
         */
        DEFAULT,

        /**
         * 不使用代理
         */
        NO,

        /**
         * 使用 JDK 动态代理
         */
        INTERFACES,

        /**
         * 使用 CGLIB 代理
         */
        TARGET_CLASS
    }
}
