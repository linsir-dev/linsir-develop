package com.linsir.spring.framework.spring_core.reflection.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义 Autowired 注解
 * 用于依赖注入示例
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {

    /**
     * 是否必需
     */
    boolean required() default true;
}
