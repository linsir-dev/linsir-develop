package com.linsir.spring.framework.spring_core.type_system.metadata.model;

import java.lang.annotation.*;

/**
 * 通用组件注解
 * 用于标记Spring管理的组件
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Component {

    /**
     * 组件名称
     *
     * @return 组件名称
     */
    String value() default "";
}
