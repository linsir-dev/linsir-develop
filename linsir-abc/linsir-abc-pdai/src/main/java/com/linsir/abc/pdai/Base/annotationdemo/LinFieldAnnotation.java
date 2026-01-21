package com.linsir.abc.pdai.Base.annotationdemo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段级别的自定义注解示例
 * 演示如何定义一个字段级别的注解
 */
@Target(ElementType.FIELD) // 注解可应用于字段
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可被访问
public @interface LinFieldAnnotation {
    // 注解属性
    String value() default "";
    boolean required() default false;
    String description() default "";
}
