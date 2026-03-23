package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 限定符注解
 *
 * 用于在自动装配时指定具体的依赖实现。
 * 当有多个相同类型的候选依赖时，使用 @Qualifier 指定具体的 Bean。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Qualifier {

    /**
     * 限定符值
     * 用于区分相同类型的不同实现
     *
     * @return 限定符值
     */
    String value() default "";
}
