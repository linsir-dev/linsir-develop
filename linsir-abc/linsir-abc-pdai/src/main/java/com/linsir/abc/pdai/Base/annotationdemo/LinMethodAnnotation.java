package com.linsir.abc.pdai.Base.annotationdemo;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法级别的自定义注解示例
 * 演示如何定义一个方法级别的注解
 */
@Target(ElementType.METHOD) // 注解可应用于方法
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可被访问
public @interface LinMethodAnnotation {
    // 注解属性
    String value() default "";
    String author() default "linsir";
    String date();
    String[] tags() default {};
}
