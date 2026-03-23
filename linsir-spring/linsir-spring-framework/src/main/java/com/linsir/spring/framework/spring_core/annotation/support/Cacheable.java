package com.linsir.spring.framework.spring_core.annotation.support;

import java.lang.annotation.*;

/**
 * 缓存注解
 *
 * 标识一个方法的返回值可以被缓存。
 * 当方法被再次调用时，如果缓存中存在结果，则直接返回缓存值。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cacheable {

    /**
     * 缓存名称
     *
     * @return 缓存名称
     */
    String[] value() default {};

    /**
     * 缓存键表达式
     * 支持 SpEL 表达式
     *
     * @return 缓存键
     */
    String key() default "";

    /**
     * 缓存条件表达式
     * 当条件为 true 时才缓存
     *
     * @return 条件表达式
     */
    String condition() default "";

    /**
     * 不缓存的条件表达式
     * 当条件为 true 时不缓存
     *
     * @return 不缓存条件
     */
    String unless() default "";
}
