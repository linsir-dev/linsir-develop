package com.linsir.spring.framework.spring_core.type_system.metadata.model;

import java.lang.annotation.*;

/**
 * 数据访问层组件注解
 * 用于标记数据访问层组件
 *
 * @author linsir
 * @version 1.0.0
 * @since 2024-01-01
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {

    /**
     * 仓库名称
     *
     * @return 仓库名称
     */
    String value() default "";
}
