package com.linsir.spring.framework.spring_core.reflection.event;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 事件监听器注解
 * 标记处理特定事件的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    /**
     * 监听的事件类型
     * 如果不指定，则根据方法参数类型推断
     */
    Class<? extends ApplicationEvent>[] value() default {};

    /**
     * 执行顺序，数字越小优先级越高
     */
    int order() default 0;
}
