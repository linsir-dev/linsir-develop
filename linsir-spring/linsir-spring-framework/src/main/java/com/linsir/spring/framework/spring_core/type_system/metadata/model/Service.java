package com.linsir.spring.framework.spring_core.type_system.metadata.model;

import java.lang.annotation.*;

/**
 * 服务层组件注解
 * 用于标记业务逻辑层组件
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {

    /**
     * 服务名称
     *
     * @return 服务名称
     */
    String value() default "";

    /**
     * 服务描述
     *
     * @return 服务描述
     */
    String description() default "";
}
