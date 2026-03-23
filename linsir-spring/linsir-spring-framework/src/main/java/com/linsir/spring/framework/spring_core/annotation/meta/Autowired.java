package com.linsir.spring.framework.spring_core.annotation.meta;

import java.lang.annotation.*;

/**
 * 自动装配注解
 *
 * 标识一个字段、方法或构造器需要自动注入依赖。
 * 可以用于字段注入、setter 方法注入和构造器注入。
 *
 * @author linsir
 * @since 1.0.0
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {

    /**
     * 是否必需
     * 如果为 true 且找不到匹配的依赖，将抛出异常
     *
     * @return 是否必需
     */
    boolean required() default true;
}
