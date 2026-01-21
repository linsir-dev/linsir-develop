package com.linsir.abc.pdai.base.annotationdemo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解示例
 * 演示如何定义一个类级别的注解
 */
@Target(ElementType.TYPE) // 注解可应用于类
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可被访问
public @interface LinAnnotation {
    // 注解属性
    String value() default "";
    String name() default "lin";
    int version() default 1;
}
